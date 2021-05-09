package org.jamocha.rete.sc;

public interface OOIndex {
	OOFact[] getFacts();
	boolean equals(Object obj);
	int hashCode();
	OOIndex add(OOFact value);
	OOIndex addAll(OOIndex index);
}
