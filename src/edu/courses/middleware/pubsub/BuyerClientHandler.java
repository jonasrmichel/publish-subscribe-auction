package edu.courses.middleware.pubsub;

import edu.courses.middleware.pubsub.events.BidEvent;
import edu.courses.middleware.pubsub.events.BidUpdateEvent;
import edu.courses.middleware.pubsub.events.Event;
import edu.courses.middleware.pubsub.events.InterestEvent;
import edu.courses.middleware.pubsub.events.SaleFinalizedEvent;

/**
 * A ClientHandler used by Brokers specifically for Buyer clients.
 * 
 */
public class BuyerClientHandler extends ClientHandler {

	public BuyerClientHandler(long identifier, int port, boolean ack,
			ClientHandlerDelegate delegate) {
		super(identifier, port, ack, delegate);
	}

	@Override
	public void process(Event event) {
		if (event instanceof InterestEvent) {
			receivedInterest((InterestEvent) event);

		} else if (event instanceof BidEvent) {
			receivedBid((BidEvent) event);

		} else {
			// ignore
		}
	}

	private void receivedInterest(InterestEvent event) {
		// subscribe to Items matching the included template
		subscribe(event);
	}

	private void receivedBid(BidEvent event) {
		// subscribe to all updates about the bid on Item
		subscribe(new BidUpdateEvent(event.getItemId()));
		subscribe(new SaleFinalizedEvent(event.getItemId()));
	}

}
