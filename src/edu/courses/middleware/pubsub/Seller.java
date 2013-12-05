package edu.courses.middleware.pubsub;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import edu.courses.middleware.pubsub.events.AvailableItemEvent;
import edu.courses.middleware.pubsub.events.BidEvent;
import edu.courses.middleware.pubsub.events.BidUpdateEvent;
import edu.courses.middleware.pubsub.events.SaleFinalizedEvent;
import edu.courses.middleware.pubsub.ui.SellerUIHandler;
import edu.courses.middleware.pubsub.ui.SellerUIHandlerDelegate;

/**
 * A Seller is a client in the distributed auction system. A user controls a
 * Seller through a command line interface.
 * 
 * Sellers may list Items for sale and close their auctions (end bidding)
 * whenever desired. A Seller is automatically subscribed to all bid updates on
 * listed items. However, the user is only notified when a listed Item receives
 * a new high bid.
 * 
 */
public class Seller implements SellerServerHandlerDelegate,
		SellerUIHandlerDelegate {

	/** A unique identifier (assigned by a Broker). */
	private long identifier = -1L;

	/** Manages the network connection to the Broker tree. */
	private SellerServerHandler serverHandler;

	/** Manages the user interface thread. */
	private SellerUIHandler uiHandler;

	/**
	 * The a running count of the number of listed Items (used for generating
	 * Item identifiers).
	 */
	private int itemCount;

	/** Holds the Items currently listed by the Seller. */
	private Map<String, Item> itemsAvailable;

	/** Holds the Items sold by the Seller. */
	private Map<String, Item> itemsSold;

	public Seller() {
		// configure the system's decimal formatter if necessary
		// Settings.formatter.setMaximumFractionDigits(2);

		identifier = System.currentTimeMillis();
		serverHandler = new SellerServerHandler(this);
		uiHandler = new SellerUIHandler(this);

		itemCount = 0;
		itemsAvailable = new ConcurrentHashMap<String, Item>();
		itemsSold = new ConcurrentHashMap<String, Item>();
	}

	/**
	 * Lists a new Item as available and open for bidding.
	 * 
	 * @param item
	 */
	public void publishAvailableItem(Item item) {
		// increment the count of listed Items
		itemCount++;

		// keep track that we've listed this Item
		itemsAvailable.put(item.getId(), item);

		// announce the Item's availability
		AvailableItemEvent event = new AvailableItemEvent(item);
		serverHandler.sendEvent(event);
	}

	/**
	 * Publishes a bid update for a particular Item.
	 * 
	 * @param item
	 */
	public void publishBidUpdate(Item item) {
		BidUpdateEvent event = new BidUpdateEvent(item.getId(),
				item.getBuyerId(), item.getBid());
		serverHandler.sendEvent(event);
	}

	/**
	 * Closes bidding and ends the auction on a listed Item. The Item will be
	 * sold to the highest bidder or marked as "cancelled" if the Item's reserve
	 * has not been met.
	 * 
	 * @param item
	 */
	public void publishSaleFinalized(Item item) {
		SaleFinalizedEvent event = new SaleFinalizedEvent(item.getId(),
				item.getBuyerId());
		serverHandler.sendEvent(event);
	}

	/* SellerUIHandlerDelegate Interface Implementation */

	@Override
	public Map<String, Item> getItemsAvailable() {
		return itemsAvailable;
	}

	@Override
	public Map<String, Item> getItemsSold() {
		return itemsSold;
	}

	@Override
	public Item getItem(String itemId) {
		return itemsAvailable.get(itemId);
	}

	@Override
	public void listAvailableItem(String name, Set<String> attributes,
			float reserve) {
		// create the Item object
		Item item = new Item(identifier, itemCount, name, attributes, reserve);

		// publish its availability
		publishAvailableItem(item);
	}

	@Override
	public void finalizeSale(Item item) {
		// the Item is no longer available
		itemsAvailable.remove(item.getId());

		// it has been sold!
		itemsSold.put(item.getId(), item);

		// announce the sale
		publishSaleFinalized(item);
	}

	/* SellerServerHandlerDelegate Interface Implementation */

	@Override
	public void connected(long identifier, ServerHandler handler) {
		// the server has assigned us a unique identifier
		this.identifier = identifier;
		
		// now we can start interacting with our sever and user
		serverHandler.start();
		uiHandler.start();
	}

	@Override
	public void close(ServerHandler handler) {
		System.out.println("Closing Seller.");
		System.exit(0);
	}

	@Override
	public void receivedBid(BidEvent event) {
		Item item = itemsAvailable.get(event.getItemId());

		// ensure this Item is still available
		if (item == null)
			return;

		// ensure the bid price exceeds both the Item's reserve and current bid
		// (at the penny level)
		if (Math.round(event.getBid() * 100.0) / 100.0 < item.getReserve()
				|| Math.round(event.getBid() * 100.0) / 100.0 <= item.getBid())
			return;

		// new highest bid!
		item.setBid(event.getBid()); // update the current bid
		item.setBuyerId(event.getBuyerId()); // keep track of the highest bidder

		// alert the user
		uiHandler.deliver(event);

		// announce the new bid price
		publishBidUpdate(item);
	}

	/* SellerUIHandlerDelegate Interface Implementation */

	@Override
	public void quit() {
		close(null);
	}

	public static void main(String[] args) {
		new Seller();
	}
}
