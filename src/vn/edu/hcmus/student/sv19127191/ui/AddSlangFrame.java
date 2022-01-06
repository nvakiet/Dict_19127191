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

	/**
	 * Get a slang from the slang text field of "Add" dialog
	 */
	public String getSlang() {
		return slangField.getText();
	}

	/**
	 * Set the slang text field in the "Add" dialog
	 * @param slang a slang selected in the slang list of dictionary UI
	 */
	public void setSlangField(String slang) {
		slangField.setText(slang);
	}

	/**
	 * Get a definition from the definition text field of "Add" dialog
	 */
	public String getDef() {
		return defField.getText();
	}

	/**
	 * Get the content panel of this frame to pass into the "Add" dialog
	 */
	public JPanel getContentPane() {
		return contentPane;
	}
}
