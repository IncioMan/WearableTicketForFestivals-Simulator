package com.group14.findeyourfriend.bracelet;

import java.util.ArrayList;
import java.util.HashMap;

import com.group14.common_interface.Position;
import com.group14.common_interface.Vector2;
import com.group14.findeyourfriend.Clock;
import com.group14.findeyourfriend.chart.Chart;
import com.group14.findeyourfriend.debug.DebugLog;
import com.group14.findeyourfriend.message.Broker;
import com.group14.findeyourfriend.message.Message;
import com.group14.findeyourfriend.message.UpdateMessage;
import com.group14.findeyourfriend.simulation.events.ConcertEvent;
import com.group14.findeyourfriend.statemachine.Command;
import com.group14.findeyourfriend.statemachine.ProcessState;
import com.group14.findeyourfriend.statemachine.StateMachineProcess;

import javafx.scene.chart.XYChart;

public class Bracelet {
	private boolean guiding;
	private boolean found;

	protected Battery battery;
	protected Radio radio;
	protected CPU cpu;
	// TODO implement screen

	protected Person person;
	protected Person _lookForPerson;

	protected StateMachineProcess stateMachine;

	protected boolean _timerFRun;
	protected boolean _timerLedRun;
	protected boolean _timerRCPRun;
	protected boolean _timerUpRun;

	protected Broker broker;

	// protected ArrayList<Message> updateMessagesToRelay = new ArrayList<>();

	protected final HashMap<Integer, DatabaseEntry> dataBase = new HashMap<>();

	protected final Object _stateLock = new Object();
	protected final Object _dbLock = new Object();

	protected double _proximity = 2.0;

	// protected double broadcastTime;
	protected double updateLedTime;

	protected double visualFeedBackCurrent_mA;
	protected double visualFeedBackOnTime;

	private ConcertEvent event;
	private boolean _timerMoveToEventRun;

	public Bracelet(Battery b, Radio r, CPU c, Person person) {
		battery = b;
		radio = r;
		this.person = person;
		this.cpu = c;
		stateMachine = new StateMachineProcess();

		DatabaseEntry dbE = new DatabaseEntry();
		dbE.setPosition(getPosition());
		dbE.setTimeStamp(Clock.getClock());
		dataBase.put(person.getId(), dbE);

		// broadcastTime = 0.1;
		updateLedTime = 0.0001;
		visualFeedBackCurrent_mA = 20;
		visualFeedBackOnTime = 8000;
	}

