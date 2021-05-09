package org.jamocha.rete;

/**
 * CountFact is used by the engine to propagate quantified partial matches
 * down the RETE network. It is meant to be general purpose and can be
 * used by multiple quantifiers.
 * 
 * @author Peter Lin
 *
 */
public class CountFact extends Deftemplate {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CountFact() {
		super(Constants.COUNT_FACT);
		this.createSlots();
	}
	
	protected void createSlots() {
		this.slots = new Slot[2];
		this.slots[0] = new Slot(Constants.COUNT_SLOT);
		this.slots[1] = new Slot(Constants.COUNT_VALUE);
	}
}
