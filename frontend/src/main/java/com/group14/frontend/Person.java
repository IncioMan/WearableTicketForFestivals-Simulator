package com.group14.frontend;

public class Person extends Mover {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Boolean searching;
	private String personId;

	public Boolean getSearching() {
		return searching;
	}

	public void setSearching(Boolean searching) {
		this.searching = searching;
		updateStyle();
	}

	public String getPersonId() {
		return personId;
	}

	public void setPersonId(String personId) {
		this.personId = personId;
		updateStyle();
	}

	private void updateStyle() {
		if (searching != null && searching) {
			addStyleName(ICustomStyles.MOVER_SEARCHING);
		} else {
			removeStyleName(ICustomStyles.MOVER_SEARCHING);
		}

		setDescription(personId);
	}

}
