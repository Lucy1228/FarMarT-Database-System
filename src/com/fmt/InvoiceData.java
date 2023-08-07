package com.fmt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;

import framework.fmt.Address;
import framework.fmt.DatabaseConnection;
import framework.fmt.Equipment;
import framework.fmt.Invoice;
import framework.fmt.Item;
import framework.fmt.Person;
import framework.fmt.Product;
import framework.fmt.Service;
import framework.fmt.Store;

import java.util.ArrayList;

/**
 * This is a collection of utility methods that define a general API for
 * interacting with the database supporting this application.
 *
 */
public class InvoiceData {

	/**
	 * Removes all records from all tables in the database.
	 */
	public static void clearDatabase() {

		List<String> listOfQueries = new ArrayList<>();
		listOfQueries.add("delete from Email where emailId > 0");
		listOfQueries.add("delete from InvoiceItem where invoiceItemId > 0");
		listOfQueries.add("delete from Invoice where invoiceId > 0");
		listOfQueries.add("delete from Store where storeId > 0");
		listOfQueries.add("delete from Person where personId > 0");
		listOfQueries.add("delete from Address where addressId > 0");
		listOfQueries.add("delete from Item where itemId > 0");

		try (Connection conn = DatabaseConnection.getConnection()) {

			for (String query : listOfQueries) {
				try (PreparedStatement ps = conn.prepareStatement(query)) {
					ps.executeUpdate();
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	/**
	 * adds an address to the database. checks if the address is null, existing, or
	 * empty before allowing insertion
	 * 
	 * @param street
	 * @param city
	 * @param state
	 * @param zip
	 * @param country
	 * @return addressId associated with the already existing address or the new one
	 *         created
	 */
	public static int addAddress(String street, String city, String state, String zip, String country) {

		if (Address.getAddressById(street, city, state, zip, country) > 0) {
			return Address.getAddressById(street, city, state, zip, country);
		} else {

			int addressId = 0;

			if (Address.isValid(street, city, state, zip, country)) {

				String addressQuery = """
						insert into Address (street, city, state, zip, country)
						values (?,?,?,?,?);
						""";

				try (Connection conn = DatabaseConnection.getConnection()) {

					try (PreparedStatement addressPs = conn.prepareStatement(addressQuery,
							Statement.RETURN_GENERATED_KEYS)) {
						addressPs.setString(1, street);
						addressPs.setString(2, city);
						addressPs.setString(3, state);
						addressPs.setString(4, zip);
						addressPs.setString(5, country);
						addressPs.executeUpdate();
						ResultSet addressKey = addressPs.getGeneratedKeys();

						if (addressKey.next()) {
							addressId = addressKey.getInt(1);
						} else {
							throw new RuntimeException("invalid addressId");
						}
					}
				} catch (SQLException e) {
					e.printStackTrace();
					throw new RuntimeException(e);
				}
			}
			return addressId;
		}
	}

	/**
	 * Method to add a person record to the database with the provided data. Checks
	 * for person already exists before inserting person Checks if arguments
	 * provided are not null before inserting per FMT guidelines
	 *
	 * @param personCode
	 * @param firstName
	 * @param lastName
	 * @param street
	 * @param city
	 * @param state
	 * @param zip
	 * @param country
	 */
	public static void addPerson(String personCode, String firstName, String lastName, String street, String city,
			String state, String zip, String country) {

		if (Person.getPersonByCode(personCode) > 0) {
			System.out.println(personCode + " already exists");
			return;
		} else {

			if (Person.isValid(firstName, lastName)) {

				String personQuery = """
						insert into Person (personCode, lastName, firstName, addressId)
						values (?,?,?,?)
						""";

				try (Connection conn = DatabaseConnection.getConnection()) {

					try (PreparedStatement personPs = conn.prepareStatement(personQuery,
							Statement.RETURN_GENERATED_KEYS)) {

						int addressId = addAddress(street, city, state, zip, country);
						personPs.setString(1, personCode);
						personPs.setString(2, lastName);
						personPs.setString(3, firstName);
						personPs.setInt(4, addressId);
						personPs.executeUpdate();
					}
				} catch (SQLException e) {
					e.printStackTrace();
					throw new RuntimeException(e);
				}
			} else {
				System.out.println("invalid arguments");
			}
		}
	}

	/**
	 * Adds an email record corresponding person record corresponding to the
	 * provided <code>personCode</code>
	 *
	 * @param personCode
	 * @param email
	 */
	public static void addEmail(String personCode, String email) {

		if (Person.getPersonByCode(personCode) > 0) {
			if (email != "" && email != null) {

				String emailQuery = """
						insert into Email (personId, emailAddress)
						values (?,?)
						""";

				try (Connection conn = DatabaseConnection.getConnection()) {

					try (PreparedStatement emailPs = conn.prepareStatement(emailQuery,
							Statement.RETURN_GENERATED_KEYS)) {

						int personId = Person.getPersonByCode(personCode);
						emailPs.setInt(1, personId);
						emailPs.setString(2, email);
						emailPs.executeUpdate();
					}
				} catch (SQLException e) {
					e.printStackTrace();
					throw new RuntimeException(e);
				}
			} else {
				System.out.println("please enter a valid email");
			}
		} else {
			System.out.println("invalid person code");
		}
	}

	/**
	 * Adds a store record to the database managed by the person identified by the
	 * given code.
	 *
	 * @param storeCode
	 * @param managerCode
	 * @param street
	 * @param city
	 * @param state
	 * @param zip
	 * @param country
	 */
	public static void addStore(String storeCode, String managerCode, String street, String city, String state,
			String zip, String country) {

		if (Store.getStoreByCode(storeCode) > 0) {
			System.out.println(storeCode + " already exists");
			return;
		} else {

			if (Person.getPersonByCode(managerCode) > 0 && Address.isValid(street, city, state, zip, country)) {

				String storeQuery = """
						insert into Store (storeCode, managerId, addressId)
						values (?,?,?)
						""";

				try (Connection conn = DatabaseConnection.getConnection()) {

					try (PreparedStatement storePs = conn.prepareStatement(storeQuery,
							Statement.RETURN_GENERATED_KEYS)) {

						int addressId = addAddress(street, city, state, zip, country);
						int managerId = Person.getPersonByCode(managerCode);

						storePs.setString(1, storeCode);
						storePs.setInt(2, managerId);
						storePs.setInt(3, addressId);
						storePs.executeUpdate();
					}
				} catch (SQLException e) {
					e.printStackTrace();
					throw new RuntimeException(e);
				}

			} else {
				System.out.println("invalid arguments");
			}
		}
	}

	/**
	 * Adds a product record to the database with the given <code>code</code>,
	 * <code>name</code> and <code>unit</code> and <code>pricePerUnit</code>.
	 *
	 * @param itemCode
	 * @param name
	 * @param unit
	 * @param pricePerUnit
	 */
	public static void addProduct(String code, String name, String unit, double pricePerUnit) {

		if (Item.getItemByCode(code) > 0) {
			System.out.println("This product already exists");
			return;
		} else {

			if (Product.isValid(name, unit, pricePerUnit)) {

				String productQuery = """
						insert into Item (itemCode, type, itemName, unit, unitPrice)
						values (?,?,?,?,?)
						""";

				try (Connection conn = DatabaseConnection.getConnection()) {

					try (PreparedStatement productPs = conn.prepareStatement(productQuery,
							Statement.RETURN_GENERATED_KEYS)) {

						productPs.setString(1, code);
						productPs.setString(2, "Product");
						productPs.setString(3, name);
						productPs.setString(4, unit);
						productPs.setDouble(5, pricePerUnit);
						productPs.executeUpdate();
					}
				} catch (SQLException e) {
					e.printStackTrace();
					throw new RuntimeException(e);
				}
			} else {
				System.out.println("invalid arguments");
			}
		}
	}

	/**
	 * Adds an equipment record to the database with the given <code>code</code>,
	 * <code>name</code> and <code>modelNumber</code>.
	 *
	 * @param itemCode
	 * @param name
	 * @param modelNumber
	 */
	public static void addEquipment(String code, String name, String modelNumber) {

		if (Item.getItemByCode(code) > 0) {
			System.out.println("This equipment already exists");
			return;
		} else {

			if (Equipment.isValid(name, modelNumber)) {

				String equipmentQuery = """
						insert into Item (itemCode, type, itemName, model)
						values (?,?,?,?)
						""";

				try (Connection conn = DatabaseConnection.getConnection()) {

					try (PreparedStatement equipmentPs = conn.prepareStatement(equipmentQuery,
							Statement.RETURN_GENERATED_KEYS)) {

						equipmentPs.setString(1, code);
						equipmentPs.setString(2, "Equipment");
						equipmentPs.setString(3, name);
						equipmentPs.setString(4, modelNumber);
						equipmentPs.executeUpdate();
					}
				} catch (SQLException e) {
					e.printStackTrace();
					throw new RuntimeException(e);
				}
			} else {
				System.out.println("invalid arguments");
			}
		}
	}

	/**
	 * Adds a service record to the database with the given <code>code</code>,
	 * <code>name</code> and <code>costPerHour</code>.
	 *
	 * @param itemCode
	 * @param name
	 * @param modelNumber
	 */
	public static void addService(String code, String name, double costPerHour) {

		if (Item.getItemByCode(code) > 0) {
			System.out.println("This service already exists");
			return;
		} else {

			if (Service.isValid(name, costPerHour)) {

				String serviceQuery = """
						insert into Item (itemCode, type, itemName, hourlyRate)
						values (?,?,?,?)
						""";

				try (Connection conn = DatabaseConnection.getConnection()) {

					try (PreparedStatement servicePs = conn.prepareStatement(serviceQuery,
							Statement.RETURN_GENERATED_KEYS)) {

						servicePs.setString(1, code);
						servicePs.setString(2, "Service");
						servicePs.setString(3, name);
						servicePs.setDouble(4, costPerHour);
						servicePs.executeUpdate();
					}
				} catch (SQLException e) {
					e.printStackTrace();
					throw new RuntimeException(e);
				}
			} else {
				System.out.println("invalid arguments");
			}
		}
	}

	/**
	 * Adds an invoice record to the database with the given data.
	 *
	 * @param invoiceCode
	 * @param storeCode
	 * @param customerCode
	 * @param salesPersonCode
	 * @param invoiceDate
	 */
	public static void addInvoice(String invoiceCode, String storeCode, String customerCode, String salesPersonCode,
			String invoiceDate) {

		if (Invoice.getInvoiceByCode(invoiceCode) > 0) {
			System.out.println(invoiceCode + " already exists");
			return;
		} else {

			if (Invoice.isValid(storeCode, customerCode, salesPersonCode) && Invoice.isValidDateFormat(invoiceDate)
					&& Person.getPersonByCode(customerCode) > 0 && Person.getPersonByCode(salesPersonCode) > 0
					&& Store.getStoreByCode(storeCode) > 0) {

				String invoiceQuery = """
						insert into Invoice (invoiceCode, date, customerId, salespersonId, storeId)
						values (?,?,?,?,?)
						""";

				try (Connection conn = DatabaseConnection.getConnection()) {

					try (PreparedStatement invoicePs = conn.prepareStatement(invoiceQuery,
							Statement.RETURN_GENERATED_KEYS)) {

						int salespersonId = Person.getPersonByCode(salesPersonCode);
						int customerId = Person.getPersonByCode(customerCode);
						int storeId = Store.getStoreByCode(storeCode);
						invoicePs.setString(1, invoiceCode);
						invoicePs.setString(2, invoiceDate);
						invoicePs.setInt(3, customerId);
						invoicePs.setInt(4, salespersonId);
						invoicePs.setInt(5, storeId);
						invoicePs.executeUpdate();
					}
				} catch (SQLException e) {
					e.printStackTrace();
					throw new RuntimeException(e);
				}
			} else {
				System.out.println("invalid arguments");
			}
		}
	}

	/**
	 * Adds a particular product (identified by <code>itemCode</code>) to a
	 * particular invoice (identified by <code>invoiceCode</code>) with the
	 * specified quantity.
	 *
	 * @param invoiceCode
	 * @param itemCode
	 * @param quantity
	 */
	public static void addProductToInvoice(String invoiceCode, String itemCode, int quantity) {

		if (Invoice.getInvoiceByCode(invoiceCode) > 0 && Item.getItemByCode(itemCode) > 0) {
			if (quantity > 0) {

				String invoiceItemQuery = """
						insert into InvoiceItem (invoiceId, itemId, quantity)
						values (?,?,?)
						""";

				try (Connection conn = DatabaseConnection.getConnection()) {

					try (PreparedStatement invoiceItemPs = conn.prepareStatement(invoiceItemQuery,
							Statement.RETURN_GENERATED_KEYS)) {

						int invoiceId = Invoice.getInvoiceByCode(invoiceCode);
						int itemId = Item.getItemByCode(itemCode);
						invoiceItemPs.setInt(1, invoiceId);
						invoiceItemPs.setInt(2, itemId);
						invoiceItemPs.setInt(3, quantity);
						invoiceItemPs.executeUpdate();
					}
				} catch (SQLException e) {
					e.printStackTrace();
					throw new RuntimeException(e);
				}

			}
		}

	}

	/**
	 * Adds a particular equipment <i>purchase</i> (identified by
	 * <code>itemCode</code>) to a particular invoice (identified by
	 * <code>invoiceCode</code>) at the given <code>purchasePrice</code>.
	 *
	 * @param invoiceCode
	 * @param itemCode
	 * @param purchasePrice
	 */
	public static void addEquipmentToInvoice(String invoiceCode, String itemCode, double purchasePrice) {

		if (Invoice.getInvoiceByCode(invoiceCode) > 0 && Item.getItemByCode(itemCode) > 0) {
			if (purchasePrice > 0.0) {

				String invoiceItemQuery = """
						insert into InvoiceItem (invoiceId, itemId, purchasePrice, eStatus)
						values (?,?,?,?)
						""";

				try (Connection conn = DatabaseConnection.getConnection()) {

					try (PreparedStatement invoiceItemPs = conn.prepareStatement(invoiceItemQuery,
							Statement.RETURN_GENERATED_KEYS)) {

						int invoiceId = Invoice.getInvoiceByCode(invoiceCode);
						int itemId = Item.getItemByCode(itemCode);
						invoiceItemPs.setInt(1, invoiceId);
						invoiceItemPs.setInt(2, itemId);
						invoiceItemPs.setDouble(3, purchasePrice);
						invoiceItemPs.setString(4, "Purchased");
						invoiceItemPs.executeUpdate();
					}
				} catch (SQLException e) {
					e.printStackTrace();
					throw new RuntimeException(e);
				}
			}
		} else {
			System.out.println("invalid arguments");
		}
	}

	/**
	 * Adds a particular equipment <i>lease</i> (identified by
	 * <code>itemCode</code>) to a particular invoice (identified by
	 * <code>invoiceCode</code>) with the given 30-day <code>periodFee</code> and
	 * <code>beginDate/endDate</code>.
	 *
	 * @param invoiceCode
	 * @param itemCode
	 * @param amount
	 */
	public static void addEquipmentToInvoice(String invoiceCode, String itemCode, double periodFee, String beginDate,
			String endDate) {

		if (Invoice.isValidDateFormat(beginDate) && Invoice.isValidDateFormat(endDate)
				&& Invoice.getInvoiceByCode(invoiceCode) > 0 && Item.getItemByCode(itemCode) > 0) {
			if (periodFee > 0.0) {

				String invoiceItemQuery = """
						insert into InvoiceItem (invoiceId, itemId, fee, startDate, endDate, eStatus)
						values (?,?,?,?,?,?)
						""";

				try (Connection conn = DatabaseConnection.getConnection()) {

					try (PreparedStatement invoiceItemPs = conn.prepareStatement(invoiceItemQuery,
							Statement.RETURN_GENERATED_KEYS)) {

						int invoiceId = Invoice.getInvoiceByCode(invoiceCode);
						int itemId = Item.getItemByCode(itemCode);
						invoiceItemPs.setInt(1, invoiceId);
						invoiceItemPs.setInt(2, itemId);
						invoiceItemPs.setDouble(3, periodFee);
						invoiceItemPs.setString(4, beginDate);
						invoiceItemPs.setString(5, endDate);
						invoiceItemPs.setString(6, "Leased");
						invoiceItemPs.executeUpdate();
					}
				} catch (SQLException e) {
					e.printStackTrace();
					throw new RuntimeException(e);
				}
			}
		} else {
			System.out.println("invalid arguments");
		}
	}

	/**
	 * Adds a particular service (identified by <code>itemCode</code>) to a
	 * particular invoice (identified by <code>invoiceCode</code>) with the
	 * specified number of hours.
	 *
	 * @param invoiceCode
	 * @param itemCode
	 * @param billedHours
	 */
	public static void addServiceToInvoice(String invoiceCode, String itemCode, double billedHours) {

		if (Invoice.getInvoiceByCode(invoiceCode) > 0 && Item.getItemByCode(itemCode) > 0) {
			if (billedHours > 0.0) {

				String invoiceItemQuery = """
						insert into InvoiceItem (invoiceId, itemId, hours)
						values (?,?,?)
						""";

				try (Connection conn = DatabaseConnection.getConnection()) {

					try (PreparedStatement invoiceItemPs = conn.prepareStatement(invoiceItemQuery,
							Statement.RETURN_GENERATED_KEYS)) {

						int invoiceId = Invoice.getInvoiceByCode(invoiceCode);
						int itemId = Item.getItemByCode(itemCode);
						invoiceItemPs.setInt(1, invoiceId);
						invoiceItemPs.setInt(2, itemId);
						invoiceItemPs.setDouble(3, billedHours);
						invoiceItemPs.executeUpdate();
					}
				} catch (SQLException e) {
					e.printStackTrace();
					throw new RuntimeException(e);
				}

			}
		} else {
			System.out.println("invalid arguments");
		}
	}
}