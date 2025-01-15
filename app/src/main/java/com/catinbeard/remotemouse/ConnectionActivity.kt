package com.catinbeard.remotemouse

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast
import androidx.fragment.app.Fragment

class ConnectionActivity : AppCompatActivity(), TouchpadFragment.FullTouchpadListener {

    private companion object {
        private const val FIRST_CONNECTION_MESSAGE = "HELLO"
        private const val SEND_AUTH_REQUEST = "AUTH_REQUEST"
        private const val AUTHORIZATION_SUCCESS = "AUTHORIZATION_SUCCESS"
        private const val ALLREADY_AUTHORIZATION = "ALLREADY_AUTHORIZATION"
        private const val AUTHORIZATION_WAITING = "AUTHORIZATION_WAITING"
        private const val SETUP_V1_REL = "SETUP:v1rel"
        private const val SETUP_SUCCESS = "SETUP_SUCCESS"

    }

    private var doubleBackToExitPressedOnce = false

    lateinit var connection: NetworkConnection
    lateinit var waitingConnectionFragment: WaitingConnectionFragment
    lateinit var touchpadFragment: Fragment
    lateinit var connectionHandler: Handler

    private lateinit var sharedPreferences: SharedPreferences
    private var accuracy: Float = 20F
    private var speed: Float = 1F

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_connection)

        waitingConnectionFragment = WaitingConnectionFragment()
        touchpadFragment = TouchpadFragment()

        supportFragmentManager.beginTransaction()
            .add(R.id.ConnectionFragmentContainer, waitingConnectionFragment)
            .commit()


        val ipPortSettingName = getString(R.string.settings_ip)
        val sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE)
        val ipPort = sharedPreferences?.getString(ipPortSettingName, "") ?: ":"
        speed = sharedPreferences.getFloat(getString(R.string.settings_speed), 1F)

        val connectionInitThread = Thread {
            try {
                connection = NetworkConnection(
                    ipPort.split(":")[0] ?: "",
                    (ipPort.split(":")[1] ?: "5123").toInt(),
                    ProtocolType.TCP
                )
                var message = connection.readFromConnection();

                if (message == null || !message.startsWith(FIRST_CONNECTION_MESSAGE)) {
                    runOnUiThread {
                        closeConnection(this);
                    }
                    return@Thread
                }

                connection.writeToConnection(SEND_AUTH_REQUEST);
                runOnUiThread {
                    waitingConnectionFragment.setStatusText(getString(R.string.connecting_confirmation_required))
                }


                message = connection.readFromConnection();

                if (message == AUTHORIZATION_WAITING ) {

                    message = connection.readFromConnection();

                }

                if (message == ALLREADY_AUTHORIZATION || message == AUTHORIZATION_SUCCESS) {
                    connection.writeToConnection(SETUP_V1_REL)
                    message = connection.readFromConnection();
                    if(message == SETUP_SUCCESS){
                        runOnUiThread {
                            supportFragmentManager.beginTransaction()
                                .replace(R.id.ConnectionFragmentContainer, touchpadFragment)
                                .commit()
                        }
                        return@Thread
                    }
                }



                runOnUiThread {
                    closeConnection(this);
                }
                return@Thread

            } catch (e: Exception) {
                Log.e("NetworkConnection", e.toString(), e)
                runOnUiThread {
                    closeConnection(this);
                }
                return@Thread
            }
        }

        connectionInitThread.start()

        val handlerThread = HandlerThread("ConnectionThread")
        handlerThread.start()
        connectionHandler = Handler(handlerThread.looper) {
            true
        }


    }

    private fun closeConnection(context: Context) {
        Toast.makeText(context, getString(R.string.connection_failed), Toast.LENGTH_LONG).show()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    override fun onTouchpadMove(deltaX: Float, deltaY: Float) {
        val x = (deltaX*speed).toInt()
        val y = (deltaY*speed).toInt()

        if(x != 0) {
            writeToConnection("rx$x")
        }
        if(y != 0) {
            writeToConnection("ry$y")
        }
    }

    override fun onClick() {
        writeToConnection("ml1")
        connectionHandler.postDelayed({
            writeToConnection("ml0")
        }, 10)
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

    override fun onLeftMouseDown() {
        writeToConnection("ml1")
    }

    override fun onLeftMouseUp() {
        writeToConnection("ml0")
    }

    override fun onRightMouseDown() {
        writeToConnection("mr1")
    }

    override fun onRightMouseUp() {
        writeToConnection("mr0")
    }

    private fun writeToConnection(string: String){
        connectionHandler.post {
            if(!connection.socket?.isConnected!!){
                runOnUiThread{
                    runOnUiThread {
                        closeConnection(this);
                    }
                }
            }
            connection.writeToConnection(string)
        }
    }
}