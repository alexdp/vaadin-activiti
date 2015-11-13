package com.github.alexdp.util.vaadin;

import java.util.Map;

import com.github.alexdp.model.Applicant;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitEvent;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.fieldgroup.FieldGroup.CommitHandler;
import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Field;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;

public class WorkflowFormLayout<T> extends FormLayout {
	
	private T bean;
	private FieldGroup fieldGroup;
	private HorizontalLayout buttonLayout;
	private boolean isAttached = false;
	
	public WorkflowFormLayout(T bean) {
		this.bean = bean;
	}
	
	private FieldGroup getFieldGroup() {
		if (this.fieldGroup == null) {
			BeanItem<T> item = new BeanItem<T>(this.bean);
			this.fieldGroup = new BeanFieldGroup<Applicant>(Applicant.class);
			this.fieldGroup.setItemDataSource(item);
			this.fieldGroup.addCommitHandler(new CommitHandler() {
				@Override
				public void preCommit(CommitEvent commitEvent) throws CommitException {
					getFieldGroup().isValid();
				}
				@Override
				public void postCommit(CommitEvent commitEvent) throws CommitException {
					// Nothing to do
				}
			});
		}
		return this.fieldGroup;
	}
	
	private void addBeanFields() {
		for (Object propertyId : getFieldGroup().getUnboundPropertyIds()) {
			Field<?> aField = getFieldGroup().buildAndBind(propertyId);
			addComponent(aField);
		}
	}
	
	private void addButtonBar() {
		if (getButtonLayout().getComponentCount() > 0) {
			addComponent(getButtonLayout());
		}
	}

	public void addButton(String title, boolean validateForm, Command<T> actionToPerform) {
		Button b = new Button(title);
		b.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				try {
					if (validateForm) {
						getFieldGroup().commit();
					}
					actionToPerform.execute(WorkflowFormLayout.this.bean);
				} catch (CommitException e) {
					Map<Field<?>, InvalidValueException> invalidFields = e.getInvalidFields();
					for (Map.Entry<Field<?>, InvalidValueException> invalidField : invalidFields.entrySet()) {
						((AbstractField<?>) invalidField.getKey()).setValidationVisible(true);
					}
					if (invalidFields.isEmpty()) {
						// TODO Handle this error
						e.printStackTrace();
						Notification.show("Action failed, Please try again");
					}
				}				
			}
		});
		getButtonLayout().addComponent(b);
	}
	
	
	@Override
	public void attach() {
		super.attach();
		if (!this.isAttached) {
			addBeanFields();
			addButtonBar();
			this.isAttached = true;
		}
	}
	
	public interface Command<T> {
		   void execute(T bean);
	}
	
	private HorizontalLayout getButtonLayout() {
		if (this.buttonLayout == null) {
			this.buttonLayout = new HorizontalLayout();
			this.buttonLayout.setSizeFull();
		}
		return this.buttonLayout;
	}
	
}
