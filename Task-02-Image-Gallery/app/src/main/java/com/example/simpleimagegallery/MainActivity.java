package com.example.simpleimagegallery;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize the GridView from the layout
        GridView gridView = findViewById(R.id.gridView);

        // Set the custom adapter to populate the GridView with images
        gridView.setAdapter(new ImageAdapter(this));

        // Implement OnItemClickListener to handle image clicks
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                // Create an intent to open FullImageActivity
                Intent intent = new Intent(MainActivity.this, FullImageActivity.class);
                // Pass the image position/index
                intent.putExtra("id", position);
                startActivity(intent);
                // Apply fade in animation
                overridePendingTransition(R.anim.fade_in, 0);
            }
        });
    }
}