package com.example.tetris.helpers

import android.graphics.Color

enum class BlockColor(val rgbValue: Int, val value: Byte) {
    PINK(Color.rgb(255, 105, 180), 2),
    GREEN(Color.rgb(0, 128, 0), 3),
    ORANGE(Color.rgb(255, 140, 0), 4),
    YELLOW(Color.rgb(255, 255, 0), 5),
    CYAN(Color.rgb(0, 255, 255), 6),
}