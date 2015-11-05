package com.braintech.cmyco.utils;


import android.app.ProgressDialog;
import android.content.Context;
import android.view.WindowManager;

import com.braintech.cmyco.R;


public class Progress {

    static ProgressDialog dialog;

    public static void start(Context context) {
        if (dialog != null)
            dialog.dismiss();

        dialog = new ProgressDialog(context, ProgressDialog.THEME_HOLO_LIGHT);

        try {
            dialog.show();
        } catch (WindowManager.BadTokenException e) {

        }
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);

        dialog.setContentView(R.layout.progressdialog);

    }

    public static void stop() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }
}
