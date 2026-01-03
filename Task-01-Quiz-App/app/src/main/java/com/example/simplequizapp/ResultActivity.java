package com.example.simplequizapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class ResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        int score = getIntent().getIntExtra("SCORE", 0);
        int total = getIntent().getIntExtra("TOTAL", 0);

        TextView tvScore = findViewById(R.id.tvScore);
        tvScore.setText(score + " / " + total);
        
        TextView tvFeedback = findViewById(R.id.tvFeedback);
        ImageView ivResult = findViewById(R.id.ivResult);
        
        // Determine feedback based on percentage
        int percentage = (total > 0) ? (score * 100 / total) : 0;
        
        if (percentage >= 80) {
            tvFeedback.setText("Excellent Work!");
            tvFeedback.setTextColor(getColor(R.color.correct));
            ivResult.setColorFilter(getColor(R.color.correct));
        } else if (percentage >= 50) {
            tvFeedback.setText("Good Job!");
            tvFeedback.setTextColor(getColor(R.color.timer_warning)); // Orange-ish
            ivResult.setColorFilter(getColor(R.color.timer_warning));
        } else {
            tvFeedback.setText("Keep Practicing!");
            tvFeedback.setTextColor(getColor(R.color.wrong));
            ivResult.setColorFilter(getColor(R.color.wrong));
        }

        // Save High Score
        SharedPreferences prefs = getSharedPreferences("QuizPrefs", Context.MODE_PRIVATE);
        int highScore = prefs.getInt("HIGH_SCORE", 0);
        if (score > highScore) {
            prefs.edit().putInt("HIGH_SCORE", score).apply();
            // Optional: Show "New High Score!" message
        }

        MaterialButton btnRestart = findViewById(R.id.btnRestart);
        MaterialButton btnExit = findViewById(R.id.btnExit);

        btnRestart.setOnClickListener(v -> {
            Intent intent = new Intent(ResultActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        btnExit.setOnClickListener(v -> {
            finishAffinity();
        });
    }
}
