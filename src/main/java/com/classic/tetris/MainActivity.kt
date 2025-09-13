package com.classic.tetris

import android.os.Bundle
import android.os.Handler
import android.os.Looper
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        initializeViews()
        setupButtonListeners()
    }
    
    private fun initializeViews() {
        gameView = findViewById(R.id.gameView)
        scoreTextView = findViewById(R.id.scoreTextView)
        levelTextView = findViewById(R.id.levelTextView)
        startButton = findViewById(R.id.startButton)
        pauseButton = findViewById(R.id.pauseButton)
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
        }
        
        pauseButton.setOnClickListener {
            if (gameView.isPaused) {
                gameView.resumeGame()
                pauseButton.text = "继续"
                startGameLoop()
            } else {
                gameView.pauseGame()
                pauseButton.text = "暂停"
                stopGameLoop()
            }
        }
        
        moveLeftButton.setOnClickListener {
            gameView.moveLeft()
        }
        
        moveRightButton.setOnClickListener {
            gameView.moveRight()
        }
        
        rotateButton.setOnClickListener {
            gameView.rotate()
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
    }
    
    override fun onResume() {
        super.onResume()
        if (!gameView.isPaused && !gameView.isGameOver) {
            startGameLoop()
        }
    }
}