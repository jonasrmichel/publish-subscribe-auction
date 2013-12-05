package edu.courses.middleware.pubsub;

/**
 * A BuyerItem extends the Item class adding some extra Buyer-specific metadata
 * (e.g., for automatic bidding) and toString() formats.
 * 
 * An instance of a BuyerItem is only read from / written to by a single Buyer
 * (i.e., the metadata is not shared).
 * 
 */
public class BuyerItem extends Item {
	private static final long serialVersionUID = 4597449229196409079L;

	/** The automatic bid increment. */
	private float increment;

	/** The automatic maximum bid. */
	private float max;

	public static enum BuyingMode {
		USER_DRIVEN, AUTOMATIC
	};

	/** The buyer-mode of the Buyer. */
	private BuyingMode mode;

	/** The most recent bid by the Buyer. */
	private float lastBid;

	public BuyerItem(Item item, float increment, float max, BuyingMode mode) {
		super(item.getId(), item.getName(), item.getAttributes(), item
				.getReserve(), item.getBid(), item.getBuyerId());

		this.increment = increment;
		this.max = max;
		this.mode = mode;
	}

	public BuyingMode getMode() {
		return mode;
	}

	public float getLastBid() {
		return lastBid;
	}

	public void setLastBid(float bid) {
		this.lastBid = bid;
	}

	public float getIncrement() {
		return increment;
	}

	public float getMax() {
		return max;
	}

	public void setMax(float max) {
		this.max = max;
	}

	public String toBuyerString() {
		String highestBidder = buyerId == -1 ? "none" : "Buyer-#"
				+ Long.toString(buyerId);

		String modeStr = mode.equals(BuyingMode.USER_DRIVEN) ? "User-driven"
				: "Auto";

		String itemStr = null;
		if (mode.equals(BuyingMode.USER_DRIVEN)) {
			itemStr = "BuyerItem [name: " + name + ", attributes: "
					+ attributes.toString() + ", current bid: $"
					+ Settings.formatter.format(bid) + ", highest bidder: "
					+ highestBidder + ", buying mode: " + modeStr
					+ ", your most recent bid: $"
					+ Settings.formatter.format(lastBid) + "]";
		} else {
			itemStr = "BuyerItem [name: " + name + ", attributes: "
					+ attributes.toString() + ", current bid: $"
					+ Settings.formatter.format(bid) + ", highest bidder: "
					+ highestBidder + ", auto bid increment: "
					+ Settings.formatter.format(increment) + ", buying mode: "
					+ modeStr + ", your most recent bid: $"
					+ Settings.formatter.format(lastBid) + "]";
		}

		return itemStr;
	}

	public String toPurchasedString() {
		String buyer = buyerId == -1 ? "none (auction was cancelled)"
				: "Buyer-#" + Long.toString(buyerId);

		return "BuyerItem [name: " + name + ", attributes: "
				+ attributes.toString() + ", purchase price: $"
				+ Settings.formatter.format(bid) + ", buyer: " + buyer + "]";
	}

	@Override
	public String toString() {
		String modeStr = mode.equals(BuyingMode.USER_DRIVEN) ? "User-driven"
				: "Auto";

		return "BuyerItem [id=" + id + ", name=" + name + ", attributes="
				+ attributes.toString() + ", reserve="
				+ Settings.formatter.format(reserve) + ", bid="
				+ Settings.formatter.format(bid) + ", buyerId="
				+ Long.toString(buyerId) + ", increment=" + increment
				+ ", mode=" + modeStr + ", lastBid=" + lastBid + "]";
	}
}
