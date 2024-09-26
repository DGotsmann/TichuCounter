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
import androidx.core.content.ContextCompat
import android.app.AlertDialog
import android.content.Intent   // Import for Intent
import android.content.Context
import android.content.SharedPreferences
import android.widget.Toast
import java.time.LocalDateTime


import com.google.gson.Gson

import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.Entry
import kotlin.math.abs

class MainActivity : AppCompatActivity() {

lateinit var currentgame: Game

// Function to load a game from SharedPreferences
fun loadGame(gameName: String): Game? {
    val sharedPreferences: SharedPreferences = getSharedPreferences("SavedGames", MODE_PRIVATE)
    // Get the game JSON string from SharedPreferences
    val gameJson = sharedPreferences.getString(gameName, null)
    // If no game is found, return null
    if (gameJson == null) {
        Toast.makeText(this, "Game not found!", Toast.LENGTH_SHORT).show()
        return null
    }
    // Convert the JSON string back to a Game object
    val gson = Gson()
    return gson.fromJson(gameJson, Game::class.java)
}

override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    //initializing variables and objects

    // Check if a Game was passed as an argument in the Intent
    val gameName = intent.getStringExtra("gameName")
    if (gameName != null) {
        val passedGame = loadGame(gameName) ?: Game()
        currentgame = passedGame
        // Set the names in the UI
        findViewById<EditText>(R.id.nameInput1).setText(currentgame.name1)
        findViewById<EditText>(R.id.nameInput2).setText(currentgame.name2)
        findViewById<EditText>(R.id.nameInput3).setText(currentgame.name3)
        findViewById<EditText>(R.id.nameInput4).setText(currentgame.name4)
    } else {
        // If no Game was passed, create a new one
        currentgame = Game()
    }

    var isupdating: Boolean = false //used to stop infinite updates of textEdits
    var sliderValue: Int = 16 // position of slider, Default value (center)

    // Get the current theme mode
    val nightModeFlags = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
    val isNightMode = nightModeFlags == Configuration.UI_MODE_NIGHT_YES


    val saveButton: Button = findViewById(R.id.saveButton)
    val saveGameButton: Button = findViewById(R.id.saveGameButton)
    val scoreInput1: EditText = findViewById(R.id.scoreInput1)
    val scoreInput2: EditText = findViewById(R.id.scoreInput2)
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
        val primaryTextColor = ContextCompat.getColor(this, R.color.primaryTextColor)

        val teamAScore = "Team A: ${currentgame.score1[currentgame.score1.size-1]}             "
        val teamBScore = "Team B: ${currentgame.score2[currentgame.score2.size-1]}"

        // Create LineDataSet objects for score1 and score2
        val dataSet1 = LineDataSet(entries1, teamAScore)
        dataSet1.color = ContextCompat.getColor(this, R.color.blue) // Use a resource color
        dataSet1.setCircleColor(primaryTextColor)
        dataSet1.valueTextColor = primaryTextColor
        dataSet1.valueTextSize = 11f // Increase the size of the tag text


        val dataSet2 = LineDataSet(entries2, teamBScore)
        dataSet2.color = ContextCompat.getColor(this, R.color.red) // Use a resource color
        dataSet2.setCircleColor(primaryTextColor)
        dataSet2.valueTextColor = primaryTextColor
        dataSet2.valueTextSize = 11f // Increase the size of the tag text

        // Create LineData object and add LineDataSet objects
        val lineData = LineData(dataSet1, dataSet2)

