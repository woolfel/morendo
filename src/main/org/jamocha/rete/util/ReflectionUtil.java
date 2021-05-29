package org.jamocha.rete.util;

import java.lang.reflect.Method;

import org.jamocha.rete.Defclass;

public class ReflectionUtil {

	public ReflectionUtil() {
	}

	@SuppressWarnings("rawtypes")
	public static Method findMethod(Defclass dfclass, String name, Object[] parameters) {
		Method m = null;
		Class clazz = dfclass.getClassObject();
		Method[] methods = clazz.getMethods();
		for (int idx=0; idx < methods.length; idx++) {
			// we loop over the methods to find a method with the same name
			if (methods[idx].getName().equals(name)) {
				Class[] mparams = methods[idx].getParameterTypes();
				if (compareParameters(mparams,parameters)) {
					m = methods[idx];
					break;
				}
			}
		}
		return m;
	}
	
	@SuppressWarnings("rawtypes")
	public static boolean compareParameters(Class[] cparams, Object[] parameters) {
		if (cparams.length == parameters.length) {
			boolean equal = true;
			for (int idx=0; idx < cparams.length; idx++) {
				Class left = cparams[idx];
				Class right = (Class)parameters[idx];
				if (!compareClass(left,right)) {
					equal = false;
					break;
				}
			}
			return equal;
		} else {
			return false;
		}
	}
	
	@SuppressWarnings("rawtypes")
	public static boolean compareClass(Class left, Class right) {
		boolean equal = false;
		if (left == right) {
			return true;
		} else if (left == String.class && right == Object.class) {
			return true;
		} else if (left == String[].class && right == Object[].class) {
			return true;
		}
		return equal;
	}
}