	protected void RunBracelet() {
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
		default:
			DebugLog.log(person.getId() + ": In DefaultState");
			break;
		}
	}

	protected void LedState() {
		switch (stateMachine.getLastCommand()) {
		case TimerEvent:
			if (person.getPosition().DistanceTo(event.getConcertLocation()) > _proximity) {
				// Update LEDS
				person.GoTowards(event.getConcertLocation());
				DebugLog.log(person.getId() + " not arrived to the event " + event.getConcertLocation().getCoordinates()
						+ " yet");
			} else {
				if (Clock.getClock() > event.getEndTime()) { // TODO implement parameter passing?
					_timerMoveToEventRun = false;
					event = null;
					// Turn off LEDs
					_timerLedRun = false;
					person.setSpeed(Vector2.getRandomVector());
				} else {
					DebugLog.log(person.getId() + " has arrived to the event "
							+ event.getConcertLocation().getCoordinates());
					person.setSpeed(Vector2.Zero); // Stop when found!
				}
			}
		case TimerF:
			if (_lookForPerson != null && !IsFound()) {
				synchronized (_dbLock) {
					if (dataBase.containsKey(_lookForPerson.getId())) {
						DatabaseEntry dbEntry = dataBase.get(_lookForPerson.getId());
						if (person.getPosition().DistanceTo(dbEntry.getPosition()) > _proximity) {
							// Decrement battery from CPU
							battery.DecrementEnergy(cpu.cpuCurrentRun_mA, updateLedTime);
							person.GoTowards(dbEntry.getPosition());
							setFound(false);
							setGuiding(true);
							// Update LEDS
							DebugLog.log(person.getId() + " has not Found " + _lookForPerson.getId() + " yet");
						} else {
							setFound(true);
							setGuiding(false);
							DebugLog.log(person.getId() + " has Found " + _lookForPerson.getId());
							_timerFRun = false;
							person.setSpeed(Vector2.Zero); // Stop when found!
							_lookForPerson.setSpeed(Vector2.Zero); // Other person stop
							broker.notifyEvent(this.getPerson(), BraceletEvent.FRIEND_MET);
							// Turn off LEDs
							_timerLedRun = false;
						}
					}
				}
			}
			break;
		case TimerLed:
			// Change the Leds??
			synchronized (_dbLock) {
				DatabaseEntry dbEntry = dataBase.get(_lookForPerson.getId());
				person.GoTowards(dbEntry.getPosition());
				setFound(false);
				setGuiding(true);
				// Update LEDS
				battery.DecrementEnergy(visualFeedBackCurrent_mA, visualFeedBackOnTime);// Decrement battery from
																						// showing visuals LED/Eink or
																						// whatever
				battery.DecrementEnergy(cpu.cpuCurrentRun_mA, updateLedTime);// Decrement battery from CPU time
			}
			break;
		case FriendFound: // When friend was found in the database.
			// Start guiding??
			DebugLog.log(person.getId() + " started looking for " + _lookForPerson.getId());
			_timerMoveToEventRun = false; // stop going to event
			_timerFRun = true;
			_timerLedRun = true;
			broker.notifyEvent(this.getPerson(), BraceletEvent.FRIEND_FOUND_IN_DB);
			setFound(false);
			setGuiding(true);
			break;
		case GoToEvent:
			DebugLog.log(person.getId() + " moving towards event " + event.getConcertLocation().getCoordinates());
			_timerMoveToEventRun = true;
			_timerLedRun = true;
			_timerFRun = false;
			break;
		}
		stateMachine.MoveNext(Command.Sleep);
	}

	protected void CommState() {
		_timerRCPRun = true;
		_timerUpRun = true;

		CreateUpdateMessage();
		// BroadcastMessages(updateMessagesToRelay);

		battery.DecrementEnergy(cpu.cpuCurrentBroadcastAvg_mA, cpu.timerUpDelay);// Decrement battery from CPU for the
																					// entire CreateUpdateMessage
		// TODO here is a crucial point. Should we decrement on a message basis or for
		// the whole duration of the phase?

		DebugLog.log(person.getId() + ": Broadcasting, collecting recent and relaying recent");
	}

	protected void UpdateState() {
		// DebugLog.log(person.getId() + ": Updating locations");
		battery.DecrementEnergy(cpu.cpuCurrentRun_mA, 1000);
		// Some logic needed here
		stateMachine.MoveNext(Command.Sleep);
	}

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
						broker.notifyEvent(getPerson(), BraceletEvent.FRIEND_NOT_FOUND_IN_DB);
						stateMachine.MoveNext(Command.FriendNotFound);
					}
				} else {
					DebugLog.log(person.getId() + ": Did not find person in my DB");
					broker.notifyEvent(getPerson(), BraceletEvent.FRIEND_NOT_FOUND_IN_DB);
					stateMachine.MoveNext(Command.FriendNotFound);
				}
				RunBracelet();
			}
		}
	}

	/**
	 * Method run once the bracelet should go into communication phase
	 */
	protected void OnTimerCp() {
		synchronized (_stateLock) {
			if (stateMachine.getCurrentState() == ProcessState.SLEEP_STATE) {
				DebugLog.logTimer(person.getId() + ": OnTimerCp");
				stateMachine.MoveNext(Command.TimerCp);
				RunBracelet();
			}
		}
	}

	/**
	 * Method run once the isFound timer is elapsed
	 */
	protected void OnTimerF() {
		synchronized (_stateLock) {
			if (stateMachine.getCurrentState() == ProcessState.SLEEP_STATE) {
				DebugLog.logTimer(person.getId() + ": OnTimerF");
				if (!IsFound() && IsGuiding()) {
					stateMachine.MoveNext(Command.TimerF); // Go to change LEDS
					RunBracelet();
				}
			}
		}
	}

	/**
	 * Method run once the LedTimer is elapsed
	 */
	protected void OnTimerLed() {
		synchronized (_stateLock) {
			if (stateMachine.getCurrentState() == ProcessState.SLEEP_STATE) {
				DebugLog.logTimer(person.getId() + ": OnTimerLed");
				if (IsGuiding()) {
					stateMachine.MoveNext(Command.TimerLed); // Go to Update Leds according to database
					RunBracelet();
				}
			}
		}
	}

	/**
	 * Method run once the Rebroadcast timer is elapsed
	 *
	 */
	protected void OnTimerRCP() {
		synchronized (_stateLock) {
			if (stateMachine.getCurrentState() == ProcessState.COMMUNICATION_STATE) {
				DebugLog.logTimer(person.getId() + ": Rebroadcast");
				CreateUpdateMessage();
				// BroadcastMessages(updateMessagesToRelay);
				// radio.setState(RadioState.Transmitting);
				// battery.DecrementEnergy(radio.getConsumption(), broadcastTime);
				// radio.setState(RadioState.Passive);
				// RunBracelet();
			}
		}
	}

	/**
	 * Method run once the Update database timer is elapsed
	 */
	protected void OnTimerUp() {
		_timerRCPRun = false;
		_timerUpRun = false;
		// _timerUp.cancel(); // Stop the timer from happening when not usefull
		// _timerR.cancel(); // Stop the rebroadcast timer
		synchronized (_stateLock) {
			if (stateMachine.getCurrentState() == ProcessState.COMMUNICATION_STATE) {
				// updateMessagesToRelay = new ArrayList<>();
				DebugLog.logTimer(person.getId() + ": OnTimerUp");
				stateMachine.MoveNext(Command.TimerUp);
				RunBracelet();
			}
		}
	}

	public final void Subscribe(Broker broker) {
		this.broker = broker;
		this.broker.AddBracelet(this);
	}

	/**
	 * Method to handle incoming CreateUpdateMessage
	 *
	 */
	public final void HandleBroadcast(Message msg) {

		synchronized (_dbLock) // Thread safety locking
		{
			msg.process();

		}

	}

	public void StartSearch(Person person) {
		synchronized (_stateLock) {
			// DebugLog.log(this.person.getId() + ": started searching for " +
			// person.getId());
			_lookForPerson = person;
			double distance = this.person.getPosition().DistanceTo(_lookForPerson.getPosition());
			// System.out.println("Distance to other person is: " + String.format("%1$,.2f",
			// distance));
			stateMachine.MoveNext(Command.StartSearch);
			broker.notifyEvent(this.getPerson(), BraceletEvent.START_SEARCH);
			RunBracelet();
		}
	}

	// FIXME accept an abstract Event with endTime
	public void takeMeToEvent(ConcertEvent event) {
		synchronized (_stateLock) {
			DebugLog.log(this.person.getId() + ": started guiding to event " + event);
			this.event = event;
			stateMachine.MoveNext(Command.GoToEvent);
			broker.notifyEvent(this.getPerson(), BraceletEvent.GO_TO_CONCERT);
			RunBracelet();
		}
	}

	public final boolean IsFound() {
		return found;
	}

	protected void setFound(boolean value) {
		found = value;
	}

	public boolean IsGuiding() {
		return guiding;
	}

	public void setGuiding(boolean value) {
		guiding = value;
	}

	public final Position getPosition() {
		return person.getPosition();
	}

	public HashMap<Integer, DatabaseEntry> getDataBase() {
		return dataBase;
	}

	public final double getRadioRange() {
		return radio.getRange_M();
	}

	public StateMachineProcess getStateMachine() {
		return stateMachine;
	}

	private void CreateUpdateMessage() {
		DatabaseEntry dbE = new DatabaseEntry();
		dbE.setPosition(getPosition());
		dbE.setTimeStamp(Clock.getClock());
		dataBase.put(person.getId(), dbE);
		UpdateMessage updateMessage = new UpdateMessage(this, dataBase);
		// storeUpdateMessage(updateMessage);
		broker.DoBroadcast(this, getPosition(), radio.getRange_M(), updateMessage);
	}

	protected void BroadcastMessages(ArrayList<Message> messages) {
		for (Message msg : messages) {
			broker.DoBroadcast(this, getPosition(), radio.getRange_M(), msg);
		}

	}

	public void transition(int clock) {

		if (clock % cpu.timerCpDelay == 0) {
			// Goto Communication phase
			OnTimerCp();
		}
		if (clock % cpu.timerFDelay == 0 && _timerFRun) {
			// Goto
			OnTimerF();
		}
		if (clock % cpu.timerMoveToEventDelay == 0 && _timerMoveToEventRun) {
			// Goto
			OnTimerMoveToEvent();
		}
		if (clock % cpu.timerRCPDelay == 0 && _timerRCPRun) {
			//
			OnTimerRCP();
		}
		if (clock % cpu.timerUpDelay == 0 && _timerUpRun) {
			OnTimerUp();
		}
		if (clock % cpu.timerLedDelay == 0 && _timerLedRun) {
			OnTimerLed();
		}
		// else battery.DecrementEnergy(cpu.cpuCurrentSleep_mA, 0.001);

		if (clock % 60000 == 0) {
			ArrayList<XYChart.Data> dataPoints = Chart.DataPointsMap.getOrDefault(person.getId(), new ArrayList<>());
			dataPoints.add(new XYChart.Data(clock / 60000, battery.getEnergyLeft())); // every "minute" new datapoint
			Chart.DataPointsMap.putIfAbsent(person.getId(), dataPoints);
		}
	}

	private void OnTimerMoveToEvent() {
		if (stateMachine.getCurrentState() == ProcessState.SLEEP_STATE) {
			DebugLog.logTimer(person.getId() + ": OnTimerMoveToEvent");
			if (event != null) {
				stateMachine.MoveNext(Command.TimerEvent); // Go to change LEDS
				RunBracelet();
			}
		}
	}

	public Person getPerson() {
		return person;
	}

	// public void storeUpdateMessage(UpdateMessage msg)
	// {updateMessagesToRelay.add(msg);}
}