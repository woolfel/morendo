package org.jamocha.model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

public class Edge {

	private List<PropertyChangeListener> listeners = new ArrayList<PropertyChangeListener>();
	private String id;
	private String source;
	private String target;
	private String label;
	private int weight;
	
	public Edge() {
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		String old = this.id;
		this.id = id;
		this.notifyListener("id", old, id);
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		String old = this.source;
		this.source = source;
		this.notifyListener("source", old, source);
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		String old = this.target;
		this.target = target;
		this.notifyListener("target", old, target);
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		String old = this.label;
		this.label = label;
		this.notifyListener("label", old, label);
	}

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		int old = this.weight;
		this.weight = weight;
		this.notifyListener("weight", old, weight);
	}
	
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		this.listeners.add(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		this.listeners.remove(listener);
	}

	protected void notifyListener(String field, Object oldValue, Object newValue) {
		if (listeners == null || listeners.size() == 0) {
			return;
		} else {
			PropertyChangeEvent event = new PropertyChangeEvent(this, field,
					oldValue, newValue);

			for (int i = 0; i < listeners.size(); i++) {
				((java.beans.PropertyChangeListener) listeners.get(i))
						.propertyChange(event);
			}
		}

	}
}
