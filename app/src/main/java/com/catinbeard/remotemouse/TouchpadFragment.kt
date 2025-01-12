package com.catinbeard.remotemouse

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.catinbeard.remotemouse.TouchpadView.OnTouchpadMoveListener

class TouchpadFragment : Fragment() {


    private lateinit var touchpadView: TouchpadView
    private lateinit var touchpadMoveListener: OnTouchpadMoveListener
    private lateinit var left_btn: Button
    private lateinit var right_btn: Button

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnTouchpadMoveListener) {
            touchpadMoveListener = context
        } else {
            throw ClassCastException("$context must implement touchpadMoveListener")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_touchpad, container, false)

        touchpadView = view.findViewById(R.id.touchpad_view)
        left_btn = view.findViewById(R.id.left_btn)
        right_btn = view.findViewById(R.id.right_btn)

        touchpadView.setOnTouchpadMoveListener(object : OnTouchpadMoveListener {

            override fun onTouchpadMove(deltaX: Float, deltaY: Float) {
                touchpadMoveListener.onTouchpadMove(deltaX, deltaY)
            }

            override fun onClick() {
                touchpadMoveListener.onClick()
            }
        })

        return view
    }

}