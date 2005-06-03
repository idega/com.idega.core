/* * 
 * Created on 24.6.2003
 */
package com.idega.util.reflect;

import java.lang.reflect.Field;
import com.idega.repository.data.Instantiator;
import com.idega.repository.data.RefactorClassRegistry;
import com.idega.repository.data.Singleton;
import com.idega.repository.data.SingletonRepository;

/**
 * A utility class to get access to class/object fields by reflection.
 * Title:       idega Reflection utility classes
 * Copyright:    Copyright (c) 2003
 * Company:      idega
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */
public class FieldAccessor implements Singleton
{

	private static Instantiator instantiator = new Instantiator() { public Object getInstance() { return new FieldAccessor();}};

	/**
	 * 
	 */
	private FieldAccessor()
	{
		super();
		// TODO Auto-generated constructor stub
	}
	
	public static FieldAccessor getInstance(){
		return (FieldAccessor) SingletonRepository.getRepository().getInstance(FieldAccessor.class, instantiator);
	}

	public Object getFieldValue(Object instance,String fieldName) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException{
		Class objectClass = instance.getClass();
		Field field = getField(objectClass,fieldName);
		return field.get(instance);
	}
	
	public void setFieldValue(Object instance,String fieldName,Object fieldValue) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException{
		Class objectClass = instance.getClass();
		Field field = getField(objectClass,fieldName);
		field.set(instance,fieldValue);
	}
	
	public Object getStaticFieldValue(String objectClassName,String fieldName) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, ClassNotFoundException{
		Class c = RefactorClassRegistry.forName(objectClassName);
		return getStaticFieldValue(c,fieldName);
	}
	
	public void setStaticFieldValue(String objectClassName,String fieldName,Object fieldValue) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, ClassNotFoundException{
		Class c = RefactorClassRegistry.forName(objectClassName);
		setStaticFieldValue(c,fieldName,fieldValue);
	}
	
	public Object getStaticFieldValue(Class objectClass,String fieldName) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException{
		Field field = getField(objectClass,fieldName);
		return field.get(null);
	}

	public int getStaticIntFieldValue(Class objectClass,String fieldName) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException{
		Object o = getStaticFieldValue(objectClass,fieldName);
		Integer integ = (Integer)o;
		return integ.intValue();
	}
	
	public String getStaticStringFieldValue(Class objectClass,String fieldName) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException{
		Object o = getStaticFieldValue(objectClass,fieldName);
		String s = (String)o;
		return s;
	}

	public void setStaticFieldValue(Class objectClass,String fieldName,Object fieldValue) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException{
		Field field = getField(objectClass,fieldName);
		field.set(null,fieldValue);
	}
	
	/**
	 * Gets a field in an object with name fieldName
	 * @param objectClass the class type to get the field in.
	 * @param fieldName the name of the member or static field
	 * @return The Field object if a corresponding field is found
	 * @throws NoSuchFieldException if no field with name name is found
	 */
	public Field getField(Class objectClass,String fieldName)throws NoSuchFieldException {
		Field[] fields = objectClass.getFields();
		for (int i = 0; i < fields.length; i++)
		{
			Field f = fields[i];
			if(f.getName().equals(fieldName)){
				return f;
			}
		}
		throw new NoSuchFieldException ("No such field: "+fieldName);
	}
	
}
