package edu.courses.middleware.pubsub;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import edu.courses.middleware.pubsub.events.JoinRequestAckEvent;
import edu.courses.middleware.pubsub.events.Event;

/**
 * ClientHandlers are used by Brokers (servers) to send/receive Events to/from
 * clients (Buyers, Sellers, or other Brokers).
 * 
 * The ClientHandler class handles all of the "low level" socket communication
 * details and simply enables a Broker to send Events and be notified when
 * Events are received.
 * 
 * This class should be extended for specific types of clients.
 * 
 */
public abstract class ClientHandler extends Thread {
	/** The client's unique identifier. */
	protected long identifier;

	/** The socket used to communicate with the client. */
	protected Socket socket = null;

	/** Input/output streams. */
	protected ObjectInputStream in = null;
	protected ObjectOutputStream out = null;

	/** We issue callbacks to our delegate. */
	protected ClientHandlerDelegate delegate;

	/**
	 * A list of the client's (and possibly the client's subtree's)
	 * subscriptions.
	 */
	protected List<Event> subscriptions;

	/** The size of the client's subtree. */
	private int subTreeSize;

	public ClientHandler(long identifier, Socket socket, ObjectInputStream in,
			ObjectOutputStream out, boolean ack, ClientHandlerDelegate delegate) {
		super();

		try {
			this.identifier = identifier;
			this.socket = socket;
			this.in = in;
			this.out = out;
			this.delegate = delegate;

			if (ack)
				out.writeObject(new JoinRequestAckEvent(identifier, true));

			subscriptions = new ArrayList<Event>();
			subTreeSize = 0;

		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public ClientHandler(long identifier, int port, boolean ack,
			ClientHandlerDelegate delegate) {
		super();

		try {
			this.identifier = identifier;
			this.delegate = delegate;

			this.socket = new Socket("localhost", port);
			this.out = new ObjectOutputStream(socket.getOutputStream());
			this.in = new ObjectInputStream(socket.getInputStream());

			if (ack)
				out.writeObject(new JoinRequestAckEvent(identifier, true));

			subscriptions = new ArrayList<Event>();
			subTreeSize = 0;

		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public long getIdentifer() {
		return identifier;
	}

	public List<Event> getSubscriptions() {
		return subscriptions;
	}

	public int getSubTreeSize() {
		return subTreeSize;
	}

	public void addedSubTreeNode() {
		subTreeSize++;
	}

	public void removedSubTreeNode() {
		subTreeSize--;
	}

	/**
	 * Processes an event received from a client.
	 * 
	 * @param event
	 */
	public abstract void process(Event event);

	/**
	 * Sends an event object to the client via the socket's output stream.
	 * 
	 * @param event
	 */
	public void sendEvent(Event event) {
		try {
			out.writeObject(event);
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected void subscribe(Event event) {
		if (subscriptions.contains(event))
			return;

		subscriptions.add(event);

		// notify delegate of subscription
		delegate.subscribe(event);
	}

	protected void unsubscribe(Event event) {
		subscriptions.remove(event);

		// notify delegate of unsubscription
		delegate.unsubscribe(event);
	}

	public boolean isSubscribed(Event event) {
		for (Event subscription : subscriptions) {
			if (subscription.matches(event))
				return true;
		}

		return false;
	}

	@Override
	public void run() {
		while (true) {
			try {
				// read an event off the stream
				Event event = (Event) in.readObject();

				// process the event locally
				process(event);

				// propagate to the delegate
				delegate.receivedEvent(event, this);

			} catch (IOException e) {
				e.printStackTrace();
				close();
				return;
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				close();
				return;
			}
		}
	}

	private void close() {
		if (Settings.DEBUG_BROKER)
			System.out.println("Closing ClientHandler for Participant-#" 
					+ Long.toString(identifier));

		try {
			delegate.close(this);
			in.close();
			out.close();
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (identifier ^ (identifier >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof ClientHandler))
			return false;
		ClientHandler other = (ClientHandler) obj;
		if (identifier != other.identifier)
			return false;
		return true;
	}
}
