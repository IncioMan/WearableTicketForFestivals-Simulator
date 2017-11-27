package com.group14.findeyourfriend;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;

public class JsonTest {
	@Test
	public void testConversion() throws JsonGenerationException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.enableDefaultTyping();

		ArrivalEvent event = new ArrivalEvent();

		Person person = new Person("ciao", 1);

		List<Person> people = new ArrayList<>();
		people.add(person);

		event.setPeopleComing(people);

		// Object to JSON in String
		String jsonInString = mapper.writeValueAsString(event);
		System.out.println(jsonInString);
		// JSON from String to Object
		ArrivalEvent event2 = mapper.readValue(jsonInString, ArrivalEvent.class);

		SearchEvent searchEvent = new SearchEvent();
		searchEvent.setHunterId(1);
		searchEvent.setPreyId(1);
		searchEvent.setHunterName("ciao");

		// Object to JSON in String
		jsonInString = mapper.writeValueAsString(searchEvent);
		System.out.println(jsonInString);
		// JSON from String to Object
		SearchEvent searchEvent2 = mapper.readValue(jsonInString, SearchEvent.class);

		Events events = new Events();
		events.addEvent(searchEvent);
		events.addEvent(event);

		// Object to JSON in String
		jsonInString = mapper.writeValueAsString(events.getEvents());
		System.out.println(jsonInString);
		// JSON from String to Object
		// List<Events> events2 = mapper.readValue(jsonInString,
		// mapper.getTypeFactory().constructCollectionType(List.class, Event.class));
	}

	public class Events {
		List<Event> events = new ArrayList<>();

		public Events() {
			// TODO Auto-generated constructor stub
		}

		public List<Event> getEvents() {
			return events;
		}

		public void setEvents(List<Event> events) {
			this.events = events;
		}

		public void addEvent(Event event) {
			events.add(event);
		}

	}

}
