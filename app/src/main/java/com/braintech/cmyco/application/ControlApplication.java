package com.braintech.cmyco.application;

import android.app.Application;

import com.braintech.cmyco.utils.Foreground;

/**
 * Created by Braintech on 24-Nov-15.
 */
public class ControlApplication extends Application
{
    private static final String TAG=ControlApplication.class.getName();

    // only lazy initializations here!
    @Override
    public void onCreate()
    {
        super.onCreate();

        Foreground.init(this);
    }

}
