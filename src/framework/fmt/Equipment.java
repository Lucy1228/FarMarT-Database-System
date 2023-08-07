package framework.fmt;

/**
 * The Equipment class is a subclass of Item and represents various equipments
 * 
 * @author lucyb
 *
 */

public class Equipment extends Item {

	protected String model;

	public Equipment(String itemCode, String itemName, String model) {
		super(itemCode, itemName);
		this.model = model;
	}

	/**
	 * Copy constructor that includes itemId for data retrieval using primary key in
	 * a database
	 */
	public Equipment(int itemId, String itemCode, String itemName, String model) {
		super(itemId, itemCode, itemName);
		this.model = model;
	}

	public String getModel() {
		return model;
	}

	/**
	 * getSubtotal and getTaxes return dummy values as they are defined in Buy/Lease
	 * subclasses respectively
	 */

	public double getSubtotal() {

		return 0.0;
	}

	public double getTaxes() {
		return 0.0;
	}
	
	/**
	 * checks if there is an item and model to enter
	 *
	 * @param itemName
	 * @param model
	 * @return
	 */
	public static boolean isValid(String itemName, String model) {
		if (itemName != null && itemName != "" && model != null && model != "") {
			return true;
		} else {
			return false;
		}

	}

}
