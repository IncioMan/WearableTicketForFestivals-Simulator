package com.group14.findeyourfriend.simulation.events;

import java.util.List;

import com.group14.findeyourfriend.bracelet.Person;

public class ArrivalEvent extends Event {

	private List<Person> peopleComing;

	@Override
	public void process() {
		sim.newGuestsArrived(peopleComing);
	}

	public void setPeopleComing(List<Person> peopleComing) {
		this.peopleComing = peopleComing;
	}

	public List<Person> getPeopleComing() {
		return peopleComing;
	}
}
