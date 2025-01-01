package com.example.tichucounter0


import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.app.AlertDialog
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import android.content.Context
import com.jakewharton.threetenabp.AndroidThreeTen

class GamesListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_games_list)

        val gamesRecyclerView: RecyclerView = findViewById(R.id.gamesRecyclerView)
        val newGameButton: Button = findViewById(R.id.newGameButton)
        val showStatsButton: Button = findViewById(R.id.showStatsButton)

        // Get the list of saved game names
        val savedGameNames = getSavedGameNames()

        // Load all games from SharedPreferences and sort them by datetime
        val games = savedGameNames.mapNotNull { gameName -> loadGame(gameName) }
            .sortedByDescending { game -> game.dateTime }
            .toMutableList()

        // Set up the RecyclerView with the adapter and handle the button click
        val adapter = GamesAdapter(games, onGameClick = { game ->
            // Handle the game click: change the view
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("gameName", game.game_name) // Pass the game name to MainActivity
            startActivity(intent)
        }, onGameDeleted = { game ->
            // Handle the game deletion: remove the game from the list and update the view
            val sharedPreferences: SharedPreferences = getSharedPreferences("SavedGames", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.remove(game.game_name)
            editor.apply()
            Toast.makeText(this, "Game deleted!", Toast.LENGTH_SHORT).show()
        })

        gamesRecyclerView.layoutManager = LinearLayoutManager(this)
        gamesRecyclerView.adapter = adapter

        // Set up the new game button to start MainActivity without passing a game name
        newGameButton.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Create Game")

            // Create an input field for the game name
            val input = EditText(this)
            input.hint = "Enter Game Name"
            builder.setView(input)

            builder.setPositiveButton("Create") { dialog, which ->
                val gameName = input.text.toString()

                if (gameName.isNotEmpty()) {
                    // Check if the game name already exists
                    val sharedPreferences: SharedPreferences = getSharedPreferences("SavedGames", Context.MODE_PRIVATE)
                    if (sharedPreferences.contains(gameName)) {
                        Toast.makeText(this, "Name already exists", Toast.LENGTH_SHORT).show()
                    } 
                    else {
                    // Save the game using SharedPreferences
                    val game = Game()
                    game.game_name = gameName
                    val gson = Gson()
                    val gameJson = gson.toJson(game)
                    val editor = sharedPreferences.edit()
                    editor.putString(gameName, gameJson)
                    editor.apply()
                    // load the game screen
                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra("gameName", gameName)
                    startActivity(intent)
                    }
                }
                else {
                    Toast.makeText(this, "Please enter a name", Toast.LENGTH_SHORT).show()
                }
            }
            builder.setNegativeButton("Cancel", null)
            builder.show()
        }

        // Set up the new game button to start MainActivity without passing a game name
        showStatsButton.setOnClickListener {
            val intent = Intent(this, PlayersListActivity::class.java)
            // No need to add any extras here since this is for a new game
            startActivity(intent)
        }

    }

    override fun onBackPressed() {
    // Finish the current task and return to the previous app or screen
    finishAffinity()
    }

    // Function to load a game from SharedPreferences
    private fun loadGame(gameName: String): Game? {
        val sharedPreferences: SharedPreferences = getSharedPreferences("SavedGames", Context.MODE_PRIVATE)

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

    // Function to get all saved game names
    private fun getSavedGameNames(): Set<String> {
        val sharedPreferences: SharedPreferences = getSharedPreferences("SavedGames", Context.MODE_PRIVATE)
        return sharedPreferences.all.keys
    }
}

