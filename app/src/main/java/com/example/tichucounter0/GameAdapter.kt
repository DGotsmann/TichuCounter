package com.example.tichucounter0

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import org.threeten.bp.format.DateTimeFormatter

// Adapter for displaying Game objects in RecyclerView
class GamesAdapter(
    private val games: MutableList<Game>, // Mutable list to allow deletion
    private val onGameClick: (Game) -> Unit, // Callback for game click
    private val onGameDeleted: (Game) -> Unit // Callback for game deletion
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

        // Set the long click listener for the button to delete the game
        holder.gameButton.setOnLongClickListener {
            // Show a confirmation dialog
            AlertDialog.Builder(holder.itemView.context)
                .setTitle("Delete Game")
                .setMessage("Are you sure you want to delete this game?")
                .setPositiveButton("Delete") { dialog, _ ->
                    // Remove the game from the list and notify adapter
                    onGameDeleted(game)
                    games.removeAt(holder.adapterPosition)
                    notifyItemRemoved(holder.adapterPosition)
                    dialog.dismiss()
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()

            true // Return true to indicate the long click was handled
        }
    }

    override fun getItemCount(): Int = games.size
}
