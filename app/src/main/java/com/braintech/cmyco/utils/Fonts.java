package com.braintech.cmyco.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

public class Fonts {

    public static void robotoBold(Context context, TextView text) {
        String path = "fonts/Roboto-Bold.ttf";
        // Loading Font Face
        Typeface tf = Typeface.createFromAsset(context.getAssets(), path);
        text.setTypeface(tf);
    }

    public static void robotoRegular(Context context, TextView text) {
        String path = "fonts/Roboto-Regular.ttf";
        // Loading Font Face
        Typeface tf = Typeface.createFromAsset(context.getAssets(), path);
        text.setTypeface(tf);
    }

    public static void robotoRegularToEditText(Context context, EditText editText) {
        String path = "fonts/Roboto-Regular.ttf";
        // Loading Font Face
        Typeface tf = Typeface.createFromAsset(context.getAssets(), path);
        editText.setTypeface(tf);
    }


    public static void robotoRegularToButton(Context context, Button button) {
        String path = "fonts/Roboto-Regular.ttf";
        // Loading Font Face
        Typeface tf = Typeface.createFromAsset(context.getAssets(), path);
        button.setTypeface(tf);
    }


}
