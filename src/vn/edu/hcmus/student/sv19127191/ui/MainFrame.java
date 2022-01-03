package vn.edu.hcmus.student.sv19127191.ui;

import vn.edu.hcmus.student.sv19127191.dict.Dictionary;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
	private JList slangJList;
	private JList defJList;
	private JButton delDefBtn;
	private JButton refreshBtn;
	private JComboBox queryTypeBox;
	private JTextField searchField;
	private JButton searchBtn;
	private JButton resetBtn;
	private JButton addBtn;
	private JButton editBtn;
	private JButton delSlangBtn;
	private JTable histTable;
	private JLabel randomSlang;
	private JList randDefJList;
	private JButton quiz2Btn;
	private JButton quiz1Btn;
	private JButton btnB;
	private JButton btnA;
	private JButton btnC;
	private JButton btnD;

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
}
