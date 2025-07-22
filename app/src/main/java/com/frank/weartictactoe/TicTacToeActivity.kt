package com.frank.weartictactoe

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.ui.draw.clip

class TicTacToeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                TicTacToePager()
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TicTacToePager() {
    val pagerState = rememberPagerState(pageCount = { 2 })
    var xScore by rememberSaveable { mutableStateOf(0) }
    var oScore by rememberSaveable { mutableStateOf(0) }
    var startingPlayer by rememberSaveable { mutableStateOf("X") }
    var resetKey by remember { mutableStateOf(0) } // To force recomposition on reset

    Box(modifier = Modifier.fillMaxSize()) {
        HorizontalPager(state = pagerState) { page ->
            when (page) {
                0 -> TicTacToeScreen(
                    xScore = xScore,
                    oScore = oScore,
                    startingPlayer = startingPlayer,
                    onPlayerWin = { winner ->
                        if (winner == "X") xScore++
                        if (winner == "O") oScore++
                    },
                    onRestart = {
                        // Alternate starting player after each game restart
                        startingPlayer = if (startingPlayer == "X") "O" else "X"
                        resetKey++
                    },
                    resetKey = resetKey
                )
                1 -> ScoreboardScreen(
                    xScore = xScore,
                    oScore = oScore,
                    onResetScores = {
                        xScore = 0
                        oScore = 0
                        startingPlayer = "X" // Always X after score reset
                        resetKey++
                    }
                )
            }
        }
    }
}

@Composable
fun ScoreboardScreen(xScore: Int, oScore: Int, onResetScores: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Scoreboard", fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 16.dp))
        Row(
            modifier = Modifier.padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Player X", fontWeight = FontWeight.Bold)
                Text("$xScore", fontSize = 24.sp, color = Color(0xFF1976D2), fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.width(32.dp))
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Player O", fontWeight = FontWeight.Bold)
                Text("$oScore", fontSize = 24.sp, color = Color(0xFFD32F2F), fontWeight = FontWeight.Bold)
            }
        }
        androidx.wear.compose.material.Button(
            onClick = onResetScores,
            modifier = Modifier
                .height(24.dp)
                .width(100.dp),
            shape = RoundedCornerShape(50)
        ) {
            Text("Reset Scores", fontSize = 12.sp)
        }
    }
}

@Composable
fun TicTacToeScreen(
    xScore: Int,
    oScore: Int,
    startingPlayer: String,
    onPlayerWin: (String) -> Unit,
    onRestart: () -> Unit,
    resetKey: Int
) {
    var board by remember(resetKey) { mutableStateOf(List(3) { MutableList(3) { "" } }) }
    var currentPlayer by remember(resetKey) { mutableStateOf(startingPlayer) }
    var winner by remember(resetKey) { mutableStateOf<String?>(null) }
    var isDraw by remember(resetKey) { mutableStateOf(false) }
    var winningCells by remember(resetKey) { mutableStateOf<List<Pair<Int, Int>>>(emptyList()) }

    val winColor = Color(0xFF90CAF9) // Light blue
    val winTextColor = Color(0xFF0D47A1) // Dark blue

    fun checkWinner(): String? {
        // Rows and columns
        for (i in 0..2) {
            if (board[i][0] != "" && board[i][0] == board[i][1] && board[i][1] == board[i][2]) {
                winningCells = listOf(Pair(i, 0), Pair(i, 1), Pair(i, 2))
                return board[i][0]
            }
            if (board[0][i] != "" && board[0][i] == board[1][i] && board[1][i] == board[2][i]) {
                winningCells = listOf(Pair(0, i), Pair(1, i), Pair(2, i))
                return board[0][i]
            }
        }
        // Diagonals
        if (board[0][0] != "" && board[0][0] == board[1][1] && board[1][1] == board[2][2]) {
            winningCells = listOf(Pair(0, 0), Pair(1, 1), Pair(2, 2))
            return board[0][0]
        }
        if (board[0][2] != "" && board[0][2] == board[1][1] && board[1][1] == board[2][0]) {
            winningCells = listOf(Pair(0, 2), Pair(1, 1), Pair(2, 0))
            return board[0][2]
        }
        winningCells = emptyList()
        return null
    }

    fun checkDraw(): Boolean {
        return board.all { row -> row.all { it != "" } } && winner == null
    }

    fun resetGame() {
        onRestart()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            when {
                winner != null -> Text(
                    text = "Player $winner wins!",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp),
                    color = winColor
                )
                isDraw -> Text(
                    text = "It's a draw!",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp),
                    color = Color.Yellow
                )
                else -> Text(
                    text = "Player $currentPlayer's turn",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            for (row in 0..2) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    for (col in 0..2) {
                        val isWinningCell = winningCells.contains(Pair(row, col))
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(40.dp)
                                .padding(1.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isWinningCell) winColor else Color.DarkGray)
                                .clickable(enabled = board[row][col] == "" && winner == null && !isDraw) {
                                    if (board[row][col] == "" && winner == null && !isDraw) {
                                        val newBoard = board.map { it.toMutableList() }.toMutableList()
                                        newBoard[row][col] = currentPlayer
                                        board = newBoard
                                        winner = checkWinner()
                                        isDraw = checkDraw()
                                        if (winner == null && !isDraw) {
                                            currentPlayer = if (currentPlayer == "X") "O" else "X"
                                        }
                                        if (winner != null) {
                                            onPlayerWin(winner!!)
                                        }
                                    }
                                }
                        ) {
                            Text(
                                text = board[row][col],
                                fontSize = 20.sp,
                                color = if (isWinningCell) winTextColor else Color.White,
                                fontWeight = if (isWinningCell) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        androidx.wear.compose.material.Button(
            onClick = { resetGame() },
            modifier = Modifier
                .padding(bottom = 4.dp)
                .height(24.dp)
                .width(100.dp),
            shape = RoundedCornerShape(50)
        ) {
            Text("Restart", fontSize = 12.sp)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TicTacToeScreenPreview() {
    MaterialTheme {
        TicTacToeScreen(0, 0, "X", {}, {}, 0)
    }
} 