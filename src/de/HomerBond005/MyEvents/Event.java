/*
 * Copyright HomerBond005
 * 
 *  Published under CC BY-NC-ND 3.0
 *  http://creativecommons.org/licenses/by-nc-nd/3.0/
 */
package de.HomerBond005.MyEvents;

import java.util.List;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import org.bukkit.Location;

public class Event{
	private String name;
	private GregorianCalendar time;
	private Location location;
	private String creator;
	private String desc;
	private LinkedList<String> participants;
	private LinkedList<String> teleported;
	private int duration;
	public Event(String creator, String name, long time, Location location, String desc, int duration){
		this.creator = creator;
		this.name = name;
		this.time = new GregorianCalendar();
		this.time.setTimeInMillis(time);
		this.location = location;
		this.desc = desc;
		participants = new LinkedList<String>();
		teleported = new LinkedList<String>();
		this.duration = duration;
	}
	public Event(String creator, String name, GregorianCalendar time, Location location, String desc, int duration){
		this.creator = creator;
		this.name = name;
		this.time = time;
		this.location = location;
		this.desc = desc;
		participants = new LinkedList<String>();
		teleported = new LinkedList<String>();
		this.duration = duration;
	}
	public GregorianCalendar getTime(){
		return time;
	}
	public String getName(){
		return name;
	}
	public String getCreator(){
		return creator;
	}
	public Location getLocation(){
		return location;
	}
	public String getDesc(){
		return desc;
	}
	public void setCreator(String creator){
		this.creator = creator;
	}
	public void setName(String name){
		this.name = name;
	}
	public void setTime(GregorianCalendar time){
		this.time = time;
	}
	public void setLocation(Location location){
		this.location = location;
	}
	public void setDesc(String desc){
		this.desc = desc;
	}
	public void setDuration(int duration){
		this.duration = duration;
	}
	public void addParticipant(String player){
		if(!isParticipant(player))
			participants.add(player);
	}
	public void addParticipants(List<String> players){
		participants.addAll(players);
	}
	public LinkedList<String> getParticipants(){
		return participants;
	}
	public boolean isParticipant(String player){
		return participants.contains(player);
	}
	public void removeParticipant(String player){
		if(isParticipant(player))
			participants.remove(player);
	}
	public int getDuration(){
		return duration;
	}
	public boolean isRunning(){
		long millis = new GregorianCalendar().getTimeInMillis();
		if(millis > time.getTimeInMillis())
			if(millis < (time.getTimeInMillis()+(1000*60*duration)))
				return true;
		return false;
	}
	public void playerTeleported(String player){
		if(!hasTeleported(player))
			teleported.add(player);
	}
	public boolean hasTeleported(String player){
		return teleported.contains(player);
	}
}
