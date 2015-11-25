package com.github.alexdp.util.hibernate;

import java.io.File;

import javax.persistence.MappedSuperclass;

import org.hibernate.annotations.TypeDef;
import org.hibernate.dialect.Dialect;
import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.DiscriminatorType;
import org.hibernate.type.StringType;
import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.AbstractTypeDescriptor;
import org.hibernate.type.descriptor.sql.VarcharTypeDescriptor;

@MappedSuperclass
@TypeDef(defaultForType = File.class, typeClass = FilePathType.class)
public class FilePathType extends AbstractSingleColumnStandardBasicType<File> implements DiscriminatorType<File> {

	public static final FilePathType INSTANCE = new FilePathType();
	
	public static final String CLASS_NAME = FilePathType.class.getName();
	
	public FilePathType() {
		super(VarcharTypeDescriptor.INSTANCE, FilePathTypeDescriptor.INSTANCE);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getName() {
		return "file_path";
	}

	@Override
	protected boolean registerUnderJavaType() {
		return true;
	}

	@Override
	public String toString(File value) {
		return FilePathTypeDescriptor.INSTANCE.toString(value);
	}

	public String objectToSQLString(File value, Dialect dialect) throws Exception {
		return StringType.INSTANCE.objectToSQLString(toString(value), dialect);
	}

	public File stringToObject(String xml) throws Exception {
		return FilePathTypeDescriptor.INSTANCE.fromString(xml);
	}

	public static class FilePathTypeDescriptor extends AbstractTypeDescriptor<File> {
		public static final FilePathTypeDescriptor INSTANCE = new FilePathTypeDescriptor();

		public FilePathTypeDescriptor() {
			super(File.class);
		}

		public String toString(File value) {
			return value.getPath();
		}

		public File fromString(String string) {
			return new File(string);
		}

		@SuppressWarnings({ "unchecked" })
		public <X> X unwrap(File value, Class<X> type, WrapperOptions options) {
			if (value == null) {
				return null;
			}
			if (String.class.isAssignableFrom(type)) {
				return (X) toString(value);
			}
			throw unknownUnwrap(type);
		}

		public <X> File wrap(X value, WrapperOptions options) {
			if (value == null) {
				return null;
			}
			if (String.class.isInstance(value)) {
				return fromString((String) value);
			}
			throw unknownWrap(value.getClass());
		}
	}

}
