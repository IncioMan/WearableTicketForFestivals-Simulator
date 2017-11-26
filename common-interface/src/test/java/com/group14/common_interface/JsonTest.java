package com.group14.common_interface;

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
		MessageSimulationPayload message = new MessageSimulationPayload();

		PersonDto personDto = new PersonDto();
		personDto.setPosition(new Position(10f, 11f));

		List<PersonDto> people = new ArrayList<>();
		people.add(personDto);

		message.setPeople(people);

		// Object to JSON in String
		String jsonInString = mapper.writeValueAsString(message);
		System.out.println(jsonInString);
		// JSON from String to Object
		MessageSimulationPayload message2 = mapper.readValue(jsonInString, MessageSimulationPayload.class);
	}

}
