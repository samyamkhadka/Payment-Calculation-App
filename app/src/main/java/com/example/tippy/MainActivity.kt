package com.example.tippy

import android.animation.ArgbEvaluator
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import android.widget.SeekBar
import android.widget.TextView
import androidx.core.content.ContextCompat

private const val TAG = "MainActivity"
private const val INITIAL_TIP_PERCENT = 15
class MainActivity : AppCompatActivity() {
    private lateinit var etBaseAmount: EditText
    private lateinit var seekBarTip:SeekBar
    private lateinit var tvTipPercentLabel: TextView
    private lateinit var tvTipAmount : TextView
    private lateinit var tvTotalAmount : TextView
    private lateinit var tvTipDescription : TextView
    private lateinit var etSplitBetween : EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        etBaseAmount = findViewById(R.id.etBaseAmount)
        seekBarTip = findViewById(R.id.seekBarTip)
        tvTipPercentLabel = findViewById(R.id.tvTipPercentLabel)
        tvTipAmount = findViewById(R.id.tvTipAmount)
        tvTotalAmount = findViewById(R.id.tvTotalAmount)
        tvTipDescription = findViewById(R.id.tvTipDescription)
        etSplitBetween = findViewById(R.id.etSplitBetween)

        seekBarTip.progress = INITIAL_TIP_PERCENT
        tvTipPercentLabel.text = "$INITIAL_TIP_PERCENT%"
        updateTipDescription(INITIAL_TIP_PERCENT)
        seekBarTip.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                Log.i(TAG,"onProgressChanged $p1")
                tvTipPercentLabel.text = "$p1%"
                computeTipAndTotal(false)
                updateTipDescription(p1)
            }

            override fun onStartTrackingTouch(p0: SeekBar?){}

            override fun onStopTrackingTouch(p0: SeekBar?) {}

        })

        etSplitBetween.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                computeTipAndTotal(true)
            }

            override fun afterTextChanged(p0: Editable?) {}

        })

        etBaseAmount.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(p0: Editable?) {
                Log.i(TAG, "afterTextChange $p0")
                computeTipAndTotal(false)
            }

        })
    }

    private fun computeTipAndTotal(splitCheck: Boolean) {
        if(etBaseAmount.text.isBlank()) {
            tvTipAmount.text = ""
            tvTotalAmount.text = ""
            return
        }
        //1. Get the value of the base and tip percentage
        val baseAmount = etBaseAmount.text.toString().toDouble()
        val tipPercent = seekBarTip.progress.toString().toDouble()
        //2. Compute the tip and total
        val tipAmount = baseAmount * tipPercent / 100
        var totalAmount = baseAmount + tipAmount
        if(splitCheck && etSplitBetween.text.isNotBlank()) {
            totalAmount /= etSplitBetween.text.toString().toDouble();
        }
        //3. Update the UI
        tvTipAmount.text = "%.2f".format(tipAmount)
        tvTotalAmount.text = "%.2f".format(totalAmount)
    }

    private fun updateTipDescription(tipPercent: Int){
        val tipDescription = when(tipPercent){
            in 0..9 -> "Poor!"
            in 10..14 -> "Acceptable!"
            in 15..19 -> "Good!"
            in 20..24 -> "Great!"
            else -> "Amazing!"
        }
        tvTipDescription.text = tipDescription
        //Update the color based on the tipPercent
        val color = ArgbEvaluator().evaluate(
            tipPercent.toFloat() / seekBarTip.max,
            ContextCompat.getColor(this, R.color.color_worst_tip),
                    ContextCompat.getColor(this, R.color.color_best_tip)
        ) as Int
        tvTipDescription.setTextColor(color)

    }
}

