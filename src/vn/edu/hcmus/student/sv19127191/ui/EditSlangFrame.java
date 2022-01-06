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

	/**
	 * Get the content panel of this frame to pass into the "Edit" dialog
	 */
	public JPanel getContentPane() {
		return contentPane;
	}

	/**
	 * Get a slang from old slang text field of the dialog
	 */
	public String getOldSlang() {
		return oldField.getText();
	}

	/**
	 * Set the old slang text field of the dialog
	 * @param slang a slang selected in the slang list of dictionary UI
	 */
	public void setOldField(String slang) {
		oldField.setText(slang);
	}

	/**
	 * Get a slang from the new slang text field of the dialog
	 */
	public String getNewSlang() {
		return newField.getText();
	}
}
