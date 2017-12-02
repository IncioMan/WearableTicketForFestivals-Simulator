package com.group14.findeyourfriend.bracelet;

import com.group14.findeyourfriend.chart.Chart;
import com.group14.findeyourfriend.debug.DebugLog;
import com.group14.findeyourfriend.message.Message;
import com.group14.findeyourfriend.message.SearchRequest;
import com.group14.findeyourfriend.message.SearchResponse;
import com.group14.findeyourfriend.statemachine.Command;
import com.group14.findeyourfriend.statemachine.ProcessState;
import com.group14.findeyourfriend.statemachine.SRStateMachine;
import javafx.scene.chart.XYChart;

import java.util.ArrayList;
import java.util.Stack;

public class SRBracelet extends Bracelet{

    private Stack<Message> searchRequestsToRelay = new Stack<>();
    private Stack<Message> searchResponsesToRelay = new Stack<>();
    private Stack<Message> searchResponsesToBroadcast = new Stack<>();

    private boolean timerLPRun;
    private boolean timerDLPRun;
    private boolean timerDRPRun;
    private boolean timerIPRun;

    private boolean timerRLPRun;
    private boolean timerRRPRun;
    private boolean timerRQPRun;


    public SRBracelet(Battery b, Radio r, CPU c, Person owner) {
        super(b, r, c, owner);
        stateMachine = new SRStateMachine();
    }

    @Override
    public void RunBracelet() {
        switch (stateMachine.getCurrentState()) {
            case SLEEP_STATE:
                break;
            case COMMUNICATION_STATE:
                CommState();
                break;
            case LED_STATE:
                LedState();
                break;
            case S_LOOKUP_STATE:
                SLookupState();
                break;
            case UPDATE_STATE:
                UpdateState();
                break;
            case REQUEST_STATE:
                RequestState();
            case INTERPRET_STATE:
                InterpretState();
            case LISTEN_STATE:
                ListenState();
            case R_LOOKUP_STATE:
                RLookupState();
            case RESPONSE_STATE:
                ResponseState();
            default:
                DebugLog.log(person.getId() + ": In DefaultState");
                break;
        }
    }

    private void ResponseState() {
        timerRRPRun = true;
        timerDRPRun = true;

        BroadcastSearchResponses();
        RelayMessages(searchRequestsToRelay);
        RelayMessages(searchResponsesToRelay);
    }

    private void RLookupState() {
        if (!searchResponsesToBroadcast.empty()){
            synchronized (_stateLock) {
                if (stateMachine.getCurrentState() == ProcessState.R_LOOKUP_STATE) {
                    stateMachine.MoveNext(Command.SendResponse); // to Response phase
                    RunBracelet();
                }
            }
        }
    }

    private void ListenState() {
        RelayMessages(searchRequestsToRelay);
        RelayMessages(searchResponsesToRelay);
    }

    private void InterpretState() {
        if (dataBase.containsKey(_lookForPerson.getId())){
            synchronized (_stateLock) {
                if (stateMachine.getCurrentState() == ProcessState.INTERPRET_STATE) {
                    stateMachine.MoveNext(Command.FriendFound); // to LED phase
                    RunBracelet();
                }
            }
        }
    }

    private void RequestState() {
        _timerRCPRun = true;
        timerIPRun = true;

        BroadcastSearchRequest();
        RelayMessages(searchRequestsToRelay);
    }



