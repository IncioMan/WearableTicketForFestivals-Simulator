package com.group14.findeyourfriend.bracelet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import com.group14.common_interface.Position;
import com.group14.common_interface.Vector2;
import com.group14.findeyourfriend.message.Broker;
import com.group14.findeyourfriend.chart.Chart;
import com.group14.findeyourfriend.debug.DebugLog;
import com.group14.findeyourfriend.statemachine.Command;
import com.group14.findeyourfriend.statemachine.ProcessState;
import com.group14.findeyourfriend.statemachine.StateMachineProcess;

import javafx.scene.chart.XYChart;

public class Bracelet {
	private boolean guiding;
	private boolean found;
	private final StateMachineProcess stateMachine = new StateMachineProcess();
	private boolean _timerFRun;
	private boolean _timerLedRun;
	private boolean _timerRRun;
	private boolean _timerUpRun;
	private Broker _broker;
	private final HashMap<Integer, DatabaseEntry> dataBase = new HashMap<>();
	private HashMap<Integer, HashSet<Long>> receivedMessages = new HashMap<>();

	protected Battery battery;
	private Radio radio;
	protected CPU cpu;
	// TODO implement screen

    protected Person owner;
	protected Person searchedPrsn;

	protected StateMachineProcess _stateMachine;

	protected boolean _timerFRun;
	protected boolean _timerLedRun;
	protected boolean _timerRRun;
	protected boolean _timerUpRun;

	private Broker _broker;

	protected final HashMap<Integer, DatabaseEntry> dataBase = new HashMap<>();

	protected final Object _stateLock = new Object();
	protected final Object _dbLock = new Object();

	private double _proximity = 2.0;

	private double broadcastTime;
	private double updateLedTime;

	private double visualFeedBackCurrent_mA;
	private double visualFeedBackOnTime;

	public Bracelet(Battery b, Radio r, CPU c, Person owner) {
		battery = b;
		radio = r;
		this.owner = owner;
		this.cpu = c;
		_stateMachine = new StateMachineProcess();

		broadcastTime = 0.1;
		updateLedTime = 0.0001;
		visualFeedBackCurrent_mA = 20;
		visualFeedBackOnTime = 8000;
	}

	protected void RunBracelet() {
		switch (_stateMachine.getCurrentState()) {
		case SleepState:
			break;
		case COMMUNICATION_STATE:
			CommState();
			break;
		case LED_STATE:
			LedState();
			break;
		case SLookupState:
			SLookupState();
			break;
		case UPDATE_STATE:
			UpdateState();
			break;
		default:
			DebugLog.log(owner.getId() + ": In DefaultState");
			break;
		}
	}

