package com.example.admin.foodcart;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.andremion.counterfab.CounterFab;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.admin.foodcart.Common.Common;
import com.example.admin.foodcart.Database.Database;
import com.example.admin.foodcart.Model.Food;
import com.example.admin.foodcart.Model.Order;
import com.example.admin.foodcart.Model.Rating;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.stepstone.apprating.AppRatingDialog;
import com.stepstone.apprating.listener.RatingDialogListener;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class FoodDetail extends AppCompatActivity implements RatingDialogListener {
     private TextView food_name, food_price,food_description;
    private ImageView food_image;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private FloatingActionButton btnRating;
    private CounterFab btncrt;
    private RatingBar ratingBar;
    private ElegantNumberButton elegantNumberButton;
   private String foodId="";
    private FirebaseDatabase database;
    private Button btncomment;
   private DatabaseReference foods;
    private DatabaseReference ratingtbl;
   private Food currentFood;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_detail);
        database = FirebaseDatabase.getInstance();
        foods = database.getReference("Foods");
        ratingtbl = database.getReference("Rating");
        elegantNumberButton = (ElegantNumberButton)findViewById(R.id.number_detail);
        btncrt = (CounterFab) findViewById(R.id.btncrt);
        btnRating =  (FloatingActionButton)findViewById(R.id.btnrating);
        ratingBar = (RatingBar)findViewById(R.id.ratingbar);
        btncomment = (Button)findViewById(R.id.btnComment);
        btncomment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FoodDetail.this,ShowComment.class);
                intent.putExtra(Common.INTENT_FOOD_ID,foodId);
                startActivity(intent);
            }
        });

        btnRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRatingDialog();
            }
        });

        btncrt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                  new Database(getBaseContext()).addToCart(new Order(
                          Common.currentUser.getPhone(),
                          foodId,
                          currentFood.getName(),
                          elegantNumberButton.getNumber(),
                          currentFood.getPrice(),
                          currentFood.getDiscount(),
                          currentFood.getImage()
                  ));
                  Toast.makeText(FoodDetail.this,"Added to cart",Toast.LENGTH_SHORT).show();
            }
        });
        btncrt.setCount(new Database(this).getCartCount(Common.currentUser.getPhone()));
        food_description = (TextView)findViewById(R.id.fooddescription);
        food_name = (TextView)findViewById(R.id.food_name);
        food_price = (TextView)findViewById(R.id.food_price);
        food_image =  (ImageView)findViewById(R.id.img_food);
        collapsingToolbarLayout = (CollapsingToolbarLayout)findViewById(R.id.collapsing);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppbar);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppbar);

        if (getIntent()!=null)
            foodId = getIntent().getStringExtra("FoodId");
        if (!foodId.isEmpty()){
            if (Common.isConnectedToInternet(getBaseContext()))
            {
                getDetailFood(foodId);
                getRatingFood(foodId);
            }
            else {
                Toast.makeText(FoodDetail.this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
                return;
            }
        }

    }

    private void getRatingFood(String foodId) {
        com.google.firebase.database.Query foodRating =  ratingtbl.orderByChild("foodId").equalTo(foodId);
        foodRating.addValueEventListener(new ValueEventListener() {
            int count=0,sum=0;
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                for (DataSnapshot postSnapshot:dataSnapshot.getChildren())
                {
                    Rating item  = postSnapshot.getValue(Rating.class);
                    sum+=Integer.parseInt(item.getRateValue());
                    count++;
                }
                if (count!=0) {
                    float average = sum / count;
                    ratingBar.setRating(average);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void showRatingDialog() {
    new AppRatingDialog.Builder()
            .setPositiveButtonText("Submit")
            .setNegativeButtonText("Cancel")
            .setNoteDescriptions(Arrays.asList("Very bad","Not good","Quite ok","Very good","Excellent"))
            .setDefaultRating(1)
            .setTitle("Rate this food")
            .setDescription("Please select some stars andgive your feedback")
            .setTitleTextColor(R.color.colorPrimary)
            .setDescriptionTextColor(R.color.colorPrimary)
            .setHint("Please write your comment here...")
            .setHintTextColor(R.color.colorAccent)
            .setCommentTextColor(android.R.color.white)
            .setCommentBackgroundColor(R.color.colorPrimaryDark)
            .setWindowAnimation(R.style.RatingDialogFadeAnim)
            .create(FoodDetail.this)
            .show();

    }

    private void getDetailFood(String foodId) {
        foods.child(foodId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                currentFood = dataSnapshot.getValue(Food.class);
                Picasso.with(getBaseContext()).load(currentFood.getImage()).into(food_image);
                food_price.setText(currentFood.getPrice());
                food_name.setText(currentFood.getName());
                food_description.setText(currentFood.getDescription());
                //Toast.makeText(FoodDetail.this,"Added to Cart",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onNegativeButtonClicked() {

    }

    @Override
    public void onPositiveButtonClicked(int value, @NotNull String comments) {
        final Rating rating = new Rating(Common.currentUser.getPhone(),
                foodId,
                String.valueOf(value),
                comments);

        ratingtbl.push().setValue(rating).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(FoodDetail.this,"Thank you for submit rating!!!",Toast.LENGTH_LONG).show();

            }
        });
        /*
        ratingtbl.child(Common.currentUser.getPhone()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(Common.currentUser.getPhone()).exists())
                {
                    ratingtbl.child(Common.currentUser.getPhone()).removeValue();
                    ratingtbl.child(Common.currentUser.getPhone()).setValue(rating);
                }
                else
                {
                    ratingtbl.child(Common.currentUser.getPhone()).setValue(rating);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(FoodDetail.this,"Thank you for submit rating!!!",Toast.LENGTH_LONG).show();

            }
        });*/
    }
}
