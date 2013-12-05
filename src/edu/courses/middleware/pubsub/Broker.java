package edu.courses.middleware.pubsub;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import edu.courses.middleware.pubsub.events.JoinRequestAckEvent;
import edu.courses.middleware.pubsub.events.JoinRequestEvent;
import edu.courses.middleware.pubsub.events.JoinRequestEvent.ClientType;
import edu.courses.middleware.pubsub.events.Event;
import edu.courses.middleware.pubsub.events.SubscriptionEvent;
import edu.courses.middleware.pubsub.events.UnsubscriptionEvent;

/**
 * Brokers are the intermediaries between Buyers and Sellers and act as servers
 * in the distributed auction system.
 * 
 * Brokers automatically arrange themselves in a balanced tree structure; the
 * first instantiated Broker acts as the root of the tree and subsequently
 * created Brokers join the tree as internal nodes.
 * 
 */
public class Broker implements ClientHandlerDelegate,
		BrokerClientHandlerDelegate, ServerListenerDelegate {

	/** The globally known root Broker's listening port. */
	public static final int ROOT_BROKER_PORT = 8808;

	/** The maximum number of per-Broker connections. */
	public static final int MAX_CONNECTIONS = 3;

	/** A unique identifier. */
	private long identifier = 0;

	/**
	 * A running count of system participants (used by the root Broker to
	 * generate unique identifiers).
	 */
	private long participantCount;

	/** Holds (children) client handlers. */
	private ClientHandler[] clientHandlers;

	/**
	 * The listening socket that accepts connections (only used by the root
	 * Broker node).
	 */
	private ServerSocket serverSocket = null;

	/**
	 * A thread that listens for asynchronous server callbacks (only used by
	 * non-root Broker nodes).
	 */
	private ServerListener serverListener = null;

	/** The parent Broker handler. */
	private BrokerClientHandler parentHandler = null;

	public Broker() {
		clientHandlers = new ClientHandler[MAX_CONNECTIONS];

		try {
			// attempt to initialize as the root Broker
			initializeAsRoot();

		} catch (IOException e) {
			try {
				// the root Broker already exists, join the Broker tree
				initializeAsInternal();

			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}

	/**
	 * Attempts to initialize the Broker as the Broker tree root node.
	 * 
	 * @throws IOException
	 *             thrown if the dedicated root node port is in use (i.e., the
	 *             root node already exists).
	 */
	public void initializeAsRoot() throws IOException {
		System.out
				.print("Attempting to start as the Broker tree's root node...");

		serverSocket = new ServerSocket(ROOT_BROKER_PORT);

		identifier = generateUniqueIdentifier();

		System.out.println("success!");
		System.out.println("(Broker is root node [Participant-#"
				+ Long.toString(identifier) + "].)");

		// begin listening for client connections
		System.out.println("Listening for connections...");
		listen();
	}

	/**
	 * Initialized the Broker as an internal node in the Broker tree.
	 * 
	 * @throws IOException
	 *             thrown if the tree cannot be joined.
	 */
	public void initializeAsInternal() throws IOException {
		System.out.println("fail!");
		System.out.println("(Root node already exists.)");
		System.out.print("Joining the Broker tree...");

		serverListener = new ServerListener(this);
		serverListener.start();

		Socket socket = new Socket("localhost", Broker.ROOT_BROKER_PORT);
		ObjectOutputStream out = new ObjectOutputStream(
				socket.getOutputStream());

		// request a spot in the Broker tree
		out.writeObject(new JoinRequestEvent(serverListener.getPort(),
				ClientType.BROKER));
		out.flush();

	}

	/**
	 * Generates a unique participant identifier from the running count of
	 * system participants.
	 * 
	 * This is only used by the Broker tree root.
	 * 
	 * @return a unique participant identifier.
	 */
	private long generateUniqueIdentifier() {
		return participantCount++;
	}

	/**
	 * Listens for Broker tree join requests. (Run by the Broker tree root.)
	 */
	public void listen() {
		while (true) {
			try {
				// listen for connections
				Socket socket = serverSocket.accept();

				ObjectInputStream in = new ObjectInputStream(
						socket.getInputStream());

				// read the join request
				JoinRequestEvent event = (JoinRequestEvent) in.readObject();

				// generate a unique identifier for this (potential) client
				event.setIdentifier(generateUniqueIdentifier());

				if (Settings.DEBUG_BROKER)
					System.out
							.println("Broker tree root received a new connection request"
									+ " and assigned the unique identifier Participant-#"
									+ Long.toString(event.getIdentifier()));

				receivedJoinRequest(event);

			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * Accepts a Broker tree join request and creates a handler for the new
	 * client.
	 * 
	 * @param event
	 *            the join request.
	 */
	private void acceptConnectionRequest(JoinRequestEvent event) {
		// create a handler for this connection
		ClientHandler handler = null;
		ClientType clientType = event.getClientType();
		if (clientType.equals(ClientType.BROKER)) {
			handler = new BrokerClientHandler(event.getIdentifier(),
					event.getPort(), true, this);

		} else if (clientType.equals(ClientType.BUYER)) {
			handler = new BuyerClientHandler(event.getIdentifier(),
					event.getPort(), true, this);

		} else if (clientType.equals(ClientType.SELLER)) {
			handler = new SellerClientHandler(event.getIdentifier(),
					event.getPort(), true, this);

		} else {
			System.err
					.println("Attempted to accept a connection request from unknown client type");
			return;
		}

		// keep track of the handler and start it up
		for (int i = 0; i < MAX_CONNECTIONS; i++) {
			if (clientHandlers[i] != null)
				continue;

			clientHandlers[i] = handler;
			handler.start();
			break;
		}
	}

	/**
	 * Rejects a Broker tree join request.
	 * 
	 * @param event
	 *            the join request.
	 */
	private void rejectConnectionRequest(JoinRequestEvent event) {
		try {
			Socket socket = new Socket("localhost", event.getPort());
			ObjectOutputStream out = new ObjectOutputStream(
					socket.getOutputStream());
			new ObjectInputStream(socket.getInputStream()); // necessary to
															// match client's
															// ObjectOutputStream

			out.writeObject(new JoinRequestAckEvent(-1L, false));

		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/* ClientHandlerDelegate Interface Implementation */

	@Override
	public void receivedEvent(Event event, ClientHandler handler) {
		if (event instanceof JoinRequestEvent)
			return; // this is a special case handled by a BrokerClientHandler

		// only propagate "relevant" events down the Broker tree
		// (towards event-subscribed clients)
		for (ClientHandler h : clientHandlers) {
			if (h == null || h.equals(handler) || !h.isSubscribed(event))
				continue;

			h.sendEvent(event);
		}

		// propagate all events up the Broker tree (towards the root)
		if (!(parentHandler == null || handler.equals(parentHandler)))
			parentHandler.sendEvent(event);
	}

	@Override
	public void subscribe(Event event) {
		// notify our parent
		if (parentHandler == null)
			return;

		parentHandler.sendEvent(new SubscriptionEvent(event));
	}

	@Override
	public void unsubscribe(Event event) {
		// notify our parent
		if (parentHandler == null)
			return;

		parentHandler.sendEvent(new UnsubscriptionEvent(event));
	}

	@Override
	public void close(ClientHandler handler) {
		for (int i = 0; i < MAX_CONNECTIONS; i++) {
			if (clientHandlers[i] != null && clientHandlers[i].equals(handler)) {
				clientHandlers[i] = null;
				break;
			}
		}
	}

	/* BrokerClientHandlerDelegate Interface Implementation */

	@Override
	public void receivedJoinRequest(JoinRequestEvent event) {
		if (Settings.DEBUG_BROKER)
			System.out.println("Handling a connection request...");

		// propagate the new client towards the next available
		// spot in the tree

		// identify the branch with the smallest subtree
		int minSubTree = Integer.MAX_VALUE;
		ClientHandler minBranch = null;
		for (int i = 0; i < MAX_CONNECTIONS; i++) {
			if (clientHandlers[i] == null) {
				minBranch = null;
				break;
			}

			if (clientHandlers[i] instanceof BuyerClientHandler
					|| clientHandlers[i] instanceof SellerClientHandler) {
				if (i == MAX_CONNECTIONS - 1) {
					// we're at the bottom of the Broker tree and there are
					// no available connection spots, so we must reject this
					// connection request
					if (Settings.DEBUG_BROKER)
						System.out
								.println("Rejecting connection request from Participant-#"
										+ Long.toString(event.getIdentifier()));

					rejectConnectionRequest(event);
					return;
				}

				// continue looking
				continue;
			}

			ClientHandler branch = clientHandlers[i];
			if (branch.getSubTreeSize() < minSubTree) {
				minSubTree = branch.getSubTreeSize();
				minBranch = branch;
			}
		}

		if (minBranch == null) {
			// we have an available connection spot
			if (Settings.DEBUG_BROKER)
				System.out
						.println("Accepting connection request from Participant-#"
								+ Long.toString(event.getIdentifier()));

			acceptConnectionRequest(event);

		} else {
			// propagate the connection request downwards towards along
			// the branch with the smallest subtree
			if (Settings.DEBUG_BROKER)
				System.out
						.println("Propagating connection request from Participant-#"
								+ Long.toString(event.getIdentifier())
								+ " to Participant-#"
								+ Long.toString(minBranch.getIdentifer()));

			minBranch.sendEvent(event);
			minBranch.addedSubTreeNode();
		}

	}

	/* ServerListenerDelegate Interface Implementation */

	public void connected(long identifier, Socket socket, ObjectInputStream in,
			ObjectOutputStream out, boolean accepted) {
		if (!accepted) {
			System.out.println("fail!");
			System.err.println("Could not join the Broker tree.");
			System.exit(1);
		}

		System.out.println("success!");
		System.out.println("(Joined Broker tree as Participant-#"
				+ Long.toString(identifier) + ")");

		this.identifier = identifier;
		parentHandler = new BrokerClientHandler(identifier, socket, in, out,
				false, this);
		parentHandler.start();
	}

	public static void main(String[] args) {
		new Broker();
	}
}
