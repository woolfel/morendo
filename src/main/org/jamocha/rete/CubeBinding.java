package org.jamocha.rete;

public class CubeBinding extends Binding {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String templateName = null;
	private String slotName = null;
	private boolean join = false;
	private boolean measure = false;
	
	public CubeBinding() {
		super();
	}
	
	public boolean isJoin() {
		return join;
	}

	public void setJoin(boolean join) {
		this.join = join;
	}

	public String getTemplateName() {
		return templateName;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

	public String getSlotName() {
		return this.slotName;
	}
	
	public void setSlotName(String name) {
		this.slotName = name;
	}
	
	public boolean isMeasure() {
		return measure;
	}

	public void setMeasure(boolean measure) {
		this.measure = measure;
	}

}
