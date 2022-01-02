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

	public History() {
		queryRecords = new ArrayList<>();
	}

	public void save() throws Exception {
		File file = new File("data/saved/history.dat");
		ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
		oos.writeObject(queryRecords);
		oos.close();
	}

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

	public void addRecord(Record rec) {
		queryRecords.add(rec);
	}

	public String[][] getTable() {
		String[][] table = new String[queryRecords.size()][3];
		for (int i = 0; i < queryRecords.size(); ++i) {
			table[i] = queryRecords.get(i).toTableRow();
		}
		return table;
	}
}

class Record implements Serializable {
	public LocalDateTime time;
	private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
	public String query;
	public boolean isSlang;

	public Record(LocalDateTime time, String query, boolean isSlang) {
		this.time = time;
		this.query = query;
		this.isSlang = isSlang;
	}

	public String[] toTableRow() {
		String[] row = new String[3];
		row[0] = time.format(formatter);
		row[1] = query;
		row[2] = isSlang? "slang" : "definition";
		return row;
	}

	public static Record fromTableRow(String[] row) {
		return new Record(
				LocalDateTime.parse(row[0], Record.formatter),
				row[1],
				row[2].equals("slang")
		);
	}
}