package com.e.paintwidget

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Color.BLACK
import android.graphics.ColorFilter
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.*
import android.os.Bundle
import android.os.Parcelable
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import android.util.Log
import android.util.StateSet
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.paintwidget_view.view.*

class PaintWidget @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr){
    var listener : OnChangedListener? = null
    var seekBarMaxWidth = 10
        set(value){
            field = value
            widthSeekBar.max = field
        }
    var defaultColorPosition = 1
        set(value){
        field = value
        when (field) {
            1 -> radiobutton_first.isChecked = true
            2 -> radiobutton_red.isChecked = true
            3 -> radiobutton_green.isChecked = true
            4 -> radiobutton_blue.isChecked = true
            else -> throw IllegalArgumentException("1-4 can only be")
        }
    }
    var firstItemColor = Color.CYAN
    set(value) {
        field = value
        initRadioButton(radiobutton_first,field)
    }

    init {
        View.inflate(context, R.layout.paintwidget_view, this)
        widthSeekBar.setOnSeekBarChangeListener(object:SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {}
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                val paintWidth = widthSeekBar.progress.toString()
                widthValueTextView.text = paintWidth
                val checkedButton = findViewById<RadioButton>(radiobutton_group_colors.checkedRadioButtonId)
                val intColor = checkedButton.tag as Int
                listener?.onChanged(paintWidth, String.format("#%06X", 0xFFFFFF and intColor))
            }
        })
        initAttributes()
        initRadioButton(radiobutton_first,firstItemColor)
        initRadioButton(radiobutton_blue,Color.BLUE)
        initRadioButton(radiobutton_green,Color.GREEN)
        initRadioButton(radiobutton_red,Color.RED)
    }
    private fun initAttributes(){
        val attrs = context.obtainStyledAttributes(R.styleable.PaintWidget)
        defaultColorPosition = attrs.getInteger(R.styleable.PaintWidget_defaultColorPosition, 2)
        seekBarMaxWidth = attrs.getInteger(R.styleable.PaintWidget_maxWidth, 10)
        firstItemColor = attrs.getInteger(R.styleable.PaintWidget_firstItemColor, BLACK)
        attrs.recycle()
    }
    private fun initRadioButton(radioButton: RadioButton, intColor: Int){
        var checkedStateDrawable = GradientDrawable().apply {
                shape = GradientDrawable.OVAL
                setColor(intColor)
                setSize(60, 60)
                setStroke(10, Color.parseColor("#808080"))
        }
        var uncheckedStateDrawable = GradientDrawable().apply {
            shape = GradientDrawable.OVAL
            setColor(intColor)
            setSize(40,40)
        }
        var stateListDrawable = StateListDrawable()
        stateListDrawable.addState(intArrayOf(android.R.attr.state_checked

        ),checkedStateDrawable)
        stateListDrawable.addState(StateSet.WILD_CARD, uncheckedStateDrawable)
        radioButton.buttonDrawable = stateListDrawable

        radioButton.tag = intColor
    }
    interface OnChangedListener{
        fun onChanged(width:String,color:String)
    }
    fun setOnChangedListener(context:Context) {
        if (context is OnChangedListener) {
            listener = context
        }
    }
    private fun setThumbColor(intColor: Int) {
        widthSeekBar.thumb.setColorFilter(PorterDuffColorFilter(intColor,PorterDuff.Mode.SRC_ATOP))
        widthSeekBar.progressDrawable.setColorFilter(PorterDuffColorFilter(intColor,PorterDuff.Mode.SRC_ATOP))
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        radiobutton_group_colors.setOnCheckedChangeListener {
                group, _ ->
            val checkedButton = findViewById<RadioButton>(group.checkedRadioButtonId)
            val intColor = checkedButton.tag as Int
            listener?.onChanged(widthSeekBar.progress.toString(), String.format("#%06X", 0xFFFFFF and intColor))
            setThumbColor(intColor)
        }
    }

    override fun onSaveInstanceState(): Parcelable? {
        val bundle = Bundle()
        bundle.putParcelable(SUPER_STATE, super.onSaveInstanceState())
        bundle.putInt(SEEKBAR_PROGRESS, widthSeekBar.progress)
        return bundle
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state is Bundle) {
            val seekbarProgress = state.getInt(SEEKBAR_PROGRESS)
            widthValueTextView.text = seekbarProgress.toString()
            val superState:Parcelable? = state.getParcelable(SUPER_STATE)
            super.onRestoreInstanceState(superState)
            return
        }
        super.onRestoreInstanceState(state)
    }
    private companion object{
        private const val SUPER_STATE = "superState"
        private const val SEEKBAR_PROGRESS = "seekBarProgress"
    }
}