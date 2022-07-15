package com.loloof64.android.chessboard.ui

import androidx.compose.foundation.layout.requiredSize
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun ChessBoard(totalSize: Dp) {
    val backgroundColor = Color(0xFFBF360C)
    Surface(modifier = Modifier
        .requiredSize(totalSize),
        color = backgroundColor
    ) {

    }
}