package com.idega.repository.data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.idega.util.ArrayUtil;
import com.idega.util.datastructures.HashMatrix;

/**
 * <p>Title: idegaWeb</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: idega Software</p>
 * @author <a href="thomas@idega.is">Thomas Hilbig</a>
 * @version 1.0
 * Created on Jun 14, 2004
 */
public class ImplementorRepository implements Singleton {

	private static Instantiator instantiator = new Instantiator() {
		@Override
		public Object getInstance() {
			return new ImplementorRepository();
		}
	};

	private static final String GENERAL = "general";

	static public ImplementorRepository getInstance() {
		return (ImplementorRepository) SingletonRepository.getRepository().getInstance(ImplementorRepository.class, instantiator);
	  }

	private HashMatrix<String, String, List<String>> interfaceCallerImplementor = null;

	protected ImplementorRepository(){
		// should not be initialized by constructor
	}

	public <T, C, I extends T> void addImplementorForCaller(Class<T> interfaceClass, Class<C> callerClass, Class<I> implementorClass) {
		if (this.interfaceCallerImplementor == null) {
			this.interfaceCallerImplementor = new HashMatrix<String, String, List<String>>();
		}
		String interfaceClassName = interfaceClass.getName();
		String callerClassName = (callerClass == null) ? GENERAL : callerClass.getName();
		String implementorClassName = implementorClass.getName();
		if (this.interfaceCallerImplementor.containsKey(interfaceClassName, callerClassName)) {
			List<String> implementorNames = this.interfaceCallerImplementor.get(interfaceClassName, callerClassName);
			if (implementorNames.contains(implementorClassName)) {
				// already added
				return;
			}
			// add the name to the existing list
			implementorNames.add(implementorClassName);
		}
		else{
			// new entry
			List<String> implementorNames = new ArrayList<String>(1);
			implementorNames.add(implementorClassName);
			this.interfaceCallerImplementor.put(interfaceClassName, callerClassName, implementorNames);
		}
	}

	public <T, I extends T> void addImplementor(Class<T> interfaceClass, Class<I> implementationClass) {
		addImplementorForCaller(interfaceClass, null, implementationClass);
	}

	public <T, I extends T, C> I newInstance(Class<T> interfaceClass, Class<C> callerClass) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		List<Class<I>> implementors = getValidImplementorClasses(interfaceClass, callerClass);
		// get the first one
		if (implementors == null) {
			throw new ClassNotFoundException("[ImplementorRepository] ImImplementor for interface " + interfaceClass.getName() + "could not be found");
		}
		Class<I> implementorClass = implementors.get(0);
		return implementorClass.newInstance();
	}

	public <T, I extends T, C> I newInstanceOrNull(Class<T> interfaceClass, Class<C> callerClass) {
		try {
			return newInstance(interfaceClass, callerClass);
		}
		catch (ClassNotFoundException e) {
			return null;
		}
		catch (InstantiationException e) {
			return null;
		}
		catch (IllegalAccessException e) {
			return null;
		}
	}

	public <T, I extends T> List<I> newInstances(Class<T> interfaceClass, Class<?> callerClass) {
		List<Class<I>> implementors = getValidImplementorClasses(interfaceClass, callerClass);
		List<I> instances = null;
		if (implementors == null) {
			// return empty list
			return new ArrayList<I>(0);
		}
		instances = new ArrayList<I>(implementors.size());

		for (Iterator<Class<I>> iterator = implementors.iterator(); iterator.hasNext();) {
			Class<I> aClass = iterator.next();
			try {
				I object = aClass.newInstance();
				instances.add(object);
			}
			catch (InstantiationException e) {
				// ignore
			}
			catch (IllegalAccessException e) {
				// ignore
			}
		}
		return instances;
	}

	public <T, I extends T, C> Class<I> getAnyClassImpl(Class<T> interfaceClass, Class<C> callerClass) {
		List<Class<I>> validClasses = getValidImplementorClasses(interfaceClass, callerClass);
		if (validClasses == null || validClasses.isEmpty()) {
			return null;
		}
		return validClasses.get(0);
	}

	/**
	 *
	 * @param interfaceClass
	 * @param callerClass
	 * @return null or a non-empty list
	 */
	private <T, C> List<String> getImplementorNames(Class<T> interfaceClass, Class<C> callerClass) {
		if (this.interfaceCallerImplementor == null) {
			return null;
		}
		String interfaceClassName = interfaceClass.getName();
		String callerClassName = callerClass.getName();
		if (! this.interfaceCallerImplementor.containsKey(interfaceClassName, callerClassName)) {
			callerClassName = GENERAL;
		}
		List<String> implementors = this.interfaceCallerImplementor.get(interfaceClassName, callerClassName);
		if (implementors == null || implementors.isEmpty()) {
			return null;
		}
		return implementors;
	}

	public <T, I extends T, C> List<Class<I>> getValidImplementorClasses(Class<T> interfaceClass, Class<C> callerClass) {
		List<String> names = getImplementorNames(interfaceClass, callerClass);
		if (names == null) {
			return null;
		}
		List<Class<I>> classes = new ArrayList<Class<I>>(names.size());
		for (Iterator<String> iterator = names.iterator(); iterator.hasNext();) {
			String name = iterator.next();
			try {
				Class<I> implementorClass = RefactorClassRegistry.forName(name);
				classes.add(implementorClass);
			}
			catch (ClassNotFoundException e) {
				// do nothing, not very likely that a class is registered but doesn't exist
				e.printStackTrace();
			}
		}
		if (classes.isEmpty()) {
			return null;
		}
		return classes;
	}


	/**
	 * Checks if the caller class is considered to be of the specified type.
	 * Use that method only for interfaces that are used as flags like Clonable.
	 * If that method returns true it doesn't mean that you can cast an instance of
	 * the callerClass to the specified class.
	 * If you are going to perform a cast use the instanceOf check.
	 * @param interfaceClass
	 * @param callerClass
	 * @return true if the callerClass is considered to be of the specified type.
	 */
	public <T, I extends T, C> boolean isTypeOf(Class<T> interfaceClass, Class<C> callerClass) {
		// first part:
		// does the same like "instanceOf"
		//
		// get all super classes and check for the interface
		List<Class<?>> classes = new ArrayList<Class<?>>();
		Class<?> superClass = callerClass;
		while (superClass != null) {
			Class<?>[] interfaces = superClass.getInterfaces();
			if (ArrayUtil.contains(interfaces,interfaceClass)) {
				// if the method is left at the place a cast could be done
				return true;
			}
			classes.add(superClass);
			superClass = superClass.getSuperclass();
		}
		// second part:
		// not found? check if there are some registered implementors that are considered as interface implementors
		// Important note: You can't cast the caller to that type! You can only check if that class is of that type.
		List<Class<I>> implementorClasses = getValidImplementorClasses(interfaceClass, callerClass);
		if (implementorClasses == null) {
			return false;
		}
		for (Iterator<Class<I>> iterator = implementorClasses.iterator(); iterator.hasNext();) {
			Class<I> implementor = iterator.next();
			if (classes.contains(implementor)) {
				return true;
			}
		}
		return false;
	}

}