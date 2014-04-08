package javav;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class JProgram {
	List<String> lines = null;
	
	public JProgram(String fileName){
		// Get all the lines
		try {
			lines = Files.readAllLines(Paths.get(fileName), Charset.forName("UTF-8"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void execute(int index) {
		System.out.println(lines.get(index) + " from Java");
		
//		try {
//			System.out.println("Sleep 800ms\n");
//			Thread.sleep(500);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
	}
}