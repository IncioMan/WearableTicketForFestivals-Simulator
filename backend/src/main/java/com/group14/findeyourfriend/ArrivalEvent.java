package com.group14.findeyourfriend;

import java.util.List;

public class ArrivalEvent extends Event {
	//
	private List<Person> peopleComing;

	@Override
	void process() {
		sim.newGuestsArrived(peopleComing);
	}

	public void setPeopleComing(List<Person> comers) {
		this.peopleComing = comers;
	}
}
