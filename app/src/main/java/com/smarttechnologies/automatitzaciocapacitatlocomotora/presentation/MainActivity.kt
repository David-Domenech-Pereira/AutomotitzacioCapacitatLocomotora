/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter and
 * https://github.com/android/wear-os-samples/tree/main/ComposeAdvanced to find the most up to date
 * changes to the libraries and their usages.
 */

package com.smarttechnologies.automatitzaciocapacitatlocomotora.presentation

import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.smarttechnologies.automatitzaciocapacitatlocomotora.R
import com.smarttechnologies.automatitzaciocapacitatlocomotora.presentation.theme.AutomatitzacioCapacitatLocomotoraTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)
        val button: Button = findViewById(R.id.button);
        button.setOnClickListener{
            val intent = Intent(this, Cadira::class.java)
            startActivity(intent)
        }
        val button2: Button = findViewById(R.id.button2);
        button2.setOnClickListener{
            val intent = Intent(this, Balance::class.java)
            startActivity(intent)
        }
        println("AAAAAAAAAAAAAAA")
        startService(Intent(this, WalkingSpeedService::class.java))
    }

}

