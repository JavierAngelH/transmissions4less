package com.transmissions4less.price;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
@Theme("valo")
public class Transmissions4lessUI extends UI {
	
	SQLContainer container = null;
	VerticalLayout layoutContent = new VerticalLayout();
	TableLayout tableLayout = new TableLayout();


	@WebServlet(value = "/*", asyncSupported = true)
	@VaadinServletConfiguration(productionMode = false, ui = Transmissions4lessUI.class)
	public static class Servlet extends VaadinServlet {
	}

	@Override
	protected void init(VaadinRequest request) {

		Page.getCurrent().setTitle("Prices");
	
		tableLayout.setSizeFull();
		tableLayout.fillTable();
		setContent(tableLayout);

	}

}