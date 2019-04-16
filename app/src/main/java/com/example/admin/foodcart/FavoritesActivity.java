package com.example.admin.foodcart;

import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.RelativeLayout;

import com.example.admin.foodcart.Common.Common;
import com.example.admin.foodcart.Database.Database;
import com.example.admin.foodcart.Helper.RecyclerItemTouchHelper;
import com.example.admin.foodcart.Interface.RecyclerItemTouchHelperListener;
import com.example.admin.foodcart.Model.Favorites;
import com.example.admin.foodcart.Model.Order;
import com.example.admin.foodcart.ViewHolder.FavoritesAdapter;
import com.example.admin.foodcart.ViewHolder.FavoritesViewHolder;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class FavoritesActivity extends AppCompatActivity implements RecyclerItemTouchHelperListener {
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    FavoritesAdapter adapter;
    RelativeLayout relativeLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);
        relativeLayout = (RelativeLayout)findViewById(R.id.root_layout);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_food);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(recyclerView.getContext(),R.anim.layout_fall_down);
        recyclerView.setLayoutAnimation(controller);
        ItemTouchHelper.SimpleCallback itemTouchHelperCallBack = new RecyclerItemTouchHelper(0,ItemTouchHelper.LEFT,this);
        new ItemTouchHelper(itemTouchHelperCallBack).attachToRecyclerView(recyclerView);
        loadFavorites();
    }

    private void loadFavorites() {
        adapter = new FavoritesAdapter(this,new Database(this).getAllFavorites(Common.currentUser.getPhone()));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
     if (viewHolder instanceof FavoritesViewHolder){
         String name = ((FavoritesAdapter)recyclerView.getAdapter()).getitem(position).getFoodName();
         final Favorites deleteitem = ((FavoritesAdapter)recyclerView.getAdapter()).getitem(viewHolder.getAdapterPosition());
         final int deleteindex= viewHolder.getAdapterPosition();
         adapter.removeitem(viewHolder.getAdapterPosition());
         new Database(getBaseContext()).removeFromFavorites(deleteitem.getFoodId(),Common.currentUser.getPhone());
         Snackbar snackbar = Snackbar.make(relativeLayout,name+"removed from cart",Snackbar.LENGTH_SHORT);
         snackbar.setAction("UNDO", new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 adapter.restoreitem(deleteitem,deleteindex);
                 new Database(getBaseContext()).addToFavorites(deleteitem);

             }
         });
         snackbar.setActionTextColor(Color.YELLOW);
         snackbar.show();
     }
    }
}
