package edu.courses.middleware.pubsub.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.courses.middleware.pubsub.BuyerItem;
import edu.courses.middleware.pubsub.Item;
import edu.courses.middleware.pubsub.ItemTemplate;
import edu.courses.middleware.pubsub.Settings;
import edu.courses.middleware.pubsub.BuyerItem.BuyingMode;
import edu.courses.middleware.pubsub.events.AvailableItemEvent;
import edu.courses.middleware.pubsub.events.BidUpdateEvent;
import edu.courses.middleware.pubsub.events.Event;
import edu.courses.middleware.pubsub.events.SaleFinalizedEvent;

/**
 * Defines the command line interface and functionality for Buyers.
 * 
 */
public class BuyerUIHandler extends UIHandler {

	private BuyerUIHandlerDelegate delegate;

	/** Buyer-specific commands. */
	public static final String ENTER_INTEREST_COMMAND = "n";
	public static final String BID_COMMAND = "b";
	public static final String DISPLAY_ITEMS_OF_INTEREST_COMMAND = "i";
	public static final String DISPLAY_ITEMS_BIDDING_COMMAND = "a";
	public static final String DISPLAY_ITEMS_PURCHASED_COMMAND = "p";
	public static final String DISPLAY_ITEMS_LOST_COMMAND = "l";

	public BuyerUIHandler(UIHandlerDelegate delegate) {
		super(delegate);

		this.delegate = (BuyerUIHandlerDelegate) delegate;
	}

	@Override
	protected void initCommands() {
		commands.put(ENTER_INTEREST_COMMAND, "Enter new interest in auctions");
		commands.put(BID_COMMAND, "Bid on an available item");
		commands.put(DISPLAY_ITEMS_OF_INTEREST_COMMAND,
				"Display items matching your preferrences");
		commands.put(DISPLAY_ITEMS_BIDDING_COMMAND,
				"Display items you are currently bidding on");
		commands.put(DISPLAY_ITEMS_PURCHASED_COMMAND,
				"Display items you have purchased");
		commands.put(DISPLAY_ITEMS_LOST_COMMAND,
				"Display items whose auctions you have lost");
	}

	@Override
	public String getDisplayName() {
		return "Buyer";
	}

	@Override
	public void process(String input) {
		if (input.equals(ENTER_INTEREST_COMMAND)) {
			doInterest();

		} else if (input.equals(BID_COMMAND)) {
			doBid();

		} else if (input.equals(DISPLAY_ITEMS_OF_INTEREST_COMMAND)) {
			doDisplayItemsOfInterest(0);

		} else if (input.equals(DISPLAY_ITEMS_BIDDING_COMMAND)) {
			doDisplayItemsBidding(0);

		} else if (input.equals(DISPLAY_ITEMS_PURCHASED_COMMAND)) {
			doDisplayItemsPurchased();

		} else if (input.equals(DISPLAY_ITEMS_LOST_COMMAND)) {
			doDisplayItemsLost();

		} else {
			showHelp();

		}
	}

	@Override
	public void alert(Event event) {
		if (event instanceof AvailableItemEvent) {
			alertAvailableItem((AvailableItemEvent) event);

		} else if (event instanceof BidUpdateEvent) {
			alertBidUpdate((BidUpdateEvent) event);

		} else if (event instanceof SaleFinalizedEvent) {
			alertSaleFinalized((SaleFinalizedEvent) event);

		}
	}

	/**
	 * Called when the Buyer receives word that an Item matching one or more of
	 * the user's interests/preferences is listed.
	 * 
	 * @param event
	 */
	private void alertAvailableItem(AvailableItemEvent event) {
		System.out
				.println("\t* A new item matching your preferences is available for bidding:");
		System.out.println("\t  " + event.getItem().toBuyerString());
	}

	/**
	 * Called when the Buyer receives word that an Item of interest / being bid
	 * has a new high bid(der).
	 * 
	 * @param event
	 */
	private void alertBidUpdate(BidUpdateEvent event) {
		if (delegate.getItemsBidding().containsKey(event.getItemId())) {
			BuyerItem item = delegate.getItemsBidding().get(event.getItemId());

			if (delegate.getIdentifier() != event.getBuyerId()) {
				// we've been outbid!
				System.out
						.println("\t* You were outbid on the following item:");

			} else {
				// we are the new highest bidder!
				System.out
						.println("\t* You are now the highest bidder on the following item:");
			}

			System.out.println("\t  " + item.toBuyerString());
		}
	}

	/**
	 * Called when the Buyer receives word that an Item's auction has closed.
	 * 
	 * @param event
	 */
	private void alertSaleFinalized(SaleFinalizedEvent event) {
		if (delegate.getItemsBidding().containsKey(event.getItemId())
				|| delegate.getItemsPurchased().containsKey(event.getItemId())
				|| delegate.getItemsLost().containsKey(event.getItemId())) {

			// retrieve the Item object (it's in one of those lists...)
			BuyerItem item = null;
			if (item == null)
				item = delegate.getItemsBidding().get(event.getItemId());
			if (item == null)
				item = delegate.getItemsPurchased().get(event.getItemId());
			if (item == null)
				item = delegate.getItemsLost().get(event.getItemId());

			System.out.print("\t* Bidding on the following item closed...");
			if (delegate.getIdentifier() == event.getBuyerId()) {
				System.out.println("Congrats, you were the highest bidder:");
			} else {
				System.out
						.println("Sorry, either you were outbid or the auction was cancelled:");
			}

			System.out.println("\t  " + item.toSoldString());
		}
	}

