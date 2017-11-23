package com.group14.findeyourfriend;

import java.util.HashMap;

public class StateMachineProcess {
	private HashMap<StateTransition, ProcessState> _transitions;
	private ProcessState _currentState = ProcessState.values()[0];

	public final ProcessState getCurrentState() {
		synchronized (_thisLock) {
			return _currentState;
		}
	}

	private void setCurrentState(ProcessState value) {
		synchronized (_thisLock) {
			_currentState = value;
		}
	}

	private Command LastCommand = Command.values()[0];

	public final Command getLastCommand() {
		return LastCommand;
	}

	private void setLastCommand(Command value) {
		LastCommand = value;
	}

	private final Object _thisLock = new Object();

	private static class StateTransition {
		private ProcessState _currentState = ProcessState.values()[0];
		private Command _command = Command.values()[0];

		public StateTransition(ProcessState currentState, Command command) {
			_currentState = currentState;
			_command = command;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o)
				return true;
			if (o == null || getClass() != o.getClass())
				return false;

			StateTransition that = (StateTransition) o;

			if (_currentState != that._currentState)
				return false;
			return _command == that._command;
		}

		@Override
		public int hashCode() {
			int result = _currentState != null ? _currentState.hashCode() : 0;
			result = 31 * result + (_command != null ? _command.hashCode() : 0);
			return result;
		}
	}

	/**
	 * Constructor where we set up the statetransitions based on currentstate,
	 * command -> next state
	 */
	public StateMachineProcess() {
		setCurrentState(ProcessState.SleepState);
		_transitions = new HashMap<StateTransition, ProcessState>();
		_transitions.put(new StateTransition(ProcessState.SleepState, Command.TimerCp), ProcessState.CommState);
		_transitions.put(new StateTransition(ProcessState.SleepState, Command.TimerLed), ProcessState.LedState);
		_transitions.put(new StateTransition(ProcessState.SleepState, Command.StartSearch), ProcessState.SearchState);
		_transitions.put(new StateTransition(ProcessState.CommState, Command.StartSearch), ProcessState.SearchState);
		_transitions.put(new StateTransition(ProcessState.LedState, Command.StartSearch), ProcessState.SearchState);
		_transitions.put(new StateTransition(ProcessState.UpdateState, Command.StartSearch), ProcessState.SearchState);
		_transitions.put(new StateTransition(ProcessState.SleepState, Command.TimerF), ProcessState.LedState);
		_transitions.put(new StateTransition(ProcessState.LedState, Command.Next), ProcessState.SleepState);
		_transitions.put(new StateTransition(ProcessState.SearchState, Command.FriendNotFound),
				ProcessState.SleepState);
		_transitions.put(new StateTransition(ProcessState.SearchState, Command.FriendFound), ProcessState.LedState);
		_transitions.put(new StateTransition(ProcessState.CommState, Command.TimerUp), ProcessState.UpdateState);
		_transitions.put(new StateTransition(ProcessState.UpdateState, Command.Next), ProcessState.SleepState);
	}

	public final ProcessState GetNext(Command command) {
		StateTransition transition = new StateTransition(getCurrentState(), command);
		ProcessState nextState = _transitions.get(transition);
		if (nextState != null)
			return nextState;
		else
			throw new RuntimeException("Invalid transition: " + getCurrentState() + " -> " + command);
	}

	public final ProcessState MoveNext(Command command) {
		synchronized (_thisLock) {
			setCurrentState(GetNext(command));
			setLastCommand(command);
			return getCurrentState();
		}

	}
}