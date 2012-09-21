package com.cs460402.app;

public class Neighbor {

	// fields
	private Node node;
	private String nodeID;
	private Node parentNode;
	private int distance;

	// constructor
	public Neighbor(String nodeID, int distance) {
		this.node = null;
		this.parentNode = null;
		this.distance = distance;
		this.nodeID = nodeID;
	}

	// constructor overload
	public Neighbor(Node nodeObj, String nodeID, int distance) {
		this.node = nodeObj;
		this.parentNode = null;
		this.distance = distance;
		this.nodeID = nodeID;
	}

	public void setNode(Node neighborNodeObj) {
		this.node = neighborNodeObj;
	}

	public Node getNode() {
		return node;
	}

	public void setParentNode(Node parentNodeObj) {
		this.parentNode = parentNodeObj;
	}

	public Node getParentNode() {
		return parentNode;
	}

	public void setDistance(int distance) {
		this.distance = distance;
	}

	public int getDistance() {
		return distance;
	}

	public void setNodeID(String nodeID) {
		this.nodeID = nodeID;
	}

	public String getNodeID() {
		return nodeID;
	}

}
