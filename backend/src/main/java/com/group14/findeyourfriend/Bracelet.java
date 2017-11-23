package com.group14.findeyourfriend;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import com.group14.findeyourfriend.debug.DebugLog;
import com.group14.findeyourfriend.math.Vector2;

//========================================================================
// This conversion was produced by the Free Edition of
// C# to Java Converter courtesy of Tangible Software Solutions.
// Order the Premium Edition at https://www.tangiblesoftwaresolutions.com
//========================================================================

public class Bracelet {
	private boolean guiding;
	private boolean found;
	private final StateMachineProcess _stateMachine = new StateMachineProcess();
	private TimerTask _timerCp;
	private TimerTask _timerF;
	private TimerTask _timerLed;
	private TimerTask _timerR;
	private TimerTask _timerUp;
	private Timer _timer;
	private Broker _broker;
	private final HashMap<Integer, DatabaseEntry> _dataBase = new HashMap<Integer, DatabaseEntry>();

	private final Object _stateLock = new Object();
	private final Object _dbLock = new Object();

	@SuppressWarnings("unused")
	private Battery battery;
	private Radio radio;
	// TODO implement screen
	private Person person;
	private Person _lookForPerson;
	private double _proximity = 2.0;

	public Bracelet(Battery b, Radio r, Person person) {
		battery = b;
		radio = r;
		this.person = person;
		_timer = new Timer(true); // Run as a Daemon
		_timerCp = new TimerCpTask();
		_timer.scheduleAtFixedRate(_timerCp, 1000, 10000);

		// _timerF = new TimerFTask();
		// _timer.scheduleAtFixedRate(_timerF, 500,500);

		// _timerLed = new TimerLedTask();
		// _timer.scheduleAtFixedRate(_timerLed, 8000,8000);

	}

	private class TimerCpTask extends TimerTask {

		@Override
		public void run() {
			Bracelet.this.OnTimerCp();
		}
	}

	private class TimerFTask extends TimerTask {

		@Override
		public void run() {
			Bracelet.this.OnTimerF();
		}
	}

	private class TimerLedTask extends TimerTask {

		@Override
		public void run() {
			Bracelet.this.OnTimerLed();
		}
	}

	private class TimerRTask extends TimerTask {

		@Override
		public void run() {
			Bracelet.this.OnTimerR();
		}
	}

	private class TimerUpTask extends TimerTask {

		@Override
		public void run() {
			Bracelet.this.OnTimerUp();
		}
	}

	private void RunBracelet() {
		switch (_stateMachine.getCurrentState()) {
		case SleepState:
			break;
		case CommState:
			CommState();
			break;
		case LedState:
			LedState();
			break;
		case SearchState:
			SearchState();
			break;
		case UpdateState:
			UpdateState();
			break;
		default:
			DebugLog.log(person.getId() + ": In DefaultState");
			break;
		}
	}

	private void LedState() {
		switch (_stateMachine.getLastCommand()) {
		case TimerF:
			if (_lookForPerson != null && !IsFound()) {
				synchronized (_dbLock) {
					if (_dataBase.containsKey(_lookForPerson.getId())) {
						DatabaseEntry dbEntry = _dataBase.get(_lookForPerson.getId());
						if (person.getPosition().DistanceTo(dbEntry.getPosition()) > _proximity) {
							person.GoTowards(dbEntry.getPosition());
							setFound(false);
							setGuiding(true);
							// Update LEDS
							com.group14.findeyourfriend.debug.DebugLog
									.log(person.getId() + " has not Found " + _lookForPerson.getId() + " yet");
						} else {
							setFound(true);
							setGuiding(false);
							DebugLog.log(person.getId() + " has Found " + _lookForPerson.getId());
							_timerF.cancel();
							person.setAcceleration(Vector2.Zero); // Stop when found!
							_lookForPerson.setAcceleration(Vector2.Zero); // Other person stop
							// Turn off LEDs
							_timerLed.cancel();
						}
					}
				}
			}
			break;
		case TimerLed:
			// Change the Leds??
			synchronized (_dbLock) {

				DatabaseEntry dbEntry = _dataBase.get(_lookForPerson.getId());
				person.GoTowards(dbEntry.getPosition());
				setFound(false);
				setGuiding(true);
				// Update LEDS
			}
			break;
		case FriendFound: // When friend was found in the database.
			// Start guiding??
			DebugLog.log(person.getId() + " started looking for " + _lookForPerson.getId());
			_timerF = new TimerFTask();
			_timer.scheduleAtFixedRate(_timerF, 500, 500);
			_timerLed = new TimerLedTask();
			_timer.scheduleAtFixedRate(_timerLed, 8000, 8000);
			setFound(false);
			setGuiding(true);
			break;
		case TimerUp:
			// TODO
			break;
		case TimerCp:
			// TODO
			break;
		case StartSearch:
			// TODO
			break;
		case Next:
			// TODO
			break;
		case FriendNotFound:
			// TODO
			break;
		}
		_stateMachine.MoveNext(Command.Next);
	}

