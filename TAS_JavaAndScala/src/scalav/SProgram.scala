package scalav

import scalav.models.Reading
import variables.Variables
import scalav.models.StuckAtReading
import scala.reflect.internal.util.Collections

class SProgram(fileName: String, dT : Long, ind: Int) extends Runnable {	
	val readings = fillReadings(fileName)
			var stuckAtReading = new StuckAtReading()
	var result : Double = -1.0d
	var index : Int = ind

	var sensorLives = Array.ofDim[Int](3) // When sensor life = 0, it "dies"
	sensorLives(0) = 1
	sensorLives(1) = 1
	sensorLives(2) = 1

	var ignoringSensor = Array.ofDim[Boolean](3)
	ignoringSensor(0) = false
	ignoringSensor(1) = false
	ignoringSensor(2) = false
	var sensor1IgnoredIterations = 0
	var sensor2IgnoredIterations = 0
	var sensor3IgnoredIterations = 0

	def run(){

		var failure = Array.ofDim[Boolean](3)
		failure(0) = false
		failure(1) = false
		failure(2) = false

		// Avarias
		// Verificar se falha de leitura: "--" (-1.0)
		checkReadingFailure(failure)
		// Verificar se há valores fora de gama
		checkOutOfRange(failure)
		// Verificar se ultimos 4 estao colados
		checkStuckAt(failure)
		// Verificar se valor de um sensor diverge +- em 20% em relação aos outros da leitura
		checkIf20Dif(failure)
		// Verificar se valores estão a variar muito rapidamente
		checkIfFastChange(failure)
		
		// Actualiza os Sensores
		updateSensors(failure)
		
		var sensorTAS = Array.ofDim[Double](3) // When sensor life = 0, it "dies"
		sensorTAS(0) = calculateTAS(0) // Calcular o TAS do sensor 1
		sensorTAS(1) = calculateTAS(1) // Calcular o TAS do sensor 2
		sensorTAS(2) = calculateTAS(2) // Calcular o TAS do sensor 3
		
		
		result = averageTAS(sensorTAS)
		
		println("\nAverage TAS: " + result)

		index += 1
	}

		
	def averageTAS(sensorTAS : Array[Double]): Double ={
		var averageTemp : Double = 0.0d
		var numSensors : Double = 0.0d
		
		for(i <- 0 until 3){
			if(sensorTAS(i) != -1.0d){
				averageTemp += sensorTAS(i)
				numSensors += 1.0d
			}
		}

		if(numSensors > 0.0d){
			averageTemp / numSensors
		}
		else{
		  -1.0d
		}
	}
	
	def calculateTAS(sensorNum : Int): Double ={
		var m : Double = getMachNumber(sensorNum);
		var t : Double = -1.0d
		
		println("MACH: " + m)
		
		if(m != -1.0d){
		    t = getOAT(sensorNum, m)
		}
		println("OAT: " + t)
		
		// TAS
		if(m != -1.0d && t != -1.0d)
		  Variables.SOUND_VEL_AT_SEA_LEVEL * m * Math.sqrt(t / Variables.AIR_TEMP_AT_SEA_LEVEL)
		else 
		  -1.0d
	}
	
	def getOAT(sensorNum : Int, m : Double): Double ={
		if(readings(index).pt(sensorNum) != -1.0d)
			readings(index).pt(sensorNum)/(1.0d + 0.2d * Math.pow(m, 2))
		else
			-1.0d
	}
	
	def getMachNumber(sensorNum : Int): Double ={
		if(readings(index).pd(sensorNum) != -1.0d && readings(index).pe(sensorNum) != -1.0d)
			Math.sqrt(5.0d * (Math.pow(((readings(index).pd(sensorNum) / readings(index).pe(sensorNum)) + 1.0d), (2.0d/7.0d)) - 1.0d))
		else
			-1.0d
	}

	def fillReadings(fileName : String): Array[Reading] = {
		var lines = io.Source.fromFile(fileName).getLines.toList
		val dimen : Int = lines.size

		var reading = Array.ofDim[Reading](dimen)

		for(i <- 0 until dimen){
			var line : String = lines(i).replaceAll("--", "-1.0")

			val split = line.split("#")
			val sensors = split(1).split(":")
			val sensor1 = sensors(0).split(" ")
			val sensor2 = sensors(1).split(" ")
			val sensor3 = sensors(2).split(" ")

			reading(i) = new Reading(split(0).toInt, sensor1(1).toDouble, sensor1(2).toDouble, sensor1(3).toDouble, 
					sensor2(1).toDouble, sensor2(2).toDouble, sensor2(3).toDouble, 
					sensor3(1).toDouble, sensor3(2).toDouble, sensor3(3).toDouble)

		}

		reading
	}

