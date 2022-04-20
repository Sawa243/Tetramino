package com.example.tetris.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.os.Handler
import android.os.Message
import android.util.AttributeSet
import android.view.View
import com.example.tetris.constants.CellConstants
import com.example.tetris.constants.FieldConstant
import com.example.tetris.helpers.Dimension
import com.example.tetris.helpers.Motions
import com.example.tetris.helpers.Statuses
import com.example.tetris.models.AppModel
import com.example.tetris.models.Block

class TetrisView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint()
    private var lastMove: Long = 0
    private var model: AppModel? = null
    private var callbackScore: (currentScore: String) -> Unit = {}
    private val viewHandler = ViewHandler(this)
    private var cellSize: Dimension = Dimension(0, 0)
    private var frameoffset: Dimension = Dimension(0, 0)

    fun setModel(model: AppModel) {
        this.model = model
    }

    fun setCallbackForScore(callback: (currentScore: String) -> Unit) {
        callbackScore = callback
    }

    fun setGameCommand(move: Motions) {
        if (null != model && (model?.currentState ==
                    Statuses.ACTIVE.name)
        ) {
            if (Motions.DOWN == move) {
                model?.generateField(move.name)
                invalidate()
                return
            }
            setGameCommandWithDelay(move)
        }
    }

    fun setGameCommandWithDelay(move: Motions) {
        val now = System.currentTimeMillis()
        if (now - lastMove > DELAY) {
            model?.generateField(move.name)
            invalidate()
            lastMove = now
        }
        updateScore()

        viewHandler.sleep(DELAY.toLong())
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawFrame(canvas)
        if (model != null) {
            for (i in 0 until FieldConstant.ROW_COUNT.value) {
                for (j in 0 until FieldConstant.COLUMN_COUNT.value) {
                    drawCell(canvas, i, j)
                }
            }
        }
    }

    private fun drawFrame(canvas: Canvas) {
        paint.color = Color.LTGRAY
        canvas.drawRect(
            frameoffset.width.toFloat(),
            frameoffset.height.toFloat(),
            width - frameoffset.width.toFloat(),
            height - frameoffset.height.toFloat(), paint
        )
    }

    private fun drawCell(canvas: Canvas, row: Int, col: Int) {
        val cellStatus = model?.getCellStatus(row, col)
        if (CellConstants.EMPTY.value != cellStatus) {
            val color = if (CellConstants.EPHEMERAL.value == cellStatus) {
                model?.currentBlock?.color
            } else {
                Block.getColor(cellStatus as Byte)
            }
            drawCell(canvas, col, row, color as Int)
        }
    }

    private fun drawCell(canvas: Canvas, x: Int, y: Int, rgbColor: Int) {
        paint.color = rgbColor
        val top: Float = (frameoffset.height + y * cellSize.height + BLOCK_OFFSET).toFloat()
        val left: Float = (frameoffset.width + x * cellSize.width + BLOCK_OFFSET).toFloat()
        val bottom: Float =
            (frameoffset.height + (y + 1) * cellSize.height - BLOCK_OFFSET).toFloat()
        val right: Float = (frameoffset.width + (x + 1) * cellSize.width - BLOCK_OFFSET).toFloat()
        val rectangle = RectF(left, top, right, bottom)
        canvas.drawRoundRect(rectangle, 4F, 4F, paint)
    }

    override fun onSizeChanged(width: Int, height: Int, previousWidth: Int, preiviousHeight: Int) {
        super.onSizeChanged(width, height, previousWidth, preiviousHeight)
        val cellWidth = (width - 2 * FRAME_OFFSET_BASE) / FieldConstant.COLUMN_COUNT.value
        val cellHeight = (height - 2 * FRAME_OFFSET_BASE) / FieldConstant.ROW_COUNT.value
        val n = cellWidth.coerceAtMost(cellHeight)
        this.cellSize = Dimension(n, n)
        val offsetX = (width - FieldConstant.COLUMN_COUNT.value * n) / 2
        val offsetY = (height - FieldConstant.ROW_COUNT.value * n) / 2
        this.frameoffset = Dimension(offsetX, offsetY)
    }

    private fun updateScore() {
        callbackScore.invoke(model?.score.toString())
    }

    companion object {
        private const val DELAY = 500
        private const val BLOCK_OFFSET = 2
        private const val FRAME_OFFSET_BASE = 10
    }

    private class ViewHandler(private val owner: TetrisView) : Handler() {
        override fun handleMessage(message: Message) {
            if (message.what == 0) {
                if (owner.model != null) {
                    if (owner.model!!.isGameOver()) {
                        owner.model?.endGame()
                    }
                    if (owner.model!!.isGameActive()) {
                        owner.setGameCommandWithDelay(Motions.DOWN)
                    }
                }
            }
        }

        fun sleep(delay: Long) {
            this.removeMessages(0)
            sendMessageDelayed(obtainMessage(0), delay)
        }
    }

}