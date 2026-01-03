package com.example.switchactivityapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_ACTIVITY_TWO = 1;
    private TextView textViewResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewResult = findViewById(R.id.textViewResult);
        Button buttonGoToActivityTwo = findViewById(R.id.buttonGoToActivityTwo);

        buttonGoToActivityTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to ActivityTwo using startActivityForResult
                Intent intent = new Intent(MainActivity.this, ActivityTwo.class);
                intent.putExtra("message", "Hello from Activity One");
                startActivityForResult(intent, REQUEST_CODE_ACTIVITY_TWO);

                // Apply slide-in right animation
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
    }

    // Handle the result from ActivityTwo
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_ACTIVITY_TWO) {
            if (resultCode == RESULT_OK && data != null) {
                String reply = data.getStringExtra("reply_message");
                
                // Display in TextView
                textViewResult.setText("Reply: " + reply);
                
                // Display in Toast
                Toast.makeText(this, "Received: " + reply, Toast.LENGTH_SHORT).show();
            }
        }
    }
}