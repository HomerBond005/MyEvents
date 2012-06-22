/*
 * Copyright HomerBond005
 * 
 *  Published under CC BY-NC-ND 3.0
 *  http://creativecommons.org/licenses/by-nc-nd/3.0/
 */
package de.HomerBond005.MyEvents.API;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import de.HomerBond005.MyEvents.EventStorage;

abstract class MyEventsEvent extends Event{
	/**
	 * The timestamp
	 */
	private long timestamp;
	/**
	 * The EventStorage this event is about
	 */
	private EventStorage event;
	/**
	 * A list of handlers, required for Bukkit
	 */
	private static final HandlerList handlers = new HandlerList();
	
	/**
	 * Create a new MyEventsEvent pattern
	 * @param event An EventStorage object with the MyEvents event
	 * @param timestamp
	 */
	public MyEventsEvent(EventStorage event, long timestamp){
		this.event = event;
		this.timestamp = timestamp;
	}
	
	/**
	 * Get the EventStorage of this event
	 * @return A EventStorage object
	 */
	public EventStorage getEvent(){
		return event;
	}
	
	/**
	 * Get the timestamp when the event was triggered
	 * @return A timestamp as long
	 */
	public long getTimestamp(){
		return timestamp;
	}

	/**
	 * @see org.bukkit.event.Event#getHandlers()
	 */
	@Override
	public HandlerList getHandlers(){
		return handlers;
	}
	
	/**
	 * Static alias for
	 * @see org.bukkit.event.Event#getHandlers()
	 */
	public static HandlerList getHandlerList() {
	    return handlers;
	}
}
