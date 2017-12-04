package com.group14.findeyourfriend.message;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.function.Consumer;

import org.apache.commons.lang3.tuple.Pair;

import com.group14.common_interface.Position;
import com.group14.findeyourfriend.bracelet.Bracelet;
import com.group14.findeyourfriend.bracelet.BraceletEvent;
import com.group14.findeyourfriend.bracelet.Person;
import com.group14.findeyourfriend.statemachine.ProcessState;
import com.group14.findeyourfriend.utils.Utils;

//========================================================================
// This conversion was produced by the Free Edition of
// C# to Java Converter courtesy of Tangible Software Solutions.
// Order the Premium Edition at https://www.tangiblesoftwaresolutions.com
//========================================================================

public class Broker {
	private final HashSet<Bracelet> _bracelets = new HashSet<>();
	private List<Consumer<Pair<Person, BraceletEvent>>> braceletsConsumers;

	public Broker() {
		braceletsConsumers = new ArrayList<>();
	}

	// public final void DoBroadcast(int senderId, Position broadcastPosition,
	// double range, long messageId, HashMap<Integer, DatabaseEntry> database) {
	public final void DoBroadcast(Bracelet sender, Position broadcastPosition, double range, Message message) {
		for (Bracelet bracelet : _bracelets) {
			if (Utils.isReachable(broadcastPosition, bracelet.getPosition(), range) && !bracelet.equals(sender)
					&& bracelet.getStateMachine().getCurrentState() != ProcessState.SLEEP_STATE) {
				// bracelet.HandleBroadcast(senderId, messageId, database);
				if (!message.isSeen(bracelet.getPerson().getId())) {
					message.setReceiver(bracelet);
					bracelet.HandleBroadcast(message);
					message.setSeenBracelet(bracelet.getPerson().getId());
				}
			}
		}
	}

	public final void AddBracelet(Bracelet bracelet) {
		_bracelets.add(bracelet);
	}

	public void notifyEvent(Person person, BraceletEvent event) {
		braceletsConsumers.forEach(c -> {
			c.accept(Pair.of(person, event));
		});
	}

	public void addEventConsumer(Consumer<Pair<Person, BraceletEvent>> consumer) {
		braceletsConsumers.add(consumer);
	}
}