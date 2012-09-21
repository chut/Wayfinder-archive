package com.cs460402.app.io;

public class NodeInfo {
	public final String id;
	public final String label;
	public final String type;
	public final String photo;
	public final int x;
	public final int y;
	public final boolean isConnector;
	public final boolean isPOI;
	public final String poiImg;
	public final String bldgID;
	public final String bldgName;
	public final String floorID;
	public final int floorLevel;
	public final String floorMap;
	public final String neighborNodeID;
	public final int neighborDist;
	
	public NodeInfo(String id, String label, String type, String photo, int x, int y, 
			boolean isConnector, boolean isPOI, String poiImg, String bldgID, 
			String bldName, String floorID, int floorLevel,	String floorMap, 
			String neighborNodeID, int neighborDist) {
		this.id = id;
		this.label = label;			
		this.type = null;
		this.photo = null;
		this.x = -1;
		this.y = -1;
		this.isConnector = false;
		this.isPOI = false;
		this.poiImg = null;
		this.bldgID = null;
		this.bldgName = null;
		this.floorID = null;
		this.floorLevel = -1;
		this.floorMap = null;
		this.neighborNodeID = null;
		this.neighborDist = -1;
	}
	
	public NodeInfo(String id, String label) {
		this.id = id;
		this.label = label;
		
		this.type = null;
		this.photo = null;
		this.x = -1;
		this.y = -1;
		this.isConnector = false;
		this.isPOI = false;
		this.poiImg = null;
		this.bldgID = null;
		this.bldgName = null;
		this.floorID = null;
		this.floorLevel = -1;
		this.floorMap = null;
		this.neighborNodeID = null;
		this.neighborDist = -1;
	}
	
	public NodeInfo(String floorID) {
		this.id = null;
		this.label = null;			
		this.type = null;
		this.photo = null;
		this.x = -1;
		this.y = -1;
		this.isConnector = false;
		this.isPOI = false;
		this.poiImg = null;
		this.bldgID = null;
		this.bldgName = null;
		
		this.floorID = floorID;
		
		this.floorLevel = -1;			
		this.floorMap = null;
		this.neighborNodeID = null;
		this.neighborDist = -1;
	}
	
	@Override
	public String toString() {
		return label;
	}
}
