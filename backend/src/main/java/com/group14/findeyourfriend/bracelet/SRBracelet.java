package com.group14.findeyourfriend.bracelet;

import java.util.ArrayList;

import com.group14.findeyourfriend.Clock;
import com.group14.findeyourfriend.chart.Chart;
import com.group14.findeyourfriend.debug.DebugLog;
import com.group14.findeyourfriend.message.Message;
import com.group14.findeyourfriend.message.SearchRequest;
import com.group14.findeyourfriend.message.SearchResponse;
import com.group14.findeyourfriend.statemachine.Command;
import com.group14.findeyourfriend.statemachine.ProcessState;
import com.group14.findeyourfriend.statemachine.SRStateMachine;

import javafx.scene.chart.XYChart;

public class SRBracelet extends Bracelet {

	private SearchRequest requestToBroadcast;
	private ArrayList<Message> requestsToRelay = new ArrayList<>();
	private ArrayList<Message> responsesToRelay = new ArrayList<>();
	private ArrayList<Message> responsesToBroadcast = new ArrayList<>();

	private boolean timerDLPRun;
	private boolean timerDRPRun;
	private boolean timerIPRun;

	private boolean timerRLPRun;
	private boolean timerRRPRun;
	private boolean timerRQPRun;

	public SRBracelet(Battery b, Radio r, CPU c, Person owner, long clockOffset) {
		super(b, r, c, owner, clockOffset);
		stateMachine = new SRStateMachine();
	}

