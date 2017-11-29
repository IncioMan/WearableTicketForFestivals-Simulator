package com.group14.findeyourfriend.simulation;

public abstract class Event {
	protected int start;
	protected Simulation sim;

	abstract void process();

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
