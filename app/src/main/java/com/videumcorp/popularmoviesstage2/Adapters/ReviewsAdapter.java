package com.videumcorp.popularmoviesstage2.Adapters;

import android.app.Activity;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.videumcorp.popularmoviesstage2.Gsons.Reviews.ReviewsResultsItem;
import com.videumcorp.popularmoviesstage2.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.MyViewHolder> {

    private final List<ReviewsResultsItem> reviewsResultsItem;
    private final Activity activity;

    class MyViewHolder extends RecyclerView.ViewHolder {

        //VIEWS
        @BindView(R.id.cardViewReview)
        CardView cardViewReview;
        @BindView(R.id.textViewAuthor)
        TextView textViewAuthor;
        @BindView(R.id.textViewContent)
        TextView textViewContent;

        MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public ReviewsAdapter(List<ReviewsResultsItem> reviewsResultsItem, Activity activity) {
        this.reviewsResultsItem = reviewsResultsItem;
        this.activity = activity;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        final ReviewsResultsItem review = reviewsResultsItem.get(position);
        holder.textViewAuthor.setText(review.getAuthor());
        holder.textViewContent.setText(review.getContent());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            holder.textViewContent.setJustificationMode(Layout.JUSTIFICATION_MODE_INTER_WORD);
        }

        TextView textViewReviewLabel = activity.findViewById(R.id.textViewReviewLabel);
        holder.cardViewReview.setCardBackgroundColor(textViewReviewLabel.getTextColors());
    }

    @Override
    public int getItemCount() {
        return reviewsResultsItem.size();
    }
}