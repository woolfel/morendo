package org.jamocha.rete.macro;

/**
 * A ReadMacro is used to call a method on an object. This avoids
 * using Method.invoke to extract the value of a Bean property.
 * The idea is to generate a macro for each property in a java bean,
 * which isn't an EJB (enterprise java bean). Defclass will have an
 * array of ReadMacro. It can optionally use it, if the user wants
 * max performance. An example of what the class might look like.
 * <code>
 * package org.jamocha.benchmark2.order;
 * 
 * public class ReadTicker implements ReadMacro {
 *     public ReadTicker() {}
 *     
 *     public Object getProperty(Object instance) {
 *         return ((org.jamocha.benchmark2.Order)instance).getTicker();
 *     }
 * }
 * 
 * @author Peter Lin
 */
public interface ReadMacro {
	Object getProperty(Object instance);
}
