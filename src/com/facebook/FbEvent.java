package com.facebook;

public class FbEvent {
	private String id;
	private String title;
	private String startTime;
	private String endTime;
	private String location;
 
	public FbEvent(String _id, String _title, String _sT, String _eT, String _loc){
		this.id = _id;
		this.title = _title;
		this.startTime = _sT;
		this.endTime = _eT;
		this.location = _loc;
	}
 
	public String getId(){
		return id;
	}
 
	public String getTitle(){
		return title;
	}
 
	public String getStartTime(){
		return startTime;
	}
 
	public String getEndTime(){
		return endTime;
	}
 
	public String getLocation(){
		return location;
	}
}