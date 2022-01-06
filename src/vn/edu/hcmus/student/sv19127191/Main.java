package vn.edu.hcmus.student.sv19127191;

import vn.edu.hcmus.student.sv19127191.ui.MainFrame;
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

/**
 * vn.edu.hcmus.student.sv19127191<br/>
 * Created by Ngo Van Anh Kiet - MSSV: 19127191<br/>
 * Date 1/1/2022 - 7:18 AM<br/>
 * Description: Main class of the program<br/>
 */
public class Main {
	/**
	 * Main method of the program
	 * @param args command line arguments (doesn't use)
	 */
	public static void main(String[] args) {
		MainFrame frame = new MainFrame();
		frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
	}
}
