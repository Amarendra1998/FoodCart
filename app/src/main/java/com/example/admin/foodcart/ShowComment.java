package com.example.admin.foodcart;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.admin.foodcart.Common.Common;
import com.example.admin.foodcart.Model.Rating;
import com.example.admin.foodcart.ViewHolder.ShowCommentViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class ShowComment extends AppCompatActivity {
   RecyclerView recyclerView;
   RecyclerView.LayoutManager layoutManager;
   FirebaseDatabase database;
   DatabaseReference ratingTb1;
   SwipeRefreshLayout swipeRefreshLayout;
   FirebaseRecyclerAdapter<Rating,ShowCommentViewHolder>adapter;
   String foodId="";
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (adapter!=null){
            adapter.stopListening();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_comment);

        database = FirebaseDatabase.getInstance();
        ratingTb1 = database.getReference("Rating");
        recyclerView = (RecyclerView)findViewById(R.id.recyclerComment);
        layoutManager= new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (getIntent()!=null){
                    foodId = getIntent().getStringExtra(Common.INTENT_FOOD_ID);
                    if (!foodId.isEmpty()&&foodId!=null){
                        Query query = ratingTb1.orderByChild("foodId").equalTo(foodId);
                        FirebaseRecyclerOptions<Rating>options= new FirebaseRecyclerOptions.Builder<Rating>()
                                .setQuery(query,Rating.class)
                                .build();

                        adapter = new FirebaseRecyclerAdapter<Rating, ShowCommentViewHolder>(options) {
                            @Override
                            protected void onBindViewHolder(@NonNull ShowCommentViewHolder holder, int position, @NonNull Rating model) {
                               holder.ratingBar.setRating(Float.parseFloat(model.getRateValue()));
                               holder.txtComment.setText(model.getComment());
                               holder.txtUserPhone.setText(model.getUserPhone());
                            }

                            @NonNull
                            @Override
                            public ShowCommentViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.show_comment_layout,viewGroup,false);
                                return new ShowCommentViewHolder(view);
                            }
                        };
                        loadcomment(foodId);
                    }
                }
            }
        });
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                if (getIntent() != null) {
                    foodId = getIntent().getStringExtra(Common.INTENT_FOOD_ID);
                    if (!foodId.isEmpty() && foodId != null) {
                        Query query = ratingTb1.orderByChild("foodId").equalTo(foodId);
                        FirebaseRecyclerOptions<Rating> options = new FirebaseRecyclerOptions.Builder<Rating>()
                                .setQuery(query, Rating.class)
                                .build();

                        adapter = new FirebaseRecyclerAdapter<Rating, ShowCommentViewHolder>(options) {
                            @Override
                            protected void onBindViewHolder(@NonNull ShowCommentViewHolder holder, int position, @NonNull Rating model) {
                                holder.ratingBar.setRating(Float.parseFloat(model.getRateValue()));
                                holder.txtComment.setText(model.getComment());
                                holder.txtUserPhone.setText(model.getUserPhone());
                            }

                            @NonNull
                            @Override
                            public ShowCommentViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.show_comment_layout, viewGroup, false);
                                return new ShowCommentViewHolder(view);
                            }
                        };
                        loadcomment(foodId);
                    }
                }
            }

            });
    }


    private void loadcomment(String foodId) {
        adapter.startListening();
        recyclerView.setAdapter(adapter);
        swipeRefreshLayout.setRefreshing(false);
    }
}
