package framework.fmt;
import java.io.File;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;
/**
 * This class parses data files and creates corresponding objects. Static
 * methods are used to load data from CSV files and store them in maps
 */

public class CSVParser {

	public static Map<String, Person> personMap;
	public static Map<String, Item> itemMap;
	public static Map<String, Store> storeMap;
	public static Map<String, Invoice> invoiceMap;
	/**
	 * This static block initializes static fields and parses data from the CSV
	 * files into memory organized via maps. Ensures data parsing only happens once
	 * and for easy data access
	 */
	static {

		personMap = new HashMap<>();
		itemMap = new HashMap<>();
		storeMap = new HashMap<>();
		invoiceMap = new HashMap<>();

		loadPerson();
		loadItem();
		loadStore();
		loadInvoice();
		loadInvoiceItem();
	}

	/**
	 * Reads in a person csv and stores person objects in the person map. Email is
	 * instantiated as a list to allow addition or removal of emails and allow for
	 * any number of emails to be associated with a Person object
	 */
	public static void loadPerson() {

		File personFile = new File("data/Persons.csv");

		try (Scanner scanner = new Scanner(personFile)) {
			Integer.parseInt(scanner.nextLine());
			while (scanner.hasNext()) {
				String line = scanner.nextLine();
				if (!line.trim().isEmpty()) {
					List<String> email = new ArrayList<String>();
					Person personObject = null;
					String personFields[] = line.split(",");
					String personCode = personFields[0];
					String firstName = personFields[1];
					String lastName = personFields[2];
					String street = personFields[3];
					String city = personFields[4];
					String state = personFields[5];
					String zip = personFields[6];
					String country = personFields[7];
					Address address = new Address(street, city, state, zip, country);
					if (personFields.length > 8) {
						for (int i = 8; i < personFields.length; i++) {
							email.add(personFields[i]);
						}
					}
					personObject = new Person(personCode, firstName, lastName, address);

					for (String newEmail : email) {
						personObject.addEmail(newEmail);
					}
					personMap.put(personCode, personObject);
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Reads in an item csv and stores item objects in the item map. Creates an Item
	 * object depending on item type for a flexible way to handle different types of
	 * items with different properties
	 * 
	 */

	public static void loadItem() {

		File itemFile = new File("data/Items.csv");

		try (Scanner scanner = new Scanner(itemFile)) {
			Integer.parseInt(scanner.nextLine());
			while (scanner.hasNext()) {
				String line = scanner.nextLine();
				if (!line.trim().isEmpty()) {
					Item itemObject = null;
					String itemFields[] = line.split(",");
					String itemCode = itemFields[0];
					String itemName = itemFields[2];

					if (itemFields[1].equals("E")) {
						String model = itemFields.length > 3 ? itemFields[3] : "";
						itemObject = new Equipment(itemCode, itemName, model);

					} else if (itemFields[1].equals("P")) {
						String unit = itemFields.length > 3 ? itemFields[3] : "";
						double unitPrice = itemFields.length > 4 ? Double.parseDouble(itemFields[4]) : 0.0;
						itemObject = new Product(itemCode, itemName, unit, unitPrice);

					} else if (itemFields[1].equals("S")) {
						double hourlyRate = itemFields.length > 3 ? Double.parseDouble(itemFields[3]) : 0.0;
						itemObject = new Service(itemCode, itemName, hourlyRate);
					}

					itemMap.put(itemCode, itemObject);
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Reads in a store csv and stores store objects in the store map. Creates Store
	 * objects.Retrieves Person objects that match the manager code from the Persons
	 * Map. Avoids creating duplicate Person objects for managers who own multiple
	 * stores
	 * 
	 */

	public static void loadStore() {

		File storeFile = new File("data/Stores.csv");
		try (Scanner scanner = new Scanner(storeFile)) {
			Integer.parseInt(scanner.nextLine());
			while (scanner.hasNext()) {
				String line = scanner.nextLine();
				if (!line.trim().isEmpty()) {
					Store storeObject = null;
					String storeFields[] = line.split(",");
					String storeCode = storeFields[0];
					String managerCode = storeFields[1];
					String street = storeFields[2];
					String city = storeFields[3];
					String state = storeFields[4];
					String zip = storeFields[5];
					String country = storeFields[6];
					Address storeAddress = new Address(street, city, state, zip, country);
					Person manager = personMap.get(managerCode);
					storeObject = new Store(storeCode, manager, storeAddress);
					storeMap.put(storeCode, storeObject);
				}
			}

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Reads in the invoice file and adds invoice objects to the invoice map.
	 *
	 */

	public static void loadInvoice() {

		File invoiceFile = new File("data/Invoices.csv");
		try (Scanner scanner = new Scanner(invoiceFile)) {
			Integer.parseInt(scanner.nextLine());
			while (scanner.hasNext()) {
				String line = scanner.nextLine();
				if (!line.trim().isEmpty()) {
					Invoice invoiceObject = null;
					String invoiceFields[] = line.split(",");
					String invoiceCode = invoiceFields[0];
					String storeCode = invoiceFields[1];
					String customerCode = invoiceFields[2];
					String salespersonCode = invoiceFields[3];
					LocalDate date = LocalDate.parse(invoiceFields[4]);
					Person customer = personMap.get(customerCode);
					Person salesperson = personMap.get(salespersonCode);
					Store store = storeMap.get(storeCode);
					invoiceObject = new Invoice(invoiceCode, store, customer, salesperson, date);
					invoiceMap.put(invoiceCode, invoiceObject);
					storeMap.get(storeCode).addInvoice(invoiceObject);

				}
			}

		} catch (Exception e) {
			throw new RuntimeException(e);

		}
	}

	/**
	 * Reads in the invoice item csv and adds the items to their corresponding
	 * invoices based on the type of item by referencing the invoice map
	 *
	 */

	public static void loadInvoiceItem() {

		File invoiceItemsFile = new File("data/InvoiceItems.csv");
		try (Scanner scanner = new Scanner(invoiceItemsFile)) {
			Integer.parseInt(scanner.nextLine());
			while (scanner.hasNext()) {
				String line = scanner.nextLine();
				if (!line.trim().isEmpty()) {
					Item invoiceItemObject = null;
					String invoiceItemFields[] = line.split(",");
					String invoiceCode = invoiceItemFields[0];
					String itemCode = invoiceItemFields[1];
					Item item = itemMap.get(itemCode);

					if (item instanceof Equipment) {
						Equipment equipment = (Equipment) item;
						String itemName = item.getItemName();
						String model = equipment.getModel();

						if (invoiceItemFields[2].equals("P")) {
							double purchasePrice = invoiceItemFields.length > 3
									? Double.parseDouble(invoiceItemFields[3])
									: 0.0;
							invoiceItemObject = new Purchase(itemCode, itemName, model, purchasePrice);

						} else if (invoiceItemFields[2].equals("L")) {
							double fee = invoiceItemFields.length > 3 ? Double.parseDouble(invoiceItemFields[3]) : 0.0;
							LocalDate startDate = invoiceItemFields.length > 3 ? LocalDate.parse(invoiceItemFields[4])
									: LocalDate.now();
							LocalDate endDate = invoiceItemFields.length > 3 ? LocalDate.parse(invoiceItemFields[5])
									: LocalDate.now();
							invoiceItemObject = new Lease(itemCode, itemName, model, fee, startDate, endDate);
						}

					} else if (item instanceof Product) {
						int quantity = invoiceItemFields.length > 2 ? Integer.parseInt(invoiceItemFields[2]) : 0;
						invoiceItemObject = new Product((Product) item, quantity);

					} else if (item instanceof Service) {
						double hours = invoiceItemFields.length > 2 ? Double.parseDouble(invoiceItemFields[2]) : 0.0;
						invoiceItemObject = new Service((Service) item, hours);
					}
					invoiceMap.get(invoiceCode).addItem(invoiceItemObject);

				}

			}
		} catch (

		Exception e) {
			throw new RuntimeException(e);

		}

	}
}
