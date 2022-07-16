package com.loloof64.android.basicchessendgamestrainer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.loloof64.android.basicchessendgamestrainer.ui.theme.BasicChessEndgamesTheme
import com.loloof64.android.chessboard.ui.ChessBoard
import com.loloof64.android.chessboard.ui.ChessBoardParameters
import com.loloof64.android.chessboard.ui.ChessBoardParametersBuilder

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BasicChessEndgamesTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Greeting("Android")
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    BasicChessEndgamesTheme {
        Greeting("Android")
    }
}

@Preview
@Composable
fun ChessBoardPreview() {
    val boardParameters = ChessBoardParametersBuilder()
        .setBackgroundColorTo(Color.Yellow)
        .setTotalSizeTo(300.dp)
        .build()

    Row(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        ChessBoard(parameters = boardParameters)
    }
}