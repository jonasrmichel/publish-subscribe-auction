package edu.courses.middleware.pubsub;

import edu.courses.middleware.pubsub.events.BidEvent;

/**
 * Defines the callbacks used by the SellerServerHandler.
 * 
 */
public interface SellerServerHandlerDelegate extends ServerHandlerDelegate {

	/**
	 * Called when a Seller receives a bid on a listed Item.
	 * 
	 * @param event
	 *            a Buyer's bid on on of the Seller's Items.
	 */
	public void receivedBid(BidEvent event);

}
