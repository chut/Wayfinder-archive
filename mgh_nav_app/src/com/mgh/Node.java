/*
 * This is the node Class. It Represents every node on the map
 * 
 * 
 */
package com.mgh;

public class Node {
	
	private String name;
	private int x;
	private int y;
	private int floor;
	private int type;
	
	
	/* 
	 * Main constructor. The only parameter is a String which is the name of the node. the naming
	 * convention of the nodes are xxyyft 
	 * where xx is the x coordinate of the node
	 * yy is the y coordinate of the node
	 * f is the floor number
	 * t is the type of node
	 * 
	 * this naming convention is also implemented in the database
	 */
	public Node(String name) {
		super();
		this.name = name;
		
		this.x = Integer.parseInt(name.substring(0,2));
		this.y = Integer.parseInt(name.substring(2,4));
		this.floor = Integer.parseInt(name.substring(4,5));
		this.type = Integer.parseInt(name.substring(5));
	}

	public String getName() {
		return name;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public String toString() {
		return "Node [name=" + name + ", x=" + x + ", y=" + y + ", floor="
				+ floor + ", type=" + type + "]";
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setX(int x) {
		this.x = x;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getFloor() {
		return floor;
	}

	public void setFloor(int floor) {
		this.floor = floor;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	
	
}
