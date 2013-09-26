/*
 * Copyright HomerBond005
 * 
 *  Published under CC BY-NC-ND 3.0
 *  http://creativecommons.org/licenses/by-nc-nd/3.0/
 */
package de.homerbond005.myevents.api;

import de.homerbond005.myevents.EventStorage;

/**
 * @author HomerBond005
 * This event is called when a MyEvents event is created
 * 
 */
public class EventCreatedEvent extends MyEventsEvent{

	/**
	 * Create a new EventCreatedEvent object
	 * @param event An EventStorage object with the MyEvents event
	 * @param timestamp A timestamp as long
	 */
	public EventCreatedEvent(EventStorage event, long timestamp){
		super(event, timestamp);
	}
}