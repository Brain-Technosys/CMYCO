package com.braintech.cmyco.application;

import android.app.Application;
import android.util.Log;

import com.braintech.cmyco.utils.Waiter;

/**
 * Created by Braintech on 24-Nov-15.
 */
public class ControlApplication extends Application
{
    private static final String TAG=ControlApplication.class.getName();
    private Waiter waiter;  //Thread which controls idle time

    // only lazy initializations here!
    @Override
    public void onCreate()
    {
        super.onCreate();
        Log.d(TAG, "Starting application" + this.toString());
        waiter=new Waiter(15*60*1000); //15 mins
        waiter.start();
    }

    public void touch()
    {
        waiter.touch();
    }
}
