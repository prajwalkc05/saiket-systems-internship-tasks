package com.example.simpleimagegallery;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class FullImageActivity extends Activity {

    private ScaleGestureDetector scaleGestureDetector;
    private GestureDetector gestureDetector;
    private float mScaleFactor = 1.0f;
    private ImageView imageView;
    private ImageAdapter imageAdapter;
    private int currentPosition;

    // Slideshow variables
    private Handler slideshowHandler;
    private Runnable slideshowRunnable;
    private static final int SLIDESHOW_INTERVAL = 3000; // 3 seconds
    private boolean isSlideshowRunning = false;
    private Button btnSlideshow;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_image);

        // Retrieve the image resource
        Intent i = getIntent();
        if (i.getExtras() != null) {
            currentPosition = i.getExtras().getInt("id");
        }
        imageAdapter = new ImageAdapter(this);

        imageView = findViewById(R.id.fullImageView);
        loadImage();

        // Initialize ScaleGestureDetector
        scaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener());
        // Initialize GestureDetector
        gestureDetector = new GestureDetector(this, new GestureListener());

        // Initialize Slideshow Handler
        slideshowHandler = new Handler(Looper.getMainLooper());
        slideshowRunnable = new Runnable() {
            @Override
            public void run() {
                showNextSlide();
                slideshowHandler.postDelayed(this, SLIDESHOW_INTERVAL);
            }
        };

        // Initialize Buttons
        Button btnBack = findViewById(R.id.btnBack);
        btnSlideshow = findViewById(R.id.btnSlideshow);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Go back to the previous activity
                finish();
                overridePendingTransition(0, R.anim.fade_out);
            }
        });

        btnSlideshow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSlideshowRunning) {
                    stopSlideshow();
                } else {
                    startSlideshow();
                }
            }
        });

        updateSlideshowButton();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Slideshow does not auto-start
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopSlideshow();
    }

    private void startSlideshow() {
        if (!isSlideshowRunning) {
            slideshowHandler.postDelayed(slideshowRunnable, SLIDESHOW_INTERVAL);
            isSlideshowRunning = true;
            updateSlideshowButton();
        }
    }

    private void stopSlideshow() {
        slideshowHandler.removeCallbacks(slideshowRunnable);
        isSlideshowRunning = false;
        updateSlideshowButton();
    }

    private void updateSlideshowButton() {
        if (btnSlideshow != null) {
            btnSlideshow.setText(isSlideshowRunning ? "Stop Slideshow" : "Start Slideshow");
        }
    }

    private void showNextSlide() {
        currentPosition++;
        if (currentPosition >= imageAdapter.getCount()) {
            currentPosition = 0; // Loop back to the first image
        }
        loadImage();
    }

    private void loadImage() {
        imageView.setImageResource((Integer) imageAdapter.getItem(currentPosition));
        // Reset zoom when loading new image
        mScaleFactor = 1.0f;
        imageView.setScaleX(mScaleFactor);
        imageView.setScaleY(mScaleFactor);
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        scaleGestureDetector.onTouchEvent(motionEvent);
        gestureDetector.onTouchEvent(motionEvent);
        return true;
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
            mScaleFactor *= scaleGestureDetector.getScaleFactor();
            mScaleFactor = Math.max(1.0f, Math.min(mScaleFactor, 5.0f)); // Limit zoom (1x to 5x)
            
            imageView.setScaleX(mScaleFactor);
            imageView.setScaleY(mScaleFactor);
            return true;
        }
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        private static final int SWIPE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            if (mScaleFactor > 1.0f) {
                // Zoom out
                mScaleFactor = 1.0f;
            } else {
                // Zoom in
                mScaleFactor = 3.0f;
            }
            // Animate zoom
            imageView.animate().scaleX(mScaleFactor).scaleY(mScaleFactor).setDuration(300).start();
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (e1 == null || e2 == null) return false;
            
            float diffX = e2.getX() - e1.getX();
            float diffY = e2.getY() - e1.getY();

            // Only swipe if not zoomed in significantly to avoid conflict with panning
            if (mScaleFactor > 1.2f) return false;

            if (Math.abs(diffX) > Math.abs(diffY)) {
                if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffX > 0) {
                        onSwipeRight();
                    } else {
                        onSwipeLeft();
                    }
                    return true;
                }
            }
            return false;
        }
    }

    public void onSwipeRight() {
        if (currentPosition > 0) {
            currentPosition--;
            loadImage();
        } else {
            Toast.makeText(this, "First Image", Toast.LENGTH_SHORT).show();
        }
    }

    public void onSwipeLeft() {
        if (currentPosition < imageAdapter.getCount() - 1) {
            currentPosition++;
            loadImage();
        } else {
            Toast.makeText(this, "Last Image", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, R.anim.fade_out);
    }
}