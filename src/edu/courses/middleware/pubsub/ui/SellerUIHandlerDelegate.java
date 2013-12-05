package edu.courses.middleware.pubsub.ui;

import java.util.Map;
import java.util.Set;

import edu.courses.middleware.pubsub.Item;

/**
 * Defines the callbacks used by the SellerUIHandler.
 * 
 */
public interface SellerUIHandlerDelegate extends UIHandlerDelegate {

	/**
	 * Called to obtain a map of the Items currently listed for sale by the
	 * Seller.
	 * 
	 * @return
	 */
	public Map<String, Item> getItemsAvailable();

	/**
	 * Called to obtain a map of the Items sold/cancelled by the Seller.
	 * 
	 * @return
	 */
	public Map<String, Item> getItemsSold();

	/**
	 * Called to obtain the Item with the provided identifier.
	 * 
	 * @param itemId
	 * @return
	 */
	public Item getItem(String itemId);

	/**
	 * Called to create and publish the listing of a new Item with the provided
	 * name, attributes, and reserve (minimum bid).
	 * 
	 * @param name
	 * @param attributes
	 * @param reserve
	 */
	public void listAvailableItem(String name, Set<String> attributes,
			float reserve);

	/**
	 * Called to close the provided Item's auction.
	 * 
	 * @param item
	 */
	public void finalizeSale(Item item);

}
