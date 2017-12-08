package com.group14.common_interface;

import java.io.Serializable;

public class PersonDto implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Position position;
	private Boolean broadcasting;
	private Boolean communicating;
	private String id;
	private Number range;

	public PersonDto() {
		communicating = false;
		broadcasting = false;
	}

	public Position getPosition() {
		return position;
	}

	public void setPosition(Position position) {
		this.position = position;
	}

	public void setBroadcasting(Boolean broadcasting) {
		this.broadcasting = broadcasting;
	}

	public Boolean getBroadcasting() {
		return broadcasting;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Boolean getCommunicating() {
		return communicating;
	}

	public void setCommunicating(Boolean communicating) {
		this.communicating = communicating;
	}

	public String getId() {
		return id;
	}

	public Number getRange() {
		return range;
	}

	public void setRange(Number range) {
		this.range = range;
	}

}
