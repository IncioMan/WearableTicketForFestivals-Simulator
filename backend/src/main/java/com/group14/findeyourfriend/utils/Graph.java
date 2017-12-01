package com.group14.findeyourfriend.utils;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

public class Graph {
	private Set<GraphNode> nodes;

	public Graph() {
		nodes = new HashSet<>();
	}

	public boolean areConneted(GraphNode start, GraphNode end) {
		if (start == null || end == null) {
			return false;
		}

		Queue<GraphNode> queue = new LinkedList<>();
		Set<GraphNode> seenNodes = new HashSet<>();

		GraphNode currentNode = null;
		queue.add(start);
		seenNodes.add(start);
		while (!queue.isEmpty()) {
			currentNode = queue.poll();
			if (end.equals(currentNode)) {
				return true;
			}

			for (GraphNode n : currentNode.getReachableNodes()) {
				if (!seenNodes.contains(n)) {
					queue.add(n);
					seenNodes.add(n);
				}
			}
		}

		return false;
	}

	public void addNode(GraphNode node) {
		nodes.add(node);
	}

	public Set<GraphNode> getNodes() {
		return nodes;
	}

	public void setNodes(Set<GraphNode> nodes) {
		this.nodes = nodes;
	}
}