package com.braintech.cmyco.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Braintech on 11/16/2015.
 */
public class TimeCheck {
    public static boolean isTimeBetweenTwoTime(String initialTime, String finalTime, String currentTime) throws ParseException {
        String reg = "^([0-1][0-9]|2[0-3]):([0-5][0-9]):([0-5][0-9])$";
        if(initialTime.matches(reg) && finalTime.matches(reg) && currentTime.matches(reg)) {
            boolean valid  = false;
            //Start Time
            java.util.Date inTime = new SimpleDateFormat("HH:mm:ss").parse(initialTime);
            Calendar calendar1 = Calendar.getInstance();
            calendar1.setTime(inTime);

            //Current Time
            java.util.Date checkTime = new SimpleDateFormat("HH:mm:ss").parse(currentTime);
            Calendar calendar3 = Calendar.getInstance();
            calendar3.setTime(checkTime);

            //End Time
            java.util.Date finTime = new SimpleDateFormat("HH:mm:ss").parse(finalTime);
            Calendar calendar2 = Calendar.getInstance();
            calendar2.setTime(finTime);

            if(finalTime.compareTo(initialTime) < 0) {
                calendar2.add(Calendar.DATE, 1);
                calendar3.add(Calendar.DATE, 1);
            }

            java.util.Date actualTime = calendar3.getTime();
            if((actualTime.after(calendar1.getTime()) || actualTime.compareTo(calendar1.getTime())==0) && actualTime.before(calendar2.getTime())){
                valid = true;
            }
            return valid;
        } else {
            throw new IllegalArgumentException("not a valid time, expecting HH:MM:SS format");
        }

    }
    public static void main(String[] args) {
        try{
            if(isTimeBetweenTwoTime("9:10:11", "15:50:55", "17:15:45"))
                System.out.println("Given time lies between two times");
            else
                System.out.println("Given time doesn't lies between two times");

        }catch (ParseException e){
            e.printStackTrace();
        }

    }
}