package edu.courses.middleware.pubsub.events;

import java.io.Serializable;

/**
 * Events are the message objects transferred between Buyers, Sellers, and
 * Brokers in the distributed auction system.
 * 
 * This class should be extended to define a particular type of system event.
 * 
 */
public abstract class Event implements Serializable {
	private static final long serialVersionUID = -5178457197113136818L;

	/**
	 * Used to determine if two Events "match", whatever that may mean in the
	 * context of those particular Events.
	 * 
	 * @param event
	 *            a candidate matching Event.
	 * @return true if the provided Event "matches" this Event.
	 */
	public abstract boolean matches(Event event);
}
