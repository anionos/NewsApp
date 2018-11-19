package com.example.user.newsapp;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<News>>, TrendingNewsAdapter.TrendingListItemClickListner {


    /**
     * URL for earthquake data from the USGS dataset
     */
    private static final String USGS_REQUEST_URL =
            "http://content.guardianapis.com/search";

    /**
     * Constant value for the earthquake loader ID. We can choose any integer.
     * This really only comes into play if you're using multiple loaders.
     */
    private static final int NEWS_LOADER_ID = 1;
    private static final int TRENDING_NEWS_LOADER_ID = 2;
    private static final int SPORT_NEWS_LOADER_ID = 3;
    private static final int FASHION_NEWS_LOADER_ID = 4;
    private static final int POLITICS_NEWS_LOADER_ID = 5;
    private static final String TAG = MainActivity.class.getSimpleName();

    /**
     * Adapter for the list of earthquakes
     */
    private NewsAdapter mNewsAdapter;
    private TrendingNewsAdapter mTrendingAdapter;

    /**
     * TextView that is displayed when the list is empty
     */
    private TextView mEmptyStateTextView;
    /**
     * TextView that is displayed when the recyclerview is empty
     */
    private TextView mEmptyStateTextViewRecycler;
    // Find a reference to the {@link ListView} in the layout
    ListView newsListView;
    RecyclerView trendingRecyclerView;
    TrendingNewsAdapter trendingNewsAdapter;

    ArrayList<News> trendingNews = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mEmptyStateTextView = findViewById(R.id.empty_view);
        mEmptyStateTextViewRecycler = findViewById(R.id.empty_view_recyclerview);
        newsListView = findViewById(R.id.news_list);
        trendingRecyclerView = findViewById(R.id.trending_news_list);

        //set the empty view for the listview
        newsListView.setEmptyView(mEmptyStateTextView);
        //set the empty view for the recyclerview
        setViewForRecyclerView(trendingNews);
        // create a linear layout manager
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        //set the layout manager to the recyclerview
        trendingRecyclerView.setLayoutManager(layoutManager);


        // create the adapter
        mNewsAdapter = new NewsAdapter(this, new ArrayList<News>());
        //set the listview to the adapter
        newsListView.setAdapter(mNewsAdapter);

        newsListView.setOnTouchListener(new View.OnTouchListener() {
            // Setting on Touch Listener for handling the touch inside ScrollView
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Disallow the touch request for parent scroll on touch of child view
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
        setListViewHeightBasedOnChildren(newsListView);

        trendingRecyclerView.setNestedScrollingEnabled(false);



        //create the adapter
        trendingNewsAdapter = new TrendingNewsAdapter(this, new ArrayList<News>(), this);
        //set the adapter to the recyclerview
        trendingRecyclerView.setAdapter(trendingNewsAdapter);


        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        // If there is a network connection, fetch data
        if (networkInfo != null && networkInfo.isConnected()) {
            // Get a reference to the LoaderManager, in order to interact with loaders.
            LoaderManager loaderManager = getLoaderManager();

            Bundle bundle = new Bundle();
            bundle.putString("key", "debate");
            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
            loaderManager.initLoader(NEWS_LOADER_ID, bundle, this);

            Bundle bundleTrending = new Bundle();
            bundleTrending.putString("key", "world");
            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
            loaderManager.initLoader(TRENDING_NEWS_LOADER_ID, bundleTrending, this);
        } else {
            // Otherwise, display error
            // First, hide loading indicator so error message will be visible
            View loadingIndicator = findViewById(R.id.loading_indicator);
            loadingIndicator.setVisibility(View.GONE);

            // Update empty state with no connection error message
            mEmptyStateTextView.setText(R.string.no_internet_connection);
        }

        newsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Find the current earthquake that was clicked on
                News currentNews = mNewsAdapter.getItem(position);

                // Convert the String URL into a URI object (to pass into the Intent constructor)
                Uri earthquakeUri = Uri.parse(currentNews.getUrl());

                // Create a new intent to view the earthquake URI
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, earthquakeUri);

                // Send the intent to launch a new activity
                startActivity(websiteIntent);
            }
        });


        setTodayNewsClickListners();
    }

    /**** Method for Setting the Height of the ListView dynamically.
     **** Hack to fix the issue of not showing all the items of the ListView
     **** when placed inside a ScrollView  ****/
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, ViewGroup.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    public void setTodayNewsClickListners() {
        CardView worldCardView = findViewById(R.id.world);
        CardView sportCardView = findViewById(R.id.sport);
        CardView fashionCardView = findViewById(R.id.fashion);
        CardView politicsCardView = findViewById(R.id.politics);

        final View view1 = findViewById(R.id.view1);
        final View view2 = findViewById(R.id.view2);
        final View view3 = findViewById(R.id.view3);
        final View view4 = findViewById(R.id.view4);

        final LoaderManager loaderManager = getLoaderManager();
        worldCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view1.setVisibility(View.VISIBLE);
                view2.setVisibility(View.GONE);
                view3.setVisibility(View.GONE);
                view4.setVisibility(View.GONE);

                View loadingIndicator = findViewById(R.id.loading_indicator);
                loadingIndicator.setVisibility(View.VISIBLE);

                Bundle bundle = new Bundle();
                bundle.putString("key", "world");
                // Initialize the loader. Pass in the int ID constant defined above and pass in null for
                // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
                // because this activity implements the LoaderCallbacks interface).
                loaderManager.destroyLoader(NEWS_LOADER_ID);
                loaderManager.restartLoader(NEWS_LOADER_ID, bundle, MainActivity.this);

            }
        });
        sportCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view1.setVisibility(View.GONE);
                view2.setVisibility(View.VISIBLE);
                view3.setVisibility(View.GONE);
                view4.setVisibility(View.GONE);

                View loadingIndicator = findViewById(R.id.loading_indicator);
                loadingIndicator.setVisibility(View.VISIBLE);

                Bundle bundle = new Bundle();
                bundle.putString("key", "sports");
                // Initialize the loader. Pass in the int ID constant defined above and pass in null for
                // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
                // because this activity implements the LoaderCallbacks interface).
                loaderManager.destroyLoader(NEWS_LOADER_ID);
                loaderManager.restartLoader(NEWS_LOADER_ID, bundle, MainActivity.this);

            }
        });
        fashionCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view1.setVisibility(View.GONE);
                view2.setVisibility(View.GONE);
                view3.setVisibility(View.VISIBLE);
                view4.setVisibility(View.GONE);

                View loadingIndicator = findViewById(R.id.loading_indicator);
                loadingIndicator.setVisibility(View.VISIBLE);

                Bundle bundle = new Bundle();
                bundle.putString("key", "fashion");
                // Initialize the loader. Pass in the int ID constant defined above and pass in null for
                // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
                // because this activity implements the LoaderCallbacks interface).
                loaderManager.destroyLoader(NEWS_LOADER_ID);
                loaderManager.restartLoader(NEWS_LOADER_ID, bundle, MainActivity.this);
            }
        });
        politicsCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view1.setVisibility(View.GONE);
                view2.setVisibility(View.GONE);
                view3.setVisibility(View.GONE);
                view4.setVisibility(View.VISIBLE);

                View loadingIndicator = findViewById(R.id.loading_indicator);
                loadingIndicator.setVisibility(View.VISIBLE);

                Bundle bundle = new Bundle();
                bundle.putString("key", "politics");
                // Initialize the loader. Pass in the int ID constant defined above and pass in null for
                // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
                // because this activity implements the LoaderCallbacks interface).
                loaderManager.destroyLoader(NEWS_LOADER_ID);
                loaderManager.restartLoader(NEWS_LOADER_ID, bundle, MainActivity.this);

            }
        });
    }

    public void setViewForRecyclerView(List<News> news) {
        if (news.isEmpty()) {
            trendingRecyclerView.setVisibility(View.INVISIBLE);
            mEmptyStateTextViewRecycler.setVisibility(View.VISIBLE);
        } else {
            trendingRecyclerView.setVisibility(View.VISIBLE);
            mEmptyStateTextViewRecycler.setVisibility(View.GONE);
        }
    }

    @Override
    public Loader<List<News>> onCreateLoader(int i, Bundle bundle) {

        Log.e(TAG, "i is this " + i);
        Uri baseUri = Uri.parse(USGS_REQUEST_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        uriBuilder.appendQueryParameter("q", bundle.getString("key"));
        uriBuilder.appendQueryParameter("api-key", "test");


        return new NewsLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<News>> loader, List<News> news) {
// Hide loading indicator because the data has been loaded
        View loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);

        // Set empty state text to display "No earthquakes found."
        mEmptyStateTextView.setText(R.string.no_news);
        // Clear the adapter of previous earthquake data


        // If there is a valid list of {@link Earthquake}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (loader.getId() == 2) {
            if (news != null && !news.isEmpty()) {
                setViewForRecyclerView(news);
                trendingNewsAdapter.setData(news);
            }
        } else {
            if (news != null && !news.isEmpty()) {
                mNewsAdapter.clear();
                mNewsAdapter.notifyDataSetChanged();
                mNewsAdapter.addAll(news);
//            updateUi(earthquakes);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<List<News>> loader) {
// Loader reset, so we can clear out our existing data.
        mNewsAdapter.clear();
    }


    @Override
    public void onTrendingItemClick(News news) {
        // Convert the String URL into a URI object (to pass into the Intent constructor)
        Uri earthquakeUri = Uri.parse(news.getUrl());

        // Create a new intent to view the earthquake URI
        Intent websiteIntent = new Intent(Intent.ACTION_VIEW, earthquakeUri);

        // Send the intent to launch a new activity
        startActivity(websiteIntent);
    }
}
