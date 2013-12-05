package edu.courses.middleware.pubsub;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import edu.courses.middleware.pubsub.BuyerItem.BuyingMode;
import edu.courses.middleware.pubsub.events.AvailableItemEvent;
import edu.courses.middleware.pubsub.events.BidEvent;
import edu.courses.middleware.pubsub.events.BidUpdateEvent;
import edu.courses.middleware.pubsub.events.InterestEvent;
import edu.courses.middleware.pubsub.events.SaleFinalizedEvent;
import edu.courses.middleware.pubsub.ui.BuyerUIHandler;
import edu.courses.middleware.pubsub.ui.BuyerUIHandlerDelegate;

/**
 * A Buyer is a client in the distributed auction system. A user controls a
 * Buyer through a command line interface.
 * 
 * Buyers explicitly subscribe to "publications" about items/auctions that match
 * their expressed interests. A Buyer may bid on an item of interest in one of
 * two modes: user-driven (user must manually re-bid if outbid) and automatic
 * (Buyer will automatically re-bid, up to a limit, if outbid on an item).
 * 
 */
public class Buyer implements BuyerServerHandlerDelegate,
		BuyerUIHandlerDelegate {

	/** A unique identifier (assigned by a Broker). */
	private long identifier = -1L;

	/** Manages the network connection to the Broker tree. */
	private BuyerServerHandler serverHandler;

	/** Manages the user interface thread. */
	private BuyerUIHandler uiHandler;

	/** Holds the Items matching the Buyer's subscribed interests. */
	private Map<String, Item> itemsOfInterest;

	/** Holds the Items in whose auctions the Buyer is participating. */
	private Map<String, BuyerItem> itemsBidding;

	/** Holds the Items the Buyer has purchased. */
	private Map<String, BuyerItem> itemsPurchased;

	/** Holds the Items the Buyer has bid on and lost. */
	private Map<String, BuyerItem> itemsLost;

	public Buyer() {
		serverHandler = new BuyerServerHandler(this);
		uiHandler = new BuyerUIHandler(this);

		itemsOfInterest = new ConcurrentHashMap<String, Item>();
		itemsBidding = new ConcurrentHashMap<String, BuyerItem>();
		itemsPurchased = new ConcurrentHashMap<String, BuyerItem>();
		itemsLost = new ConcurrentHashMap<String, BuyerItem>();
	}

	/* BuyerUIHandlerDelegate Interface Implementation */

	@Override
	public Map<String, Item> getItemsOfInterest() {
		return itemsOfInterest;
	}

	@Override
	public Map<String, BuyerItem> getItemsBidding() {
		return itemsBidding;
	}

	@Override
	public Map<String, BuyerItem> getItemsPurchased() {
		return itemsPurchased;
	}

	@Override
	public Map<String, BuyerItem> getItemsLost() {
		return itemsLost;
	}

	@Override
	public void subscribeInterest(ItemTemplate template) {
		InterestEvent event = new InterestEvent(template);
		serverHandler.sendEvent(event);
	}

	@Override
	public void publishBid(BuyerItem item, float bid) {
		// remove this item from the of-interest Items (if necessary)
		if (itemsOfInterest.containsKey(item.getId()))
			itemsOfInterest.remove(item.getId());

		// keep track of our bid on this Item
		item.setLastBid(bid);

		// keep track that we've placed a bid on this Item
		if (!itemsBidding.containsKey(item.getId()))
			itemsBidding.put(item.getId(), item);

		// announce the bid
		BidEvent event = new BidEvent(item.getId(), identifier, bid);
		serverHandler.sendEvent(event);
	}

	/* BuyerServerHandlerDelegate Interface Implementation */

	@Override
	public long getIdentifier() {
		return identifier;
	}

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
		System.out.println("Closing Buyer.");
		System.exit(0);
	}

	@Override
	public void receivedAvailableItem(AvailableItemEvent event) {
		Item item = event.getItem();

		// quit now if we already have a copy of this item
		if (itemsOfInterest.containsKey(item.getId()))
			return;

		// keep track of this Item in the interest map
		itemsOfInterest.put(item.getId(), item);

		// alert the user
		uiHandler.deliver(event);
	}

	@Override
	public void recievedBidUpdate(BidUpdateEvent event) {
		if (itemsOfInterest.containsKey(event.getItemId())) {
			// update our local view of the Item
			Item item = itemsOfInterest.get(event.getItemId());
			item.setBuyerId(event.getBuyerId());
			item.setBid(event.getBid());

		} else if (itemsBidding.containsKey(event.getItemId())) {
			// update our local view of the Item
			BuyerItem item = itemsBidding.get(event.getItemId());
			item.setBuyerId(event.getBuyerId());
			item.setBid(event.getBid());

			// alert the user
			uiHandler.deliver(event);

			if (identifier != event.getBuyerId()) {
				// we've been outbid!
				if (item.getMode().equals(BuyingMode.AUTOMATIC)) {
					// automatically re-bid if we can
					float bid = event.getBid() + item.getIncrement();
					if (bid <= item.getMax()) {
						// re-bid with the auto increment
						publishBid(item, bid);
						
					} else {
						// re-bid at the maximum
						publishBid(item, item.getMax());
						
					}
				}
			}
		}
	}

	@Override
	public void receivedSaleFinalized(SaleFinalizedEvent event) {
		if (itemsOfInterest.containsKey(event.getItemId())) {
			itemsOfInterest.remove(event.getItemId());

		} else if (itemsBidding.containsKey(event.getItemId())) {
			// alert the user
			uiHandler.deliver(event);

			BuyerItem item = itemsBidding.remove(event.getItemId());

			if (identifier == event.getBuyerId()) {
				// we won!
				itemsPurchased.put(item.getId(), item);
			} else {
				// we lost :(
				itemsLost.put(item.getId(), item);
			}
		}
	}

	/* BuyerUIHandlerDelegate Interface Implementation */

	@Override
	public void quit() {
		// TODO
	}

	public static void main(String[] args) {
		new Buyer();
	}

}
