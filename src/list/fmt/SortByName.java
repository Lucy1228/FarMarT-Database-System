package list.fmt;

import java.util.Comparator;

import framework.fmt.Invoice;

/**
 * This class sorts customer names on Invoices alphabetically
 * 
 * @author lucyb
 *
 */

public class SortByName implements Comparator<Invoice> {

	@Override
	public int compare(Invoice o1, Invoice o2) {

		int byLastName = o1.getCustomer().compareTo(o2.getCustomer());
		return byLastName;
	}
}
