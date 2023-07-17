package com.smarttechnologies.automatitzaciocapacitatlocomotora.presentation

import android.app.Service
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.IBinder
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.Date

class WalkingSpeedService : Service(), SensorEventListener {
    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private var stepCounter: Sensor? = null
    private var stepDetector: Sensor? = null
    private var lastStepTime: Long = 0
    private var stepCount: Int = 0
    private var walkingSpeed: Double = 0.0
    private var avgWalkingSpeed: Double = -1.0
    private var tiempoInicio: Long = 0

    override fun onCreate() {
        super.onCreate()


        tiempoInicio = System.currentTimeMillis()// contem el temps desde que s'ha iniciat
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        stepCounter = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        stepDetector = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        stepCounter = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        stepDetector = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)

        if (accelerometer == null || stepCounter == null || stepDetector == null) {
            println("No hi ha sensors")
        } else {
            // Inicia el seguimiento

            startTracking()
        }

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startTracking()
        return START_STICKY
    }

    override fun onDestroy() {
        stopTracking()
        super.onDestroy()
    }

    private fun startTracking() {
        lastStepTime = System.currentTimeMillis()
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
        sensorManager.registerListener(this, stepCounter, SensorManager.SENSOR_DELAY_NORMAL)
        sensorManager.registerListener(this, stepDetector, SensorManager.SENSOR_DELAY_NORMAL)
    }

    private fun stopTracking() {
        sensorManager.unregisterListener(this)
        //guardamos la velocidad
        guardarDatos(avgWalkingSpeed.toString(),"walkingSpeed_")
        println("Stop tracking")
    }
    private fun guardarDatos(datos: String, type:String) {
        val tiempoActual = System.currentTimeMillis() - tiempoInicio
        val formatoFecha = SimpleDateFormat("yyyy-MM-dd")
        val fechaActual = formatoFecha.format(Date())
        val nombreArchivo = "$type$fechaActual.txt"
        val directorio = filesDir // Obtén el directorio de archivos de la aplicación

        try {
            val archivo = File(directorio, nombreArchivo)
            val escritor = FileWriter(archivo, true) // Usar "true" para habilitar el modo de anexar
            escritor.append("$tiempoActual;$datos\n")
            escritor.flush()
            escritor.close()
            // Archivo guardado exitosamente
        } catch (e: Exception) {
            e.printStackTrace()
            // Ocurrió un error al guardar el archivo
        }
    }
    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
        // No se utiliza en este caso
    }

    override fun onSensorChanged(event: SensorEvent) {

        // Código para el seguimiento de los pasos y el cálculo de la velocidad de caminata
        when (event.sensor.type) {
            Sensor.TYPE_ACCELEROMETER -> {
                val currentTime = System.currentTimeMillis()
                val elapsedTime = currentTime - lastStepTime
                val x = event.values[0]
                val y = event.values[1]
                val z = event.values[2]


                val datos = "$x;$y;$z;" //posem les dades separades per ;

                println(datos); //printegem les dades
                guardarDatos(datos,"accelerometre_passiu");

            }
            Sensor.TYPE_STEP_COUNTER -> {
                // Actualiza el contador de pasos desde el sensor
                if (stepCount == 0) {
                    stepCount = event.values[0].toInt()
                }
            }
            Sensor.TYPE_STEP_DETECTOR -> {
                // Incrementa el contador de pasos por cada evento de detección de paso
                val currentTime = System.currentTimeMillis()
                stepCount++
                lastStepTime = currentTime
            }
        }

        // Calcula la velocidad de caminata (en metros por segundo)
        walkingSpeed = calculateWalkingSpeed(stepCount)
        if(walkingSpeed>0.0) {
            if (avgWalkingSpeed <= 0) {
                //la asignamos
                avgWalkingSpeed = walkingSpeed
            } else {
                avgWalkingSpeed = (avgWalkingSpeed + walkingSpeed) / 2 //hacemos el promedio
            }
            println(avgWalkingSpeed)
        }
    }

    private fun calculateWalkingSpeed(stepCount: Int): Double {
        // Cálculo de la velocidad de caminata
        val distanceInMeters = stepCount * 1.5 //TODO ajustar
        val elapsedTimeInSeconds = (System.currentTimeMillis() - lastStepTime) / 1000.0
        if(elapsedTimeInSeconds==0.0){
            return -1.0
        }
        return distanceInMeters / elapsedTimeInSeconds
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
