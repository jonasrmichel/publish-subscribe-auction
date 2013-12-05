package edu.courses.middleware.pubsub.events;

/**
 * This Event is used to indicate an Event unsubscription along a path in the
 * Broker tree.
 * 
 */
public class UnsubscriptionEvent extends Event {
	private static final long serialVersionUID = 6963303524174981529L;

	/** The Event being unsubscribed from. */
	private Event event;

	public UnsubscriptionEvent(Event event) {
		this.event = event;
	}

	public Event getUnsubscription() {
		return event;
	}

	@Override
	public boolean matches(Event event) {
		// TODO Auto-generated method stub
		return false;
	}
}
