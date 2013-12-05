package edu.courses.middleware.pubsub;

import edu.courses.middleware.pubsub.events.AvailableItemEvent;
import edu.courses.middleware.pubsub.events.BidUpdateEvent;
import edu.courses.middleware.pubsub.events.SaleFinalizedEvent;

/**
 * Defines the callbacks used by the BuyerServerHandler.
 * 
 */
public interface BuyerServerHandlerDelegate extends ServerHandlerDelegate {

	/**
	 * Called when a Buyer is notified of an available Item (publication) that
	 * matches expressed (subscribed) interests/preferences.
	 * 
	 * @param event
	 *            the event containing an Item of interest.
	 */
	public void receivedAvailableItem(AvailableItemEvent event);

	/**
	 * Called when a Buyer is notified of a bid on an Item of interest or
	 * currently being bid on.
	 * 
	 * @param event
	 *            the bid update.
	 */
	public void recievedBidUpdate(BidUpdateEvent event);

	/**
	 * Called when a Buyer is notified that an of-interest / bid on Item's
	 * auction has ended.
	 * 
	 * @param event
	 *            the event specifying the Item whose auction has ended.
	 */
	public void receivedSaleFinalized(SaleFinalizedEvent event);
}
