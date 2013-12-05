package edu.courses.middleware.pubsub;

import edu.courses.middleware.pubsub.events.JoinRequestEvent;

/**
 * Defines the callbacks used by the BrokerClientHandler.
 *
 */
public interface BrokerClientHandlerDelegate extends ClientHandlerDelegate {

	/**
	 * Called when a BrokerClientHandler receives a request to join the Broker
	 * tree.
	 * 
	 * @param event
	 *            the join request.
	 */
	public void receivedJoinRequest(JoinRequestEvent event);

}
