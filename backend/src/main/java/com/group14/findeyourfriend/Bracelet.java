package com.group14.findeyourfriend;

import com.group14.findeyourfriend.chart.Chart;
import com.group14.findeyourfriend.debug.DebugLog;
import com.group14.findeyourfriend.math.Vector2;
import javafx.scene.chart.XYChart;

import java.util.*;

//========================================================================
// This conversion was produced by the Free Edition of
// C# to Java Converter courtesy of Tangible Software Solutions.
// Order the Premium Edition at https://www.tangiblesoftwaresolutions.com
//========================================================================



public class Bracelet
{
	private boolean guiding;
	private boolean found;
	private final StateMachineProcess _stateMachine = new StateMachineProcess();
	private int _timerCpDelay;
	private int _timerFDelay;
	private boolean _timerFRun;
	private int _timerLedDelay;
	private boolean _timerLedRun;
	private int _timerRDelay;
	private boolean _timerRRun;
	private boolean _timerUpRun;
	private int _timerUpDelay;
	private Broker _broker;
	private final HashMap<Integer, DatabaseEntry> _dataBase = new HashMap<Integer, DatabaseEntry>();

	private final Object _stateLock = new Object();
	private final Object _dbLock = new Object();

	private Battery battery;
	private Radio radio;
	// TODO implement screen
	private Person person;
	private Person _lookForPerson;
	private double _proximity = 2.0;
	private double cpuCurrentSleep_mA;
	private double cpuCurrentRun_mA;
	private double broadcastTime;
	private double updateLedTime;
	private double visualFeedBackCurrent_mA;
	private double visualFeedBackOnTime;

	public Bracelet(Battery b, Radio r, Person person)
	{
		battery = b;
		radio = r;
		this.person = person;
		_timerFDelay = 500;
		_timerCpDelay = 10000;
		_timerLedDelay = 8000;
		_timerUpDelay = 1000;
		_timerRDelay = 500;
		cpuCurrentSleep_mA = 0.0002;
		cpuCurrentRun_mA = 0.35;
		broadcastTime = 0.1;
		updateLedTime = 0.1;
		visualFeedBackCurrent_mA = 20;
		visualFeedBackOnTime = 8000;
	}

