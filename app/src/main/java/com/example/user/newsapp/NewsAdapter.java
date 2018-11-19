package com.example.user.newsapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

/**
 * Created by user on 11/17/2018.
 */

public class NewsAdapter extends ArrayAdapter<News> {

    Random random = new Random();
    int[] drawables = new int[] {R.drawable.sport, R.drawable.fashion, R.drawable.world, R.drawable.politics,
            R.drawable.sport, R.drawable.fashion, R.drawable.world, R.drawable.politics,
            R.drawable.sport, R.drawable.fashion, R.drawable.world, R.drawable.politics};
    public NewsAdapter(Context context, ArrayList<News> events) {
        super(context, 0, events);

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if the existing view is being reused, otherwise inflate the view
        View newsListItem = convertView;
        if (newsListItem == null) {
            newsListItem = LayoutInflater.from(getContext()).inflate(
                    R.layout.news_list_item, parent, false);

            News news = getItem(position);
            // Find the TextView in the list_item.xml layout with the ID version_name
            TextView newsTitleTextView = newsListItem.findViewById(R.id.textView_news_title);
            TextView categoryTextView = newsListItem.findViewById(R.id.textView_news_category);
            TextView dateTextView = newsListItem.findViewById(R.id.textView_date_time);
            ImageView newImageView = newsListItem.findViewById(R.id.imageView_news);


            // Get the version name from the current AndroidFlavor object and
            // set this text on the name TextView
            newsTitleTextView.setText(news.getTitle());
            categoryTextView.setText(news.getCategory());
            dateTextView.setText(news.getDate().substring(0,10));

            int randomImagePosition = random.nextInt((10 - 0) + 1) + 0;
            newImageView.setImageResource(drawables[randomImagePosition]);

        }
        return newsListItem;
    }
}