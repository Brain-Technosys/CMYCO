package com.braintech.cmyco.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;

import com.braintech.cmyco.R;
import com.braintech.cmyco.application.ControlApplication;
import com.braintech.cmyco.common.CommonAPI;
import com.braintech.cmyco.my_interface.SnakeOnClick;
import com.braintech.cmyco.utils.AlertDialogManager;
import com.braintech.cmyco.utils.Const;
import com.braintech.cmyco.utils.JsonParser;
import com.braintech.cmyco.utils.Progress;
import com.braintech.cmyco.utils.SnackNotify;
import com.braintech.cmyco.utils.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class InstructionActivityTwo extends MyBaseActivity {

    @InjectView(R.id.coordinatorLayout)
    CoordinatorLayout coordinatorLayout;

    @InjectView(R.id.wv_instruction)
    WebView instructionWebView;

    SnakeOnClick snakeOnClick;

    CommonAPI logoutAPI;

    AlertDialogManager alertDialogManager;

    private static final String TAG = InstructionActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instruction_page_two);

        ButterKnife.inject(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        logoutAPI = new CommonAPI(this);

        alertDialogManager = new AlertDialogManager();

        handleSnakeRetryCall();

        showInstruction();


    }

    private void showInstruction() {
        if (Utility.isNetworkAvailable(this)) {
            new GetInstructions().execute();
        } else {
            SnackNotify.showSnakeBar(this, snakeOnClick, coordinatorLayout);
        }
    }

    private void handleSnakeRetryCall() {
        snakeOnClick = new SnakeOnClick() {
            @Override
            public void onRetryClick() {
                logoutAPI.logout(snakeOnClick, coordinatorLayout);
            }
        };
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.setting_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            logoutAPI.logout(snakeOnClick, coordinatorLayout);
            return true;
        } else if (id == android.R.id.home) {
            this.finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.btn_continue)
    void goNext() {
        Intent intent = new Intent(this, GamePlayStrategyActivity.class);
        startActivity(intent);
    }

    private class GetInstructions extends AsyncTask<String, String, String> {
        String instruction;
        String msg;
        String id;
        int result = -1;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            Progress.start(InstructionActivityTwo.this);
        }

        @Override
        protected String doInBackground(String... strings) {
            JsonParser jsonParser = new JsonParser(InstructionActivityTwo.this);

            String url = Const.GET_INSTRUCTION2;
            //Log.e(url, url);
            String jsonString = jsonParser.getJSONFromUrl(url);
            try {
                JSONObject jsonObject = new JSONObject(jsonString);
                if (jsonObject != null) {
                    result = jsonObject.getInt(Const.KEY_RESULT);
                    if (result == 1) {
                        JSONObject jsonObjectContent = jsonObject.getJSONObject(Const.KEY_DATA);
                        if (jsonObjectContent != null) {
                            instruction = jsonObjectContent.getString(Const.KEY_TEXT);
                            id = jsonObjectContent.getString(Const.KEY_ID);
                        }
                    } else {
                        msg = jsonObject.getString(Const.KEY_MSG);
                    }

                }

            } catch (NullPointerException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Progress.stop();

            if (result == 1) {
                //showing justify text in webView
                String text = "<html><body style=\"text-align:justify\"> %s </body></Html>";
                instructionWebView.loadData(String.format(text, instruction), "text/html", "UTF-8");
                instructionWebView.setBackgroundColor(Color.TRANSPARENT);

            } else if (result == 0) {
                alertDialogManager.showAlertDialog(InstructionActivityTwo.this, msg);
            } else {
                alertDialogManager.showAlertDialog(InstructionActivityTwo.this, getString(R.string.server_not_responding));
            }
        }
    }

//    public ControlApplication getApp()
//    {
//        return (ControlApplication )this.getApplication();
//    }
//
//    @Override
//    public void onUserInteraction()
//    {
//        super.onUserInteraction();
//        getApp().touch();
//        Log.e(TAG, "User interaction to " + this.toString());
//    }

}
