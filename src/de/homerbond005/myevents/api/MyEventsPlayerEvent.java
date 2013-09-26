/*
 * Copyright HomerBond005
 * 
 *  Published under CC BY-NC-ND 3.0
 *  http://creativecommons.org/licenses/by-nc-nd/3.0/
 */
package de.homerbond005.myevents.api;

import org.bukkit.entity.Player;

import de.homerbond005.myevents.EventStorage;

abstract class MyEventsPlayerEvent extends MyEventsEvent{
	/**
	 * The player that is the reason for this event being toggled
	 */
	private Player player;
	/**
	 * Is this event cancelled? (Will be handled by the main class)
	 */
	private boolean cancelled = false;
	
	/**
	 * Create a new MyEventsPlayerEvent object
	 * @param event An EventStorage object with the MyEvents event
	 * @param timestamp A timestamp as long
	 * @param player The player that is the reason for this event being toggled
	 */
	public MyEventsPlayerEvent(EventStorage event, long timestamp, Player player){
		super(event, timestamp);
		this.player = player;
	}
	
	/**
	 * Get the player that is the reason for this event being toggled
	 * @return A Player object
	 */
	public Player getPlayer(){
		return player;
	}
	
	/**
	 * Set the cancel status of this event
	 * @param cancelled The new status
	 */
	public void setCancelled(boolean cancelled){
		this.cancelled = cancelled;
	}
	
	/**
	 * Check if this event is cancelled
	 * @return The status
	 */
	public boolean isCancelled(){
		return cancelled;
	}
}