package edu.courses.middleware.pubsub.events;

/**
 * This Event is used to indicate an Event subscription along a path in the
 * Broker tree.
 * 
 */
public class SubscriptionEvent extends Event {
	private static final long serialVersionUID = 6525775463087228059L;

	/** The Event being subscribed to. */
	private Event event;

	public SubscriptionEvent(Event event) {
		this.event = event;
	}

	public Event getSubscription() {
		return event;
	}

	@Override
	public boolean matches(Event event) {
		// TODO Auto-generated method stub
		return false;
	}

}
