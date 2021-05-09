/*
 * Copyright 2002-2009 Peter Lin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://jamocha.sourceforge.net/
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package org.jamocha.rete;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.MethodDescriptor;
import java.beans.PropertyChangeListener;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.jamocha.rete.macro.PropertyMacros;
import org.jamocha.rete.macro.ReadMacro;
import org.jamocha.rete.macro.WriteMacro;
import org.jamocha.rete.util.ReflectionUtil;

/**
 * Defclass contains the introspection information for a single object type.
 * It takes a class and uses java introspection to get a list of the get/set
 * attributes. It also checks to see if the class implements java beans
 * propertyChangeListener support. If it does, the Method object for those
 * two are cached.
 */
public class Defclass implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Class OBJECT_CLASS = null;
	private BeanInfo INFO = null;
	private PropertyDescriptor[] PROPS = null;
	private boolean ISBEAN = false;
	private Method addListener = null;
	private Method removeListener = null;
	private Map methods = new HashMap();
	private Map callMethods = new HashMap();
	private PropertyMacros[] macros = null;
	private boolean useMacros = false;

	/**
	 * 
	 */
	public Defclass(Class obj) {
		super();
		this.OBJECT_CLASS = obj;
		init();
	}

	/**
	 * init is responsible for checking the class to make sure
	 * it implements addPropertyChangeListener(java.beans.PropertyChangeListener)
	 * and removePropertyChangeListener(java.beans.PropertyChangeListener).
	 * We don't require the classes extend PropertyChangeSupport.
	 */
	public void init() {
		try {
			this.INFO = Introspector.getBeanInfo(this.OBJECT_CLASS);
			// we have to filter out the class PropertyDescriptor
			PropertyDescriptor[] pd = this.INFO.getPropertyDescriptors();
			ArrayList list = new ArrayList();
			for (int idx = 0; idx < pd.length; idx++) {
				if (pd[idx].getName().equals("class")) {
					// don't add
				} else {
					// we map the methods using the PropertyDescriptor.getName for
					// the key and the PropertyDescriptor as the value
					methods.put(pd[idx].getName(), pd[idx]);
					list.add(pd[idx]);
				}
			}
			PropertyDescriptor[] newpd = new PropertyDescriptor[list.size()];
			this.PROPS = (PropertyDescriptor[]) list.toArray(newpd);
			// logic for filtering the PropertyDescriptors
			if (this.checkBean()) {
				this.ISBEAN = true;
			}
			// we clean up the array and arraylist
			list.clear();
			pd = null;
		} catch (IntrospectionException e) {
			// we should log this and throw an exception
		}
	}
	
	public void clear() {
		this.methods.clear();
		this.callMethods.clear();
		macros = null;
		PROPS = null;
		OBJECT_CLASS = null;
		INFO = null;
	}

	/**
	 * method checks to see if the class implements addPropertyChangeListener
	 * @return
	 */
	protected boolean checkBean() {
		getUtilMethods();
		if (this.addListener != null && this.removeListener != null) {
			return true;
 		} else {
 			return false;
 		}
	}

	/**
	 * method will try to look up add and remove property change listener.
	 */
	protected void getUtilMethods() {
		try {
			// since a class may inherit the addListener method from
			// a parent, we lookup all methods and not just the
			// declared methods.
			addListener = this.OBJECT_CLASS.getMethod(Constants.PCS_ADD,
					new Class[] { PropertyChangeListener.class });
			removeListener = this.OBJECT_CLASS.getMethod(Constants.PCS_REMOVE,
					new Class[] { PropertyChangeListener.class });
		} catch (NoSuchMethodException e) {
			// we should log this
		}
	}

	/**
	 * Method checks the MethodDescriptor to make sure it only
	 * has 1 parameter and that it is a propertyChangeListener
	 * @param desc
	 * @return
	 */
	public boolean checkParameter(MethodDescriptor desc) {
		boolean ispcl = false;
		if (desc.getMethod().getParameterTypes().length == 1) {
			if (desc.getMethod().getParameterTypes()[0] == PropertyChangeListener.class) {
				ispcl = true;
			}
		}
		return ispcl;
	}

	/**
	 * If the class has a method for adding propertyChangeListener,
	 * the method return true.
	 * @return
	 */
	public boolean isJavaBean() {
		return this.ISBEAN;
	}

	/**
	 * Return the PropertyDescriptors for the class
	 * @return
	 */
	public PropertyDescriptor[] getPropertyDescriptors() {
		return this.PROPS;
	}

	/**
	 * Get the BeanInfo for the class
	 * @return
	 */
	public BeanInfo getBeanInfo() {
		return this.INFO;
	}

	public Class getClassObject() {
		return this.OBJECT_CLASS;
	}

	/**
	 * Note: haven't decided if the method should throw an exception
	 * or not. Assuming the class has been declared and the defclass
	 * exists for it, it normally shouldn't encounter an exception.
	 * Cases where it would is if the method is not public. We should
	 * do that at declaration time and not runtime.
	 * @param col
	 * @param data
	 * @return
	 */
	public Object getSlotValue(int col, Object data) {
		if (useMacros) {
			return macros[col].getReadMacro().getProperty(data);
		} else {
			try {
				if (this.PROPS[col].getReadMethod() != null) {
					return this.PROPS[col].getReadMethod().invoke(data, new Object[0]);
				}
				return null;
			} catch (IllegalAccessException e) {
				return null;
			} catch (IllegalArgumentException e) {
				return null;
			} catch (InvocationTargetException e) {
				return null;
			}
		}
	}
	
	/**
	 * Use the method to update an object property
	 * @param col
	 * @param data
	 * @param value
	 */
	public void setFieldValue(int col, Object data, Object value) {
		if (useMacros) {
			macros[col].getWriteMacro().setProperty(data, value);
		} else {
			try {
				if (this.PROPS[col].getWriteMethod() != null) {
					this.PROPS[col].getWriteMethod().invoke(data, new Object[]{value});
			}
			} catch (IllegalArgumentException e) {
			} catch (IllegalAccessException e) {
			} catch (InvocationTargetException e) {
			}
		}
	}
	
	/**
	 * create the deftemplate for the defclass
	 * @param tempName
	 * @return
	 */
	public Template createDeftemplate(String tempName) {
		Slot[] st = new Slot[this.PROPS.length];
		for (int idx = 0; idx < st.length; idx++) {
			if (this.PROPS[idx].getPropertyType().isArray()) {
				st[idx] = new MultiSlot(this.PROPS[idx].getName());
				st[idx].setId(idx);
			} else {
				st[idx] = new Slot(this.PROPS[idx].getName());
				st[idx].setValueType(ConversionUtils.getTypeCode(this.PROPS[idx]
						.getPropertyType()));
				// set the column id for the slot
				st[idx].setId(idx);
			}
		}
		Deftemplate temp = new Deftemplate(tempName, this.OBJECT_CLASS
				.getName(), st);
		return temp;
	}

	/**
	 * Create the Deftemplate for the class, but with a given parent. If a
	 * template has a parent, only call this method. If the other method is
	 * called, the template is not gauranteed to work correctly.
	 * @param tempName
	 * @param parent
	 * @return
	 */
	public Template createDeftemplate(String tempName, Template parent) {
		reOrderDescriptors(parent);
		return createDeftemplate(tempName);
	}

	/**
	 * Method is specifically used to declare Cubes, so that we can assert
	 * a cube and use it for pattern matching.
	 * @param cube
	 * @return
	 */
	public Template createCubeTemplate(Cube cube) {
		CubeDimension[] dimensions = cube.getDimensions();
		Defmeasure[] defmeasures = cube.getDefmeasures();
		
		BaseSlot[] slots = new BaseSlot[dimensions.length + defmeasures.length + 1];
		int counter = 0;
		for (int idx=0; idx < dimensions.length; idx++) {
			DimensionSlot s = new DimensionSlot(dimensions[idx]);
			s.setId(idx);
			slots[idx] = s;
			counter++;
		}
		for (int idx=0; idx < defmeasures.length; idx++) {
			MeasureSlot s = new MeasureSlot(defmeasures[idx]);
			s.setId(counter);
			slots[counter] = s;
			counter++;
		}
		int lastslot = slots.length - 1;
		Slot s = new Slot();
		s.setName("dataset");
		s.setId(lastslot);
		slots[lastslot] = s;
		
		CubeTemplate templ = new CubeTemplate(cube.getName(), this.OBJECT_CLASS.getName(),slots);
		return templ;
	}
	
	/**
	 * the purpose of this method is to re-order the PropertyDescriptors, so
	 * that template inheritance works correctly.
	 * @param parent
	 */
	protected void reOrderDescriptors(Template parent) {
		ArrayList desc = null;
		boolean add = false;
		BaseSlot[] pslots = parent.getAllSlots();
		PropertyDescriptor[] newprops = new PropertyDescriptor[this.PROPS.length];
		// first thing is to make sure the existing slots from the parent
		// are in the same column
		// now check to see if the new class has more fields
		if (newprops.length > pslots.length) {
			desc = new ArrayList();
			add = true;
		}
		for (int idx = 0; idx < pslots.length; idx++) {
			newprops[idx] = getDescriptor(pslots[idx].getName());
			if (add) {
				desc.add(pslots[idx].getName());
			}
		}
		if (add) {
			ArrayList newfields = new ArrayList();
			for (int idz = 0; idz < this.PROPS.length; idz++) {
				if (!desc.contains(this.PROPS[idz].getName())) {
					// we add it to the new fields
					newfields.add(this.PROPS[idz]);
				}
			}
			int c = 0;
			// now we start from where parent slots left off
			for (int n = pslots.length; n < newprops.length; n++) {
				newprops[n] = (PropertyDescriptor) newfields.get(c);
				c++;
			}
		}
		this.PROPS = newprops;
	}

	/**
	 * Find the PropertyDescriptor with the same name
	 * @param name
	 * @return
	 */
	protected PropertyDescriptor getDescriptor(String name) {
		PropertyDescriptor pd = null;
		for (int idx = 0; idx < this.PROPS.length; idx++) {
			if (this.PROPS[idx].getName().equals(name)) {
				pd = this.PROPS[idx];
				break;
			}
		}
		return pd;
	}

	/**
	 * Get the addPropertyChangeListener(PropertyChangeListener) method for
	 * the class.
	 * @return
	 */
	public Method getAddListenerMethod() {
		return this.addListener;
	}

	/**
	 * Get the removePropertyChangeListener(PropertyChangeListener) method for
	 * the class.
	 * @return
	 */
	public Method getRemoveListenerMethod() {
		return this.removeListener;
	}

	/**
	 * Return the write method using slot name for the key
	 * @param name
	 * @return
	 */
	public Method getWriteMethod(String name) {
		Object method = this.methods.get(name);
		if (method != null) {
			return ((PropertyDescriptor)method).getWriteMethod();
		} else {
			return null;
		}
	}

	/**
	 * Return the read method using the slot name for the key
	 * @param name
	 * @return
	 */
	public Method getReadMethod(String name) {
		Object method = this.methods.get(name);
		if (method != null) {
			return ((PropertyDescriptor) method).getReadMethod();
		} else {
			return null;
		}
	}
	
	public Method getCallMethod(String name, Object[] parameters) {
		String key = name + "(";
		Class[] cparams = new Class[0];
		if (parameters != null) {
			cparams = new Class[parameters.length];
			for (int idx=0; idx < parameters.length; idx++) {
				if (idx > 0) {
					key += ",";
				}
				key += parameters[idx].getClass();
				cparams[idx] = parameters[idx].getClass();
			}
		}
		key += ")";
		Method m = (Method)this.callMethods.get(key);
		if (m != null) {
			return m;
		} else {
			m = ReflectionUtil.findMethod(this,name, cparams);
			if (m != null) {
				this.callMethods.put(key, m);
			}
		}
		return m;
	}

	/**
	 * Method is responsible for creating instances of the macros and
	 * creating an Array of PropertyMacros. If it is successful, it will
	 * set the use macro flag to true and set the macros. If it failed
	 * for any reason, use macros will stay off.
	 */
	public void loadMacros(ClassLoader cl) {
		String packageName = OBJECT_CLASS.getName().toLowerCase();
		ArrayList macrolist = new ArrayList();
		for (int idx=0; idx < PROPS.length; idx++) {
			PropertyMacros macro = new PropertyMacros();
			try {
				String pname = PROPS[idx].getName();
				String formattedName = pname.substring(0,1).toUpperCase() + pname.substring(1);
				String read = packageName + ".Read" + formattedName;
				Class rclzz = cl.loadClass(read);
				ReadMacro rmacro = (ReadMacro)rclzz.newInstance();
				macro.setReadMacro(rmacro);
				
				// create the write macro
				String write = packageName + ".Write" + formattedName;
				Class wclzz = cl.loadClass(write);
				WriteMacro wmacro = (WriteMacro)wclzz.newInstance();
				macro.setWriteMacro(wmacro);
			} catch (ClassNotFoundException exc) {
			} catch (InstantiationException e) {
			} catch (IllegalAccessException e) {
			}
			macrolist.add(macro);
		}
		// only set the macros if we could load all of them
		// otherwise don't set the macros or the flag
		if (PROPS.length == macrolist.size()) {
			PropertyMacros[] allmacros = new PropertyMacros[PROPS.length];
			allmacros = (PropertyMacros[])macrolist.toArray(allmacros);
			this.macros = allmacros;
			this.useMacros = true;
		}
	}
	
	/**
	 * Method will make a copy and return it. When a copy is made, the 
	 * Method classes are not cloned. Instead, just the HashMap is cloned.
	 * @return
	 */
	public Defclass cloneDefclass(Rete engine) {
		Defclass dcl = new Defclass(this.OBJECT_CLASS);
		dcl.addListener = this.addListener;
		dcl.INFO = this.INFO;
		dcl.ISBEAN = this.ISBEAN;
		dcl.PROPS = this.PROPS;
		dcl.removeListener = this.removeListener;
		dcl.methods = engine.newLocalMap();
		dcl.methods.putAll(this.methods);
		return dcl;
	}
}
