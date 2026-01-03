package com.example.simplequizapp;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.radiobutton.MaterialRadioButton;

import java.util.Collections;
import java.util.List;

public class QuizActivity extends AppCompatActivity {

    private TextView tvQuestion, tvProgress, tvTimer, tvResult;
    private RadioGroup radioGroup;
    private MaterialRadioButton rbOption1, rbOption2, rbOption3, rbOption4;
    private MaterialButton btnSubmit;
    private LinearProgressIndicator progressBar;
    private MaterialToolbar toolbar;

    private List<Question> questionList;
    private int currentQuestionIndex = 0;
    private int score = 0;
    private boolean isAnswered = false;

    private CountDownTimer countDownTimer;
    private long timeLeftInMillis;
    private static final long COUNTDOWN_IN_MILLIS = 15000; // 15 seconds

    private ColorStateList defaultRbColor;
    private Vibrator vibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        // Initialize Views
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        
        tvQuestion = findViewById(R.id.tvQuestion);
        tvProgress = findViewById(R.id.tvProgress);
        tvTimer = findViewById(R.id.tvTimer);
        radioGroup = findViewById(R.id.radioGroup);
        rbOption1 = findViewById(R.id.rbOption1);
        rbOption2 = findViewById(R.id.rbOption2);
        rbOption3 = findViewById(R.id.rbOption3);
        rbOption4 = findViewById(R.id.rbOption4);
        btnSubmit = findViewById(R.id.btnSubmit);
        progressBar = findViewById(R.id.progressBar);
        tvResult = findViewById(R.id.tvResult);

        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        // Defensive checks
        if (rbOption1 == null || radioGroup == null) {
            Toast.makeText(this, "Error initializing views", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        defaultRbColor = rbOption1.getTextColors();

        // Handle Back Press using modern API
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Toast.makeText(QuizActivity.this, "Please finish the quiz!", Toast.LENGTH_SHORT).show();
            }
        });

        // Load Questions
        QuizRepository repository = new QuizRepository();
        questionList = repository.loadQuestions(this);
        
        if (questionList == null || questionList.isEmpty()) {
            Toast.makeText(this, R.string.no_questions_found, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Collections.shuffle(questionList);
        // Limit to 10 questions max for this "premium" feel or all available
        if (questionList.size() > 10) {
            questionList = questionList.subList(0, 10);
        }

        showNextQuestion();

        btnSubmit.setOnClickListener(v -> {
            if (!isAnswered) {
                if (radioGroup.getCheckedRadioButtonId() != -1) {
                    checkAnswer();
                } else {
                    Toast.makeText(QuizActivity.this, R.string.please_select_answer, Toast.LENGTH_SHORT).show();
                }
            } else {
                showNextQuestion();
            }
        });
    }

    private void showNextQuestion() {
        if (radioGroup != null) radioGroup.clearCheck();
        
        // Reset option styling
        resetOptionStyle(rbOption1);
        resetOptionStyle(rbOption2);
        resetOptionStyle(rbOption3);
        resetOptionStyle(rbOption4);
        
        // Clear result text
        if (tvResult != null) {
             tvResult.setText("");
        }

        if (radioGroup != null) {
            for (int i = 0; i < radioGroup.getChildCount(); i++) {
                radioGroup.getChildAt(i).setEnabled(true);
            }
        }

        if (currentQuestionIndex < questionList.size()) {
            isAnswered = false;
            btnSubmit.setText(R.string.submit);

            Question currentQuestion = questionList.get(currentQuestionIndex);

            if (tvQuestion != null) tvQuestion.setText(currentQuestion.getText());
            if (tvProgress != null) tvProgress.setText(getString(R.string.question_progress, (currentQuestionIndex + 1), questionList.size()));
            if (progressBar != null) progressBar.setProgress((int) (((float) (currentQuestionIndex + 1) / questionList.size()) * 100));

            List<String> options = currentQuestion.getOptions();
            if (options != null && options.size() >= 4) {
                rbOption1.setText(options.get(0));
                rbOption2.setText(options.get(1));
                rbOption3.setText(options.get(2));
                rbOption4.setText(options.get(3));
            }

            timeLeftInMillis = COUNTDOWN_IN_MILLIS;
            startCountDown();
        } else {
            finishQuiz();
        }
    }
    
    private void resetOptionStyle(MaterialRadioButton rb) {
        if (rb == null) return;
        rb.setTextColor(ContextCompat.getColor(this, R.color.text_primary));
        rb.setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.primary)));
        rb.setBackgroundResource(R.drawable.bg_option_selector);
    }

    private void startCountDown() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                timeLeftInMillis = 0;
                updateCountDownText();
                if (!isFinishing() && !isDestroyed()) {
                     checkAnswer();
                }
            }
        }.start();
    }

    private void updateCountDownText() {
        if (tvTimer == null) return;
        int seconds = (int) (timeLeftInMillis / 1000);
        tvTimer.setText(String.valueOf(seconds));
        
        if (seconds < 5) {
            tvTimer.setTextColor(ContextCompat.getColor(this, R.color.timer_warning));
        } else {
            tvTimer.setTextColor(ContextCompat.getColor(this, R.color.primary));
        }
    }

    private void checkAnswer() {
        isAnswered = true;
        if (countDownTimer != null) countDownTimer.cancel();

        RadioButton rbSelected = null;
        if (radioGroup != null) {
             int selectedId = radioGroup.getCheckedRadioButtonId();
             if (selectedId != -1) {
                 rbSelected = findViewById(selectedId);
             }
        }

        Question currentQuestion = questionList.get(currentQuestionIndex);
        String selectedAnswer = (rbSelected != null) ? rbSelected.getText().toString() : "";

        if (selectedAnswer.equals(currentQuestion.getCorrectAnswer())) {
            score++;
            if (tvResult != null) {
                tvResult.setText(R.string.correct_message);
                tvResult.setTextColor(ContextCompat.getColor(this, R.color.correct));
            }
            feedback(true);
        } else {
            if (tvResult != null) {
                tvResult.setText(R.string.wrong_message);
                tvResult.setTextColor(ContextCompat.getColor(this, R.color.wrong));
            }
            feedback(false);
        }

        showSolution();
    }

    private void showSolution() {
        if (radioGroup != null) {
            for (int i = 0; i < radioGroup.getChildCount(); i++) {
                radioGroup.getChildAt(i).setEnabled(false);
            }
        }

        Question currentQuestion = questionList.get(currentQuestionIndex);
        String correctAnswer = currentQuestion.getCorrectAnswer();

        highlightAnswer(rbOption1, correctAnswer);
        highlightAnswer(rbOption2, correctAnswer);
        highlightAnswer(rbOption3, correctAnswer);
        highlightAnswer(rbOption4, correctAnswer);

        if (btnSubmit != null) {
            if (currentQuestionIndex < questionList.size() - 1) {
                btnSubmit.setText(R.string.next);
            } else {
                btnSubmit.setText(R.string.finish);
            }
        }
        
        currentQuestionIndex++;
    }
    
    private void highlightAnswer(MaterialRadioButton rb, String correctAnswer) {
        if (rb == null) return;
        
        if (rb.getText().toString().equals(correctAnswer)) {
            // Correct answer: Green
            rb.setTextColor(ContextCompat.getColor(this, R.color.correct));
            rb.setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.correct)));
            // Optional: Set a specific background or icon
        } else if (rb.isChecked()) {
            // Wrong selected answer: Red
            rb.setTextColor(ContextCompat.getColor(this, R.color.wrong));
            rb.setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.wrong)));
        }
    }

    private void feedback(boolean isCorrect) {
        if (vibrator != null && vibrator.hasVibrator()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (isCorrect) {
                    vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                }
            } else {
                vibrator.vibrate(100);
            }
        }
    }

    private void finishQuiz() {
        Intent intent = new Intent(QuizActivity.this, ResultActivity.class);
        intent.putExtra("SCORE", score);
        intent.putExtra("TOTAL", questionList.size());
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}
