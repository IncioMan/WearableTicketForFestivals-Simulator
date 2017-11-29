package com.group14.findeyourfriend.statemachine;

//========================================================================
// This conversion was produced by the Free Edition of
// C# to Java Converter courtesy of Tangible Software Solutions.
// Order the Premium Edition at https://www.tangiblesoftwaresolutions.com
//========================================================================

public enum ProcessState {
	SleepState, CommState, LedState, SLookupState, UpdateState,
	// SR states below
	InterpretState, RequestState, RLookupState, ListenState, ResponseState;

	public static final int SIZE = java.lang.Integer.SIZE;

	public int getValue() {
		return this.ordinal();
	}

	public static ProcessState forValue(int value) {
		return values()[value];
	}
}