	private void CommState() {
		_timerR = new TimerRTask();
		_timer.scheduleAtFixedRate(_timerR, 0, 1000);

		_timerUp = new TimerUpTask();
		_timer.schedule(_timerUp, 5000);
		_broker.DoBroadcast(this);
		DebugLog.log(person.getId() + ": Broadcasting, collecting recent and relaying recent");
	}

	private void UpdateState() {
		DebugLog.log(person.getId() + ": Updating locations");
		// Some logic needed here
		_stateMachine.MoveNext(Command.Next);
	}

	private void SearchState() {
		// Some logic for actually looking up a friend
		synchronized (_dbLock) {
			DebugLog.log(person.getId() + ": Looking up location in my DB");
			if (_lookForPerson != null) {
				if (_dataBase.containsKey(_lookForPerson.getId())) {
					setGuiding(true);
					setFound(false);
				}
				if (IsGuiding()) {
					_stateMachine.MoveNext(Command.FriendFound);
				} else {
					_stateMachine.MoveNext(Command.FriendNotFound);
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
			if (_stateMachine.getCurrentState() == ProcessState.SleepState) {
				DebugLog.log(person.getId() + ": OnTimerCp");
				_stateMachine.MoveNext(Command.TimerCp);
				RunBracelet();
			}
		}
	}

	/**
	 * Method run once the isFound timer is elapsed
	 */
	private void OnTimerF() {
		synchronized (_stateLock) {
			if (_stateMachine.getCurrentState() == ProcessState.SleepState) {
				DebugLog.log(person.getId() + ": OnTimerF");
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
	private void OnTimerLed() {
		synchronized (_stateLock) {
			if (_stateMachine.getCurrentState() == ProcessState.SleepState) {
				DebugLog.log(person.getId() + ": OnTimerLed");
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
	private void OnTimerR() {
		synchronized (_stateLock) {
			if (_stateMachine.getCurrentState() == ProcessState.CommState) {
				DebugLog.log(person.getId() + ": Rebroadcast");
				_broker.DoBroadcast(this);
				// RunBracelet();
			}
		}
	}

	/**
	 * Method run once the Update database timer is elapsed
	 */
	private void OnTimerUp() {
		_timerUp.cancel(); // Stop the timer from happening when not usefull
		_timerR.cancel(); // Stop the rebroadcast timer
		synchronized (_stateLock) {
			if (_stateMachine.getCurrentState() == ProcessState.CommState) {
				DebugLog.log(person.getId() + ": OnTimerUp");
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
			databaseEnty.setTimeStamp(java.time.LocalDateTime.now());
			_dataBase.put(sender.person.getId(), databaseEnty); // add or overwrite
			DebugLog.log(person.getId() + ": HEARD IT FROM " + sender.person.getId());
		}
	}

	public final void StartSearch(Person person) {
		synchronized (_stateLock) {
			DebugLog.log(this.person.getId() + ": StartSearch for: " + person.getId());
			_lookForPerson = person;
			_stateMachine.MoveNext(Command.StartSearch);
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

	public final Position GetPosition() {
		return person.getPosition();
	}

	public final double GetRadioRange() {
		return radio.getRange_M();
	}

}