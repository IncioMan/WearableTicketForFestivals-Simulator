package com.group14.findeyourfriend.statemachine;

//========================================================================
// This conversion was produced by the Free Edition of
// C# to Java Converter courtesy of Tangible Software Solutions.
// Order the Premium Edition at https://www.tangiblesoftwaresolutions.com
//========================================================================

public enum ProcessState {
	SLEEP_STATE, COMMUNICATION_STATE, LED_STATE, S_LOOKUP_STATE, UPDATE_STATE,
	// SR states below
	INTERPRET_STATE, REQUEST_STATE, R_LOOKUP_STATE, LISTEN_STATE, RESPONSE_STATE;

	public static final int SIZE = java.lang.Integer.SIZE;

	public int getValue() {
		return this.ordinal();
	}

	public static ProcessState forValue(int value) {
		return values()[value];
	}
}