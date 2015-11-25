package com.braintech.cmyco.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.Toast;

import com.braintech.cmyco.R;
import com.braintech.cmyco.application.ControlApplication;
import com.braintech.cmyco.common.CommonAPI;
import com.braintech.cmyco.my_interface.SnakeOnClick;
import com.braintech.cmyco.services.PollService;
import com.braintech.cmyco.sessions.PollsPref;
import com.braintech.cmyco.sessions.UserSession;
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

public class InstructionActivity extends AppCompatActivity {


    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    @InjectView(R.id.wv_instruction)
    WebView instructionWebView;

    @InjectView(R.id.coordinatorLayout)
    CoordinatorLayout coordinatorLayout;

    AlertDialogManager alertDialogManager;

    UserSession userSession;

    PollsPref pollsPref;

    SnakeOnClick snakeOnClick;

    SnakeOnClick snakeOnClickForLogout;

    CommonAPI logoutAPI;

    PollService pollService;

    private boolean mIsBound = false;

    long postDelayedTime;

    private static final String TAG= InstructionActivity.class.getName();

    private ServiceConnection sCon = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            pollService = ((PollService.ServiceBinder) service).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            pollService = null;
        }
    };

    void doBindService() {

        bindService(new Intent(InstructionActivity.this, PollService.class),
                sCon, Context.BIND_AUTO_CREATE);
        mIsBound = true;
    }

    void doUnbindService() {
        if (mIsBound) {
            unbindService(sCon);
            mIsBound = false;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instruction);

        ButterKnife.inject(this);


        //Binding service to activity, after this we are able to call all method of PollService from This Activity or any other Activity.
        handleService();

        alertDialogManager = new AlertDialogManager();

        userSession = new UserSession(this);

        pollsPref = new PollsPref(InstructionActivity.this);

        logoutAPI = new CommonAPI(this);

        manageToolbar();

        handleSnakeRetryCall();

        handleLogoutRetry();

        //showing data in webView
        showInstruction();

        // callingPollData();

        setPostDelayedDuration(5000);

        pollsPref.ActivityRunning(false);

    }

    public void setPostDelayedDuration(long duration)
    {
        postDelayedTime=duration;
    }

    private void callingPollData() {

//        //setting default id
//      //  pollsPref.storePLAY("KEY");

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do something after 100ms
                if (pollsPref.getTeam() != null) {
                    if (pollService != null) {


                        if (pollsPref.isPollActivated()) {
                            //do nothing
                        } else {
                            pollService.getPollData(InstructionActivity.this);
                        }
                    }

                    handler.postDelayed(this, postDelayedTime);
                }
            }
        }, 1000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //unbinding service when Activity
        if (pollService != null) {
            doUnbindService();
        }
    }

    private void handleService() {
        doBindService();
        Intent poll = new Intent();
        poll.setClass(InstructionActivity.this, PollService.class);
        startService(poll);
    }

    private void handleLogoutRetry() {
        snakeOnClickForLogout = new SnakeOnClick() {
            @Override
            public void onRetryClick() {
                logoutAPI.logout(snakeOnClickForLogout, coordinatorLayout);
            }
        };

    }

    private void handleSnakeRetryCall() {
        snakeOnClick = new SnakeOnClick() {
            @Override
            public void onRetryClick() {
                showInstruction();
            }
        };
    }

    private void manageToolbar() {
        toolbar.setTitle(getTitle());
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void showInstruction() {
        if (Utility.isNetworkAvailable(InstructionActivity.this)) {
            new GetInstructions().execute();
        } else {
            SnackNotify.showSnakeBar(this, snakeOnClick, coordinatorLayout);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.setting_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
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

    private class GetInstructions extends AsyncTask<String, String, String> {
        String instruction;
        String msg;
        String id;
        int result = -1;
        int page = 1;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            Progress.start(InstructionActivity.this);
        }

        @Override
        protected String doInBackground(String... strings) {
            JsonParser jsonParser = new JsonParser(InstructionActivity.this);

            //
            String url = Const.GET_INSTRUCTION + page;
           // Log.e(url, url);
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
                alertDialogManager.showAlertDialog(InstructionActivity.this, msg);
            } else {
                alertDialogManager.showAlertDialog(InstructionActivity.this, getString(R.string.server_not_responding));
            }

            callingPollData();
        }
    }

    @OnClick(R.id.btn_continue)
    void continueToDefence() {
        Intent intent = new Intent(InstructionActivity.this, InstructionActivityTwo.class);
        startActivity(intent);
    }


//    public ControlApplication getApp()
//    {
//        return (ControlApplication)this.getApplication();
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
