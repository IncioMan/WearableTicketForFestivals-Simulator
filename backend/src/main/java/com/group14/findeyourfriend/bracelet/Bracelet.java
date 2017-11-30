package com.group14.findeyourfriend.bracelet;

import java.util.ArrayList;
import java.util.HashMap;

import com.group14.common_interface.Position;
import com.group14.common_interface.Vector2;
import com.group14.findeyourfriend.chart.Chart;
import com.group14.findeyourfriend.debug.DebugLog;
import com.group14.findeyourfriend.statemachine.Command;
import com.group14.findeyourfriend.statemachine.ProcessState;
import com.group14.findeyourfriend.statemachine.StateMachineProcess;

import javafx.scene.chart.XYChart;

//========================================================================
// This conversion was produced by the Free Edition of
// C# to Java Converter courtesy of Tangible Software Solutions.
// Order the Premium Edition at https://www.tangiblesoftwaresolutions.com
//========================================================================

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

	private final Object _stateLock = new Object();
	private final Object _dbLock = new Object();

	private Battery battery;
	private Radio radio;
	private CPU cpu;
	// TODO implement screen
	private Person person;
	private Person _lookForPerson;
	private double _proximity = 2.0;
	private double broadcastTime;
	private double updateLedTime;
	private double visualFeedBackCurrent_mA;
	private double visualFeedBackOnTime;

	public Bracelet(Battery b, Radio r, CPU c, Person person) {
		battery = b;
		radio = r;
		this.person = person;
		this.cpu = c;

		broadcastTime = 0.1;
		updateLedTime = 0.0001;
		visualFeedBackCurrent_mA = 20;
		visualFeedBackOnTime = 8000;
	}

	private void RunBracelet() {
		switch (stateMachine.getCurrentState()) {
		case SLEEP_STATE:
			break;
		case COMMUNICATION_STATE:
			CommState();
			break;
		case LED_STATE:
			LedState();
			break;
		case SEARCH_STATE:
			SearchState();
			break;
		case UPDATE_STATE:
			UpdateState();
			break;
		default:
			DebugLog.log(person.getId() + ": In DefaultState");
			break;
		}
	}

	private void LedState() {
		switch (stateMachine.getLastCommand()) {
		case TimerF:
			if (_lookForPerson != null && !IsFound()) {
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
							DebugLog.log(person.getId() + " has not Found " + _lookForPerson.getId() + " yet");
						} else {
							setFound(true);
							setGuiding(false);
							DebugLog.log(person.getId() + " has Found " + _lookForPerson.getId());
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
			_timerFRun = true;
			_timerLedRun = true;
			setFound(false);
			setGuiding(true);
			break;
		}
		stateMachine.MoveNext(Command.Next);
	}

	private void CommState() {
		_timerRRun = true;
		_timerUpRun = true;
		_broker.DoBroadcast(this);

		battery.DecrementEnergy(cpu.cpuCurrentBroadcastAvg_mA, cpu.timerUpDelay);// Decrement battery from CPU for the
																					// entire broadcast
		// radio.setState(RadioState.Transmitting);
		// battery.DecrementEnergy(radio.getConsumption(), broadcastTime);// Decrement
		// from radio for single broadcast
		// radio.setState(RadioState.Passive);

		DebugLog.log(person.getId() + ": Broadcasting, collecting recent and relaying recent");
	}

	private void UpdateState() {
		DebugLog.log(person.getId() + ": Updating locations");
		battery.DecrementEnergy(cpu.cpuCurrentRun_mA, 1000);
		// Some logic needed here
		stateMachine.MoveNext(Command.Next);
	}

	private void SearchState() {
		// Some logic for actually looking up a friend
		synchronized (_dbLock) {
			DebugLog.log(person.getId() + ": Looking up location in my DB");
			if (_lookForPerson != null) {
				battery.DecrementEnergy(cpu.cpuCurrentRun_mA, 1000);
				if (dataBase.containsKey(_lookForPerson.getId())) {
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
	private void OnTimerCp() {
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
	private void OnTimerF() {
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
	private void OnTimerLed() {
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
	private void OnTimerR() {
		synchronized (_stateLock) {
			if (stateMachine.getCurrentState() == ProcessState.COMMUNICATION_STATE) {
				DebugLog.logTimer(person.getId() + ": Rebroadcast");
				_broker.DoBroadcast(this);
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
	private void OnTimerUp() {
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
	 * @param sender
	 * @param position
	 */
	public final void HandleBroadcast(Bracelet sender, Position position) {
		// Maybe add check to see if we are in CommState? I guess we don't want to
		// handle broadcast else?
		if (sender.person.getId() == this.person.getId()) {
			return;
		}
		synchronized (_dbLock) // Thread safety locking
		{
			DatabaseEntry databaseEnty = new DatabaseEntry();
			databaseEnty.setPosition(position);

			databaseEnty.setTimeStamp(System.currentTimeMillis());
			dataBase.put(sender.person.getId(), databaseEnty); // add or overwrite
			DebugLog.logTimer(person.getId() + ": HEARD IT FROM " + sender.person.getId());
		}
	}

	public final void StartSearch(Person person) {
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

	private void setFound(boolean value) {
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
			// Rebroadcast
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