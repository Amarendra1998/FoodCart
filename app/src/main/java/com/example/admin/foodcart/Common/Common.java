package com.example.admin.foodcart.Common;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.ParseException;

import com.example.admin.foodcart.Model.User;
import com.example.admin.foodcart.Remote.APIService;
import com.example.admin.foodcart.Remote.GoogleRetrofitClient;
import com.example.admin.foodcart.Remote.IGoogleService;
import com.example.admin.foodcart.Remote.RetrofitClient;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class Common {
    public static String topicName = "Name";
    public static User currentUser;
    public static String PHONE_TEXT = "userPhone";
    public static final String INTENT_FOOD_ID = "FoodId";
    private static final String BASE_URL = "https://fcm.googleapis.com/";
    private static final String Google_Api_URL = "https://maps.googleapis.com/";

    public static APIService getFCMService()
    {
        return RetrofitClient.getClient(BASE_URL).create(APIService.class);
    }

    public static IGoogleService getGoogleMapApi()
    {
        return GoogleRetrofitClient.getGoogleClient(Google_Api_URL).create(IGoogleService.class);
    }
    public static String convertCodeToStatus(String status) {
        if (status.equals("0"))
            return "Placed";
        else if (status.equals("1"))
            return "On my way";
        else
            return "Shipped";
    }
    public static final String DELETE = "Delete";
    public static final String USER_KEY = "User";
    public static final String PWD_KEY = "Password";
    public static boolean isConnectedToInternet(Context context)
    {
        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager!=null)
        {
            NetworkInfo[] info = connectivityManager.getAllNetworkInfo();
            if (info!=null)
            {
                for (int i=0;i<info.length;i++)
                {
                    if (info[i].getState()== NetworkInfo.State.CONNECTED)
                        return true;
                }
            }
        }
        return false;
    }
    public static BigDecimal formatCurrency(String amount, Locale locale)throws ParseException
    {
        NumberFormat format = NumberFormat.getCurrencyInstance(locale);
        if (format instanceof DecimalFormat)
            ((DecimalFormat)format).setParseBigDecimal(true);
        try {
            return (BigDecimal)format.parse(amount.replace("[^\\d.,]",""));
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
