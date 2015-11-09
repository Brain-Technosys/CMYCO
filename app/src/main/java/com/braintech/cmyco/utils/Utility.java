package com.braintech.cmyco.utils;

import android.app.TimePickerDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.TimePicker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Created by Braintech on 29-Oct-15.
 */
public class Utility {

    //method to check valid email id
    public static boolean isValidEmailAddress(String email) {
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        Pattern p = Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(email);
        return m.matches();
    }

    // Method to check Internet connectivity
    public static boolean isNetworkAvailable(Context context) {
        //return ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo() != null;
        ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }


    public static String getCurrentTime() {
        Date date = new Date(System.currentTimeMillis());

        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm aa", Locale.ENGLISH);

        String var = dateFormat.format(date);

        return var;
    }

    public static String getTime() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String strDate = sdf.format(c.getTime());

        return strDate;
    }

    public static String convertTimeFormat(String time) {

        SimpleDateFormat formatter, FORMATTER;
        formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = formatter.parse(time.substring(0, 24));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        FORMATTER = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        //  System.out.println("OldDate-->"+oldDate);
        Log.e("NewDate-->", FORMATTER.format(date).toString());
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        String strDate = sdf.format(time);
        return FORMATTER.format(date).toString();
    }
    public static String changeDateFormat(String OLD_DATE) {

        String old_date_format = "yyyy-mm-dd | hh:mm:ss aa";
        String new_date_format = " hh:mm:ss";
        String NEW_DATE = null;

        try {

            SimpleDateFormat sdf = new SimpleDateFormat(old_date_format);
            Date d = sdf.parse(OLD_DATE);
            sdf.applyPattern(new_date_format);
            NEW_DATE = sdf.format(d);
            Log.e("Date", NEW_DATE);

        } catch (ParseException p) {
            p.printStackTrace();
        }

        return NEW_DATE;
    }


    public static String convertDateFormat(String dateString) {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-mm-dd | hh:mm:ss aa");
        Date date = null;
        try {
            date = fmt.parse(dateString);
        } catch (ParseException e) {

            e.printStackTrace();
        }

        SimpleDateFormat fmtOut = new SimpleDateFormat("hh:mm:ss");
        return fmtOut.format(date);

    }
}
