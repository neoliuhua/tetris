package com.classic.tetris

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import kotlin.random.Random

class GameView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    
    private val board = Array(GameConstants.BOARD_HEIGHT) { IntArray(GameConstants.BOARD_WIDTH) }
    private var currentBlock: Block? = null
    private var nextBlock: Block? = null
    private val blockPaint = Paint().apply {
        style = Paint.Style.FILL
        isAntiAlias = true
    }
    private val gridPaint = Paint().apply {
        style = Paint.Style.STROKE
        color = Color.GRAY
        strokeWidth = 1f
    }
    
    var score = 0
    var level = 1
    var isPaused = false
    var isGameOver = false
    
    private val blockColors = listOf(
        Color.RED,      // I-block
        Color.GREEN,    // J-block
        Color.BLUE,     // L-block
        Color.YELLOW,   // O-block
        Color.MAGENTA,  // S-block
        Color.CYAN,     // T-block
        Color.GRAY      // Z-block
    )
    
    fun startGame() {
        resetGame()
        generateNewBlock()
        invalidate()
    }
    
    fun pauseGame() {
        isPaused = true
    }
    
    fun resumeGame() {
        isPaused = false
    }
    
    fun moveLeft() {
        if (!isPaused && !isGameOver) {
            currentBlock?.moveLeft(board)
            invalidate()
        }
    }
    
    fun moveRight() {
        if (!isPaused && !isGameOver) {
            currentBlock?.moveRight(board)
            invalidate()
        }
    }
    
    fun rotate() {
        if (!isPaused && !isGameOver) {
            currentBlock?.rotate(board)
            invalidate()
        }
    }
    
    fun moveDown() {
        if (!isPaused && !isGameOver) {
            if (currentBlock?.moveDown(board) == false) {
                placeBlock()
                checkLines()
                generateNewBlock()
                if (checkGameOver()) {
                isGameOver = true
            }
            }
            invalidate()
        }
    }
    
    fun dropToBottom() {
        if (!isPaused && !isGameOver) {
            var dropDistance = 0
            while (currentBlock?.moveDown(board) == true) {
                dropDistance++
            }
            
            if (dropDistance > 0) {
                // 额外奖励分数：根据下降距离给分
                score += dropDistance * 2
            }
            
            placeBlock()
            checkLines()
            generateNewBlock()
            if (checkGameOver()) {
                isGameOver = true
            }
            invalidate()
        }
    }
    
    fun update() {
        if (!isPaused && !isGameOver) {
            moveDown()
        }
    }
    
    private fun resetGame() {
        for (y in 0 until GameConstants.BOARD_HEIGHT) {
            for (x in 0 until GameConstants.BOARD_WIDTH) {
                board[y][x] = 0
            }
        }
        score = 0
        level = 1
        isPaused = false
        isGameOver = false
        currentBlock = null
        nextBlock = null
    }
    
    private fun generateNewBlock() {
        currentBlock = nextBlock ?: createRandomBlock()
        nextBlock = createRandomBlock()
        
        if (currentBlock?.collides(board) == true) {
            isGameOver = true
        }
    }
    
    private fun createRandomBlock(): Block {
        val type = BlockType.values()[Random.nextInt(BlockType.values().size)]
        return Block(type, GameConstants.BOARD_WIDTH / 2 - 1, 0)
    }
    
    private fun placeBlock() {
        currentBlock?.let { block ->
            for (y in block.y until block.y + block.shape.size) {
                for (x in block.x until block.x + block.shape[0].size) {
                    if (block.shape[y - block.y][x - block.x] == 1) {
                        board[y][x] = block.type.ordinal + 1
                    }
                }
            }
        }
    }
    
    private fun checkLines() {
        var linesCleared = 0
        var y = GameConstants.BOARD_HEIGHT - 1
        
        while (y >= 0) {
            if (board[y].all { it > 0 }) {
                // Remove the line
                for (yy in y downTo 1) {
                    board[yy] = board[yy - 1].copyOf()
                }
                board[0] = IntArray(GameConstants.BOARD_WIDTH)
                linesCleared++
                // Don't decrement y, check the same line again
            } else {
                y--
            }
        }
        
        if (linesCleared > 0) {
            score += when (linesCleared) {
                1 -> 100 * level
                2 -> 300 * level
                3 -> 500 * level
                4 -> 800 * level
                else -> 0
            }
            
            level = (score / 1000) + 1
        }
    }
    
    private fun checkGameOver(): Boolean {
        return currentBlock?.collides(board) == true
    }
    
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        
        // Draw background
        canvas.drawColor(Color.BLACK)
        
        // Draw grid
        for (y in 0 until GameConstants.BOARD_HEIGHT) {
            for (x in 0 until GameConstants.BOARD_WIDTH) {
                val left = x * GameConstants.BLOCK_SIZE.toFloat()
                val top = y * GameConstants.BLOCK_SIZE.toFloat()
                val right = left + GameConstants.BLOCK_SIZE
                val bottom = top + GameConstants.BLOCK_SIZE
                
                // Draw grid cell
                canvas.drawRect(left, top, right, bottom, gridPaint)
                
                // Draw block if present
                if (board[y][x] > 0) {
                    blockPaint.color = blockColors[board[y][x] - 1]
                    canvas.drawRect(left + 1, top + 1, right - 1, bottom - 1, blockPaint)
                }
            }
        }
        
        // Draw current block
        currentBlock?.let { block ->
            blockPaint.color = blockColors[block.type.ordinal]
            for (y in block.y until block.y + block.shape.size) {
                for (x in block.x until block.x + block.shape[0].size) {
                    if (block.shape[y - block.y][x - block.x] == 1) {
                        val left = x * GameConstants.BLOCK_SIZE.toFloat()
                        val top = y * GameConstants.BLOCK_SIZE.toFloat()
                        val right = left + GameConstants.BLOCK_SIZE
                        val bottom = top + GameConstants.BLOCK_SIZE
                        canvas.drawRect(left + 1, top + 1, right - 1, bottom - 1, blockPaint)
                    }
                }
            }
        }
    }
    
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = GameConstants.BOARD_WIDTH * GameConstants.BLOCK_SIZE
        val height = GameConstants.BOARD_HEIGHT * GameConstants.BLOCK_SIZE
        setMeasuredDimension(width, height)
    }
}