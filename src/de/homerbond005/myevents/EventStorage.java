/*
 * Copyright HomerBond005
 * 
 *  Published under CC BY-NC-ND 3.0
 *  http://creativecommons.org/licenses/by-nc-nd/3.0/
 */
package de.homerbond005.myevents;

import java.util.List;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import org.bukkit.Location;

/**
 * This class contains all informations about an event
 * @author HomerBond005
 *
 */
public class EventStorage{
	private String name;
	private GregorianCalendar time;
	private Location location;
	private String creator;
	private String desc;
	private LinkedList<String> participants;
	private LinkedList<String> teleported;
	private int duration;
	private boolean hasEventSentStarted = false;
	private boolean hasEventSentStopped = false;
	
	/**
	 * Create a new EventStorage object with the time as long
	 * @param creator The player name of the creator
	 * @param name The name of the event
	 * @param time The time when the event starts as long
	 * @param location The place where the players teleport to
	 * @param desc A description that describes the event
	 * @param duration The duration of the event
	 */
	public EventStorage(String creator, String name, long time, Location location, String desc, int duration){
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
	
	/**
	 * Create a new EventStorage object with the time as GregorianCalendar
	 * @param creator The player name of the creator
	 * @param name The name of the event
	 * @param time The time when the event starts as GregorianCalendar
	 * @param location The place where the players teleport to
	 * @param desc A description that describes the event
	 * @param duration The duration of the event
	 */
	public EventStorage(String creator, String name, GregorianCalendar time, Location location, String desc, int duration){
		this.creator = creator;
		this.name = name;
		this.time = time;
		this.location = location;
		this.desc = desc;
		participants = new LinkedList<String>();
		teleported = new LinkedList<String>();
		this.duration = duration;
	}
	
	/**
	 * Get the start time of the event
	 * @return A GregorianCalendar with the start time
	 */
	public GregorianCalendar getTime(){
		return time;
	}
	
	/**
	 * Get the name of the event
	 * @return The name of the event
	 */
	public String getName(){
		return name;
	}
	
	/**
	 * Get the name of the creator
	 * @return The name of the creator
	 */
	public String getCreator(){
		return creator;
	}
	
	/**
	 * Get the place where the players teleport to
	 * @return A Location with the coordinates of the teleport point
	 */
	public Location getLocation(){
		return location;
	}
	
	/**
	 * Get the description of this event
	 * @return
	 */
	public String getDesc(){
		return desc;
	}
	
	/**
	 * Set the creator of the event
	 * @param creator The new creator
	 */
	public void setCreator(String creator){
		this.creator = creator;
	}
	
	/**
	 * Set the name of the event
	 * @param name
	 */
	public void setName(String name){
		this.name = name;
	}
	
	/**
	 * Set the time when the event will start
	 * @param time The new time as GregorianCalendar
	 */
	public void setTime(GregorianCalendar time){
		this.time = time;
	}
	
	/**
	 * Set the time when the event will start
	 * @param time The new time as GregorianCalendar
	 */
	public void setTime(long time){
		this.time.setTimeInMillis(time);
	}
	
	/**
	 * Set the location where the players teleport to
	 * @param location A location with the 
	 */
	public void setLocation(Location location){
		this.location = location;
	}
	
	/**
	 * Set the description of the event
	 * @param desc The new description
	 */
	public void setDesc(String desc){
		this.desc = desc;
	}
	
	/**
	 * Set the duration of the event
	 * @param duration The new duration
	 */
	public void setDuration(int duration){
		this.duration = duration;
	}
	
	/**
	 * Add a player to the participants list
	 * @param player The player's name
	 */
	public void addParticipant(String player){
		if(!isParticipant(player))
			participants.add(player);
	}
	
	/**
	 * Add a list of players to the participants list
	 * @param players The names of the players
	 */
	public void addParticipants(List<String> players){
		participants.addAll(players);
	}
	
	/**
	 * Get the players that are currently participating
	 * @return A list with participants
	 */
	public LinkedList<String> getParticipants(){
		return participants;
	}
	
	/**
	 * Check if a player is participating
	 * @param player The player's name
	 * @return A boolean that indicates if the player is participating
	 */
	public boolean isParticipant(String player){
		return participants.contains(player);
	}
	
	/**
	 * Remove a player from the participants list
	 * @param player The player's name
	 */
	public void removeParticipant(String player){
		if(isParticipant(player))
			participants.remove(player);
	}
	
	/**
	 * Get the duration of the event
	 * @return The duration of the event
	 */
	public int getDuration(){
		return duration;
	}
	
	/**
	 * Check if the event is currently running
	 * @return A boolean that indicates if the event is running
	 */
	public boolean isRunning(){
		long millis = new GregorianCalendar().getTimeInMillis();
		if(millis > time.getTimeInMillis())
			if(millis < (time.getTimeInMillis()+(1000*60*duration)))
				return true;
		return false;
	}
	
	/**
	 * Set the status of a player to 'teleported'
	 * @param player The player's name
	 */
	public void playerTeleported(String player){
		if(!hasTeleported(player))
			teleported.add(player);
	}
	
	/**
	 * Check if a player has teleported yet
	 * @param player The player's name
	 * @return A boolean that indicates if the player has teleported yet
	 */
	public boolean hasTeleported(String player){
		return teleported.contains(player);
	}
	
	/**
	 * Check if a Bukkit event has been send for this event
	 * @param event "STOPPED" or "STARTED"
	 * @return A boolean that indicates if a Bukkit event has been send for this event
	 */
	public boolean hasEventSent(String event){
		if(event.equalsIgnoreCase("STARTED"))
			return hasEventSentStarted;
		if(event.equalsIgnoreCase("STOPPED"))
			return hasEventSentStopped;
		return false;
	}
	
	/**
	 * Notify this EventStorage object that an Bukkit event has been toggled
	 * @param event "STOPPED" or "STARTED"
	 */
	public void setEventSent(String event){
		if(event.equalsIgnoreCase("STARTED"))
			hasEventSentStarted = true;
		if(event.equalsIgnoreCase("STOPPED"))
			hasEventSentStopped = true;
	}
}