	// TODO override communication phase to also relay search requests and responses

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
			break;
		case INTERPRET_STATE:
			InterpretState();
			break;
		case LISTEN_STATE:
			ListenState();
			break;
		case R_LOOKUP_STATE:
			RLookupState();
			break;
		case RESPONSE_STATE:
			ResponseState();
			break;
		default:
			DebugLog.log(person.getId() + ": In DefaultState");
			break;
		}
	}

	@Override
	protected void SLookupState() {
		// Some logic for actually looking up a friend
		synchronized (_dbLock) {
			DebugLog.log(person.getId() + ": Looking up location in my DB");
			if (_lookForPerson != null) {
				battery.DecrementEnergy(cpu.cpuCurrentRun_mA, 1000);
				if (dataBase.containsKey(_lookForPerson.getId())) {
					// TODO implement if(!recentEnough) then it is not found
					if (Clock.isRecentEnough(dataBase.get(_lookForPerson.getId()))) {
						setGuiding(true);
						setFound(false);
						stateMachine.MoveNext(Command.FriendFound);
						DebugLog.log(person.getId() + ": Found recent location in my DB");
					} else {
						DebugLog.log(person.getId() + ": Found obsolete location in my DB");
						// broker.notifyEvent(getPerson(), BraceletEvent.FRIEND_NOT_FOUND_IN_DB);
						stateMachine.MoveNext(Command.FriendNotFound);
					}
				} else {
					DebugLog.log(person.getId() + ": Did not find person in my DB");
					// broker.notifyEvent(getPerson(), BraceletEvent.FRIEND_NOT_FOUND_IN_DB);
					stateMachine.MoveNext(Command.FriendNotFound);
				}
				RunBracelet();
			}
		}
	}

	private void ResponseState() {
		timerRRPRun = true;
		timerDRPRun = true;

		synchronized (_stateLock) {
			if (stateMachine.getCurrentState() == ProcessState.RESPONSE_STATE) {
				BroadcastSearchResponses();
				BroadcastMessages(requestsToRelay);
				BroadcastMessages(responsesToRelay);
			}
		}

	}

	private void RLookupState() {
		synchronized (_stateLock) {
			if (stateMachine.getCurrentState() == ProcessState.R_LOOKUP_STATE) {
				if (!responsesToBroadcast.isEmpty()) {
					stateMachine.MoveNext(Command.SendResponse); // to Response phase
					RunBracelet();
				} else {
					DebugLog.log(person.getId() + ": No search responses to send");
					stateMachine.MoveNext(Command.NoResponse); // to Update phase
					RunBracelet();
				}
			}
		}
	}

	private void ListenState() {
		timerRLPRun = true;
		timerDLPRun = true;

		synchronized (_stateLock) {
			if (stateMachine.getCurrentState() == ProcessState.LISTEN_STATE) {
				DebugLog.log(person.getId() + ": Listening for search requests");
				BroadcastMessages(requestsToRelay);
				BroadcastMessages(responsesToRelay);
			}
		}

	}

	private void InterpretState() {
		synchronized (_stateLock) {
			if (stateMachine.getCurrentState() == ProcessState.INTERPRET_STATE) {
				if (dataBase.containsKey(_lookForPerson.getId())) {
					DebugLog.log(person.getId() + ": Received search response for person " + _lookForPerson.getId());
					broker.notifyEvent(this.getPerson(), BraceletEvent.FRIEND_FOUND_IN_DB);
					stateMachine.MoveNext(Command.FriendFound); // to LED phase
					RunBracelet();
				} else {
					DebugLog.log(person.getId() + ": No search responses received");
					broker.notifyEvent(this.getPerson(), BraceletEvent.FRIEND_NOT_FOUND_IN_DB);
					stateMachine.MoveNext(Command.FriendNotFound); // to Sleep
					RunBracelet();
				}
			}
		}
	}

	private void RequestState() {
		timerRQPRun = true;
		timerIPRun = true;

		synchronized (_stateLock) {
			if (stateMachine.getCurrentState() == ProcessState.REQUEST_STATE) {
				DebugLog.log(person.getId() + ": Broadcasting search request for " + _lookForPerson.getId());
				BroadcastSearchRequest();
				BroadcastMessages(requestsToRelay);
			}
		}

	}

	@Override
	public void transition(long clock) {
		clock = clock - clockOffset;
		if (clock % cpu.timerCpDelay == 0) {
			// Goto Communication phase
			OnTimerCp();
		}
		if (clock % (cpu.timerFDelay + clockDrift) == 0 && _timerFRun) {
			// Goto
			OnTimerF();
		}
		if (clock % (cpu.timerRCPDelay + clockDrift) == 0 && _timerRCPRun) {
			// Rebroadcast
			OnTimerRCP();
		}
		if (clock % (cpu.timerUpDelay + clockDrift) == 0 && _timerUpRun) {
			OnTimerUp();
		}
		if (clock % (cpu.timerLedDelay + clockDrift) == 0 && _timerLedRun) {
			OnTimerLed();
		}
		if (clock % (cpu.timerMoveToEventDelay + clockDrift) == 0 && _timerMoveToEventRun) {
			// Goto
			OnTimerMoveToEvent();
		}
		if (clock % (cpu.timerIPDelay + clockDrift) == 0 && timerIPRun) {
			OnTimerIP();
		}

		if (clock % (cpu.timerLPDelay + clockDrift) == 0) {
			OnTimerLP();
		}

		if (clock % (cpu.timerDLPDelay + clockDrift) == 0 && timerDLPRun) {
			OnTimerDLP();
		}

		if (clock % (cpu.timerDRPDelay + clockDrift) == 0 && timerDRPRun) {
			OnTimerDRP();
		}

		if (clock % (cpu.timerRLPDelay + clockDrift) == 0 && timerRLPRun) {
			// Rebroadcast
			OnTimerRLP();
		}

		if (clock % (cpu.timerRRPDelay + clockDrift) == 0 && timerRRPRun) {
			// Rebroadcast
			OnTimerRRP();
		}

		if (clock % (cpu.timerRQPDelay + clockDrift) == 0 && timerRQPRun) {
			// Rebroadcast
			OnTimerRQP();
		}

		if (clock % 60000 == 0) {
			ArrayList<XYChart.Data> dataPoints = Chart.DataPointsMap.getOrDefault(person.getId(), new ArrayList<>());
			dataPoints.add(new XYChart.Data(clock / 60000, battery.getEnergyLeft())); // every "minute" new datapoint
			Chart.DataPointsMap.putIfAbsent(person.getId(), dataPoints);
		}

	}

	private void OnTimerRQP() {
		synchronized (_stateLock) {
			if (stateMachine.getCurrentState() == ProcessState.REQUEST_STATE) {
				DebugLog.logTimer(person.getId() + ": OnTimerRQP");
				BroadcastSearchRequest();
				BroadcastMessages(requestsToRelay);
				BroadcastMessages(responsesToRelay);
			}
		}
	}

	private void BroadcastSearchRequest() {
		synchronized (_stateLock) {
			if (stateMachine.getCurrentState() == ProcessState.REQUEST_STATE) {
				if (requestToBroadcast == null)
					requestToBroadcast = new SearchRequest(this, _lookForPerson.getId());
				broker.DoBroadcast(this, getPosition(), radio.getRange_M(), requestToBroadcast);
			}
		}
	}

	private void OnTimerRRP() {
		synchronized (_stateLock) {
			if (stateMachine.getCurrentState() == ProcessState.RESPONSE_STATE) {
				DebugLog.logTimer(person.getId() + ": OnTimerRRP");
				BroadcastSearchResponses();
				BroadcastMessages(requestsToRelay);
				BroadcastMessages(responsesToRelay);
			}
		}

	}

	private void BroadcastSearchResponses() {
		synchronized (_stateLock) {
			if (stateMachine.getCurrentState() == ProcessState.RESPONSE_STATE) {
				if (!responsesToBroadcast.isEmpty()) {
					DebugLog.log(person.getId() + ": Broadcasting search responses");
					for (Message msg : responsesToBroadcast)
						broker.DoBroadcast(this, getPosition(), radio.getRange_M(), msg);
				}
			}
		}
	}

	private void OnTimerRLP() {
		synchronized (_stateLock) {
			if (stateMachine.getCurrentState() == ProcessState.LISTEN_STATE) {
				DebugLog.logTimer(person.getId() + ": OnTimerRLP");
				if (!requestsToRelay.isEmpty()) {
					DebugLog.log(person.getId() + ": Relaying search requests");
					BroadcastMessages(requestsToRelay);
				}
				if (!responsesToRelay.isEmpty()) {
					DebugLog.log(person.getId() + ": Relaying search responses");
					BroadcastMessages(responsesToRelay);
				}
			}
		}
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
		timerRLPRun = false;

		synchronized (_stateLock) {
			if (stateMachine.getCurrentState() == ProcessState.LISTEN_STATE) {
				DebugLog.logTimer(person.getId() + ": OnTimerDLP");
				stateMachine.MoveNext(Command.TimerDLP); // to RLookup phase
				RunBracelet();
			}
		}
	}

	private void OnTimerLP() {
		timerDLPRun = true;
		timerRRPRun = true;

		synchronized (_stateLock) {
			if (stateMachine.getCurrentState() == ProcessState.SLEEP_STATE) {
				DebugLog.logTimer(person.getId() + ": OnTimerLP");
				stateMachine.MoveNext(Command.TimerLP); // to listen phase
				RunBracelet();
			}
		}
	}

	private void OnTimerIP() {
		timerIPRun = false;

		synchronized (_stateLock) {
			if (stateMachine.getCurrentState() == ProcessState.REQUEST_STATE) {
				requestToBroadcast = null;
				requestsToRelay = new ArrayList<>();
				responsesToRelay = new ArrayList<>();
				DebugLog.logTimer(person.getId() + ": OnTimerIP");
				stateMachine.MoveNext(Command.TimerIP); // go to interpretation phase
				RunBracelet();
			}
		}
	}

	public void storeSearchRequest(SearchRequest msg) {
		requestsToRelay.add(msg);
	}

	public void storeSearchResponseToRelay(SearchResponse msg) {
		responsesToRelay.add(msg);
	}

	public void storeSearchResponseToBroadcast(SearchResponse msg) {
		responsesToBroadcast.add(msg);
	}

}
