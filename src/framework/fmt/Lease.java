package framework.fmt;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Lease class represents various leases in FMT stores, including start date, end
 * date and when the lease was signed
 */
public class Lease extends Equipment {
	private LocalDate startDate;
	private LocalDate endDate;
	private double fee;

	public Lease(String itemCode, String itemName, String model, double fee, LocalDate startDate, LocalDate endDate) {
		super(itemCode, itemName, model);
		this.startDate = startDate;
		this.endDate = endDate;
		this.fee = fee;
	}

	/**
	 * Copy constructor that includes itemId for data retrieval using primary key in
	 * a database
	 */
	public Lease(int itemId, String itemCode, String itemName, String model, double fee, LocalDate startDate,
			LocalDate endDate) {
		super(itemId, itemCode, itemName, model);
		this.startDate = startDate;
		this.endDate = endDate;
		this.fee = fee;
	}

	@Override
	public String toString() {
		return "(Lease) " + getItemName() + "-" + getModel() + "\n                " + getLeaseDays() + " days ("
				+ startDate + "-> " + endDate + ")" + " @ " + fee + "/ 30 days";
	}

	public LocalDate getStartDate() {
		return startDate;
	}

	public LocalDate getEndDate() {
		return endDate;
	}

	/**
	 * Returns the monthly fee for the lease
	 * 
	 * @return monthly fee
	 */
	public double getFee() {
		return fee;
	}

	/**
	 * Returns subtotal of lease rounded to the nearest cent per FMT guidelines
	 * 
	 * @return subtotal
	 */
	public double getSubtotal() {
		double subtotal = 0.0;
		double leasePeriod = (double) getLeaseDays() / 30;
		subtotal = getFee() * leasePeriod;
		subtotal = Math.round(subtotal * 100.0) / 100.0;

		return subtotal;
	}

	/**
	 * Returns the number of days the lease is signed for. Added one to number of
	 * days to account for FMT guidelines of including both start and end date in
	 * the lease
	 * 
	 * @return number of days in lease
	 */
	public long getLeaseDays() {
		long days = (ChronoUnit.DAYS.between(startDate, endDate)) + 1;
		return days;
	}

	/**
	 * Flat tax rate based on lease subtotal per FMT guidelines
	 */
	public double getTaxes() {
		double taxes = 0.0;
		if ((getSubtotal() >= 10000) && (getSubtotal() <= 100000)) {
			taxes = 500.0;
		} else if (getSubtotal() > 100000) {
			taxes = 1500.0;
		} else {
			taxes = 0.0;
		}
		return taxes;
	}

}
