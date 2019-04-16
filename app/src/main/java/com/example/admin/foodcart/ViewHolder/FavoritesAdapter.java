package com.example.admin.foodcart.ViewHolder;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.admin.foodcart.Common.Common;
import com.example.admin.foodcart.Database.Database;
import com.example.admin.foodcart.FoodDetail;
import com.example.admin.foodcart.FoodList;
import com.example.admin.foodcart.Interface.ItemClickListener;
import com.example.admin.foodcart.Model.Favorites;
import com.example.admin.foodcart.Model.Food;
import com.example.admin.foodcart.Model.Order;
import com.example.admin.foodcart.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import static com.example.admin.foodcart.Common.Common.currentUser;
import static com.facebook.accountkit.internal.AccountKitController.getApplicationContext;

public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesViewHolder> {
    private Context context;
    private List<Favorites>favoritesList;

    public FavoritesAdapter(Context context, List<Favorites> favoritesList) {
        this.context = context;
        this.favoritesList = favoritesList;
    }

    public List<Favorites> getFavoritesList() {
        return favoritesList;
    }

    public void setFavoritesList(List<Favorites> favoritesList) {
        this.favoritesList = favoritesList;
    }

    @NonNull
    @Override
    public FavoritesViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemview = LayoutInflater.from(context).inflate(R.layout.favorites_item,viewGroup,false);
        return new FavoritesViewHolder(itemview);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoritesViewHolder viewHolder, final int position) {
        viewHolder.food_name.setText(favoritesList.get(position).getFoodName());
        viewHolder.food_price.setText(String.format("$ %s", favoritesList.get(position).getFoodPrice()));
        Picasso.with(context).load(favoritesList.get(position).getFoodImage()).into(viewHolder.food_image);
        final boolean isExists =  new Database(context).checkFoodExists(favoritesList.get(position).getFoodId(),currentUser.getPhone());

        viewHolder.quickCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isExists) {
                    new Database(context).addToCart(new Order(
                            currentUser.getPhone(),
                            favoritesList.get(position).getFoodId(),
                            favoritesList.get(position).getFoodName(),
                            "1",
                            favoritesList.get(position).getFoodPrice(),
                            favoritesList.get(position).getFoodDiscount(),
                            favoritesList.get(position).getFoodImage()
                    ));
                } else {
                    new Database(context).increaseCart(currentUser.getPhone(), favoritesList.get(position).getFoodId());

                }
                Toast.makeText(context, "Added to cart", Toast.LENGTH_SHORT).show();
            }
        });




        final Favorites local = favoritesList.get(position);
        viewHolder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick (View view,int Posiition, boolean islongclick){
                Intent fooddetail = new Intent(context, FoodDetail.class);
                fooddetail.putExtra("FoodId", favoritesList.get(position).getFoodId());
                context.startActivity(fooddetail);
            }
        });
    }


    @Override
    public int getItemCount() {
        return favoritesList.size();
    }
    public void removeitem(int position)
    {
        favoritesList.remove(position);
        notifyItemRemoved(position);
    }
    public void restoreitem(Favorites item,int position)
    {
        favoritesList.add(position,item);
        notifyItemInserted(position);
    }
    public Favorites getitem(int position){
        return favoritesList.get(position);
    }
}
