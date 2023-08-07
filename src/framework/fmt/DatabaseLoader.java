package framework.fmt;

import java.sql.Connection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * The DatabaseLoader class is responsible for loading data from a database. It
 * has methods for loading different entities such as people, items, stores and
 * invoices and then stores them in their corresponding maps for easy and
 * flexible data retrieval
 * 
 * @author lucyb
 *
 */
public class DatabaseLoader {

	public static Map<Integer, Person> personMap;
	public static Map<Integer, Item> itemMap;
	public static Map<Integer, Store> storeMap;
	public static Map<Integer, Invoice> invoiceMap;

	/**
	 * This static block initializes static fields and loads data from the database
	 * into memory organized via maps. Ensures data loading only happens once and
	 * for easy data access
	 */

	static {

		personMap = new HashMap<>();
		itemMap = new HashMap<>();
		storeMap = new HashMap<>();
		invoiceMap = new HashMap<>();

		loadPeople();
		loadItem();
		loadStore();
		loadInvoice();
		loadInvoiceItem();
	}

	/**
	 * Loads an address with the given address Id
	 * 
	 * @param addressId
	 * @param conn      to avoid opening and closing multiple connections
	 * @return address object representing the loaded address
	 */
	public static Address loadAddress(int addressId, Connection conn) {

		Address address = null;

		try {

			String query = "select a.addressId, a.street, a.city, a.state, a.zip, a.country " 
					+ "from Address a "
					+ "where a.addressId = ? ";

			PreparedStatement ps = conn.prepareStatement(query);
			ps.setInt(1, addressId);
			ResultSet resultSet = ps.executeQuery();

			if (resultSet.next()) {
				String street = resultSet.getString("street");
				String city = resultSet.getString("city");
				String state = resultSet.getString("state");
				String zip = resultSet.getString("zip");
				String country = resultSet.getString("country");

				address = new Address(street, city, state, zip, country);
			}
			resultSet.close();
			ps.close();

		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

		return address;

	}

	/**
	 * Loads all people from the database and populates the person map. Uses
	 * loadAddress method to get the address associated with the person Id
	 * CurrentPersonId is used to keep track of the last personId in the database in
	 * order to not create multiple instances of the same person. Ensures all emails
	 * are added to the correct person without creating duplicates or data being
	 * overwritten
	 */
	public static void loadPeople() {

		Connection conn = null;
		Person personObject = null;

		try {
			conn = DatabaseConnection.getConnection();
			String query = "select p.personId, p.personCode, p.firstname, p.lastName, p.addressId, e.emailAddress "
					+ "from Person p " 
					+ "join Address a ON p.addressId = a.addressId "
					+ "left join Email e ON p.personId = e.personId ";
			PreparedStatement ps = conn.prepareStatement(query);
			ResultSet resultSet = ps.executeQuery();

			int currentPersonId = 0;

			while (resultSet.next()) {
				int personId = resultSet.getInt("personId");

				if (currentPersonId != personId) {
					String personCode = resultSet.getString("personCode");
					String firstName = resultSet.getString("firstName");
					String lastName = resultSet.getString("lastName");
					int addressId = resultSet.getInt("addressId");
					Address address = loadAddress(addressId, conn);

					personObject = new Person(personId, personCode, lastName, firstName, address);
					personMap.put(personId, personObject);
					currentPersonId = personId;
				}

				String email = resultSet.getString("emailAddress");
				if (email != null) {
					personMap.get(personId).addEmail(email);
				}
			}
			resultSet.close();
			ps.close();
			conn.close();
		} catch (

		SQLException e) {
			throw new RuntimeException(e);
		}

	}

	/**
	 * Loads all items from the database, creates item objects based on type and
	 * populates the item map.
	 */
	public static void loadItem() {

		Connection conn = null;
		Item itemObject = null;

		try {
			conn = DatabaseConnection.getConnection();
			String query = "select itemId, itemCode, itemName, type, hourlyRate, unit, unitPrice, model "
					+ "from Item ";
			PreparedStatement ps = conn.prepareStatement(query);
			ResultSet resultSet = ps.executeQuery();

			while (resultSet.next()) {

				int itemId = resultSet.getInt("itemId");
				String itemCode = resultSet.getString("itemCode");
				String itemName = resultSet.getString("itemName");
				String type = resultSet.getString("type");
				double hourlyRate = resultSet.getDouble("hourlyRate");
				String unit = resultSet.getString("unit");
				double unitPrice = resultSet.getDouble("unitPrice");
				String model = resultSet.getString("model");

				if (type.equals("Equipment")) {
					itemObject = new Equipment(itemId, itemCode, itemName, model);
				} else if (type.equals("Product")) {
					itemObject = new Product(itemId, itemCode, itemName, unit, unitPrice);
				} else if (type.equals("Service")) {
					itemObject = new Service(itemId, itemCode, itemName, hourlyRate);
				}

				itemMap.put(itemId, itemObject);
			}
			resultSet.close();
			ps.close();
			conn.close();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Loads all stores from the database, creates store objects, and populates the
	 * store map.Uses loadAddress method to get the address associated with the
	 * store Id
	 */
	public static void loadStore() {

		Connection conn = null;
		Store storeObject = null;

		try {

			conn = DatabaseConnection.getConnection();
			String query = "select a.addressId, s.storeId, s.storeCode, s.managerId " 
					+ "from Store s "
					+ "join Address a on s.addressId = a.addressId ";
			PreparedStatement ps = conn.prepareStatement(query);
			ResultSet resultSet = ps.executeQuery();

			while (resultSet.next()) {

				int storeId = resultSet.getInt("storeId");
				String storeCode = resultSet.getString("storeCode");
				int managerId = resultSet.getInt("managerId");
				int addressId = resultSet.getInt("addressId");
				Person manager = personMap.get(managerId);
				Address storeAddress = loadAddress(addressId, conn);
				storeObject = new Store(storeId, storeCode, manager, storeAddress);
				storeMap.put(storeId, storeObject);
			}
			resultSet.close();
			ps.close();
			conn.close();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Loads all invoices from the database, creates invoice objects, and populates
	 * the invoice map.
	 */
	public static void loadInvoice() {

		Connection conn = null;
		Invoice invoiceObject = null;

		try {

			conn = DatabaseConnection.getConnection();
			String query = "select i.invoiceId, i.invoiceCode, i.date, i.customerId, i.salespersonId, i.storeId "
					+ "from Invoice i";
			PreparedStatement ps = conn.prepareStatement(query);
			ResultSet resultSet = ps.executeQuery();

			while (resultSet.next()) {

				int invoiceId = resultSet.getInt("invoiceId");
				String invoiceCode = resultSet.getString("invoiceCode");
				LocalDate date = LocalDate.parse(resultSet.getString("date"));
				int customerId = resultSet.getInt("customerId");
				int salespersonId = resultSet.getInt("salespersonId");
				int storeId = resultSet.getInt("storeId");
				Person customer = personMap.get(customerId);
				Person salesperson = personMap.get(salespersonId);
				Store store = storeMap.get(storeId);
				invoiceObject = new Invoice(invoiceId, invoiceCode, store, customer, salesperson, date);
				invoiceMap.put(invoiceId, invoiceObject);
				storeMap.get(storeId).addInvoice(invoiceObject);
			}
			resultSet.close();
			ps.close();
			conn.close();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Loads all invoice items from the database, creates invoice item objects based
	 * on type of item by referencing the item map, and then adds them to their
	 * corresponding invoices by referencing the invoice map
	 */
	public static void loadInvoiceItem() {

		Connection conn = null;
		Item invoiceItemObject = null;

		try {

			conn = DatabaseConnection.getConnection();
			String query = "select i.itemId, i.itemCode, i.model, i.type, i.itemName, ii.eStatus, ii.invoiceItemId, ii.invoiceId, ii.startDate, ii.endDate, ii.quantity, ii.hours, ii.fee, ii.purchasePrice "
					+ "from Item i " 
					+ "join InvoiceItem ii on ii.itemId = i.itemId";
			PreparedStatement ps = conn.prepareStatement(query);
			ResultSet resultSet = ps.executeQuery();

			while (resultSet.next()) {

				int invoiceId = resultSet.getInt("invoiceId");
				int itemId = resultSet.getInt("itemId");
				String itemCode = resultSet.getString("itemCode");
				String itemName = resultSet.getString("itemName");
				String type = resultSet.getString("type");
				String eStatus = resultSet.getString("eStatus");
				Item item = itemMap.get(itemId);

				if (type.equals("Equipment")) {
					if (eStatus.equals("Leased")) {
						LocalDate startDate = LocalDate.parse(resultSet.getString("startDate"));
						LocalDate endDate = LocalDate.parse(resultSet.getString("endDate"));
						int fee = resultSet.getInt("fee");
						String model = resultSet.getString("model");
						invoiceItemObject = new Lease(itemId, itemCode, itemName, model, fee, startDate, endDate);
					} else {
						String model = resultSet.getString("model");
						Double purchasePrice = resultSet.getDouble("purchasePrice");
						invoiceItemObject = new Purchase(itemId, itemCode, itemName, model, purchasePrice);
					}
				} else if (type.equals("Product")) {
					int quantity = resultSet.getInt("quantity");
					invoiceItemObject = new Product((Product) item, quantity);
				} else if (type.equals("Service")) {
					double hours = resultSet.getDouble("hours");
					invoiceItemObject = new Service((Service) item, hours);
				}
				invoiceMap.get(invoiceId).addItem(invoiceItemObject);
			}
			resultSet.close();
			ps.close();
			conn.close();
		} catch (

		SQLException e) {
			throw new RuntimeException(e);
		}

	}
}
