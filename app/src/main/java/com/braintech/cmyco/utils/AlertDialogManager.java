package com.braintech.cmyco.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.internal.widget.DialogTitle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.braintech.cmyco.R;


public class AlertDialogManager {

    TextView tvMsg;
    TextView tvTitle;

    Button btnOk;
    Button btnCancel;



//    public void showAlertDialog(Context context, String message) {
//
//        final CustomDialog customDialog = new CustomDialog(context);
//
//        customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        customDialog.setContentView(R.layout.alert_dialog_layout);
//
//        //AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
//        tvMsg = (TextView) customDialog.findViewById(R.id.message);
//        tvTitle = (TextView) customDialog.findViewById(R.id.alertTitle);
//        btnOk = (Button) customDialog.findViewById(R.id.button2);
//        btnCancel = (Button) customDialog.findViewById(R.id.button3);
//
//        // Setting Dialog Message
//        tvTitle.setText("ALERT");
//        tvMsg.setText(message);
//        btnOk.setText("OK");
//        //  btnCancel.setText("CANCEL");
//        // alertDialog.setTitle("Alert");
//
//        Fonts.robotoBold(context, tvTitle);
//        Fonts.robotoRegular(context, tvMsg);
//
//
//        // Setting OK Button
//        btnOk.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                customDialog.dismiss();
//            }
//        });
//
//
////        // Showing Alert Message
////        AlertDialog alert = alertDialog.show();
//
////        TextView messageText = (TextView) alert
////                .findViewById(android.R.id.message);
//
////        Fonts.robotoRegular(context, messageText);
//        tvMsg.setGravity(Gravity.CENTER);
//        tvMsg.setTextColor(Color.parseColor("#000000"));
//
//        customDialog.show();
//    }


    public void showAlertDialog(Context context, String message) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);

        // Setting Dialog Message
        alertDialog.setMessage(message);
        // alertDialog.setTitle("Alert");

        // Setting OK Button
        alertDialog.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });


        // Showing Alert Message
        AlertDialog alert = alertDialog.show();
        TextView messageText = (TextView) alert
                .findViewById(android.R.id.message);
        messageText.setTextColor(Color.parseColor("#000000"));
        Fonts.robotoRegular(context, messageText);
        messageText.setGravity(Gravity.CENTER);

        alert.show();
    }

    public void showAlertForFinish(final Context context, String message) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);

        // Setting Dialog Message
        alertDialog.setMessage(message);

        // Setting OK Button
        alertDialog.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ((Activity) context).finish();
                    }
                });


        // Showing Alert Message
        AlertDialog alert = alertDialog.show();
        TextView messageText = (TextView) alert
                .findViewById(android.R.id.message);
        Fonts.robotoRegular(context, messageText);
        messageText.setTextColor(Color.parseColor("#000000"));
        messageText.setGravity(Gravity.CENTER);

        alert.show();

    }


}