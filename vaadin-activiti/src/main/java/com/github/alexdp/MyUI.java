package com.github.alexdp;

import com.github.alexdp.ui.LoginPanel;
import com.vaadin.annotations.Theme;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.UI;

@SpringUI
@Theme("reindeer")
public class MyUI extends UI {

	@Override
	protected void init(VaadinRequest request) {
		setSizeFull();
		setContent(new LoginPanel());
	}

}
