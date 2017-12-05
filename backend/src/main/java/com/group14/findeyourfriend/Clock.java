package com.group14.findeyourfriend;

import com.group14.findeyourfriend.bracelet.DatabaseEntry;

public class Clock {
	private static Long clock = 0L;

	// TODO implement something to make simulation go slower?

	private static Long maxAge = 60000L; // TODO implement parameter passing?

	public static Long getClock() {
		return clock;
	}

	public static void incrementClock() {
		clock++;
	}

	public static void resetClock() {
		clock = 0l;
	}

	public static boolean isRecentEnough(DatabaseEntry databaseEntry) {
		if(clock - databaseEntry.getTimeStamp() <= maxAge)
		    return true;
		return false;
	}
}
