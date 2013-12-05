package edu.courses.middleware.pubsub.ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import edu.courses.middleware.pubsub.events.Event;

/**
 * A UIHandler manages the command line interface exposed to a user to control a
 * Buyer or Seller. The UIHandler class defines default commands and
 * functionality.
 * 
 * This class should be extended to build a command line interface for a
 * specific system participant (Buyer, Seller).
 * 
 */
public abstract class UIHandler extends Thread implements
		UIAlertHandlerDelegate {

	/** The command line reader. */
	protected BufferedReader br;

	/** We make callbacks on the delegate. */
	protected UIHandlerDelegate delegate;

	/** Holds a map of commands and their explanations. */
	protected Map<String, String> commands;

	/** Default commands. */
	public static final String HELP_COMMAND = "h";
	public static final String QUIT_COMMAND = "q";

	/**
	 * The thread that issues alerts to the user when the command line is not
	 * "in use."
	 */
	private UIAlertHandler uiAlertHandler;

	public UIHandler(UIHandlerDelegate delegate) {
		super();

		br = new BufferedReader(new InputStreamReader(System.in));
		this.delegate = delegate;

		commands = new HashMap<String, String>();
		commands.put(HELP_COMMAND, "Display this help message");
		commands.put(QUIT_COMMAND, "Quit");

		initCommands();

		uiAlertHandler = new UIAlertHandler(this);
		uiAlertHandler.start();
	}

	/**
	 * Fills the commands map.
	 */
	protected abstract void initCommands();

	/**
	 * Returns a String that indicates the client type (e.g., "Buyer",
	 * "Seller").
	 */
	public abstract String getDisplayName();

	/**
	 * Processes validated user input.
	 * 
	 * @param input
	 *            a valid command.
	 */
	public abstract void process(String input);

	/**
	 * Queues an Event on the UIAlertHandler to be displayed to the user when
	 * the CLI is not in use.
	 * 
	 * @param event
	 *            a user alert.
	 */
	public void deliver(Event event) {
		uiAlertHandler.enqueue(event);
	}

	/**
	 * Prints the CLI's command map.
	 */
	public void showHelp() {
		StringBuilder sb = new StringBuilder();
		sb.append(System.getProperty("line.separator"));
		sb.append(getDisplayName()
				+ " help: you may enter one of the following commands"
				+ System.getProperty("line.separator"));

		for (Map.Entry<String, String> entry : commands.entrySet()) {
			sb.append("\t" + entry.getKey() + "\t" + entry.getValue()
					+ System.getProperty("line.separator"));
		}

		System.out.println(sb.toString());
	}

	/**
	 * Called by the UIAlertHandler to display an alert Event when the CLI is
	 * not in use.
	 */
	@Override
	public abstract void alert(Event event);

	@Override
	public void run() {
		while (true) {
			try {
				System.out.print(getDisplayName() + " command: ");

				String command = br.readLine();

				synchronized (this) {
					if (command.equals("") || command.equals(" ")) {
						// do nothing, the user hit enter to clear alerts
					} else if (!commands.containsKey(command)) {
						System.out.println("Sorry, " + command
								+ " is not a valid command");
						showHelp();

					} else if (command.equals(HELP_COMMAND)) {
						showHelp();

					} else if (command.equals(QUIT_COMMAND)) {
						delegate.quit();

					} else {
						process(command);

					}

					this.notify();
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
