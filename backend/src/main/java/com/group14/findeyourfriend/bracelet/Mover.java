package com.group14.findeyourfriend.bracelet;

import com.group14.common_interface.Position;
import com.group14.common_interface.Vector2;
import com.group14.findeyourfriend.Constants;
import com.group14.findeyourfriend.debug.DebugLog;

public class Mover {
	private Position _position;
	private Vector2 _speed = new Vector2();
	private Vector2 _acceleration = new Vector2();

	public final Position getPosition() {
		return _position;
	}

	public final void setPosition(Position value) {
		_position = value;
	}

	public final Vector2 getSpeed() {
		return _speed;
	}

	private void setSpeed(Vector2 value) {
		_speed = value;
	}

	public final Vector2 getAcceleration() {
		return _acceleration;
	}

	public final void setAcceleration(Vector2 value) {
		_acceleration = value;
	}

	public final void MoveTo(Position pos) {
		setPosition(pos);
	}

	public final void UpdatePosition() {
		if ((getPosition().getCoordinates().x + getAcceleration().x) > Constants.MAX_WIDTH
				|| (getPosition().getCoordinates().x + getAcceleration().x) < Constants.MIN_WIDTH) {
			Turn180DegreesOnXAxis();
		}
		if ((getPosition().getCoordinates().y + getAcceleration().y) > Constants.MAX_HEIGHT
				|| (getPosition().getCoordinates().y + getAcceleration().y) < Constants.MIN_HEIGHT) {
			Turn180DegreesOnYAxis();
		}

		getPosition().Update(getAcceleration());

		// Speed = Vector2.Add(Speed, Acceleration);
	}

	public final void Turn180DegreesOnXAxis() {
		setAcceleration(new Vector2(getAcceleration().x * -1, getAcceleration().y));
	}

	public final void Turn180DegreesOnYAxis() {
		setAcceleration(new Vector2(getAcceleration().x, getAcceleration().y * -1));
	}

	@Override
	public String toString() {
		return "Position: " + getPosition().toString();
	}

	public final void GoTowards(Position otherPosition) {
		DebugLog.log(getPosition() + " GoTowards " + otherPosition);
		Vector2 heading = Vector2.Subtract(otherPosition.getCoordinates(), getPosition().getCoordinates());
		Vector2 direction = Vector2.Normalize(heading);
		setAcceleration(Vector2.Multiply(0.5f, direction));
		DebugLog.log("Direction: " + direction);
	}
}