package com.group14.findeyourfriend.simulation.events;

import com.group14.findeyourfriend.simulation.Simulation;

public abstract class Event {
	protected int start;
	protected Simulation sim;

	public abstract void process();

	public abstract String getDescription();

	public void setSimulation(Simulation simulation) {
		this.sim = simulation;
	}

	@Override
	public String toString() {
		return super.toString();
	}

	public void setStart(String start) {
		this.start = Integer.parseInt(start);
	}

	public int getStart() {
		return start;
	}
}
