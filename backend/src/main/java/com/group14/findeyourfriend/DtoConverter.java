package com.group14.findeyourfriend;

import com.group14.common_interface.PersonDto;
import com.group14.common_interface.PersonSearchRequestDto;
import com.group14.findeyourfriend.bracelet.Bracelet;
import com.group14.findeyourfriend.bracelet.Person;
import com.group14.findeyourfriend.bracelet.SRBracelet;

public class DtoConverter {

	public PersonDto convert(Person model) {
		if (model == null) {
			return null;
		}

		if (model.getBracelet() instanceof SRBracelet) {
			PersonSearchRequestDto dto = new PersonSearchRequestDto();
			dto.setId(model.getName());
			dto.setPosition(model.getPosition());
			dto.setRange(model.getBracelet().getRadioRange());
			switch (model.getBracelet().getStateMachine().getCurrentState()) {
			// TODO
			case COMMUNICATION_STATE:
				dto.setCommunicating(true);
				break;
			case S_LOOKUP_STATE:
				dto.setCommunicating(true);
				break;
			case REQUEST_STATE:
				dto.setRequesting(true);
				break;
			case LISTEN_STATE:
				dto.setListening(true);
				break;
			default:
				break;
			}

			return dto;
		}

		if (model.getBracelet() instanceof Bracelet) {
			PersonDto dto = new PersonDto();
			dto.setId(model.getName());
			dto.setPosition(model.getPosition());
			dto.setRange(model.getBracelet().getRadioRange());
			switch (model.getBracelet().getStateMachine().getCurrentState()) {
			// TODO
			case COMMUNICATION_STATE:
				dto.setCommunicating(true);
				break;
			case S_LOOKUP_STATE:
				dto.setCommunicating(true);
				break;
			default:
				break;
			}

			return dto;
		}

		return null;
	}
}
