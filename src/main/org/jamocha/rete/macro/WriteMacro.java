package org.jamocha.rete.macro;

/**
 * A WriteMacro is used to set the value of a property. This way,
 * the rule engine can avoid using reflection to invoke the write
 * method. An example of what a write macro would look like
 * <code>
 * package org.jamocha.benchmark2.order.WriteShares;
 * 
 * public class WriteShares implements WriteMacro {
 *     public WriteShares() {}
 *     
 *     public void setProperty(Object instance, Object value) {
 *         ((org.jamocha.benchmark2.Order)instance).setShares( ((Integer)value).intValue());
 *     }
 * }
 * </code>
 * 
 * @author Peter Lin
 */
public interface WriteMacro {
	void setProperty(Object instance, Object value);
}