	protected void LedState() {
		switch (_stateMachine.getLastCommand()) {
		case TimerF:
			if (searchedPrsn != null && !IsFound()) {
				synchronized (_dbLock) {
					if (dataBase.containsKey(_lookForPerson.getId())) {
						DatabaseEntry dbEntry = dataBase.get(_lookForPerson.getId());
						if (person.getPosition().DistanceTo(dbEntry.getPosition()) > _proximity) {
							battery.DecrementEnergy(cpu.cpuCurrentRun_mA, updateLedTime);// Decrement battery from CPU
																							// time
							person.GoTowards(dbEntry.getPosition());
							setFound(false);
							setGuiding(true);
							// Update LEDS
							DebugLog.log(owner.getId() + " has not Found " + searchedPrsn.getId() + " yet");
						} else {
							setFound(true);
							setGuiding(false);
							DebugLog.log(owner.getId() + " has Found " + searchedPrsn.getId());
							_timerFRun = false;
							person.setSpeed(Vector2.Zero); // Stop when found!
							_lookForPerson.setSpeed(Vector2.Zero); // Other person stop
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
				DatabaseEntry dbEntry = dataBase.get(searchedPrsn.getId());
				owner.GoTowards(dbEntry.getPosition());
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
			DebugLog.log(owner.getId() + " started looking for " + searchedPrsn.getId());
			_timerFRun = true;
			_timerLedRun = true;
			setFound(false);
			setGuiding(true);
			break;
		}
		stateMachine.MoveNext(Command.Next);
	}

	protected void CommState() {
		_timerRRun = true;
		_timerUpRun = true;
		broadcast();
		battery.DecrementEnergy(cpu.cpuCurrentBroadcastAvg_mA, cpu.timerUpDelay);// Decrement battery from CPU for the
																					// entire broadcast
		// radio.setState(RadioState.Transmitting);
		// battery.DecrementEnergy(radio.getConsumption(), broadcastTime);// Decrement
		// from radio for single broadcast
		// radio.setState(RadioState.Passive);

		DebugLog.log(owner.getId() + ": Broadcasting, collecting recent and relaying recent");
	}

	protected void UpdateState() {
		DebugLog.log(owner.getId() + ": Updating locations");
		battery.DecrementEnergy(cpu.cpuCurrentRun_mA, 1000);
		// Some logic needed here
		stateMachine.MoveNext(Command.Next);
	}

	protected void SLookupState() {
		// Some logic for actually looking up a friend
		synchronized (_dbLock) {
			DebugLog.log(owner.getId() + ": Looking up location in my DB");
			if (searchedPrsn != null) {
                battery.DecrementEnergy(cpu.cpuCurrentRun_mA, 1000);
				if (dataBase.containsKey(searchedPrsn.getId())) {
				    // TODO implement if(!recentEnough) then it is not found
					setGuiding(true);
					setFound(false);
					stateMachine.MoveNext(Command.FriendFound);
					DebugLog.log(person.getId() + ": Found person in my DB");
				} else {
					stateMachine.MoveNext(Command.FriendNotFound);
					DebugLog.log(person.getId() + ": Did not find person in my DB");
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
    protected void OnTimerR() {
		synchronized (_stateLock) {
			if (stateMachine.getCurrentState() == ProcessState.COMMUNICATION_STATE) {
				DebugLog.logTimer(person.getId() + ": Rebroadcast");
				broadcast();
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
		_timerRRun = false;
		_timerUpRun = false;
		// _timerUp.cancel(); // Stop the timer from happening when not usefull
		// _timerR.cancel(); // Stop the rebroadcast timer
		synchronized (_stateLock) {
			if (stateMachine.getCurrentState() == ProcessState.COMMUNICATION_STATE) {
				DebugLog.logTimer(person.getId() + ": OnTimerUp");
				stateMachine.MoveNext(Command.TimerUp);
				RunBracelet();
			}
		}
	}

	public final void Subscribe(Broker broker) {
		_broker = broker;
		_broker.AddBracelet(this);
	}

	/**
	 * Method to handle incomming broadcast
	 * 
	 * @param senderId
	 * @param messageId
	 */
	public final void HandleBroadcast(int senderId, long messageId, HashMap<Integer, DatabaseEntry> senderDataBase) {
		if (senderId == this.person.getId()) {
			return;
		}
		HashSet<Long> senderMessageIds = receivedMessages.getOrDefault(senderId, new HashSet<>());
		if(senderMessageIds.contains(messageId)) return; // Only handle received message once
        receivedMessages.put(senderId, senderMessageIds);// Add to received messages
		synchronized (_dbLock) // Thread safety locking
		{
		    for (int dbKey: senderDataBase.keySet()) {
                if(dbKey == person.getId()) continue; //Dont update my own position
                DatabaseEntry entry = senderDataBase.get(dbKey);
                if(entry.getTimeStamp() > dataBase.getOrDefault(dbKey, new DatabaseEntry()).getTimeStamp()) dataBase.put(dbKey, entry); // update or overwrite
            }
			DebugLog.logTimer(person.getId() + ": HEARD IT FROM " + senderId);
		}
	}

	public void StartSearch(Person person) {
		synchronized (_stateLock) {
			DebugLog.log(this.person.getId() + ": StartSearch for: " + person.getId());
			_lookForPerson = person;
			stateMachine.MoveNext(Command.StartSearch);
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
	private void broadcast(){
	    int myId = person.getId();
        HashSet<Long> myMessages = receivedMessages.getOrDefault(myId, new HashSet<>());
        long messageId = System.currentTimeMillis();
        myMessages.add(messageId);
        receivedMessages.put(myId, myMessages);
        DatabaseEntry dbE = new DatabaseEntry();
        dbE.setPosition(getPosition());
        dbE.setTimeStamp(System.currentTimeMillis());
        dataBase.put(person.getId(), dbE);
		_broker.DoBroadcast(person.getId(), getPosition(), radio.getRange_M(), messageId, getDataBase());
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
		if (clock % cpu.timerRDelay == 0 && _timerRRun) {
			//
			OnTimerR();
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

}