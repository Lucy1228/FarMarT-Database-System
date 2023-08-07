package framework.fmt;
import java.util.Map;
import list.fmt.SortByName;
import list.fmt.SortByValue;
import list.fmt.SortByStore;
import list.fmt.FMTList;

/**
 * The ReportGenerator class provides methods that generate different sales and
 * invoice reports. All methods use a generic type K for keys in the maps in
 * order to use CSVParser class and DatabaseLoader class interchangeably
 * 
 * @author lucyb
 *
 */
public class ReportGenerator {
	/**
	 * Generates a sales summary report for all invoices and prints it to the
	 * standard output. It calculates the total sales, total number of items, and
	 * the total tax for all invoices.
	 * 
	 * @param invoiceMap
	 */
	public static <K> void allSalesSummary(Map<K, Invoice> invoiceMap) {

		StringBuilder sb = new StringBuilder();

		sb.append(
				"+--------------------------------------------------------------------------------------------------+\n");
		sb.append(
				"| Summary Report - By Total                                                                        |\n");
		sb.append(
				"+--------------------------------------------------------------------------------------------------+\n");
		sb.append(String.format("| %-10s | %-20s | %-20s | %10s | %10s | %10s |\n", "Invoice #", "Store", "Customer",
				"Num Items", "Tax", "Total"));
		sb.append(
				"+--------------------------------------------------------------------------------------------------+\n");

		double totalAllSales = 0.0;
		int numItemsTotal = 0;
		double taxTotal = 0.0;

		for (Map.Entry<K, Invoice> invoice : invoiceMap.entrySet()) {
			sb.append(invoice.getValue().printSummary());
			sb.append("\n");
			totalAllSales += (invoice.getValue().getInvoiceSubtotal() + invoice.getValue().getInvoiceTaxes());
			numItemsTotal += invoice.getValue().getNumItems();
			taxTotal += invoice.getValue().getInvoiceTaxes();
		}

		sb.append(
				"+------------------------------------------------------------------------------------------------+\n");
		sb.append(String.format("| %-10s | %-20s | %-20s | %10d | $%9.2f | $%9.2f |\n", "", "", "", numItemsTotal,
				taxTotal, totalAllSales));
		sb.append("\n");

		System.out.println(sb.toString());
	}

	/**
	 * Generates a sales summary report for each store and prints it to the standard
	 * output.Contains a table of store summaries such as store number, manager,
	 * number of sales, and the grand total. Calculates total in sales and total
	 * number of sales for each store
	 * 
	 * @param storeMap
	 */
	public static <K> void storeSalesSummary(Map<K, Store> storeMap) {

		StringBuilder sb = new StringBuilder();

		sb.append("+----------------------------------------------------------------------------------------+\n");
		sb.append("| Store Sales Summary Report                                                             |\n");
		sb.append("+----------------------------------------------------------------------------------------+\n");
		sb.append(String.format("| %-8s | %-20s | %10s | %15s |\n", "Store #", "Manager", "# Sales", "Grand Total"));
		sb.append("+----------------------------------------------------------------------------------------+\n");

		double totalAllSales = 0.0;
		int numSalesTotal = 0;
		for (Map.Entry<K, Store> store : storeMap.entrySet()) {
			sb.append(store.getValue().printStoreSummary());
			sb.append('\n');
			totalAllSales += store.getValue().getGrandTotal();
			numSalesTotal += store.getValue().getNumSales();
		}
		sb.append("+----------------------------------------------------------------------------------------+\n");
		sb.append(String.format("| %-8s | %-20s | %10d | $%14.2f |\n", "", "", numSalesTotal, totalAllSales));
		sb.append('\n');

		System.out.println(sb.toString());
	}

	/**
	 * Generates an invoice report for each individual invoice and prints it to the
	 * standard output. Displays the invoice number, store, date, customer,
	 * salesperson, item code, type of item, and total for each item. It calculates
	 * the subtotal, tax, and grand total for each invoice
	 * 
	 * @param invoiceMap
	 */
	public static <K> void invoiceReport(Map<K, Invoice> invoiceMap) {

		for (Map.Entry<K, Invoice> invoice : invoiceMap.entrySet()) {
			System.out.println(invoice.getValue().printInvoiceDetails());
		}
	}

	/**
	 * Sorts and prints the invoices in a Map by customer, total, store and
	 * salesperson. Formats the each sorted list of invoices
	 *
	 * @param <K>
	 * @param invoiceMap map containing the invoices to be sorted and printed
	 */
	public static <K> void sortedInvoices(Map<K, Invoice> invoiceMap) {

		FMTList<Invoice> byCustomer = new FMTList<Invoice>(new SortByName());
		FMTList<Invoice> byTotal = new FMTList<Invoice>(new SortByValue());
		FMTList<Invoice> byStore = new FMTList<Invoice>(new SortByStore());

		for (Invoice invoice : invoiceMap.values()) {
			byCustomer.add(invoice);
			byTotal.add(invoice);
			byStore.add(invoice);
		}

		StringBuilder sb = new StringBuilder();

		sb.append(
				"+------------------------------------------------------------------------------------------------------+\n");
		sb.append(
				"| Summary Report - By Customer                                                                         |\n");
		sb.append(
				"+------------------------------------------------------------------------------------------------------+\n");
		sb.append(String.format("| %-10s | %-20s | %-20s | %17s | %20s |\n", "Sale #", "Store", "Customer",
				"Salesperson", "Total"));

		for (Invoice invoice : byCustomer) {
			sb.append(invoice.printSorted());
		}

		sb.append(
				"+------------------------------------------------------------------------------------------------------+\n");
		sb.append(
				"| Summary Report - By Total                                                                            |\n");
		sb.append(
				"+------------------------------------------------------------------------------------------------------+\n");
		sb.append(String.format("| %-10s | %-20s | %-20s | %17s | %20s |\n", "Sale #", "Store", "Customer",
				"Salesperson", "Total"));

		for (Invoice invoice : byTotal) {
			sb.append(invoice.printSorted());
		}
		sb.append(
				"+----------------------------------------------------------------------------------------------------+\n");
		sb.append(
				"| Summary Report - By Store                                                                          |\n");
		sb.append(
				"+----------------------------------------------------------------------------------------------------+\n");
		sb.append(String.format("| %-10s | %-20s | %-20s | %17s | %20s |\n", "Sale #", "Store", "Customer",
				"Salesperson", "Total"));

		for (Invoice invoice : byStore) {
			sb.append(invoice.printSorted());
		}
		System.out.println(sb.toString());
	}
}
