package edu.courses.middleware.pubsub;

import edu.courses.middleware.pubsub.events.AvailableItemEvent;
import edu.courses.middleware.pubsub.events.BidUpdateEvent;
import edu.courses.middleware.pubsub.events.Event;
import edu.courses.middleware.pubsub.events.JoinRequestEvent.ClientType;
import edu.courses.middleware.pubsub.events.SaleFinalizedEvent;

/**
 * A ServerHandler used by Buyer clients to communicate with the Broker tree.
 * 
 */
public class BuyerServerHandler extends ServerHandler {

	private BuyerServerHandlerDelegate delegate;

	public BuyerServerHandler(ServerHandlerDelegate delegate) {
		super(delegate);

		this.delegate = (BuyerServerHandlerDelegate) delegate;
	}

	@Override
	public ClientType getClientType() {
		return ClientType.BUYER;
	}

	@Override
	public void process(Event event) {
		if (event instanceof AvailableItemEvent) {
			delegate.receivedAvailableItem((AvailableItemEvent) event);

		} else if (event instanceof BidUpdateEvent) {
			delegate.recievedBidUpdate((BidUpdateEvent) event);

		} else if (event instanceof SaleFinalizedEvent) {
			delegate.receivedSaleFinalized((SaleFinalizedEvent) event);

		} else {
			// ignore
		}

	}
}
