package com.catinbeard.remotemouse

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private companion object {
        private const val IP_PORT_PATTERN = "^([0-9]{1,3}\\.){3}[0-9]{1,3}:[0-9]{1,5}$"
        private const val IP_PATTERN = "^([0-9]{1,3}\\.){3}[0-9]{1,3}$"
        private const val DEFAULT_PORT = "5123"
    }

    private lateinit var editText: EditText
    private lateinit var buttonConnect: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        editText = findViewById(R.id.ip_and_port)
        buttonConnect = findViewById(R.id.button_connect)

        val ipPortSettingName = getString(R.string.settings_ip)
        val sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE)
        val ipPort = sharedPreferences.getString(ipPortSettingName, "")
        if (ipPort != null && ipPort.isNotEmpty()) {
            editText.setText(ipPort)
        }

        buttonConnect.setOnClickListener {
            val ipPortText = editText.text.toString()
            if (ipPortText.matches(Regex(IP_PORT_PATTERN))) {
                sharedPreferences.edit().putString(ipPortSettingName, ipPortText).apply()
                val intent = Intent(this, ConnectionActivity::class.java)
                startActivity(intent)
            } else if (ipPortText.matches(Regex(IP_PATTERN))) {
                    val ipPortTextWithPort = "$ipPortText:$DEFAULT_PORT"
                    sharedPreferences.edit().putString(ipPortSettingName, ipPortTextWithPort).apply()
                    val intent = Intent(this, ConnectionActivity::class.java)
                    startActivity(intent)
            } else {
                Toast.makeText(this, getString(R.string.ip_and_port_validation_error), Toast.LENGTH_SHORT).show()
            }

        }
    }
}
