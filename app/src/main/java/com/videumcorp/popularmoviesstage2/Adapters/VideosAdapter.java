package com.videumcorp.popularmoviesstage2.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.videumcorp.popularmoviesstage2.GlideApp;
import com.videumcorp.popularmoviesstage2.Gsons.Videos.VideosResultsItem;
import com.videumcorp.popularmoviesstage2.R;

import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

public class VideosAdapter extends RecyclerView.Adapter<VideosAdapter.MyViewHolder> {

    private final List<VideosResultsItem> videosResultsItemList;
    private final Activity activity;
    private static final int glide_duration = 500;

    class MyViewHolder extends RecyclerView.ViewHolder {

        //VIEWS
        @BindView(R.id.relativeLayoutVideo)
        RelativeLayout relativeLayoutVideo;
        @BindView(R.id.imageViewYouTubeThumbnail)
        ImageView imageViewYouTubeThumbnail;
        @BindView(R.id.frameLayoutUpperDivider)
        FrameLayout frameLayoutUpperDivider;

        //STRINGS
        @BindString(R.string.api_image_base_url)
        String api_image_base_url;
        @BindString(R.string.api_image_w185_url)
        String api_image_w185_url;
        @BindString(R.string.youtube_thumbnail_base_url)
        String youtube_thumbnail_base_url;
        @BindString(R.string.youtube_thumbnail_end_url)
        String youtube_thumbnail_end_url;
        @BindString(R.string.youtube_base_url)
        String youtube_base_url;

        MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public VideosAdapter(List<VideosResultsItem> videosResultsItemList, Activity activity) {
        this.videosResultsItemList = videosResultsItemList;
        this.activity = activity;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.video_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        final VideosResultsItem video = videosResultsItemList.get(position);

        GlideApp.with(holder.itemView.getContext())
                .load(holder.youtube_thumbnail_base_url + video.getKey() + holder.youtube_thumbnail_end_url)
                .transition(DrawableTransitionOptions.withCrossFade(glide_duration))
                .centerCrop()
                .placeholder(holder.itemView.getContext().getResources().getDrawable(R.drawable.placeholder_video))
                .into(holder.imageViewYouTubeThumbnail);

        //In this case I don't want to have the first divider
        if (position == 0) {
            holder.frameLayoutUpperDivider.setVisibility(View.GONE);
        } else {
            holder.frameLayoutUpperDivider.setVisibility(View.VISIBLE);
        }

        holder.relativeLayoutVideo.setOnClickListener(v -> watchYoutubeVideo(activity, holder.youtube_base_url, video.getKey()));
    }

    @Override
    public int getItemCount() {
        return videosResultsItemList.size();
    }

    private static void watchYoutubeVideo(Context context, String youtube_base_url, String id) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(youtube_base_url + id));

        Intent chooser = Intent.createChooser(intent, context.getString(R.string.label_open_with));

        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(chooser);
        }
    }
}