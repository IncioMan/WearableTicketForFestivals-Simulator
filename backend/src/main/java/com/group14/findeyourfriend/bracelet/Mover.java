package com.group14.findeyourfriend.bracelet;

import com.group14.common_interface.Position;
import com.group14.common_interface.Vector2;
import com.group14.findeyourfriend.Constants;
import com.group14.findeyourfriend.debug.DebugLog;

public class Mover {
	private Position _position;
	private Vector2 _speed = new Vector2();
	// private Vector2 _acceleration = new Vector2();

	public final Position getPosition() {
		return _position;
	}

	public final void setPosition(Position value) {
		_position = value;
	}

	public final Vector2 getSpeed() {
		return _speed;
	}

	public void setSpeed(Vector2 value) {
		_speed = value;
	}

	// public final Vector2 getAcceleration() {
	// return _acceleration;
	// }
	//
	// public final void setAcceleration(Vector2 value) {
	// _acceleration = value;
	// }

	public final void MoveTo(Position pos) {
		setPosition(pos);
	}

	public final void UpdatePosition() {
		if ((getPosition().getCoordinates().x + getSpeed().x) > Constants.MAX_WIDTH
				|| (getPosition().getCoordinates().x + getSpeed().x) < Constants.MIN_WIDTH) {
			Turn180DegreesOnXAxis();
		}
		if ((getPosition().getCoordinates().y + getSpeed().y) > Constants.MAX_HEIGHT
				|| (getPosition().getCoordinates().y + getSpeed().y) < Constants.MIN_HEIGHT) {
			Turn180DegreesOnYAxis();
		}

		getPosition().Update(getSpeed());

		// Speed = Vector2.Add(Speed, Acceleration);
	}

	public final void Turn180DegreesOnXAxis() {
		setSpeed(new Vector2(getSpeed().x * -1, getSpeed().y));
	}

	public final void Turn180DegreesOnYAxis() {
		setSpeed(new Vector2(getSpeed().x, getSpeed().y * -1));
	}

	@Override
	public String toString() {
		return "Position: " + getPosition().toString();
	}

	public final void GoTowards(Position otherPosition) {
		DebugLog.log(getPosition() + " GoTowards " + otherPosition);
		Vector2 heading = Vector2.Subtract(otherPosition.getCoordinates(), getPosition().getCoordinates());
		Vector2 direction = Vector2.Normalize(heading);
		setSpeed(Vector2.Multiply(1.5f, direction));
		DebugLog.log("Direction: " + direction);
	}
}