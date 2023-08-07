package com.fmt;


import framework.fmt.DatabaseLoader;
import framework.fmt.ReportGenerator;


/**
 * InvoiceReport has a main method that creates instances of DatabaseLoader and
 * ReportGenerator, and then calls their respective methods to load the data and
 * generate the necessary FarMarT reports
 * 
 * @author lucyb
 *
 */
public class InvoiceReport {

	public static void main(String[] args) {
		
		ReportGenerator.sortedInvoices(DatabaseLoader.invoiceMap);
	
	}
}