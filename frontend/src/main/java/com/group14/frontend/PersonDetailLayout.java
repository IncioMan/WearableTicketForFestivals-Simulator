package com.group14.frontend;

import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

public class PersonDetailLayout extends CustomComponent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Label titleLabel;
	private Label searchingLabel;

	public PersonDetailLayout() {
		setCompositionRoot(buildLayout());
	}

	private Component buildLayout() {
		VerticalLayout layout = new VerticalLayout();
		layout.setSizeFull();

		layout.addComponent(buildTitle());
		layout.addComponent(buildSearchingLayout());

		return layout;
	}

	private Component buildTitle() {
		titleLabel = new Label();
		titleLabel.addStyleName(ValoTheme.LABEL_H1);
		return titleLabel;
	}

	private Component buildSearchingLayout() {
		searchingLabel = new Label();
		searchingLabel.addStyleName(ValoTheme.LABEL_H3);
		return searchingLabel;
	}

	public void updateDetails(Person person) {
		titleLabel.setValue("Person: " + person.getPersonId());
		searchingLabel.setValue("Searching: " + person.getSearching());
	}
}
