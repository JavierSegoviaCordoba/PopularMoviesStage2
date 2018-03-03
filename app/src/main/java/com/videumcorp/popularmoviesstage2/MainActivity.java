package com.videumcorp.popularmoviesstage2;

import android.content.ContentResolver;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Menu;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.videumcorp.popularmoviesstage2.Adapters.MoviesAdapter;
import com.videumcorp.popularmoviesstage2.ContentProviders.FavoritesProvider;
import com.videumcorp.popularmoviesstage2.Gsons.Movie.GsonMovie;
import com.videumcorp.popularmoviesstage2.Gsons.Movie.MovieResultsItem;
import com.videumcorp.popularmoviesstage2.Utils.EndlessRecyclerViewScrollListener;
import com.videumcorp.popularmoviesstage2.Utils.NetworkUtils;
import com.videumcorp.popularmoviesstage2.Utils.ScreenMeasures;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    //VIEWS
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.bottomNavigationView)
    BottomNavigationView bottomNavigationView;
    @BindView(R.id.recyclerViewVideos)
    RecyclerView recyclerView;
    @BindView(R.id.imageViewNoInternet)
    ImageView imageViewNoInternet;
    private Toast toastNoInternet;

    //STRINGS
    //API URL
    @BindString(R.string.api_base_url)
    String api_base_url;
    @BindString(R.string.api_popular_url)
    String api_popular_url;
    @BindString(R.string.api_top_rated_url)
    String api_top_rated_url;
    @BindString(R.string.api_page)
    String api_page;
    @BindString(R.string.api_key_pre)
    String api_key_pre;
    @BindString(R.string.api_key)
    String api_key;

    //INTEGER
    private int currentPage = 1;

    //RECYCLERVIEW ELEMENTS
    private final List<MovieResultsItem> movieResultsItemList = new ArrayList<>();

    private MoviesAdapter moviesAdapter;
    private RecyclerView.LayoutManager gridLayoutManager;
    private EndlessRecyclerViewScrollListener endlessRecyclerViewScrollListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        initRecyclerView();

        initBottomNavigationView();

        getMovieData(api_popular_url, "1");
    }

    private void initBottomNavigationView() {
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            movieResultsItemList.clear();
            currentPage = 0;
            moviesAdapter.notifyDataSetChanged();

            switch (id) {
                case R.id.action_popular:
                    endlessRecyclerViewScrollListener.resetState();
                    item.setChecked(true);
                    getMovieData(api_popular_url, "1");
                    break;
                case R.id.action_top_rated:
                    endlessRecyclerViewScrollListener.resetState();
                    item.setChecked(true);
                    getMovieData(api_top_rated_url, "1");
                    break;
                case R.id.action_favorites:
                    endlessRecyclerViewScrollListener.resetState();
                    item.setChecked(true);
                    getMoviesInDB();
                    break;
                default:
                    currentPage = 0;
                    endlessRecyclerViewScrollListener.resetState();
                    getMovieData(api_popular_url, "1");
                    break;
            }
            return false;
        });
    }

    private void initRecyclerView() {
        moviesAdapter = new MoviesAdapter(movieResultsItemList, this);
        gridLayoutManager = new GridLayoutManager(this, getGridLayoutManagerColumnsAuto());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(moviesAdapter);

        final int acumulateScroll[] = new int[2];
        acumulateScroll[0] = 0;
        acumulateScroll[1] = 0;
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                Animation animationHide = new TranslateAnimation(0, 0, 0, bottomNavigationView.getHeight());
                animationHide.setInterpolator(new AnticipateOvershootInterpolator(1));
                animationHide.setDuration(500);

                Animation animationShow = new TranslateAnimation(0, 0, bottomNavigationView.getHeight(), 0);
                animationShow.setInterpolator(new AnticipateOvershootInterpolator(1));
                animationShow.setDuration(500);

                if (NetworkUtils.networkAvailable(getApplicationContext())) {
                    if (dy >= 0) {
                        acumulateScroll[0] = acumulateScroll[0] + dy;
                    } else {
                        acumulateScroll[1] = acumulateScroll[1] + dy;
                    }

                    if (acumulateScroll[0] >= 200) {
                        acumulateScroll[0] = 0;
                        acumulateScroll[1] = 0;
                        if (bottomNavigationView.getVisibility() == View.VISIBLE) {
                            bottomNavigationView.startAnimation(animationHide);
                            bottomNavigationView.setVisibility(View.GONE);
                        }
                    }

                    if (acumulateScroll[1] <= -200) {
                        acumulateScroll[0] = 0;
                        acumulateScroll[1] = 0;
                        if (bottomNavigationView.getVisibility() == View.GONE) {
                            bottomNavigationView.startAnimation(animationShow);
                            bottomNavigationView.setVisibility(View.VISIBLE);
                        }
                    }
                } else if (bottomNavigationView.getVisibility() == View.GONE) {
                    bottomNavigationView.startAnimation(animationShow);
                    bottomNavigationView.setVisibility(View.VISIBLE);
                }
            }
        });

        endlessRecyclerViewScrollListener = new EndlessRecyclerViewScrollListener((GridLayoutManager) gridLayoutManager, currentPage) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                currentPage = page;

                switch (bottomNavigationView.getSelectedItemId()) {
                    case R.id.action_popular:
                        getMovieData(api_popular_url, String.valueOf(page));
                        break;
                    case R.id.action_top_rated:
                        getMovieData(api_top_rated_url, String.valueOf(page));
                        break;
                    case R.id.action_favorites:
                        break;
                    default:
                        getMovieData(api_popular_url, String.valueOf(page));
                        break;
                }
            }
        };

        recyclerView.addOnScrollListener(endlessRecyclerViewScrollListener);
    }

    private void getMovieData(String sort, String page) {
        if (NetworkUtils.networkAvailable(this)) {
            simpleVolleyRequest(api_base_url + sort + api_page + page + api_key_pre + api_key);
            showHideImageViewNoInternet(false);
        } else {
            showHideImageViewNoInternet(true);
        }
    }

    private void showHideImageViewNoInternet(boolean show) {
        if (show) {
            if (toastNoInternet == null) {
                toastNoInternet = Toast.makeText(this, getResources().getString(R.string.label_no_internet), Toast.LENGTH_SHORT);
                toastNoInternet.show();
            } else {
                if (!toastNoInternet.getView().isShown()) {
                    toastNoInternet.show();
                }
            }
            if (imageViewNoInternet.getVisibility() != View.VISIBLE) {
                Animation animation = new AlphaAnimation(0, 1);
                animation.setDuration(500);
                animation.setInterpolator(new FastOutSlowInInterpolator());
                imageViewNoInternet.startAnimation(animation);
                imageViewNoInternet.setVisibility(View.VISIBLE);
            }
        } else {
            if (imageViewNoInternet.getVisibility() == View.VISIBLE) {
                Animation animation = new AlphaAnimation(1, 0);
                animation.setInterpolator(new FastOutSlowInInterpolator());
                animation.setDuration(500);
                imageViewNoInternet.startAnimation(animation);
                imageViewNoInternet.setVisibility(View.GONE);
            }
        }
    }

    private void simpleVolleyRequest(String url) {
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject jsonObjectAPIResponse = new JSONObject(response);
                        Gson gson = new Gson();
                        GsonMovie gsonMovie = gson.fromJson(String.valueOf(jsonObjectAPIResponse), GsonMovie.class);
                        movieResultsItemList.addAll(gsonMovie.getResults());
                        moviesAdapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> {
        });
        queue.add(stringRequest);
    }

    private void getMoviesInDB() {
        ContentResolver contentResolver = getContentResolver();
        String[] columns = {
                FavoritesProvider.Favorites.COL_ID,
                FavoritesProvider.Favorites.COL_GSON_MOVIE,
                FavoritesProvider.Favorites.COL_GSON_VIDEOS,
                FavoritesProvider.Favorites.COL_GSON_REVIEWS};
        Cursor cursor = contentResolver.query(FavoritesProvider.CONTENT_URI, columns, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                try {
                    JSONObject jsonObject = new JSONObject(cursor.getString(1));
                    Gson gson = new Gson();
                    MovieResultsItem movieResultsItem = gson.fromJson(String.valueOf(jsonObject), MovieResultsItem.class);
                    movieResultsItemList.add(movieResultsItem);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } while (cursor.moveToNext());
            moviesAdapter.notifyDataSetChanged();
            cursor.close();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        int position = ((GridLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();

        gridLayoutManager = new GridLayoutManager(this, getGridLayoutManagerColumnsAuto());
        recyclerView.setLayoutManager(gridLayoutManager);
        gridLayoutManager.scrollToPosition(position);

        endlessRecyclerViewScrollListener = new EndlessRecyclerViewScrollListener((GridLayoutManager) gridLayoutManager, currentPage) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                currentPage = page;

                switch (bottomNavigationView.getSelectedItemId()) {
                    case R.id.action_popular:
                        getMovieData(api_popular_url, String.valueOf(page));
                        break;
                    case R.id.action_top_rated:
                        getMovieData(api_top_rated_url, String.valueOf(page));
                        break;
                    case R.id.action_favorites:
                        break;
                    default:
                        getMovieData(api_popular_url, String.valueOf(page));
                        break;
                }
            }
        };

        recyclerView.addOnScrollListener(endlessRecyclerViewScrollListener);
    }

    private int getGridLayoutManagerColumnsAuto() {
        int screenWidth = ScreenMeasures.screenWidth(this);
        float itemWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 120, getResources().getDisplayMetrics());
        return (int) (screenWidth / itemWidth);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (bottomNavigationView.getVisibility() == View.GONE && recyclerView.computeVerticalScrollOffset() <= 200) {
            Animation animationShow = new TranslateAnimation(0, 0, bottomNavigationView.getHeight(), 0);
            animationShow.setInterpolator(new AnticipateOvershootInterpolator(1.5f));
            animationShow.setDuration(250);
            bottomNavigationView.startAnimation(animationShow);
            bottomNavigationView.setVisibility(View.VISIBLE);
        }
        if (bottomNavigationView.getSelectedItemId() == R.id.action_favorites) {
            ContentResolver contentResolver = getContentResolver();
            String[] columns = {
                    FavoritesProvider.Favorites.COL_ID,
                    FavoritesProvider.Favorites.COL_GSON_MOVIE,
                    FavoritesProvider.Favorites.COL_GSON_VIDEOS,
                    FavoritesProvider.Favorites.COL_GSON_REVIEWS};
            Cursor cursor = contentResolver.query(FavoritesProvider.CONTENT_URI, columns, null, null, null);

            if (cursor != null && movieResultsItemList.size() != cursor.getCount()) {
                movieResultsItemList.clear();
                currentPage = 0;
                getMoviesInDB();
                cursor.close();
            }
        }
    }
}
