package edu.courses.middleware.pubsub.events;

/**
 * The Event issued by a Broker (server) to a client (Buyer, Seller, Broker) who
 * has requested to join the Broker tree. This Event indicates whether or not
 * the join request was accepted.
 * 
 */
public class JoinRequestAckEvent extends Event {
	private static final long serialVersionUID = -7552565961826096755L;

	/** The assigned unique identifier. */
	private long identifier;
	
	/** True if the client's join request was accepted. */
	private boolean accepted;

	public JoinRequestAckEvent(long identifier, boolean accepted) {
		this.identifier = identifier;
		this.accepted = accepted;
	}
	
	public long getIdentifier() {
		return identifier;
	}

	public boolean getAccepted() {
		return accepted;
	}

	@Override
	public boolean matches(Event event) {
		// TODO Auto-generated method stub
		return false;
	}

}
