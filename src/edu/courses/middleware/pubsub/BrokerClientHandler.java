package edu.courses.middleware.pubsub;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import edu.courses.middleware.pubsub.events.JoinRequestEvent;
import edu.courses.middleware.pubsub.events.Event;
import edu.courses.middleware.pubsub.events.SubscriptionEvent;
import edu.courses.middleware.pubsub.events.UnsubscriptionEvent;

/**
 * A ClientHandler used by Brokers specifically for other Broker clients (i.e.,
 * children/parent in the Broker tree).
 * 
 */
public class BrokerClientHandler extends ClientHandler {

	private BrokerClientHandlerDelegate delegate;

	public BrokerClientHandler(long identifier, Socket socket, ObjectInputStream in,
			ObjectOutputStream out, boolean ack, ClientHandlerDelegate delegate) {
		super(identifier, socket, in, out, ack, delegate);

		this.delegate = (BrokerClientHandlerDelegate) delegate;
	}

	public BrokerClientHandler(long identifier, int port, boolean ack,
			ClientHandlerDelegate delegate) {
		super(identifier, port, ack, delegate);

		this.delegate = (BrokerClientHandlerDelegate) delegate;
	}

	@Override
	public void process(Event event) {
		if (event instanceof JoinRequestEvent) {
			receivedConnectionRequest((JoinRequestEvent) event);

		} else if (event instanceof SubscriptionEvent) {
			receivedSubscription((SubscriptionEvent) event);

		} else if (event instanceof UnsubscriptionEvent) {
			receivedUnsubscription((UnsubscriptionEvent) event);

		} else {
			// ignore
		}
	}

	private void receivedConnectionRequest(JoinRequestEvent event) {
		// inform the delegate
		delegate.receivedJoinRequest(event);
	}

	private void receivedSubscription(SubscriptionEvent event) {
		// store the subscription locally
		subscribe(event.getSubscription());
	}

	private void receivedUnsubscription(UnsubscriptionEvent event) {
		// remove the local subscription
		unsubscribe(event.getUnsubscription());
	}
}
