package javav;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;

public class JProgram implements Runnable {
	List<String> lines = null;
	private long dt = 0;
	private int index;
	private double result;

	public JProgram(String fileName, long dt, int index) {
		this.dt = dt;
		this.index = index;

		// Get all the lines
		try {
			lines = Files.readAllLines(Paths.get(fileName),
					Charset.forName("UTF-8"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public double getResult(){
		return result;
	}
	
	@Override
	public void run() {
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(lines.get(index) + " from Java");

		// try {
		// System.out.println("Sleep 800ms\n");
		// Thread.sleep(500);
		// } catch (InterruptedException e) {
		// e.printStackTrace();
		// }
	}

}