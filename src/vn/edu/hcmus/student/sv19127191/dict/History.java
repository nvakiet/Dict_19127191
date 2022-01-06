package vn.edu.hcmus.student.sv19127191.dict;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * vn.edu.hcmus.student.sv19127191.dict<br/>
 * Created by Ngo Van Anh Kiet - MSSV: 19127191<br/>
 * Date 2/1/2022 - 7:21 PM<br/>
 * Description: A class to manage the history of queries to the slang dictionary<br/>
 */
public class History {
	private ArrayList<Record> queryRecords;
	private String[][] table = null;
	private boolean shouldRefresh = false;
	public boolean shouldSave = false;

	/**
	 * The default constructor of this class
	 */
	public History() {
		queryRecords = new ArrayList<>();
	}

	/**
	 * Save the query history to a file on the hard drive (data/saved/history.dat)
	 */
	public void save() throws Exception {
		if (!shouldSave) return;
		File file = new File("data/saved/history.dat");
		ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
		oos.writeObject(queryRecords);
		oos.close();
		shouldSave = false;
	}

	/**
	 * Load the query history from a file on the hard drive (data/saved/history.dat)
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void load() throws Exception {
		File file = new File("data/saved/history.dat");
		// If there's saved data, load the saved dictionary
		if (file.exists() && !file.isDirectory()) {
			ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream(file)));
			queryRecords = (ArrayList<Record>) ois.readObject();
			ois.close();
		}
	}

	/**
	 * Add a query record to this history.
	 * @param rec The record of a dictionary search query.
	 */
	public void addRecord(Record rec) {
		queryRecords.add(rec);
		shouldSave = true;
		shouldRefresh = true;
	}

	/**
	 * Get the whole history as a 2d matrix: each row has 3 columns: query date, query string, query type.
	 * @return The history table
	 */
	public String[][] getTable() {
		if (table == null || shouldRefresh) {
			table = new String[queryRecords.size()][3];
			for (int i = 0; i < queryRecords.size(); ++i) {
				table[i] = queryRecords.get(i).toTableRow();
			}
			shouldRefresh = false;
		}
		return table;
	}
}

/**
 * This class is a wrapper class for the history records.<br>
 * It holds record for: query date, query string and query type.<br>
 * This is serializable to save the record in a file.
 */
class Record implements Serializable {
	public LocalDateTime time;
	private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	public String query;
	public boolean isSlang;

	/**
	 * The primary constructor of this class.
	 * @param time The date and time of when the query is performed.
	 * @param query The query string.
	 * @param isSlang The type of the query: slang or definition.
	 */
	public Record(LocalDateTime time, String query, boolean isSlang) {
		this.time = time;
		this.query = query;
		this.isSlang = isSlang;
	}

	/**
	 * Convert this record to a row in the history table.
	 * @return The table row as an array of 3 strings: date time, query string, query type
	 */
	public String[] toTableRow() {
		String[] row = new String[3];
		row[0] = time.format(formatter);
		row[1] = query;
		row[2] = isSlang? "slang" : "definition";
		return row;
	}

	/**
	 * Convert a row in the history table into a Record.
	 * @param row The table row with 3 string columns: date time, query string, query time
	 * @return A new Record
	 */
	public static Record fromTableRow(String[] row) {
		return new Record(
				LocalDateTime.parse(row[0], Record.formatter),
				row[1],
				row[2].equals("slang")
		);
	}
}