	// verificar se há valores fora de gama
	private def checkOutOfRange(failure: Array[Boolean]): Unit = {
			// Sensor 1
			if(!(readings(index).pe(0) >= Variables.PRESSURE_MIN && readings(index).pe(0) <= Variables.PRESSURE_MAX) 
					|| !(readings(index).pd(0) >= Variables.PRESSURE_MIN && readings(index).pd(0) <= Variables.PRESSURE_MAX)  
					|| !(readings(index).pt(0) >= Variables.TEMPERATURE_MIN && readings(index).pt(0) <= Variables.TEMPERATURE_MAX)){
				failure(0) = true
			}
			// Sensor 2
			if(!(readings(index).pe(1) >= Variables.PRESSURE_MIN && readings(index).pe(1) <= Variables.PRESSURE_MAX) 
					|| !(readings(index).pd(1) >= Variables.PRESSURE_MIN && readings(index).pd(1) <= Variables.PRESSURE_MAX)  
					|| !(readings(index).pt(1) >= Variables.TEMPERATURE_MIN && readings(index).pt(1) <= Variables.TEMPERATURE_MAX)){
				failure(1) = true
			}
			// Sensor 3
			if(!(readings(index).pe(2) >= Variables.PRESSURE_MIN && readings(index).pe(2) <= Variables.PRESSURE_MAX) 
					|| !(readings(index).pd(2) >= Variables.PRESSURE_MIN && readings(index).pd(2) <= Variables.PRESSURE_MAX)  
					|| !(readings(index).pt(2) >= Variables.TEMPERATURE_MIN && readings(index).pt(2) <= Variables.TEMPERATURE_MAX)){
				failure(2) = true
			}
	}

	private def checkReadingFailure(failure: Array[Boolean]): Unit = {

			if(readings(index).pe(0) == -1.0 || readings(index).pd(0) == -1.0  || readings(index).pt(0) == -1.0){
				failure(0) = true
			}
			if(readings(index).pe(1) == -1.0 || readings(index).pd(1) == -1.0  || readings(index).pt(1) == -1.0){
				failure(1) = true
			}
			if(readings(index).pe(2) == -1.0 || readings(index).pd(2) == -1.0  || readings(index).pt(2) == -1.0){
				failure(2) = true
			}
	}

	private def checkStuckAt(failure: Array[Boolean]): Unit = {
			if(index > 0){
				// Verificar se pressão estática está stuck-at
				if(readings(index-1).pe(0) == readings(index).pe(0)){
					stuckAtReading.pe(0) += 1

					if(stuckAtReading.pe(0) == 4){
						failure(0) = true
						stuckAtReading.pe(0) = 0
					}
				}
				if(readings(index-1).pe(1) == readings(index).pe(1)){
					stuckAtReading.pe(1) += 1

					if(stuckAtReading.pe(1) == 4){
						failure(1) = true
								stuckAtReading.pe(1) = 0
					}
				}
				if(readings(index-1).pe(2) == readings(index).pe(2)){
					stuckAtReading.pe(2) += 1

					if(stuckAtReading.pe(2) == 4){
						failure(2) = true
								stuckAtReading.pe(2) = 0
					}
				}
				// Verificar se pressão dinamica está stuck-at
				if(readings(index-1).pd(0) == readings(index).pd(0)){
					stuckAtReading.pd(0) += 1

					if(stuckAtReading.pd(0) == 4){
						failure(0) = true
								stuckAtReading.pd(0) = 0
					}
				}
				if(readings(index-1).pd(1) == readings(index).pd(1)){
					stuckAtReading.pd(1) += 1

					if(stuckAtReading.pd(1) == 4){
						failure(1) = true
								stuckAtReading.pd(1) = 0
					}
				}
				if(readings(index-1).pd(2) == readings(index).pd(2)){
					stuckAtReading.pd(2) += 1

					if(stuckAtReading.pd(2) == 4){
						failure(2) = true
								stuckAtReading.pd(2) = 0
					}
				}
				// Verificar se temperatura está stuck-at
				if(readings(index-1).pt(0) == readings(index).pt(0)){
					stuckAtReading.pt(0) += 1

					if(stuckAtReading.pt(0) == 4){
						failure(0) = true
								stuckAtReading.pt(0) = 0
					}
				}
				if(readings(index-1).pt(1) == readings(index).pt(1)){
					stuckAtReading.pt(1) += 1

					if(stuckAtReading.pt(1) == 4){
						failure(1) = true
								stuckAtReading.pt(1) = 0
					}
				}
				if(readings(index-1).pt(2) == readings(index).pt(2)){
					stuckAtReading.pt(2) += 1

					if(stuckAtReading.pt(2) == 4){
						failure(2) = true
								stuckAtReading.pt(2) = 0
					}
				}
			}
	}

