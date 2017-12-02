package com.group14.findeyourfriend.statemachine;

import java.util.HashMap;

public class SRStateMachine extends StateMachineProcess{

    public SRStateMachine(){
        setCurrentState(ProcessState.SLEEP_STATE);
        _transitions = new HashMap<StateTransition, ProcessState>();
        _transitions.put(new StateTransition(ProcessState.SLEEP_STATE, Command.TimerCp), ProcessState.COMMUNICATION_STATE);
        _transitions.put(new StateTransition(ProcessState.SLEEP_STATE, Command.TimerLed), ProcessState.LED_STATE);
        _transitions.put(new StateTransition(ProcessState.SLEEP_STATE, Command.TimerF), ProcessState.LED_STATE);
        _transitions.put(new StateTransition(ProcessState.LED_STATE, Command.Sleep), ProcessState.SLEEP_STATE);

        _transitions.put(new StateTransition(ProcessState.INTERPRET_STATE, Command.FriendNotFound), ProcessState.SLEEP_STATE);
        _transitions.put(new StateTransition(ProcessState.INTERPRET_STATE, Command.FriendFound), ProcessState.LED_STATE);

        _transitions.put(new StateTransition(ProcessState.S_LOOKUP_STATE, Command.FriendFound), ProcessState.LED_STATE);
        _transitions.put(new StateTransition(ProcessState.S_LOOKUP_STATE, Command.FriendNotFound), ProcessState.REQUEST_STATE);
        _transitions.put(new StateTransition(ProcessState.REQUEST_STATE, Command.TimerIP), ProcessState.INTERPRET_STATE);

        _transitions.put(new StateTransition(ProcessState.COMMUNICATION_STATE, Command.TimerUp), ProcessState.UPDATE_STATE);
        _transitions.put(new StateTransition(ProcessState.UPDATE_STATE, Command.Sleep), ProcessState.SLEEP_STATE);

        _transitions.put(new StateTransition(ProcessState.SLEEP_STATE, Command.TimerLP), ProcessState.LISTEN_STATE);
        _transitions.put(new StateTransition(ProcessState.LISTEN_STATE, Command.TimerDLP), ProcessState.R_LOOKUP_STATE);

        _transitions.put(new StateTransition(ProcessState.R_LOOKUP_STATE, Command.SendResponse), ProcessState.RESPONSE_STATE);
        _transitions.put(new StateTransition(ProcessState.R_LOOKUP_STATE, Command.NoResponse), ProcessState.UPDATE_STATE);

        _transitions.put(new StateTransition(ProcessState.RESPONSE_STATE, Command.TimerDRP), ProcessState.UPDATE_STATE);

        _transitions.put(new StateTransition(ProcessState.SLEEP_STATE, Command.StartSearch), ProcessState.S_LOOKUP_STATE);
        _transitions.put(new StateTransition(ProcessState.COMMUNICATION_STATE, Command.StartSearch), ProcessState.S_LOOKUP_STATE);
        _transitions.put(new StateTransition(ProcessState.LED_STATE, Command.StartSearch), ProcessState.S_LOOKUP_STATE);
        _transitions.put(new StateTransition(ProcessState.UPDATE_STATE, Command.StartSearch), ProcessState.S_LOOKUP_STATE);
        _transitions.put(new StateTransition(ProcessState.LISTEN_STATE, Command.StartSearch), ProcessState.S_LOOKUP_STATE);
        _transitions.put(new StateTransition(ProcessState.R_LOOKUP_STATE, Command.StartSearch), ProcessState.S_LOOKUP_STATE);
        _transitions.put(new StateTransition(ProcessState.RESPONSE_STATE, Command.StartSearch), ProcessState.S_LOOKUP_STATE);
        _transitions.put(new StateTransition(ProcessState.REQUEST_STATE, Command.StartSearch), ProcessState.S_LOOKUP_STATE);
        _transitions.put(new StateTransition(ProcessState.INTERPRET_STATE, Command.StartSearch), ProcessState.S_LOOKUP_STATE);
    }

}
