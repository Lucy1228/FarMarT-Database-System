package framework.fmt;

/**
 * This Product class is a subclass of Item and represents various products sold
 * at FMT stores
 * 
 * @author lucyb
 *
 */

public class Product extends Item {

	private String unit;
	private double unitPrice;
	private int quantity;
	private static double taxRate = 0.0715;

	public Product(String itemCode, String itemName, String unit, double unitPrice) {
		super(itemCode, itemName);
		this.unit = unit;
		this.unitPrice = unitPrice;
	}

	/**
	 * Copy constructor that includes itemId for data retrieval using primary key in
	 * a database
	 */
	public Product(int itemId, String itemCode, String itemName, String unit, double unitPrice) {
		super(itemId, itemCode, itemName);
		this.unit = unit;
		this.unitPrice = unitPrice;
	}

	/**
	 * Copy constructor that adds the quantity attribute to a product
	 * 
	 * @param copyProduct copies existing Product object
	 * @param quantity    amount of product
	 */
	public Product(Product copyProduct, int quantity) {
		this(copyProduct.getItemCode(), copyProduct.getItemName(), copyProduct.getUnit(), copyProduct.getUnitPrice());
		this.quantity = quantity;
	}

	@Override
	public String toString() {
		return " (Product) " + getItemName() + "\n                     " + quantity + " @ " + unitPrice + "/" + unit;
	}

	public String getUnit() {
		return unit;
	}

	public double getUnitPrice() {
		return unitPrice;
	}

	public int getQuantity() {
		return quantity;
	}

	/**
	 * Product subtotal rounded to the nearest cent per FMT guidelines
	 */
	public double getSubtotal() {
		double subtotal = getQuantity() * getUnitPrice();
		subtotal = Math.round(subtotal * 100.0) / 100.0;
		return subtotal;

	}

	/**
	 * Product taxes rounded to the nearest cent per FMT guidelines
	 */
	public double getTaxes() {
		double taxes = getSubtotal() * taxRate;
		taxes = Math.round(taxes * 100.0) / 100.0;
		return taxes;
	}

	/**
	 * checks if there is an item name and unit to enter 
	 * checks that unit price is not negative or 0 per FMT guidelines
	 * 
	 * @param itemName
	 * @return
	 */
	public static boolean isValid(String itemName, String unit, double unitPrice) {
		if (itemName != null && itemName != "" && unit != null && unit != "" && unitPrice > 0.0) {
			return true;
		} else {
			return false;
		}

	}

}
