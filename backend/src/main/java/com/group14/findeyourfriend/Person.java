package com.group14.findeyourfriend;

public class Person extends Mover {
	private String name;
	private Bracelet bracelet;
	private int id;

	public Person(String name, int id) {
		this.id = id;
		this.name = name;
	}

	public Person() {
		super();
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

	public void setId(int id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "Person" + id + ": " + name + ", " + super.toString();
	}

	public String getName() {
		return name;
	}
}