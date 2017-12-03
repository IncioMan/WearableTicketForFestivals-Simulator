package com.group14.findeyourfriend.statemachine;

public enum Command {
	TimerF, TimerUp, TimerCp, TimerLed, StartSearch, FriendFound, FriendNotFound, Sleep,
	// SR commands below
	TimerIP, TimerLP, TimerDLP, TimerDRP, SendResponse, NoResponse, GoToEvent, MoveToEvent, TimerEvent;

	public static final int SIZE = java.lang.Integer.SIZE;

	public int getValue() {
		return this.ordinal();
	}

	public static Command forValue(int value) {
		return values()[value];
	}
}