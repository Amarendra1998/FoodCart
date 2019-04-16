package com.example.admin.foodcart.Interface;

import android.support.v7.widget.RecyclerView;

public interface RecyclerItemTouchHelper {
  void onSwiped(RecyclerView.ViewHolder viewHolder,int direction,int position);
}
