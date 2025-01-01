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
import android.view.View
import androidx.core.content.ContextCompat
import android.app.AlertDialog
import android.content.Intent   // Import for Intent
import android.content.Context
import android.content.SharedPreferences
import android.widget.Toast
import com.jakewharton.threetenabp.AndroidThreeTen
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter


import com.google.gson.Gson

import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.Entry
import kotlin.math.abs

class MainActivity : AppCompatActivity() {
lateinit var currentgame: Game


fun saveGame(gameName: String, game: Game) {
    val sharedPreferences: SharedPreferences = getSharedPreferences("SavedGames", Context.MODE_PRIVATE)
    // Save the names currently written in the name boxes only if they are not empty
    findViewById<EditText>(R.id.nameInput_me).text.toString().let {
        if (it.isNotEmpty()) currentgame.name_me = it
    }
    findViewById<EditText>(R.id.nameInput_teammate).text.toString().let {
        if (it.isNotEmpty()) currentgame.name_teammate = it
    }
    findViewById<EditText>(R.id.nameInput_enemy1).text.toString().let {
        if (it.isNotEmpty()) currentgame.name_enemy1 = it
    }
    findViewById<EditText>(R.id.nameInput_enemy2).text.toString().let {
        if (it.isNotEmpty()) currentgame.name_enemy2 = it
    }

    game.game_name = gameName
    game.dateTime = LocalDateTime.now()
    val gson = Gson()
    val gameJson = gson.toJson(game)
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

fun adjusttichuview(view: TextView, tichucount: Int, isNightMode: Boolean){
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
    

override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    //initializing variables and objects
    var isupdating: Boolean = false //used to stop infinite updates of textEdits
    var sliderValue: Int = 16 // position of slider, Default value (center)

    // Get the current theme mode
    var nightModeFlags = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
    var isNightMode = nightModeFlags == Configuration.UI_MODE_NIGHT_YES


    val nextRoundButton: Button = findViewById(R.id.nextRoundButton)
    val resetRoundButton: Button = findViewById(R.id.resetRoundButton)
    val scoreInput1: TextView = findViewById(R.id.scoreInput1)
    val scoreInput2: TextView = findViewById(R.id.scoreInput2)
    val seekBar: SeekBar = findViewById(R.id.scoresliderBar)
    seekBar.progress = sliderValue
    val lineChart: LineChart = findViewById(R.id.lineChart)
    val plusButton_me: Button = findViewById(R.id.plusButton_me)
    val plusButton_teammate: Button = findViewById(R.id.plusButton_teammate)
    val plusButton_enemy1: Button = findViewById(R.id.plusButton_enemy1)
    val plusButton_enemy2: Button = findViewById(R.id.plusButton_enemy2)
    val minusButton_me: Button = findViewById(R.id.minusButton_me)
    val minusButton_teammate: Button = findViewById(R.id.minusButton_teammate)
    val minusButton_enemy1: Button = findViewById(R.id.minusButton_enemy1)
    val minusButton_enemy2: Button = findViewById(R.id.minusButton_enemy2)
    val tichuView_me: TextView = findViewById(R.id.tichuView_me)
    val tichuView_teammate: TextView = findViewById(R.id.tichuView_teammate)
    val tichuView_enemy1: TextView = findViewById(R.id.tichuView_enemy1)
    val tichuView_enemy2: TextView = findViewById(R.id.tichuView_enemy2)

    // Check if a Game was passed as an argument in the Intent
    val gameName = intent.getStringExtra("gameName")
    if (gameName != null) {
        val passedGame = loadGame(gameName) ?: Game()
        currentgame = passedGame
        // Set the names in the UI
        findViewById<EditText>(R.id.nameInput_me).setText(currentgame.name_me)
        findViewById<EditText>(R.id.nameInput_teammate).setText(currentgame.name_teammate)
        findViewById<EditText>(R.id.nameInput_enemy1).setText(currentgame.name_enemy1)
        findViewById<EditText>(R.id.nameInput_enemy2).setText(currentgame.name_enemy2)
        // Set tichus to 0 on the ui
        adjusttichuview(tichuView_me, 0, isNightMode)
        adjusttichuview(tichuView_teammate, 0, isNightMode)
        adjusttichuview(tichuView_enemy1, 0, isNightMode)
        adjusttichuview(tichuView_enemy2, 0, isNightMode)
    } else {
        // If no Game was passed, create a new one
        currentgame = Game()
    }


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
            scr1 = 100-(sliderValue-6)*5
            scr2 = (sliderValue-6)*5
        }

        scr1 += 100*(currentgame.tichu_me.last() + currentgame.tichu_teammate.last())
        scr2 += 100*(currentgame.tichu_enemy1.last() + currentgame.tichu_enemy2.last())

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

        val teamAScore = "My Bros: ${currentgame.score1[currentgame.score1.size-1]}             "
        val teamBScore = "The Hoes: ${currentgame.score2[currentgame.score2.size-1]}"

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

//Slider Implementation-------------------------------------------------------------------------
seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        // Save the slider value when moved
        isupdating = true
        sliderValue = progress

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
            scr1 = 100-(sliderValue-6)*5
            scr2 = (sliderValue-6)*5
        }

