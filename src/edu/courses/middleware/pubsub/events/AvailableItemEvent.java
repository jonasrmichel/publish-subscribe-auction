package edu.courses.middleware.pubsub.events;

import java.io.Serializable;

import edu.courses.middleware.pubsub.Item;

/**
 * The Event published by Sellers when listing a new Item for sale.
 *
 */
public class AvailableItemEvent extends Event implements Serializable {
	private static final long serialVersionUID = -8922404650884158993L;
	
	/** The Item for sale. */
	private Item item;
	
	public AvailableItemEvent(Item item) {
		this.item = item;
	}
	
	public Item getItem() {
		return item;
	}
	
	@Override
	public boolean matches(Event event) {
		if (this == event)
			return true;
		if (event == null)
			return false;
		if (!(event instanceof AvailableItemEvent))
			return false;
		AvailableItemEvent other = (AvailableItemEvent) event;
		if (item == null) {
			if (other.item != null)
				return false;
		} else if (!item.equals(other.item))
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((item == null) ? 0 : item.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof AvailableItemEvent))
			return false;
		AvailableItemEvent other = (AvailableItemEvent) obj;
		if (item == null) {
			if (other.item != null)
				return false;
		} else if (!item.equals(other.item))
			return false;
		return true;
	}
}