	/**
	 * The procedure to generate a subscription to Items matching an
	 * ItemTemplate from user input.
	 */
	private void doInterest() {
		try {
			System.out
					.println("Ok, tell me a little about the types of items you're interested in...");
			System.out
					.println("(Hit ENTER to skip a step if you don't have a preference.)");

			// an item template captures the user's expressed interest
			ItemTemplate template = new ItemTemplate();

			// name
			System.out
					.print("Enter preferred item names (separated by commas): ");
			String[] namesArr = br.readLine().split(",");
			for (String s : namesArr) {
				String namePref = s.trim();
				if (namePref.equals("") || namePref.equals(" "))
					continue;

				template.addName(namePref);
			}

			// attributes
			System.out
					.print("Enter preferred item attributes (separated by commas): ");
			String[] attributesArr = br.readLine().split(",");
			for (String s : attributesArr) {
				String attributePref = s.trim();
				if (attributePref.equals("") || attributePref.equals(" "))
					continue;

				template.addAttribute(attributePref);
			}

			// min bid
			System.out.print("Minimum current bid preference ($): ");
			try {
				float bidMin = Float.parseFloat(br.readLine().trim());
				template.setBidMin(bidMin);

			} catch (NumberFormatException e) {
				// no input
			}

			// max bid
			System.out.print("Maximum current bid preference ($): ");
			try {
				float bidMax = Float.parseFloat(br.readLine().trim());
				template.setBidMax(bidMax);

			} catch (NumberFormatException e) {
				// no input
			}

			// min reserve
			System.out.print("Minimum reserve preference ($): ");
			try {
				float reserveMin = Float.parseFloat(br.readLine().trim());
				template.setReserveMin(reserveMin);

			} catch (NumberFormatException e) {
				// no input
			}

			// max reserve
			System.out.print("Maximum reserve preference ($): ");
			try {
				float reserveMax = Float.parseFloat(br.readLine().trim());
				template.setReserveMax(reserveMax);

			} catch (NumberFormatException e) {
				// no input
			}

			System.out
					.println("Subscribing to items matching your preferences...");

			delegate.subscribeInterest(template);

			System.out
					.println("Alright, you will be alerted if any items match your preferences!");

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * The procedure to begin bidding or re-bid on an Item from user input.
	 */
	private void doBid() {
		try {
			List<Item> items = doDisplayAllItems();

			if (items.size() == 0)
				return;

			System.out.println("Ok, which item would you like to bid on?");
			System.out.print("Enter item number: ");
			int n = Integer.parseInt(br.readLine());

			Item item = items.get(n - 1);

			// these are to be defined
			BuyerItem buyerItem = null;
			float bid = 0f;

			if (delegate.getItemsOfInterest().containsKey(item.getId())) {
				// this is an item we're not currently bidding on
				System.out
						.println("Alright, let's start bidding on this item.");

				// we must build the BuyerItem
				// buying mode
				String modeStr = "";
				while (!(modeStr.equals("a") || modeStr.equals("m"))) {
					System.out
							.print("Enter bid mode ( automatic (a), manual (m) ): ");
					modeStr = br.readLine().toLowerCase();

					if (!(modeStr.equals("a") || modeStr.equals("m")))
						System.out
								.println("Choices are [a,m]. Please try again.");
				}
				BuyingMode mode = modeStr.equals("a") ? BuyingMode.AUTOMATIC
						: BuyingMode.USER_DRIVEN;

				float increment = 0f;
				float max = 0f;
				if (mode.equals(BuyingMode.AUTOMATIC)) {
					// automatic bid increment
					System.out.print("Enter automatic bid increment ($): ");
					while (increment <= 0) {
						increment = Float.parseFloat(br.readLine());

						if (increment <= 0)
							System.out
									.println("Well, that wouldn't turn out well for you. Try again.");
					}

					// automatic max bid
					System.out.print("Enter automatic maximum bid ($): ");
					max = Float.parseFloat(br.readLine());
				}

				// we can now fill out the configured BuyerItem
				buyerItem = new BuyerItem(item, increment, max, mode);

				// initial bid
				System.out.print("Enter initial bid ($): ");
				bid = Float.parseFloat(br.readLine());

			} else if (delegate.getItemsBidding().containsKey(item.getId())) {
				// this is an item we're already bidding on
				buyerItem = (BuyerItem) item;
				if (buyerItem.getMode().equals(BuyingMode.AUTOMATIC)) {
					// an item in automatic buying mode
					System.out
							.println("This item is in automatic buying mode.");

					if (buyerItem.getBid() > buyerItem.getMax()) {
						// the current bid has exceeded the automatic maximum
						// bid
						System.out.println("The current bid ($"
								+ buyerItem.getBid()
								+ ") has exceeded your maximum ($"
								+ buyerItem.getMax() + ").");
						System.out
								.print("Increase automatic bid maximum (y/n): ");
						String yesno = br.readLine();

						if (yesno.equals("n")) {
							System.out.println("Alright, nevermind then.");
							return;
						}

						// increase automatic max bid
						System.out
								.print("Enter new automatic maximum bid ($): ");
						buyerItem.setMax(Float.parseFloat(br.readLine()));

					} else {
						// automatic bidding is still ongoing
						System.out
								.println("Automatic bidding is ongoing for this item.");
						return;
					}

				} else {
					// an item in user-driven buying mode
					System.out.print("Enter bid ($): ");
					bid = Float.parseFloat(br.readLine());
				}

			} else {
				// too slow joe, the auction ended
				System.out
						.println("Oh no! This item's auction has just ended!");
				System.out.println("Better luck next time.");
				return;
			}

			System.out.println("Publishing your bid of $"
					+ Settings.formatter.format(bid));

			delegate.publishBid(buyerItem, bid);

			System.out
					.println("Alright, you will be alerted if the seller accepts your bid.");

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Displays a numbered list of all Items of interest and currently being bid
	 * on by the Buyer.
	 * 
	 * @return
	 */
	private List<Item> doDisplayAllItems() {
		List<Item> items = doDisplayItemsOfInterest(0);
		items.addAll(doDisplayItemsBidding(items.size()));

		return items;
	}

	/**
	 * Displays a numbered list of all Items of interest to the Buyer.
	 * 
	 * @param init
	 *            the number to begin counting Items from.
	 * @return
	 */
	private List<Item> doDisplayItemsOfInterest(int init) {
		List<Item> items = new ArrayList<Item>(delegate.getItemsOfInterest()
				.values());
		StringBuilder sb = new StringBuilder();

		sb.append("Items matching your preferences:"
				+ System.getProperty("line.separator"));
		int count = init;
		for (Item item : items) {
			sb.append("\t" + Integer.toString(++count) + ".) "
					+ item.toBuyerString()
					+ System.getProperty("line.separator"));
		}

		if (items.size() == 0)
			sb.append("Sorry, there are no available items matching your preferences.");

		System.out.println(sb.toString());

		return items;
	}

	/**
	 * Displays a numbered list of all Items currently bid on by the Buyer.
	 * 
	 * @param init
	 *            the number to begin counting Items from.
	 * @return
	 */
	private List<BuyerItem> doDisplayItemsBidding(int init) {
		List<BuyerItem> items = new ArrayList<BuyerItem>(delegate
				.getItemsBidding().values());
		StringBuilder sb = new StringBuilder();

		sb.append("Items you are currently bidding on:"
				+ System.getProperty("line.separator"));
		sb.append("(*) = you are currently the highest bidder"
				+ System.getProperty("line.separator"));
		int count = init;
		for (BuyerItem item : items) {
			sb.append("\t" + Integer.toString(++count) + ".) "
					+ item.toBuyerString());
			if (delegate.getIdentifier() == item.getBuyerId())
				sb.append(" (*) ");
			sb.append(System.getProperty("line.separator"));
		}

		if (items.size() == 0)
			sb.append("You are not currently bidding on any items.");

		System.out.println(sb.toString());

		return items;
	}

	/**
	 * Displays a numbered list of all Items purchased by the Buyer.
	 * 
	 * @return
	 */
	private List<BuyerItem> doDisplayItemsPurchased() {
		List<BuyerItem> items = new ArrayList<BuyerItem>(delegate
				.getItemsPurchased().values());
		StringBuilder sb = new StringBuilder();

		sb.append("Items you have purchased:"
				+ System.getProperty("line.separator"));
		int count = 0;
		for (BuyerItem item : items) {
			sb.append("\t" + Integer.toString(++count) + ".) "
					+ item.toPurchasedString()
					+ System.getProperty("line.separator"));
		}

		if (items.size() == 0)
			sb.append("You have not purchased any items.");

		System.out.println(sb.toString());

		return items;
	}

	/**
	 * Displays a numbered list of all Items whose auctions the Buyer has lost.
	 * 
	 * @return
	 */
	private List<BuyerItem> doDisplayItemsLost() {
		List<BuyerItem> items = new ArrayList<BuyerItem>(delegate
				.getItemsLost().values());
		StringBuilder sb = new StringBuilder();

		sb.append("Items whose auctions you have lost:"
				+ System.getProperty("line.separator"));
		int count = 0;
		for (BuyerItem item : items) {
			sb.append("\t" + Integer.toString(++count) + ".) "
					+ item.toPurchasedString()
					+ System.getProperty("line.separator"));
		}

		if (items.size() == 0)
			sb.append("Woohoo! You have not lost any auctions yet.");

		System.out.println(sb.toString());

		return items;
	}

	public static void main(String[] args) {
		new BuyerUIHandler(null).run();
	}

}
