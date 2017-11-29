package com.group14.findeyourfriend.bracelet;

import com.group14.findeyourfriend.chart.Chart;
import com.group14.findeyourfriend.debug.DebugLog;
import com.group14.findeyourfriend.statemachine.Command;
import com.group14.findeyourfriend.statemachine.ProcessState;
import com.group14.findeyourfriend.statemachine.SRStateMachine;
import javafx.scene.chart.XYChart;

import java.util.ArrayList;

public class SRBracelet extends Bracelet{

    private boolean timerDLPRun;
    private boolean timerDRPPRun;

    public SRBracelet(Battery b, Radio r, CPU c, Person owner) {
        super(b, r, c, owner);
        _stateMachine = new SRStateMachine();
    }

    @Override
    public void RunBracelet() {
        switch (_stateMachine.getCurrentState()) {
            case SleepState:
                break;
            case CommState:
                CommState();
                break;
            case LedState:
                LedState();
                break;
            case SLookupState:
                SLookupState();
                break;
            case UpdateState:
                UpdateState();
                break;
            case RequestState:
                RequestState();
            case InterpretState:
                InterpretState();
            case ListenState:
                ListenState();
            case RLookupState:
                RLookupState();
            case ResponseState:
                ResponseState();
            default:
                DebugLog.log(owner.getId() + ": In DefaultState");
                break;
        }
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
        if (clock % cpu.timerRDelay == 0 && _timerRRun) {
            // Rebroadcast
            OnTimerR();
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

        if (clock % cpu.timerDLPDelay == 0 && timerDLPPRun){
            OnTimerDLP();
        }

        if (clock % cpu.timerDRPDelay == 0 && timerDRPPRun){
            OnTimerDRP();
        }

        if (clock % 60000 == 0) {
            ArrayList<XYChart.Data> dataPoints = Chart.DataPointsMap.getOrDefault(owner.getId(),  new ArrayList<>());
            dataPoints.add(new XYChart.Data(clock / 60000, battery.getEnergyLeft())); // every "minute" new datapoint
            Chart.DataPointsMap.putIfAbsent(owner.getId(), dataPoints);
        }

    }

    private void OnTimerDRP() {
        timerDRPPRun = false;
        // TODO implement relay rebroadcast timer
        synchronized (_stateLock) {
            if (_stateMachine.getCurrentState() == ProcessState.ResponseState) {
                DebugLog.logTimer(owner.getId() + ": OnTimerDRP");
                _stateMachine.MoveNext(Command.TimerDRP); // to update phase
                RunBracelet();
            }
        }
    }

    private void OnTimerDLP() {
        timerDLPRun = false;
        // TODO implement relay rebroadcast timer
        synchronized (_stateLock) {
            if (_stateMachine.getCurrentState() == ProcessState.ListenState) {
                DebugLog.logTimer(owner.getId() + ": OnTimerDLP");
                _stateMachine.MoveNext(Command.TimerDLP); // to RLookup phase
                RunBracelet();
            }
        }
    }

    private void OnTimerLP() {
        synchronized (_stateLock) {
            if (_stateMachine.getCurrentState() == ProcessState.SleepState) {
                DebugLog.logTimer(owner.getId() + ": OnTimerLP");
                _stateMachine.MoveNext(Command.TimerLP); // to listen phase
                RunBracelet();
            }
        }
    }

    private void OnTimerIP() {
        synchronized (_stateLock) {
            if (_stateMachine.getCurrentState() == ProcessState.RequestState) {
                DebugLog.logTimer(owner.getId() + ": OnTimerIP");
                _stateMachine.MoveNext(Command.TimerIP); // go to interpretation phase
                RunBracelet();
            }
        }
    }

}
