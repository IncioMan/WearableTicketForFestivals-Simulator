package com.group14.findeyourfriend.message;

import java.util.HashMap;
import java.util.HashSet;

import com.group14.common_interface.Position;
import com.group14.findeyourfriend.bracelet.Bracelet;
import com.group14.findeyourfriend.bracelet.DatabaseEntry;
import com.group14.findeyourfriend.statemachine.ProcessState;
import com.group14.findeyourfriend.utils.Utils;

//========================================================================
// This conversion was produced by the Free Edition of
// C# to Java Converter courtesy of Tangible Software Solutions.
// Order the Premium Edition at https://www.tangiblesoftwaresolutions.com
//========================================================================

public class Broker {
	private final HashSet<Bracelet> _bracelets = new HashSet<>();

	//public final void DoBroadcast(int senderId, Position broadcastPosition, double range, long messageId, HashMap<Integer, DatabaseEntry> database) {
    public final void DoBroadcast(Bracelet sender, Position broadcastPosition, double range, Message message) {

        for (Bracelet bracelet : _bracelets) {
			if (Utils.isReachable(broadcastPosition, bracelet.getPosition(), range) &&
                    !bracelet.equals(sender) &&
					bracelet.getStateMachine().getCurrentState() != ProcessState.SLEEP_STATE){
				//bracelet.HandleBroadcast(senderId, messageId, database);
                message.setReceiver(bracelet);
                bracelet.HandleBroadcast(message);
			}
		}

	}

	public final void AddBracelet(Bracelet bracelet) {
		_bracelets.add(bracelet);
	}
}