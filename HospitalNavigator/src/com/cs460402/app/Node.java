package com.cs460402.app;

import java.util.ArrayList;


//hello from git
public class Node {

	// fields
	private String nodeID;
	private String nodeLabel;
	private int shortestDist;
	private Node predecessor;
	private String buildingID;
	private String floorID;
	private int floorLevel;
	private ArrayList<Neighbor> neighborList;
	private String nodeType;
	private boolean isConnector;
	private String mapImg;
	private String photoImg;
	private int xCoordinate;
	private int yCoordinate;
	private boolean isPOI;
	private String poiIconImg;

	// constructor
	public Node(String nodeID, String nodeLabel, String buildingID,
			String floorID, int floorLevel, String nodeType,
			boolean isConnector, String mapImg, String photoImg,
			int xCoordinate, int yCoordinate, boolean isPOI, String poiIconImg) {
		this.nodeID = nodeID;
		this.neighborList = new ArrayList<Neighbor>();
		this.nodeLabel = nodeLabel;
		this.shortestDist = 999999999;
		this.predecessor = null;
		this.buildingID = buildingID;
		this.floorID = floorID;
		this.floorLevel = floorLevel;
		this.nodeType = nodeType;
		this.isConnector = isConnector;
		this.mapImg = mapImg;
		this.photoImg = photoImg;
		this.xCoordinate = xCoordinate;
		this.yCoordinate = yCoordinate;
		this.isPOI = isPOI;
		this.poiIconImg = poiIconImg;
	}

	public Node(String nodeID) {
		this.nodeID = nodeID;
	}

	// setters and getters
	public void setNodeID(String nodeID) {
		this.nodeID = nodeID;
	}

	public String getNodeID() {
		return nodeID;
	}

	public void setNodeLabel(String nodeLabel) {
		this.nodeLabel = nodeLabel;
	}

	public String getNodeLabel() {
		return nodeLabel;
	}

	public void setShortestDist(int distance) {
		this.shortestDist = distance;
	}

	public int getShortestDist() {
		return shortestDist;
	}

	public void setPredecessor(Node predecessor) {
		this.predecessor = predecessor;
	}

	public Node getPredecessor() {
		return predecessor;
	}

	public void setBuildingID(String buildingID) {
		this.buildingID = buildingID;
	}

	public String getBuildingID() {
		return buildingID;
	}

	public void setFloorID(String floorID) {
		this.floorID = floorID;
	}

	public String getFloorID() {
		return floorID;
	}

	public void setFloorLevel(int floorLevel) {
		this.floorLevel = floorLevel;
	}

	public int getFloorLevel() {
		return floorLevel;
	}

	public Neighbor addNeighbor(Node nodeObj, int distanceToNeighbor) {
		Neighbor x1 = new Neighbor(nodeObj, nodeObj.getNodeID(),
				distanceToNeighbor);
		neighborList.add(x1);
		x1.setParentNode(this);

		return x1;
	}

	public Neighbor addNeighbor(String nodeID, int distanceToNeighbor) {
		Neighbor x1 = new Neighbor(nodeID, distanceToNeighbor);
		neighborList.add(x1);

		return x1;
	}

	public ArrayList<Neighbor> getNeighborList() {
		return neighborList;
	}

	public void setNodeType(String nodeType) {
		this.nodeType = nodeType;
	}

	public String getNodeType() {
		return nodeType;
	}

	public void setIsConnector(boolean b) {
		this.isConnector = b;
	}

	public boolean getIsConnector() {
		return isConnector;
	}

	public Neighbor getNeighborByNode(Node nodeObj) {
		if ((neighborList == null) || (neighborList.isEmpty())) {
			return null;
		}

		for (int i = 0; i < neighborList.size(); i++) {
			if (neighborList.get(i).getNode().getNodeID()
					.equals(nodeObj.getNodeID()))
				return neighborList.get(i);
		}
		return null;
	}

	public Neighbor getNeighborByNodeID(String nodeID) {
		if ((neighborList == null) || (neighborList.isEmpty())) {
			return null;
		}

		for (int i = 0; i < neighborList.size(); i++) {
			if (neighborList.get(i).getNode().getNodeID().equals(nodeID))
				return neighborList.get(i);
		}
		return null;
	}

	public void setMapImg(String mapImg) {
		this.mapImg = mapImg;
	}

	public String getMapImg() {
		return mapImg;
	}

	public void setPhotoImg(String photoImg) {
		this.photoImg = photoImg;
	}

	public String getPhotoImg() {
		return photoImg;
	}

	public void setX(int x) {
		this.xCoordinate = x;
	}

	public int getX() {
		return xCoordinate;
	}

	public void setY(int y) {
		this.yCoordinate = y;
	}

	public int getY() {
		return yCoordinate;
	}

	public void setIsPOI(boolean isPOI) {
		this.isPOI = isPOI;
	}

	public boolean getIsPOI() {
		return isPOI;
	}

	public void setPoiIconImg(String poiIconImg) {
		this.poiIconImg = poiIconImg;
	}

	public String getPoiIconImg() {
		return poiIconImg;
	}

}
