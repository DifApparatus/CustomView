package com.e.paintwidget

import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.paintwidget_view.*

class MainActivity : AppCompatActivity(), PaintWidget.OnChangedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button.setOnClickListener({
            paintwidget.visibility = View.VISIBLE
        })
        button2.setOnClickListener({
            paintwidget.visibility = View.INVISIBLE
        })
        paintwidget.setOnChangedListener(this)
        paintwidget.firstItemColor = Color.YELLOW
    }

    override fun onChanged(width:String,color:String){
        Toast.makeText(this, "PaintWidget: width = $width, color = $color", Toast.LENGTH_SHORT).show()
    }
}
