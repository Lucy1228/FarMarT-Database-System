package framework.fmt;

/**
 * The Purchase class is a subclass of Equipment and represents a flat equipment
 * purchase made at FMT store and a purchase price respectively
 * 
 * @author lucyb
 *
 */
public class Purchase extends Equipment {

	private double purchasePrice;

	public Purchase(String itemCode, String itemName, String model, double purchasePrice) {
		super(itemCode, itemName, model);
		this.purchasePrice = purchasePrice;

	}

	public Purchase(int itemId, String itemCode, String itemName, String model, double purchasePrice) {
		super(itemId, itemCode, itemName, model);
		this.purchasePrice = purchasePrice;

	}

	@Override
	public String toString() {
		return "(Purchase) " + getItemName() + " " + getModel();
	}

	@Override
	public double getSubtotal() {
		double subtotal = purchasePrice;
		subtotal = Math.round(subtotal * 100.0) / 100.0;
		return subtotal;
	}

	/**
	 * No taxes on purchased equipment, per FMT guidelines
	 */
	@Override
	public double getTaxes() {
		double taxes = 0.0;
		return taxes;
	}

	public double getPurchasePrice() {
		return purchasePrice;
	}

}
