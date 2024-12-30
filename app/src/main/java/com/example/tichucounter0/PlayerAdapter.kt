package com.example.tichucounter0

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import androidx.constraintlayout.widget.ConstraintLayout
import org.threeten.bp.format.DateTimeFormatter

// Adapter for displaying Player objects in RecyclerView
class PlayerAdapter(
    private val players: MutableList<Player>, // Mutable list to allow deletion
    private val onPlayerClick: (Player) -> Unit, // Callback for player click
) : RecyclerView.Adapter<PlayerAdapter.PlayerViewHolder>() {

    class PlayerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val playerContainer: ConstraintLayout = itemView.findViewById(R.id.playerContainer)
        val playerNameTextView: TextView = itemView.findViewById(R.id.playerNameTextView)
        val currentStatTextView: TextView = itemView.findViewById(R.id.currentStatTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_player, parent, false)
        return PlayerViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlayerViewHolder, position: Int) {
        val player = players[position]

        // Display the current scores (last element in score1 and score2 lists)
        val rounds_played = player.rounds_played ?: 0

        // Set the text values to the views
        val playerTitle = "${player.player_name}"
        holder.playerNameTextView.text = playerTitle
        holder.currentStatTextView.text = "Rounds Played: $rounds_played | hallo"

        // Set the click listener for the button to load the player
        holder.playerContainer.setOnClickListener {
            onPlayerClick(player)
        }
    }

    override fun getItemCount(): Int = players.size
}
