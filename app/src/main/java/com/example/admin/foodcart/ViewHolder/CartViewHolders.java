package com.example.admin.foodcart.ViewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.admin.foodcart.Cart;
import com.example.admin.foodcart.Common.Common;
import com.example.admin.foodcart.Database.Database;
import com.example.admin.foodcart.Interface.ItemClickListener;
import com.example.admin.foodcart.Model.Order;
import com.example.admin.foodcart.R;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CartViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener
            ,View.OnCreateContextMenuListener {
        public TextView txt_cart_name, txt_price;
        public ElegantNumberButton btn_quantity;
        public ImageView cart_image;
        public RelativeLayout view_background;
        public LinearLayout view_foreground;
        private ItemClickListener itemClickListener;

        public void setTxt_cart_name(TextView txt_cart_name) {
            this.txt_cart_name = txt_cart_name;
        }

        public CartViewHolders(@NonNull View itemView) {
            super(itemView);
            txt_cart_name =(TextView)itemView.findViewById(R.id.cart_item_name);
            txt_price =(TextView)itemView.findViewById(R.id.cart_item_price);
            btn_quantity =(ElegantNumberButton) itemView.findViewById(R.id.btn_quantity);
            cart_image =(ImageView) itemView.findViewById(R.id.cart_image);
            view_background = (RelativeLayout)itemView.findViewById(R.id.view_background);
            view_foreground = (LinearLayout) itemView.findViewById(R.id.view_foreground);
            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onClick(View v) {

        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.setHeaderTitle("Select action");
            menu.add(0,0,getAdapterPosition(),Common.DELETE);
        }
    }


