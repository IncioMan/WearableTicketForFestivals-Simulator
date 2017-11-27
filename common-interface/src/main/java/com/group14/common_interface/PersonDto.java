package com.group14.common_interface;

import java.io.Serializable;

public class PersonDto implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Position position;
	private Boolean broadcasting;
	private String id;

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

	public String getId() {
		return id;
	}
}
