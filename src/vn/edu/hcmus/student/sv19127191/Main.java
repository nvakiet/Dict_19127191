package vn.edu.hcmus.student.sv19127191;

import vn.edu.hcmus.student.sv19127191.dict.Dictionary;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * vn.edu.hcmus.student.sv19127191<br/>
 * Created by Ngo Van Anh Kiet - MSSV: 19127191<br/>
 * Date 1/1/2022 - 7:18 AM<br/>
 * Description: ...<br/>
 */
public class Main {
	public static void main(String[] args) {
		try {
			Dictionary dict = new Dictionary();
			dict.load();
			double[] measures = new double[1];
			for (int i = 0; i < 1; ++i) {
				long startTime = System.nanoTime();
				ArrayList<String> res = dict.queryDefinition("it", 1f);
				long stopTime = System.nanoTime();
				for (String s : res) {
					System.out.println(s);
				}
				measures[i] = (stopTime - startTime) / 1_000_000_000.0;
				System.out.printf("Time elapsed: %.6f\n", measures[i]);
			}
			System.out.printf("Average time: %.6f\n", Arrays.stream(measures).average().orElse(Double.NaN));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
