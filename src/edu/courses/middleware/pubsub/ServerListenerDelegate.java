package edu.courses.middleware.pubsub;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Defines the callbacks used by a ServerListener.
 * 
 */
public interface ServerListenerDelegate {

	/**
	 * Called when a ServerListener receives a join request acknowledgement from
	 * a Broker server.
	 * 
	 * @param identifier
	 *            a unique identifier assigned by the server.
	 * 
	 * @param socket
	 *            the communication socket.
	 * @param in
	 *            the socket's input stream.
	 * @param out
	 *            the socket's output stream.
	 * @param accepted
	 *            whether or not the join request was accepted.
	 */
	public void connected(long identifier, Socket socket, ObjectInputStream in,
			ObjectOutputStream out, boolean accepted);

}
