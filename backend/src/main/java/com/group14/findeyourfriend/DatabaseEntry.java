package com.group14.findeyourfriend;

//========================================================================

// This conversion was produced by the Free Edition of
// C# to Java Converter courtesy of Tangible Software Solutions.
// Order the Premium Edition at https://www.tangiblesoftwaresolutions.com
//========================================================================
public class DatabaseEntry {
	private Long timeStamp;

	public Long getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(Long timeStamp) {
		this.timeStamp = timeStamp;
	}

	private com.group14.common_interface.Position Position;

	public final com.group14.common_interface.Position getPosition() {
		return Position;
	}

	public final void setPosition(com.group14.common_interface.Position value) {
		Position = value;
	}
}