package com.example.tichucounter0


import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import android.content.Context
import com.jakewharton.threetenabp.AndroidThreeTen

class PlayersListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_players_list)

        Toast.makeText(this, "we're here", Toast.LENGTH_SHORT).show()

        val playersRecyclerView: RecyclerView = findViewById(R.id.playersRecyclerView)

        // Get the list of saved game names
        val savedGameNames = getSavedGameNames()

        // Load all games from SharedPreferences and sort them by datetime
        val games = savedGameNames.mapNotNull { gameName -> loadGame(gameName) }
            .sortedByDescending { game -> game.dateTime }
            .toMutableList()

        // Get the list of saved player names
        val savedPlayerNames = getPlayerNames(games)

        // Load all players from games from SharedPreferences and sort them by datetime
        val players = savedPlayerNames.mapNotNull { playerName -> loadPlayer(playerName, games) }
            .sortedByDescending { player -> player.player_name }
            .toMutableList()

        if (players.isNotEmpty()) {
            Toast.makeText(this, "Last Player: ${players.last().player_name}", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "No players found", Toast.LENGTH_SHORT).show()
        }

        // Set up the RecyclerView with the adapter and handle the button click
        val adapter = PlayerAdapter(players, onPlayerClick = { player ->
            // Handle the game click: change the view
            
            // POPUP WITH STATS
            Toast.makeText(this, "So much stats! WOw", Toast.LENGTH_SHORT).show()
        })

        playersRecyclerView.layoutManager = LinearLayoutManager(this)
        playersRecyclerView.adapter = adapter

    }

    override fun onBackPressed() {
    // Finish the current task and return to the previous app or screen
    finishAffinity()
    }

    // Function to load a game from SharedPreferences
    private fun loadPlayer(playerName: String, games: MutableList<Game>): Player? {
        // Create a new Player object
        val player = Player()
        player.player_name = playerName

        // Initialize statistics
        var roundsPlayed = 0
        var totalPointDifference = 0
        var numberOfTichus = 0

        // Iterate through all games to calculate stats
        for (game in games) {
            val participants = listOf(game.name1, game.name2, game.name3, game.name4)
            if (playerName in participants) {
                // Increment rounds played
                roundsPlayed += game.round

                // Identify player's team
                val isTeam1 = playerName == game.name1 || playerName == game.name3
                val playerTichus = when (playerName) {
                    game.name1 -> game.tichu1
                    game.name3 -> game.tichu3
                    game.name2 -> game.tichu2
                    game.name4 -> game.tichu4   
                    else -> mutableListOf()
                }

                // Add Tichus declared
                numberOfTichus += playerTichus.sum()

                // Calculate point difference based on the last score entry
                val team1Score = game.score1.lastOrNull() ?: 0
                val team2Score = game.score2.lastOrNull() ?: 0

                if (isTeam1) {
                    totalPointDifference += team1Score - team2Score
                } else {
                    totalPointDifference += team2Score - team1Score
                }
            }
        }

        // Assign calculated stats to the player object
        player.rounds_played = roundsPlayed
        player.total_point_dif = totalPointDifference
        player.number_of_tichus = numberOfTichus

        return player
    }


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

    private fun getPlayerNames(games: MutableList<Game>): Set<String> {
        val playerNames = mutableSetOf<String>()
        for (game in games) {
            playerNames.add(game.name1)
            playerNames.add(game.name2)
            playerNames.add(game.name3)
            playerNames.add(game.name4)
        }
        return playerNames
    }
}



