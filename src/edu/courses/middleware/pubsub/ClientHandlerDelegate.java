package edu.courses.middleware.pubsub;

import edu.courses.middleware.pubsub.events.Event;

/**
 * Defines the callbacks used by a ClientHandler.
 * 
 */
public interface ClientHandlerDelegate {

	/**
	 * Called when a ClientHandler receives an Event.
	 * 
	 * @param event
	 *            the received Event.
	 * @param handler
	 *            the ClientHandler who received the event.
	 */
	public void receivedEvent(Event event, ClientHandler handler);

	/**
	 * Called when a ClientHandler subscribes to an Event.
	 * 
	 * @param event
	 */
	public void subscribe(Event event);

	/**
	 * Called when a ClientHandler unsubscribes from an Event.
	 * 
	 * @param event
	 */
	public void unsubscribe(Event event);

	/**
	 * Called when a ClientHandler has terminated for whatever reason.
	 * 
	 * @param handler
	 */
	public void close(ClientHandler handler);
}