        // Set LineData to LineChart
        lineChart.data = lineData
        // Customize chart appearance
        lineChart.description.isEnabled = false
        lineChart.xAxis.setDrawLabels(true) // Enable X axis labels if needed
        lineChart.xAxis.isGranularityEnabled = true // Enable granularity for labels
        lineChart.xAxis.granularity = 1f // Set granularity to 2 (even numbers)
        lineChart.legend.isEnabled = true // Enable legend if needed
        lineChart.legend.textColor = primaryTextColor // Set legend text color to primaryTextColor
        lineChart.legend.textSize = 20f // Set legend text size to 12f
        // Apply the color to your chart axis text
        lineChart.xAxis.textColor = primaryTextColor
        lineChart.axisLeft.textColor = primaryTextColor
        lineChart.axisRight.isEnabled = false // Disable right axis

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
        else if(abs(tichucount) == 1){
            view.text = "Tichu"
        }
        else if(abs(tichucount) == 2){
            view.text = "Grosses\nTichu"
        }
        else if(abs(tichucount) == 3){
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

//Actually the Next Round Button ----------------------------------------------------------------------------------
    saveButton.setOnClickListener {
        val in1 = scoreInput1.text.toString()
        val in2 = scoreInput2.text.toString()


        if (in1.isNotEmpty() and in2.isNotEmpty()) {
            // Get the entered number from the EditText
            currentgame.score1.add(currentgame.score1[currentgame.score1.size-1] + scoreInput1.text.toString().toInt())
            currentgame.score2.add(currentgame.score2[currentgame.score2.size-1] + scoreInput2.text.toString().toInt())
            displayscoreschart()

            // Clear TextInput fields
            scoreInput1.text.clear()
            scoreInput2.text.clear()
            sliderValue = 16
            seekBar.progress = sliderValue

            // Save the names currently written in the name boxes
            currentgame.name1 = findViewById<EditText>(R.id.nameInput1).text.toString()
            currentgame.name2 = findViewById<EditText>(R.id.nameInput2).text.toString()
            currentgame.name3 = findViewById<EditText>(R.id.nameInput3).text.toString()
            currentgame.name4 = findViewById<EditText>(R.id.nameInput4).text.toString()

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

    fun saveGame(gameName: String, game: Game) {
        val sharedPreferences: SharedPreferences = getSharedPreferences("SavedGames", Context.MODE_PRIVATE)

        game.game_name = gameName
        game.dateTime = LocalDateTime.now()

        // Convert the game object to a JSON string
        val gson = Gson()
        val gameJson = gson.toJson(game)

        // Save the JSON string with the game name as the key
        val editor = sharedPreferences.edit()
        editor.putString(gameName, gameJson)
        editor.apply()

        Toast.makeText(this, "Game saved successfully!", Toast.LENGTH_SHORT).show()

        // Navigate to the GamesListActivity after saving
        val intent = Intent(this, GamesListActivity::class.java)
        startActivity(intent)

        // Optionally, finish the current activity to remove it from the back stack
        finish()

    }

    // Function to get all saved game names
    fun getSavedGameNames(): Set<String> {
        val sharedPreferences: SharedPreferences = getSharedPreferences("SavedGames", MODE_PRIVATE)
        return sharedPreferences.all.keys
    }



    saveGameButton.setOnClickListener {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Save Game")

        // Check if the game name is already set
        if (currentgame.game_name.isNotEmpty() && currentgame.game_name != "unnamed game") {
            // Save the game using the existing game name
            saveGame(currentgame.game_name, currentgame)
        }

        // Create an input field for the game name
        val input = EditText(this)
        input.hint = "Enter Game Name"
        builder.setView(input)

        builder.setPositiveButton("Save") { dialog, which ->
            val gameName = input.text.toString()

            if (gameName.isNotEmpty()) {
                // Check if the game name already exists
                val sharedPreferences: SharedPreferences = getSharedPreferences("SavedGames", Context.MODE_PRIVATE)
                if (sharedPreferences.contains(gameName)) {
                    Toast.makeText(this, "Name already exists", Toast.LENGTH_SHORT).show()
                } 
                else {
                // Save the game using SharedPreferences
                saveGame(gameName, currentgame)
                }
            }
        }
        builder.setNegativeButton("Cancel", null)
        builder.show()
    }


}
}