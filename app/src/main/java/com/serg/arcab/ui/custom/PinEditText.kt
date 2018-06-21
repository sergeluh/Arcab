package com.serg.arcab.ui.custom

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.text.InputFilter
import android.content.res.ColorStateList
import android.support.v4.content.ContextCompat
import com.serg.arcab.R

class PinEditText: EditText {

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    var mSpace = 24f
    var mCharSize = 0f
    var mNumChars = 6
    var mLineSpacing = 8f

    private var mLineStroke = 1f //1dp by default
    private var mLinesPaint: Paint
    var mStates = arrayOf(intArrayOf(android.R.attr.state_selected),
            intArrayOf(android.R.attr.state_focused),
            intArrayOf(-android.R.attr.state_focused))

    var mColors = intArrayOf(ContextCompat.getColor(context, R.color.colorAccent), Color.GRAY, Color.LTGRAY)
    var mColorStates = ColorStateList(mStates, mColors)

    private var clickListener: View.OnClickListener? = null

    init {
        setBackgroundResource(0)

        val multi = context.resources.displayMetrics.density
        mSpace = multi * mSpace //convert to pixels for our density

        mLineSpacing = multi * mLineSpacing; //convert to pixels

        mLineStroke = multi * mLineStroke
        mLinesPaint = Paint(getPaint())
        mLinesPaint.setStrokeWidth(mLineStroke)

        //Disable copy paste
        super.setCustomSelectionActionModeCallback(
                object : ActionMode.Callback {
                    override fun onPrepareActionMode(mode: ActionMode,
                                            menu: Menu): Boolean {
                        return false
                    }

                    override fun onDestroyActionMode(mode: ActionMode) {}

                    override fun onCreateActionMode(mode: ActionMode,
                                           menu: Menu): Boolean {
                        return false
                    }

                    override fun onActionItemClicked(mode: ActionMode,
                                            item: MenuItem): Boolean {
                        return false
                    }
                })
        //When tapped, move cursor to end of the text
        super.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                setSelection(text.length)
                if (clickListener != null) {
                    clickListener?.onClick(v)
                }
            }
        })

        inputType = EditorInfo.TYPE_TEXT_FLAG_NO_SUGGESTIONS
        filters = arrayOf<InputFilter>(InputFilter.LengthFilter(mNumChars))
    }

    override fun setOnClickListener(listener: View.OnClickListener?) {
        clickListener = listener
    }

    override fun setCustomSelectionActionModeCallback(actionModeCallback: ActionMode.Callback) {
        throw RuntimeException("setCustomSelectionActionModeCallback() not supported.")
    }

    private fun updateColorForLines(next: Boolean) {
        if (isFocused) {
            mLinesPaint.color = getColorForState(android.R.attr.state_focused)
            if (next) {
                mLinesPaint.color = getColorForState(android.R.attr.state_selected)
            }
        } else {
            mLinesPaint.color = getColorForState(-android.R.attr.state_focused)
        }
    }

    private fun getColorForState(vararg states: Int): Int {
        return mColorStates.getColorForState(states, Color.GRAY)
    }

    override fun onDraw(canvas: Canvas) {
//        super.onDraw(canvas)
        val availableWidth = width - paddingRight - paddingLeft
        mCharSize = if (mSpace < 0) {
            (availableWidth / (mNumChars * 2 - 1)).toFloat()
        } else {
            (availableWidth - mSpace * (mNumChars - 1)) / mNumChars
        }

        var startX = paddingLeft.toFloat()
        val bottom = (height - paddingBottom).toFloat()

        val text = text
        val textLength = text.length
        val textWidths = FloatArray(textLength)
        paint.getTextWidths(getText(), 0, textLength, textWidths)

        for (i in 0 until mNumChars) {
            updateColorForLines(i == textLength)
            canvas.drawLine(
                    startX, bottom, startX + mCharSize, bottom, mLinesPaint)

            if (text.length > i) {
                val middle = startX + mCharSize / 2
                canvas.drawText(text,
                        i,
                        i + 1,
                        middle - textWidths[0] / 2,
                        bottom - mLineSpacing,
                        paint)
            }

            startX += if (mSpace < 0) {
                mCharSize * 2
            } else {
                mCharSize + mSpace
            }
        }
    }
}