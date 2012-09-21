package com.cs460402.app;

public class RouteStep {

	// fields
	private Node stepNode;
	private String stepText;
	private int distanceToNextStep;
	private String directionalText;
	private boolean isNavPoint;

	// constructor
	public RouteStep(Node nodeObj, String stepText, int distanceToNextStep) {
		this.stepNode = nodeObj;
		this.stepText = stepText;
		this.distanceToNextStep = distanceToNextStep;
		this.directionalText = "xxx";
		this.isNavPoint = true;
	}

	// setters and getters
	public void setStepNode(Node nodeObj) {
		this.stepNode = nodeObj;
	}

	public Node getStepNode() {
		return stepNode;
	}

	public void setStepText(String stepText) {
		this.stepText = stepText;
	}

	public String getStepText() {
		return stepText;
	}

	public void setDistanceToNextStep(int distance) {
		this.distanceToNextStep = distance;
	}

	public int getDistanceToNextStep() {
		return distanceToNextStep;
	}

	public void setDirectionalText(String direction) {
		this.directionalText = direction;
	}

	public String getDirectionalText() {
		return directionalText;
	}

	public void setIsNavPoint(boolean b) {
		this.isNavPoint = b;
	}

	public boolean getIsNavPoint() {
		return isNavPoint;
	}
}
