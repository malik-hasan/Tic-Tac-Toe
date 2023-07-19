package me.malikhasan.tic_tac_toe

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.malikhasan.tic_tac_toe.ui.theme.TicTacToeTheme
import kotlin.random.Random

enum class Win {
    PLAYER, AI, DRAW
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TicTacToeTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    TicTacToeScreen()
                }
            }
        }
    }
}

@Composable
fun TicTacToeScreen() {
    //true - player's turn; false - AI's turn
    var playerTurn by remember { mutableStateOf<Boolean?>(true) }
    //true - player's move; false - AI's move; null - no move
    val moves = remember { mutableStateListOf<Boolean?>(*arrayOfNulls(9)) }
    var win by remember { mutableStateOf<Win?>(null) }

    val onClick = { index: Int ->
        if (moves[index] == null && playerTurn == true && win == null) {
            moves[index] = true
            win = evaluateWin(moves)
            if (win == Win.DRAW)
                playerTurn = null
            else if (win != Win.PLAYER)
                playerTurn = false
        }
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = "Tic Tac Toe", fontSize = 30.sp, modifier = Modifier.padding(16.dp))
        Header(playerTurn)
        Board(moves, onClick)

        if (playerTurn == false && win == null) {
            CircularProgressIndicator(color = Color.Red, modifier = Modifier.padding(16.dp))

            val coroutineScope = rememberCoroutineScope()
            LaunchedEffect(key1 = Unit) {
                coroutineScope.launch {
                    delay(1500)
                    var i = Random.nextInt(9)
                    while (moves[i] != null) {
                        i = Random.nextInt(9)
                    }
                    moves[i] = false
                    win = evaluateWin(moves)
                    if (win == Win.DRAW)
                        playerTurn = null
                    else if (win != Win.AI)
                        playerTurn = true
                }
            }
        }

        win?.let {
            when (it) {
                Win.PLAYER -> {
                    Text(text = "You win!! \uD83C\uDF89", fontSize = 25.sp)
                }

                Win.AI -> {
                    Text(text = "Computer wins. \uD83D\uDE24", fontSize = 25.sp)
                }

                Win.DRAW -> {
                    Text(text = "It's a draw. \uD83D\uDE33", fontSize = 25.sp)
                }
            }
            Button(onClick = {
                playerTurn = true
                win = null
                moves.replaceAll { null }
            }) {
                Text(text = "Start Over")
            }
        }
    }
}

fun evaluateWin(moves: List<Boolean?>): Win? {
    val winCombinations = listOf(
        listOf(0, 1, 2),
        listOf(3, 4, 5),
        listOf(6, 7, 8),
        listOf(0, 3, 6),
        listOf(1, 4, 7),
        listOf(2, 5, 8),
        listOf(0, 4, 8),
        listOf(2, 4, 6)
    )

    for (combination in winCombinations) {
        if (combination.all { moves[it] == true }) return Win.PLAYER
        else if (combination.all { moves[it] == false }) return Win.AI
    }

    return if (moves.all { it != null }) Win.DRAW else null
}

@Composable
fun Header(playerTurn: Boolean?) {
    Row {
        PlayerBox(player = "Player", color = Color.Blue, playerTurn = playerTurn)
        PlayerBox(
            player = "Computer",
            color = Color.Red,
            playerTurn = playerTurn?.let { !playerTurn })
    }
}

@Composable
fun RowScope.PlayerBox(player: String, color: Color, playerTurn: Boolean?) {
    Box(
        modifier = Modifier
            .weight(1f)
            .background(if (playerTurn == true) color else Color.LightGray)
    ) {
        Text(text = player, modifier = Modifier.padding(8.dp))
    }
}

@Composable
fun Board(moves: List<Boolean?>, onClick: (Int) -> Unit) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3), modifier = Modifier
            .padding(10.dp)
            .border(
                BorderStroke(2.dp, Color.White)
            )
    ) {
        itemsIndexed(items = moves) { index, move ->
            Box(modifier = Modifier
                .aspectRatio(1f)
                .background(Color.LightGray)
                .clickable { onClick(index) }
                .border(BorderStroke(2.dp, Color.Black))) {
                Move(move)
            }
        }
    }
}

@Composable
fun Move(move: Boolean?) {
    when (move) {
        true -> Image(
            painter = painterResource(id = R.drawable.ic_x),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            colorFilter = ColorFilter.tint(Color.Blue)
        )

        false -> Image(
            painter = painterResource(id = R.drawable.ic_o),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            colorFilter = ColorFilter.tint(Color.Red)
        )

        else -> null
    }
}
