package com.group14.common_interface;

import java.io.Serializable;

public class Vector2 implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static Vector2 Zero = new Vector2();

	// Members
	public float x;
	public float y;

	// Constructors
	public Vector2() {
		this.x = 0.0f;
		this.y = 0.0f;
	}

	public Vector2(float x, float y) {
		this.x = x;
		this.y = y;
	}

	// Compare two vectors
	public boolean equals(Vector2 other) {
		return (this.x == other.x && this.y == other.y);
	}

	public static double Distance(Vector2 a, Vector2 b) {
		float v0 = b.x - a.x;
		float v1 = b.y - a.y;
		return Math.sqrt(v0 * v0 + v1 * v1);
	}

	public static Vector2 Add(Vector2 a, Vector2 b) {
		float x = a.x + b.x;
		float y = a.y + b.y;
		return new Vector2(x, y);

	}

	public static Vector2 Normalize(Vector2 a) {
		// sets length to 1
		//
		double length = Math.sqrt(a.x * a.x + a.y * a.y);
		float x = a.x;
		float y = a.y;
		if (length != 0.0) {
			float s = 1.0f / (float) length;
			x = x * s;
			y = y * s;
		}
		return new Vector2(x, y);
	}

	public static Vector2 Multiply(float s, Vector2 a) {
		// scales length of a by s
		//
		double length = Math.sqrt(a.x * a.x + a.y * a.y);
		float x = a.x;
		float y = a.y;
		if (length != 0.0) {
			x = x * s;
			y = y * s;
		}
		return new Vector2(x, y);
	}

	public static Vector2 Subtract(Vector2 a, Vector2 b) {
		float x = a.x - b.x;
		float y = a.y - b.y;
		return new Vector2(x, y);
	}

	@Override
	public String toString() {
		return "Vector[" + x + ", " + y + "]";
	}

}
