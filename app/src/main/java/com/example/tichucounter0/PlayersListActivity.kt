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
import android.view.LayoutInflater
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
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
            .sortedByDescending { player -> player.rounds_played }
            .toMutableList()

        // Set up the RecyclerView with the adapter and handle the button click
        val adapter = PlayerAdapter(players, onPlayerClick = { player ->
            // Inflate the popup layout
            val dialogView = LayoutInflater.from(this).inflate(R.layout.player_stats_popup, null)

            // Create the dialog
            val dialog = AlertDialog.Builder(this)
                .setView(dialogView)
                .create()

            // Populate the stats in the popup
            dialogView.findViewById<TextView>(R.id.playerNameTextView).text = player.player_name
            dialogView.findViewById<TextView>(R.id.roundsPlayedTextView).text           = "Rounds Played:                       ${player.rounds_played}"
            dialogView.findViewById<TextView>(R.id.avgPointsPerRoundTextView).text      = "Avg Points Per Round:           ${player.avg_points_p_round}"
            dialogView.findViewById<TextView>(R.id.oppPointsPerRoundTextView).text      = "Opp Points Per Round:          ${player.opp_points_p_round}"
            dialogView.findViewById<TextView>(R.id.avgPointsWithMeTextView).text        = "Avg Points With Me:               ${player.avg_points_p_round_w_me}"
            dialogView.findViewById<TextView>(R.id.avgPointsAgainstMeTextView).text     = "Avg Points Against Me:         ${player.avg_points_p_round_against_me}"
            dialogView.findViewById<TextView>(R.id.tichusDeclaredTextView).text         = "Total Tichus:                           ${player.tichus_won}/${player.tichus_called}"
            dialogView.findViewById<TextView>(R.id.smallTichusTextView).text            = "Small Tichus:                          ${player.small_tichus_won}/${player.small_tichus_called}"
            dialogView.findViewById<TextView>(R.id.grandTichusTextView).text            = "Grossi Tichus:                        ${player.grand_tichus_won}/${player.grand_tichus_called}"
            dialogView.findViewById<TextView>(R.id.ganzGrossiTichusTextView).text       = "Ganz Grossi Tichus:              ${player.ganz_grossi_tichus_won}/${player.ganz_grossi_tichus_called}"

            // Handle the close button
            dialogView.findViewById<Button>(R.id.closeButton).setOnClickListener {
                dialog.dismiss()
            }

            // Show the dialog
            dialog.show()
        })



        playersRecyclerView.layoutManager = LinearLayoutManager(this)
        playersRecyclerView.adapter = adapter

    }


    // Function to load a game from SharedPreferences
    private fun loadPlayer(playerName: String, games: MutableList<Game>): Player? {
    // Create a new Player object
    val player = Player()
    player.player_name = playerName

    // Initialize statistics
    var roundsPlayed = 0
    var totalPointDifference = 0
    var totalPointsScored = 0
    var totalOppPoints = 0
    var tichusCalled = 0
    var tichusWon = 0
    var smallTichusCalled = 0
    var smallTichusWon = 0
    var grandTichusCalled = 0
    var grandTichusWon = 0
    var ganzGrossiTichusCalled = 0
    var ganzGrossiTichusWon = 0
    var totalPointsWithMe = 0
    var totalPointsAgainstMe = 0
    var roundsWithMe = 0

    // Iterate through all games to calculate stats
    for (game in games) {
        val participants = listOf(game.name_me, game.name_teammate, game.name_enemy1, game.name_enemy2)
        if (playerName in participants) {
            // Increment rounds played
            roundsPlayed += game.round - 1

            // Identify player's team
            val isTeam1 = playerName == game.name_me || playerName == game.name_teammate
            val playerTichus = when (playerName) {
                game.name_me -> game.tichu_me
                game.name_enemy1 -> game.tichu_enemy1
                game.name_teammate -> game.tichu_teammate
                game.name_enemy2 -> game.tichu_enemy2
                else -> mutableListOf()
            }

            // Rounds played with me
            if (isTeam1) {
                roundsWithMe += game.round - 1
            }

            // Add Tichus declared
            tichusCalled += playerTichus.count { it != 0 }
            smallTichusCalled += playerTichus.count { it == 1 || it == -1 }
            grandTichusCalled += playerTichus.count { it == 2 || it == -2 }
            ganzGrossiTichusCalled += playerTichus.count { it == 3 || it == -3 }

            // Check won Tichus
            tichusWon = playerTichus.count { it > 0 }
            smallTichusWon = playerTichus.count { it == 1 }
            grandTichusWon = playerTichus.count { it == 2 }
            ganzGrossiTichusWon = playerTichus.count { it == 3 }

            // Calculate point difference and totals
            val team1Score = game.score1.lastOrNull() ?: 0
            val team2Score = game.score2.lastOrNull() ?: 0
            if (isTeam1) {
                totalPointDifference += team1Score - team2Score
                totalPointsScored += team1Score
                totalOppPoints += team2Score
                totalPointsWithMe += team1Score
            } else {
                totalPointDifference += team2Score - team1Score
                totalPointsScored += team2Score
                totalOppPoints += team1Score
                totalPointsAgainstMe += team2Score
            }
        }
    }

    // Assign calculated stats to the player object
    player.rounds_played = roundsPlayed
    player.total_point_dif = totalPointDifference
    player.avg_points_p_round = if (roundsPlayed > 0) totalPointsScored / roundsPlayed else 0
    player.opp_points_p_round = if (roundsPlayed > 0) totalOppPoints / roundsPlayed else 0
    player.avg_points_p_round_w_me = if (roundsWithMe > 0) totalPointsWithMe / roundsWithMe else 0
    player.avg_points_p_round_against_me = if (roundsPlayed - roundsWithMe > 0) totalPointsAgainstMe / (roundsPlayed - roundsWithMe) else 0
    player.tichus_called = tichusCalled
    player.tichus_won = tichusWon
    player.small_tichus_called = smallTichusCalled
    player.small_tichus_won = smallTichusWon
    player.grand_tichus_called = grandTichusCalled
    player.grand_tichus_won = grandTichusWon
    player.ganz_grossi_tichus_called = ganzGrossiTichusCalled
    player.ganz_grossi_tichus_won = ganzGrossiTichusWon

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
            playerNames.add(game.name_me)
            playerNames.add(game.name_teammate)
            playerNames.add(game.name_enemy1)
            playerNames.add(game.name_enemy2)
        }
        return playerNames
    }
}