    @Override
    public void transition(int clock){
        if (clock % cpu.timerCpDelay == 0) {
            // Goto Communication phase
            OnTimerCp();
        }
        if (clock % cpu.timerFDelay == 0 && _timerFRun) {
            // Goto
            OnTimerF();
        }
        if (clock % cpu.timerRCPDelay == 0 && _timerRCPRun) {
            // Rebroadcast
            OnTimerRCP();
        }
        if (clock % cpu.timerUpDelay == 0 && _timerUpRun) {
            OnTimerUp();
        }
        if (clock % cpu.timerLedDelay == 0 && _timerLedRun) {
            OnTimerLed();
        }

        if (clock % cpu.timerIPDelay == 0 && timerIPRun){
            OnTimerIP();
        }

        if (clock % cpu.timerLPDelay == 0){
            OnTimerLP();
        }

        if (clock % cpu.timerDLPDelay == 0 && timerDLPRun){
            OnTimerDLP();
        }

        if (clock % cpu.timerDRPDelay == 0 && timerDRPRun){
            OnTimerDRP();
        }

        if (clock % cpu.timerRLPDelay == 0 && timerRLPRun) {
            // Rebroadcast
            OnTimerRLP();
        }

        if (clock % cpu.timerRRPDelay == 0 && timerRRPRun) {
            // Rebroadcast
            OnTimerRRP();
        }

        if (clock % cpu.timerRQPDelay == 0 && timerRQPRun) {
            // Rebroadcast
            OnTimerRQP();
        }

        if (clock % 60000 == 0) {
            ArrayList<XYChart.Data> dataPoints = Chart.DataPointsMap.getOrDefault(person.getId(),  new ArrayList<>());
            dataPoints.add(new XYChart.Data(clock / 60000, battery.getEnergyLeft())); // every "minute" new datapoint
            Chart.DataPointsMap.putIfAbsent(person.getId(), dataPoints);
        }

    }

    private void OnTimerRQP() {
        BroadcastSearchRequest();
        RelayMessages(searchRequestsToRelay);
        RelayMessages(searchResponsesToRelay);
    }

    private void BroadcastSearchRequest() {
        SearchRequest msg = new SearchRequest(this, _lookForPerson.getId());
        _broker.DoBroadcast(this, getPosition(), radio.getRange_M(), msg);
    }

    private void OnTimerRRP() {
        BroadcastSearchResponses();
        RelayMessages(searchRequestsToRelay);
        RelayMessages(searchResponsesToRelay);
    }

    private void BroadcastSearchResponses() {
        Message msg;
        while(!searchResponsesToBroadcast.empty()){
            msg = searchResponsesToBroadcast.pop();
            _broker.DoBroadcast(this, getPosition(), radio.getRange_M(), msg);
        }
    }

    private void OnTimerRLP() {
        RelayMessages(searchRequestsToRelay);
        RelayMessages(searchResponsesToRelay);
    }

    private void OnTimerDRP() {
        timerDRPRun = false;
        timerRRPRun = false;

        synchronized (_stateLock) {
            if (stateMachine.getCurrentState() == ProcessState.RESPONSE_STATE) {
                DebugLog.logTimer(person.getId() + ": OnTimerDRP");
                stateMachine.MoveNext(Command.TimerDRP); // to update phase
                RunBracelet();
            }
        }
    }

    private void OnTimerDLP() {
        timerDLPRun = false;
        timerRRPRun = false;

        synchronized (_stateLock) {
            if (stateMachine.getCurrentState() == ProcessState.LISTEN_STATE) {
                DebugLog.logTimer(person.getId() + ": OnTimerDLP");
                stateMachine.MoveNext(Command.TimerDLP); // to RLookup phase
                RunBracelet();
            }
        }
    }

    private void OnTimerLP() {
        synchronized (_stateLock) {
            if (stateMachine.getCurrentState() == ProcessState.SLEEP_STATE) {
                DebugLog.logTimer(person.getId() + ": OnTimerLP");
                stateMachine.MoveNext(Command.TimerLP); // to listen phase
                RunBracelet();
            }
        }
    }

    private void OnTimerIP() {
        synchronized (_stateLock) {
            if (stateMachine.getCurrentState() == ProcessState.REQUEST_STATE) {
                DebugLog.logTimer(person.getId() + ": OnTimerIP");
                stateMachine.MoveNext(Command.TimerIP); // go to interpretation phase
                RunBracelet();
            }
        }
    }

    public void storeSearchRequest(SearchRequest msg){
        searchRequestsToRelay.push(msg);
    }
    public void storeSearchResponseToRelay(SearchResponse msg){
        searchResponsesToRelay.push(msg);
    }
    public void storeSearchResponseToBroadcast(SearchResponse msg){
        searchResponsesToBroadcast.push(msg);
    }

}
