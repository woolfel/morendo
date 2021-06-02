package org.jamocha.rete.sc;

public interface OOFact {
	long getTimestamp();
	long getFactId();
	/**
	 * Return the Class object of the underlying java object.
	 * @return
	 */
	Class<?> getFactClass();
}
