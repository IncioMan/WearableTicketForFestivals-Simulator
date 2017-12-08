package com.group14.common_interface;

public class PersonSearchRequestDto extends PersonDto {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Boolean listening;
	private Boolean responsing;
	private Boolean requesting;

	public Boolean getListening() {
		return listening;
	}

	public void setListening(Boolean listening) {
		this.listening = listening;
	}

	public Boolean getResponsing() {
		return responsing;
	}

	public void setResponsing(Boolean responsing) {
		this.responsing = responsing;
	}

	public Boolean getRequesting() {
		return requesting;
	}

	public void setRequesting(Boolean requesting) {
		this.requesting = requesting;
	}

}
