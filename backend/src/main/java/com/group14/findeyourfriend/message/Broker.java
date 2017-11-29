package com.group14.findeyourfriend.message;

import java.util.HashSet;

import com.group14.common_interface.Position;
import com.group14.findeyourfriend.bracelet.Bracelet;

//========================================================================
// This conversion was produced by the Free Edition of
// C# to Java Converter courtesy of Tangible Software Solutions.
// Order the Premium Edition at https://www.tangiblesoftwaresolutions.com
//========================================================================

public class Broker {
	private final HashSet<Bracelet> _bracelets = new HashSet<Bracelet>();

	public final void DoBroadcast(Bracelet b) {
		Position broadcastPosition = b.GetPosition();
		double range = b.GetRadioRange();
		for (Bracelet bracelet : _bracelets) {
			if (broadcastPosition.DistanceTo(bracelet.GetPosition()) <= range) {
				bracelet.HandleBroadcast(b, broadcastPosition);
			}
		}

	}

	public final void AddBracelet(Bracelet bracelet) {
		_bracelets.add(bracelet);
	}
}