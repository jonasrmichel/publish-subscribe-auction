package edu.courses.middleware.pubsub.ui;

import edu.courses.middleware.pubsub.events.Event;

/**
 * Defines the callbacks used by the UIAlertHandler.
 * 
 */
public interface UIAlertHandlerDelegate {

	/**
	 * Called to obtain the client's display name type (i.e., "Buyer", "Seller")
	 * 
	 * @return
	 */
	public String getDisplayName();

	/**
	 * Called to display an Event to the user.
	 * 
	 * @param event
	 */
	public void alert(Event event);
}
