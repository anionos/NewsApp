package com.example.user.newsapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by user on 11/19/2018.
 */

public class TrendingNewsAdapter  extends RecyclerView.Adapter<TrendingNewsAdapter.TrendingViewHolder>{

    Random random = new Random();
    int[] drawables = new int[] {R.drawable.sport, R.drawable.fashion, R.drawable.world, R.drawable.world2,
            R.drawable.sport, R.drawable.film, R.drawable.business, R.drawable.politics,
            R.drawable.global, R.drawable.fashion, R.drawable.food, R.drawable.and};
    private Context context;
    private ArrayList<News> newsArrayList;
    TrendingListItemClickListner trendingListItemClickListner;

    public TrendingNewsAdapter(Context context, ArrayList<News> newsArrayList, TrendingListItemClickListner trendingListItemClickListner) {
        this.context = context;
        this.newsArrayList = newsArrayList;
        this.trendingListItemClickListner = trendingListItemClickListner;
    }

    @Override
    public TrendingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.trending_news_list, parent, false);

        return new TrendingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TrendingViewHolder holder, int position) {
        final News news = newsArrayList.get(position);

        holder.newsTitle.setText(news.getTitle());
        holder.newsCategory.setText(news.getCategory());
        holder.newsDate.setText(news.getDate().substring(0,10));

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                trendingListItemClickListner.onTrendingItemClick(news);
            }
        });

        int randomImagePosition = random.nextInt((10 - 0) + 1) + 0;
        holder.imageView.setImageResource(drawables[randomImagePosition]);
    }

    @Override
    public int getItemCount() {
        return newsArrayList.size();
    }


    public void setData(List<News> newsList){
        newsArrayList = new ArrayList<>(newsList);
        notifyDataSetChanged();
    }

    class TrendingViewHolder extends RecyclerView.ViewHolder {
        View view;
        TextView newsTitle;
        TextView newsCategory;
        TextView newsDate;
        ImageView imageView;

        public TrendingViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            newsTitle = itemView.findViewById(R.id.textView_news_title);
            newsCategory = itemView.findViewById(R.id.textView_news_category);
            newsDate = itemView.findViewById(R.id.textView_news_date);
            imageView = itemView.findViewById(R.id.trending_img);
        }
    }

    public interface TrendingListItemClickListner {
        void onTrendingItemClick(News news);
    }
}
