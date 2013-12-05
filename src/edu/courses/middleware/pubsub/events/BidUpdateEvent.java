package edu.courses.middleware.pubsub.events;

import java.io.Serializable;

/**
 * The Event published when a Seller accepts a bid on a listed Item. This Event
 * is used to modify distributed knowledge of a for sale Item's state.
 * 
 */
public class BidUpdateEvent extends Event implements Serializable {
	private static final long serialVersionUID = -8277083537239563455L;

	/** The Item's unique identifier. */
	private String itemId;
	
	/** The new high bidder's unique identifier. */
	private long buyerId;
	
	/** The new high bid. */
	private float bid;

	public BidUpdateEvent(String itemId) {
		this.itemId = itemId;
		buyerId = -1L;
		bid = -1f;
	}

	public BidUpdateEvent(String itemId, long buyerId, float bid) {
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
		if (!(event instanceof BidUpdateEvent))
			return false;
		BidUpdateEvent other = (BidUpdateEvent) event;
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
		if (!(obj instanceof BidUpdateEvent))
			return false;
		BidUpdateEvent other = (BidUpdateEvent) obj;
		if (itemId == null) {
			if (other.itemId != null)
				return false;
		} else if (!itemId.equals(other.itemId))
			return false;
		return true;
	}
}
