package com.catinbeard.remotemouse

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import androidx.fragment.app.Fragment

class WaitingConnectionFragment : Fragment() {


    private lateinit var buttonCancel: Button
    private lateinit var connecting_status_text_view : TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_wait_conncetion, container, false)

        buttonCancel = view.findViewById(R.id.button_cancel)
        connecting_status_text_view = view.findViewById(R.id.connecting_status)

        buttonCancel.setOnClickListener{
            val intent = Intent(activity, MainActivity::class.java)
            startActivity(intent)
        }

        return view
    }

    fun setStatusText(text: String){
        connecting_status_text_view.setText(text)
    }
}