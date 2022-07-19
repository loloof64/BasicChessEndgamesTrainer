package com.loloof64.android.chessboard.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.github.bhlangonijr.chesslib.Board
import com.github.bhlangonijr.chesslib.Constants
import com.loloof64.android.chessboard.R

private fun newBoardFromFen(positionFen: String): Board {
    val board = Board()
    board.loadFromFen(positionFen)
    return board
}

@Composable
private fun drawableFromPieceFen(pieceFen: Char): ImageVector? =
    when (pieceFen) {
        'P' -> ImageVector.vectorResource(id = R.drawable.ic_chess_pl)
        'N' -> ImageVector.vectorResource(id = R.drawable.ic_chess_nl)
        'B' -> ImageVector.vectorResource(id = R.drawable.ic_chess_bl)
        'R' -> ImageVector.vectorResource(id = R.drawable.ic_chess_rl)
        'Q' -> ImageVector.vectorResource(id = R.drawable.ic_chess_ql)
        'K' -> ImageVector.vectorResource(id = R.drawable.ic_chess_kl)
        'p' -> ImageVector.vectorResource(id = R.drawable.ic_chess_pd)
        'n' -> ImageVector.vectorResource(id = R.drawable.ic_chess_nd)
        'b' -> ImageVector.vectorResource(id = R.drawable.ic_chess_bd)
        'r' -> ImageVector.vectorResource(id = R.drawable.ic_chess_rd)
        'q' -> ImageVector.vectorResource(id = R.drawable.ic_chess_qd)
        'k' -> ImageVector.vectorResource(id = R.drawable.ic_chess_kd)
        else -> null
    }

@Composable
private fun contentDescriptionFor(pieceFen: Char): String =
    stringResource(
        id = when (pieceFen) {
            'P' -> R.string.white_pawn
            'N' -> R.string.white_knight
            'B' -> R.string.white_bishop
            'R' -> R.string.white_rook
            'Q' -> R.string.white_queen
            'K' -> R.string.white_king
            'p' -> R.string.black_pawn
            'n' -> R.string.black_knight
            'b' -> R.string.black_bishop
            'r' -> R.string.black_rook
            'q' -> R.string.black_queen
            'k' -> R.string.black_king
            else -> R.string.unknown_piece
        }
    )

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

class ChessBoardState(currentPositionFen: String, oldPositions: List<String> = listOf()) {
    private var innerBoard by mutableStateOf(newBoardFromFen(currentPositionFen))
    private val playedPositions = oldPositions.toMutableList()

    // TODO add method playMove which will also update played positions

    /**
     * Gets the values of all cells, following board part of Forsyth-Edwards Notation.
     * Each line in the array is matching a rank.
     * The first line is rank 8, and the first column is A.
     */
    fun cellsValues(): Array<Array<Char>> {
        val values = MutableList(8) {
            MutableList(8) {
                0.toChar()
            }
        }

        val boardLines = innerBoard.fen.split(" ")[0].split("/")

        for ((lineCounter, line) in boardLines.withIndex()) {
            for ((colCounter, elem) in line.toCharArray().withIndex()) {
                if (elem.isLetter()) {
                    values[lineCounter][colCounter] = elem
                }
            }
        }

        return values.map { it.toTypedArray() }.toTypedArray()
    }

    companion object {
        val Saver: Saver<ChessBoardState, List<String>> = Saver(
            save = { listOf(it.innerBoard.fen, *(it.playedPositions.toTypedArray())) },
            restore = {
                val currentPositionFen = it[0]
                val oldPositions = it.toMutableList()
                oldPositions.removeFirst()
                ChessBoardState(
                    currentPositionFen = currentPositionFen,
                    oldPositions = oldPositions
                )
            }
        )
    }
}

@Composable
fun rememberChessBoardState(initialPositionFen: String): ChessBoardState =
    rememberSaveable(saver = ChessBoardState.Saver) {
        ChessBoardState(initialPositionFen)
    }

@Composable
fun ChessBoard(
    modifier: Modifier = Modifier,
    parameters: ChessBoardParameters
) {
    val chessBoardState =
        rememberChessBoardState(initialPositionFen = Constants.startStandardFENPosition)

    Surface(
        modifier = modifier
            .requiredSize(parameters.totalSize),
        color = parameters.backgroundColor
    ) {
        CellsZone(
            parameters = parameters,
            cellsValues = chessBoardState.cellsValues(),
        )
    }
}

@Composable
fun CellsZone(
    parameters: ChessBoardParameters,
    cellsValues: Array<Array<Char>>,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        for (row in 0 until 8) {
            Row {
                for (col in 0 until 8) {
                    val isWhiteCell = (row + col) % 2 == 0
                    Cell(
                        parameters = parameters,
                        isWhiteCell = isWhiteCell,
                        pieceFen = cellsValues[row][col]
                    )
                }
            }
        }
    }
}

@Composable
fun Cell(
    parameters: ChessBoardParameters,
    isWhiteCell: Boolean,
    pieceFen: Char
) {
    val cellSize = parameters.totalSize * 0.111f
    val color = if (isWhiteCell) parameters.whiteCellsColor else parameters.blackCellsColor

    Surface(
        modifier = Modifier.requiredSize(cellSize),
        color = color,
    ) {
        val vector = drawableFromPieceFen(pieceFen = pieceFen)
        val contentDescription = contentDescriptionFor(pieceFen = pieceFen)
        if (vector != null) {
            Image(imageVector = vector, contentDescription = contentDescription)
        }
    }
}