package edu.courses.middleware.pubsub.ui;

import java.util.Map;

import edu.courses.middleware.pubsub.BuyerItem;
import edu.courses.middleware.pubsub.Item;
import edu.courses.middleware.pubsub.ItemTemplate;

/**
 * Defines the callbacks used by the BuyerUIHandler.
 * 
 */
public interface BuyerUIHandlerDelegate extends UIHandlerDelegate {

	/**
	 * Called to obtain the Buyer's unique identifier.
	 * 
	 * @return
	 */
	public long getIdentifier();

	/**
	 * Called to obtain a map of the currently listed Items of interest to the
	 * Buyer.
	 * 
	 * @return
	 */
	public Map<String, Item> getItemsOfInterest();

	/**
	 * Called to obtain a map of Items currently being bid on by the Buyer.
	 * 
	 * @return
	 */
	public Map<String, BuyerItem> getItemsBidding();

	/**
	 * Called to obtain a map of the Items purchased by the Buyer.
	 * 
	 * @return
	 */
	public Map<String, BuyerItem> getItemsPurchased();

	/**
	 * Called to obtain a map of Items whose auction the Buyer has lost.
	 * 
	 * @return
	 */
	public Map<String, BuyerItem> getItemsLost();

	/**
	 * Called to subscribe the Buyer to notifications about Items matching an
	 * ItemTemplate.
	 * 
	 * @param template
	 */
	public void subscribeInterest(ItemTemplate template);

	/**
	 * Called to publish a bid on a listed Item.
	 * 
	 * @param item
	 * @param bid
	 */
	public void publishBid(BuyerItem item, float bid);

}
