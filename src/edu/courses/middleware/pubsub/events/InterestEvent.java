package edu.courses.middleware.pubsub.events;

import edu.courses.middleware.pubsub.ItemTemplate;

/**
 * The Event published by Buyers when expressing interest in listed Item
 * characteristics.
 * 
 */
public class InterestEvent extends Event {
	private static final long serialVersionUID = -1223597402558547711L;

	/** The Buyer's Item preferences. */
	private ItemTemplate template;

	public InterestEvent(ItemTemplate template) {
		this.template = template;
	}

	public ItemTemplate getItemTemplate() {
		return template;
	}

	@Override
	public boolean matches(Event event) {
		if (event == null)
			return false;
		if (!(event instanceof AvailableItemEvent))
			return false;
		AvailableItemEvent availableItem = (AvailableItemEvent) event;
		if (!template.matches(availableItem.getItem()))
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((template == null) ? 0 : template.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof InterestEvent))
			return false;
		InterestEvent other = (InterestEvent) obj;
		if (template == null) {
			if (other.template != null)
				return false;
		} else if (!template.equals(other.template))
			return false;
		return true;
	}

}
