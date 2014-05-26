package javav;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;

import jp.vdmtools.VDM.CGException;

public class JProgram implements Runnable {
	public static double ERROR = -1;
	public static double EOF = -2;
	List<String> lines = null;
	private long dt = 0;
	private int index;
	private double result;
	private ATInput input;
	private boolean debug = false;

	public JProgram(String fileName, long dt, int index) {
		this.dt = dt;
		this.index = index;
		try {
			this.input = new ATInput(debug);
		} catch (CGException e1) {
			e1.printStackTrace();
		}

		// Get all the lines
		try {
			lines = Files.readAllLines(Paths.get(fileName),
					Charset.forName("UTF-8"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private Line readLine() {
		String line = lines.get(index);
		String sensors = line.split("#")[1];
		String sensor1 = sensors.split(":")[0];
		String sensor2 = sensors.split(":")[1];
		String sensor3 = sensors.split(":")[2];

		return new Line(toSensor(sensor1), toSensor(sensor2), toSensor(sensor3),index);
	}

	private Sensor toSensor(String sensor) {
		String[] vals = sensor.split(" ");
		double pe = readValue(vals[1]);
		double pd = readValue(vals[2]);
		double t = readValue(vals[3]);

		return new Sensor(pe, pd, t);
	}

	private double readValue(String vals) {
		double value;
		try{
			value= Double.parseDouble(vals);
		}catch(NumberFormatException e){
			value = ERROR;
		}
		
		return value;
	}

	public double getResult() {
		return result;
	}

	@Override
	public void run() {
		try {
			Line line = readLine();
			result = input.read(line.s1.pe, line.s1.pd, line.s1.t,
					line.s2.pe, line.s2.pd, line.s2.t, line.s3.pe, line.s3.pd,
					line.s3.t).doubleValue();

			index++;
		} catch (CGException e) {
			e.printStackTrace();
		} catch(IndexOutOfBoundsException e){
			result = EOF;
		}
	}

	private class Sensor {
		private double pe, pd, t;

		public Sensor(double pe, double pd, double t) {
			this.pe = pe;
			this.pd = pd;
			this.t = t;
		}
		
		@Override
		public String toString(){
			String ret="";
			
			if( pe != ERROR )
				ret+=""+pe+" ";
			else
				ret+="-- ";
			
			if( pd != ERROR )
				ret+=""+pd+" ";
			else
				ret+="-- ";
			
			if( t != ERROR )
				ret+=""+t;
			else
				ret+="--";
			
			return ret;
		}
	}

	private class Line {
		private Sensor s1, s2, s3;
		private int index;

		public Line(Sensor s1, Sensor s2, Sensor s3, int index) {
			this.s1 = s1;
			this.s2 = s2;
			this.s3 = s3;
			this.index = index;
		}
		
		@Override
		public String toString(){
			String ret = ""+(index+1) + "# ";
			ret+=s1.toString() + " : ";
			ret+=s2.toString() + " : ";
			ret+=s3.toString();
			return ret;
		}
	}

	public void setDebug(boolean d) {
		this.debug = d;
	}

}