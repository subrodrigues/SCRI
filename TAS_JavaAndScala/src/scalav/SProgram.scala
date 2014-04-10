package scalav

class SProgram(fileName: String, dT : Long, index: Int) extends Runnable {	
	val lines = io.Source.fromFile(fileName).getLines.toList
	val dt = dT
	
	def run(){
	  println(lines(index) + " from Scala")
//		println("Sleep 800ms\n");
//		Thread.sleep(500);
	}
	
}