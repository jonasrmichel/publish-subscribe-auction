package edu.courses.middleware.pubsub;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * An Item represents distributed knowledge of an item for sale and its state in
 * the auction system.
 * 
 * Item state can only be modified by the listing Seller.
 * 
 */
public class Item implements Serializable {
	private static final long serialVersionUID = -8704445700393510628L;

	/** A globally unique identifier. */
	protected String id;

	/** A short textual description of the item. */
	protected String name;

	/** A set of attributes describing the item. */
	protected Set<String> attributes;

	/** The minimum bid. */
	protected float reserve;

	/** The current bid. */
	protected float bid;

	/** The highest bidding buyer. */
	protected long buyerId;

	protected Item(String id, String name, Set<String> attributes,
			float reserve, float bid, long buyerId) {
		this.id = id;
		this.name = name;
		this.attributes = attributes;
		this.reserve = reserve;
		this.bid = bid;
		this.buyerId = buyerId;
	}

	public Item(long sellerId, int itemNumber, String name,
			Set<String> attributes, float reserve) {
		// generate the unique identifier
		this.id = Long.toString(sellerId) + ":" + Integer.toString(itemNumber);
		
		this.name = name;
		this.attributes = attributes;
		this.reserve = reserve;

		setBid(0f);
		setBuyerId(-1);
	}

	public Item(long sellerId, int itemNumber, String name, float reserve) {
		this(sellerId, itemNumber, name, new HashSet<String>(), reserve);
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
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

	public float getReserve() {
		return reserve;
	}

	public float getBid() {
		return bid;
	}

	public void setBid(float bid) {
		this.bid = bid;
	}

	public long getBuyerId() {
		return buyerId;
	}

	public void setBuyerId(long buyerId) {
		this.buyerId = buyerId;
	}
	
	public String toBuyerString() {
		String highestBidder = buyerId == -1 ? "none" : "Buyer-#"
				+ Long.toString(buyerId);

		return "Item [name=" + name + ", attributes=" + attributes.toString()
				+ ", current bid=$" + Settings.formatter.format(bid)
				+ ", highgest bidder=" + highestBidder + "]";
	}

	public String toSellerString() {
		String highestBidder = buyerId == -1 ? "none" : "Buyer-#"
				+ Long.toString(buyerId);

		return "Item [name=" + name + ", attributes=" + attributes.toString()
				+ ", reserve=$" + Settings.formatter.format(reserve)
				+ ", current bid=$" + Settings.formatter.format(bid)
				+ ", highgest bidder=" + highestBidder + "]";
	}

	public String toSoldString() {
		String buyer = buyerId == -1 ? "none (auction was cancelled)"
				: "Buyer-#" + Long.toString(buyerId);

		return "Item [name: " + name + ", attributes: " + attributes.toString()
				+ ", reserve: $" + Settings.formatter.format(reserve)
				+ ", selling price: $" + Settings.formatter.format(bid)
				+ ", buyer: " + buyer + "]";
	}

	@Override
	public String toString() {
		return "Item [id=" + id + ", name=" + name + ", attributes="
				+ attributes.toString() + ", reserve="
				+ Settings.formatter.format(reserve) + ", bid="
				+ Settings.formatter.format(bid) + ", buyerId="
				+ Long.toString(buyerId) + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((attributes == null) ? 0 : attributes.hashCode());
		result = prime * result + Float.floatToIntBits(bid);
		result = prime * result + (int) (buyerId ^ (buyerId >>> 32));
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + Float.floatToIntBits(reserve);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Item))
			return false;
		Item other = (Item) obj;
		if (attributes == null) {
			if (other.attributes != null)
				return false;
		} else if (!attributes.equals(other.attributes))
			return false;
		if (Float.floatToIntBits(bid) != Float.floatToIntBits(other.bid))
			return false;
		if (buyerId != other.buyerId)
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (Float.floatToIntBits(reserve) != Float
				.floatToIntBits(other.reserve))
			return false;
		return true;
	}
}