	private def checkIf20Dif(failure: Array[Boolean]): Unit = {
			val divPE1 : Double = 0.2*readings(index).pe(0)
			val divPD1 : Double = 0.2*readings(index).pd(0)
			val divPT1 : Double = 0.2*readings(index).pt(0)
			val divPE2 : Double = 0.2*readings(index).pe(1)
			val divPD2 : Double = 0.2*readings(index).pd(1)
			val divPT2 : Double = 0.2*readings(index).pt(1)
			val divPE3 : Double = 0.2*readings(index).pe(2)
			val divPD3 : Double = 0.2*readings(index).pd(2)
			val divPT3 : Double = 0.2*readings(index).pt(2)

			// Pressão Estática Sensor 1
			if(readings(index).pe(0) != -1.0){
			  if(readings(index).pe(1) != -1.0 && Math.abs(readings(index).pe(0) - (readings(index).pe(1))) > Math.abs(divPE1)){
			    failure(0) = true
			  }
	  		  if(readings(index).pe(2) != -1.0 && Math.abs(readings(index).pe(0) - (readings(index).pe(2))) > Math.abs(divPE1)){
			    failure(0) = true
			  }
			}
			// Pressão Dinamica Sensor 1
			if(readings(index).pd(0) != -1.0){
			  if(readings(index).pd(1) != -1.0 && Math.abs(readings(index).pd(0) - (readings(index).pd(1))) > Math.abs(divPD1)){
			    failure(0) = true
			  }
	  		  if(readings(index).pd(2) != -1.0 && Math.abs(readings(index).pd(0) - (readings(index).pd(2))) > Math.abs(divPD1)){
			    failure(0) = true
			  }
			}
			// Pressão Temperatura Sensor 1
			if(readings(index).pt(0) != -1.0){
			  if(readings(index).pt(1) != -1.0 && Math.abs(readings(index).pt(0) - (readings(index).pt(1))) > Math.abs(divPT1)){
			    failure(0) = true
			  }
	  		  if(readings(index).pt(2) != -1.0 && Math.abs(readings(index).pt(0) - (readings(index).pt(2))) > Math.abs(divPT1)){
			    failure(0) = true
			  }
			}
			
			// Pressão Estática Sensor 2
			if(readings(index).pe(1) != -1.0){
			  if(readings(index).pe(0) != -1.0 && Math.abs(readings(index).pe(1) - (readings(index).pe(0))) > Math.abs(divPE2)){
			    failure(1) = true
			  }
	  		  if(readings(index).pe(2) != -1.0 && Math.abs(readings(index).pe(1) - (readings(index).pe(2))) > Math.abs(divPE2)){
			    failure(1) = true
			  }
			}
			// Pressão Dinamica Sensor 2
			if(readings(index).pd(1) != -1.0){
			  if(readings(index).pd(0) != -1.0 && Math.abs(readings(index).pd(1) - (readings(index).pd(0))) > Math.abs(divPD2)){
			    failure(1) = true
			  }
	  		  if(readings(index).pd(2) != -1.0 && Math.abs(readings(index).pd(1) - (readings(index).pd(2))) > Math.abs(divPD2)){
			    failure(1) = true
			  }
			}
			// Pressão Temperatura Sensor 2
			if(readings(index).pt(1) != -1.0){
			  if(readings(index).pt(0) != -1.0 && Math.abs(readings(index).pt(1) - (readings(index).pt(0))) > Math.abs(divPT2)){
			    failure(1) = true
			  }
	  		  if(readings(index).pt(2) != -1.0 && Math.abs(readings(index).pt(1) - (readings(index).pt(2))) > Math.abs(divPT2)){
			    failure(1) = true
			  }
			}
	  		  
			// Pressão Estática Sensor 3
			if(readings(index).pe(2) != -1.0){
			  if(readings(index).pe(0) != -1.0 && Math.abs(readings(index).pe(2) - (readings(index).pe(0))) > Math.abs(divPE3)){
			    failure(2) = true
			  }
	  		  if(readings(index).pe(1) != -1.0 && Math.abs(readings(index).pe(2) - (readings(index).pe(1))) > Math.abs(divPE3)){
			    failure(2) = true
			  }
			}
			// Pressão Dinamica Sensor 3
			if(readings(index).pd(2) != -1.0){
			  if(readings(index).pd(0) != -1.0 && Math.abs(readings(index).pd(2) - (readings(index).pd(0))) > Math.abs(divPD3)){
			    failure(2) = true
			  }
	  		  if(readings(index).pd(1) != -1.0 && Math.abs(readings(index).pd(2) - (readings(index).pd(1))) > Math.abs(divPD3)){
			    failure(2) = true
			  }
			}
			// Pressão Temperatura Sensor 3
			if(readings(index).pt(2) != -1.0){
			  if(readings(index).pt(0) != -1.0 && Math.abs(readings(index).pt(2) - (readings(index).pt(0))) > Math.abs(divPT3)){
			    failure(2) = true
			  }
	  		  if(readings(index).pt(1) != -1.0 && Math.abs(readings(index).pt(2) - (readings(index).pt(1))) > Math.abs(divPT3)){
			    failure(2) = true
			  }
			}
			    
//			if(Math.abs(readings(index).pe(0) - ((readings(index).pe(1) + readings(index).pe(2))/2.0)) > Math.abs(divPE1)
//						|| Math.abs(readings(index).pd(0) - ((readings(index).pd(1) + readings(index).pd(2))/2.0)) > Math.abs(divPD1)
//						|| Math.abs(readings(index).pt(0) - ((readings(index).pt(1) + readings(index).pt(2))/2.0)) > Math.abs(divPT1)){
//					failure(0) = true
//				}
//			
//			if(Math.abs(readings(index).pe(1) - ((readings(index).pe(0) + readings(index).pe(2))/2.0)) > Math.abs(divPE2)
//					|| Math.abs(readings(index).pd(1) - ((readings(index).pd(0) + readings(index).pd(2))/2.0)) > Math.abs(divPD2)
//					|| Math.abs(readings(index).pt(1) - ((readings(index).pt(0) + readings(index).pt(2))/2.0)) > Math.abs(divPT2)){
//				failure(1) = true
//			}
//			if(Math.abs(readings(index).pe(2) - ((readings(index).pe(0) + readings(index).pe(1))/2.0)) > Math.abs(divPE3)
//					|| Math.abs(readings(index).pd(2) - ((readings(index).pd(0) + readings(index).pd(1))/2.0)) > Math.abs(divPD3)
//					|| Math.abs(readings(index).pt(2) - ((readings(index).pt(0) + readings(index).pt(1))/2.0)) > Math.abs(divPT3)){
//				failure(2) = true
//			}
	}

