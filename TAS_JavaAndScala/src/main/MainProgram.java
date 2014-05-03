package main;

import java.util.Date;

import javav.JProgram;
import scalav.SProgram;

public class MainProgram {
	JProgram jProgram = null;
	SProgram sProgram = null;
	private int index = 0;
	
	private static final int MIN_DT = 200;
	
	private long dt;
	
	public MainProgram(long dt){
		jProgram = new JProgram("src/files/input.txt", dt, index);
		sProgram = new SProgram("src/files/input.txt", dt, index);
		this.dt = dt;
	}
	
	public void printMessage() {
		System.out.println("TAS - True AirSpeed\n");
	}

	public static void main(final String... arguments) {
		
		if(arguments.length < 1){
			System.err.println("You should specify the DT!");
			return;
		}
		
		long dt;
		try{
			dt = Long.parseLong(arguments[0]);
		}catch(NumberFormatException e){
			System.err.println("DT must be a long number!");
			return;
		}
		
		if(dt <MIN_DT){
			System.err.println("DT must b at least " + MIN_DT + "ms");
			return;
		}
		
		MainProgram main = new MainProgram(dt);
		main.printMessage();
		
		for(int i = 0; i < 10; i++){
			main.readLine(i);
			
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
					System.out.println("Sleep " + (dt-finalElapsedTime) + "ms\n");
					Thread.sleep(dt-finalElapsedTime);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
	    }
	}
	
}