	private void RunBracelet()
	{
		switch (_stateMachine.getCurrentState())
		{
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

	private void LedState()
	{
		switch (_stateMachine.getLastCommand())
		{
			case TimerF:
				if (_lookForPerson != null && !IsFound())
				{
					synchronized (_dbLock)
					{
						if (_dataBase.containsKey(_lookForPerson.getId()))
						{
							DatabaseEntry dbEntry = _dataBase.get(_lookForPerson.getId());
							if (person.getPosition().DistanceTo(dbEntry.getPosition()) > _proximity)
							{
								battery.DecrementEnergy(cpuCurrentRun_mA,updateLedTime);//Decrement battery from CPU time
								person.GoTowards(dbEntry.getPosition());
								setFound(false);
								setGuiding(true);
								//Update LEDS
								DebugLog.log(person.getId() + " has not Found " + _lookForPerson.getId() + " yet");
							}
							else
							{
								setFound(true);
								setGuiding(false);
								DebugLog.log(person.getId() + " has Found " + _lookForPerson.getId());
								_timerFRun = false;
								person.setAcceleration(Vector2.Zero); // Stop when found!
								_lookForPerson.setAcceleration(Vector2.Zero); // Other person stop
								// Turn off LEDs
								_timerLedRun = false;
							}
						}
					}
				}
				break;
			case TimerLed:
				// Change the Leds??
				synchronized (_dbLock)
				{
					DatabaseEntry dbEntry = _dataBase.get(_lookForPerson.getId());
					person.GoTowards(dbEntry.getPosition());
					setFound(false);
					setGuiding(true);
					//Update LEDS
					battery.DecrementEnergy(visualFeedBackCurrent_mA, visualFeedBackOnTime);//Decrement battery from showing visuals LED/Eink or whatever
					battery.DecrementEnergy(cpuCurrentRun_mA,updateLedTime);//Decrement battery from CPU time
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
		_stateMachine.MoveNext(Command.Next);
	}

	private void CommState()
	{
		_timerRRun = true;
		_timerUpRun = true;
		_broker.DoBroadcast(this);

		battery.DecrementEnergy(cpuCurrentRun_mA, _timerUpDelay);//Decrement battery from CPU for the entire broadcast
		radio.setState(RadioState.Transmitting);
		battery.DecrementEnergy(radio.getConsumption(), broadcastTime);// Decrement from radio for single broadcast
		radio.setState(RadioState.Passive);

		DebugLog.log(person.getId() + ": Broadcasting, collecting recent and relaying recent");
	}

	private void UpdateState()
	{
		DebugLog.log(person.getId() + ": Updating locations");
		battery.DecrementEnergy(cpuCurrentRun_mA, 1);
		// Some logic needed here
		_stateMachine.MoveNext(Command.Next);
	}

	private void SearchState()
	{
		//Some logic for actually looking up a friend
		synchronized (_dbLock)
		{
			DebugLog.log(person.getId() + ": Looking up location in my DB");
			if (_lookForPerson != null)
			{
				battery.DecrementEnergy(cpuCurrentRun_mA, 1);
				if (_dataBase.containsKey(_lookForPerson.getId()))
				{
					setGuiding(true);
					setFound(false);
					_stateMachine.MoveNext(Command.FriendFound);
					DebugLog.log(person.getId() + ": Found person in my DB");
				}else{
					_stateMachine.MoveNext(Command.FriendNotFound);
					DebugLog.log(person.getId() + ": Did not find person in my DB");
				}
				RunBracelet();
			}
		}
	}




	/**
	 Method run once the bracelet should go into communication phase
	 */
	private void OnTimerCp()
	{
		synchronized (_stateLock)
		{
			if (_stateMachine.getCurrentState() == ProcessState.SleepState)
			{
				DebugLog.logTimer(person.getId() + ": OnTimerCp");
				_stateMachine.MoveNext(Command.TimerCp);
				RunBracelet();
			}
		}
	}

	/**
	 Method run once the isFound timer is elapsed
	 */
	private void OnTimerF()
	{
		synchronized (_stateLock)
		{
			if (_stateMachine.getCurrentState() == ProcessState.SleepState)
			{
				DebugLog.logTimer(person.getId() + ": OnTimerF");
				if (!IsFound() && IsGuiding())
				{
					_stateMachine.MoveNext(Command.TimerF); // Go to change LEDS
					RunBracelet();
				}
			}
		}
	}

	/**
	 Method run once the LedTimer is elapsed
	 */
	private void OnTimerLed()
	{
		synchronized (_stateLock)
		{
			if (_stateMachine.getCurrentState() == ProcessState.SleepState)
			{
				DebugLog.logTimer(person.getId() + ": OnTimerLed");
				if (IsGuiding())
				{
					_stateMachine.MoveNext(Command.TimerLed); // Go to Update Leds according to database
					RunBracelet();
				}
			}
		}
	}

	/**
	 Method run once the Rebroadcast timer is elapsed

	 */
	private void OnTimerR()
	{
		synchronized (_stateLock)
		{
			if (_stateMachine.getCurrentState() == ProcessState.CommState)
			{
				DebugLog.logTimer(person.getId() + ": Rebroadcast");
				_broker.DoBroadcast(this);
				radio.setState(RadioState.Transmitting);
				battery.DecrementEnergy(radio.getConsumption(), broadcastTime);
				radio.setState(RadioState.Passive);
				//RunBracelet();
			}
		}
	}

	/**
	 Method run once the Update database timer is elapsed
	 */
	private void OnTimerUp()
	{
		_timerRRun = false;
		_timerUpRun = false;
//		_timerUp.cancel(); // Stop the timer from happening when not usefull
//		_timerR.cancel(); // Stop the rebroadcast timer
		synchronized (_stateLock)
		{
			if (_stateMachine.getCurrentState() == ProcessState.CommState)
			{
				DebugLog.logTimer(person.getId() + ": OnTimerUp");
				_stateMachine.MoveNext(Command.TimerUp);
				RunBracelet();
			}
		}
	}


	public final void Subscribe(Broker broker)
	{
		_broker = broker;
		_broker.AddBracelet(this);
	}

	/**
	 Method to handle incomming broadcast
	 @param sender
	 @param position
	 */
	public final void HandleBroadcast(Bracelet sender, Position position)
	{
		// Maybe add check to see if we are in CommState? I guess we don't want to handle broadcast else?
		if ( sender.person.getId() == this.person.getId())
		{
			return;
		}
		synchronized (_dbLock) // Thread safety locking
		{
			DatabaseEntry databaseEnty = new DatabaseEntry();
			databaseEnty.setPosition(position);
			databaseEnty.setTimeStamp(java.time.LocalDateTime.now());
			_dataBase.put(sender.person.getId(), databaseEnty); // add or overwrite
			DebugLog.logTimer(person.getId() + ": HEARD IT FROM " + sender.person.getId());
		}
	}

	public final void StartSearch(Person person)
	{
		synchronized (_stateLock)
		{
			DebugLog.log(this.person.getId() + ": StartSearch for: " + person.getId());
			_lookForPerson = person;
			_stateMachine.MoveNext(Command.StartSearch);
			RunBracelet();
		}
	}

	public final boolean IsFound()
	{
		return found;
	}

	private void setFound(boolean value)
	{
		found = value;
	}

	public boolean IsGuiding()
	{
		return guiding;
	}

	public void setGuiding(boolean value)
	{
		guiding = value;
	}

	public final Position GetPosition()
	{
		return person.getPosition();
	}

	public final double GetRadioRange()
	{
		return radio.getRange_M();
	}

	public void transition(int clock){
		if(clock % _timerCpDelay == 0){
			// Goto Communication phase
			OnTimerCp();
		}
		if(clock % _timerFDelay == 0 && _timerFRun){
			// Goto
			OnTimerF();
		}
		if(clock % _timerRDelay == 0 && _timerRRun){
			// Rebroadcast
			OnTimerR();
		}
		if(clock % _timerUpDelay == 0 && _timerUpRun){
			OnTimerUp();
		}
		if(clock % _timerLedDelay == 0 && _timerLedRun){
			OnTimerLed();
		}
		if(clock % 60000 == 0) {
			Chart.DataPoints.add(new XYChart.Data(clock/60000, battery.getEnergyLeft())); // every "minute" new datapoint
		}
	}

}