	private def checkIfFastChange(failure: Array[Boolean]): Unit = {
			if(index > 0){
				if((readings(index).pe(0) != -1.0 && readings(index-1).pe(0) != -1.0 && (Math.abs(readings(index).pe(0) - readings(index-1).pe(0)) > Variables.PRESSURE_FAST_CHANGE))
						|| (readings(index).pd(0) != -1.0 && readings(index-1).pd(0) != -1.0 && (Math.abs(readings(index).pd(0) - readings(index-1).pd(0)) > Variables.PRESSURE_FAST_CHANGE))
						|| (readings(index).pt(0) != -1.0 && readings(index-1).pt(0) != -1.0 && (Math.abs(readings(index).pt(0) - readings(index-1).pt(0)) > Variables.TEMPERATURE_FAST_CHANGE))){
					failure(0) = true
				}
				if((readings(index).pe(1) != -1.0 && readings(index-1).pe(1) != -1.0 && (Math.abs(readings(index).pe(1) - readings(index-1).pe(1)) > Variables.PRESSURE_FAST_CHANGE))
						|| (readings(index).pd(1) != -1.0 && readings(index-1).pd(1) != -1.0 && (Math.abs(readings(index).pd(1) - readings(index-1).pd(1)) > Variables.PRESSURE_FAST_CHANGE))
						|| (readings(index).pt(1) != -1.0 && readings(index-1).pt(1) != -1.0 && (Math.abs(readings(index).pt(1) - readings(index-1).pt(1)) > Variables.TEMPERATURE_FAST_CHANGE))){
					failure(1) = true
				}
				if((readings(index).pe(2) != -1.0 && readings(index-1).pe(2) != -1.0 && (Math.abs(readings(index).pe(2) - readings(index-1).pe(2)) > Variables.PRESSURE_FAST_CHANGE))
						|| (readings(index).pd(2) != -1.0 && readings(index-1).pd(2) != -1.0 && (Math.abs(readings(index).pd(2) - readings(index-1).pd(2)) > Variables.PRESSURE_FAST_CHANGE))
						|| (readings(index).pt(2) != -1.0 && readings(index-1).pt(2) != -1.0 && (Math.abs(readings(index).pt(2) - readings(index-1).pt(2)) > Variables.TEMPERATURE_FAST_CHANGE))){
					failure(2) = true
				}
			}
	}
  
