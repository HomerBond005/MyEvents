/*
 * Copyright HomerBond005
 * 
 *  Published under CC BY-NC-ND 3.0
 *  http://creativecommons.org/licenses/by-nc-nd/3.0/
 */
package de.HomerBond005.MyEvents.API;

import org.bukkit.entity.Player;
import de.HomerBond005.MyEvents.EventStorage;

/**
 * @author HomerBond005
 * This event is called when a player teleports to a MyEvents event
 * It can be cancelled using the setCancelled() method.
 * 
 */
public class PlayerTeleportEventEvent extends MyEventsPlayerEvent{

	/**
	 * Create a PlayerLogOffEventEvent
	 * @param event An EventStorage object with the MyEvents event
	 * @param timestamp A timestamp with the current time as long
	 * @param player The player that is teleporting to an event
	 */
	public PlayerTeleportEventEvent(EventStorage event, long timestamp, Player player){
		super(event, timestamp, player);
	}
}