package com.github.alexdp.util.vaadin;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PropertyUtils {
	
	private static final String SEPARATOR = ".";
	private static final int MAX_DEPTH = 5;
	private static final String ARRAY_INDICATOR = "[]";
	
	
	public static List<String> filterTopLevelPropertyIDs(Map<String, Field> beanDescription) {
		List<String> result = new ArrayList<>();
		for (String propertyId : beanDescription.keySet()) {
			if (!propertyId.contains(".")) {
				result.add(propertyId);
			}
		}
		return result;
	}
	
	public static List<String> filterNestedPropertyIDs(Map<String, Field> beanDescription) {
		List<String> result = new ArrayList<>();
		for (String propertyId : beanDescription.keySet()) {
			if (propertyId.contains(".")) {
				result.add(propertyId);
			}
		}
		return result;
	}
	
	public static Map<String, Field> describe(Class<?> clazz) throws IntrospectionException {
		return describe(clazz, null);
	}
	
	
	private static Map<String, Field> describe(Class<?> clazz, String parentPropertyId) throws IntrospectionException {
		Map<String, Field> result = new HashMap<>();
		if (parentPropertyId != null && parentPropertyId.split("\\.").length > MAX_DEPTH) {
			return result;
		}
		final BeanInfo beanInfo = Introspector.getBeanInfo(clazz);
	    final PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
	    Map<String, PropertyDescriptor> propertyDescriptorsByName = new HashMap<>();
	    for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
	    	if (propertyDescriptor.getReadMethod() == null || propertyDescriptor.getWriteMethod() == null) {
	    		continue;
	    	}
	    	String propertyId = propertyDescriptor.getDisplayName();
	    	propertyDescriptorsByName.put(propertyId, propertyDescriptor);
	    }
	    
	    for (Field aField : clazz.getDeclaredFields()) {
	    	String propertyId = aField.getName();
	    	if (!propertyDescriptorsByName.containsKey(propertyId)) {
	    		continue;
	    	}
	    	if (parentPropertyId != null) {
	    		propertyId = parentPropertyId + SEPARATOR + propertyId;
	    	}
	    	Class<?> propertyType = aField.getType();
//	    	if (Array.class.isAssignableFrom(propertyType) || Collection.class.isAssignableFrom(propertyType)) {
//	    		propertyId = propertyId + ARRAY_INDICATOR;
//	    	}
	    	Map<String, Field> nestedDescribe = describe(propertyType, propertyId);
			result.putAll(nestedDescribe);
			if (nestedDescribe.isEmpty()) {
				result.put(propertyId, aField);
			}
	    }
	    return result;
	}

}
