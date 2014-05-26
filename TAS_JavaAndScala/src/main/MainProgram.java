package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;

import javav.JProgram;
import scalav.SProgram;

public class MainProgram {
	JProgram jProgram = null;
	SProgram sProgram = null;
	private int index = 0;
	private boolean debug = false;
	private double finalTAS = 0;
	
	private static final int MIN_DT = 200;
	
	private long dt;
	
	public MainProgram(String filename, long dt, boolean debug){
		this.debug = debug;
		
		jProgram = new JProgram(filename, dt, index);
		jProgram.setDebug(debug);
		
		sProgram = new SProgram(filename, dt, index);
		sProgram.setDebug(debug);
		
		this.dt = dt;
	}
	
	public void printMessage() {
		System.out.println("TAS - True AirSpeed\n");
	}
	
	public void setDebug(boolean d){
		this.debug = d;
	}

	public static void main(final String... arguments) {
		if(arguments.length < 1){
			System.err.println("You should specify the file to load <filename.txt> !");
			return;
		}
		try{
			File f = new File(arguments[0]);
			if(f.exists() && !f.isDirectory()) { }
		}catch(NumberFormatException e){
			System.err.println("Error file not found. Please use a valid path!");
			return;
		}
		
		
		long dt = -1;
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		while(dt == -1){
			
			System.out.println("Please choose time in milliseconds <dt> :");
			try {
				dt = Long.parseLong(br.readLine(), 10);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		
		if(dt < MIN_DT){
			System.err.println("DT must b at least " + MIN_DT + "ms");
			return;
		}

		MainProgram main = null;
		if(arguments.length >= 2 && arguments[1].equals("-d")){
			main = new MainProgram(arguments[0], dt, true);
		}
		else
			main = new MainProgram(arguments[0], dt, false);

		main.printMessage();
		
		while(main.finalTAS != -1.0d){
			main.readLine(main.getIndex());
			main.incIndex();
		}
		

		try {
			System.out.println("\n\nPress Enter to continue.");
			System.in.read();
		} catch (IOException e) {
			// TODO Auto-generated catch block
		}
	}
	
	
	public void readLine(int index) {
		
	    synchronized(this) {
	        Thread t1 = new Thread(jProgram);
	        Thread t2 = new Thread(sProgram);

	        t1.start();
	        t2.start();
	        
	        long startTime = System.currentTimeMillis();
	        try {
				t1.join(dt);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
	        long endTime = System.currentTimeMillis();
	        long elapsedTime = endTime-startTime;
	        
	        //TODO Check if dt-elapsedTime is 0
	        if(dt-elapsedTime <= 0){
	        	t1.stop();
	        }
	        else{
	        	//TODO get result from t1
	        }
	        
	        try {
	        	if( dt-elapsedTime > 0)
	        		t2.join(dt-elapsedTime);
	        	else if(t2.isAlive()){
	        		//ERROR
	        	}
	        	else{
	        		//TODO get result from t2
	        	}
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
	        
	        long finalTime = System.currentTimeMillis();
	        long finalElapsedTime = finalTime-startTime;
	     
			try {
				if(dt-finalElapsedTime > 0){
					if(debug){
						System.out.println("Java Average TAS: " + jProgram.getResult());
						System.out.println("Scala Average TAS: " + sProgram.getResult());
						System.out.println("Sleep " + (dt-finalElapsedTime) + "ms\n");
					}
					
					printFinalTAS(index);
					Thread.sleep(dt-finalElapsedTime);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
	    }
	}

	private void printFinalTAS(Integer index) {
		double javaTAS = jProgram.getResult();
		double scalaTAS = sProgram.getResult();
		finalTAS = (javaTAS+scalaTAS)/2;
		
		if(javaTAS != -1.0d && javaTAS != -1.0d && finalTAS >= 0 && finalTAS <= 1000d){
			System.out.println((Integer)(index+1)+"#: " + (double)((javaTAS+scalaTAS)/2) + " Knots");
			if(debug)
				System.out.println("\n\n");
		}
		else{
			finalTAS = -1.0d;
			System.out.println((Integer)(index+1)+"#: " + "NO SPEED VALUE");
		}
	}
	
	private int getIndex() {
		return index;
	}
	
	private void incIndex() {
		index++;
	}
}
