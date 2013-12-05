package edu.courses.middleware.pubsub.events;

import java.io.Serializable;

/**
 * The Event published by Sellers when a listed Item's auction is closed,
 * finalizing the sale to the Item's current high bidder.
 * 
 */
public class SaleFinalizedEvent extends Event implements Serializable {
	private static final long serialVersionUID = 6815112369173186107L;

	/** The Item's unique identifier. */
	private String itemId;

	/** The Item's high bidder. */
	private long buyerId;

	public SaleFinalizedEvent(String itemId) {
		this.itemId = itemId;
		buyerId = -1L;
	}

	public SaleFinalizedEvent(String itemId, long buyerId) {
		this.itemId = itemId;
		this.buyerId = buyerId;
	}

	public String getItemId() {
		return itemId;
	}

	public long getBuyerId() {
		return buyerId;
	}

	@Override
	public boolean matches(Event event) {
		if (this == event)
			return true;
		if (event == null)
			return false;
		if (!(event instanceof SaleFinalizedEvent))
			return false;
		SaleFinalizedEvent other = (SaleFinalizedEvent) event;
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
		if (!(obj instanceof SaleFinalizedEvent))
			return false;
		SaleFinalizedEvent other = (SaleFinalizedEvent) obj;
		if (itemId == null) {
			if (other.itemId != null)
				return false;
		} else if (!itemId.equals(other.itemId))
			return false;
		return true;
	}

}
