package com.group14.findeyourfriend.simulation.events;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import com.group14.common_interface.Position;
import com.group14.findeyourfriend.bracelet.Person;
import com.group14.findeyourfriend.debug.DebugLog;

public class EventParser {

	private static int arrivedGuestsCounter = 0;

	public static Queue<Event> parse(String s) throws IOException {

		Queue<Event> simulationEvents = new ArrayDeque<>();

		String thisLine;

		try (BufferedReader in = new BufferedReader(new FileReader(s))) {
			while ((thisLine = in.readLine()) != null) {
				if (!thisLine.startsWith("#")) {
					Event e = interpretLine(thisLine);
					simulationEvents.add(e);
				}
			}
		}

		DebugLog.log("List of events correctly parsed.");
		return simulationEvents;
	}

	public static Queue<Event> parse(InputStream stream) throws IOException {

		Queue<Event> simulationEvents = new ArrayDeque<>();

		String thisLine;

		BufferedReader in = new BufferedReader(new InputStreamReader(stream));

		while ((thisLine = in.readLine()) != null) {
			if (!thisLine.startsWith("#")) {
				Event e = interpretLine(thisLine);
				simulationEvents.add(e);
			}
		}

		DebugLog.log("List of events correctly parsed.");
		return simulationEvents;
	}

	private static Event interpretLine(String line) throws IOException {
		String[] words = line.split(" ");

		switch (words[0]) {
		case "A":
			// example of ARRIVAL: A 1 luca
			ArrivalEvent aEvent = new ArrivalEvent();
			aEvent.setStart(words[1]);

			List<Person> friends = new ArrayList<>();

			for (String name : words[2].split(",")) {
				arrivedGuestsCounter++;
				Person friend = new Person(name, arrivedGuestsCounter);
				friends.add(friend);
			}

			aEvent.setPeopleComing(friends);

			return aEvent;

		case "S":
			// example of SEARCH: S 5 luca,alex
			SearchEvent sEvent = new SearchEvent();
			sEvent = new SearchEvent();
			sEvent.setStart(words[1]);

			sEvent.setHunterName(words[2].split(",")[0]);
			sEvent.setPreyName(words[2].split(",")[1]);
			// TODO Add set ID of hunter/prey
			return sEvent;

		case "C":
			// example of CONCERT: C 1500 10,20 1,2,3,4
			// (10,20) is the location of the event
			// 1,2,3,4 are people that will go to that concert
			ConcertEvent concertEvent = new ConcertEvent();
			Position concertPosition = new Position(Float.parseFloat(words[2].split(",")[0]),
					Float.parseFloat(words[2].split(",")[1]));
			concertEvent.setGuestsToConcert(words[3].split(","));
			concertEvent.setConcertLocation(concertPosition);
			// TODO Add set ID of hunter/prey
			return concertEvent;
		default:
			throw new IOException("Incorrect event in event list");
		}
	}
}
