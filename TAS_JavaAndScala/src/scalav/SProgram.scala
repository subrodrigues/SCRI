package scalav

class SProgram(fileName: String, dT : Int) {	
	val lines = io.Source.fromFile(fileName).getLines.toList
	val dt = dT
	
	def execute(index: Int) {
		println(lines(index) + " from Scala")
//		println("Sleep 800ms\n");
//		Thread.sleep(500);
	}
}