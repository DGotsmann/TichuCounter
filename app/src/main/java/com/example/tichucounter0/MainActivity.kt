package com.example.tichucounter0

import android.content.res.Configuration
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.SeekBar
import android.widget.TextView

import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.Entry

class MainActivity : AppCompatActivity() {

override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    //initializing variables and objects

    var currentgame: Game = Game() //game variable, that saves scores, tichus and names

    var isupdating: Boolean = false //used to stop infinite updates of textEdits
    var sliderValue: Int = 16 // position of slider, Default value (center)

    // Get the current theme mode
    val nightModeFlags = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
    val isNightMode = nightModeFlags == Configuration.UI_MODE_NIGHT_YES


    val saveButton: Button = findViewById(R.id.saveButton)
    val scoreInput1: EditText = findViewById(R.id.scoreInput1)
    val scoreInput2: EditText = findViewById(R.id.scoreInput2)
    val score1TextView: TextView = findViewById(R.id.scoreTextView1)
    score1TextView.text = "Team A: 0"
    val score2TextView: TextView = findViewById(R.id.scoreTextView2)
    score2TextView.text = "Team B: 0"
    val seekBar: SeekBar = findViewById(R.id.scoresliderBar)
    seekBar.progress = sliderValue
    val lineChart: LineChart = findViewById(R.id.lineChart)
    val plusButton1: Button = findViewById(R.id.plusButton1)
    val plusButton2: Button = findViewById(R.id.plusButton2)
    val plusButton3: Button = findViewById(R.id.plusButton3)
    val plusButton4: Button = findViewById(R.id.plusButton4)
    val minusButton1: Button = findViewById(R.id.minusButton1)
    val minusButton2: Button = findViewById(R.id.minusButton2)
    val minusButton3: Button = findViewById(R.id.minusButton3)
    val minusButton4: Button = findViewById(R.id.minusButton4)
    val tichuView1: TextView = findViewById(R.id.tichuView1)
    val tichuView2: TextView = findViewById(R.id.tichuView2)
    val tichuView3: TextView = findViewById(R.id.tichuView3)
    val tichuView4: TextView = findViewById(R.id.tichuView4)

//Function to display score of round in entry field,  only to be called when isupdating == true
    fun displayroundscore(){
        var scr1 = 0
        var scr2 = 0
        if (sliderValue==0){
            scr1 = 200
            scr2 = 0
        }
        else if (sliderValue==32){
            scr1 = 0
            scr2 = 200
        }
        else{
            scr1 = (sliderValue-6)*5
            scr2 = 100-(sliderValue-6)*5
        }

        scr1 += 100*(currentgame.tichu1.last() + currentgame.tichu2.last())
        scr2 += 100*(currentgame.tichu3.last() + currentgame.tichu4.last())

        scoreInput1.setText(scr1.toString())
        scoreInput2.setText(scr2.toString())
        seekBar.progress = sliderValue
    }

//Graph implementation--------------------------------------------------------------------------
    fun displayscoreschart() {
        // Create Entry lists for score1 and score2
        val entries1 = mutableListOf<Entry>()
        val entries2 = mutableListOf<Entry>()

        for (i in currentgame.score1.indices) {
            entries1.add(Entry(i.toFloat(), currentgame.score1[i].toFloat()))
            entries2.add(Entry(i.toFloat(), currentgame.score2[i].toFloat()))
        }

        // Create LineDataSet objects for score1 and score2
        val dataSet1 = LineDataSet(entries1, "Score 1")
        //dataSet1.color = Color.BLUE

        val dataSet2 = LineDataSet(entries2, "Score 2")
        //dataSet2.color = Color.RED

        // Create LineData object and add LineDataSet objects
        val lineData = LineData(dataSet1, dataSet2)

        // Set LineData to LineChart
        lineChart.data = lineData

        lineChart.description.isEnabled = false
        lineChart.xAxis.setDrawLabels(false)
        lineChart.legend.isEnabled = false
        // Refresh chart
        lineChart.invalidate()
    }
    displayscoreschart() //initial display

//Manual Score Input----------------------------------------------------------------------------
    scoreInput1.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            // Not needed in this case
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if (!isupdating) {
                isupdating = true

                val inputText = s.toString()
                if (inputText.isNotEmpty() && inputText != "-"){
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

//Plus/Minus-Buttons ---------------------------------------------------------------------------
    fun adjusttichuview(view: TextView,tichucount: Int){
        if(tichucount == 0){
            view.text = "Kein\nTichu"
            view.setTextColor(if (isNightMode) Color.WHITE else Color.BLACK)
        }
        else if(kotlin.math.abs(tichucount) == 1){
            view.text = "Tichu"
        }
        else if(kotlin.math.abs(tichucount) == 2){
            view.text = "Grosses\nTichu"
        }
        else if(kotlin.math.abs(tichucount) == 3){
            view.text = "Ganz\nGrosses"
        }
        if(tichucount > 0){
            view.setTextColor(Color.GREEN)
        }
        else if(tichucount < 0){
            view.setTextColor(Color.RED)
        }
    }

    plusButton1.setOnClickListener {
        if(currentgame.tichu1.last() < 3){currentgame.tichu1[currentgame.tichu1.size-1] += 1}
        adjusttichuview(tichuView1, currentgame.tichu1.last())
        isupdating = true
        displayroundscore()
        isupdating = false
    }
    plusButton2.setOnClickListener {
        if(currentgame.tichu2.last() < 3){currentgame.tichu2[currentgame.tichu2.size-1] += 1}
        adjusttichuview(tichuView2, currentgame.tichu2.last())
        isupdating = true
        displayroundscore()
        isupdating = false
    }
    plusButton3.setOnClickListener {
        if(currentgame.tichu3.last() < 3){currentgame.tichu3[currentgame.tichu3.size-1] += 1}
        adjusttichuview(tichuView3, currentgame.tichu3.last())
        isupdating = true
        displayroundscore()
        isupdating = false
    }
    plusButton4.setOnClickListener {
        if(currentgame.tichu4.last() < 3){currentgame.tichu4[currentgame.tichu4.size-1] += 1}
        adjusttichuview(tichuView4, currentgame.tichu4.last())
        isupdating = true
        displayroundscore()
        isupdating = false
    }
    minusButton1.setOnClickListener {
        if(currentgame.tichu1.last() > -3){currentgame.tichu1[currentgame.tichu1.size-1] -= 1}
        adjusttichuview(tichuView1, currentgame.tichu1.last())
        isupdating = true
        displayroundscore()
        isupdating = false
    }
    minusButton2.setOnClickListener {
        if(currentgame.tichu2.last() > -3){currentgame.tichu2[currentgame.tichu2.size-1] -= 1}
        adjusttichuview(tichuView2, currentgame.tichu2.last())
        isupdating = true
        displayroundscore()
        isupdating = false
    }
    minusButton3.setOnClickListener {
        if(currentgame.tichu3.last() > -3){currentgame.tichu3[currentgame.tichu3.size-1] -= 1}
        adjusttichuview(tichuView3, currentgame.tichu3.last())
        isupdating = true
        displayroundscore()
        isupdating = false
    }
    minusButton4.setOnClickListener {
        if(currentgame.tichu4.last() > -3){currentgame.tichu4[currentgame.tichu4.size-1] -= 1}
        adjusttichuview(tichuView4, currentgame.tichu4.last())
        isupdating = true
        displayroundscore()
        isupdating = false
    }

//Save Button ----------------------------------------------------------------------------------
    saveButton.setOnClickListener {
        val in1 = scoreInput1.text.toString()
        val in2 = scoreInput2.text.toString()


        if (in1.isNotEmpty() and in2.isNotEmpty()) {
            // Get the entered number from the EditText
            currentgame.score1.add(currentgame.score1[currentgame.score1.size-1] + scoreInput1.text.toString().toInt())
            currentgame.score2.add(currentgame.score2[currentgame.score2.size-1] + scoreInput2.text.toString().toInt())
            displayscoreschart()

            // Display the result in the TextView
            score1TextView.text = "Team A: ${currentgame.score1[currentgame.score1.size-1]}"
            score2TextView.text = "Team B: ${currentgame.score2[currentgame.score2.size-1]}"

            // Clear TextInput fields
            scoreInput1.text.clear()
            scoreInput2.text.clear()
            sliderValue = 16
            seekBar.progress = sliderValue

            //reset tichuviews
            currentgame.tichu1.add(0)
            currentgame.tichu2.add(0)
            currentgame.tichu3.add(0)
            currentgame.tichu4.add(0)
            adjusttichuview(tichuView1, currentgame.tichu1[currentgame.tichu1.size-1])
            adjusttichuview(tichuView2, currentgame.tichu2[currentgame.tichu2.size-1])
            adjusttichuview(tichuView3, currentgame.tichu3[currentgame.tichu3.size-1])
            adjusttichuview(tichuView4, currentgame.tichu4[currentgame.tichu4.size-1])
        }
    }
}
}