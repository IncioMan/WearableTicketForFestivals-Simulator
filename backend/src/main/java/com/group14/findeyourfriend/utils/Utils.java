package com.group14.findeyourfriend.utils;

import com.group14.common_interface.Position;

public class Utils {

	public static boolean isInReachable(Position startPosition, Position targetPosition, Double radius) {
		return startPosition.DistanceTo(targetPosition) <= radius;
	}

}
