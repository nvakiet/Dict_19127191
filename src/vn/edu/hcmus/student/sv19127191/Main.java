package vn.edu.hcmus.student.sv19127191;

import vn.edu.hcmus.student.sv19127191.dict.Dictionary;
import vn.edu.hcmus.student.sv19127191.ui.MainFrame;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

/**
 * vn.edu.hcmus.student.sv19127191<br/>
 * Created by Ngo Van Anh Kiet - MSSV: 19127191<br/>
 * Date 1/1/2022 - 7:18 AM<br/>
 * Description: ...<br/>
 */
public class Main {
	public static void mainTest(String[] args) {
		try {
			Dictionary dict = new Dictionary();
			dict.load();
			double[] measures = new double[1];
			for (int i = 0; i < 1; ++i) {
				long startTime = System.nanoTime();
//				ArrayList<String> res = dict.queryDefinition("it", 1f);
				String[][] table = dict.history.getTable();
				long stopTime = System.nanoTime();
//				for (String s : res) {
//					System.out.println(s);
//				}
//				dict.save();
				for (String[] row : table) {
					System.out.println(row[0] + " - " + row[1] + " - " + row[2]);
				}
				measures[i] = (stopTime - startTime) / 1_000_000_000.0;
				System.out.printf("Time elapsed: %.6f\n", measures[i]);
			}
			System.out.printf("Average time: %.6f\n", Arrays.stream(measures).average().orElse(Double.NaN));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		MainFrame frame = new MainFrame();
		frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
	}
}
