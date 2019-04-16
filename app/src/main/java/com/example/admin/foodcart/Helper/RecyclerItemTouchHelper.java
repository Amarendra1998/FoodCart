package com.example.admin.foodcart.Helper;

import android.graphics.Canvas;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import com.example.admin.foodcart.Interface.RecyclerItemTouchHelperListener;
import com.example.admin.foodcart.Model.Favorites;
import com.example.admin.foodcart.ViewHolder.CartAdapter;
import com.example.admin.foodcart.ViewHolder.CartViewHolders;
import com.example.admin.foodcart.ViewHolder.FavoritesViewHolder;

import java.util.Objects;

public class RecyclerItemTouchHelper extends ItemTouchHelper.SimpleCallback {
     private RecyclerItemTouchHelperListener listener;

    public RecyclerItemTouchHelper(int dragDirs, int swipeDirs, RecyclerItemTouchHelperListener listener) {
        super(dragDirs, swipeDirs);
        this.listener = listener;
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
        return true;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
       if (listener!=null)
           listener.onSwiped(viewHolder,direction,viewHolder.getAdapterPosition());
    }

    @Override
    public int convertToAbsoluteDirection(int flags, int layoutDirection) {

        return super.convertToAbsoluteDirection(flags, layoutDirection);
    }

    @Override
    public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        if (viewHolder instanceof CartViewHolders) {
            View foreground = ((CartViewHolders) viewHolder).view_foreground;
            getDefaultUIUtil().clearView(foreground);
        }else if (viewHolder instanceof FavoritesViewHolder){
            View foreground = ((FavoritesViewHolder) viewHolder).view_foreground;
            getDefaultUIUtil().clearView(foreground);
        }
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        if (viewHolder instanceof CartViewHolders) {
            View foreground = ((CartViewHolders) viewHolder).view_background;
            getDefaultUIUtil().onDraw(c, recyclerView, foreground, dX, dY, actionState, isCurrentlyActive);
        }else if (viewHolder instanceof FavoritesViewHolder){
            View foreground = ((FavoritesViewHolder) viewHolder).view_background;
            getDefaultUIUtil().onDraw(c, recyclerView, foreground, dX, dY, actionState, isCurrentlyActive);
        }
    }

    @Override
    public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
        super.onSelectedChanged(viewHolder, actionState);
        if (viewHolder!=null)
        {
            if (viewHolder instanceof CartViewHolders) {
                View foregroundview = ((CartViewHolders) viewHolder).view_foreground;
                getDefaultUIUtil().onSelected(foregroundview);
            }else if (viewHolder instanceof FavoritesViewHolder){
                View foregroundview = ((FavoritesViewHolder) viewHolder).view_foreground;
                getDefaultUIUtil().onSelected(foregroundview);
            }
        }
    }

    @Override
    public void onChildDrawOver(@NonNull Canvas c, @NonNull RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        if (viewHolder instanceof CartViewHolders) {
            View foreground = ((CartViewHolders) viewHolder).view_foreground;
            getDefaultUIUtil().onDrawOver(c, recyclerView, foreground, dX, dY, actionState, isCurrentlyActive);
        }else if (viewHolder instanceof FavoritesViewHolder){
            View foreground = ((FavoritesViewHolder) viewHolder).view_foreground;
            getDefaultUIUtil().onDrawOver(c, recyclerView, foreground, dX, dY, actionState, isCurrentlyActive);
        }
    }
}
