package com.example.tichucounter0

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.SeekBar
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //initializing variables and objects

        var score1: Int = 0
        var score2: Int = 0
        var isupdating: Boolean = false //used to stop infinite updates of textEdits
        var sliderValue: Int = 16 // position of slider, Default value (center)

        val saveButton: Button = findViewById(R.id.finishButton)
        val scoreInput1: EditText = findViewById(R.id.scoreInput1)
        val scoreInput2: EditText = findViewById(R.id.scoreInput2)
        val score1TextView: TextView = findViewById(R.id.scoreTextView1)
        val score2TextView: TextView = findViewById(R.id.scoreTextView2)
        val seekBar: SeekBar = findViewById(R.id.scoresliderBar)
        seekBar.progress = sliderValue

        // Display the result in the TextView
        score1TextView.text = "Team A: $score1"
        score2TextView.text = "Team B: $score2"

    //Function to display score of round,  only to be called when isupdating == true ---------------
        fun displayroundscore(){
            if (sliderValue==0){
                scoreInput1.setText("200")
                scoreInput2.setText("0")
            }
            else if (sliderValue==32){
                scoreInput1.setText("0")
                scoreInput2.setText("200")
            }
            else{
                scoreInput1.setText(((sliderValue-6)*5).toString())
                scoreInput2.setText((100-((sliderValue-6)*5)).toString())
            }

            seekBar.progress = sliderValue
        }
    //Save Button ----------------------------------------------------------------------------------
        saveButton.setOnClickListener {
            var in1 = scoreInput1.text.toString()
            var in2 = scoreInput2.text.toString()

            if (in1.isNotEmpty() and in2.isNotEmpty()) {
                // Get the entered number from the EditText
                score1 += scoreInput1.text.toString().toInt()
                score2 += scoreInput2.text.toString().toInt()


                // Display the result in the TextView
                score1TextView.text = "Team A: $score1"
                score2TextView.text = "Team B: $score2"

                // Clear TextInput fields
                scoreInput1.text.clear()
                scoreInput2.text.clear()
                sliderValue = 16
                seekBar.progress = sliderValue
            }
        }

    //Manual Score Input----------------------------------------------------------------------------
        scoreInput1.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Not needed in this case
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!isupdating) {
                    isupdating = true

                    val inputText = s.toString()
                    if (inputText.isNotEmpty()){
                        if (inputText.toInt()%5 == 0){//need for userfriendly input
                        sliderValue = inputText.toInt()/5+6
                        displayroundscore()
                    }}
                    scoreInput1.setSelection(scoreInput1.text.length)

                    isupdating = false
                }
            }

            override fun afterTextChanged(s: Editable?) {
                // Not needed in this case
            }
        })

        scoreInput2.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Not needed in this case
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!isupdating) {
                    isupdating = true

                    val inputText = s.toString()
                    if (inputText.isNotEmpty()){//need for userfriendly input
                        if (inputText.toInt()%5 == 0){
                        sliderValue = (100-inputText.toInt())/5+6
                        displayroundscore()
                    }}

                    scoreInput2.setSelection(scoreInput2.text.length)

                    isupdating = false
                }
            }

            override fun afterTextChanged(s: Editable?) {
                // Not needed in this case
            }
        })

    //Slider Implementation-------------------------------------------------------------------------
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                // Save the slider value when moved
                isupdating = true
                sliderValue = progress
                displayroundscore()
                isupdating = false
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                // Not needed in this case
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                // Not needed in this case
            }
        })

    //Graph implementation--------------------------------------------------------------------------

    }
}