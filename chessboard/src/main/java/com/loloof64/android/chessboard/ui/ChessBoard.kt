package com.loloof64.android.chessboard.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.github.bhlangonijr.chesslib.*
import com.github.bhlangonijr.chesslib.move.Move
import com.loloof64.android.chessboard.R
import java.util.*

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
    var blackCellsColor = Color(0xFFE65100)

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

    /**
     * Gets the values of all cells, following board part of Forsyth-Edwards Notation.
     * Each line in the array is matching a rank.
     * The first line is rank 8, and the first column is A.
     */
    var cellsValues by mutableStateOf(computeCellsValues())

    var whiteTurn by mutableStateOf(isWhiteTurn())

    // TODO add method playMove which will also update played positions

    private fun computeCellsValues(): Array<Array<Char>> {
        val values = MutableList(8) {
            MutableList(8) {
                0.toChar()
            }
        }

        val boardLines = innerBoard.fen.split(" ")[0].split("/")

        for ((lineCounter, line) in boardLines.withIndex()) {
            var colCounter = 0
            for (elem in line.toCharArray()) {
                if (elem.isLetter()) {
                    values[lineCounter][colCounter] = elem
                    colCounter++
                } else {
                    val holes = elem.digitToInt()
                    colCounter += holes
                }
            }
        }

        return values.map { it.toTypedArray() }.toTypedArray()
    }

    private fun isWhiteTurn() = innerBoard.fen.split(" ")[1] == "w"

    fun doMove(move: Move): Boolean {
        val result = innerBoard.doMove(move, true)
        /////////////////////////
        println(result)
        ////////////////////////
        update()
        return result
    }

    private fun update() {
        cellsValues = computeCellsValues()
        whiteTurn = isWhiteTurn()
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
    initialPositionFen: String = Constants.startStandardFENPosition,
    parameters: ChessBoardParameters
) {
    val chessBoardState =
        rememberChessBoardState(initialPositionFen = initialPositionFen)
    val innerZoneOffset = parameters.totalSize * 0.0555f
    val playerTurnOffset = parameters.totalSize * 0.4675f
    val cornerSize = parameters.totalSize * 0.0555f

    Surface(
        modifier = modifier
            .requiredSize(parameters.totalSize),
        color = parameters.backgroundColor
    ) {
        InnerZone(innerZoneOffset, parameters, chessBoardState)
        PlayerTurn(
            isWhiteTurn = chessBoardState.whiteTurn,
            modifier = Modifier
                .offset(playerTurnOffset, playerTurnOffset)
                .requiredSize(cornerSize),
            cornerSize = cornerSize,
        )
    }
}

@Composable
private fun InnerZone(
    offset: Dp,
    chessBoardParameters: ChessBoardParameters,
    chessBoardState: ChessBoardState
) {
    Box(
        modifier = Modifier
            .offset(offset, offset)
    ) {
        CellsZone(
            parameters = chessBoardParameters,
        )
        PiecesZone(
            parameters = chessBoardParameters,
            piecesValues = chessBoardState.cellsValues,
            isWhiteTurn = chessBoardState.whiteTurn,
            validateMove = {
                return@PiecesZone chessBoardState.doMove(it)
            }
        )
    }
}

@Composable
fun PlayerTurn(
    modifier: Modifier = Modifier,
    isWhiteTurn: Boolean,
    cornerSize: Dp,
) {
    Surface(
        modifier = modifier,
        color = if (isWhiteTurn) Color.White else Color.Black,
        shape = RoundedCornerShape(cornerSize)
    ) {

    }
}

@Composable
fun CellsZone(
    parameters: ChessBoardParameters,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        for (row in 0 until 8) {
            Row {
                for (col in 0 until 8) {
                    val isWhiteCell = (row + col) % 2 == 0
                    Cell(
                        parameters = parameters,
                        isWhiteCell = isWhiteCell
                    )
                }
            }
        }
    }
}

@Composable
fun Cell(
    modifier: Modifier = Modifier,
    parameters: ChessBoardParameters,
    isWhiteCell: Boolean,
) {
    val cellSize = parameters.totalSize * 0.111f
    val color = if (isWhiteCell) parameters.whiteCellsColor else parameters.blackCellsColor

    Surface(
        modifier = modifier.requiredSize(cellSize),
        color = color,
    ) {

    }
}

