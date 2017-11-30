package com.group14.findeyourfriend.utils;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import com.group14.findeyourfriend.bracelet.Person;

public class GraphNode {
	private Person value;
	private Set<GraphNode> reachableNodes;

	public GraphNode() {
		reachableNodes = new HashSet<>();
	}

	public GraphNode(Person value) {
		this();
		this.value = value;
	}

	public Person getValue() {
		return value;
	}

	public void setValue(Person value) {
		this.value = value;
	}

	public Set<GraphNode> getReachableNodes() {
		return reachableNodes;
	}

	public void addNode(GraphNode node) {
		reachableNodes.add(node);
	}

	@Override
	public int hashCode() {
		return Objects.hash(value);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof GraphNode) {
			return this.getValue().equals(((GraphNode) obj).getValue());
		}

		return false;
	}
}
