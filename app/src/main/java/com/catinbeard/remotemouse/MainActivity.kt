package com.catinbeard.remotemouse

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private companion object {
        private const val IP_PORT_PATTERN = "^([0-9]{1,3}\\.){3}[0-9]{1,3}:[0-9]{1,5}$"
        private const val IP_PATTERN = "^([0-9]{1,3}\\.){3}[0-9]{1,3}$"
        private const val PREF_IP_PORT = "ip_port"
        private const val DEFAULT_PORT = "5123"
    }

    private lateinit var editText: EditText
    private lateinit var buttonConnect: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        editText = findViewById(R.id.ip_and_port)
        buttonConnect = findViewById(R.id.button_connect)

        val sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE)
        val ipPort = sharedPreferences.getString(PREF_IP_PORT, "")
        if (ipPort != null && ipPort.isNotEmpty()) {
            editText.setText(ipPort)
        }

        buttonConnect.setOnClickListener {
            val ipPortText = editText.text.toString()
            if (ipPortText.matches(Regex(IP_PORT_PATTERN))) {
                sharedPreferences.edit().putString(PREF_IP_PORT, ipPortText).apply()
            } else if (ipPortText.matches(Regex(IP_PATTERN))) {
                    val ipPortTextWithPort = "$ipPortText:$DEFAULT_PORT"
                    sharedPreferences.edit().putString(PREF_IP_PORT, ipPortTextWithPort).apply()
            } else {
                Toast.makeText(this, getString(R.string.ip_and_port_validation_error), Toast.LENGTH_SHORT).show()
            }

        }
    }
}
