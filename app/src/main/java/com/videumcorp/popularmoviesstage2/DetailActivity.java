package com.videumcorp.popularmoviesstage2;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.graphics.ColorUtils;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.gson.Gson;
import com.videumcorp.popularmoviesstage2.Adapters.ReviewsAdapter;
import com.videumcorp.popularmoviesstage2.Adapters.VideosAdapter;
import com.videumcorp.popularmoviesstage2.ContentProviders.FavoritesProvider;
import com.videumcorp.popularmoviesstage2.Gsons.Movie.MovieResultsItem;
import com.videumcorp.popularmoviesstage2.Gsons.Reviews.GsonReviews;
import com.videumcorp.popularmoviesstage2.Gsons.Reviews.ReviewsResultsItem;
import com.videumcorp.popularmoviesstage2.Gsons.Videos.GsonVideos;
import com.videumcorp.popularmoviesstage2.Gsons.Videos.VideosResultsItem;
import com.videumcorp.popularmoviesstage2.Utils.NetworkUtils;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailActivity extends AppCompatActivity {

    //VIEWS
    @BindView(R.id.floatingActionButtonFavorite)
    FloatingActionButton floatingActionButtonFavorite;
    @BindView(R.id.lineLayoutReviews)
    LinearLayout linearLayoutReviews;
    @BindView(R.id.lineLayoutReviewsChild)
    LinearLayout linearLayoutReviewsChild;
    @BindView(R.id.lineLayoutReviewsChild2)
    LinearLayout linearLayoutReviewsChild2;
    @BindView(R.id.imageViewChevronReviews1)
    ImageView imageViewChevronReviews1;
    @BindView(R.id.textViewReviewLabel)
    TextView textViewReviewLabel;
    @BindView(R.id.imageViewChevronReviews2)
    ImageView imageViewChevronReviews2;
    @BindView(R.id.appBarLayout)
    AppBarLayout appBarLayout;
    @BindView(R.id.collapsingToolbarLayout)
    CollapsingToolbarLayout collapsingToolbarLayout;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.textViewTitle)
    TextView textViewTitle;
    @BindView(R.id.imageViewBackdrop)
    ImageView imageViewBackdrop;
    @BindView(R.id.nestedScrollView)
    NestedScrollView nestedScrollView;
    @BindView(R.id.imageViewPoster)
    ImageView imageViewPoster;
    @BindView(R.id.linearLayoutDetailActivityBackground)
    LinearLayout linearLayoutDetailActivityBackground;
    @BindView(R.id.textViewOverview)
    TextView textViewOverview;
    @BindView(R.id.textViewReleaseDate)
    TextView textViewReleaseDate;
    @BindView(R.id.textViewOriginalLanguage)
    TextView textViewOriginalLanguage;
    @BindView(R.id.textViewVote)
    TextView textViewVoteCount;
    @BindView(R.id.recyclerViewVideos)
    RecyclerView recyclerViewVideos;
    @BindView(R.id.recyclerViewReviews)
    RecyclerView recyclerViewReviews;

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
    @BindString(R.string.api_image_base_url)
    String api_image_base_url;
    @BindString(R.string.api_image_w780_url)
    String api_image_w780_url;
    @BindString(R.string.api_image_w185_url)
    String api_image_w185_url;
    @BindString(R.string.api_videos_url)
    String api_videos_url;
    @BindString(R.string.api_reviews_url)
    String api_reviews_url;

    //MORE
    @BindString(R.string.youtube_base_url)
    String youtube_base_url;

    //BOOLEANS
    private boolean localeFound = false;

    MovieResultsItem gsonMovie;
    GsonVideos gsonVideos;
    GsonReviews gsonReviews;

    //RECYCLERVIEW ELEMENTS
    private final List<VideosResultsItem> videosResultsItemList = new ArrayList<>();
    private final List<ReviewsResultsItem> reviewsResultsItemList = new ArrayList<>();
    private VideosAdapter videosAdapter;
    private ReviewsAdapter reviewsAdapter;

    private static final int appbar_animation_duration = 300;
    private static final int scroll_to_animate = 200;
    private static final int glide_duration = 500;
    private static final float tension = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        getGsonFromIntent();

        populateUI();

        if (!NetworkUtils.networkAvailable(this)) {
            Toast.makeText(this, getResources().getString(R.string.label_no_internet), Toast.LENGTH_SHORT).show();
            getMovieVideosInDB();
            getMovieReviewsInDB();
        } else {
            simpleVolleyRequest(api_base_url + gsonMovie.getId() + api_videos_url + api_key_pre + api_key,
                    api_base_url + gsonMovie.getId() + api_reviews_url + api_key_pre + api_key);
        }
    }

    private void getMovieReviewsInDB() {
        ContentResolver contentResolver = getContentResolver();
        String selection = FavoritesProvider.Favorites.COL_ID + " =?";
        String[] selectionArgs = {String.valueOf(gsonMovie.getId())};
        Cursor cursor = contentResolver.query(FavoritesProvider.CONTENT_URI, null, selection, selectionArgs, null);

        if (cursor != null) {
            cursor.moveToFirst();
            try {
                JSONObject jsonObject = new JSONObject(cursor.getString(3));
                Gson gson = new Gson();
                GsonReviews gsonReviews = gson.fromJson(String.valueOf(jsonObject), GsonReviews.class);
                reviewsResultsItemList.addAll(gsonReviews.getResults());
                reviewsAdapter.notifyDataSetChanged();
                textViewReviewLabel.setText(MessageFormat.format("{0} {1}", reviewsResultsItemList.size(), getResources().getString(R.string.label_reviews)));

                if (reviewsResultsItemList.size() != 0) {
                    linearLayoutReviewsChild.setOnClickListener(v -> {
                        if (linearLayoutReviewsChild2.getVisibility() == View.VISIBLE) {
                            linearLayoutReviewsChild2.setVisibility(View.GONE);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                imageViewChevronReviews1.setImageDrawable(getResources().getDrawable(R.drawable.ic_keyboard_arrow_up_black_24dp));
                                ((AnimatedVectorDrawable) imageViewChevronReviews1.getDrawable()).start();
                                imageViewChevronReviews2.setImageDrawable(getResources().getDrawable(R.drawable.ic_keyboard_arrow_up_black_24dp));
                                ((AnimatedVectorDrawable) imageViewChevronReviews2.getDrawable()).start();
                            } else {
                                imageViewChevronReviews1.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24dp);
                                imageViewChevronReviews2.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24dp);
                            }
                        } else {
                            int duration;
                            if (isAppBarExpanded(appBarLayout)) {
                                duration = appbar_animation_duration;
                            } else {
                                duration = 0;
                            }
                            appBarLayout.setExpanded(false, true);
                            final Handler handler = new Handler();
                            handler.postDelayed(() -> {
                                linearLayoutReviewsChild2.setVisibility(View.VISIBLE);
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    imageViewChevronReviews1.setImageDrawable(getResources().getDrawable(R.drawable.ic_keyboard_arrow_down_black_24dp));
                                    ((AnimatedVectorDrawable) imageViewChevronReviews1.getDrawable()).start();
                                    imageViewChevronReviews2.setImageDrawable(getResources().getDrawable(R.drawable.ic_keyboard_arrow_down_black_24dp));
                                    ((AnimatedVectorDrawable) imageViewChevronReviews2.getDrawable()).start();
                                } else {
                                    imageViewChevronReviews1.setImageResource(R.drawable.ic_keyboard_arrow_up_black_24dp);
                                    imageViewChevronReviews2.setImageResource(R.drawable.ic_keyboard_arrow_up_black_24dp);
                                }
                            }, duration);
                        }
                    });
                } else {
                    linearLayoutReviewsChild.setClickable(false);
                    linearLayoutReviewsChild.setFocusable(false);
                    imageViewChevronReviews1.setImageResource(android.R.color.transparent);
                    imageViewChevronReviews2.setImageResource(android.R.color.transparent);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            cursor.close();
        }
    }

    private void getMovieVideosInDB() {
        ContentResolver contentResolver = getContentResolver();
        String selection = FavoritesProvider.Favorites.COL_ID + " =?";
        String[] selectionArgs = {String.valueOf(gsonMovie.getId())};
        Cursor cursor = contentResolver.query(FavoritesProvider.CONTENT_URI, null, selection, selectionArgs, null);

        if (cursor != null) {
            cursor.moveToFirst();
            try {
                JSONObject jsonObject = new JSONObject(cursor.getString(2));
                Gson gson = new Gson();
                GsonVideos gsonVideos = gson.fromJson(String.valueOf(jsonObject), GsonVideos.class);
                videosResultsItemList.addAll(gsonVideos.getResults());
                videosAdapter.notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            cursor.close();
        }
    }

    public boolean checkIfMovieIsInDB() {
        ContentResolver contentResolver = getContentResolver();
        String[] columns = {FavoritesProvider.Favorites.COL_ID};
        String selection = FavoritesProvider.Favorites.COL_ID + " =?";
        String[] selectionArgs = {String.valueOf(gsonMovie.getId())};
        String limit = "1";
        Cursor cursor = contentResolver.query(FavoritesProvider.CONTENT_URI, columns, selection, selectionArgs, limit);
        if (cursor != null) {
            if (cursor.getCount() == 0) {
                cursor.close();
                return false;
            } else {
                cursor.close();
                return true;
            }
        }
        return false;
    }


    private void insertMovieInDB() {
        try {
            ContentValues contentValues = new ContentValues();

            contentValues.put(FavoritesProvider.Favorites.COL_ID, gsonMovie.getId());

            Gson gson = new Gson();
            JSONObject jsonObjectMovie = new JSONObject(gson.toJson(gsonMovie, MovieResultsItem.class));
            contentValues.put(FavoritesProvider.Favorites.COL_GSON_MOVIE, jsonObjectMovie.toString());

            JSONObject jsonObjectVideos = new JSONObject(gson.toJson(gsonVideos, GsonVideos.class));
            contentValues.put(FavoritesProvider.Favorites.COL_GSON_VIDEOS, jsonObjectVideos.toString());

            JSONObject jsonObjectReviews = new JSONObject(gson.toJson(gsonReviews, GsonReviews.class));
            contentValues.put(FavoritesProvider.Favorites.COL_GSON_REVIEWS, jsonObjectReviews.toString());

            ContentResolver contentResolver = getContentResolver();
            contentResolver.insert(FavoritesProvider.CONTENT_URI, contentValues);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void deleteMovieInDB() {
        ContentResolver contentResolver = getContentResolver();
        String selection = FavoritesProvider.Favorites.COL_ID + " =?";
        String[] selectionArgs = {String.valueOf(gsonMovie.getId())};
        contentResolver.delete(FavoritesProvider.CONTENT_URI, selection, selectionArgs);
    }

    private void initRecyclerView() {
        videosAdapter = new VideosAdapter(videosResultsItemList, this);
        LinearLayoutManager linearLayoutManagerVideos = new LinearLayoutManager(this);
        recyclerViewVideos.setHasFixedSize(true);
        recyclerViewVideos.setLayoutManager(linearLayoutManagerVideos);
        recyclerViewVideos.setItemAnimator(new DefaultItemAnimator());
        recyclerViewVideos.setAdapter(videosAdapter);

        reviewsAdapter = new ReviewsAdapter(reviewsResultsItemList, this);
        LinearLayoutManager linearLayoutManagerReviews = new LinearLayoutManager(this);
        recyclerViewReviews.setHasFixedSize(true);
        recyclerViewReviews.setLayoutManager(linearLayoutManagerReviews);
        recyclerViewReviews.setItemAnimator(new DefaultItemAnimator());
        recyclerViewReviews.setAdapter(reviewsAdapter);
    }

    private void getGsonFromIntent() {
        Gson gson = new Gson();
        String gson_movie = getIntent().getStringExtra("GSON_MOVIE");
        gsonMovie = gson.fromJson(gson_movie, MovieResultsItem.class);
    }

    private void populateUI() {

        GlideApp.with(this)
                .load(api_image_base_url + api_image_w780_url + gsonMovie.getBackdropPath())
                .transition(DrawableTransitionOptions.withCrossFade())
                .centerCrop()
                .placeholder(getResources().getDrawable(R.drawable.placeholder))
                .into(imageViewBackdrop);


        GlideApp.with(this)
                .asBitmap()
                .load(api_image_base_url + api_image_w185_url + gsonMovie.getPosterPath())
                .placeholder(getResources().getDrawable(R.drawable.placeholder))
                .listener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                        onPalette(Palette.from(resource).generate());
                        imageViewPoster.setImageBitmap(resource);
                        return false;
                    }

                    void onPalette(Palette palette) {
                        if (null != palette) {
                            floatingActionButtonFavorite.setBackgroundTintList(ColorStateList.valueOf(palette.getMutedColor(palette.getVibrantColor(palette.getDarkMutedColor(getResources().getColor(R.color.colorPrimaryDark))))));
                            linearLayoutDetailActivityBackground.setBackgroundColor(ColorUtils.setAlphaComponent(palette.getDarkVibrantColor(palette.getVibrantColor(palette.getDarkMutedColor(getResources().getColor(R.color.colorPrimaryDark)))), 128));
                            collapsingToolbarLayout.setStatusBarScrimColor(palette.getDarkVibrantColor(palette.getVibrantColor(palette.getDarkMutedColor(getResources().getColor(R.color.colorPrimaryDark)))));
                            collapsingToolbarLayout.setContentScrimColor(palette.getDarkVibrantColor(palette.getVibrantColor(palette.getDarkMutedColor(getResources().getColor(R.color.colorPrimary)))));
                            imageViewChevronReviews1.setColorFilter(palette.getDarkVibrantColor(palette.getVibrantColor(palette.getDarkMutedColor(getResources().getColor(R.color.colorPrimaryDark)))));
                            textViewReviewLabel.setTextColor(palette.getDarkVibrantColor(palette.getVibrantColor(palette.getDarkMutedColor(getResources().getColor(R.color.colorPrimaryDark)))));
                            imageViewChevronReviews2.setColorFilter(palette.getDarkVibrantColor(palette.getVibrantColor(palette.getDarkMutedColor(getResources().getColor(R.color.colorPrimaryDark)))));
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                getWindow().setNavigationBarColor(ColorUtils.setAlphaComponent(palette.getDarkVibrantColor(palette.getVibrantColor(palette.getDarkMutedColor(getResources().getColor(R.color.colorPrimaryDark)))), 212));
                            }
                        }
                    }
                }).into(imageViewPoster);

        textViewTitle.setText(gsonMovie.getTitle());

        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault());
            Date date = simpleDateFormat.parse(gsonMovie.getReleaseDate());
            textViewReleaseDate.setText(dateFormat.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
            textViewReleaseDate.setText(gsonMovie.getReleaseDate());
        }

        for (Locale locale : Locale.getAvailableLocales()) {
            if (locale.getLanguage().equals(gsonMovie.getOriginalLanguage()) && !localeFound) {
                textViewOriginalLanguage.setText(StringUtils.capitalize(locale.getDisplayName()));
                localeFound = true;
            } else if (!localeFound) {
                textViewOriginalLanguage.setText(gsonMovie.getOriginalLanguage().toUpperCase());
                localeFound = false;
            }
        }

        textViewVoteCount.setText(MessageFormat.format("{0}/10" + "\n{1} votes", String.valueOf(gsonMovie.getVoteAverage()), String.valueOf(gsonMovie.getVoteCount())));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            textViewOverview.setJustificationMode(Layout.JUSTIFICATION_MODE_INTER_WORD);
        }
        textViewOverview.setText(gsonMovie.getOverview());
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            appBarLayout.setExpanded(false, true);
            nestedScrollView.setNestedScrollingEnabled(false);
        } else {
            appBarLayout.setExpanded(true, true);
            nestedScrollView.setNestedScrollingEnabled(true);
        }

        final int acumulateScroll[] = new int[2];
        acumulateScroll[0] = 0;
        acumulateScroll[1] = 0;
        nestedScrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            Animation animationHide = new TranslateAnimation(0, 0, 0, -linearLayoutReviews.getHeight());
            animationHide.setInterpolator(new AnticipateOvershootInterpolator(tension));
            animationHide.setDuration(glide_duration);

            Animation animationShow = new TranslateAnimation(0, 0, -linearLayoutReviews.getHeight(), 0);
            animationShow.setInterpolator(new AnticipateOvershootInterpolator(tension));
            animationShow.setDuration(glide_duration);

            if (NetworkUtils.networkAvailable(getApplicationContext())) {
                if (scrollY >= oldScrollY) {
                    acumulateScroll[0] = acumulateScroll[0] + scrollY - oldScrollY;
                    acumulateScroll[1] = 0;
                } else {
                    acumulateScroll[0] = 0;
                    acumulateScroll[1] = acumulateScroll[1] + oldScrollY - scrollY;
                }

                if (acumulateScroll[0] >= scroll_to_animate) {
                    acumulateScroll[0] = 0;
                    acumulateScroll[1] = 0;
                    if (linearLayoutReviews.getVisibility() == View.VISIBLE) {
                        floatingActionButtonFavorite.hide();
                        linearLayoutReviews.startAnimation(animationHide);
                        linearLayoutReviews.setVisibility(View.GONE);
                    }
                }

                if (acumulateScroll[1] >= scroll_to_animate) {
                    acumulateScroll[0] = 0;
                    acumulateScroll[1] = 0;
                    if (linearLayoutReviews.getVisibility() == View.GONE) {
                        floatingActionButtonFavorite.show();
                        linearLayoutReviews.startAnimation(animationShow);
                        linearLayoutReviews.setVisibility(View.VISIBLE);
                    }
                }
            } else if (linearLayoutReviews.getVisibility() == View.GONE) {
                floatingActionButtonFavorite.show();
                linearLayoutReviews.startAnimation(animationShow);
                linearLayoutReviews.setVisibility(View.VISIBLE);
            }
        });

        initRecyclerView();

        initFloatingActionButton();
    }

    private void initFloatingActionButton() {
        if (!checkIfMovieIsInDB()) {
            floatingActionButtonFavorite.setImageResource(R.drawable.ic_star_border_black_24dp);
        } else {
            floatingActionButtonFavorite.setImageResource(R.drawable.ic_star_black_24dp);
        }

        floatingActionButtonFavorite.setOnClickListener(v -> {
            if (!checkIfMovieIsInDB()) {
                floatingActionButtonFavorite.setImageResource(R.drawable.ic_star_black_24dp);
                Snackbar.make(floatingActionButtonFavorite, R.string.label_added_to_favorites, Snackbar.LENGTH_SHORT).show();
                insertMovieInDB();
            } else {
                floatingActionButtonFavorite.setImageResource(R.drawable.ic_star_border_black_24dp);
                deleteMovieInDB();
                Snackbar.make(floatingActionButtonFavorite, R.string.label_removed_from_favorites, Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    private void simpleVolleyRequest(String urlVideos, String urlReviews) {
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequestVideos = new StringRequest(Request.Method.GET, urlVideos,
                response -> {
                    try {
                        JSONObject jsonObjectAPIResponse = new JSONObject(response);
                        Gson gson = new Gson();
                        gsonVideos = gson.fromJson(String.valueOf(jsonObjectAPIResponse), GsonVideos.class);
                        videosResultsItemList.addAll(gsonVideos.getResults());
                        videosAdapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> {
        });
        StringRequest stringRequestReviews = new StringRequest(Request.Method.GET, urlReviews,
                response -> {
                    try {
                        JSONObject jsonObjectAPIResponse = new JSONObject(response);
                        Gson gson = new Gson();
                        gsonReviews = gson.fromJson(String.valueOf(jsonObjectAPIResponse), GsonReviews.class);
                        reviewsResultsItemList.addAll(gsonReviews.getResults());
                        reviewsAdapter.notifyDataSetChanged();
                        textViewReviewLabel.setText(MessageFormat.format("{0} {1}", reviewsResultsItemList.size(), getResources().getString(R.string.label_reviews)));

                        if (reviewsResultsItemList.size() != 0) {
                            linearLayoutReviewsChild.setOnClickListener(v -> {
                                if (linearLayoutReviewsChild2.getVisibility() == View.VISIBLE) {
                                    linearLayoutReviewsChild2.setVisibility(View.GONE);
                                    floatingActionButtonFavorite.show();
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                        imageViewChevronReviews1.setImageDrawable(getResources().getDrawable(R.drawable.ic_keyboard_arrow_up_black_24dp));
                                        ((AnimatedVectorDrawable) imageViewChevronReviews1.getDrawable()).start();
                                        imageViewChevronReviews2.setImageDrawable(getResources().getDrawable(R.drawable.ic_keyboard_arrow_up_black_24dp));
                                        ((AnimatedVectorDrawable) imageViewChevronReviews2.getDrawable()).start();
                                    } else {
                                        imageViewChevronReviews1.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24dp);
                                        imageViewChevronReviews2.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24dp);
                                    }
                                } else {
                                    int duration;
                                    if (isAppBarExpanded(appBarLayout)) {
                                        duration = 300;
                                    } else {
                                        duration = 0;
                                    }
                                    appBarLayout.setExpanded(false, true);
                                    final Handler handler = new Handler();
                                    handler.postDelayed(() -> {
                                        linearLayoutReviewsChild2.setVisibility(View.VISIBLE);
                                        floatingActionButtonFavorite.hide();
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                            imageViewChevronReviews1.setImageDrawable(getResources().getDrawable(R.drawable.ic_keyboard_arrow_down_black_24dp));
                                            ((AnimatedVectorDrawable) imageViewChevronReviews1.getDrawable()).start();
                                            imageViewChevronReviews2.setImageDrawable(getResources().getDrawable(R.drawable.ic_keyboard_arrow_down_black_24dp));
                                            ((AnimatedVectorDrawable) imageViewChevronReviews2.getDrawable()).start();
                                        } else {
                                            imageViewChevronReviews1.setImageResource(R.drawable.ic_keyboard_arrow_up_black_24dp);
                                            imageViewChevronReviews2.setImageResource(R.drawable.ic_keyboard_arrow_up_black_24dp);
                                        }
                                    }, duration);
                                }
                            });
                        } else {
                            linearLayoutReviewsChild.setClickable(false);
                            linearLayoutReviewsChild.setFocusable(false);
                            imageViewChevronReviews1.setImageResource(android.R.color.transparent);
                            imageViewChevronReviews2.setImageResource(android.R.color.transparent);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> {
        });
        queue.add(stringRequestVideos);
        queue.add(stringRequestReviews);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        floatingActionButtonFavorite.setVisibility(View.GONE);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            appBarLayout.setExpanded(false, false);
            nestedScrollView.setNestedScrollingEnabled(false);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            appBarLayout.setExpanded(true);
            nestedScrollView.setNestedScrollingEnabled(true);
        }
    }

    public boolean isAppBarExpanded(AppBarLayout appBarLayout) {
        final CoordinatorLayout.Behavior behavior = ((CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams()).getBehavior();
        return (behavior instanceof AppBarLayout.Behavior) && (((AppBarLayout.Behavior) behavior).getTopAndBottomOffset() == 0);
    }
}
