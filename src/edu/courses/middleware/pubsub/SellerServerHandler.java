package edu.courses.middleware.pubsub;

import edu.courses.middleware.pubsub.events.BidEvent;
import edu.courses.middleware.pubsub.events.Event;
import edu.courses.middleware.pubsub.events.JoinRequestEvent.ClientType;

/**
 * A ServerHandler used by Seller clients to communicate with the Broker tree.
 * 
 */
public class SellerServerHandler extends ServerHandler {
	
	private SellerServerHandlerDelegate delegate;

	public SellerServerHandler(ServerHandlerDelegate delegate) {
		super(delegate);
		
		this.delegate = (SellerServerHandlerDelegate) delegate;
	}
	
	@Override
	public ClientType getClientType() {
		return ClientType.SELLER;
	}

	@Override
	public void process(Event event) {
		if (event instanceof BidEvent) {
			delegate.receivedBid((BidEvent) event);
		
		} else {
			// ignore
		}
	}
	
}
