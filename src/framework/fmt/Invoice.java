package framework.fmt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import list.fmt.SortByName;

/**
 * The Invoice class represents an invoice in a store. It contains the invoice
 * code, the store code, the customer, the salesperson, the invoice date and the
 * list of items included in the invoice.
 */
public class Invoice {

	private int invoiceId;
	private String invoiceCode;
	private Store storeCode;
	private Person customer;
	private Person salesperson;
	private LocalDate date;
	private List<Item> invoiceItems;

	public Invoice(String invoiceCode, Store storeCode, Person customer, Person salesperson, LocalDate date) {
		this.invoiceCode = invoiceCode;
		this.storeCode = storeCode;
		this.customer = customer;
		this.salesperson = salesperson;
		this.date = date;
		this.invoiceItems = new ArrayList<>();
	}

	/**
	 * Copy constructor that includes invoiceId for data FMT using primary key in a
	 * database
	 */
	public Invoice(int invoiceId, String invoiceCode, Store storeCode, Person customer, Person salesperson,
			LocalDate date) {
		this(invoiceCode, storeCode, customer, salesperson, date);
		this.invoiceId = invoiceId;
	}

	public int getInvoiceId() {
		return invoiceId;
	}

	@Override
	public String toString() {
		return invoiceCode + storeCode + date + customer + salesperson;
	}

	public String getInvoiceCode() {
		return invoiceCode;
	}

	public Store getStoreCode() {
		return storeCode;
	}

	public Person getSalesperson() {
		return salesperson;
	}

	public Person getCustomer() {
		return customer;
	}

	public LocalDate getDate() {
		return date;
	}

	public void addItem(Item newItem) {
		invoiceItems.add(newItem);
	}

	public List<Item> getInvoiceItems() {
		return invoiceItems;
	}

	/**
	 * 
	 * @return number of items in the invoice items list
	 */
	public int getNumItems() {
		return invoiceItems.size();
	}

	/**
	 * Calculates total taxes for all invoices. Iterates over each Item object in
	 * the Invoice Items List and returns the tax amount for that specific item.
	 * 
	 * @return Total tax amount for all items in the invoice per FMT guidelines
	 */
	public double getInvoiceTaxes() {
		double taxes = 0.0;
		for (Item item : invoiceItems) {
			taxes += item.getTaxes();
		}
		return taxes;
	}

	/**
	 * @return subtotal for all items in invoice
	 */
	public double getInvoiceSubtotal() {
		double subtotal = 0.0;
		for (Item item : invoiceItems) {
			subtotal += item.getSubtotal();
		}
		return subtotal;
	}

	/**
	 * 
	 * @return Grand total of each invoice, including taxes
	 */
	public double getTotal() {
		double total = 0.0;
		for (Item item : invoiceItems) {
			total += (item.getSubtotal() + item.getTaxes());
		}
		return total;
	}

	/**
	 * Formats summary report for invoices
	 */
	public String printSummary() {
		return String.format("| %-10s | %-20s | %-20s | %10d | $%9.2f | $%9.2f |", getInvoiceCode(),
				getStoreCode().getStoreCode(), getCustomer().getWholeName(), getNumItems(), getInvoiceTaxes(),
				getInvoiceSubtotal() + getInvoiceTaxes());
	}

	/**
	 * Formats invoice report details
	 */
	public String printInvoiceDetails() {
		StringBuilder sb = new StringBuilder();

		sb.append("Invoice #").append(getInvoiceCode()).append("\n");
		sb.append("Store ").append(getStoreCode()).append("\n");
		sb.append("Date ").append(getDate()).append("\n");
		sb.append("Customer:\n").append(getCustomer()).append("\n\n");
		sb.append("Sales Person:\n").append(getSalesperson()).append("\n");
		sb.append(String.format("%-62s %10s%n", "Item", "Total"));
		sb.append("=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-\n");

		for (Item item : getInvoiceItems()) {
			sb.append(String.format("%-15s %-20s         $%.2f%n", item.getItemCode(), item, item.getSubtotal()));
		}
		sb.append("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=\n");
		sb.append(String.format("%65s $%10.2f%n", "Subtotal", getInvoiceSubtotal()));
		sb.append(String.format("%65s $%10.2f%n", "Tax", getInvoiceTaxes()));
		sb.append(String.format("%65s $%10.2f%n", "Grand Total", getInvoiceSubtotal() + getInvoiceTaxes()));
		sb.append("\n");

		return sb.toString();
	}

	/**
	 * Formats invoices sorted by Customer last/first names
	 * 
	 * @return
	 */
	public String printSorted() {
		return String.format("| %-10s | %-20s | %-20s | %17s | %20.2f |\n", getInvoiceCode(),
				getStoreCode().getStoreCode(), getCustomer().getWholeName(), getSalesperson().getWholeName(),
				getTotal());

	}

	/**
	 * checks if invoice code exists in the database
	 * 
	 * @param invoice code
	 * @return 0 if the invoice doesn't exist, or the invoiceId of the existing
	 *         invoice
	 */
	public static int getInvoiceByCode(String invoiceCode) {

		int invoiceId = 0;

		String query = "select i.invoiceId " + "from Invoice i " + "where i.invoiceCode = ? ";

		try (Connection conn = DatabaseConnection.getConnection()) {

			PreparedStatement ps = conn.prepareStatement(query);
			ps.setString(1, invoiceCode);
			ResultSet resultSet = ps.executeQuery();

			if (resultSet.next()) {
				invoiceId = resultSet.getInt("invoiceId");
			}
			resultSet.close();
			ps.close();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return invoiceId;

	}

	/**
	 * checks if there is a store code, customerr code or salesperson code to enter
	 * 
	 * @param storeCode
	 * @param customerCode
	 * @param salespersonCode
	 * @return
	 */
	public static boolean isValid(String storeCode, String customerCode, String salespersonCode) {
		if (storeCode != null && storeCode != "" && customerCode != null && customerCode != ""
				&& salespersonCode != null && salespersonCode != "") {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * checks if the date passed in is in the correct format
	 * 
	 * @param date
	 * @return
	 */
	public static boolean isValidDateFormat(String date) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		try {
			LocalDate.parse(date, formatter);
			return true;
		} catch (DateTimeParseException e) {
			return false;
		}
	}

	/**
	 * Compares this Invoice object with the specified Invoice object for order. The
	 * comparison is based on the total value of the invoices.
	 * 
	 * @param invoice
	 * @return positive, negative or zero integer based whether the Invoice object
	 *         is greater, less than or equal to the specified Invoice object
	 */
	public int compareTo(Invoice invoice) {
		return Double.compare(this.getTotal(), invoice.getTotal());
	}

}
