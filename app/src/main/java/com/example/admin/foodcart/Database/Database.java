package com.example.admin.foodcart.Database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Log;

import com.example.admin.foodcart.Model.Favorites;
import com.example.admin.foodcart.Model.Food;
import com.example.admin.foodcart.Model.Order;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipInputStream;

public class Database extends SQLiteAssetHelper {
    private static final String DB_Name = "FoodCartDB.db";
    private static final int DB_VER=1;
    public Database(Context context) {
        super(context, DB_Name, null, DB_VER);
    }

    public  int getCartCount(String userPhone) {
        int count = 0;
        try {

            SQLiteDatabase database = getReadableDatabase();
            String query = String.format("SELECT  COUNT(*) FROM OrderDetail WHERE UserPhone='%s'",userPhone);
            Cursor cursor = database.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                do {
                    count = cursor.getInt(0);

                } while (cursor.moveToNext());
            }
            cursor.close();

        }catch (NullPointerException e){
            e.fillInStackTrace();
        }
        return count;
    }
   public boolean checkFoodExists(String foodId,String userPhone) {
       boolean flag = false;
       SQLiteDatabase db = getReadableDatabase();
       Cursor cursor = null;
       String sqlquery = String.format("SELECT *From OrderDetail WHERE UserPhone='%s' AND ProductId='%s'",userPhone,foodId);
       cursor = db.rawQuery(sqlquery,null);
       if (cursor.getCount()>0)
           flag = true;
       else
           flag = false;
       cursor.close();
       return flag;
   }
    public List<Order>getCarts(String userPhone){
        SQLiteDatabase db = getReadableDatabase();
         SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
         String[] sqlselect = {"UserPhone","ProductName","ProductId","Quantity","Price","Discount","Image"};
         String sqltable = "OrderDetail";
         qb.setTables(sqltable);
         Cursor c = qb.query(db,sqlselect,"UserPhone=?",new String[]{userPhone},null,null,null);
         final List<Order> result = new ArrayList<>();
         if (c.moveToFirst()){
             do {
                 result.add(new Order(
                         c.getString(c.getColumnIndex("UserPhone")),
                         c.getString(c.getColumnIndex("ProductId")),
                         c.getString(c.getColumnIndex("ProductName")),
                         c.getString(c.getColumnIndex("Quantity")),
                         c.getString(c.getColumnIndex("Price")),
                         c.getString(c.getColumnIndex("Discount")),
                         c.getString(c.getColumnIndex("Image"))
                         ));
             }while (c.moveToNext());
         }
         return result;
     }
     public void addToCart(Order order){
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("INSERT OR REPLACE INTO OrderDetail(UserPhone,ProductId,ProductName,Quantity,Price,Discount,Image)VALUES('%s','%s','%s','%s','%s','%s','%s');",
                order.getUserPhone(),
                order.getProductId(),
                order.getProductName(),
                order.getQuantity(),
                order.getPrice(),
                order.getDiscount(),
                order.getImage());
        db.execSQL(query);
     }
    public void cleanCart(String userPhone){
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("DELETE FROM OrderDetail WHERE UserPhone='%s'",userPhone);
        db.execSQL(query);
    }

    public boolean isFavorite(String foodId,String userPhone) {
        try {
            SQLiteDatabase database = getReadableDatabase();
            String query = String.format("SELECT * FROM Favorites WHERE FoodId='%s'and UserPhone='%s';", foodId,userPhone);
            Cursor cursor = database.rawQuery(query, null);
            if (cursor.getCount() <= 0) {
                cursor.close();
                return false;
            }
            cursor.close();

        }catch (NullPointerException e){
            e.fillInStackTrace();
        }
        return true;
    }

    public void updateCart(Order order) {
        SQLiteDatabase database = getReadableDatabase();
        String query = String.format("UPDATE OrderDetail SET Quantity=%s WHERE UserPhone=%d AND ProductId='%s'", order.getQuantity(),order.getUserPhone());
        database.execSQL(query);
    }
    public void increaseCart(String userPhone,String foodId) {
        SQLiteDatabase database = getReadableDatabase();
        String query = String.format("UPDATE OrderDetail SET Quantity= Quantity+1 WHERE UserPhone=%d AND ProductId='%s'", userPhone,foodId);
        database.execSQL(query);
    }

    public void addToFavorites(Favorites food) {
        SQLiteDatabase database = getReadableDatabase();
        String query = String.format("INSERT INTO Favorites("+"FoodId,FoodName,FoodPrice,FoodMenuId,FoodImage,FoodDiscount,FoodDescription,UserPhone)"+" VALUES('%s','%s','%s','%s','%s','%s','%s','%s');",
                food.getFoodId(),
                food.getFoodName(),
                food.getFoodPrice(),
                food.getFoodMenuId(),
                food.getFoodImage(),
                food.getFoodDiscount(),
                food.getFoodDescription(),
                food.getUserPhone());
        database.execSQL(query);
    }

    public void removeFromFavorites(String foodId,String userPhone) {
        SQLiteDatabase database = getReadableDatabase();
        String query = String.format("DELETE FROM Favorites WHERE FoodId='%s' and UserPhone='%s';",foodId,userPhone);
        database.execSQL(query);
    }


    public void removeFromCart(String productId, String phone) {
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("DELETE FROM OrderDetail WHERE UserPhone='%s' and ProductId='%s'",phone,productId);
        db.execSQL(query);
    }
    public List<Favorites>getAllFavorites(String userPhone){
        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        String[] sqlselect = {"UserPhone","FoodId","FoodName","FoodPrice","FoodMenuId","FoodImage","FoodDiscount","FoodDescription"};
        String sqltable = "Favorites";
        qb.setTables(sqltable);
        Cursor c = qb.query(db,sqlselect,"UserPhone=?",new String[]{userPhone},null,null,null);
        final List<Favorites> result = new ArrayList<>();
        if (c.moveToFirst()){
            do {
                result.add(new Favorites(
                        c.getString(c.getColumnIndex("FoodId")),
                        c.getString(c.getColumnIndex("FoodName")),
                        c.getString(c.getColumnIndex("FoodPrice")),
                        c.getString(c.getColumnIndex("FoodMenuId")),
                        c.getString(c.getColumnIndex("FoodImage")),
                        c.getString(c.getColumnIndex("FoodDiscount")),
                        c.getString(c.getColumnIndex("FoodDescription")),
                        c.getString(c.getColumnIndex("UserPhone"))
                ));
            }while (c.moveToNext());
        }
        return result;
    }

}
