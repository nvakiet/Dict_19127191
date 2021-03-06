package vn.edu.hcmus.student.sv19127191.ui;

import vn.edu.hcmus.student.sv19127191.dict.Dictionary;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

/**
 * vn.edu.hcmus.student.sv19127191.ui<br/>
 * Created by Ngo Van Anh Kiet - MSSV: 19127191<br/>
 * Date 1/1/2022 - 7:19 AM<br/>
 * Description: The main GUI class of the program, also containing logics for Dictionary<br/>
 */
public class MainFrame extends JFrame {
	Dictionary dict;
	CardLayout cardLayout;
	private JPanel mainPanel;
	private JPanel cardPane;
	private JButton dictBtn;
	private JButton histBtn;
	private JButton sltdBtn;
	private JButton quizBtn;
	private JPanel dictPanel;
	private JPanel histPanel;
	private JPanel sltdPanel;
	private JPanel quizPanel;
	private JList<String> slangJList;
	private JList<String> defJList;
	private JButton delDefBtn;
	private JButton refreshBtn;
	private JComboBox<String> queryTypeBox;
	private JTextField searchField;
	private JButton searchBtn;
	private JButton resetBtn;
	private JButton addBtn;
	private JButton editBtn;
	private JButton delSlangBtn;
	private JTable histTable;
	private JLabel randomSlang;
	private JList<String> randDefJList;
	private JButton quiz2Btn;
	private JButton quiz1Btn;
	private JButton btnB;
	private JButton btnA;
	private JButton btnC;
	private JButton btnD;
	private JPanel headerDictPane;
	private JScrollPane tableScroll;
	private JScrollPane ranDefScroll;
	private JTextPane quizQuestion;
	private JPanel mainDictPane;
	private JSplitPane dictSplitPane;

	/**
	 * The only constructor of this frame. Initialize the frame with this to start the program.
	 */
	public MainFrame() {
		super("Slang Dictionary");
		setIconImage(new ImageIcon("res/dictionary_appicon.png").getImage());
		setContentPane(mainPanel);
		try {
			dict = new Dictionary();
			dict.load("data/saved/dictionary.dat");
			Color background = new Color(175, 255, 211);
			cardPane.setBackground(background);
			cardLayout = (CardLayout) cardPane.getLayout();
			// Setup program
			setupMenuButtons();
			setupDictionaryPane();
			// Display whole dictionary at the beginning
			refreshBtn.doClick();
			setupHistoryPane();
			setupQuizPane();
			autosaveWithInterval(30);

			// Add window listener to perform final save on exit
			addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent e) {
					super.windowClosing(e);
					try {
						dict.save(false);
					} catch (Exception ex) {
						ex.printStackTrace();
						JOptionPane.showMessageDialog(mainPanel, "Error while saving dictionary: "+ ex.getMessage()
								+ "\nAny changes to the dictionary may not be saved. Please restart the program.");
					}
					e.getWindow().dispose();
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(mainPanel, e.getMessage());
		}
	}

