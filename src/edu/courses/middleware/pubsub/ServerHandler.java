package edu.courses.middleware.pubsub;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import edu.courses.middleware.pubsub.events.Event;
import edu.courses.middleware.pubsub.events.JoinRequestEvent;
import edu.courses.middleware.pubsub.events.JoinRequestEvent.ClientType;

/**
 * ServerHandlers are used by Buyer and Seller clients to send/receive Events
 * to/from a Broker in the Broker tree.
 * 
 * The ServerHandler class handles all of the "low level" socket communication
 * details and simply enables a client to send Events and be notified when
 * Events are received.
 * 
 * This class should be extended for use by specific types of clients.
 * 
 */
public abstract class ServerHandler extends Thread implements
		ServerListenerDelegate {

	protected ServerListener serverListener = null;
	protected Socket socket = null;
	protected ObjectInputStream in = null;
	protected ObjectOutputStream out = null;

	protected ServerHandlerDelegate delegate;

	public ServerHandler(ServerHandlerDelegate delegate) {
		super();

		try {
			this.delegate = delegate;

			serverListener = new ServerListener(this);
			serverListener.start();

			socket = new Socket("localhost", Broker.ROOT_BROKER_PORT);
			out = new ObjectOutputStream(socket.getOutputStream());

			// connect to the Broker tree
			System.out.print("Connecting to the Broker network...");
			sendEvent(new JoinRequestEvent(serverListener.getPort(),
					getClientType()));

		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Returns an identifier indicating the type of client we are.
	 * 
	 * @return an identifier.
	 */
	public abstract ClientType getClientType();

	/**
	 * Processes an event received from a server.
	 * 
	 * @param event
	 */
	public abstract void process(Event event);

	/**
	 * Sends an event object to the server via the socket's output stream.
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

	@Override
	public void run() {
		while (true) {
			try {
				Event event = (Event) in.readObject();
				process(event);

			} catch (IOException e) {
				e.printStackTrace();
				close();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				close();
			}
		}
	}

	private void close() {
		System.out.println("Closing client");

		try {
			in.close();
			out.close();
			socket.close();
			delegate.close(this);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/* ServerListenerDelegate Interface Implementation */

	@Override
	public void connected(long identifier, Socket socket, ObjectInputStream in,
			ObjectOutputStream out, boolean accepted) {
		if (!accepted) {
			System.out.println("connection request rejected.");
			System.out.println("(The Broker tree is at capacity "
					+ "and cannot serve any more clients. Try again later.)");
			close();
			return;
		}

		try {
			// close connection to the root Broker
			this.out.close();
			this.socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		this.socket = socket;
		this.in = in;
		this.out = out;

		delegate.connected(identifier, this);

		System.out.println("connected!");
		System.out.println("(Connected to the Broker tree as Participant-#"
				+ Long.toString(identifier) + ")");
	}
}
