package com.smarttechnologies.automatitzaciocapacitatlocomotora.presentation

import android.content.Context
import android.os.Vibrator
import androidx.activity.ComponentActivity
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.Date

open class appBackground : ComponentActivity() {
    protected var tiempoInicio: Long = 0
    protected fun guardarDatos(datos: String, type:String) {
        val tiempoActual = System.currentTimeMillis()
        val formatoFecha = SimpleDateFormat("yyyy-MM-dd")
        val fechaActual = formatoFecha.format(Date())
        val nombreArchivo = "$fechaActual.txt"
        val directorio = filesDir // Obtén el directorio de archivos de la aplicación
        var tipus = ""
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
            escritor.append("$tiempoActual;$tipus;$datos\n")
            escritor.flush()
            escritor.close()
            // Archivo guardado exitosamente
        } catch (e: Exception) {
            e.printStackTrace()
            // Ocurrió un error al guardar el archivo
        }
    }
    protected fun vibrarReloj() {
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