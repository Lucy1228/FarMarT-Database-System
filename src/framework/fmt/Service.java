package framework.fmt;

/**
 * The Service class represents various Services at FMT stores. It is a subclass
 * of Item
 * 
 * @author lucyb
 *
 */

public class Service extends Item {

	private double hourlyRate;
	private double hours;
	private static double taxRate = 0.0345;

	public Service(String itemCode, String itemName, double hourlyRate) {
		super(itemCode, itemName);
		this.hourlyRate = hourlyRate;
	}

	/**
	 * Copy constructor that includes itemId for data retrieval using primary key in
	 * a database
	 */
	public Service(int itemId, String itemCode, String itemName, double hourlyRate) {
		super(itemId, itemCode, itemName);
		this.hourlyRate = hourlyRate;
	}

	/**
	 * Copy constructor that adds the hours of the Service requested
	 * 
	 * @param copyService copies existing service object
	 * @param hours       the number of hours the service will be used for
	 */

	public Service(Service copyService, double hours) {
		this(copyService.getItemCode(), copyService.getItemName(), copyService.getHourlyRate());
		this.hours = hours;

	}

	@Override
	public String toString() {
		return " (Service) " + getItemName() + "\n                 " + hours + " hours @ " + "$" + hourlyRate + "/hr";
	}

	double getHourlyRate() {
		return hourlyRate;
	}

	public double getHours() {
		return hours;
	}

	/**
	 * Subtotal rounded to the nearest cent per FMT guidelines
	 */
	public double getSubtotal() {
		double subtotal = hourlyRate * hours;
		subtotal = Math.round(subtotal * 100.0) / 100.0;
		return subtotal;
	}

	/**
	 * Taxes rounded to the nearest cent per FMT guidelines
	 */

	public double getTaxes() {
		double taxes = getSubtotal() * taxRate;
		taxes = Math.round(taxes * 100.0) / 100.0;
		return taxes;

	}

	/**
	 * checks if there is valid service to add that is not empty or null. checks the
	 * hourly rate is not negative or 0 per FMT guidelines
	 * 
	 * @param itemName
	 * @param hourlyRate
	 * @return
	 */
	public static boolean isValid(String itemName, double hourlyRate) {
		if (itemName != null && itemName != "" && hourlyRate > 0.0) {
			return true;
		} else {
			return false;
		}

	}

}
