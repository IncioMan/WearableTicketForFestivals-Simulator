package com.group14.findeyourfriend;

import java.util.HashSet;

//========================================================================
// This conversion was produced by the Free Edition of
// C# to Java Converter courtesy of Tangible Software Solutions.
// Order the Premium Edition at https://www.tangiblesoftwaresolutions.com
//========================================================================

public class Broker {
	// public event EventHandler<PositionEventArgs> BroadCast;
	private final HashSet<Bracelet> _bracelets = new HashSet<Bracelet>();

	public final void DoBroadcast(Bracelet b) {
		Position broadcastPosition = b.GetPosition();
		double range = b.GetRadioRange();
		for (Bracelet bracelet : _bracelets) {
			if (broadcastPosition.DistanceTo(bracelet.GetPosition()) <= range) {
				bracelet.HandleBroadcast(b, broadcastPosition);
			}
		}
		// BroadCast?.Invoke(bracelet, new PositionEventArgs(position));

	}

	public final void AddBracelet(Bracelet bracelet) {
		_bracelets.add(bracelet);
	}

	public static class PositionEventArgs {
		private Position Position;

		public final Position getPosition() {
			return Position;
		}

		public final void setPosition(Position value) {
			Position = value;
		}

		public PositionEventArgs(Position position) {
			setPosition(position);
		}
	}
}