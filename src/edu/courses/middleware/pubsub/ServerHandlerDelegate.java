package edu.courses.middleware.pubsub;

/**
 * Defines the callbacks used by a ServerHandler.
 * 
 */
public interface ServerHandlerDelegate {

	/**
	 * Called when a ServerHandler has successfully connected to the Broker
	 * tree.
	 * 
	 * @param identifier
	 *            a server-assigned unique identifier.
	 * 
	 * @param handler
	 *            the connected ServerHandler.
	 */
	public void connected(long identifier, ServerHandler handler);

	/**
	 * Called when a ServerHandler is terminated and must close for whatever
	 * reason.
	 * 
	 * @param handler
	 *            the terminated ServerHandler.
	 */
	public void close(ServerHandler handler);

}
