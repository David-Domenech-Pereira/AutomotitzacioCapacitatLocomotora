package com.smarttechnologies.automatitzaciocapacitatlocomotora.presentation

import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.Vibrator
import android.widget.Button
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.compose.ui.res.stringResource
import com.smarttechnologies.automatitzaciocapacitatlocomotora.R
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.Date

class Cadira : ComponentActivity() {
    //variables necessàries per l'acceleròmetre
    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private lateinit var textView: TextView
    private lateinit var boton: Button
    private var tiempoInicio: Long = 0
    private lateinit var sensorEventListener: SensorEventListener
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tiempoInicio = System.currentTimeMillis()// contem el temps desde que s'ha iniciat
        setContentView(R.layout.cadira);
        //inicialitzem
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        textView = findViewById(R.id.textView)
        textView.text = "Aixeca't de la cadira"
        guardarDatos("Start") //indiquem que comença a mesurar
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
                guardarDatos(datos);
            }
        }

        sensorManager.registerListener(sensorEventListener, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
        boton = findViewById(R.id.end)
        boton.setOnClickListener {
            //carguem el end
            end()
        }
        //TODO fer que el botó es vegi quan creiem que ha acabat

        vibrarReloj() //cridem l'atenció de l'usuari
    }
    private fun end(){
        //apaguem el sensor
        sensorManager.unregisterListener(sensorEventListener)
        vibrarReloj() //fem que vibri
        //guardem les dades
        guardarDatos("End")
        //posem un missatge
        textView.text = "Finalitzat"
        boton.text = "Tornar a l'inici"
        boton.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
    private fun guardarDatos(datos: String) {
        val tiempoActual = System.currentTimeMillis() - tiempoInicio
        val formatoFecha = SimpleDateFormat("yyyy-MM-dd")
        val fechaActual = formatoFecha.format(Date())
        val nombreArchivo = "accelerometre_$fechaActual.txt"
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