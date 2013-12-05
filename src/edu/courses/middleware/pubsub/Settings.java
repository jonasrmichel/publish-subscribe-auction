package edu.courses.middleware.pubsub;

import java.text.DecimalFormat;

/**
 * A global static settings file.
 *
 */
public class Settings {
	
	/** Debug settings. */
	public static final boolean DEBUG_BROKER = true;
	public static final boolean DEBUG_BUYER = true;
	public static final boolean DEBUG_SELLER = true;
	
	/** Decimal formatting settings. */
	public static DecimalFormat formatter = new DecimalFormat("###.##");
}
