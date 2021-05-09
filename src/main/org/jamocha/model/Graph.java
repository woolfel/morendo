package org.jamocha.model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

public class Graph {

	private List<PropertyChangeListener> listeners = new ArrayList<PropertyChangeListener>();
	private String id;
	private String label;
	private String type;
	private Node[] nodes = null;
	private Edge[] edges = null;
	
	public Graph() {
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		String old = this.id;
		this.id = id;
		this.notifyListener("id", old, id);
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		String old = this.label;
		this.label = label;
		this.notifyListener("label", old, label);
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		String old = this.type;
		this.type = type;
		this.notifyListener("type", old, type);
	}

	public Node[] getNodes() {
		return nodes;
	}

	public void setNodes(Node[] nodes) {
		Node[] old = this.nodes;
		this.nodes = nodes;
		this.notifyListener("nodes", old, nodes);
	}

	public Edge[] getEdges() {
		return edges;
	}

	public void setEdges(Edge[] edges) {
		Edge[] old = this.edges;
		this.edges = edges;
		this.notifyListener("edges", old, edges);
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
