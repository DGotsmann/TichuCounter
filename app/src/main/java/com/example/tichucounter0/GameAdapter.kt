package com.example.tichucounter0

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tichucounter0.Game
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter

// Adapter for displaying Game objects in RecyclerView
class GamesAdapter(
    private val games: List<Game>,
    private val onGameClick: (Game) -> Unit // Callback for game click
) : RecyclerView.Adapter<GamesAdapter.GameViewHolder>() {

    class GameViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val gameButton: Button = itemView.findViewById(R.id.gameButton)
        val gameNameTextView: TextView = itemView.findViewById(R.id.gameNameTextView)
        val currentScoreTextView: TextView = itemView.findViewById(R.id.currentScoreTextView)
        val playerNamesTextView: TextView = itemView.findViewById(R.id.playerNamesTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_game, parent, false)
        return GameViewHolder(view)
    }

    override fun onBindViewHolder(holder: GameViewHolder, position: Int) {
        val game = games[position]

        // Display the current scores (last element in score1 and score2 lists)
        val scoreTeamA = game.score1.lastOrNull() ?: 0
        val scoreTeamB = game.score2.lastOrNull() ?: 0
        val round = game.round

        // Display the player names
        val playerNames = listOf(game.name1, game.name2, game.name3, game.name4)

        // Set the text values to the views
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
        val datetimeString = game.dateTime?.format(formatter) ?: "Unknown date"
        val gameTitle = "${game.game_name} - ($round) - $datetimeString"
        holder.gameNameTextView.text = gameTitle
        holder.currentScoreTextView.text = "Score: $scoreTeamA | $scoreTeamB"
        holder.playerNamesTextView.text = "Teams: ${game.name1}, ${game.name2} | ${game.name3}, ${game.name4}"

        // Set the click listener for the button to load the game
        holder.gameButton.setOnClickListener {
            onGameClick(game)
        }
    }

    override fun getItemCount(): Int = games.size
}
