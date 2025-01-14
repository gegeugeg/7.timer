package com.example.coursework2

import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Button
import android.widget.NumberPicker
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.AudioManager
import android.widget.ImageButton
import android.widget.RelativeLayout

class MainActivity : AppCompatActivity() {

    private lateinit var startStopButton: Button
    private lateinit var resetButton: Button
    private lateinit var timerTextView: TextView
    private lateinit var hoursPicker: NumberPicker
    private lateinit var minutesPicker: NumberPicker
    private lateinit var secondsPicker: NumberPicker
    private lateinit var mainLayout: RelativeLayout
    private lateinit var volumeButton: ImageButton
    private lateinit var audioManager: AudioManager
    private lateinit var openSecondActivityButton: ImageButton
    private var timer: CountDownTimer? = null
    private var isTimerRunning = false
    private var timeInMillis: Long = 0
    private var currentVolume: Int = 0

    private lateinit var tickPlayer: MediaPlayer
    private lateinit var endTimePlayer: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_layout)

        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        currentVolume=100
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume, 0)

        val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)

        openSecondActivityButton = findViewById(R.id.groupImage)
        startStopButton = findViewById(R.id.startButton)
        resetButton = findViewById(R.id.resetButton)
        timerTextView = findViewById(R.id.timerTextView)
        hoursPicker = findViewById(R.id.hoursPicker)
        minutesPicker = findViewById(R.id.minutesPicker)
        secondsPicker = findViewById(R.id.secondsPicker)
        mainLayout = findViewById(R.id.mainLayout)
        volumeButton = findViewById(R.id.soundImage)

        hoursPicker.minValue = 0
        hoursPicker.maxValue = 23

        minutesPicker.minValue = 0
        minutesPicker.maxValue = 59

        secondsPicker.minValue = 0
        secondsPicker.maxValue = 59

        tickPlayer = MediaPlayer.create(this, R.raw.tick)
        endTimePlayer = MediaPlayer.create(this, R.raw.sish)

        startStopButton.setOnClickListener {
            if (!isTimerRunning) {
                startTimer()
            } else {
                pauseTimer()
            }
        }

        resetButton.setOnClickListener {
            resetTimer()
        }

        volumeButton.setOnClickListener {
            if (currentVolume < maxVolume) {
                currentVolume=100
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume, 0)
            } else {
                currentVolume = 0
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume, 0)
            }
        }

        openSecondActivityButton.setOnClickListener {
            val intent = Intent(this, GroupActivity::class.java)
            startActivity(intent)
        }

        startGradientAnimation()
    }

    private fun startTimer() {
        if (timeInMillis == 0L) {
            timeInMillis = (hoursPicker.value * 3600 + minutesPicker.value * 60 + secondsPicker.value).toLong() * 1000 + 200
        }
        timer = object : CountDownTimer(timeInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeInMillis = millisUntilFinished
                updateTimer()
            }

            override fun onFinish() {
                isTimerRunning = false
                startStopButton.text = "Старт"
                timeInMillis = 0L
                stopTickingSound()
                playEndTimeSound()

            }
        }.start()
        isTimerRunning = true
        startStopButton.text = "Пауза"
        startTickingSound()
    }

    private fun pauseTimer() {
        timer?.cancel()
        isTimerRunning = false
        startStopButton.text = "Продолжить"
        stopTickingSound()
    }

    private fun resetTimer() {
        stopTimer()
        timeInMillis = 0
        timerTextView.text = "00:00:00"
        hoursPicker.value = 0
        minutesPicker.value = 0
        secondsPicker.value = 0
        startStopButton.text = "Старт"
        stopTickingSound()
    }

    private fun stopTimer() {
        timer?.cancel()
        isTimerRunning = false
        startStopButton.text = "Старт"
    }

    private fun updateTimer() {
        val seconds = (timeInMillis / 1000) % 60
        val minutes = (timeInMillis / (1000 * 60)) % 60
        val hours = (timeInMillis / (1000 * 60 * 60)) % 24

        timerTextView.text = String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }

    private fun startGradientAnimation() {
        val colors = intArrayOf(Color.parseColor("#3786c8"), Color.parseColor("#20df98"))
        val animator = ValueAnimator.ofFloat(0f, 1f)
        animator.duration = 15000
        animator.repeatCount = ValueAnimator.INFINITE
        animator.repeatMode = ValueAnimator.REVERSE

        animator.addUpdateListener { animation ->
            val fraction = animation.animatedValue as Float
            val color = ArgbEvaluator().evaluate(fraction, colors[0], colors[1]) as Int
            mainLayout.setBackgroundColor(color)
        }

        animator.start()
    }

    private fun startTickingSound() {
        tickPlayer.isLooping = true
        tickPlayer.start()
    }

    private fun stopTickingSound() {
        if (tickPlayer.isPlaying) {
            tickPlayer.pause()
        }
    }


    private fun playEndTimeSound() {
        endTimePlayer.start()
    }

    override fun onPause() {
        super.onPause()
        stopTickingSound()
    }

    override fun onResume() {
        super.onResume()
        if (isTimerRunning) {
            startTickingSound()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        tickPlayer.release()
        endTimePlayer.release()
    }
}
