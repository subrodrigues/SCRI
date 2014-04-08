package main;

import javav.JProgram;
import scalav.SProgram;

public class MainProgram {
	JProgram jProgram = null;
	SProgram sProgram = null;
	
	public MainProgram(){
		jProgram = new JProgram("src/files/input.txt");
		sProgram = new SProgram("src/files/input.txt");
	}
	
	public void printMessage() {
		System.out.println("TAS - True AirSpeed\n");
	}

	public static void main(final String... arguments) {
		MainProgram main = new MainProgram();
		main.printMessage();
		
		for(int i = 0; i < 4; i++){
			main.readLine(i);
		}
	}
	
	public void readLine(int index) {
	    synchronized(this) {
	        jProgram.execute(index);
	        sProgram.execute(index);
	        
			System.out.println("Sleep 800ms\n");
			try {
				Thread.sleep(800);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
	    }
	}
}
