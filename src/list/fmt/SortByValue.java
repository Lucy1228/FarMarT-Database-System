package list.fmt;

import java.util.Comparator;

import framework.fmt.Invoice;

/**
 * This class sorts Invoices by their total
 * 
 * @author lucyb
 *
 */
public class SortByValue implements Comparator<Invoice> {

	@Override
	public int compare(Invoice o1, Invoice o2) {
		int byValue = Double.compare(o1.getTotal(), o2.getTotal());

		if (byValue < 0) {
			return 1;
		} else if (byValue > 0) {
			return -1;
		} else {
			return 0;
		}

	}
}