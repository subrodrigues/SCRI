package scalav

class SProgram(fileName: String) {	
	val lines = io.Source.fromFile(fileName).getLines.toList

	def execute(index: Int) {
		println(lines(index) + " from Scala")

//		println("Sleep 800ms\n");
//		Thread.sleep(500);
	}
}