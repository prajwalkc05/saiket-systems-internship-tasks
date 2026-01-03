package com.example.simpletimerapp;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    // UI Components
    private TextView tvTimer;
    private MaterialButton btnStartPause;
    private MaterialButton btnReset;

    // Logic Variables
    private Handler handler;
    private long startTime = 0L;
    private long timeInMilliseconds = 0L;
    private long timeSwapBuff = 0L;
    private long updatedTime = 0L;

    // State Management
    private enum TimerState {
        STOPPED,
        RUNNING,
        PAUSED
    }
    private TimerState currentState = TimerState.STOPPED;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Views
        tvTimer = findViewById(R.id.tvTimer);
        btnStartPause = findViewById(R.id.btnStartPause);
        btnReset = findViewById(R.id.btnReset);

        // Fix: Use Looper.getMainLooper() to avoid deprecation warning
        handler = new Handler(Looper.getMainLooper());

        // Restore state if rotated
        if (savedInstanceState != null) {
            // Suppress deprecation for getSerializable as we support minSdk 21
            @SuppressWarnings("deprecation")
            TimerState state = (TimerState) savedInstanceState.getSerializable("state");
            currentState = state;
            
            startTime = savedInstanceState.getLong("startTime");
            timeSwapBuff = savedInstanceState.getLong("timeSwapBuff");
            updatedTime = savedInstanceState.getLong("updatedTime");

            // Restore UI immediately
            updateTimerText(updatedTime);
            updateButtons();

            // If it was running, restart the handler
            if (currentState == TimerState.RUNNING) {
                // Adjust startTime so the timer continues smoothly from where it left off
                startTime = SystemClock.uptimeMillis() - (updatedTime - timeSwapBuff);
                handler.postDelayed(updateTimerThread, 0);
            }
        }

        // Start/Pause Button Logic
        btnStartPause.setOnClickListener(v -> {
            if (currentState == TimerState.STOPPED || currentState == TimerState.PAUSED) {
                startTimer();
            } else {
                pauseTimer();
            }
        });

        // Reset Button Logic
        btnReset.setOnClickListener(v -> resetTimer());
        
        // Initial Button State
        updateButtons();
    }

    // --- Core Timer Logic ---

    private Runnable updateTimerThread = new Runnable() {
        public void run() {
            timeInMilliseconds = SystemClock.uptimeMillis() - startTime;
            updatedTime = timeSwapBuff + timeInMilliseconds;

            updateTimerText(updatedTime);

            // Re-run this runnable in 30ms (approx 30fps) for smooth updates
            handler.postDelayed(this, 30);
        }
    };

    private void startTimer() {
        startTime = SystemClock.uptimeMillis();
        handler.postDelayed(updateTimerThread, 0);
        currentState = TimerState.RUNNING;
        updateButtons();
    }

    private void pauseTimer() {
        timeSwapBuff += timeInMilliseconds;
        handler.removeCallbacks(updateTimerThread);
        currentState = TimerState.PAUSED;
        updateButtons();
    }

    private void resetTimer() {
        startTime = 0L;
        timeInMilliseconds = 0L;
        timeSwapBuff = 0L;
        updatedTime = 0L;
        
        handler.removeCallbacks(updateTimerThread);
        currentState = TimerState.STOPPED;
        
        updateTimerText(0);
        updateButtons();
    }

    // --- Helper Methods ---

    private void updateTimerText(long timeInMillis) {
        int secs = (int) (timeInMillis / 1000);
        int mins = secs / 60;
        secs = secs % 60;
        int milliseconds = (int) (timeInMillis % 1000) / 10; // Show only 2 digits

        String strTime = String.format(Locale.getDefault(), "%02d:%02d:%02d", mins, secs, milliseconds);
        tvTimer.setText(strTime);
    }

    private void updateButtons() {
        switch (currentState) {
            case STOPPED:
                btnStartPause.setText("Start");
                btnStartPause.setIconResource(android.R.drawable.ic_media_play);
                btnReset.setEnabled(false); // Can't reset if already at 0
                break;
            case RUNNING:
                btnStartPause.setText("Pause");
                btnStartPause.setIconResource(android.R.drawable.ic_media_pause);
                btnReset.setEnabled(true);
                break;
            case PAUSED:
                btnStartPause.setText("Resume");
                btnStartPause.setIconResource(android.R.drawable.ic_media_play);
                btnReset.setEnabled(true);
                break;
        }
    }

    // --- Persistence (Rotation Support) ---

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("state", currentState);
        outState.putLong("startTime", startTime);
        outState.putLong("timeSwapBuff", timeSwapBuff);
        outState.putLong("updatedTime", updatedTime);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Prevent memory leaks by stopping the handler when activity is destroyed
        if (isFinishing()) {
            handler.removeCallbacks(updateTimerThread);
        }
    }
}