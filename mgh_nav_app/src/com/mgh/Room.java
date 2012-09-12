/*
 * a class representing a room object
 * implements parcelable to be able to send the room object between activities
 * 
 * Created by: Nick Hentschel
 */
package com.mgh;

import android.os.Parcel;
import android.os.Parcelable;



public class Room implements Parcelable {
	
	private String floor;
	private String room;
	private String type;
	private String locationPoint;
	
	public Room() {
		this.floor = "";
		this.room = "";
		this.type = "";
		this.locationPoint = "";
	}
	
	public Room(String input){
		// format for barcode text: mghnav-floor-roomname-type-locationpoint
		String[] a = input.split("[-]");
		this.floor = a[1];
		this.room = a[2];
		this.type = a[3];
		this.locationPoint = a[4];
	}
	
	public Room(String fl, String rm, String ty, String lp){
		this.floor = fl;
		this.room = rm;
		this.type = ty;
		this.locationPoint = lp; 
	}

	public String getFloor() {
		return floor;
	}
	
	public void setFloor(String floor) {
		this.floor = floor;
	}
	
	public String getRoom() {
		return room;
	}

	public void setRoom(String room) {
		this.room = room;
	}

	public String getType() {
		return type;
	}
	
	public String getLocationPoint() {
		return locationPoint;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "Floor: " + floor + " Room: " + room + " Type: " + type + "Location Point: " + locationPoint; 
	}

	public int describeContents() {
		return 0;
	}
	
	public Room(Parcel in){
		this.floor = in.readString();
		this.room = in.readString();
		this.type = in.readString();
		this.locationPoint = in.readString();
	}

	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.floor);
		dest.writeString(this.room);
		dest.writeString(this.type);
		dest.writeString(this.locationPoint);
		
	}
	
	public static final Parcelable.Creator<Room> CREATOR = new Parcelable.Creator<Room>() {
		public Room createFromParcel(Parcel in){
			return new Room(in);
		}
		
		public Room[] newArray(int size){
			return new Room[size];
		}
	
	};
	
	/*
	public static void main (String[] args){
		Room test = new Room(2, "205B", "OR");
		
		System.out.println(test);
	}
	*/
}
