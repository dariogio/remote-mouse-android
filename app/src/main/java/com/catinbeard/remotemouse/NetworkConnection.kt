package com.catinbeard.remotemouse

import android.util.Log
import java.net.InetSocketAddress
import java.net.Socket
import java.net.ServerSocket
import java.net.DatagramSocket
import java.net.DatagramPacket
import java.nio.charset.StandardCharsets

class NetworkConnection(private val ip: String, private val port: Int, private val protocol: ProtocolType): AutoCloseable {

    var socket: Socket? = null
    private var serverSocket: ServerSocket? = null
    private var datagramSocket: DatagramSocket? = null

    override fun close() {
        closeConnection()
    }

    init {
        when (protocol) {
            ProtocolType.TCP -> {
                socket = Socket()
                socket?.connect(InetSocketAddress(ip, port))
                Log.d("NetworkConnection", "Create tcp connection")
            }
            ProtocolType.UDP -> {
                datagramSocket = DatagramSocket()
                Log.d("NetworkConnection", "Create udp connection")
            }
        }
    }

    fun readFromConnection(): String? {
        return when (protocol) {
            ProtocolType.TCP -> {
                val inputStream = socket?.getInputStream()
                val buffer = ByteArray(1024)
                val bytesRead = inputStream?.read(buffer)
                bytesRead?.let {
                    val message = String(buffer, 0, bytesRead, StandardCharsets.UTF_8)
                    Log.d("NetworkConnection", "TCP-read: $message")
                    message
                }
            }
            ProtocolType.UDP -> {
                val buffer = ByteArray(1024)
                val packet = DatagramPacket(buffer, buffer.size)
                datagramSocket?.receive(packet)
                val message = String(packet.data, 0, packet.length, StandardCharsets.UTF_8)
                Log.d("NetworkConnection", "UDP-read: $message")
                message
            }
        }
    }


    fun writeToConnection(message: String) {
        when (protocol) {
            ProtocolType.TCP -> {
                val outputStream = socket?.getOutputStream()
                outputStream?.write(message.toByteArray(StandardCharsets.UTF_8))
                Log.d("NetworkConnection", "TCP-write: $message")
            }
            ProtocolType.UDP -> {
                val buffer = message.toByteArray(StandardCharsets.UTF_8)
                val packet = DatagramPacket(buffer, buffer.size, InetSocketAddress(ip, port))
                datagramSocket?.send(packet)
                Log.d("NetworkConnection", "UDP-write: $message")
            }
        }
    }

    fun closeConnection() {
        socket?.close()
        serverSocket?.close()
        datagramSocket?.close()
        Log.d("NetworkConnection", "Conncetion closed")
    }

}
