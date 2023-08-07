package framework.fmt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import list.fmt.SortByStore;

/**
 * This class represents various FMT stores
 * 
 * @author lucyb
 *
 */

public class Store {

	private int storeId;
	private String storeCode;
	private Person manager;
	private Address storeAddress;
	private List<Invoice> invoiceList;

	public Store(String storeCode, Person manager, Address storeAddress) {
		super();
		this.storeCode = storeCode;
		this.manager = manager;
		this.storeAddress = storeAddress;
		this.invoiceList = new ArrayList<>();
	}

	/**
	 * Copy constructor that includes storeId for data retrieval using primary key
	 * in a database
	 */
	public Store(int storeId, String storeCode, Person manager, Address storeAddress) {
		this(storeCode, manager, storeAddress);
		this.storeId = storeId;
	}

	public int getStoreId() {
		return storeId;
	}

	@Override
	public String toString() {
		return storeCode;
	}

	public Address getStoreAddress() {
		return storeAddress;
	}

	public List<Invoice> getInvoiceList() {
		return invoiceList;
	}

	public void addInvoice(Invoice newInvoice) {
		this.invoiceList.add(newInvoice);

	}

	public String getStoreCode() {
		return storeCode;
	}

	public Person getManager() {
		return manager;
	}

	/**
	 * Counts the number of sales made by the store by returning the size of the
	 * invoice list
	 * 
	 * @return
	 */
	public int getNumSales() {
		int numSales = invoiceList.size();
		return numSales;
	}

	/**
	 * Iterates through each Invoice in the Invoice List and adds the taxes and
	 * subtotals together to return the total amount earned by the store
	 * 
	 * @return total amount made by store
	 */
	public double getGrandTotal() {
		double grandTotal = 0.0;

		for (Invoice invoice : invoiceList) {
			grandTotal += invoice.getInvoiceTaxes() + invoice.getInvoiceSubtotal();
		}

		return grandTotal;
	}

	/**
	 * Formats all store transaction details
	 */
	public String printStoreSummary() {
		return String.format("| %-8s | %-20s | %10s | $%14.2f |", this.storeCode, this.manager.getWholeName(),
				this.getNumSales(), this.getGrandTotal());
	}

	/**
	 * checks if store code exists in the database
	 * 
	 * @param storeCode
	 * @return 0 if the store doesn't exist, or the storeId of the existing person
	 * 
	 */

	public static int getStoreByCode(String storeCode) {

		int storeId = 0;

		String query = "select s.storeId " + "from Store s " + "where s.storeCode = ? ";

		try (Connection conn = DatabaseConnection.getConnection()) {

			PreparedStatement ps = conn.prepareStatement(query);
			ps.setString(1, storeCode);
			ResultSet resultSet = ps.executeQuery();

			if (resultSet.next()) {
				storeId = resultSet.getInt("storeId");
			}
			resultSet.close();
			ps.close();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return storeId;

	}

	/**
	 * Compares the store codes of this store and the given store
	 * 
	 * @param store
	 * @return negative integer if the store code of this store comes before the
	 *         store code of the given store, a positive integer if this store code
	 *         comes after the given store code, and 0 if the store codes are equal.
	 */
	public int compareTo(Store store) {

		return this.storeCode.compareTo(store.getStoreCode());
	}

}
