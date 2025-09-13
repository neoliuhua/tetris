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
    private val ghostBlockPaint = Paint().apply {
        style = Paint.Style.FILL
        isAntiAlias = true
        alpha = 150 // 设置半透明效果为90
    }
    private val ghostLinePaint = Paint().apply {
        style = Paint.Style.STROKE
        isAntiAlias = true
        strokeWidth = 2f
        alpha = 150 // 设置半透明效果为90
        // 设置虚线效果
        pathEffect = android.graphics.DashPathEffect(floatArrayOf(5f, 5f), 0f)
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
    
    fun resetGame() {
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
    
    private fun getGhostBlockPosition(): Block? {
        currentBlock?.let { block ->
            // 创建一个临时的方块副本用于计算投影位置
            val ghostBlock = Block(block.type, block.x, block.y, block.shape)
            
            // 不断向下移动，直到碰撞
            while (ghostBlock.moveDown(board)) {
                // 继续下落
            }
            
            return ghostBlock
        }
        return null
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
        
        // Draw ghost block (projection)
        getGhostBlockPosition()?.let { ghostBlock ->
            ghostBlockPaint.color = blockColors[ghostBlock.type.ordinal]
            for (y in ghostBlock.y until ghostBlock.y + ghostBlock.shape.size) {
                for (x in ghostBlock.x until ghostBlock.x + ghostBlock.shape[0].size) {
                    if (ghostBlock.shape[y - ghostBlock.y][x - ghostBlock.x] == 1) {
                        val left = x * GameConstants.BLOCK_SIZE.toFloat()
                        val top = y * GameConstants.BLOCK_SIZE.toFloat()
                        val right = left + GameConstants.BLOCK_SIZE
                        val bottom = top + GameConstants.BLOCK_SIZE
                        canvas.drawRect(left + 1, top + 1, right - 1, bottom - 1, ghostBlockPaint)
                    }
                }
            }
            
            // Draw connecting lines between current block and ghost block
            currentBlock?.let { block ->
                ghostLinePaint.color = blockColors[block.type.ordinal]
                
                // 收集所有需要绘制的连线
                val linesToDraw = mutableListOf<Pair<Float, Float>>()
                
                // 为当前方块的每个部分和投影方块的对应部分之间计算虚线
                for (blockY in block.y until block.y + block.shape.size) {
                    for (blockX in block.x until block.x + block.shape[0].size) {
                        if (block.shape[blockY - block.y][blockX - block.x] == 1) {
                            // 计算当前方块部分的中心点
                            val blockCenterX = blockX * GameConstants.BLOCK_SIZE + GameConstants.BLOCK_SIZE / 2
                            val blockCenterY = blockY * GameConstants.BLOCK_SIZE + GameConstants.BLOCK_SIZE / 2
                            
                            // 找到投影方块中对应的部分
                            for (ghostY in ghostBlock.y until ghostBlock.y + ghostBlock.shape.size) {
                                for (ghostX in ghostBlock.x until ghostBlock.x + ghostBlock.shape[0].size) {
                                    if (ghostBlock.shape[ghostY - ghostBlock.y][ghostX - ghostBlock.x] == 1 &&
                                        ghostX - ghostBlock.x == blockX - block.x &&
                                        ghostY - ghostBlock.y == blockY - block.y) {
                                        // 计算投影方块部分的中心点
                                        val ghostCenterX = ghostX * GameConstants.BLOCK_SIZE + GameConstants.BLOCK_SIZE / 2
                                        val ghostCenterY = ghostY * GameConstants.BLOCK_SIZE + GameConstants.BLOCK_SIZE / 2
                                        
                                        // 添加到连线列表
                                        linesToDraw.add(Pair(blockCenterX.toFloat(), ghostCenterX.toFloat()))
                                    }
                                }
                            }
                        }
                    }
                }
                
                // 如果有超过2条连线，只保留最左边和最右边的连线
                if (linesToDraw.size > 2) {
                    // 按X坐标排序
                    linesToDraw.sortBy { it.first }
                    // 只保留第一条和最后一条
                    val filteredLines = listOf(linesToDraw.first(), linesToDraw.last())
                    
                    // 绘制筛选后的连线
                    for (line in filteredLines) {
                        // 重新计算Y坐标
                        val blockCenterY = block.y * GameConstants.BLOCK_SIZE + GameConstants.BLOCK_SIZE / 2
                        val ghostCenterY = ghostBlock.y * GameConstants.BLOCK_SIZE + GameConstants.BLOCK_SIZE / 2
                        
                        canvas.drawLine(
                            line.first, 
                            blockCenterY.toFloat(),
                            line.second, 
                            ghostCenterY.toFloat(),
                            ghostLinePaint
                        )
                    }
                } else {
                    // 如果连线数量不超过2条，绘制所有连线
                    for (line in linesToDraw) {
                        // 重新计算Y坐标
                        val blockCenterY = block.y * GameConstants.BLOCK_SIZE + GameConstants.BLOCK_SIZE / 2
                        val ghostCenterY = ghostBlock.y * GameConstants.BLOCK_SIZE + GameConstants.BLOCK_SIZE / 2
                        
                        canvas.drawLine(
                            line.first, 
                            blockCenterY.toFloat(),
                            line.second, 
                            ghostCenterY.toFloat(),
                            ghostLinePaint
                        )
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