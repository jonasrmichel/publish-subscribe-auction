package edu.courses.middleware.pubsub.events;

import java.io.Serializable;

/**
 * The event published by Buyers when making a bid on a listed Item.
 *
 */
public class BidEvent extends Event implements Serializable {
	private static final long serialVersionUID = 6438336536110241725L;

	/** The Item's unique identifier. */
	private String itemId;
	
	/** The bidder's unique identifier. */
	private long buyerId;
	
	/** The bid. */
	private float bid;
	
	public BidEvent(String itemId) {
		this.itemId = itemId;
		buyerId = -1L;
		bid = -1f;
	}
	
	public BidEvent(String itemId, long buyerId, float bid) {
		this.itemId = itemId;
		this.buyerId = buyerId;
		this.bid = bid;
	}
	
	public String getItemId() {
		return itemId;
	}
	
	public long getBuyerId() {
		return buyerId;
	}
	
	public float getBid() {
		return bid;
	}

	@Override
	public boolean matches(Event event) {
		if (this == event)
			return true;
		if (event == null)
			return false;
		if (!(event instanceof BidEvent))
			return false;
		BidEvent other = (BidEvent) event;
		if (itemId == null) {
			if (other.itemId != null)
				return false;
		} else if (!itemId.equals(other.itemId))
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((itemId == null) ? 0 : itemId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof BidEvent))
			return false;
		BidEvent other = (BidEvent) obj;
		if (itemId == null) {
			if (other.itemId != null)
				return false;
		} else if (!itemId.equals(other.itemId))
			return false;
		return true;
	}
}
