package com.braintech.cmyco.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;

import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


public class LoginActivity extends AppCompatActivity {


    @InjectView(R.id.lbl_login)
    TextView loginTextView;

    @InjectView(R.id.et_email)
    EditText emailEditText;

    @InjectView(R.id.et_password)
    EditText passwordEditText;

    @InjectView(R.id.btn_sign_in)
    Button signInButton;

    @InjectView(R.id.txt_forget_password)
    TextView forgetPasswordTextView;

    @InjectView(R.id.txt_sign_up)
    TextView signUpTextView;

    @InjectView(R.id.coordinatorLayout)
    CoordinatorLayout coordinatorLayout;

    SnakeOnClick snakeOnClick;
    SnakeOnClick snakeOnClickPollRetry;

    String email;
    String password;
    PollsPref pollsPref;

    AlertDialogManager alertDialogManager;
    UserSession userSession;

    boolean doubleBackToExitPressedOnce = false;

    CommonAPI commonAPI;

    public static Activity loginActivity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.inject(this);



        loginActivity = this;

        alertDialogManager = new AlertDialogManager();

        userSession = new UserSession(getApplicationContext());

        commonAPI = new CommonAPI(LoginActivity.this);

        pollsPref = new PollsPref(this);

        pollsPref.pollActivated(true);

        handleSnakeRetryCall();

        setFont();

    }

    //this method will call only if their is no internet connection
    private void handleSnakeRetryCall() {
        snakeOnClick = new SnakeOnClick() {
            @Override
            public void onRetryClick() {
                validateData();
            }
        };

        snakeOnClickPollRetry = new SnakeOnClick() {
            @Override
            public void onRetryClick() {
                commonAPI.getPollData(snakeOnClickPollRetry, coordinatorLayout);
            }
        };
    }

    //method to set font of all views
    private void setFont() {
        Fonts.robotoBold(this, loginTextView);
        Fonts.robotoRegular(this, forgetPasswordTextView);
        Fonts.robotoRegular(this, signUpTextView);

        Fonts.robotoRegularToEditText(this, emailEditText);
        Fonts.robotoRegularToEditText(this, passwordEditText);

        Fonts.robotoRegularToButton(this, signInButton);
    }

    @OnClick(R.id.btn_sign_in)
    void doSignIn() {

        doLogin();

    }

    private void doLogin() {
//
//        Utility.hideSoftKeyboard(LoginActivity.this);
        getData();
        validateData();
    }

    private void getData() {

        email = emailEditText.getText().toString().trim();
        password = passwordEditText.getText().toString().trim();

    }

    private void validateData() {
        if (email.length() == 0 || password.length() == 0) {
            alertDialogManager.showAlertDialog(LoginActivity.this, getString(R.string.empty_fields));
        } else if (!Utility.isValidEmailAddress(email)) {
            alertDialogManager.showAlertDialog(LoginActivity.this, getString(R.string.invalid_email));
        } else if (!Utility.isNetworkAvailable(LoginActivity.this)) {

            SnackNotify.showSnakeBar(this, snakeOnClick, coordinatorLayout);
        } else {

            new CallLoginAPI().execute();
        }

    }

    @OnClick(R.id.txt_sign_up)
    void doSignUp() {
        Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.txt_forget_password)
    void getForgetPassword() {
        Intent intent = new Intent(LoginActivity.this, ForgetPasswordActivity.class);
        startActivity(intent);
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
            Progress.start(LoginActivity.this);
        }

        @Override
        protected String doInBackground(String... strings) {
            jsonParser = new JsonParser(LoginActivity.this);

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
                userSession.storeLoginDetail(email, password);
                if (activeGame != 0) {

                    //Calling GetPoll API from Common Class
                    //commonAPI.getPollData(snakeOnClickPollRetry, coordinatorLayout);

                    //Saving Password and email in sheared pref


                    //Go to Home activity
                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();


                } else {
                    //  alertDialogManager.showAlertDialog(LoginActivity.this, getString(R.string.alert_no_active_game));
                    Intent intent = new Intent(LoginActivity.this, NoGameActivity.class);
                    startActivity(intent);
                    finish();
                }
            } else if (result == 0) {
                alertDialogManager.showAlertDialog(LoginActivity.this, msg);
            } else {
                alertDialogManager.showAlertDialog(LoginActivity.this, getString(R.string.server_not_responding));
            }

//
            Progress.stop();
        }
    }


}
