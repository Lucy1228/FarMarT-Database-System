package list.fmt;

import java.util.Comparator;

import framework.fmt.Invoice;

/**
 * This class sorts stores by their code and then by the last/first name of
 * the salesperson
 * 
 * @author lucyb
 *
 */
public class SortByStore implements Comparator<Invoice> {

	@Override
	public int compare(Invoice o1, Invoice o2) {

		int byStoreCode = o1.getStoreCode().compareTo(o2.getStoreCode());

		if (byStoreCode == 0) {
			return o1.getSalesperson().compareTo(o2.getSalesperson());
		}

		return byStoreCode;
	}

}
