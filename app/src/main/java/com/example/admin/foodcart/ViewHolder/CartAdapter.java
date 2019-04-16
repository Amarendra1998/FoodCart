package com.example.admin.foodcart.ViewHolder;

import android.content.Context;
import android.graphics.Color;
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

import com.amulyakhare.textdrawable.TextDrawable;
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

/*class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
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

    public CartViewHolder(@NonNull View itemView) {
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
}*/
   public class CartAdapter extends RecyclerView.Adapter<CartViewHolders>{
    private List<Order>listdata = new ArrayList<>();
    private Cart cart;

    public CartAdapter(List<Order> listdata, Cart cart) {
        this.listdata = listdata;
        this.cart = cart;
    }

    @NonNull
    @Override
    public CartViewHolders onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(cart);
        View itemview = inflater.inflate(R.layout.cart_layout,viewGroup,false);
        return new CartViewHolders(itemview);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolders cartViewHolder, int position) {
      //  TextDrawable drawable = TextDrawable.builder().buildRound(""+listdata.get(position).getQuantity(),Color.RED);
        //cartViewHolder.btn_quantity.setImageDrawable(drawable);
        Picasso.with(cart.getBaseContext())
                .load(listdata.get(position).getImage())
                .resize(70,70)
                .centerCrop()
                .into(cartViewHolder.cart_image);
        cartViewHolder.btn_quantity.setNumber(listdata.get(position).getQuantity());
        cartViewHolder.btn_quantity.setOnValueChangeListener(new ElegantNumberButton.OnValueChangeListener() {
            @Override
            public void onValueChange(ElegantNumberButton view, int oldValue, int newValue) {
                Order order = listdata.get(newValue);
                order.setQuantity(String.valueOf(newValue));
                new Database(cart).updateCart(order);

                int total = 0;
                List<Order> orders = new Database(cart).getCarts(Common.currentUser.getPhone());
                for (Order item : orders)
                    try{
                        total+=(Integer.parseInt(order.getPrice()))*(Integer.parseInt(item.getQuantity()));
                    }catch(NumberFormatException ex) {
                        ex.printStackTrace();
                    }
                Locale locale = new Locale("en", "US");
                NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
                cart.txtTotalPrice.setText(fmt.format(total));
            }
        });
        Locale locale = new Locale("en","US");
        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
        int price = 0;
            try{
                 price = (Integer.parseInt(listdata.get(position).getPrice()))*(Integer.parseInt(listdata.get(position).getQuantity()));
            }catch(NumberFormatException ex) {
                ex.printStackTrace();
            }
        cartViewHolder.txt_price.setText(fmt.format(price));
        cartViewHolder.txt_cart_name.setText(listdata.get(position).getProductName());
    }

    @Override
    public int getItemCount() {
        return listdata.size();
    }
    public void removeitem(int position)
    {
        listdata.remove(position);
        notifyItemRemoved(position);
    }
    public void restoreitem(Order item,int position)
    {
        listdata.add(position,item);
        notifyItemInserted(position);
    }

    public Order getItem(int adapterPosition) {
        return listdata.get(adapterPosition);
    }
}
