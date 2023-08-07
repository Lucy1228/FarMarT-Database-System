package com.fmt;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import framework.fmt.Item;
import framework.fmt.Person;
import framework.fmt.Store;




/**
 * This class reads in raw data files, converts them to JSON format and then
 * saves them to a file
 * 
 * @author lucyb
 *
 */
public class DataConverter {

	/**
	 * LocalDateAdapter is used to Work around the gson library issue accessing
	 * LocalDate. Google has not yet resolved.
	 * 
	 * @author lucyb
	 *
	 */

	public static final class LocalDateAdapter extends TypeAdapter<LocalDate> {
		public void write(final JsonWriter jsonWriter, final LocalDate localDate) throws IOException {
			jsonWriter.value(localDate.toString());
		}

		public LocalDate read(final JsonReader jsonReader) throws IOException {
			return LocalDate.parse(jsonReader.nextString());
		}
	}

	private static final Gson GSON = new GsonBuilder()
			.registerTypeAdapter(LocalDate.class, new LocalDateAdapter().nullSafe()).setPrettyPrinting().create();

	/**
	 * Converts a Person Map into JSON format and saves it to a file
	 * 
	 * @param result is a Person Map to be converted and saved
	 * @return Person Map converted to JSON
	 */

	public static Map<String, Person> serializePersonFile(Map<String, Person> personMap) {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		Map<String, Object> jsonMap = new HashMap<String, Object>();
		jsonMap.put("persons", personMap.values());
		String json = gson.toJson(jsonMap);

		try (PrintWriter writer = new PrintWriter("data/Persons.json")) {
			writer.print(json);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return personMap;
	}

	/**
	 * Converts an Items Map to JSON format and saves it to a file
	 * 
	 * @param Item map be converted and saved
	 * @return Item map converted to JSON
	 */

	public static Map<String, Item> serializeItemFile(Map<String, Item> itemMap) {
		Map<String, Object> jsonMap = new HashMap<>();
		jsonMap.put("items", itemMap.values());
		String json = GSON.toJson(jsonMap);

		try (PrintWriter writer = new PrintWriter("data/Items.json")) {
			writer.print(json);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		return itemMap;
	}

	/**
	 * Converts a Stores Map to JSON format and saves it to a file
	 * 
	 * @param a Store Map to be converted and saved
	 * @return Store map converted to JSON
	 */

	public static Map<String, Store> serializeStoreFile(Map<String, Store> storeMap) {
		Map<String, Object> jsonMap = new HashMap<String, Object>();
		jsonMap.put("stores", storeMap.values());
		String json = GSON.toJson(jsonMap);

		try (PrintWriter writer = new PrintWriter("data/Stores.json")) {
			writer.print(json);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return storeMap;
	}

}