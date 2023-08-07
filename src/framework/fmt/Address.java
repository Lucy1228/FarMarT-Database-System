package framework.fmt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * This class represents an address with street, city, state, country and
 * zipcode components
 * 
 * @author lucyb
 *
 */

public class Address {

	private String street;
	private String city;
	private String state;
	private String country;
	private String zip;
	private int addressId;

	public Address(String street, String city, String state, String zip, String country) {
		this.street = street;
		this.city = city;
		this.state = state;
		this.country = country;
		this.zip = zip;
	}

	/**
	 * Copy constructor that includes addressId for data retrieval using primary key
	 * in a database
	 */
	public Address(int addressId, String street, String city, String state, String zip, String country) {
		this(street, city, state, zip, country);
		this.addressId = addressId;
	}

	@Override
	public String toString() {
		return " \n" + street + "\n" + city + " " + state + " " + country + " " + zip + "\n";
	}

	public int getAddressId() {
		return addressId;
	}

	public String getStreet() {
		return street;
	}

	public String getCity() {
		return city;
	}

	public String getState() {
		return state;
	}

	public String getZip() {
		return zip;
	}

	public String getCountry() {
		return country;
	}

	/**
	 * checks if address exists in the database
	 * 
	 * @param street
	 * @param city
	 * @param state
	 * @param zip
	 * @param country
	 * @return 0 if the address doesn't exist, or the existing addressId
	 */
	public static int getAddressById(String street, String city, String state, String zip, String country) {

		int addressId = 0;

		String query = "select addressId, street, city, state, zip, country " + "from Address " + "where street = ? "
				+ "and city = ? " + "and state = ? " + "and zip = ? " + "and country = ?";

		try (Connection conn = DatabaseConnection.getConnection()) {

			PreparedStatement ps = conn.prepareStatement(query);
			ps.setString(1, street);
			ps.setString(2, city);
			ps.setString(3, state);
			ps.setString(4, zip);
			ps.setString(5, country);
			ResultSet resultSet = ps.executeQuery();

			if (resultSet.next()) {
				addressId = resultSet.getInt("addressId");
			}
			resultSet.close();
			ps.close();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return addressId;

	}

	/**
	 * checks if there is a valid address to enter that is not null or empty
	 * 
	 * @param street
	 * @param city
	 * 
	 * @param state
	 * @param zip
	 * @param country
	 * @return
	 */
	public static boolean isValid(String street, String city, String state, String zip, String country) {
		if (street != null && city != null && state != null && zip != null && country != null && street != ""
				&& city != "" && state != "" && zip != "" && country != "") {
			return true;
		} else {
			return false;
		}

	}

}
