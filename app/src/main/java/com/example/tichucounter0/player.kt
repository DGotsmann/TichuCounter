package com.example.tichucounter0
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter

class Player// Default constructor
    () {
    // Properties
    var player_name: String
    var rounds_played: Int
    var total_point_dif: Int
    var avg_points_p_round: Int
    var opp_points_p_round: Int
    var avg_points_p_round_w_me: Int
    var avg_points_p_round_against_me: Int
    var tichus_called: Int
    var tichus_won: Int
    var small_tichus_called: Int
    var small_tichus_won: Int
    var grand_tichus_called: Int
    var grand_tichus_won: Int
    var ganz_grossi_tichus_called: Int
    var ganz_grossi_tichus_won: Int

    init {
        player_name = "unnamed player"
        rounds_played = 0
        total_point_dif = 0
        avg_points_p_round = 0
        opp_points_p_round = 0
        avg_points_p_round_w_me = 0
        avg_points_p_round_against_me = 0
        tichus_called = 0
        tichus_won = 0
        grand_tichus_called = 0
        grand_tichus_won = 0
        ganz_grossi_tichus_called = 0
        ganz_grossi_tichus_won = 0
        small_tichus_called = 0
        small_tichus_won = 0
    }
}

