package com.group14.findeyourfriend.debug;
public class DebugLog {

	private static boolean enabled;
	private static boolean enableTimerPrint;

	public static void setEnabled(boolean enabled) {
		DebugLog.enabled = enabled;
	}
	public static void setEnabledTimers(boolean enabled) {
		DebugLog.enableTimerPrint = enabled;
	}

	public static void log(String s) {
		if (enabled)
			System.out.println(s);
	}
	public static void logTimer(String s) {
		if (enableTimerPrint)
			System.out.println(s);
	}
}
