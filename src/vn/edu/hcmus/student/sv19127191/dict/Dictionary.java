package vn.edu.hcmus.student.sv19127191.dict;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.stream.Collectors;

/**
 * vn.edu.hcmus.student.sv19127191.dict<br/>
 * Created by Ngo Van Anh Kiet - MSSV: 19127191<br/>
 * Date 24/12/2021 - 7:52 AM<br/>
 * Description: A class for handling the data of slang word dictionary<br/>
 */
public class Dictionary {
	// Map from a slang to its definitions
	HashMap<String, HashSet<String>> slangDefs;
	// Map from a definition token to a set of possible slangs whose definitions contain this token
	HashMap<String, HashSet<String>> defTokens;

	public Dictionary() {
		slangDefs = new HashMap<>();
		defTokens = new HashMap<>();
	}

	public HashSet<String> getDefs(String slang) {
		HashSet<String> defs = slangDefs.get(slang);
		if (defs == null)
			return new HashSet<>();
		else return defs;
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

	public void removeSlang(String slang) throws Exception {
		// Delete the dictionary record
		HashSet<String> defSet = slangDefs.remove(slang);

		// Delete the mappings from the definition tokens to the slang
		for (String oldDef : defSet) {
			String[] tokenList = oldDef.toLowerCase().split(" ");
			for (String token : tokenList) {
				HashSet<String> slangSet = defTokens.get(token);
				slangSet.remove(slang);
				if (slangSet.isEmpty())
					defTokens.remove(token, slangSet);
			}
		}
	}

	public boolean existSlang(String slang) {
		return slangDefs.containsKey(slang);
	}

	public void overwriteSlang(String slang, String def) throws Exception {
		// Remove old record
		removeSlang(slang);

		// Add new record and mapping to dictionary
		addSlang(slang, def);
	}

	public ArrayList<String> querySlang(String slang) throws Exception {
		ArrayList<String> result = slangDefs.keySet()
				.stream()
				.filter(word -> word.contains(slang))
				.collect(Collectors.toCollection(ArrayList::new));
		return result;
	}

	public ArrayList<String> queryDefinition(String def, float miss_ratio) throws Exception {
		HashSet<String> result = null;
		HashSet<String> temp = null;
		String[] tokenList = def.toLowerCase().split(" ");
		float threshold = tokenList.length * miss_ratio;
		int count = 0;

		// Query the potential slangs from definition tokens
		for (String token : tokenList) {
			temp = defTokens.get(token);
			if (temp != null) {
				if (result == null)
					result = new HashSet<>(temp);
				else {
					result.retainAll(temp);
				}
				if (result.isEmpty()) {
					result = null;
					break;
				}
			}
			else {
				++count;
				if (count > threshold) {
					result = null;
					break;
				}
			}
		}

		// Convert the result to list
		ArrayList<String> list = new ArrayList<>();
		if (result != null)
			list.addAll(result);
		return list;
	}

	public void init() throws Exception {
		File file = new File("data/init/test.txt");
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

		save();
	}

	@SuppressWarnings("unchecked")
	public void load() throws Exception {
		File file = new File("data/saved/dictionary.dat");
		// If there's saved data, load the saved dictionary
		if (file.exists() && !file.isDirectory()) {
			ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream(file)));
			slangDefs = (HashMap<String, HashSet<String>>) ois.readObject();
			defTokens = (HashMap<String, HashSet<String>>) ois.readObject();
			ois.close();
			return;
		}
		// If there's no saved data, load the initial slang.txt
		init();
	}

	public void save() throws Exception {
		File file = new File("data/saved/dictionary.dat");
		ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(file)));

		oos.writeObject(slangDefs);
		oos.writeObject(defTokens);

		oos.close();
	}

//	public static ArrayList<ArrayList<String>> to2dList(HashMap<String, HashSet<String>> hashMap) {
//		ArrayList<ArrayList<String>> result = new ArrayList<>();
//		for (String key : hashMap.keySet()) {
//			for (String value : hashMap.get(key)) {
//				ArrayList<String> row = new ArrayList<>(2);
//				row.add(key);
//				row.add(value);
//				result.add(row);
//			}
//		}
//		return result;
//	}
}
