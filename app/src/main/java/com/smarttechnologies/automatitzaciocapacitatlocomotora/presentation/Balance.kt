package com.smarttechnologies.automatitzaciocapacitatlocomotora.presentation

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.Vibrator
import android.widget.Button
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.smarttechnologies.automatitzaciocapacitatlocomotora.R
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.Date

class Balance : ComponentActivity() {
    //variables necessàries per l'acceleròmetre
    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private var heartRateSensor: Sensor? = null
    private lateinit var textView: TextView
    private lateinit var boton: Button
    private var tiempoInicio: Long = 0
    private lateinit var sensorEventListener: SensorEventListener
    private lateinit var sensorEventListener_hr: SensorEventListener
    private var max_hr  = -1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        guardarDatos("Start","accelerometre_equilibri") //indiquem que comença a mesurar
        guardarDatos("Start","hr_equilibri") //comença a mesurar
        tiempoInicio = System.currentTimeMillis()// contem el temps desde que s'ha iniciat
        setContentView(R.layout.cadira);
        //inicialitzem
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        heartRateSensor = sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE)

        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        textView = findViewById(R.id.textView)
        textView.text = "Prova iniciada"

        sensorEventListener = object : SensorEventListener {
            override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
                // No se utiliza en este caso
            }

            override fun onSensorChanged(event: SensorEvent) {
                // Lee los valores del acelerómetro y muestra las indicaciones según tus necesidades
                val x = event.values[0]
                val y = event.values[1]
                val z = event.values[2]
                val datos = "$x;$y;$z;" //posem les dades separades per ;

                println(datos); //printegem les dades
                guardarDatos(datos,"accelerometre_equilibri");
            }
        }

        sensorManager.registerListener(sensorEventListener, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
        sensorEventListener_hr = object : SensorEventListener {
            override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
                // No se utiliza en este caso
            }

            override fun onSensorChanged(event: SensorEvent) {
                if (event.sensor.type == Sensor.TYPE_HEART_RATE) {

                    val heartRate = event.values[0].toInt()

                    println("Hr: $heartRate")
                    guardarDatos(heartRate.toString(),"hr_equilibri") //guardem les dades
                    if(heartRate>max_hr){
                        //guardem el heart rate máxim
                        max_hr = heartRate
                    }
                }
            }
        }
        sensorManager.registerListener(sensorEventListener_hr, heartRateSensor, SensorManager.SENSOR_DELAY_NORMAL)
        boton = findViewById(R.id.end)
        boton.setOnClickListener {
            //carguem el end
            end()
        }
        //TODO fer que el botó es vegi quan creiem que ha acabat
        //Necessitem el permís per accedir als sen
        val permission = Manifest.permission.BODY_SENSORS
        val requestCode = 1

        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            // El permiso no se ha concedido, así que solicitamos permiso
            ActivityCompat.requestPermissions(this, arrayOf(permission), requestCode)
        } else {
            // El permiso ya se ha concedido, puedes continuar con la lógica del sensor de ritmo cardíaco
        }
        vibrarReloj() //cridem l'atenció de l'usuari
    }
    private fun end(){
        //apaguem el sensor
        sensorManager.unregisterListener(sensorEventListener)
        sensorManager.unregisterListener(sensorEventListener_hr)
        vibrarReloj() //fem que vibri
        //guardem les dades
        guardarDatos("End","accelerometre_equilibri")
        guardarDatos("End","hr_equilibri")
        //posem un missatge
        textView.text = "Test finalitzat"
        boton.text = "Tornar a l'inici"
        boton.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
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
    private fun vibrarReloj() {
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        // Verifica si el dispositivo admite la vibración
        if (vibrator.hasVibrator()) {
            // Define un patrón de vibración (por ejemplo, 500 ms vibrando, 200 ms en pausa)
            val pattern = longArrayOf(500, 200)

            // -1 indica repetición de patrón, 0 indica no repetición
            vibrator.vibrate(pattern, -1)
        }
    }
}