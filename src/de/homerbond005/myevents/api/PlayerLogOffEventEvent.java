/*
 * Copyright HomerBond005
 * 
 *  Published under CC BY-NC-ND 3.0
 *  http://creativecommons.org/licenses/by-nc-nd/3.0/
 */
package de.homerbond005.myevents.api;

import org.bukkit.entity.Player;

import de.homerbond005.myevents.EventStorage;

/**
 * @author HomerBond005
 * This event is called when a player logs off from a MyEvents event
 * It can be cancelled using the setCancelled() method.
 * 
 */
public class PlayerLogOffEventEvent extends MyEventsPlayerEvent{

	/**
	 * Create a PlayerLogOffEventEvent
	 * @param event An EventStorage object with the MyEvents event
	 * @param timestamp A timestamp with the current time as long
	 * @param player The player that is logging off from an event
	 */
	public PlayerLogOffEventEvent(EventStorage event, long timestamp, Player player){
		super(event, timestamp, player);
	}
}