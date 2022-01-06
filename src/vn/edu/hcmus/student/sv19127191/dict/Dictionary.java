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

	/**
	 * Default constructor of the dictionary.
	 */
	public Dictionary() {
		slangDefs = new TreeMap<>();
		defTokens = new HashMap<>();
		history = new History();
	}

	/**
	 * Get the whole list of slang words from the dictionary.
	 * @return a list of slang words (without their definitions)
	 */
	public List<String> getSlangList() {
		if (slangList == null || shouldRefresh) {
			slangList = new ArrayList<>(slangDefs.navigableKeySet());
			shouldRefresh = false;
		}
		return slangList;
	}

	/**
	 * Get the definitions of a slang word in the dictionary
	 * @param slang The slang word to query
	 * @return A list of definitions for the input slang word
	 */
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

	/**
	 * Add a slang word with a definition into the dictionary. This can also be used to add a definition to an existing slang word.
	 * @param slang The slang word to add to the dictionary
	 * @param def The definition of the input slang
	 */
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

	/**
	 * Add a slang word with multiple definitions to the dictionary. This can be used to add multiple definitions to an existing slang.
	 * @param slang A slang word to add to the dictionary.
	 * @param multiDef An array of definitions of the input slang word.
	 */
	public void addSlang(String slang, String[] multiDef) throws Exception {
		for (String def : multiDef) {
			addSlang(slang, def);
		}
	}

	/**
	 * Remove a slang word from the dictionary, along with its definitions.
	 * @param slang The slang word to remove.
	 */
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

	/**
	 * Remove a definition of a slang word. If the slang word has 0 definition left, the slang will also be removed from the dictionary.
	 * @param slang The slang word with the definition to be removed.
	 * @param def The definition to be removed from the input slang.
	 */
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
			if (slangSet != null) {
				slangSet.remove(slang);
				if (slangSet.isEmpty())
					defTokens.remove(token, slangSet);
			}
		}

		shouldRefresh = true;
		shouldSave = true;
	}

	/**
	 * Change a slang word into a new slang, keeping its definitions. If the new slang already exists, the old slang definitions will be merged into the new slang.
	 * @param oldSlang The slang to change from
	 * @param newSlang The slang to change to
	 */
	public void changeSlang(String oldSlang, String newSlang) throws Exception {
		HashSet<String> defSet = slangDefs.get(oldSlang);
		if (defSet == null)
			throw new Exception("This slang doesn't exist in the database. Please add a new slang.");
		removeSlang(oldSlang);
		addSlang(newSlang, defSet.toArray(String[]::new));
	}

	/**
	 * Check if a slang word exists in the dictionary
	 * @param slang The dictionary to check for its existence
	 * @return True if the slang exists, false if it doesn't exist
	 */
	public boolean existSlang(String slang) {
		return slangDefs.containsKey(slang);
	}

	/**
	 * Overwrite the old definitions of a slang with a new definition.
	 * @param slang The slang to be overwritten
	 * @param def The new definition of the input slang
	 */
	public void overwriteSlang(String slang, String def) throws Exception {
		// Remove old record
		removeSlang(slang);

		// Add new record and mapping to dictionary
		addSlang(slang, def);
	}

	/**
	 * Search for a slang word in the dictionary.
	 * @param slang The query to perform search.
	 * @return A list of potential slang words that fit the query
	 */
	public ArrayList<String> querySlang(String slang) throws Exception {
		history.addRecord(new Record(LocalDateTime.now(), slang, true));
		getSlangList();
		return slangList.stream()
				.filter(word -> word.contains(slang))
				.collect(Collectors.toCollection(ArrayList::new));
	}

	/**
	 * Search for a slang whose definitions containing the definition query.
	 * @param def The query to perform search
	 * @param miss_ratio A float value between 0 and 1. The search result will be empty if the missing times (when a token in the query returns nothing) is more than the number of tokens in the query * miss ratio.
	 * @return A list of potential slang words that fit the query
	 */
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

	/**
	 * Initialize the dictionary data with a file in data/init/slang.txt.<br>
	 * The data in that file must be in the following format for each row:<br>
	 * Slang`Definition1|Definition2|...<br>
	 * Where "`" is the delimiter character between a slang and its definitions.<br>
	 * And "|" is the delimiter character between definitions of a slang.
	 */
	public void init() throws Exception {
		File file = new File("data/init/slang.txt");
		if (file.exists() && !file.isDirectory()) {
			slangDefs.clear();
			defTokens.clear();

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
		save(true);
	}

	/**
	 * Load the dictionary data with a *.dat file (typically in data/init or data/saved).
	 * @param filepath The filepath to load the dictionary data
	 * @return True if the operation succeeds, false if it fails.
	 */
	@SuppressWarnings("unchecked")
	public boolean load(String filepath) throws Exception {
		history.load();
		File file = new File(filepath);
		// If there's saved data, load the saved dictionary
		if (file.exists() && !file.isDirectory()) {
			ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream(file)));
			slangDefs = (TreeMap<String, HashSet<String>>) ois.readObject();
			defTokens = (HashMap<String, HashSet<String>>) ois.readObject();
			lastRandomDate = (LocalDate) ois.readObject();
			dailySlang = (String) ois.readObject();
			ois.close();
			shouldRefresh = true;
			return true;
		}
		else if (filepath.equals("data/init/original.dat")) {
			// If this is already trying to load the original data file
			// That means there's no orginal.dat to load
			return false;
		}
		// If there's no saved data, try to load the initial data file
		if (!load("data/init/original.dat")) {
			// If there's no original data, initialize data from slang.txt
			init();
		}
		shouldRefresh = true;
		getSlangList();
		return true;
	}

	/**
	 * Save the dictionary data to a *.dat file. If the flag firstTime is set, it will also save to another file called original.dat.<br>
	 * The save location is in data/saved, or data/init for original.dat.
	 * @param firstTime Signal whether this save is for the first time the dictionary is started.
	 */
	public synchronized void save(boolean firstTime) throws Exception {
		if (!shouldSave) return;
		File file = new File("data/saved/dictionary.dat");
		ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
		oos.writeObject(slangDefs);
		oos.writeObject(defTokens);
		oos.writeObject(lastRandomDate);
		oos.writeObject(dailySlang);
		oos.close();
		if (firstTime) {
			lastRandomDate = LocalDate.MIN;
			dailySlang = "";
			file = new File("data/init/original.dat");
			oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
			oos.writeObject(slangDefs);
			oos.writeObject(defTokens);
			oos.writeObject(lastRandomDate);
			oos.writeObject(dailySlang);
			oos.close();
		}
		history.save();
		System.out.println("SAVED");
		shouldSave = false;
	}

	/**
	 * Get a random slang word from the dictionary
	 * @return A random slang word
	 */
	public String getRandomSlang() {
		getSlangList();
		return slangList.get(random.nextInt(slangList.size()));
	}

	/**
	 * Get a random definition from the definition set of a slang word in the dictionary.
	 * @param slang The slang to get the random definition
	 * @return A random definition of the input slang
	 */
	public String getRandomDefinition(String slang) {
		List<String> defList = getDefs(slang).stream().toList();
		if (defList.isEmpty())
			return "";
		return defList.get(random.nextInt(defList.size()));
	}

	/**
	 * Get a random slang word of a day. If the local date of the computer hasn't changed to the day after, it will return the same slang.
	 * @return A random daily slang word
	 */
	public String dailyRandomSlang() {
		LocalDate today = LocalDate.now();
		// If it's a new day since the last daily random or it's the first time running
		if (today.isAfter(lastRandomDate)
				|| (lastRandomDate.equals(LocalDate.MIN) && dailySlang.equals(""))) {
			lastRandomDate = today;
			dailySlang = getRandomSlang();
			shouldSave = true;
		}
		return dailySlang;
	}
}
