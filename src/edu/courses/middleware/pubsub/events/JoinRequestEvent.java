package edu.courses.middleware.pubsub.events;

/**
 * The Event issued by clients (Buyers, Sellers, Brokers) wishing to join the
 * Broker tree.
 * 
 */
public class JoinRequestEvent extends Event {
	private static final long serialVersionUID = -1359715691342654225L;

	public static enum ClientType {
		BROKER, BUYER, SELLER
	};

	/** The requesting client's unique identifier. */
	private long identifier;

	/** The requesting client's listening port. */
	private int port;

	/** The type of client issuing the join request. */
	private ClientType clientType;

	public JoinRequestEvent(int port, ClientType clientType) {
		this.port = port;
		this.clientType = clientType;
	}
	
	public long getIdentifier() {
		return identifier;
	}
	
	public void setIdentifier(long identifier) {
		this.identifier = identifier;
	}

	public int getPort() {
		return port;
	}

	public ClientType getClientType() {
		return clientType;
	}

	@Override
	public boolean matches(Event event) {
		if (this == event)
			return true;
		if (event == null)
			return false;
		if (getClass() != event.getClass())
			return false;
		JoinRequestEvent other = (JoinRequestEvent) event;
		if (clientType != other.clientType)
			return false;
		if (identifier != other.identifier)
			return false;
		if (port != other.port)
			return false;
		return true;
	}
}
