package com.example.domino_scoring

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class GameViewModel : ViewModel() {
    val players = mutableStateOf<List<Player>>(emptyList())
    val currentPlayerIndex = mutableStateOf(0)

    private val turnHistory = mutableListOf<Pair<Int, Int>>()

    fun addPlayer(name: String) {
        val currentPlayers = players.value.toMutableList()
        currentPlayers.add(Player(name))
        players.value = currentPlayers
    }

    fun updateScore(playerIndex: Int, scoreToAdd: Int) {
        val currentPlayers = players.value.toMutableList()
        if (playerIndex >= 0 && playerIndex < currentPlayers.size) {
            val oldPlayer = currentPlayers[playerIndex]
            val newPlayer = oldPlayer.copy(score = oldPlayer.score + scoreToAdd)
            currentPlayers[playerIndex] = newPlayer
            players.value = currentPlayers
            turnHistory.add(playerIndex to scoreToAdd)
        }
    }

    fun nextTurn() {
        val nextPlayer = (currentPlayerIndex.value + 1) % players.value.size
        currentPlayerIndex.value = nextPlayer
    }

    fun undo() {
        if (turnHistory.isNotEmpty()) {
            val (lastPlayerIndex, lastScore) = turnHistory.removeAt(turnHistory.lastIndex)
            val currentPlayers = players.value.toMutableList()
            val oldPlayer = currentPlayers[lastPlayerIndex]
            val newPlayer = oldPlayer.copy(score = oldPlayer.score - lastScore)
            currentPlayers[lastPlayerIndex] = newPlayer
            players.value = currentPlayers
            currentPlayerIndex.value = lastPlayerIndex
        }
    }
}