	/**
	 * Set up the listeners and logic for main menu buttons: Dictionary, History, Slang of The Day, Quiz
	 */
	private void setupMenuButtons() {
		// Dictionary Menu
		dictBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				cardLayout.show(cardPane, "dictCard");
				Color background = new Color(175,255,211);
				cardPane.setBackground(background);
			}
		});

		// History menu
		// Also config the table to not be editable
		JTextField tf = new JTextField();
		tf.setEditable(false);
		DefaultCellEditor editor = new DefaultCellEditor( tf );
		histTable.setDefaultEditor(Object.class, editor);
		histBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				cardLayout.show(cardPane, "histCard");
				Color background = new Color(255,249,175);
				cardPane.setBackground(background);
				tableScroll.getViewport().setBackground(background);
				setupHistoryPane();
			}
		});

		// "Slang of the day" menu
		ranDefScroll.setPreferredSize(new Dimension(600, 400));
		randDefJList.setModel(new DefaultListModel<String>());
		sltdBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				cardLayout.show(cardPane, "sltdCard");
				Color background = new Color(158,222,255);
				cardPane.setBackground(background);
				String dailySlang = dict.dailyRandomSlang();
				randomSlang.setText(dailySlang);
				setJListData(randDefJList, dict.getDefs(dailySlang));
			}
		});

		// Quiz menu
		quizBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				cardLayout.show(cardPane, "quizCard");
				Color background = new Color(251,222,255);
				cardPane.setBackground(background);
				quizQuestion.setText("Choose a quiz type");
				btnA.setText("Answer A");
				btnB.setText("Answer B");
				btnC.setText("Answer C");
				btnD.setText("Answer D");
			}
		});
	}

	/**
	 * Set up the Dictionary UI: GUI compositions, listeners, logic.
	 */
	private void setupDictionaryPane() {
		// Set the whole slang list to be displayed by default
		slangJList = new JList<>(new DefaultListModel<String>());
		defJList = new JList<>(new String[]{"Select a slang on the left list to see its definitions."});
		slangJList.setSelectionMode(DefaultListSelectionModel.SINGLE_SELECTION);
		defJList.setSelectionMode(DefaultListSelectionModel.SINGLE_SELECTION);
		JScrollPane jsrlpSlang = new JScrollPane(slangJList,ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		JScrollPane jsrlpDef = new JScrollPane(defJList,ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		jsrlpSlang.setPreferredSize(new Dimension(400, 400));
		jsrlpDef.setPreferredSize(new Dimension(400, 400));
		dictSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		dictSplitPane.setLeftComponent(jsrlpSlang);
		dictSplitPane.setRightComponent(jsrlpDef);
		mainDictPane = new JPanel();
		mainDictPane.setLayout(new BoxLayout(mainDictPane, BoxLayout.PAGE_AXIS));
		mainDictPane.add(dictSplitPane);
		dictPanel.add(mainDictPane, BorderLayout.CENTER);

		// Add a listener to show definition on the right list
		slangJList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (!e.getValueIsAdjusting()) {
					String selected = slangJList.getSelectedValue();
					if (selected == null) return;
					DefaultListModel<String> model = new DefaultListModel<>();
					model.addAll(dict.getDefs(selected));
					defJList.setModel(model);
				}
			}
		});

		// Add listener for search feature
		searchBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				doSearch(e);
			}
		});
		searchField.addActionListener(searchBtn.getActionListeners()[0]);

		// Add listener for refresh feature
		refreshBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setJListData(slangJList, dict.getSlangList());
				setJListData(defJList, null);
			}
		});

		// Add listener for reset feature
		resetBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					boolean success = dict.load("data/init/original.dat");
					if (!success) {
						int opt = JOptionPane.showConfirmDialog(
								mainPanel,
								"Can't find data/init/original.dat\n" +
										"Will try to reinitialize dictionary using data/init/slang.txt\n" +
										"Proceed?",
								"Unsuccessful reset",
								JOptionPane.YES_NO_OPTION
						);
						if (opt == JOptionPane.YES_OPTION)
							dict.init();
					}
					refreshBtn.doClick(); // "Click" the refresh button to refresh
				} catch (Exception ex) {
					ex.printStackTrace();
					JOptionPane.showMessageDialog(mainPanel, ex.getMessage());
				}
			}
		});

		// Add listener for "Add slang" feature
		addBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				doAddSlang();
			}
		});

		// Add listener for "Edit slang" feature
		editBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				doEdit();
			}
		});

		// Add listener for "Delete slang" feature
		delSlangBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				doRemoveSlang();
			}
		});

		// Add listener for "Delete definition" feature
		delDefBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				doRemoveDef();
			}
		});
	}

	/**
	 * Set up the History UI: prepare history data to display
	 */
	private void setupHistoryPane() {
		DefaultTableModel model = new DefaultTableModel(dict.history.getTable(), new String[] {
				"Date", "Query String", "Query Type"
		});
		histTable.setModel(model);
	}

	/**
	 * Perform a search in the dictionary. This is for using with an Action Listener.
	 * @param e An event detected by the action listener
	 */
	private void doSearch(ActionEvent e) {
		try {
			String type = (String) queryTypeBox.getSelectedItem();
			if (type == null) return;
			String query = searchField.getText();
			if (type.equals("slang")) {
				ArrayList<String> result = dict.querySlang(query);
				setJListData(slangJList, result);
			}
			else if (type.equals("definition")) {
				ArrayList<String> result = dict.queryDefinition(query, 0.2f);
				setJListData(slangJList, result);
			}
			setJListData(defJList, null);
			dict.history.save();
		} catch (Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(mainPanel, ex.getMessage());
		}
	}

	/**
	 * Set the data for a JList to display.
	 * @param jList The target JList used for displaying data.
	 * @param data The data to display. If it's null, then JList is empty.
	 */
	private void setJListData(JList<String> jList, List<String> data) {
		if (data == null) {
			jList.setModel(new DefaultListModel<String>());
			return;
		}
		DefaultListModel<String> model = ((DefaultListModel<String>) jList.getModel());
		model.clear();
		model.addAll(data);
	}

	/**
	 * Set the auto-saving interval of the Dictionary.
	 * The Dictionary will automatically save the data after a number of seconds, if there're changes in the dictionary.
	 * @param seconds The time between each save-checking.
	 */
	private void autosaveWithInterval(long seconds) {
		Thread t = new Thread(() -> {
			try {
				long milis = seconds * 1000;
				while (true) {
					dict.save(false);
					Thread.sleep(milis);
				}
			} catch (Exception e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(mainPanel, "Error while saving dictionary: "+ e.getMessage()
						+ "\nAny changes to the dictionary may not be saved. Please restart the program.");
			}
		});
		t.setDaemon(true);
		t.start();
	}

	/**
	 * Perform the "Add slang" feature in the Dictionary UI
	 */
	private void doAddSlang() {
		try {
			AddSlangFrame addFrame = new AddSlangFrame();
			addFrame.setSlangField(slangJList.getSelectedValue());
			int input = JOptionPane.showConfirmDialog(mainPanel,
					addFrame.getContentPane(),
					"Add new slang",
					JOptionPane.OK_CANCEL_OPTION,
					JOptionPane.PLAIN_MESSAGE);
			if (input == JOptionPane.OK_OPTION) {
				String inputSlang = addFrame.getSlang();
				String inputDef = addFrame.getDef();
				if (dict.existSlang(inputSlang)) {
					String[] options = {"Overwrite", "Add definition", "Cancel"};
					int opt = JOptionPane.showOptionDialog(
							mainPanel,
							"The slang already exists."
									+ "\nOverwrite the old slang or add a new definition to this slang?",
							"Duplicate Slang",
							JOptionPane.YES_NO_CANCEL_OPTION,
							JOptionPane.QUESTION_MESSAGE,
							null,
							options,
							null
					);
					if (opt == JOptionPane.YES_OPTION)
						dict.overwriteSlang(inputSlang, inputDef);
					else if (opt == JOptionPane.NO_OPTION)
						dict.addSlang(inputSlang, inputDef);
				}
				else {
					dict.addSlang(inputSlang, inputDef);
				}
				refreshBtn.doClick();
			}
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(mainPanel, e.getMessage());
		}
	}

	/**
	 * Perform the "Edit slang" feature in the Dictionary UI
	 */
	private void doEdit() {
		try {
			EditSlangFrame editSlangFrame = new EditSlangFrame();
			editSlangFrame.setOldField(slangJList.getSelectedValue());
			int input = JOptionPane.showConfirmDialog(mainPanel,
					editSlangFrame.getContentPane(),
					"Change slang",
					JOptionPane.OK_CANCEL_OPTION,
					JOptionPane.PLAIN_MESSAGE);
			if (input == JOptionPane.OK_OPTION) {
				String oldSlang = editSlangFrame.getOldSlang();
				String newSlang = editSlangFrame.getNewSlang();
				if (dict.existSlang(newSlang)) {
					int opt = JOptionPane.showConfirmDialog(
							mainPanel,
							"The new slang already exists."
									+ "\nDo you want to merge definitions of old slang into new slang?",
							"Duplicate slang",
							JOptionPane.YES_NO_OPTION);
					if (opt == JOptionPane.NO_OPTION)
						return;
				}
				dict.changeSlang(oldSlang, newSlang);
				refreshBtn.doClick();
			}
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(mainPanel, e.getMessage());
		}
	}

	/**
	 * Perform the "Remove slang" feature in the Dictionary UI
	 */
	private void doRemoveSlang() {
		try {
			String selectedSlang = slangJList.getSelectedValue();
			if (selectedSlang == null)
				return;
			int opt = JOptionPane.showConfirmDialog(
					mainPanel,
					"Do you want to remove this slang?",
					"Confirmation",
					JOptionPane.YES_NO_OPTION);
			if (opt == JOptionPane.YES_OPTION) {
				dict.removeSlang(selectedSlang);
				((DefaultListModel<String>) slangJList.getModel()).remove(slangJList.getSelectedIndex());
				setJListData(defJList, null);
			}
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(mainPanel, e.getMessage());
		}
	}

	/**
	 * Perform the "Remove definition of a slang" feature in the Dictionary UI
	 */
	private void doRemoveDef() {
		try {
			String selectedSlang = slangJList.getSelectedValue();
			String selectedDef = defJList.getSelectedValue();
			if (selectedSlang == null || selectedDef == null)
				return;
			int opt = JOptionPane.showConfirmDialog(
					mainPanel,
					"Do you want to remove this definition?",
					"Confirmation",
					JOptionPane.YES_NO_OPTION);
			if (opt == JOptionPane.YES_OPTION) {
				dict.removeDefinition(selectedSlang, selectedDef);
				DefaultListModel<String> model = (DefaultListModel<String>) defJList.getModel();
				model.remove(defJList.getSelectedIndex());
				if (model.isEmpty())
					((DefaultListModel<String>) slangJList.getModel()).remove(slangJList.getSelectedIndex());
			}
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(mainPanel, e.getMessage());
		}
	}

	/**
	 * Set up the Quiz panel: randomize slangs and definitions from the Dictionary, make action listeners
	 * Also display the quiz question and answers.
	 */
	private void setupQuizPane() {
		// Setup the UI of quiz panel, quiz question should be centered
		StyledDocument doc = quizQuestion.getStyledDocument();
		SimpleAttributeSet center = new SimpleAttributeSet();
		StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
		doc.setParagraphAttributes(0, doc.getLength(), center, false);

		// Create arrays of 4 slangs and definitions
		String[] slangs = new String[4];
		String[] defs = new String[4];
		// Make a button array for randomizer
		JButton[] buttons = {btnA, btnB, btnC, btnD};
		// Create a randomizer
		Random random = new Random();
		final int[] correct = new int[1];
		final boolean[] mode = new boolean[1]; //Mode: false = guess definition, true = guess slang
		correct[0] = -1;

		// Add listener to trigger quiz "Guess definition of a slang"
		quiz1Btn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// Prepare 4 slangs and definitions of these slangs
				// Prepare 4 definitions of 4 slangs
				mode[0] = false;
				for (int i = 0; i < 4; ++i) {
					slangs[i] = dict.getRandomSlang();
					defs[i] = dict.getRandomDefinition(slangs[i]);
				}
				// Randomize a correct answer in these 4 slangs
				correct[0] = random.nextInt(4);

				// Set the correct slang as question
				quizQuestion.setText(slangs[correct[0]]);
				// Set the definitions as texts for 4 answer buttons
				for (int i = 0; i < 4; ++i) {
					// If the string is too long, set the first 50 characters.
					// The whole string will be in tooltips
					if (defs[i].length() > 50) {
						buttons[i].setText(defs[i].substring(0, 50) + "...");
					}
					else {
						buttons[i].setText(defs[i]);
					}
					buttons[i].setToolTipText("<html><p width=\"300\">" +defs[i]+"</p></html>");
				}
			}
		});

		// Add listener to trigger quiz "Guess slang for a definition"
		quiz2Btn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// Prepare 4 slangs and definitions of these slangs
				// Prepare 4 definitions of 4 slangs
				mode[0] = true;
				for (int i = 0; i < 4; ++i) {
					slangs[i] = dict.getRandomSlang();
					defs[i] = dict.getRandomDefinition(slangs[i]);
				}
				// Randomize a correct answer in these 4 slangs
				correct[0] = random.nextInt(4);

				// Set the correct definition as question
				quizQuestion.setText(defs[correct[0]]);
				// Set the slangs as texts for 4 answer buttons
				for (int i = 0; i < 4; ++i) {
					// If the string is too long, set the first 50 characters.
					// The whole string will be in tooltips
					if (slangs[i].length() > 50) {
						buttons[i].setText(slangs[i].substring(0, 50) + "...");
					}
					else {
						buttons[i].setText(slangs[i]);
					}
					buttons[i].setToolTipText("<html><p width=\"300\">" +slangs[i]+"</p></html>");
				}
			}
		});

		// Set action listeners for answer buttons
		for (int i = 0; i < 4; ++i) {
			int finalI = i;
			buttons[i].addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (correct[0] == -1)
						return;
					if (correct[0] == finalI) {
						JOptionPane.showMessageDialog(mainPanel, "You're correct!");
					}
					else {
						String correctAnswer = !mode[0] ? defs[correct[0]] : slangs[correct[0]];
						JOptionPane.showMessageDialog(mainPanel, "<html><p width=\"400\">" +
								"You're wrong!<br>" +
								"The correct answer is \"" + correctAnswer + "\"" +
								"</p></html>");
					}
					correct[0] = -1;
					quizBtn.doClick();
				}
			});
		}
	}
}
