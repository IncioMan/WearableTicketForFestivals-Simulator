package com.group14.findeyourfriend.bracelet;

import java.util.HashMap;
import java.util.HashSet;

import com.group14.common_interface.Position;
import com.group14.findeyourfriend.statemachine.ProcessState;
import com.group14.findeyourfriend.utils.Utils;

//========================================================================
// This conversion was produced by the Free Edition of
// C# to Java Converter courtesy of Tangible Software Solutions.
// Order the Premium Edition at https://www.tangiblesoftwaresolutions.com
//========================================================================

public class Broker {
	private final HashSet<Bracelet> _bracelets = new HashSet<>();

	public final void DoBroadcast(int senderId, Position broadcastPosition, double range, long messageId, HashMap<Integer, DatabaseEntry> database) {
		for (Bracelet bracelet : _bracelets) {
			if (Utils.isReachable(broadcastPosition, bracelet.getPosition(), range) && bracelet.getStateMachine().getCurrentState() == ProcessState.COMMUNICATION_STATE) {
				bracelet.HandleBroadcast(senderId, messageId, database);
			}
		}

	}

	public final void AddBracelet(Bracelet bracelet) {
		_bracelets.add(bracelet);
	}
}