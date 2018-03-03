package com.videumcorp.popularmoviesstage2.Adapters;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.google.gson.Gson;
import com.videumcorp.popularmoviesstage2.DetailActivity;
import com.videumcorp.popularmoviesstage2.GlideApp;
import com.videumcorp.popularmoviesstage2.Gsons.Movie.MovieResultsItem;
import com.videumcorp.popularmoviesstage2.R;

import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MyViewHolder> {

    private final List<MovieResultsItem> movieList;
    private final Activity activity;
    private BottomNavigationView bottomNavigationView;
    private static final int glide_duration = 500;
    private static final float tension = 1.5f;

    class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.frameLayoutMovie)
        FrameLayout frameLayoutMovie;
        @BindView(R.id.imageViewPoster)
        ImageView imageViewPoster;
        @BindString(R.string.api_image_base_url)
        String api_image_base_url;
        @BindString(R.string.api_image_w185_url)
        String api_image_w185_url;

        MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public MoviesAdapter(List<MovieResultsItem> moviesList, Activity activity) {
        this.movieList = moviesList;
        this.activity = activity;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        final MovieResultsItem movie = movieList.get(position);

        GlideApp.with(holder.itemView.getContext())
                .load(holder.api_image_base_url + holder.api_image_w185_url + movie.getPosterPath())
                .transition(DrawableTransitionOptions.withCrossFade(glide_duration))
                .centerCrop()
                .placeholder(holder.itemView.getContext().getResources().getDrawable(R.drawable.placeholder))
                .into(holder.imageViewPoster);

        holder.frameLayoutMovie.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), DetailActivity.class);
            Gson gson = new Gson();
            intent.putExtra("GSON_MOVIE", gson.toJson(movie));
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    activity,
                    holder.imageViewPoster,
                    holder.itemView.getContext().getResources().getString(R.string.label_movie_poster));

            bottomNavigationView = activity.findViewById(R.id.bottomNavigationView);
            Animation animationHide = new TranslateAnimation(0, 0, 0, bottomNavigationView.getHeight());
            animationHide.setInterpolator(new AnticipateOvershootInterpolator(tension));
            animationHide.setDuration(glide_duration/2);
            if (bottomNavigationView.getVisibility() == View.VISIBLE) {
                bottomNavigationView.startAnimation(animationHide);
                bottomNavigationView.setVisibility(View.GONE);
                animationHide.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            holder.itemView.getContext().startActivity(intent, options.toBundle());
                        } else {
                            holder.itemView.getContext().startActivity(intent);
                        }
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    holder.itemView.getContext().startActivity(intent, options.toBundle());
                } else {
                    holder.itemView.getContext().startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }
}