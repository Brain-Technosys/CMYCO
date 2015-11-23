package com.braintech.cmyco.utils;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.TimePicker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;
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


    // Method to hide soft keyboard
    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputManager = (InputMethodManager)
                activity.getSystemService(Context.INPUT_METHOD_SERVICE);

        inputManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }


    public static String convertDateFormat(String dateString) {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss aa");
        Date date = null;
        try {
            date = fmt.parse(dateString);
        } catch (ParseException e) {

            e.printStackTrace();
        }

        SimpleDateFormat fmtOut = new SimpleDateFormat("hh:mm:ss aa");
        return fmtOut.format(date);

    }


    public static String getCurrentTime(String time, String timeZone) {

        Date myDate = null;

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm:ss aa");
        TimeZone utcZone = TimeZone.getTimeZone(timeZone);
        simpleDateFormat.setTimeZone(utcZone);
        try {
            myDate = simpleDateFormat.parse(time);
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        SimpleDateFormat outDateFormat = new SimpleDateFormat("HH:mm:ss");
        outDateFormat.setTimeZone(TimeZone.getDefault());
        String formattedDate = outDateFormat.format(myDate);

        return formattedDate;
    }

    public static long findTimeDifference(String endTime, String startTime) {
        long mills = 0;

        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        try {
            Date Date1 = format.parse(endTime);
            Date Date2 = format.parse(startTime);
            mills = Date1.getTime() - Date2.getTime();

           /* Log.v("Data1", "" + Date1.getTime());
            Log.v("Data2", "" + Date2.getTime());
            int Hours = (int) (mills / (1000 * 60 * 60));
            int Mins = (int) (mills / (1000 * 60)) % 60;
            int seconds=(int)(mills/1000)%60;

            diff = Hours*3600+Mins*60+seconds;

            Log.e("diff",""+diff);*/
            // Log.e("mills",""+mills);
        } catch (ParseException ex) {
            ex.printStackTrace();
        }

        return mills;

    }
}
