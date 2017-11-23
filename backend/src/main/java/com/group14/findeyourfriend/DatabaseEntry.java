package com.group14.findeyourfriend;

//========================================================================

// This conversion was produced by the Free Edition of
// C# to Java Converter courtesy of Tangible Software Solutions.
// Order the Premium Edition at https://www.tangiblesoftwaresolutions.com
//========================================================================

public class DatabaseEntry {
	private java.time.LocalDateTime TimeStamp = java.time.LocalDateTime.MIN;

	public final java.time.LocalDateTime getTimeStamp() {
		return TimeStamp;
	}

	public final void setTimeStamp(java.time.LocalDateTime value) {
		TimeStamp = value;
	}

	private Position Position;

	public final Position getPosition() {
		return Position;
	}

	public final void setPosition(Position value) {
		Position = value;
	}
}