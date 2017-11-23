package com.group14.findeyourfriend;

public enum RadioState {
	Passive, Transmitting, Receiving;

	public static final int SIZE = java.lang.Integer.SIZE;

	public int getValue() {
		return this.ordinal();
	}

	public static RadioState forValue(int value) {
		return values()[value];
	}
}