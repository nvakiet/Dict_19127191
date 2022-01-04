package vn.edu.hcmus.student.sv19127191.ui;

import javax.swing.*;

/**
 * vn.edu.hcmus.student.sv19127191.ui<br/>
 * Created by Ngo Van Anh Kiet - MSSV: 19127191<br/>
 * Date 4/1/2022 - 9:05 PM<br/>
 * Description: A frame for "Edit slang" option dialog<br/>
 */
public class EditSlangFrame {
	private JPanel contentPane;
	private JTextField newField;
	private JTextField oldField;

	public JPanel getContentPane() {
		return contentPane;
	}

	public String getOldSlang() {
		return oldField.getText();
	}

	public void setOldField(String slang) {
		oldField.setText(slang);
	}

	public String getNewSlang() {
		return newField.getText();
	}
}
