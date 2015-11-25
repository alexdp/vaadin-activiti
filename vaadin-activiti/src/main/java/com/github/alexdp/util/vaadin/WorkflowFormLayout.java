package com.github.alexdp.util.vaadin;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitEvent;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.fieldgroup.FieldGroup.CommitHandler;
import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.AbstractTextField;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.DefaultFieldFactory;
import com.vaadin.ui.Field;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;

public class WorkflowFormLayout<T> extends FormLayout {

	private T bean;
	private FieldGroup fieldGroup;
	private HorizontalLayout buttonLayout;
	private boolean isAttached = false;
	private Map<String, Field<?>> specialFieldBindingMap = new HashMap<String, Field<?>>();

	public WorkflowFormLayout(T bean) {
		this.bean = bean;
	}

	private FieldGroup getFieldGroup() {
		if (this.fieldGroup == null) {
			BeanItem<T> item = new BeanItem<T>(this.bean);
			this.fieldGroup = new BeanFieldGroup<T>((Class<T>) this.bean.getClass()) {
				
				private Map<String, java.lang.reflect.Field> captionToReflectFieldMap = new HashMap<String, java.lang.reflect.Field>();
				
				@Override
				public <T extends Field> T buildAndBind(String caption, Object propertyId, Class<T> fieldType) throws BindException {
					captionToReflectFieldMap.put(caption, getIntrospectedField((String) propertyId));
					return super.buildAndBind(caption, propertyId, fieldType);
				}
				
				@Override
				protected <T extends Field> T build(String caption, Class<?> dataType, Class<T> fieldType) throws BindException {
					if (specialFieldBindingMap.containsKey(caption)) {
						return (T) specialFieldBindingMap.get(caption);
					}
					Field<?> annotatedField = createAnnotatedField(captionToReflectFieldMap.get(caption));
					if (annotatedField != null) {
						annotatedField.setCaption(caption);
						return (T) annotatedField;
					}
					return super.build(caption, dataType, fieldType);
				}
			};
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
			if (AbstractTextField.class.isInstance(aField)) {
				((AbstractTextField) aField).setNullRepresentation("");
			}
			if (TextField.class.isInstance(aField)) {
				((TextField) aField).setHeight(25, Unit.PIXELS);
				((TextField) aField).setWidth(100, Unit.PERCENTAGE);
			}
			addComponent(aField);
		}
	}

	private void addButtonBar() {
		if (getButtonLayout().getComponentCount() > 0) {
			addComponent(getButtonLayout());
		}
	}

	public void addButton(String title, final boolean validateForm, final Command<T> actionToPerform) {
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

	public void bindField(Object propertyId, Field<?> aField) {
		String caption = aField.getCaption();
		if (StringUtils.isBlank(caption)) {
			caption = DefaultFieldFactory.createCaptionByPropertyId(propertyId);
			aField.setCaption(caption);
		}
		this.specialFieldBindingMap.put(caption, aField);
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

	private java.lang.reflect.Field getIntrospectedField(String propertyId) {
		Class<?> clazz = this.bean.getClass();
		try {
			return clazz.getDeclaredField(propertyId);
		} catch (SecurityException e) {
			throw new RuntimeException("Can't access field " + propertyId + " in class " + clazz.getName(), e);
		} catch (NoSuchFieldException e) {
			if (clazz.getSuperclass() != null) {
				return getIntrospectedField(propertyId);
			}
			throw new RuntimeException("Can't find field " + propertyId + " in class " + clazz.getName(), e);
		}
	}
	
	private Field<?> createAnnotatedField(java.lang.reflect.Field introspectedField) {
		if (introspectedField.isAnnotationPresent(TextAreaField.class)) {
			int rows = introspectedField.getAnnotation(TextAreaField.class).rows();
			int columns = introspectedField.getAnnotation(TextAreaField.class).columns();
			TextArea field = new TextArea();
			field.setColumns(columns);
			field.setRows(rows);
			return field;
		}
		return null;
	}
	
	
	@Documented
	@Inherited
	@Target({ ElementType.FIELD })
	@Retention(RetentionPolicy.RUNTIME)
	public @interface TextAreaField {

		/**
		 * Number of rows to be displayed.
		 * 
		 * @return number of rows
		 */
		int rows() default 0;

		/**
		 * Number of columns to be displayed.
		 * 
		 * @return number of columns
		 */
		int columns() default 0;

	}

}
