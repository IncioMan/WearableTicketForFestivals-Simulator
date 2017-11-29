package com.group14.findeyourfriend.statemachine;

import java.util.HashMap;

public class SRStateMachine extends StateMachineProcess{

    public SRStateMachine(){
        setCurrentState(ProcessState.SleepState);
        _transitions = new HashMap<StateTransition, ProcessState>();
        _transitions.put(new StateTransition(ProcessState.SleepState, Command.TimerCp), ProcessState.CommState);
        _transitions.put(new StateTransition(ProcessState.SleepState, Command.TimerLed), ProcessState.LedState);
        _transitions.put(new StateTransition(ProcessState.SleepState, Command.TimerF), ProcessState.LedState);
        _transitions.put(new StateTransition(ProcessState.LedState, Command.Sleep), ProcessState.SleepState);

        _transitions.put(new StateTransition(ProcessState.InterpretState, Command.FriendNotFound), ProcessState.SleepState);
        _transitions.put(new StateTransition(ProcessState.InterpretState, Command.FriendFound), ProcessState.LedState);

        _transitions.put(new StateTransition(ProcessState.SLookupState, Command.FriendFound), ProcessState.LedState);
        _transitions.put(new StateTransition(ProcessState.SLookupState, Command.FriendNotFound), ProcessState.RequestState);
        _transitions.put(new StateTransition(ProcessState.RequestState, Command.TimerIP), ProcessState.InterpretState);

        _transitions.put(new StateTransition(ProcessState.CommState, Command.TimerUp), ProcessState.UpdateState);
        _transitions.put(new StateTransition(ProcessState.UpdateState, Command.Sleep), ProcessState.SleepState);

        _transitions.put(new StateTransition(ProcessState.SleepState, Command.TimerLP), ProcessState.ListenState);
        _transitions.put(new StateTransition(ProcessState.ListenState, Command.TimerDLP), ProcessState.RLookupState);

        _transitions.put(new StateTransition(ProcessState.RLookupState, Command.SendResponse), ProcessState.ResponseState);
        _transitions.put(new StateTransition(ProcessState.RLookupState, Command.NoResponse), ProcessState.UpdateState);

        _transitions.put(new StateTransition(ProcessState.SleepState, Command.StartSearch), ProcessState.SLookupState);
        _transitions.put(new StateTransition(ProcessState.CommState, Command.StartSearch), ProcessState.SLookupState);
        _transitions.put(new StateTransition(ProcessState.LedState, Command.StartSearch), ProcessState.SLookupState);
        _transitions.put(new StateTransition(ProcessState.UpdateState, Command.StartSearch), ProcessState.SLookupState);
    }

}
