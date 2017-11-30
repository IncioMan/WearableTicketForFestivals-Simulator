package com.group14.findeyourfriend.bracelet;

import java.util.HashSet;

import com.group14.common_interface.Position;
import com.group14.findeyourfriend.utils.Utils;

//========================================================================
// This conversion was produced by the Free Edition of
// C# to Java Converter courtesy of Tangible Software Solutions.
// Order the Premium Edition at https://www.tangiblesoftwaresolutions.com
//========================================================================

public class Broker {
	private final HashSet<Bracelet> _bracelets = new HashSet<Bracelet>();

	public final void DoBroadcast(Bracelet b) {
		Position broadcastPosition = b.getPosition();
		double range = b.getRadioRange();
		for (Bracelet bracelet : _bracelets) {
			if (Utils.isInReachable(broadcastPosition, bracelet.getPosition(), range)) {
				bracelet.HandleBroadcast(b, broadcastPosition);
			}
		}

	}

	public final void AddBracelet(Bracelet bracelet) {
		_bracelets.add(bracelet);
	}
}