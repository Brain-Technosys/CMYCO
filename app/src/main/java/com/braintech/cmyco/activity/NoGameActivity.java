package com.braintech.cmyco.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import com.braintech.cmyco.R;
import com.braintech.cmyco.common.CommonAPI;
import com.braintech.cmyco.my_interface.SnakeOnClick;
import com.braintech.cmyco.sessions.PollsPref;
import com.braintech.cmyco.sessions.UserSession;
import com.braintech.cmyco.utils.AlertDialogManager;
import com.braintech.cmyco.utils.Const;
import com.braintech.cmyco.utils.Fonts;
import com.braintech.cmyco.utils.JsonParser;
import com.braintech.cmyco.utils.Progress;
import com.braintech.cmyco.utils.SnackNotify;
import com.braintech.cmyco.utils.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class NoGameActivity extends AppCompatActivity {

    @InjectView(R.id.btn_refresh)
    Button btn_refresh;

    @InjectView(R.id.coordinatorLayout)
    CoordinatorLayout coordinatorLayout;

    @InjectView(R.id.txt_sign_up)
    TextView txt_sign_up;

    String email;
    String password;

    SnakeOnClick snakeRetryLogout;

    PollsPref pollsPref;

    CommonAPI logoutAPI;

    SnakeOnClick snakeOnClick;

    AlertDialogManager alertDialogManager;

    boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_game);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        ButterKnife.inject(this);

        setFont();

        logoutAPI = new CommonAPI(this);

        pollsPref = new PollsPref(this);

        alertDialogManager = new AlertDialogManager();

        handleSnakeRetryCall();

        getLoginData();

    }

    private void setFont() {
        Fonts.robotoRegularToButton(this, btn_refresh);
        Fonts.robotoRegular(this, txt_sign_up);
    }

    //this method will call only if their is no internet connection
    private void handleSnakeRetryCall() {
        snakeOnClick = new SnakeOnClick() {
            @Override
            public void onRetryClick() {
                doLogin();
            }
        };

//        snakeOnClickPollRetry = new SnakeOnClick() {
//            @Override
//            public void onRetryClick() {
//                commonAPI.getPollData(snakeOnClickPollRetry, coordinatorLayout);
//            }
//        };

        snakeRetryLogout = new SnakeOnClick() {
            @Override
            public void onRetryClick() {
                logoutAPI.logout(snakeRetryLogout, coordinatorLayout);
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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            logoutAPI.logout(snakeRetryLogout, coordinatorLayout);
            return true;
        } else if (id == android.R.id.home) {
            this.finish();
        }

        return super.onOptionsItemSelected(item);
    }

    public void getLoginData() {
        UserSession userSession = new UserSession(this);
        email = userSession.getKeyEmail();
        password = userSession.getKeyPassword();

    }

    @OnClick(R.id.btn_refresh)
    void doRefresh() {
        //add resume

        doLogin();
    }

    public void doLogin() {

        if (!Utility.isNetworkAvailable(NoGameActivity.this)) {

            SnackNotify.showSnakeBar(this, snakeOnClick, coordinatorLayout);
        } else {

            new CallLoginAPI().execute();
        }
    }

    private class CallLoginAPI extends AsyncTask<String, String, String> {
        JsonParser jsonParser;
        int result = -1;
        int activeGame;
        String msg = "";
        HashMap<String, String> hashMapLoginDetail;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            hashMapLoginDetail = new HashMap<>();
            Progress.start(NoGameActivity.this);
        }

        @Override
        protected String doInBackground(String... strings) {
            jsonParser = new JsonParser(NoGameActivity.this);

            try {

                String url = Const.SIGN_IN + Const.TAG_EMAIL + URLEncoder.encode(email, "UTF-8") +
                        Const.TAG_PASSWORD + URLEncoder.encode(password, "UTF-8");

                //Log.e("url", url);

                String jsonString = jsonParser.getJSONFromUrl(url);
                JSONObject jsonObject = new JSONObject(jsonString);
                if (jsonObject != null) {
                    result = jsonObject.getInt(Const.KEY_RESULT);
                    msg = jsonObject.getString(Const.KEY_MSG);
                    if (result == 1) {


                        JSONObject jsonObjectLoginDetail = jsonObject.getJSONObject(Const.KEY_DATA);

                        hashMapLoginDetail.put(Const.KEY_ID, jsonObjectLoginDetail.getString(Const.KEY_ID));
                        hashMapLoginDetail.put(Const.KEY_USERNAME, jsonObjectLoginDetail.getString(Const.KEY_USERNAME));
                        hashMapLoginDetail.put(Const.KEY_EMAIL, jsonObjectLoginDetail.getString(Const.KEY_EMAIL));


                        activeGame = jsonObject.getInt(Const.KEY_ACTIVE_GAME);

                        pollsPref.saveActiveGame(activeGame);
                        pollsPref.storeUserInfo(jsonObjectLoginDetail.getString(Const.KEY_ID), jsonObjectLoginDetail.getString(Const.KEY_USERNAME), jsonObjectLoginDetail.getString(Const.KEY_EMAIL));

                        if (activeGame != 0) {

                            JSONObject jsonObjectGameData = jsonObject.getJSONObject(Const.KEY_GAME_DATA);

                            //Storing game json in sheared pref
                            JSONObject jsonObjectGame = jsonObjectGameData.getJSONObject(Const.KEY_GAME);
                            pollsPref.storeGameJson(jsonObjectGame.toString());

//                            //Storing team1 json in sheared pref
//                            JSONObject jsonObjectTeam1 = jsonObjectGameData.getJSONObject(Const.KEY_TEAM1);
//                            pollsPref.storeTeam1Json(jsonObjectTeam1.toString());
//
//                            //Storing team2 json in sheared pref
//                            JSONObject jsonObjectTeam2 = jsonObjectGameData.getJSONObject(Const.KEY_TEAM2);
//                            pollsPref.storeTeam2Json(jsonObjectTeam2.toString());

                        }


                    }

                }

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (result == 1) {
                if (activeGame != 0) {

                    Intent intent = new Intent(NoGameActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();

                } else {
                    alertDialogManager.showAlertDialog(NoGameActivity.this, "There is no active game.");
                }
            } else if (result == 0) {
                alertDialogManager.showAlertDialog(NoGameActivity.this, msg);
            } else {
                alertDialogManager.showAlertDialog(NoGameActivity.this, getString(R.string.server_not_responding));
            }
            Progress.stop();
        }
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;

        SnackNotify.showSnakeBarForBackPress(this, coordinatorLayout);

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

}
