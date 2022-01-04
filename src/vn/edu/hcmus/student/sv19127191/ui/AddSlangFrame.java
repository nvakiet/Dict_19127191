package vn.edu.hcmus.student.sv19127191.ui;

import javax.swing.*;

/**
 * vn.edu.hcmus.student.sv19127191.ui<br/>
 * Created by Ngo Van Anh Kiet - MSSV: 19127191<br/>
 * Date 4/1/2022 - 7:01 PM<br/>
 * Description: A frame for "add slang" option dialog<br/>
 */
public class AddSlangFrame {
	private JPanel contentPane;
	private JTextField slangField;
	private JTextField defField;

	public String getSlang() {
		return slangField.getText();
	}

	public void setSlangField(String slang) {
		slangField.setText(slang);
	}

	public String getDef() {
		return defField.getText();
	}

	public JPanel getContentPane() {
		return contentPane;
	}
}
