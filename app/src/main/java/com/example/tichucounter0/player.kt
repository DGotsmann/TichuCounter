package com.example.tichucounter0
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter

class Player// Default constructor
    () {
    // Properties
    var player_name: String
    var rounds_played: Int
    var total_point_dif: Int
    var number_of_tichus: Int


    init {
        player_name = "unnamed player"
        rounds_played = 0
        total_point_dif = 0
        number_of_tichus = 0
    }
}

