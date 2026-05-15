package com.example.fooddeliveryapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class FloatingHelper {

    public static void setup(Activity activity,
                             int riderId,
                             LinearLayout home,
                             LinearLayout orders,
                             LinearLayout delivery,
                             LinearLayout profile) {

        int activeColor = Color.parseColor("#FFA94D"); // light orange
        int inactiveColor = Color.parseColor("#777777"); // gray

        ImageView homeIcon = (ImageView) home.getChildAt(0);
        TextView homeText = (TextView) home.getChildAt(1);

        ImageView ordersIcon = (ImageView) orders.getChildAt(0);
        TextView ordersText = (TextView) orders.getChildAt(1);

        ImageView deliveryIcon = (ImageView) delivery.getChildAt(0);
        TextView deliveryText = (TextView) delivery.getChildAt(1);

        ImageView profileIcon = (ImageView) profile.getChildAt(0);
        TextView profileText = (TextView) profile.getChildAt(1);

        setActive(activity,
                home, orders, delivery, profile,
                homeIcon, homeText,
                ordersIcon, ordersText,
                deliveryIcon, deliveryText,
                profileIcon, profileText,
                activeColor, inactiveColor);

        home.setOnClickListener(v -> {
            resetAll(homeIcon, homeText,
                    ordersIcon, ordersText,
                    deliveryIcon, deliveryText,
                    profileIcon, profileText,
                    inactiveColor);

            setSelected(homeIcon, homeText, activeColor);

            if (!(activity instanceof RiderDashboardActivity)) {
                Intent i = new Intent(activity, RiderDashboardActivity.class);
                i.putExtra("user_id", riderId);
                activity.startActivity(i);
            }
        });

        orders.setOnClickListener(v -> {
            resetAll(homeIcon, homeText,
                    ordersIcon, ordersText,
                    deliveryIcon, deliveryText,
                    profileIcon, profileText,
                    inactiveColor);

            setSelected(ordersIcon, ordersText, activeColor);

            if (!(activity instanceof OrderListActivity)) {
                Intent i = new Intent(activity, OrderListActivity.class);
                i.putExtra("user_id", riderId);
                activity.startActivity(i);
            }
        });

        delivery.setOnClickListener(v -> {
            resetAll(homeIcon, homeText,
                    ordersIcon, ordersText,
                    deliveryIcon, deliveryText,
                    profileIcon, profileText,
                    inactiveColor);

            setSelected(deliveryIcon, deliveryText, activeColor);

            if (!(activity instanceof DeliveryDetailActivity)) {
                Intent i = new Intent(activity, DeliveryDetailActivity.class);
                i.putExtra("user_id", riderId);
                activity.startActivity(i);
            }
        });

        profile.setOnClickListener(v -> {
            resetAll(homeIcon, homeText,
                    ordersIcon, ordersText,
                    deliveryIcon, deliveryText,
                    profileIcon, profileText,
                    inactiveColor);

            setSelected(profileIcon, profileText, activeColor);

            if (!(activity instanceof ProfileActivity)) {
                Intent i = new Intent(activity, ProfileActivity.class);
                i.putExtra("user_id", riderId);
                activity.startActivity(i);
            }
        });
    }

    private static void setSelected(ImageView icon, TextView text, int color) {
        icon.setColorFilter(color);
        text.setTextColor(color);
    }

    private static void resetAll(ImageView homeIcon, TextView homeText,
                                 ImageView ordersIcon, TextView ordersText,
                                 ImageView deliveryIcon, TextView deliveryText,
                                 ImageView profileIcon, TextView profileText,
                                 int color) {

        homeIcon.setColorFilter(color);
        ordersIcon.setColorFilter(color);
        deliveryIcon.setColorFilter(color);
        profileIcon.setColorFilter(color);

        homeText.setTextColor(color);
        ordersText.setTextColor(color);
        deliveryText.setTextColor(color);
        profileText.setTextColor(color);
    }

    private static void setActive(Activity activity,
                                  LinearLayout home, LinearLayout orders,
                                  LinearLayout delivery, LinearLayout profile,
                                  ImageView homeIcon, TextView homeText,
                                  ImageView ordersIcon, TextView ordersText,
                                  ImageView deliveryIcon, TextView deliveryText,
                                  ImageView profileIcon, TextView profileText,
                                  int active, int inactive) {

        resetAll(homeIcon, homeText,
                ordersIcon, ordersText,
                deliveryIcon, deliveryText,
                profileIcon, profileText,
                inactive);

        if (activity instanceof RiderDashboardActivity) {
            setSelected(homeIcon, homeText, active);
        } else if (activity instanceof OrderListActivity) {
            setSelected(ordersIcon, ordersText, active);
        } else if (activity instanceof DeliveryDetailActivity) {
            setSelected(deliveryIcon, deliveryText, active);
        } else if (activity instanceof ProfileActivity) {
            setSelected(profileIcon, profileText, active);
        }
    }
}