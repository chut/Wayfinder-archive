/*
 * class responsible for grid axis, mainly the logical and physical location of the nodes on a given map
 * 
 * Created by: Kevin Roisin, Omar Almadani, Nick Hentschel
 * 
 */
package com.mgh;

import java.util.ArrayList;

public class GridPoint {
	int point;
	int pixel;
	
	public GridPoint(int aPoint, int aPixel){
		this.point = aPoint;
		this.pixel = aPixel;
	}
	
	public int getPoint() {
		return point;
	}

	public void setPoint(int x) {
		this.point = x;
	}

	public int getPixel() {
		return pixel;
	}

	public void setPixel(int p) {
		this.pixel = p;
	}

	public String toString(){
		return "("+this.point+","+this.pixel+")";
	}
	
	

}
