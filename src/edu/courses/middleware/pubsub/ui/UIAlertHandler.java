package edu.courses.middleware.pubsub.ui;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import edu.courses.middleware.pubsub.events.Event;

/**
 * A UIAlertHandler is responsible for queuing user alert Events and prompting
 * its delegate to display them to the user when the command line interface is
 * not in use.
 * 
 */
public class UIAlertHandler extends Thread {

	private UIAlertHandlerDelegate delegate;
	private Queue<Event> eventQueue;

	public UIAlertHandler(UIAlertHandlerDelegate delegate) {
		super();

		this.delegate = delegate;
		eventQueue = new ConcurrentLinkedQueue<Event>();
	}

	/**
	 * Queues an Event and notifies the thread (this thread) waiting on the
	 * Event queue.
	 * 
	 * @param event
	 */
	public void enqueue(Event event) {
		synchronized (eventQueue) {
			eventQueue.add(event);
			eventQueue.notify();
		}
	}

	@Override
	public void run() {
		while (true) {
			try {
				synchronized (eventQueue) {
					// wait until we are notified that there are Events to consume
					eventQueue.wait();

					// wait for the command line to be free (not in use)
					synchronized (delegate) {
						System.out.println();
						System.out.println(); // make some room on the command line

						// display all Events in the queue
						System.out.println("New " + delegate.getDisplayName()
								+ " alerts...");
						while (!eventQueue.isEmpty())
							delegate.alert(eventQueue.poll());
					}
				}
			} catch (InterruptedException e) {
			}
		}
	}
}
