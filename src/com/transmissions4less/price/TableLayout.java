package com.transmissions4less.price;

import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.vaadin.data.Container;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.filter.Like;
import com.vaadin.data.util.filter.Or;
import com.vaadin.data.util.filter.Compare.Equal;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.data.util.sqlcontainer.connection.JDBCConnectionPool;
import com.vaadin.data.util.sqlcontainer.connection.SimpleJDBCConnectionPool;
import com.vaadin.data.util.sqlcontainer.query.TableQuery;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Field;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Table;
import com.vaadin.ui.TableFieldFactory;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import de.steinwedel.messagebox.MessageBox;

public class TableLayout extends VerticalLayout {
	JDBCConnectionPool connectionPool;
	Table table = new Table();

	ComboBox cbCategories = new ComboBox();
	ComboBox cbSubCategories = new ComboBox();
	Label titleLabel = new Label("PRICES");
	SQLContainer containerTable = null;
	SQLContainer containerCategories = null;
	SQLContainer containerSubCategories = null;

	TextField searchTextField = new TextField();

	SQLContainer containerSubCategories2 = null;
	final Set<Object> selectedItemIds = new HashSet<Object>();

	public TableLayout() {
		addStyleName(ValoTheme.LAYOUT_WELL);
		setMargin(true);
		setSizeFull();
		buildLayout();
		customizeTable();
		try {
			connectionPool = new SimpleJDBCConnectionPool("com.mysql.jdbc.Driver", "jdbc:mysql://localhost/price", "root",
					"1234", 3, 10);
			containerCategories = new SQLContainer(new TableQuery("categories", connectionPool));

			containerSubCategories = new SQLContainer(new TableQuery("sub_categories", connectionPool));

			containerSubCategories2 = new SQLContainer(new TableQuery("sub_categories", connectionPool));

			containerTable = new SQLContainer(new TableQuery("products", connectionPool));

		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}

	public void fillTable() {
		selectedItemIds.clear();

		cbCategories.setContainerDataSource(containerCategories);

		cbSubCategories.setContainerDataSource(containerSubCategories);

		table.setContainerDataSource(containerTable);
		table.setColumnWidth("", 35);
		table.setColumnWidth("price", 100);
		table.setColumnWidth("years", 100);
		table.setVisibleColumns("", "model", "years", "trans_type", "trans_model", "engine_type", "price");

		table.setColumnHeaders(new String[] { "", "Model", "Years", "Transmission Type", "Transmission Model",
				"Engine Type/Size", "Price in USD" });

		containerTable.setAutoCommit(true);
		table.setTableFieldFactory(tableFactory);

	}

	private TableFieldFactory tableFactory = new TableFieldFactory() {

		@Override
		public Field<?> createField(Container fieldContainer, final Object itemId, Object propertyId,
				Component uiContext) {
			String column = (String) propertyId;
			TextField field = new TextField(column);
			field.setData(itemId);
			field.setNullRepresentation("");
			field.setImmediate(true);
			field.addStyleName(ValoTheme.TEXTFIELD_TINY);
			field.addStyleName(ValoTheme.TEXTFIELD_BORDERLESS);
			return field;

		}
	};

	private ClickListener addBtnListener = new ClickListener() {

		@Override
		public void buttonClick(ClickEvent event) {

			final Window subWindow = new Window();
			subWindow.setModal(true);
			subWindow.setHeight("500px");
			subWindow.setWidth("450px");
			subWindow.setCaption("Insert New Product");
			subWindow.setStyleName(ValoTheme.WINDOW_TOP_TOOLBAR);
			subWindow.setClosable(false);
			subWindow.setResizable(false);
			FormLayout formLayout = new FormLayout();
			formLayout.setMargin(new MarginInfo(false, true, false, true));
			formLayout.setSpacing(true);
			formLayout.setSizeFull();
			final ComboBox cboxCat = new ComboBox("Category");
			cboxCat.setImmediate(true);
			cboxCat.setStyleName(ValoTheme.COMBOBOX_TINY);
			cboxCat.setContainerDataSource(containerCategories);
			cboxCat.setInputPrompt("Select Category");
			cboxCat.setNullSelectionAllowed(false);
			cboxCat.setTextInputAllowed(false);
			cboxCat.setItemCaptionPropertyId("cat_name");
			cboxCat.setRequired(true);

			final ComboBox cboxSubCat = new ComboBox("Sub Category");
			cboxSubCat.setStyleName(ValoTheme.COMBOBOX_TINY);
			cboxSubCat.setContainerDataSource(containerSubCategories2);
			cboxSubCat.setInputPrompt("Select Sub Category");
			cboxSubCat.setNullSelectionAllowed(false);
			cboxSubCat.setItemCaptionPropertyId("sub_cat_name");
			cboxSubCat.setRequired(true);
			cboxSubCat.setEnabled(false);
			final TextField tfYears = new TextField("Years");
			tfYears.setRequired(true);
			tfYears.setStyleName(ValoTheme.TEXTFIELD_TINY);

			final TextField tfModel = new TextField("Model");

			tfModel.setRequired(true);
			tfModel.setStyleName(ValoTheme.TEXTFIELD_TINY);

			final TextField tfTransType = new TextField("Transmission Type");

			tfTransType.setRequired(true);
			tfTransType.setStyleName(ValoTheme.TEXTFIELD_TINY);

			final TextField tfTransModel = new TextField("Transmission Model");
			tfTransModel.setRequired(true);
			tfTransModel.setStyleName(ValoTheme.TEXTFIELD_TINY);

			final TextField tfEngineType = new TextField("Engine Type/Size");
			tfEngineType.setRequired(true);
			tfEngineType.setStyleName(ValoTheme.TEXTFIELD_TINY);

			final TextField tfPrice = new TextField("Price in USD");
			tfPrice.setRequired(true);
			tfPrice.setStyleName(ValoTheme.TEXTFIELD_TINY);

			formLayout.addComponents(cboxCat, cboxSubCat, tfYears, tfModel, tfTransType, tfTransModel, tfEngineType,
					tfPrice);

			HorizontalLayout layoutButtons = new HorizontalLayout();
			layoutButtons.setMargin(false);
			Button sendBtn = new Button("Create");
			sendBtn.addClickListener(new ClickListener() {

				@Override
				public void buttonClick(ClickEvent event) {
					try {
						cboxCat.validate();
						cboxSubCat.validate();
						tfYears.validate();
						tfModel.validate();
						tfTransType.validate();
						tfTransModel.validate();
						tfEngineType.validate();
						tfPrice.validate();
						
						Item itemCategory = cboxCat.getItem(cboxCat.getValue());
						Integer valueCategory = (Integer) itemCategory.getItemProperty("id").getValue();
						Item itemSubCategory = cboxSubCat.getItem(cboxSubCat.getValue());
						Integer valueSubCategory = (Integer) itemSubCategory.getItemProperty("id").getValue();

						
						if (addProduct(valueCategory, valueSubCategory, tfModel.getValue(), tfPrice.getValue(), tfYears.getValue(),
								tfTransType.getValue(), tfTransModel.getValue(), tfEngineType.getValue()))

							Notification.show("Product inserted successfully");
						else

							Notification.show("Error trying to insert the new product",
									Notification.Type.ERROR_MESSAGE);
						subWindow.close();
					} catch (Exception e) {
						System.out.println(e.getMessage());
					}
				}
			});
			sendBtn.setImmediate(true);
			sendBtn.setStyleName(ValoTheme.BUTTON_TINY);
			sendBtn.addStyleName(ValoTheme.BUTTON_FRIENDLY);
			Button cancelBtn = new Button("Cancel");
			cancelBtn.setStyleName(ValoTheme.BUTTON_TINY);
			cancelBtn.addStyleName(ValoTheme.BUTTON_DANGER);
			cancelBtn.setImmediate(true);
			cancelBtn.addClickListener(new ClickListener() {

				@Override
				public void buttonClick(ClickEvent event) {
					subWindow.close();

				}
			});

			layoutButtons.setSizeUndefined();
			layoutButtons.setSpacing(true);
			layoutButtons.addComponents(cancelBtn, sendBtn);

			VerticalLayout layout = new VerticalLayout();
			layout.setSpacing(true);
			layout.setMargin(true);
			layout.addComponent(formLayout);
			layout.addComponent(layoutButtons);
			layout.setComponentAlignment(formLayout, Alignment.MIDDLE_CENTER);
			layout.setComponentAlignment(layoutButtons, Alignment.MIDDLE_RIGHT);
			layout.setExpandRatio(formLayout, 3);

			layout.setSizeFull();

			subWindow.setContent(layout);
			subWindow.center();

			getUI().addWindow(subWindow);

			cboxCat.addValueChangeListener(new ValueChangeListener() {

				@Override
				public void valueChange(ValueChangeEvent event) {
					cboxSubCat.setEnabled(true);
					Item item = cboxCat.getItem(cboxCat.getValue());
					Integer value = (Integer) item.getItemProperty("id").getValue();
					String stringValue = (String) item.getItemProperty("cat_name").getValue();
					cboxSubCat.setInputPrompt("Select " + stringValue.toLowerCase());

					cboxSubCat.setValue(null);
					containerSubCategories2.removeAllContainerFilters();
					containerSubCategories2.addContainerFilter(new Equal("cat_id", value));

				}
			});

		}
	};

	private ClickListener removeListener = new ClickListener() {

		@Override
		public void buttonClick(ClickEvent event) {
			int selectedItems = selectedItemIds.size();
			if (selectedItems > 0) {

				MessageBox.createQuestion().withCaption("Confirm")
						.withMessage("Do you want to remove " + selectedItems + " product(s)?")
						.withYesButton(new Runnable() {

					@Override
					public void run() {
						for (Object object : selectedItemIds) {
							containerTable.removeItem(object);

						}

						try {
							containerTable.commit();
						} catch (UnsupportedOperationException | SQLException e) {

							e.printStackTrace();
						}

						selectedItemIds.clear();
					}
				}).withNoButton().open();

			}
		}
	};

	private void buildLayout() {
		HorizontalLayout layoutTitle = new HorizontalLayout();
		layoutTitle.setSizeUndefined();
		layoutTitle.setWidth("100%");
		layoutTitle.setSpacing(false);
		layoutTitle.setMargin(false);
		titleLabel.addStyleName(ValoTheme.LABEL_H2);
		titleLabel.addStyleName(ValoTheme.LABEL_COLORED);
		titleLabel.addStyleName(ValoTheme.LABEL_NO_MARGIN);
		titleLabel.addStyleName(ValoTheme.LABEL_BOLD);
		titleLabel.setSizeUndefined();

		layoutTitle.addComponent(titleLabel);
		layoutTitle.setComponentAlignment(titleLabel, Alignment.MIDDLE_CENTER);

		VerticalLayout layoutTable = new VerticalLayout();

		layoutTable.setSizeFull();

		layoutTable.setSpacing(true);
		HorizontalLayout layoutButtons = new HorizontalLayout();
		layoutButtons.setMargin(false);
		layoutButtons.setSpacing(true);
		layoutButtons.setSizeUndefined();
		layoutButtons.setWidth("100%");
		Button addBtn = new Button("Add new Product");
		addBtn.addClickListener(addBtnListener);
		addBtn.setImmediate(true);
		addBtn.setStyleName(ValoTheme.BUTTON_TINY);
		addBtn.addStyleName(ValoTheme.BUTTON_FRIENDLY);
		Button deleteBtn = new Button("Delete Selected");
		deleteBtn.setStyleName(ValoTheme.BUTTON_TINY);
		deleteBtn.addStyleName(ValoTheme.BUTTON_DANGER);
		deleteBtn.setImmediate(true);
		deleteBtn.addClickListener(removeListener);

		searchTextField.addStyleName(ValoTheme.TEXTFIELD_TINY);
		searchTextField.setImmediate(true);
		searchTextField.setCaption("Model");
		searchTextField.addTextChangeListener(filterChangeListener);
		searchTextField.setInputPrompt("Filter Model");
		
	
		layoutButtons.addComponents(cbCategories, cbSubCategories, searchTextField, addBtn, deleteBtn);

	
		layoutButtons.setComponentAlignment(cbCategories, Alignment.BOTTOM_LEFT);
		layoutButtons.setComponentAlignment(searchTextField, Alignment.BOTTOM_LEFT);
		layoutButtons.setComponentAlignment(cbSubCategories, Alignment.BOTTOM_LEFT);
		layoutButtons.setComponentAlignment(addBtn, Alignment.BOTTOM_RIGHT);
		layoutButtons.setComponentAlignment(deleteBtn, Alignment.BOTTOM_RIGHT);
		layoutButtons.setExpandRatio(addBtn, 3);
		addComponent(layoutTitle);
		addComponent(layoutTable);
		layoutTable.addComponent(layoutButtons);

		layoutTable.addComponent(table);
		layoutTable.setComponentAlignment(table, Alignment.TOP_CENTER);
		layoutTable.setExpandRatio(table, 3);
		setComponentAlignment(layoutTitle, Alignment.TOP_CENTER);
		setComponentAlignment(layoutTable, Alignment.TOP_CENTER);
		setExpandRatio(layoutTable, 3);
		setSpacing(true);
		setMargin(true);

		cbCategories.setStyleName(ValoTheme.COMBOBOX_TINY);
		cbCategories.setItemCaptionPropertyId("cat_name");
		cbCategories.setNullSelectionAllowed(false);
		cbCategories.setImmediate(true);
		cbCategories.setInputPrompt("Select the Category");
		cbCategories.setTextInputAllowed(false);
		cbCategories.setCaption("Category");
		cbSubCategories.setItemCaptionPropertyId("sub_cat_name");
		cbSubCategories.setStyleName(ValoTheme.COMBOBOX_TINY);
		cbSubCategories.setImmediate(true);
		cbSubCategories.setFilteringMode(FilteringMode.CONTAINS);
		cbSubCategories.setCaption("Sub Category");
		cbSubCategories.addValueChangeListener(cbSubCategoriesChange);
		cbSubCategories.setInputPrompt("Select the Sub Category");
		cbSubCategories.setPageLength(0);
		cbCategories.addValueChangeListener(cbCategoriesChange);

	}

	private ValueChangeListener cbCategoriesChange = new ValueChangeListener() {

		@Override
		public void valueChange(ValueChangeEvent event) {
			Item item = cbCategories.getItem(cbCategories.getValue());
			Integer value = (Integer) item.getItemProperty("id").getValue();
			String stringValue = (String) item.getItemProperty("cat_name").getValue();
			cbSubCategories.setInputPrompt("Select " + stringValue.toLowerCase());
			containerSubCategories.removeAllContainerFilters();
			containerTable.removeAllContainerFilters();
			containerSubCategories.addContainerFilter(new Equal("cat_id", value));
			containerTable.addContainerFilter(new Equal("cat_id", value));
			searchTextField.clear();

		}
	};

	private ValueChangeListener cbSubCategoriesChange = new ValueChangeListener() {

		@Override
		public void valueChange(ValueChangeEvent event) {
			Item item = cbSubCategories.getItem(cbSubCategories.getValue());
			if (item != null) {

				Integer subCatId = (Integer) item.getItemProperty("id").getValue();
				Integer catId = (Integer) item.getItemProperty("cat_id").getValue();

				containerTable.removeAllContainerFilters();
				containerTable.addContainerFilter(new Equal("sub_cat_id", subCatId));

				containerTable.addContainerFilter(new Equal("cat_id", catId));
				searchTextField.clear();
			}
		}
	};

	private void customizeTable() {

		table.setSizeFull();
		table.setSortEnabled(true);
		table.setStyleName(ValoTheme.TABLE_NO_HORIZONTAL_LINES);
		table.addStyleName(ValoTheme.TABLE_SMALL);
		table.setEditable(true);
		table.setImmediate(true);
		table.setSizeFull();

		table.addGeneratedColumn("", new Table.ColumnGenerator() {

			@Override
			public Object generateCell(Table source, final Object itemId, Object columnId) {
				boolean selected = false;

				final CheckBox cb = new CheckBox("", selected);

				cb.addValueChangeListener(new Property.ValueChangeListener() {

					public void valueChange(ValueChangeEvent event) {
						if (selectedItemIds.contains(itemId)) {
							selectedItemIds.remove(itemId);
						} else {
							if (cb.getValue() != false) {
								selectedItemIds.add(itemId);
							}
						}
					}
				});
				return cb;
			}
		});

	}

	@SuppressWarnings("unchecked")
	private boolean addProduct(Integer catID, Integer subCatID, String model, String price, String years,
			String transType, String transModel, String engineType) {
		Collection<Filter> filters = containerTable.getContainerFilters();
		containerTable.removeAllContainerFilters();
		containerTable.setAutoCommit(false);
		Object newProduct = containerTable.addItem();
		containerTable.getContainerProperty(newProduct, "cat_id").setValue(catID);
		containerTable.getContainerProperty(newProduct, "sub_cat_id").setValue(subCatID);
		containerTable.getContainerProperty(newProduct, "model").setValue(model);
		containerTable.getContainerProperty(newProduct, "trans_type").setValue(transType);
		containerTable.getContainerProperty(newProduct, "trans_model").setValue(transModel);
		containerTable.getContainerProperty(newProduct, "engine_type").setValue(engineType);
		containerTable.getContainerProperty(newProduct, "price").setValue(price);
		containerTable.getContainerProperty(newProduct, "years").setValue(years);

		try {
			containerTable.commit();
			containerTable.setAutoCommit(true);
			if(!filters.isEmpty())
				for (Iterator<Filter> iterator = filters.iterator(); iterator.hasNext();) {
					Filter filter  = (Filter) iterator.next();
					containerTable.addContainerFilter(filter);
				}
			return true;
		} catch (UnsupportedOperationException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	TextChangeListener filterChangeListener = new TextChangeListener() {
		@Override
		public void textChange(TextChangeEvent event) {
			String text = event.getText();
			Filter filter = new Like("model", "%" + text + "%", false);
			
			if (!text.isEmpty()) {

				containerTable.removeAllContainerFilters();
				containerTable.addContainerFilter(filter);
				

				}
			else
				containerTable.removeContainerFilters("model");
			}

	

	};

	
}