        displayroundscore()

        // Update the bubble text with the current progress value
        val seekBarBubble: TextView = findViewById(R.id.seekBarBubble)
        seekBarBubble.text = scr1.toString() + "|" + scr2.toString()

        // Position the bubble above the thumb
        val thumbPosX = seekBar?.thumb?.bounds?.exactCenterX()?.toInt() ?: 0
        val bubblePosX = thumbPosX + 15 - seekBarBubble.width / 2
        seekBarBubble.x = bubblePosX.toFloat()

        // Show the bubble while dragging
        seekBarBubble.visibility = View.VISIBLE
        seekBarBubble.bringToFront() // Ensure bubble stays in front while dragging

        isupdating = false
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {
        // Show the bubble when the user starts interacting
        findViewById<TextView>(R.id.seekBarBubble).visibility = View.VISIBLE
        findViewById<TextView>(R.id.seekBarBubble).bringToFront() // Ensure bubble stays in front while dragging
    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {
        // Hide the bubble when the user stops interacting
        findViewById<TextView>(R.id.seekBarBubble).visibility = View.INVISIBLE
    }
})


//Plus/Minus-Buttons ---------------------------------------------------------------------------

    plusButton_me.setOnClickListener {
        if(currentgame.tichu_me.last() < 3){currentgame.tichu_me[currentgame.tichu_me.size-1] += 1}
        adjusttichuview(tichuView_me, currentgame.tichu_me.last(), isNightMode)
        isupdating = true
        displayroundscore()
        isupdating = false
    }
    plusButton_teammate.setOnClickListener {
        if(currentgame.tichu_teammate.last() < 3){currentgame.tichu_teammate[currentgame.tichu_teammate.size-1] += 1}
        adjusttichuview(tichuView_teammate, currentgame.tichu_teammate.last(), isNightMode)
        isupdating = true
        displayroundscore()
        isupdating = false
    }
    plusButton_enemy1.setOnClickListener {
        if(currentgame.tichu_enemy1.last() < 3){currentgame.tichu_enemy1[currentgame.tichu_enemy1.size-1] += 1}
        adjusttichuview(tichuView_enemy1, currentgame.tichu_enemy1.last(), isNightMode)
        isupdating = true
        displayroundscore()
        isupdating = false
    }
    plusButton_enemy2.setOnClickListener {
        if(currentgame.tichu_enemy2.last() < 3){currentgame.tichu_enemy2[currentgame.tichu_enemy2.size-1] += 1}
        adjusttichuview(tichuView_enemy2, currentgame.tichu_enemy2.last(), isNightMode)
        isupdating = true
        displayroundscore()
        isupdating = false
    }
    minusButton_me.setOnClickListener {
        if(currentgame.tichu_me.last() > -3){currentgame.tichu_me[currentgame.tichu_me.size-1] -= 1}
        adjusttichuview(tichuView_me, currentgame.tichu_me.last(), isNightMode)
        isupdating = true
        displayroundscore()
        isupdating = false
    }
    minusButton_teammate.setOnClickListener {
        if(currentgame.tichu_teammate.last() > -3){currentgame.tichu_teammate[currentgame.tichu_teammate.size-1] -= 1}
        adjusttichuview(tichuView_teammate, currentgame.tichu_teammate.last(), isNightMode)
        isupdating = true
        displayroundscore()
        isupdating = false
    }
    minusButton_enemy1.setOnClickListener {
        if(currentgame.tichu_enemy1.last() > -3){currentgame.tichu_enemy1[currentgame.tichu_enemy1.size-1] -= 1}
        adjusttichuview(tichuView_enemy1, currentgame.tichu_enemy1.last(), isNightMode)
        isupdating = true
        displayroundscore()
        isupdating = false
    }
    minusButton_enemy2.setOnClickListener {
        if(currentgame.tichu_enemy2.last() > -3){currentgame.tichu_enemy2[currentgame.tichu_enemy2.size-1] -= 1}
        adjusttichuview(tichuView_enemy2, currentgame.tichu_enemy2.last(), isNightMode)
        isupdating = true
        displayroundscore()
        isupdating = false
    }

