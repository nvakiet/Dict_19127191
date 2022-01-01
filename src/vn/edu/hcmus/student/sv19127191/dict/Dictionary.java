package vn.edu.hcmus.student.sv19127191.dict;

import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeMap;

/**
 * vn.edu.hcmus.student.sv19127191.dict<br/>
 * Created by Ngo Van Anh Kiet - MSSV: 19127191<br/>
 * Date 24/12/2021 - 7:52 AM<br/>
 * Description: A class for handling the data of slang word dictionary<br/>
 */
public class Dictionary {
	// Map from a slang to its definitions
	TreeMap<String, HashSet<String>> slangDefs;
	// Map from a definition token to a set of possible slangs whose definitions contain this token
	HashMap<String, HashSet<String>> defTokens;

	public Dictionary() {
		slangDefs = new TreeMap<>();
		defTokens = new HashMap<>();
	}

	public void addSlang(String slang, String def) throws Exception {
		HashSet<String> defSet = slangDefs.get(slang);
		// If the word doesn't exist in dictionary yet
		if (defSet == null) {
			defSet = new HashSet<>();
			defSet.add(def);
			slangDefs.put(slang, defSet);
		}
		else {
			defSet.add(def);
		}

		// Break down the new definition into tokens then map those tokens to the slang
		String[] tokenList = def.toLowerCase().split(" ");
		for (String token : tokenList) {
			HashSet<String> slangSet = defTokens.get(token);
			if (slangSet == null) {
				slangSet = new HashSet<>();
				slangSet.add(slang);
				defTokens.put(token, slangSet);
			}
			else {
				slangSet.add(slang);
			}
		}
	}

	public void addSlang(String slang, String[] multiDef) throws Exception {
		for (String def : multiDef) {
			addSlang(slang, def);
		}
	}

	public boolean existSlang(String slang) {
		return slangDefs.containsKey(slang);
	}

	public void overwriteSlang(String slang, String def) throws Exception {
		// Delete the old dictionary record
		HashSet<String> defSet = slangDefs.remove(slang);

		// Delete the mappings from the old definition tokens to the slang
		for (String oldDef : defSet) {
			String[] tokenList = oldDef.toLowerCase().split(" ");
			for (String token : tokenList) {
				defTokens.get(token).remove(slang);
			}
		}

		// Add new record and mapping to dictionary
		addSlang(slang, def);
	}

	public void init() throws Exception {
		File file = new File("/data/init/slang.txt");
		if (file.exists() && !file.isDirectory()) {
			BufferedReader br = new BufferedReader(new FileReader(file));

			//Extract each line from the text file
			String line;
			while ((line = br.readLine()) != null) {
				// [0] = slang, [1] = definitions (need to be splitted further)
				String[] slangSplit = line.split("`");
				if (slangSplit.length != 2) continue;
				String slang = slangSplit[0];
				String[] defs = slangSplit[1].split("\\s*\\|\\s*");
				addSlang(slang, defs);
			}

			br.close();
		}
		else {
			throw new Exception("No data file found. Please put a slang.txt file in data/init");
		}
	}

	@SuppressWarnings("unchecked")
	public void load() throws Exception {
		File file = new File("/data/saved/dictionary.dat");
		// If there's saved data, load the saved dictionary
		if (file.exists() && !file.isDirectory()) {
			ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream(file)));
			slangDefs = (TreeMap<String, HashSet<String>>) ois.readObject();
			defTokens = (HashMap<String, HashSet<String>>) ois.readObject();
			ois.close();
			return;
		}
		// If there's no saved data, load the initial slang.txt
		init();
	}

	public void save() throws Exception {
		File file = new File("/data/saved/dictionary.dat");
		ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(file)));

		oos.writeObject(slangDefs);
		oos.writeObject(defTokens);

		oos.close();
	}
}
