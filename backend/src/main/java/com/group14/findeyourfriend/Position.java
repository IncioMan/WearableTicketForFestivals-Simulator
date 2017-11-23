package com.group14.findeyourfriend;

import com.group14.findeyourfriend.math.Vector2;

public class Position {
	private Vector2 Coordinates = new Vector2();

	public final Vector2 getCoordinates() {
		return Coordinates;
	}

	public final void setCoordinates(Vector2 value) {
		Coordinates = value;
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

		return Coordinates != null ? Coordinates.equals(position.Coordinates) : position.Coordinates == null;
	}

	@Override
	public int hashCode() {
		return Coordinates != null ? Coordinates.hashCode() : 0;
	}
}