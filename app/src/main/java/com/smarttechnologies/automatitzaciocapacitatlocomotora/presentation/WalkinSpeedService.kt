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

class WalkingSpeedService : Service() {
    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private var stepCounter: Sensor? = null
    private var stepDetector: Sensor? = null
    private var lastStepTime: Long = 0
    private var stepCount: Int = 0
    private var walkingSpeed: Double = 0.0
    private var avgWalkingSpeed: Double = -1.0
    private var tiempoInicio: Long = 0
    private lateinit var sensorEventListener_step_counter: SensorEventListener
    private lateinit var sensorEventListener_accelerometer: SensorEventListener
    private lateinit var sensorEventListener_step_detect: SensorEventListener
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
        sensorEventListener_step_counter = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                // Actualiza el contador de pasos desde el sensor
                if (stepCount == 0) {
                    stepCount = event.values[0].toInt()
                }
                guardarDatos(event.values[0].toString(),"STEPS")
            }

            override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
                //done
            }
        }
        sensorEventListener_step_detect = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                stepCount++
                guardarDatos(stepCount.toString(),"STEP")
            }

            override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
                //done
            }
        }
        sensorEventListener_accelerometer = object : SensorEventListener{
            override fun onSensorChanged(event: SensorEvent) {
                val currentTime = System.currentTimeMillis()
                val elapsedTime = currentTime - lastStepTime
                val x = event.values[0]
                val y = event.values[1]
                val z = event.values[2]


                val datos = "$x;$y;$z;" //posem les dades separades per ;

                println(datos); //printegem les dades
                guardarDatos(datos,"accelerometre");
            }

            override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
                //done
            }

        }
        sensorManager.registerListener(sensorEventListener_accelerometer, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
        sensorManager.registerListener(sensorEventListener_step_counter, stepCounter, SensorManager.SENSOR_DELAY_NORMAL)
        sensorManager.registerListener(sensorEventListener_step_detect, stepDetector, SensorManager.SENSOR_DELAY_NORMAL)
    }

    private fun stopTracking() {
      //  sensorManager.unregisterListener(sensorEventListener_accelerometer)
        //guardamos la velocidad

        println("Stop tracking")
    }
    private fun guardarDatos(datos: String, type:String) {
        val tiempoActual = System.currentTimeMillis()
        val formatoFecha = SimpleDateFormat("yyyy-MM-dd")
        val fechaActual = formatoFecha.format(Date())
        val nombreArchivo = "$fechaActual.txt"
        var tipus = ""
        val directorio = filesDir // Obtén el directorio de archivos de la aplicación
        if(type=="hr"){
            tipus = "HR"
        }else if(type=="accelerometre"){
            tipus = "ACC"
        }else{
            tipus = type
        }
        try {
            val archivo = File(directorio, nombreArchivo)
            val escritor = FileWriter(archivo, true) // Usar "true" para habilitar el modo de anexar
            escritor.append("$tiempoActual;PASSIVE_$tipus;$datos\n")
            escritor.flush()
            escritor.close()
            // Archivo guardado exitosamente
        } catch (e: Exception) {
            e.printStackTrace()
            // Ocurrió un error al guardar el archivo
        }
    }


    fun onSensorChanged(event: SensorEvent) {

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
                guardarDatos(datos,"accelerometre");

            }
            Sensor.TYPE_STEP_COUNTER -> {
                // Actualiza el contador de pasos desde el sensor
                if (stepCount == 0) {
                    stepCount = event.values[0].toInt()
                }
                guardarDatos(event.values[0].toString(),"STEPS")
            }
            Sensor.TYPE_STEP_DETECTOR -> {
                // Incrementa el contador de pasos por cada evento de detección de paso

                stepCount++
                guardarDatos(stepCount.toString(),"STEP")
            }
        }


    }



    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
