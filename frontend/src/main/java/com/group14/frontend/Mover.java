package com.group14.frontend;

import com.vaadin.ui.HorizontalLayout;

public class Mover extends HorizontalLayout {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Mover() {
		setWidth("10px");
		setHeight("10px");

		addStyleName(ICustomStyles.MOVER);
	}

}
