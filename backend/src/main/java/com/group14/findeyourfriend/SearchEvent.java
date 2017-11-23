package com.group14.findeyourfriend;

import java.util.Timer;
import java.util.TimerTask;

import com.group14.findeyourfriend.debug.DebugLog;

public class SearchEvent extends Event {

	// inherits start
	// inherits simulation
	int end;
	private String hunterName;
	private int hunterId;
	private String preyName;
	private int preyId;
	private static Timer timer;

	@Override
	void process() {
		timer = new Timer(true);

		TimerTask searchTask = new TimerTask() {
			@Override
			public void run() {
				for (Person h : sim.getGuests())// Change to hashmap and get by id? name is not unique?
					if (h.getName().equals(hunterName))
						for (Person p : sim.getGuests())
							if (p.getName().equals(preyName)) {
								h.getBracelet().StartSearch(p);
								DebugLog.log("SearchTimer: " + h.toString() + " searching for " + p.toString());
							}

				// TODO implement time elapsed?
			}
		};
		// TimerTask searchTask = new TimerTask() {
		// @Override
		// public void run() {
		// Person h = sim.getPersonById(hunterId);
		// Person p = sim.getPersonById(preyId);
		// h.getBracelet().StartSearch(p);
		// DebugLog.log("SearchTimer: " + h.toString() + " searching for " +
		// p.toString());
		// }
		// };
		timer.schedule(searchTask, 0);// Start the event
	}

	public void setHunterName(String hName) {
		this.hunterName = hName;
	}

	public void setPreyName(String pName) {
		this.preyName = pName;
	}

	public void setHunterId(int hunterId) {
		this.hunterId = hunterId;
	}

	public void setPreyId(int preyId) {
		this.preyId = preyId;
	}
}
