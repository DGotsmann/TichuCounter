package com.example.tichucounter0
import java.time.LocalDateTime

class Game// Default constructor
    () {
    // Properties
    var game_name: String
    var score1: MutableList<Int>
    var score2: MutableList<Int>
    var tichu1: MutableList<Int>
    var tichu2: MutableList<Int>
    var tichu3: MutableList<Int>
    var tichu4: MutableList<Int>
    var name1: String
    var name2: String
    var name3: String
    var name4: String
    var dateTime: LocalDateTime

    

    init {
        game_name = "unnamed game"
        score1 = mutableListOf(0)
        score2 = mutableListOf(0)
        tichu1 = mutableListOf(0)
        tichu2 = mutableListOf(0)
        tichu3 = mutableListOf(0)
        tichu4 = mutableListOf(0)
        name1 = ""
        name2 = ""
        name3 = ""
        name4 = ""
        dateTime = LocalDateTime.now()
    }
}

