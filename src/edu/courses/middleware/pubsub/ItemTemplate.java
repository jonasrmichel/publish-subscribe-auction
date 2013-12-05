package edu.courses.middleware.pubsub;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * An ItemTemplate object represents a Buyer's expressed interest in items
 * possessing a particular name, particular attributes, and/or current bid
 * price/reserve within a particular range.
 * 
 * The template may be fully, partially, or not at all defined (i.e., the Buyer
 * is interested in ALL available items).
 * 
 */
public class ItemTemplate implements Serializable {
	private static final long serialVersionUID = -1491230089402511682L;

	/** Holds Item names and attributes of interest to a Buyer. */
	private Set<String> names;
	private Set<String> attributes;

	/** The minimum and maximum current bid on an Item. */
	private float bidMin;
	private float bidMax;

	/**
	 * The minimum and maximum preferred Item reserve (minimum bid the Seller
	 * will accept).
	 */
	private float reserveMin;
	private float reserveMax;

	/** Price wildcard. */
	public static final float ANY_PRICE = -1f;

	public ItemTemplate() {
		names = new HashSet<String>();
		attributes = new HashSet<String>();

		setBidMin(ANY_PRICE);
		setBidMax(ANY_PRICE);
		setReserveMin(ANY_PRICE);
		setReserveMax(ANY_PRICE);
	}

	public Set<String> getNames() {
		return names;
	}

	public void addName(String name) {
		names.add(name);
	}

	public void addNames(Set<String> names) {
		this.names.addAll(names);
	}

	public Set<String> getAttributes() {
		return attributes;
	}

	public void addAttribute(String attribute) {
		attributes.add(attribute);
	}

	public void addAttributes(Set<String> attributes) {
		this.attributes.addAll(attributes);
	}

	public float getBidMin() {
		return bidMin;
	}

	public void setBidMin(float bidMin) {
		this.bidMin = bidMin;
	}

	public float getBidMax() {
		return bidMax;
	}

	public void setBidMax(float bidMax) {
		this.bidMax = bidMax;
	}

	public float getReserveMin() {
		return reserveMin;
	}

	public void setReserveMin(float reserveMin) {
		this.reserveMin = reserveMin;
	}

	public float getReserveMax() {
		return reserveMax;
	}

	public void setReserveMax(float reserveMax) {
		this.reserveMax = reserveMax;
	}

	/**
	 * Returns true if the intersection of sets a and b are non-empty.
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	private boolean containsAny(Set<String> a, Set<String> b) {
		for (String aStr : a)
			for (String bStr : b)
				if (aStr.equals(bStr))
					return true;

		return false;
	}

	/**
	 * The matching function that determines if a Buyer's interest "matches" an
	 * available Item.
	 * 
	 * @param item
	 *            the Item in question.
	 * @return true if this template matches the provided Item.
	 */
	public boolean matches(Item item) {
		return 
				// no name or at least one matching name
				((names.isEmpty() || names.contains(item.getName()))
				// no attribute or at least one matching attribute
				|| (attributes.isEmpty() || containsAny(attributes, item.getAttributes()))
				// no price range(s) or price is in range(s)
				&& ((bidMin == ANY_PRICE || item.getBid() >= bidMin)
						&& (bidMax == ANY_PRICE || item.getBid() <= bidMax)
						&& (reserveMin == ANY_PRICE || item.getReserve() >= reserveMin) 
						&& (reserveMax == ANY_PRICE || item.getReserve() <= reserveMax)));
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((attributes == null) ? 0 : attributes.hashCode());
		result = prime * result + Float.floatToIntBits(bidMax);
		result = prime * result + Float.floatToIntBits(bidMin);
		result = prime * result + ((names == null) ? 0 : names.hashCode());
		result = prime * result + Float.floatToIntBits(reserveMax);
		result = prime * result + Float.floatToIntBits(reserveMin);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ItemTemplate other = (ItemTemplate) obj;
		if (attributes == null) {
			if (other.attributes != null)
				return false;
		} else if (!attributes.equals(other.attributes))
			return false;
		if (Float.floatToIntBits(bidMax) != Float.floatToIntBits(other.bidMax))
			return false;
		if (Float.floatToIntBits(bidMin) != Float.floatToIntBits(other.bidMin))
			return false;
		if (names == null) {
			if (other.names != null)
				return false;
		} else if (!names.equals(other.names))
			return false;
		if (Float.floatToIntBits(reserveMax) != Float
				.floatToIntBits(other.reserveMax))
			return false;
		if (Float.floatToIntBits(reserveMin) != Float
				.floatToIntBits(other.reserveMin))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ItemTemplate [names=" + names + ", attributes=" + attributes
				+ ", bidMin=" + bidMin + ", bidMax=" + bidMax + ", reserveMin="
				+ reserveMin + ", reserveMax=" + reserveMax + "]";
	}
}
