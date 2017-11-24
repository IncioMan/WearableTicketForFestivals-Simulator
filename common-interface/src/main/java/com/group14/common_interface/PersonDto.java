package com.group14.common_interface;

import java.io.Serializable;

public class PersonDto implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Position position;

	public Position getPosition() {
		return position;
	}

	public void setPosition(Position position) {
		this.position = position;
	}
}
