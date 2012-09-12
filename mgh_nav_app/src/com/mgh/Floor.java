/*Class containting floor objects
 * 
 */
package com.mgh;



import java.util.ArrayList;
import android.os.Parcel;
import android.os.Parcelable;

public class Floor implements Parcelable {
	
	public String floor;
	public ArrayList<Room> rooms = new ArrayList<Room>(); 
	
	public Floor(String fl, ArrayList<Room> r){
		this.floor = fl;
		this.rooms = r;
	}
	
	public Floor(){
		this.floor = "";
		this.rooms = new ArrayList<Room>();
	}

	public Floor(Parcel in) {
		this.floor = in.readString();
		in.readTypedList(rooms, Room.CREATOR);
	}
	
	public String getFloor() {
		return floor;
	}

	public void setFloor(String floor) {
		this.floor = floor;
	}

	public ArrayList<Room> getRooms() {
		return rooms;
	}

	public void setRooms(ArrayList<Room> rooms) {
		this.rooms = rooms;
	}

	@Override
	public String toString() {
		return "Floor: [floor=" + floor + ", rooms=" + rooms + "]";
	}
	
	public void writeToParcel(Parcel outParcel, int flags) {
        outParcel.writeString(floor);
        outParcel.writeTypedList(rooms);
    }

	//@Override
	public int describeContents() {
		return 0;
	}

	/*
	private void readFromParcel(Parcel in) {
		this.floor = in.readString();
		in.readList(rooms, null);
		
	}
	*/

	public static final Parcelable.Creator<Floor> CREATOR = new Parcelable.Creator<Floor>() {

		public Floor createFromParcel(Parcel source) {
			return new Floor(source);
		}

		public Floor[] newArray(int size) {
			return new Floor[size];
		}
		
		
	};
	
	/*
	public static void main(String[] args){
		ArrayList<Room> fl1 = new ArrayList<Room>();
		
		for(int i = 0; i < 10; i++){
			fl1.add(new Room("1", ""+i, "OR"+i));
		}
		
		ArrayList<Room> fl2 = new ArrayList<Room>();
		
		for(int i = 0; i < 10; i++){
			fl2.add(new Room("2",""+i, "OR"+i));
		}
		
		
		Floor test1 = new Floor("1", fl1);
		Floor test2 = new Floor("2", fl2);
		
		System.out.println(test2);
		
	}
	*/
	
}