package com.example.admin.foodcart.ViewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.admin.foodcart.R;

public class ShowCommentViewHolder extends RecyclerView.ViewHolder {
    public TextView txtUserPhone,txtComment;
    public RatingBar ratingBar;
    public ShowCommentViewHolder(@NonNull View itemView) {
        super(itemView);
        txtUserPhone = (TextView)itemView.findViewById(R.id.txtUserPhone);
        txtComment = (TextView)itemView.findViewById(R.id.txtComment);
        ratingBar = (RatingBar) itemView.findViewById(R.id.ratingbar);

    }
}
