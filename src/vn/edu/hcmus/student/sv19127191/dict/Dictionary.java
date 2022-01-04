package vn.edu.hcmus.student.sv19127191.dict;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * vn.edu.hcmus.student.sv19127191.dict<br/>
 * Created by Ngo Van Anh Kiet - MSSV: 19127191<br/>
 * Date 24/12/2021 - 7:52 AM<br/>
 * Description: A class for handling the data of slang word dictionary<br/>
 */
public class Dictionary {
	// Map from a slang to its definitions
	private TreeMap<String, HashSet<String>> slangDefs;
	// Map from a definition token to a set of possible slangs whose definitions contain this token
	private HashMap<String, HashSet<String>> defTokens;
	// History of queries
	public final History history;
	// For randomizer methods
	private LocalDate lastRandomDate = LocalDate.MIN;
	private String dailySlang = "";
	private final Random random = new Random();
	private boolean shouldRefresh = false;
	private boolean shouldSave = false;
	private List<String> slangList = null;

	public Dictionary() {
		slangDefs = new TreeMap<>();
		defTokens = new HashMap<>();
		history = new History();
	}

	public List<String> getSlangList() {
		if (slangList == null || shouldRefresh) {
			slangList = new ArrayList<>(slangDefs.navigableKeySet());
			shouldRefresh = false;
		}
		return slangList;
	}

	public List<String> getDefs(String slang) {
		List<String> defs = new ArrayList<>(slangDefs.get(slang));
		defs.sort(new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				return o1.compareTo(o2);
			}
		});
		return defs;
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

		shouldRefresh = true;
		shouldSave = true;
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
				// Avoid repeating tokens whose mappings have been deleted
				if (slangSet != null) {
					slangSet.remove(slang);
					if (slangSet.isEmpty())
						defTokens.remove(token, slangSet);
				}
			}
		}

		shouldRefresh = true;
		shouldSave = true;
	}

	public void removeDefinition(String slang, String def) throws Exception {
		// Remove the definition from the slang def set
		slangDefs.get(slang).remove(def);
		if (slangDefs.get(slang).isEmpty()) {
			slangDefs.remove(slang);
		}

		// Remove the mapping from this slang's tokens to the slang
		String[] tokenList = def.split(" ");
		for (String token : tokenList) {
			HashSet<String> slangSet = defTokens.get(token);
			slangSet.remove(slang);
			if (slangSet.isEmpty())
				defTokens.remove(token, slangSet);
		}

		shouldRefresh = true;
		shouldSave = true;
	}

	public void changeSlang(String oldSlang, String newSlang) throws Exception {
		HashSet<String> defSet = slangDefs.get(oldSlang);
		if (defSet == null)
			throw new Exception("This slang doesn't exist in the database. Please add a new slang.");
		removeSlang(oldSlang);
		addSlang(newSlang, defSet.toArray(String[]::new));
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
		history.addRecord(new Record(LocalDateTime.now(), slang, true));
		getSlangList();
		return slangList.stream()
				.filter(word -> word.contains(slang))
				.collect(Collectors.toCollection(ArrayList::new));
	}

	public ArrayList<String> queryDefinition(String def, float miss_ratio) throws Exception {
		history.addRecord(new Record(LocalDateTime.now(), def, false));
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
		shouldSave = true;
		save();
	}

	public void reset() throws Exception {
		slangDefs.clear();
		defTokens.clear();
		init();
	}

	@SuppressWarnings("unchecked")
	public void load() throws Exception {
		history.load();
		File file = new File("data/saved/dictionary.dat");
		// If there's saved data, load the saved dictionary
		if (file.exists() && !file.isDirectory()) {
			ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream(file)));
			slangDefs = (TreeMap<String, HashSet<String>>) ois.readObject();
			defTokens = (HashMap<String, HashSet<String>>) ois.readObject();
			lastRandomDate = (LocalDate) ois.readObject();
			dailySlang = (String) ois.readObject();
			ois.close();
			return;
		}
		// If there's no saved data, load the initial slang.txt
		init();
		getSlangList();
	}

	public synchronized void save() throws Exception {
		if (!shouldSave) return;
		File file = new File("data/saved/dictionary.dat");
		ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(file)));

		oos.writeObject(slangDefs);
		oos.writeObject(defTokens);
		oos.writeObject(lastRandomDate);
		oos.writeObject(dailySlang);

		oos.close();
		history.save();
		System.out.println("SAVED");
		shouldSave = false;
	}

	public String getRandomSlang() {
		getSlangList();
		return slangList.get(random.nextInt(slangList.size()));
	}

	public String getRandomDefinition(String slang) {
		List<String> defList = getDefs(slang).stream().toList();
		if (defList.isEmpty())
			return "";
		return defList.get(random.nextInt(defList.size()));
	}

	public String dailyRandomSlang() {
		LocalDate today = LocalDate.now();
		// If it's a new day since the last daily random or it's the first time running
		if (today.isAfter(lastRandomDate)
				|| (lastRandomDate.equals(LocalDate.MIN) && dailySlang.equals(""))) {
			lastRandomDate = today;
			dailySlang = getRandomSlang();
		}
		return dailySlang;
	}
}
