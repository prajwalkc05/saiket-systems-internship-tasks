package com.example.switchactivityapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

public class ActivityTwo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_two);

        TextView textViewReceivedMessage = findViewById(R.id.textViewReceivedMessage);
        EditText editTextReply = findViewById(R.id.editTextReply);
        Button buttonSendBack = findViewById(R.id.buttonSendBack);

        // Retrieve the String data sent from MainActivity
        String message = getIntent().getStringExtra("message");

        // Display the received message
        if (message != null) {
            textViewReceivedMessage.setText(message);
        }

        // Handle "SEND BACK" button click
        buttonSendBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String reply = editTextReply.getText().toString();

                // Create intent to hold the result data
                Intent resultIntent = new Intent();
                resultIntent.putExtra("reply_message", reply);

                // Set result status and data
                setResult(RESULT_OK, resultIntent);

                // Finish the current activity to return to the previous one
                finish();

                // Apply slide-out right animation
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });

        // Handle Back Press using OnBackPressedDispatcher
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Finish the activity
                finish();
                // Apply animation
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });
    }
}