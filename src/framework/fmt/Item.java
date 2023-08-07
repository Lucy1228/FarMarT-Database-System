package framework.fmt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * The Item class represents various types of items in FMT stores.
 * 
 * @author lucyb
 *
 */

public abstract class Item {

	private String itemCode;
	private String itemName;
	private int itemId;

	public Item(String itemCode, String itemName) {
		this.itemCode = itemCode;
		this.itemName = itemName;
	}

	/**
	 * Copy constructor that includes itemId for data retrieval using primary key in
	 * a database
	 */
	public Item(int itemId, String itemCode, String itemName) {
		this(itemCode, itemName);
		this.itemId = itemId;

	}

	public abstract double getTaxes();

	public abstract double getSubtotal();
	
//	public abstract boolean isValid();

	public int getItemId() {
		return itemId;
	}

	public String getItemCode() {
		return itemCode;
	}

	public String getItemName() {
		return itemName;
	}

	@Override
	public String toString() {
		return itemCode + itemName;
	}

	/**
	 * checks if item code exists in the database
	 * @param itemCode
	 * @return 0 if the item doesn't exist, or the itemId of the existing item
	 */
	public static int getItemByCode(String itemCode) {

		int itemId = 0;

		String query = "select item.itemId " + "from Item item " + "where item.itemCode = ? ";

		try (Connection conn = DatabaseConnection.getConnection()) {

			PreparedStatement ps = conn.prepareStatement(query);
			ps.setString(1, itemCode);
			ResultSet resultSet = ps.executeQuery();

			if (resultSet.next()) {
				itemId = resultSet.getInt("itemId");
			}
			resultSet.close();
			ps.close();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return itemId;

	}
}