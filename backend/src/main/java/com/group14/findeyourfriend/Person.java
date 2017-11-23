package com.group14.findeyourfriend;

public class Person extends Mover {
	private final String name;
	private Bracelet bracelet;
	private int id;

	public Person(String name, int id) {
		this.id = id;
		this.name = name;
	}

	public final int getId() {
		return id;
	}

	public final Bracelet getBracelet() {
		return bracelet;
	}

	public final void setBracelet(Bracelet value) {
		bracelet = value;
	}

	@Override
	public String toString() {
		return "Person" + id + ": " + name + ", " + super.toString();
	}

	public String getName() {
		return name;
	}
}