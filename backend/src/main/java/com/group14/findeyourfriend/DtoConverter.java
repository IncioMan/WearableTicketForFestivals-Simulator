package com.group14.findeyourfriend;

import com.group14.common_interface.PersonDto;

public class DtoConverter {

	public PersonDto convert(Person model) {
		if (model == null) {
			return null;
		}

		PersonDto dto = new PersonDto();
		dto.setPosition(model.getPosition());
		return dto;
	}
}
