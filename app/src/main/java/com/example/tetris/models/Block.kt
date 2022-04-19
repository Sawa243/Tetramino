package com.example.tetris.models

import android.graphics.Point
import com.example.tetris.constants.FieldConstant
import com.example.tetris.helpers.BlockColor
import kotlin.random.Random

class Block(private val shapeIndex: Int, private val blockColor: BlockColor) {

    var frameNumber = 0
    var position: Point = Point(FieldConstant.COLUMN_COUNT.value / 2, 0)
    var color = blockColor.rgbValue

    companion object {

        fun createBlock(): Block {
            val random = Random
            val shapeIndex = random.nextInt(Shape.values().size)
            val blockColor: BlockColor =
                BlockColor.values()[random.nextInt(BlockColor.values().size)]
            val block = Block(shapeIndex, blockColor)
            block.position.x = block.position.x - Shape.values()[shapeIndex].startPosition
            return block
        }

        fun getColor(value: Byte): Int {
            for (color in BlockColor.values()) {
                if (value == color.value) {
                    return color.rgbValue
                }
            }
            return -1
        }
    }

    fun setState(frame: Int, position: Point) {
        this.frameNumber = frame
        this.position = position
    }

    fun getShape(frameNumber: Int): Array<ByteArray> {
        return Shape.values()[shapeIndex].getFrame(frameNumber).as2dByteArray()
    }

    fun getFrameCount() = Shape.values()[shapeIndex].frameCount

    fun getStaticValue() = blockColor.value
}