//Actually the Next Round Button ----------------------------------------------------------------------------------
    nextRoundButton.setOnClickListener {

        val in1 = scoreInput1.text.toString()
        val in2 = scoreInput2.text.toString()


        if (in1.isNotEmpty() and in2.isNotEmpty()) {
            // Get the entered number from the EditText
            currentgame.score1.add(currentgame.score1[currentgame.score1.size-1] + scoreInput1.text.toString().toInt())
            currentgame.score2.add(currentgame.score2[currentgame.score2.size-1] + scoreInput2.text.toString().toInt())
            currentgame.round += 1
        }

        
        // Save the names currently written in the name boxes only if they are not empty
        findViewById<EditText>(R.id.nameInput_me).text.toString().let {
            if (it.isNotEmpty()) currentgame.name_me = it
        }
        findViewById<EditText>(R.id.nameInput_teammate).text.toString().let {
            if (it.isNotEmpty()) currentgame.name_teammate = it
        }
        findViewById<EditText>(R.id.nameInput_enemy1).text.toString().let {
            if (it.isNotEmpty()) currentgame.name_enemy1 = it
        }
        findViewById<EditText>(R.id.nameInput_enemy2).text.toString().let {
            if (it.isNotEmpty()) currentgame.name_enemy2 = it
        }

        //reset tichuviews
        currentgame.tichu_me.add(0)
        currentgame.tichu_teammate.add(0)
        currentgame.tichu_enemy1.add(0)
        currentgame.tichu_enemy2.add(0)
        adjusttichuview(tichuView_me, currentgame.tichu_me[currentgame.tichu_me.size-1], isNightMode)
        adjusttichuview(tichuView_teammate, currentgame.tichu_teammate[currentgame.tichu_teammate.size-1], isNightMode)
        adjusttichuview(tichuView_enemy1, currentgame.tichu_enemy1[currentgame.tichu_enemy1.size-1], isNightMode)
        adjusttichuview(tichuView_enemy2, currentgame.tichu_enemy2[currentgame.tichu_enemy2.size-1], isNightMode)

        displayscoreschart()

        val sharedPreferences: SharedPreferences = getSharedPreferences("SavedGames", Context.MODE_PRIVATE)
        currentgame.dateTime = LocalDateTime.now()
        val gson = Gson()
        val gameJson = gson.toJson(currentgame)
        val editor = sharedPreferences.edit()
        editor.putString(currentgame.game_name, gameJson)
        editor.apply()

        // Clear TextInput fields
        val fifty = 50
        scoreInput1.setText(fifty.toString())
        scoreInput2.setText(fifty.toString())
        sliderValue = 16
        seekBar.progress = sliderValue
        val seekBarBubble: TextView = findViewById(R.id.seekBarBubble)
        seekBarBubble.visibility = View.INVISIBLE
    }

    // Function to get all saved game names
    fun getSavedGameNames(): Set<String> {
        val sharedPreferences: SharedPreferences = getSharedPreferences("SavedGames", MODE_PRIVATE)
        return sharedPreferences.all.keys
    }

    resetRoundButton.setOnClickListener {
        if (currentgame.score1.isEmpty() || currentgame.score1.size == 1) {
            // Do nothing
        } else {
            currentgame.round -= 1
            currentgame.score1.removeLast()
            currentgame.score2.removeLast()
            currentgame.tichu_me.removeLast()
            currentgame.tichu_teammate.removeLast()
            currentgame.tichu_enemy1.removeLast()
            currentgame.tichu_enemy2.removeLast()
            displayscoreschart()
            val fifty = 50
            isupdating = true
            scoreInput1.setText(fifty.toString())
            scoreInput2.setText(fifty.toString())
            isupdating = false

            val sharedPreferences: SharedPreferences = getSharedPreferences("SavedGames", Context.MODE_PRIVATE)
            currentgame.dateTime = LocalDateTime.now()
            val gson = Gson()
            val gameJson = gson.toJson(currentgame)
            val editor = sharedPreferences.edit()
            editor.putString(currentgame.game_name, gameJson)
            editor.apply()
        }
    }
}
override fun onBackPressed() {
    // save the game with its current name 
    saveGame(currentgame.game_name, currentgame)
}
}