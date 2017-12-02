package com.group14.findeyourfriend.simulation.events;

import java.util.HashSet;
import java.util.Set;

import com.group14.common_interface.Position;

public class ConcertEvent extends Event {

	private Set<String> guestsToConcert;
	private Position concertLocation;

	@Override
	public void process() {
		sim.getGuests().forEach(g -> {
			if (guestsToConcert.contains(g.getId() + "")) {
				g.MoveTo(concertLocation);
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

}
