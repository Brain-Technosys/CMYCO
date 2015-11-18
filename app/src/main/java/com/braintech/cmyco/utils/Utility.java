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

//        Calendar cal = Calendar.getInstance();
//        TimeZone tz = cal.getTimeZone();

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


  /*  public static String getDifferenceTimeZone() {
        long currentTime = System.currentTimeMillis();
        int edtOffset = TimeZone.getTimeZone("EST").getOffset(currentTime);


        int gmtOffset = TimeZone.getTimeZone("IST").getOffset(currentTime);

        Log.e("edtOffset", String.valueOf(edtOffset));

        Log.e("gmtOffset", String.valueOf(gmtOffset));
        int hourDifference = (gmtOffset - edtOffset) / (1000 * 60 * 60);
        String diff = hourDifference + " hours";
        Log.e("diff", diff);
        return diff;
    }*/

    public static String convertIntoSec(String time)//mm:ss
    {

        String[] units = time.split(":"); //will break the string up into an array
        int minutes = Integer.parseInt(units[0]); //first element
        int seconds = Integer.parseInt(units[1]); //second element
        int duration = 60 * minutes + seconds; //add up our values
        return String.valueOf(duration);
    }


   /* public static Integer compareTimes(String startTime, String endTime) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Date date = null;
        Date startDate = null;
        Date endDate = null;
        try {
            date = formatter.parse(getTime());
            startDate = formatter.parse(startTime);
            endDate = formatter.parse(endTime);


            Log.e("startDate + endDate", String.valueOf(startDate) + String.valueOf(endDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int difference = 0;
        if (date.after(startDate) && date.before(endDate)) {
            difference = 1;
        } else {
            difference = 0;
        }


//                difference = date.compareTo(formatter.parse(data));

        return difference;


    }*/

    public static String getCurrentTime(String time) {

        Date myDate=null;

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm:ss aa");
        TimeZone utcZone = TimeZone.getTimeZone("UTC");
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

    public static long findTimeDifference(String endTime,String startTime)
    {
        long mills=0;

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
        } catch(ParseException ex)
        {
            ex.printStackTrace();
        }

        return mills;

    }
}
