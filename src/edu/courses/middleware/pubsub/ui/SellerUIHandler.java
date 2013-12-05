package edu.courses.middleware.pubsub.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.courses.middleware.pubsub.Item;
import edu.courses.middleware.pubsub.Settings;
import edu.courses.middleware.pubsub.events.BidEvent;
import edu.courses.middleware.pubsub.events.Event;

/**
 * Defines the command line interface and functionality for Sellers.
 * 
 */
public class SellerUIHandler extends UIHandler {

	private SellerUIHandlerDelegate delegate;

	/** Seller-specific commands. */
	public static final String LIST_NEW_ITEM_COMMAND = "n";
	public static final String FINALIZE_ITEM_SALE_COMMAND = "f";
	public static final String DISPLAY_LISTED_ITEMS_COMMAND = "l";
	public static final String DISPLAY_SOLD_ITEMS_COMMAND = "s";

	public SellerUIHandler(UIHandlerDelegate delegate) {
		super(delegate);

		this.delegate = (SellerUIHandlerDelegate) delegate;
	}

	@Override
	protected void initCommands() {
		commands.put(LIST_NEW_ITEM_COMMAND, "Enter a new item for sale");
		commands.put(FINALIZE_ITEM_SALE_COMMAND,
				"Finalize an item's auction and sell to the highest bidder");
		commands.put(DISPLAY_LISTED_ITEMS_COMMAND,
				"Display currently listed available items");
		commands.put(DISPLAY_SOLD_ITEMS_COMMAND, "Display items sold");
	}

	@Override
	public String getDisplayName() {
		return "Seller";
	}

	@Override
	public void process(String input) {
		if (input.equals(LIST_NEW_ITEM_COMMAND)) {
			doListNewItem();

		} else if (input.equals(FINALIZE_ITEM_SALE_COMMAND)) {
			doFinalizeItemSale();

		} else if (input.equals(DISPLAY_LISTED_ITEMS_COMMAND)) {
			doDisplayListedItems();

		} else if (input.equals(DISPLAY_SOLD_ITEMS_COMMAND)) {
			doDisplaySoldItems();

		} else {
			showHelp();
		}

	}

	@Override
	public void alert(Event event) {
		if (event instanceof BidEvent) {
			alertBid((BidEvent) event);
		}
	}

	/**
	 * Called when the Seller receives word of a bid on a listed Item.
	 * 
	 * @param event
	 */
	private void alertBid(BidEvent event) {
		Item item = delegate.getItem(event.getItemId());

		System.out
				.println("\t* Your item [" + item.getName() + "] was bid on!");
		System.out.println("\t  Current bid: $"
				+ Settings.formatter.format(item.getBid()));
		System.out.println("\t  Highest bidder: Buyer-#"
				+ Long.toString(item.getBuyerId()));
	}

	/**
	 * The procedure to create and publish a new Item for sale.
	 */
	private void doListNewItem() {
		try {
			System.out.println("Ok, tell me a little about the item...");

			// name
			System.out.print("Enter item name: ");
			String name = br.readLine();

			// attributes
			System.out.print("Enter item attributes (separated by commas): ");
			String[] attributesArr = br.readLine().split(",");
			Set<String> attributes = new HashSet<String>();
			for (String s : attributesArr) {
				attributes.add(s.trim());
			}

			// reserve
			System.out.print("Enter item reserve (minimum bid): ");
			float reserve = Float.parseFloat(br.readLine());

			System.out.println("Listing your item...");

			delegate.listAvailableItem(name, attributes, reserve);

			System.out.println("Alright, bidding is now open on your item!");

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * The procedure to close bidding on a listed Item.
	 */
	private void doFinalizeItemSale() {
		try {
			List<Item> items = doDisplayListedItems();

			if (items.size() == 0)
				return;

			System.out
					.println("Ok, which item's auction would you like to like to close?");
			System.out.print("Enter item number: ");
			int n = Integer.parseInt(br.readLine());

			System.out.print("Are you sure (y/n): ");
			String yesno = br.readLine();

			if (yesno.equals("n")) {
				System.out.println("Alright, nevermind then.");
				return;
			}

			System.out.println("Closing your item's auction...");

			Item item = items.get(n - 1);
			delegate.finalizeSale(item);

			System.out.println("Bidding on your item has ended!");
			System.out.println(item.toSoldString());

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Displays a numbered list of all Items currently listed for sale by the
	 * Seller.
	 * 
	 * @return
	 */
	private List<Item> doDisplayListedItems() {
		List<Item> items = new ArrayList<Item>(delegate.getItemsAvailable()
				.values());
		StringBuilder sb = new StringBuilder();

		sb.append("Your listed items (bidding is open):"
				+ System.getProperty("line.separator"));
		int count = 0;
		for (Item item : items) {
			sb.append("\t" + Integer.toString(++count) + ".) "
					+ item.toSellerString()
					+ System.getProperty("line.separator"));
		}

		if (items.size() == 0)
			sb.append("You do not have any available items listed.");

		System.out.println(sb.toString());

		return items;
	}

	/**
	 * Displays a numbered list of all Items previously listed, but now closed
	 * (i.e., sold or cancelled) by the Seller.
	 * 
	 * @return
	 */
	private List<Item> doDisplaySoldItems() {
		List<Item> items = new ArrayList<Item>(delegate.getItemsSold().values());
		StringBuilder sb = new StringBuilder();

		sb.append("Your sold items (bidding is cosed):"
				+ System.getProperty("line.separator"));
		int count = 0;
		for (Item item : items) {
			sb.append("\t" + Integer.toString(++count) + ".) "
					+ item.toSoldString()
					+ System.getProperty("line.separator"));
		}

		if (items.size() == 0)
			sb.append("You have not sold any items.");

		System.out.println(sb.toString());

		return items;
	}
}