@Composable
fun PiecesZone(
    modifier: Modifier = Modifier,
    parameters: ChessBoardParameters,
    piecesValues: Array<Array<Char>>,
    isWhiteTurn: Boolean,
    validateMove: (Move) -> Boolean = { _ -> false },
) {
    val totalSize = parameters.totalSize * 0.8888f
    val cellSize = parameters.totalSize * 0.111f

    val cellSizePx = with(LocalDensity.current) {
        cellSize.toPx()
    }

    var dragLocationX by remember {
        mutableStateOf(0f)
    }
    var dragLocationY by remember {
        mutableStateOf(0f)
    }
    var draggedPieceCol by remember {
        mutableStateOf<Int?>(null)
    }
    var draggedPieceRow by remember {
        mutableStateOf<Int?>(null)
    }
    var dragStarted by remember {
        mutableStateOf(false)
    }

    fun handleDragStart(offset: Offset) {
        val col = (offset.x / cellSizePx).toInt()
        val row = (offset.y / cellSizePx).toInt()

        val pieceAtCell = piecesValues[row][col]
        val isNotEmptyCell = pieceAtCell.code > 0
        val isOurPiece = pieceAtCell.isWhitePiece() == isWhiteTurn

        if (isNotEmptyCell && isOurPiece) {
            draggedPieceCol = col
            draggedPieceRow = row
            dragLocationX = offset.x
            dragLocationY = offset.y
            dragStarted = true
        }
    }

    fun handleDrag(change: PointerInputChange, dragAmount: Offset) {
        if (!dragStarted) return
        dragLocationX += dragAmount.x
        dragLocationY += dragAmount.y
        change.consumeAllChanges()
    }

    fun handleDragEnd() {
        if (dragStarted) {
            val draggedPieceEndCol = (dragLocationX / cellSizePx).toInt()
            val draggedPieceEndRow = (dragLocationY / cellSizePx).toInt()

            val inRange = draggedPieceEndCol in 0..7 && draggedPieceEndRow in 0..7
            if (inRange) {
                val startFile = draggedPieceCol!!
                val startRank = 7 - draggedPieceRow!!
                val endFile = draggedPieceEndCol
                val endRank = 7 - draggedPieceEndRow

                val startSquare = Square.encode(Rank.values()[startRank], File.values()[startFile])
                val endSquare = Square.encode(Rank.values()[endRank], File.values()[endFile])
                val move = Move(startSquare, endSquare)
                validateMove(move)
            }
        }
        dragLocationX = 0f
        dragLocationY = 0f
        draggedPieceCol = null
        draggedPieceRow = null
        dragStarted = false
    }

    fun handleDragCancel() {
        dragLocationX = 0f
        dragLocationY = 0f
        draggedPieceCol = null
        draggedPieceRow = null
        dragStarted = false
    }

    Box(
        modifier = modifier
            .requiredSize(totalSize)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = ::handleDragStart,
                    onDragEnd = ::handleDragEnd,
                    onDragCancel = ::handleDragCancel,
                    onDrag = ::handleDrag
                )
            }
    ) {
        for ((row, line) in piecesValues.withIndex()) {
            for ((col, pieceFen) in line.withIndex()) {
                val x = cellSize * col
                val y = cellSize * row

                val xPx = with(LocalDensity.current) {
                    x.toPx()
                }
                val yPx = with(LocalDensity.current) {
                    y.toPx()
                }

                val isDraggedPiece = (col == draggedPieceCol) && (row == draggedPieceRow)
                val location =
                    if (isDraggedPiece) Offset(dragLocationX, dragLocationY) else Offset(xPx, yPx)

                SinglePiece(
                    modifier = Modifier.size(cellSize),
                    value = pieceFen,
                    location = location,
                )
            }
        }
    }
}

private fun Char.isWhitePiece(): Boolean =
    when (this) {
        'P', 'N', 'B', 'R', 'Q', 'K' -> true
        'p', 'n', 'b', 'r', 'q', 'k' -> false
        else -> false
    }

@Composable
fun SinglePiece(
    modifier: Modifier = Modifier,
    value: Char,
    location: Offset,
) {
    val vector = drawableFromPieceFen(pieceFen = value)
    val contentDescription = contentDescriptionFor(pieceFen = value)

    if (vector != null) {
        Image(imageVector = vector, contentDescription = contentDescription,
            modifier = modifier
                .offset {
                    IntOffset(
                        location.x.toInt(),
                        location.y.toInt(),
                    )
                }
        )
    }
}