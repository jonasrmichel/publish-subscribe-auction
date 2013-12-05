package edu.courses.middleware.pubsub;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import edu.courses.middleware.pubsub.events.JoinRequestAckEvent;

/**
 * A thread that listens for the asynchronous connection from a Broker server in
 * the Broker tree.
 * 
 * A ServerListener is started by a client before requesting to join the Broker
 * tree. After the join request is issued, the ServerListener waits for a
 * connection from a Broker in the Broker tree who will act as the client's
 * dedicated point of contact in the tree. The Broker server will immediately
 * send an acknowledgement indicating that the request has either been accepted
 * or rejected.
 * 
 */
public class ServerListener extends Thread {

	private ServerSocket serverSocket = null;
	private ServerListenerDelegate delegate;

	public ServerListener(ServerListenerDelegate delegate) {
		super();

		try {
			this.delegate = delegate;

			serverSocket = new ServerSocket(0);

		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public int getPort() {
		return serverSocket.getLocalPort();
	}

	@Override
	public void run() {
		while (true) {
			try {
				// listen for a connection from a Broker
				Socket socket = serverSocket.accept();
				ObjectInputStream in = new ObjectInputStream(
						socket.getInputStream());
				ObjectOutputStream out = new ObjectOutputStream(
						socket.getOutputStream());

				// read the connection acknowledgement from the Broker
				// (accept/reject)
				JoinRequestAckEvent event = (JoinRequestAckEvent) in
						.readObject();

				// notify the delgate
				delegate.connected(event.getIdentifier(), socket, in, out, event.getAccepted());

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
