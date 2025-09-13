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
    private val DOUBLE_CLICK_TIME_DELTA: Long = 300 // 双击时间间隔（毫秒）
    private val updateRunnable = object : Runnable {
        override fun run() {
            gameView.update()
            updateUI()
            handler.postDelayed(this, 500 - (gameView.level - 1) * 50L)
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
    private var isDropButtonPressed = false

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
            gameView.moveLeft()
            gameView.invalidate()
            updateUI()
        }
        
        moveRightButton.setOnClickListener {
            gameView.moveRight()
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
        
        // 为dropButton添加触摸监听器，实现按住加速下坠功能
        dropButton.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                when (event?.action) {
                    MotionEvent.ACTION_DOWN -> {
                        // 按下按钮时，开始加速下坠
                        isDropButtonPressed = true
                        handler.post(fastDropRunnable)
                    }
                    MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                        // 释放按钮时，停止加速下坠
                        isDropButtonPressed = false
                        handler.removeCallbacks(fastDropRunnable)
                        // 确保点击事件仍然触发
                        v?.performClick()
                    }
                }
                return true
            }
        })
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
        handler.removeCallbacks(fastDropRunnable)
    }
    
    override fun onResume() {
        super.onResume()
        if (!gameView.isPaused && !gameView.isGameOver) {
            startGameLoop()
        }
    }
}