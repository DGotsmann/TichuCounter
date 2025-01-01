package com.example.tichucounter0
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter

class Game// Default constructor
    () {
    // Properties
    var game_name: String
    var score1: MutableList<Int>
    var score2: MutableList<Int>
    var tichu_me: MutableList<Int>
    var tichu_teammate: MutableList<Int>
    var tichu_enemy1: MutableList<Int>
    var tichu_enemy2: MutableList<Int>
    var name_me: String
    var name_teammate: String
    var name_enemy1: String
    var name_enemy2: String
    var dateTime: LocalDateTime? = LocalDateTime.now() 
    var round: Int


    init {
        game_name = "unnamed game"
        score1 = mutableListOf(0)
        score2 = mutableListOf(0)
        tichu_me = mutableListOf(0)
        tichu_teammate = mutableListOf(0)
        tichu_enemy1 = mutableListOf(0)
        tichu_enemy2 = mutableListOf(0)
        name_me = "You"
        name_teammate = "Your Teammate"
        name_enemy1 = "Enemy1"
        name_enemy2 = "Enemy2"
        round = 1
    }
}

