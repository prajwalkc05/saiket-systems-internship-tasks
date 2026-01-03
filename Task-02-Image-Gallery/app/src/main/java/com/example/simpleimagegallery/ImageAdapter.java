package com.example.simpleimagegallery;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

// Adapter class to bind images to the GridView
public class ImageAdapter extends BaseAdapter {
    private Context mContext;

    // Array of sample image resource IDs
    private int[] images = {
            R.drawable.nature,
            R.drawable.city,
            R.drawable.mountain,
            R.drawable.beach,
            R.drawable.tech,
            R.drawable.nature, // Duplicating to fill grid
            R.drawable.city,
            R.drawable.mountain,
            R.drawable.beach,
            R.drawable.tech
    };

    // Constructor
    public ImageAdapter(Context c) {
        mContext = c;
    }

    // Return the number of items in the data set
    public int getCount() {
        return images.length;
    }

    // Return the data item at the specified position
    public Object getItem(int position) {
        return images[position];
    }

    // Return the row ID of the item
    public long getItemId(int position) {
        return position;
    }

    // Create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;

        if (convertView == null) {
            // If it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            // Height equals width to make it a square
            imageView.setLayoutParams(new GridView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        } else {
            imageView = (ImageView) convertView;
        }

        // Make the item square based on column width
        // In GridView, it's tricky to get dynamic square items easily without a custom view,
        // but for now we set a fixed height approximate to what 3 columns on a phone might look like or just let it be rectangle.
        // A better approach for exact squares in GridView is a custom SquaredImageView.
        // Let's approximate dynamically.
        
        DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
        int screenWidth = metrics.widthPixels;
        // 3 columns, 2dp spacing * 2 gaps = 4dp total spacing. (Actually spacing is between columns)
        // With 3 columns, we have 2 spaces. 
        // Let's just say width / 3 roughly.
        int itemDimension = screenWidth / 3;
        
        imageView.setLayoutParams(new GridView.LayoutParams(itemDimension, itemDimension));

        // Set the image resource for the ImageView
        imageView.setImageResource(images[position]);
        return imageView;
    }
}