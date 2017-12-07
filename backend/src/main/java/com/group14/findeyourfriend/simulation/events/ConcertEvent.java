package com.group14.findeyourfriend.simulation.events;

import java.util.HashSet;
import java.util.Set;

import com.group14.common_interface.Position;

public class ConcertEvent extends Event {

	private Set<String> guestsToConcert;
	private Position concertLocation;
	private Long endTime;

	@Override
	public void process() {
		sim.getGuests().forEach(g -> {
			if (guestsToConcert.contains(g.getId() + "")) {
				g.getBracelet().takeMeToEvent(this);
			}
		});
	}

	public Position getConcertLocation() {
		return concertLocation;
	}

	public void setConcertLocation(Position concertLocation) {
		this.concertLocation = concertLocation;
	}

	public void setGuestsToConcert(Set<String> guestsToConcert) {
		this.guestsToConcert = guestsToConcert;
	}

	public void setGuestsToConcert(String[] guestsToConcert) {
		this.guestsToConcert = new HashSet<>();
		for (String id : guestsToConcert) {
			this.guestsToConcert.add(id);
		}
	}

	public Set<String> getGuestsToConcert() {
		return guestsToConcert;
	}

	public Long getEndTime() {
		return endTime;
	}

	public void setEndTime(Long endTime) {
		this.endTime = endTime;
	}

	@Override
	public String getDescription() {
		StringBuilder builder = new StringBuilder();
		builder.append("Concert Event - Location: " + getConcertLocation().getCoordinates().getX() + ","
				+ getConcertLocation().getCoordinates().getY() + " - ");
		builder.append("People attending: ");
		guestsToConcert.forEach(p -> {
			builder.append(p + ", ");
		});

		return builder.toString();
	}

}
