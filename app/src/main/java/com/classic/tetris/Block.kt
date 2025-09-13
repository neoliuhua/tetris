package com.classic.tetris

object GameConstants {
    const val BOARD_WIDTH = 20  // 宽度增加一倍
    const val BOARD_HEIGHT = 40  // 高度增加一倍
    const val BLOCK_SIZE = 38  // 保持当前大小
    const val INITIAL_SPEED = 500L
}

enum class BlockType {
    I, J, L, O, S, T, Z
}

class Block(val type: BlockType, var x: Int, var y: Int) {
    
    val shape: Array<IntArray> = when (type) {
        BlockType.I -> arrayOf(
            intArrayOf(0, 0, 0, 0),
            intArrayOf(1, 1, 1, 1),
            intArrayOf(0, 0, 0, 0),
            intArrayOf(0, 0, 0, 0)
        )
        BlockType.J -> arrayOf(
            intArrayOf(1, 0, 0),
            intArrayOf(1, 1, 1),
            intArrayOf(0, 0, 0)
        )
        BlockType.L -> arrayOf(
            intArrayOf(0, 0, 1),
            intArrayOf(1, 1, 1),
            intArrayOf(0, 0, 0)
        )
        BlockType.O -> arrayOf(
            intArrayOf(1, 1),
            intArrayOf(1, 1)
        )
        BlockType.S -> arrayOf(
            intArrayOf(0, 1, 1),
            intArrayOf(1, 1, 0),
            intArrayOf(0, 0, 0)
        )
        BlockType.T -> arrayOf(
            intArrayOf(0, 1, 0),
            intArrayOf(1, 1, 1),
            intArrayOf(0, 0, 0)
        )
        BlockType.Z -> arrayOf(
            intArrayOf(1, 1, 0),
            intArrayOf(0, 1, 1),
            intArrayOf(0, 0, 0)
        )
    }
    
    fun moveLeft(board: Array<IntArray>): Boolean {
        x--
        if (collides(board)) {
            x++
            return false
        }
        return true
    }
    
    fun moveRight(board: Array<IntArray>): Boolean {
        x++
        if (collides(board)) {
            x--
            return false
        }
        return true
    }
    
    fun moveDown(board: Array<IntArray>): Boolean {
        y++
        if (collides(board)) {
            y--
            return false
        }
        return true
    }
    
    fun rotate(board: Array<IntArray>): Boolean {
        val oldShape = shape.map { it.copyOf() }.toTypedArray()
        rotateShape()
        
        if (collides(board)) {
            // Restore original shape if rotation causes collision
            for (i in shape.indices) {
                for (j in shape[i].indices) {
                    shape[i][j] = oldShape[i][j]
                }
            }
            return false
        }
        return true
    }
    
    private fun rotateShape() {
        val n = shape.size
        val m = shape[0].size
        
        // Transpose the matrix
        for (i in 0 until n) {
            for (j in i until m) {
                val temp = shape[i][j]
                shape[i][j] = shape[j][i]
                shape[j][i] = temp
            }
        }
        
        // Reverse each row
        for (i in 0 until n) {
            for (j in 0 until m / 2) {
                val temp = shape[i][j]
                shape[i][j] = shape[i][m - 1 - j]
                shape[i][m - 1 - j] = temp
            }
        }
    }
    
    fun collides(board: Array<IntArray>): Boolean {
        for (i in shape.indices) {
            for (j in shape[i].indices) {
                if (shape[i][j] == 1) {
                    val boardX = x + j
                    val boardY = y + i
                    
                    // Check if out of bounds or collides with existing block
                    if (boardX < 0 || boardX >= GameConstants.BOARD_WIDTH || 
                        boardY >= GameConstants.BOARD_HEIGHT || 
                        (boardY >= 0 && board[boardY][boardX] > 0)) {
                        return true
                    }
                }
            }
        }
        return false
    }
}