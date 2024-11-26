package com.example.tictactoe

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tictactoe.ui.theme.TicTacToeTheme
import kotlinx.coroutines.launch
import kotlinx.coroutines.time.delay

enum class Winner {
    PLAYER,
    AI,
    DRAW

}


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TicTacToeTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize()

                ) { TTTScreen() }


            }
        }
    }
}

@Composable
fun TTTScreen() {
    val playerTurn = remember { mutableStateOf(true) }

    //true-player move, false-computer move
    val moves = remember {
        mutableStateListOf<Boolean?>(
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null
        )
    }
    val winner = remember {
        mutableStateOf<Winner?>(null)
    }
    val onTap: (Offset) -> Unit = { offset ->
        if (playerTurn.value) {
            val x = offset.x
            val y = offset.y
            val row = (y / 333).toInt()
            val col = (x / 333).toInt()
            val index = row * 3 + col
            if (moves[index] == null) {
                moves[index] = playerTurn.value
                playerTurn.value = !playerTurn.value
                winner.value = checkEndGame(moves)
            }
        }

    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Tic Tac Toe", fontSize = 30.sp, modifier = Modifier.padding(16.dp))

        Header(playerTurn.value)

        Board(moves, onTap)

        if (!playerTurn.value && winner.value == null) {
            //ai move
            CircularProgressIndicator(color = Color.Red, modifier = Modifier.padding(16.dp))

            val coroutineScope = rememberCoroutineScope()
            LaunchedEffect(key1 = Unit) {
                coroutineScope.launch {
                    kotlinx.coroutines.delay(1500L)
                    //ai move
                    while (true) {
                        val index = (0..8).random()
                        if (moves[index] == null) {
                            moves[index] = playerTurn.value
                            playerTurn.value = !playerTurn.value
                            winner.value = checkEndGame(moves)
                            break
                        }
                    }
                }
            }
        }
        if (winner.value != null) {
            when (winner.value) {
                Winner.PLAYER -> {
                    Text("Player has Won", fontSize = 30.sp)
                }

                Winner.AI -> {
                    Text("AI has Won", fontSize = 30.sp)
                }

                Winner.DRAW -> {
                    Text("Draw", fontSize = 30.sp)
                }

                null -> TODO()
            }

            Button(onClick = {
                moves.clear()
                moves.addAll(listOf(null, null, null, null, null, null, null, null, null))
                playerTurn.value = true
                winner.value = null
            }) {
                Text("Restart")
            }
        }
    }

}

fun checkEndGame(m: List<Boolean?>): Winner? {
    //check rows
    for (i in 0..2) {
        if (m[i * 3] == m[i * 3 + 1] && m[i * 3 + 1] == m[i * 3 + 2]) {
            if (m[i * 3] == true) {
                return Winner.PLAYER
            } else if (m[i * 3] == false) {
                return Winner.AI
            }
        }
    }

    //check columns
    for (i in 0..2) {
        if (m[i] == m[i + 3] && m[i + 3] == m[i + 6]) {
            if (m[i] == true) {
                return Winner.PLAYER
            } else if (m[i] == false) {
                return Winner.AI
            }
        }
    }

    //check diagonals
    if (m[0] == m[4] && m[4] == m[8]) {
        if (m[0] == true) {
            return Winner.PLAYER
        } else if (m[0] == false) {
            return Winner.AI
        }
    }

    if (m[2] == m[4] && m[4] == m[6]) {
        if (m[2] == true) {
            return Winner.PLAYER
        } else if (m[2] == false) {
            return Winner.AI
        }
    }

    //check draw
    if (m.all { it != null }) {
        return Winner.DRAW
    }

    return null

}


@Composable
fun Header(playerTurn: Boolean) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        val playerBoxColor = if (playerTurn) Color.Blue else Color.LightGray
        val aiBoxColor = if (!playerTurn) Color.Red else Color.LightGray
        Box(
            modifier = Modifier
                .width(100.dp)
                .background(playerBoxColor)
        ) {

            Text(
                "Player", modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.Center)

            )


        }
        Spacer(modifier = Modifier.size(10.dp))
        Box(
            modifier = Modifier
                .width(100.dp)
                .background(aiBoxColor)
        ) {
            Text(
                "AI", modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.Center)
            )
        }
    }
}

@Composable
fun Board(moves: List<Boolean?>, onTap: (Offset) -> Unit) {
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .padding(32.dp)
            .background(Color.LightGray)
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = onTap
                )
            }
    ) {

        Column(verticalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxSize(1f)) {
            Row(
                modifier = Modifier
                    .height(2.dp)
                    .fillMaxSize(1f)
                    .background(Color.Black)
            ) {

            }
            Row(
                modifier = Modifier
                    .height(2.dp)
                    .fillMaxSize(1f)
                    .background(Color.Black)
            ) {

            }
        }

        Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxSize(1f)) {
            Column(
                modifier = Modifier
                    .width(2.dp)
                    .fillMaxHeight(1f)
                    .background(Color.Black)
            ) { }
            Column(
                modifier = Modifier
                    .width(2.dp)
                    .fillMaxHeight(1f)
                    .background(Color.Black)
            ) { }
        }

        Column(modifier = Modifier.fillMaxSize(1f)) {
            for (i in 0..2) {
                Row(modifier = Modifier.weight(1f)) {
                    for (j in 0..2) {
                        Column(
                            modifier = Modifier
                                .weight(1f)
                        )
                        {
                            getComposableFromMove(move = moves[i * 3 + j])
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun getComposableFromMove(move: Boolean?) {
    when (move) {
        true -> Image(
            painter = painterResource(id = R.drawable.ic_x),
            contentDescription = "",
            modifier = Modifier.fillMaxSize(1f),
            colorFilter = ColorFilter.tint(Color.Blue)
        )

        false -> Image(
            painter = painterResource(id = R.drawable.ic_o),
            contentDescription = "",
            modifier = Modifier.fillMaxSize(1f),
            colorFilter = ColorFilter.tint(Color.Red)
        )

        null -> Image(
            painter = painterResource(id = R.drawable.ic_null),
            contentDescription = "",
            modifier = Modifier.fillMaxSize(1f),

            )
    }

}