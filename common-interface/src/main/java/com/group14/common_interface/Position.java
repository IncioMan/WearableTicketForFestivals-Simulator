package com.group14.common_interface;

import java.io.Serializable;

public class Position implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Vector2 coordinates = new Vector2();

	public final Vector2 getCoordinates() {
		return coordinates;
	}

	public Position() {

	}

	public final void setCoordinates(Vector2 value) {
		coordinates = value;
	}

	public Position(float X, float Y) {
		setCoordinates(new Vector2(X, Y));
	}

	public final void Update(Vector2 movement) {
		setCoordinates(Vector2.Add(getCoordinates(), movement));
	}

	@Override
	public String toString() {
		return "X: " + getCoordinates().x + " Y: " + getCoordinates().y;
	}

	public final void GPStoXandY() {

	}

	public final double DistanceTo(Position other) {
		return Vector2.Distance(getCoordinates(), other.getCoordinates());
	}

	protected final boolean equals(Position other) {
		return getCoordinates().equals(other.getCoordinates());
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		Position position = (Position) o;

		return coordinates != null ? coordinates.equals(position.coordinates) : position.coordinates == null;
	}

	@Override
	public int hashCode() {
		return coordinates != null ? coordinates.hashCode() : 0;
	}
}