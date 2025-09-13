package com.classic.tetris

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    
    private lateinit var gameView: GameView
    private lateinit var scoreTextView: TextView
    private lateinit var levelTextView: TextView
    private lateinit var startButton: Button
    private lateinit var pauseButton: Button
    private lateinit var stopButton: Button
    private lateinit var moveLeftButton: Button
    private lateinit var moveRightButton: Button
    private lateinit var rotateButton: Button
    private lateinit var dropButton: Button
    
    private val handler = Handler(Looper.getMainLooper())
    private var lastDropClickTime: Long = 0
    private var lastMoveLeftClickTime: Long = 0
    private var lastMoveRightClickTime: Long = 0
    private val DOUBLE_CLICK_TIME_DELTA: Long = 300 // 双击时间间隔（毫秒）
    private val updateRunnable = object : Runnable {
        override fun run() {
            gameView.update()
            updateUI()
            handler.postDelayed(this, (500 - (gameView.level - 1) * 50L) / 2)
        }
    }
    
    private val fastDropRunnable = object : Runnable {
        override fun run() {
            if (isDropButtonPressed) {
                gameView.moveDown()
                updateUI()
                handler.postDelayed(this, 50) // 加速下坠的时间间隔
            }
        }
    }
    
    private val fastMoveLeftRunnable = object : Runnable {
        override fun run() {
            if (isMoveLeftButtonPressed) {
                gameView.moveLeft()
                updateUI()
                handler.postDelayed(this, 50) // 加速左移的时间间隔
            }
        }
    }
    
    private val fastMoveRightRunnable = object : Runnable {
        override fun run() {
            if (isMoveRightButtonPressed) {
                gameView.moveRight()
                updateUI()
                handler.postDelayed(this, 50) // 加速右移的时间间隔
            }
        }
    }
    private var isDropButtonPressed = false
    private var isMoveLeftButtonPressed = false
    private var isMoveRightButtonPressed = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        initializeViews()
        
        // 初始状态下，除了开始按钮外，其他按钮都禁用
        pauseButton.isEnabled = false
        stopButton.isEnabled = false
        moveLeftButton.isEnabled = false
        moveRightButton.isEnabled = false
        rotateButton.isEnabled = false
        dropButton.isEnabled = false
        
        // 设置按钮点击事件
        setupButtonListeners()
        
        // 初始化UI
        updateUI()
    }
    
    private fun initializeViews() {
        gameView = findViewById(R.id.gameView)
        scoreTextView = findViewById(R.id.scoreTextView)
        levelTextView = findViewById(R.id.levelTextView)
        startButton = findViewById(R.id.startButton)
        pauseButton = findViewById(R.id.pauseButton)
        stopButton = findViewById(R.id.stopButton)
        moveLeftButton = findViewById(R.id.moveLeftButton)
        moveRightButton = findViewById(R.id.moveRightButton)
        rotateButton = findViewById(R.id.rotateButton)
        dropButton = findViewById(R.id.dropButton)
    }
    
    private fun setupButtonListeners() {
        startButton.setOnClickListener {
            gameView.startGame()
            startGameLoop()
            startButton.isEnabled = false
            pauseButton.isEnabled = true
            stopButton.isEnabled = true
            moveLeftButton.isEnabled = true
            moveRightButton.isEnabled = true
            rotateButton.isEnabled = true
            dropButton.isEnabled = true
        }
        
        pauseButton.setOnClickListener {
            if (gameView.isPaused) {
                gameView.resumeGame()
                pauseButton.text = "暂停"
                startGameLoop()
            } else {
                gameView.pauseGame()
                pauseButton.text = "继续"
                stopGameLoop()
            }
        }
        
        stopButton.setOnClickListener {
            gameView.resetGame()
            stopGameLoop()
            gameView.invalidate()
            startButton.isEnabled = true
            pauseButton.isEnabled = false
            stopButton.isEnabled = false
            moveLeftButton.isEnabled = false
            moveRightButton.isEnabled = false
            rotateButton.isEnabled = false
            dropButton.isEnabled = false
            pauseButton.text = "暂停"
            updateUI()
        }
        
        moveLeftButton.setOnClickListener {
            val clickTime = System.currentTimeMillis()
            if (clickTime - lastMoveLeftClickTime < DOUBLE_CLICK_TIME_DELTA) {
                // 双击检测 - 加速向左移动
                while (gameView.getCurrentBlock()?.moveLeft(gameView.getBoard()) == true) {
                    // 持续向左移动直到无法移动
                }
            } else {
                // 单击 - 正常向左移动一格
                gameView.moveLeft()
            }
            lastMoveLeftClickTime = clickTime
            gameView.invalidate()
            updateUI()
        }
        
        moveRightButton.setOnClickListener {
            val clickTime = System.currentTimeMillis()
            if (clickTime - lastMoveRightClickTime < DOUBLE_CLICK_TIME_DELTA) {
                // 双击检测 - 加速向右移动
                while (gameView.getCurrentBlock()?.moveRight(gameView.getBoard()) == true) {
                    // 持续向右移动直到无法移动
                }
            } else {
                // 单击 - 正常向右移动一格
                gameView.moveRight()
            }
            lastMoveRightClickTime = clickTime
            gameView.invalidate()
            updateUI()
        }
        
        rotateButton.setOnClickListener {
            gameView.rotate()
            gameView.invalidate()
            updateUI()
        }
        
        dropButton.setOnClickListener {
            val clickTime = System.currentTimeMillis()
            if (clickTime - lastDropClickTime < DOUBLE_CLICK_TIME_DELTA) {
                // 双击检测 - 加速下降
                gameView.dropToBottom()
            } else {
                // 单击 - 正常下降一格
                gameView.moveDown()
            }
            lastDropClickTime = clickTime
            gameView.invalidate()
            updateUI()
        }
        
        dropButton.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    isDropButtonPressed = true
                    handler.post(fastDropRunnable)
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    isDropButtonPressed = false
                    handler.removeCallbacks(fastDropRunnable)
                }
            }
            false
        }
        
        // 为moveLeftButton添加触摸监听器，实现按住加速左移功能
        moveLeftButton.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    isMoveLeftButtonPressed = true
                    handler.post(fastMoveLeftRunnable)
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    isMoveLeftButtonPressed = false
                    handler.removeCallbacks(fastMoveLeftRunnable)
                }
            }
            false
        }
        
        // 为moveRightButton添加触摸监听器，实现按住加速右移功能
        moveRightButton.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    isMoveRightButtonPressed = true
                    handler.post(fastMoveRightRunnable)
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    isMoveRightButtonPressed = false
                    handler.removeCallbacks(fastMoveRightRunnable)
                }
            }
            false
        }
    }
    
    private fun startGameLoop() {
        stopGameLoop()
        handler.post(updateRunnable)
    }
    
    private fun stopGameLoop() {
        handler.removeCallbacks(updateRunnable)
    }
    
    private fun updateUI() {
        scoreTextView.text = "分数: ${gameView.score}"
        levelTextView.text = "等级: ${gameView.level}"
        
        if (gameView.isGameOver) {
            stopGameLoop()
            startButton.isEnabled = true
            pauseButton.isEnabled = false
        }
    }
    
    override fun onPause() {
        super.onPause()
        gameView.pauseGame()
        stopGameLoop()
        isDropButtonPressed = false
        isMoveLeftButtonPressed = false
        isMoveRightButtonPressed = false
        handler.removeCallbacks(fastDropRunnable)
        handler.removeCallbacks(fastMoveLeftRunnable)
        handler.removeCallbacks(fastMoveRightRunnable)
    }
    
    override fun onResume() {
        super.onResume()
        if (!gameView.isPaused && !gameView.isGameOver) {
            startGameLoop()
        }
    }
}