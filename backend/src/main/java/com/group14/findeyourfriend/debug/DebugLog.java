package com.group14.findeyourfriend.debug;

public class DebugLog {

	private static boolean enabled;

	public static void setEnabled(boolean enabled) {
		DebugLog.enabled = enabled;
	}

	public static void log(String s) {
		if (enabled)
			System.out.println(s);
	}
}