	/**
	 * Method that checks if a failure has been detected and updates Sensors (Enable or Disable)
	 */
  private def updateSensors(failure: Array[Boolean]): Unit = {
	  if(sensorLives(0) > 0){
	    
	    if(ignoringSensor(0) && sensor1IgnoredIterations < 5){ // Incrementa se estiver a ignorar sensor
	  		sensor1IgnoredIterations += 1
	  		println("Sensor 1 ignored: " + sensor1IgnoredIterations)
	  		
	  		if(sensor1IgnoredIterations == 5){ // Ignorou 5 vezes, activa sensor
		  		ignoringSensor(0) = false
		  		println("Sensor 1 is enabled again!")
		  	}
	  	}
	    
	  	if(failure(0) && !ignoringSensor(0) && sensor1IgnoredIterations == 0){ // Primeira falha do sensor
	  		println("Failure in Sensor 1!")
	  		ignoringSensor(0) = true
	  	}
	  	else if(failure(0) && !ignoringSensor(0) && sensor1IgnoredIterations == 5){ // Caso ocorra segunda Falha do sensor
	  		println("Second failure in Sensor 1. Sensor 1 DISABLED!")
	  		sensorLives(0) -= 1 // Ignorar permanentemente o sensor
	  	}
	  }
	  
	  if(sensorLives(1) > 0){
	    
	    if(ignoringSensor(1) && sensor2IgnoredIterations < 5){ // Incrementa se estiver a ignorar sensor
	  		sensor2IgnoredIterations += 1
	  		println("Sensor 2 ignored: " + sensor2IgnoredIterations)
	  		
	  		if(sensor2IgnoredIterations == 5){ // Ignorou 5 vezes, activa sensor
		  		ignoringSensor(1) = false
		  		println("Sensor 2 is enabled again!")
		  	}
	  	}
	    	    
	  	if(failure(1) && !ignoringSensor(1) && sensor2IgnoredIterations == 0){ // Primeira falha do sensor
	  		println("Failure in Sensor 2!")
	  		ignoringSensor(1) = true
	  	}
	  	else if(failure(1) && !ignoringSensor(1) && sensor2IgnoredIterations == 5){ // Caso ocorra segunda Falha do sensor
	  		  println("Second failure in Sensor 2. Sensor 2 DISABLED!")
	  		  sensorLives(1) -= 1 // Ignorar permanentemente o sensor
	  	}
	  }
	  if(sensorLives(2) > 0){
	    if(ignoringSensor(2) && sensor3IgnoredIterations < 5){ // Incrementa se estiver a ignorar sensor
	  		sensor3IgnoredIterations += 1
	  		println("Sensor 3 ignored: " + sensor3IgnoredIterations)
	  		
	  		if(sensor3IgnoredIterations == 5){ // Ignorou 5 vezes, activa sensor sensor
		  		ignoringSensor(2) = false
		  		println("Sensor 3 is enabled again!")
		  	}
	  	}
	    	    
	  	if(failure(2) && !ignoringSensor(2) && sensor3IgnoredIterations == 0){ // Primeira falha do sensor
	  		println("Failure in Sensor 3!")
	  		ignoringSensor(2) = true
	  	}
	  	else if(failure(2) && !ignoringSensor(2) && sensor3IgnoredIterations == 5){ // Caso ocorra segunda Falha do sensor
	  		 println("Second failure in Sensor 3. Sensor 3 DISABLED!")
	  		 sensorLives(2) -= 1 // Ignorar permanentemente o sensor
	  	}
	  }
	}

}