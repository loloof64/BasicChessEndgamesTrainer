package com.loloof64.android.chessboard.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

class ChessBoardParametersBuilder {
    var totalSize: Dp = 100.dp
    var backgroundColor = Color(0xFF795548)
    var whiteCellsColor = Color(0xFFFFF176)
    var blackCellsColor = Color(0xFF572212)

    fun setTotalSizeTo(size: Dp): ChessBoardParametersBuilder {
        totalSize = size
        return this
    }

    fun setBackgroundColorTo(color: Color): ChessBoardParametersBuilder {
        backgroundColor = color
        return this
    }

    fun setWhiteCellsColorTo(color: Color): ChessBoardParametersBuilder {
        whiteCellsColor = color
        return this
    }

    fun setBlackCellsColorTo(color: Color): ChessBoardParametersBuilder {
        blackCellsColor = color
        return this
    }

    fun build(): ChessBoardParameters {
        return ChessBoardParameters(
            backgroundColor = backgroundColor,
            totalSize = totalSize,
            whiteCellsColor = whiteCellsColor,
            blackCellsColor = blackCellsColor,
        )
    }
}

data class ChessBoardParameters(
    val backgroundColor: Color,
    val totalSize: Dp,
    val whiteCellsColor: Color,
    val blackCellsColor: Color,
)

@Composable
fun ChessBoard(
    modifier: Modifier = Modifier,
    parameters: ChessBoardParameters
) {

    Surface(
        modifier = modifier
            .requiredSize(parameters.totalSize),
        color = parameters.backgroundColor
    ) {
        CellsZone(
            parameters = parameters
        )
    }
}

@Composable
fun CellsZone(parameters: ChessBoardParameters) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        for (row in 0 until 8) {
            Row {
                for (col in 0 until 8) {
                    val isWhiteCell = (row + col) % 2 == 0
                    Cell(parameters = parameters, isWhiteCell = isWhiteCell)
                }
            }
        }
    }
}

@Composable
fun Cell(
    parameters: ChessBoardParameters,
    isWhiteCell: Boolean
) {
    val cellSize = parameters.totalSize * 0.111f
    val color = if (isWhiteCell) parameters.whiteCellsColor else parameters.blackCellsColor

    Surface(
        modifier = Modifier.requiredSize(cellSize),
        color = color,
    ) {

    }
}