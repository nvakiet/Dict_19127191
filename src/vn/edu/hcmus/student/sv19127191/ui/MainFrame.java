package vn.edu.hcmus.student.sv19127191.ui;

import vn.edu.hcmus.student.sv19127191.dict.Dictionary;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * vn.edu.hcmus.student.sv19127191.ui<br/>
 * Created by Ngo Van Anh Kiet - MSSV: 19127191<br/>
 * Date 1/1/2022 - 7:19 AM<br/>
 * Description: ...<br/>
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
	private JPanel mainDictPane;
	private JSplitPane dictSplitPane;

	public MainFrame() {
		super("Slang Dictionary");
		setIconImage(new ImageIcon("res/dictionary_appicon.png").getImage());
		setContentPane(mainPanel);
		try {
			dict = new Dictionary();
			dict.load();
			Color background = new Color(175, 255, 211);
			cardPane.setBackground(background);
			cardLayout = (CardLayout) cardPane.getLayout();
			// Setup program
			setupMenuButtons();
			setupDictionaryPane();
			setupHistoryPane();
			autosaveWithInterval(30);

			// Add window listener to perform final save on exit
			addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent e) {
					super.windowClosing(e);
					try {
						dict.save();
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
		sltdBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				cardLayout.show(cardPane, "sltdCard");
				Color background = new Color(158,222,255);
				cardPane.setBackground(background);
			}
		});

		// Quiz menu
		quizBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				cardLayout.show(cardPane, "quizCard");
				Color background = new Color(251,222,255);
				cardPane.setBackground(background);
			}
		});
	}



	private void setupDictionaryPane() {
		// Set the whole slang list to be displayed by default
		DefaultListModel<String> slangListModel = new DefaultListModel<String>();
		slangListModel.addAll(dict.getSlangList());
		slangJList = new JList<>(slangListModel);
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
					dict.reset();
					refreshBtn.doClick(); // "Click" the refresh button to refresh
				} catch (Exception ex) {
					ex.printStackTrace();
					JOptionPane.showMessageDialog(mainPanel, ex.getMessage());
				}
			}
		});
	}

	private void setupHistoryPane() {
		DefaultTableModel model = new DefaultTableModel(dict.history.getTable(), new String[] {
				"Date", "Query String", "Query Type"
		});
		histTable.setModel(model);
	}

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
				ArrayList<String> result = dict.queryDefinition(query, 0.7f);
				setJListData(slangJList, result);
			}
			setJListData(defJList, null);
			dict.history.save();
		} catch (Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(mainPanel, ex.getMessage());
		}
	}

	private void setJListData(JList<String> jList, List<String> data) {
		if (data == null) {
			jList.setModel(new DefaultListModel<String>());
			return;
		}
		DefaultListModel<String> model = ((DefaultListModel<String>) jList.getModel());
		model.clear();
		model.addAll(data);
	}

	private void autosaveWithInterval(long seconds) {
		Thread t = new Thread(() -> {
			try {
				long milis = seconds * 1000;
				while (true) {
					dict.save();
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
}
