package framework.fmt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * This class represents a person related to FMT business
 * 
 * @author lucyb
 *
 */

public class Person {

	private int personId;
	private String personCode;
	private String lastName;
	private String firstName;
	private Address address;
	private List<String> email;

	/**
	 * Constructs a person with option list of emails
	 */
	public Person(String personCode, String lastName, String firstName, Address address) {
		super();
		this.personCode = personCode;
		this.lastName = lastName;
		this.firstName = firstName;
		this.address = address;
		this.email = new ArrayList<>();
	}

	/**
	 * Copy constructor that includes personId for data retrieval using primary key
	 * in a database
	 */
	public Person(int personId, String personCode, String lastName, String firstName, Address address) {
		this(personCode, lastName, firstName, address);
		this.personId = personId;
	}

	public int getPersonId() {
		return personId;
	}

	public String getPersonCode() {
		return personCode;
	}

	@Override
	public String toString() {
		return lastName + "," + firstName + "(" + personCode + " : " + email + ")" + address;
	}

	public String getLastName() {
		return lastName;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getWholeName() {
		return lastName + ", " + firstName;
	}

	public Address getAddress() {
		return address;
	}

	public List<String> getEmail() {
		return email;
	}

	public void addEmail(String newEmail) {
		email.add(newEmail);
	}

	/**
	 * checks if person code exists in the database
	 * 
	 * @param personCode
	 * @return 0 if the persosn doesn't exist, or the personId of the existing
	 *         person
	 */

	public static int getPersonByCode(String personCode) {

		int personId = 0;

		String query = "select p.personId " + "from Person p " + "where p.personCode = ? ";

		try (Connection conn = DatabaseConnection.getConnection()) {

			PreparedStatement ps = conn.prepareStatement(query);
			ps.setString(1, personCode);
			ResultSet resultSet = ps.executeQuery();

			if (resultSet.next()) {
				personId = resultSet.getInt("personId");
			}
			resultSet.close();
			ps.close();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return personId;

	}

	/**
	 * Checks if there is a first/last name to insert per FMT guidelines
	 * 
	 * @param firstName
	 * @param lastName
	 * @return
	 */
	public static boolean isValid(String firstName, String lastName) {
		if (firstName != null && firstName != "" && lastName != null && lastName != "") {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Compares this person to the given person based on their last names and first
	 * names. If the last names are the same, the comparison is based on the first
	 * names
	 * 
	 * @param person
	 * @return negative integer if this person comes before the given person, a
	 *         positive integer if this person comes after the given person, and 0
	 *         if the names are equal.
	 */
	public int compareTo(Person person) {
		if (this.lastName.compareTo(person.getLastName()) == 0) {
			return this.firstName.compareTo(person.getFirstName());
		}
		return lastName.compareTo(person.getLastName());
	}
}
