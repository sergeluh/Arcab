package com.serg.arcab.ui.custom

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.widget.CheckBox
import android.widget.TextView

public class LetterCheckBox @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : CheckBox(context, attrs, defStyleAttr) {

    lateinit var letter: String


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        val paint = Paint()
        paint.color = Color.WHITE
        paint.style = Paint.Style.FILL
        canvas!!.drawPaint(paint)

        paint.color = Color.BLACK
        paint.textSize = 20.toFloat()
        canvas.drawText("Some Text", 1.toFloat(), 1.toFloat(), paint)
    }

}