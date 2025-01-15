package com.catinbeard.remotemouse

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {

    private companion object {
        private const val ACCURACY_MULT = 1;
        private const val ACCURACY_INC = 5;
        private const val SPEED_MULT = 0.2;
        private const val SPEED_INC = 0.05;
    }

    private var doubleBackToExitPressedOnce = false

    private lateinit var buttonSaveSettings: Button
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var accuracySeekbar: SeekBar
    private lateinit var speedSeekbar: SeekBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE)

        buttonSaveSettings  = findViewById(R.id.save_settings_button)

        accuracySeekbar  = findViewById(R.id.accuracy_seekbar)
        speedSeekbar  = findViewById(R.id.speed_seekbar)

        val accuracy = sharedPreferences.getFloat(getString(R.string.settings_accuracy), 20F)
        val speed = sharedPreferences.getFloat(getString(R.string.settings_speed), 1F)

        accuracySeekbar.progress = (accuracy / ACCURACY_MULT - ACCURACY_INC).toInt()
        speedSeekbar.progress = (speed / SPEED_MULT - SPEED_INC).toInt()

        buttonSaveSettings.setOnClickListener {

            val accuracy = accuracySeekbar.progress
            val speed = speedSeekbar.progress

            sharedPreferences.edit().putFloat(getString(R.string.settings_accuracy), (accuracy * ACCURACY_MULT + ACCURACY_INC).toFloat()).apply()
            sharedPreferences.edit().putFloat(getString(R.string.settings_speed), (speed * SPEED_MULT + SPEED_INC).toFloat()).apply()

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            return super.onBackPressed()
        }

        this.doubleBackToExitPressedOnce = true
        Toast.makeText(this, getString(R.string.double_press_to_exit), Toast.LENGTH_SHORT).show()

        Handler(Looper.getMainLooper()).postDelayed({
            doubleBackToExitPressedOnce = false
        }, 2000)
    }
}