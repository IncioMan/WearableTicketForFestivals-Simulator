package com.group14.findeyourfriend;

import com.group14.common_interface.PersonDto;
import com.group14.findeyourfriend.bracelet.Person;

public class DtoConverter {

	public PersonDto convert(Person model) {
		if (model == null) {
			return null;
		}

		PersonDto dto = new PersonDto();
		dto.setId(model.getName());
		dto.setPosition(model.getPosition());
		return dto;
	}
}
