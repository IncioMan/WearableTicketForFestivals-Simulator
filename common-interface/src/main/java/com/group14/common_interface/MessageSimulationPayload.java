package com.group14.common_interface;

import java.io.Serializable;
import java.util.List;

public class MessageSimulationPayload implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6954765546191136459L;
	private List<PersonDto> people;

	public List<PersonDto> getPeople() {
		return people;
	}

	public void setPeople(List<PersonDto> collection) {
		this.people = collection;
	}
}
