package com.group14.findeyourfriend.bracelet;
import com.group14.common_interface.Position;
//========================================================================



//========================================================================
public class DatabaseEntry {
	public DatabaseEntry(){
		this.timeStamp = 0L;
		this.Position = null;
	}

	private Long timeStamp;

	public Long getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(Long timeStamp) {
		this.timeStamp = timeStamp;
	}

	private Position Position;

	public final Position getPosition() {
		return Position;
	}

	public final void setPosition(Position value) {
		Position = value;
	}
}