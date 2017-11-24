package com.group14.findeyourfriend;
import com.group14.findeyourfriend.debug.DebugLog;

public class SearchEvent extends Event {

	int end;
	private String hunterName;
	private int hunterId;
	private String preyName;
	private int preyId;

	@Override
	void process() {
		for (Person h : sim.getGuests()) {
			if (h.getName().equals(hunterName)){
				for(Person p: sim.getGuests()){
					if (p.getName().equals(preyName)){
						h.getBracelet().StartSearch(p);
						DebugLog.log("SearchTimer: " + h.toString() + " searching for " + p.toString());
					}
				}
			}
		}// Change to hashmap and get by id? name is not unique?

		// TODO implement time elapsed?

//        TimerTask searchTask = new TimerTask() {
//            @Override
//            public void run() {
//                Person h = sim.getPersonById(hunterId);
//                Person p = sim.getPersonById(preyId);
//                h.getBracelet().StartSearch(p);
//                DebugLog.log("SearchTimer: " + h.toString() + " searching for " + p.toString());
//            }
//        };
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
