package edu.courses.middleware.pubsub;

import edu.courses.middleware.pubsub.events.AvailableItemEvent;
import edu.courses.middleware.pubsub.events.BidEvent;
import edu.courses.middleware.pubsub.events.BidUpdateEvent;
import edu.courses.middleware.pubsub.events.Event;
import edu.courses.middleware.pubsub.events.SaleFinalizedEvent;

/**
 * A ClientHandler used by Brokers specifically for Seller clients.
 * 
 */
public class SellerClientHandler extends ClientHandler {

	public SellerClientHandler(long identifier, int port, boolean ack,
			ClientHandlerDelegate delegate) {
		super(identifier, port, ack, delegate);
	}

	@Override
	public void process(Event event) {
		if (event instanceof AvailableItemEvent) {
			receivedAvailableItem((AvailableItemEvent) event);

		} else if (event instanceof BidUpdateEvent) {
			receivedBidUpdate((BidUpdateEvent) event);

		} else if (event instanceof SaleFinalizedEvent) {
			receivedSaleFinalized((SaleFinalizedEvent) event);

		} else {
			// ignore
		}
	}

	private void receivedAvailableItem(AvailableItemEvent event) {
		// subscribe to bids on this item
		subscribe(new BidEvent(event.getItem().getId()));
	}

	private void receivedBidUpdate(BidUpdateEvent event) {
		// nothing special here
	}

	private void receivedSaleFinalized(SaleFinalizedEvent event) {
		// unsubscribe from bids on this item
		unsubscribe(new BidEvent(event.getItemId()));
	}
}
