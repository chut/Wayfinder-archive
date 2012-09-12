/*
 * class representing the route object
 * will contain arraylist of Nodes presenting a given route
 * 
 * Created by: Kevin Roisin
 */
package com.mgh;

import java.util.ArrayList;

public class Route {
	private int routeId;
	private ArrayList<Node> route = new ArrayList<Node>();
	
	/*
	 *  1-arg constructor
	 *  gets passed an arraylist of Nodes representing a given route
	 */
	public Route(ArrayList<Node> route) {
		super();
		this.route = route;
	}
	
	public ArrayList<Node> getRoute() {
		return route;
	}
	public void setRoute(ArrayList<Node> route) {
		this.route = route;
	}
	@Override
	public String toString() {
		return "Route [route=" + route + "]";
	}

	
}
