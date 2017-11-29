package com.group14.findeyourfriend.bracelet;

import java.util.ArrayList;
import java.util.HashMap;

import com.group14.common_interface.Position;
import com.group14.common_interface.Vector2;
import com.group14.findeyourfriend.message.Broker;
import com.group14.findeyourfriend.statemachine.Command;
import com.group14.findeyourfriend.statemachine.ProcessState;
import com.group14.findeyourfriend.statemachine.StateMachineProcess;
import com.group14.findeyourfriend.chart.Chart;
import com.group14.findeyourfriend.debug.DebugLog;
import javafx.scene.chart.XYChart;

public class Bracelet {
	private boolean guiding;
	private boolean found;

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
					if (dataBase.containsKey(searchedPrsn.getId())) {
						DatabaseEntry dbEntry = dataBase.get(searchedPrsn.getId());
						if (owner.getPosition().DistanceTo(dbEntry.getPosition()) > _proximity) {
							battery.DecrementEnergy(cpu.cpuCurrentRun_mA, updateLedTime);// Decrement battery from CPU time
							owner.GoTowards(dbEntry.getPosition());
							setFound(false);
							setGuiding(true);
							// Update LEDS
							DebugLog.log(owner.getId() + " has not Found " + searchedPrsn.getId() + " yet");
						} else {
							setFound(true);
							setGuiding(false);
							DebugLog.log(owner.getId() + " has Found " + searchedPrsn.getId());
							_timerFRun = false;
							owner.setAcceleration(Vector2.Zero); // Stop when found!
							searchedPrsn.setAcceleration(Vector2.Zero); // Other owner stop
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
		_stateMachine.MoveNext(Command.Sleep);
	}

	protected void CommState() {
		_timerRRun = true;
		_timerUpRun = true;

		// TODO implement send recent locations only
        // UpdateMessage = new ...
		_broker.DoBroadcast(this);
	    // TODO implement relay of locations

		battery.DecrementEnergy(cpu.cpuCurrentBroadcastAvg_mA, cpu.timerUpDelay);// Decrement battery from CPU for the entire broadcast
//		radio.setState(RadioState.Transmitting);
//		battery.DecrementEnergy(radio.getConsumption(), broadcastTime);// Decrement from radio for single broadcast
//		radio.setState(RadioState.Passive);

		DebugLog.log(owner.getId() + ": Broadcasting, collecting recent and relaying recent");
	}

	protected void UpdateState() {
		DebugLog.log(owner.getId() + ": Updating locations");
		battery.DecrementEnergy(cpu.cpuCurrentRun_mA, 1000);
		// Some logic needed here
		_stateMachine.MoveNext(Command.Sleep);
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
					_stateMachine.MoveNext(Command.FriendFound);
					DebugLog.log(owner.getId() + ": Found owner in my DB");
				} else {
					_stateMachine.MoveNext(Command.FriendNotFound);
					DebugLog.log(owner.getId() + ": Did not find owner in my DB");
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
			if (_stateMachine.getCurrentState() == ProcessState.SleepState) {
				DebugLog.logTimer(owner.getId() + ": OnTimerCp");
				_stateMachine.MoveNext(Command.TimerCp);
				RunBracelet();
			}
		}
	}

	/**
	 * Method run once the isFound timer is elapsed
	 */
    protected void OnTimerF() {
		synchronized (_stateLock) {
			if (_stateMachine.getCurrentState() == ProcessState.SleepState) {
				DebugLog.logTimer(owner.getId() + ": OnTimerF");
				if (!IsFound() && IsGuiding()) {
					_stateMachine.MoveNext(Command.TimerF); // Go to change LEDS
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
			if (_stateMachine.getCurrentState() == ProcessState.SleepState) {
				DebugLog.logTimer(owner.getId() + ": OnTimerLed");
				if (IsGuiding()) {
					_stateMachine.MoveNext(Command.TimerLed); // Go to Update Leds according to database
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
			if (_stateMachine.getCurrentState() == ProcessState.CommState) {
				DebugLog.logTimer(owner.getId() + ": Rebroadcast");
				_broker.DoBroadcast(this);
//				radio.setState(RadioState.Transmitting);
//				battery.DecrementEnergy(radio.getConsumption(), broadcastTime);
//				radio.setState(RadioState.Passive);
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
			if (_stateMachine.getCurrentState() == ProcessState.CommState) {
				DebugLog.logTimer(owner.getId() + ": OnTimerUp");
				_stateMachine.MoveNext(Command.TimerUp);
				RunBracelet();
			}
		}
	}

	public final void Subscribe(Broker broker) {
		_broker = broker;
		_broker.AddBracelet(this);
	}

	/**
	 * Method to handle incoming broadcast
	 */
	public final void HandleBroadcast(Bracelet sender, Position position) {
        // TODO only handle broadcast in commstate
        // TODO synchronize update phase of bracelets at the start of every minute (clock % 60000 == 0)
		if (sender.owner.getId() == this.owner.getId()) {
			return;
		}
		synchronized (_dbLock) // Thread safety locking
		{
			DatabaseEntry databaseEnty = new DatabaseEntry();
			databaseEnty.setPosition(position);

            databaseEnty.setTimeStamp(System.currentTimeMillis());
            dataBase.put(sender.owner.getId(), databaseEnty); // add or overwrite
            DebugLog.logTimer(owner.getId() + ": HEARD IT FROM " + sender.owner.getId());
		}
	}

	public void StartSearch(Person person) {
		synchronized (_stateLock) {
			DebugLog.log(this.owner.getId() + ": StartSearch for: " + person.getId());
			searchedPrsn = person;
			_stateMachine.MoveNext(Command.StartSearch);
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

	public final Position GetPosition() {
		return owner.getPosition();
	}

	public HashMap<Integer, DatabaseEntry> getDataBase() {
		return dataBase;
	}

	public final double GetRadioRange() {
		return radio.getRange_M();
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
//		else battery.DecrementEnergy(cpu.cpuCurrentSleep_mA, 0.001);

		if (clock % 60000 == 0) {
            ArrayList<XYChart.Data> dataPoints = Chart.DataPointsMap.getOrDefault(owner.getId(),  new ArrayList<>());
			dataPoints.add(new XYChart.Data(clock / 60000, battery.getEnergyLeft())); // every "minute" new datapoint
            Chart.DataPointsMap.putIfAbsent(owner.getId(), dataPoints);
        }
	}

}