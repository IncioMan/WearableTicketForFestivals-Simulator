package com.group14.findeyourfriend;

public class Clock {
	private static Long clock = 0l;

	public static Long getClock() {
		return clock;
	}

	public static void incrementClock() {
		clock++;
	}

	public static void resetClock() {
		clock = 0l;
	}
}
