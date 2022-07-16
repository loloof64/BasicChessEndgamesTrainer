package com.loloof64.android.chessboard.ui

import androidx.compose.foundation.layout.requiredSize
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

class ChessBoardParametersBuilder
{
    var totalSize: Dp = 100.dp
    var backgroundColor = Color(0xFFBF360C)

    fun setTotalSizeTo(size: Dp) : ChessBoardParametersBuilder {
        totalSize = size
        return this
    }

    fun setBackgroundColorTo(color: Color): ChessBoardParametersBuilder {
        backgroundColor = color
        return this
    }

    fun build(): ChessBoardParameters {
        return ChessBoardParameters(
            backgroundColor = backgroundColor,
            totalSize = totalSize
        )
    }
}

data class ChessBoardParameters(
    val backgroundColor: Color,
    val totalSize: Dp
)

@Composable
fun ChessBoard(modifier: Modifier = Modifier,
        parameters: ChessBoardParameters
               ) {

    Surface(modifier = modifier
        .requiredSize(parameters.totalSize),
        color = parameters.backgroundColor
    ) {

    }
}