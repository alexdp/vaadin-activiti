package com.github.alexdp.ui;

import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

public class LoginPanel extends Panel {
	
	public LoginPanel() {
		super("Login");
		VerticalLayout layout = new VerticalLayout();
		TextField loginTextField = new TextField("Enter username");
		Button enterButton = new Button("Go!");
		layout.addComponent(loginTextField);
		layout.addComponent(enterButton);
		enterButton.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				VaadinSession.getCurrent().setAttribute("userId", loginTextField.getValue());
				getUI().setContent(new WelcomePanel());
			}
		});
		setContent(layout);
	}

}
