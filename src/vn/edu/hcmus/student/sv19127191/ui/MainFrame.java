package vn.edu.hcmus.student.sv19127191.ui;

import vn.edu.hcmus.student.sv19127191.dict.Dictionary;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
			configMenuButtons();
			configDictionaryPane();
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(mainPanel, e.getMessage());
		}
	}

	private void configMenuButtons() {
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
		histBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				cardLayout.show(cardPane, "histCard");
				Color background = new Color(255,249,175);
				cardPane.setBackground(background);
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

	private void configDictionaryPane() {
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
		} catch (Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(mainPanel, ex.getMessage());
		}
	}

	private void setJListData(JList<String> jList, List<String> data) {
		DefaultListModel<String> model = (DefaultListModel<String>) jList.getModel();
		model.clear();
		model.addAll(data);
	}
}
