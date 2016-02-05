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
import java.util.ArrayList;
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


        pollsPref.clearPollData();

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
        Utility.hideSoftKeyboard(this);
        doLogin();

    }

    private void doLogin() {

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

        ArrayList<HashMap<String, String>> listCoachTeamDetail;


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

                Log.d("Login URL", url);

                String jsonString = jsonParser.getJSONFromUrl(url);

                JSONObject jsonObject = new JSONObject(jsonString);
                if (jsonObject != null) {
                    result = jsonObject.getInt(Const.KEY_RESULT);
                    msg = jsonObject.getString(Const.KEY_MSG);
                    if (result == 1) {

                        listCoachTeamDetail = new ArrayList<>();
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

                            HashMap hashMapTitle = new HashMap();
                            hashMapTitle.put(Const.KEY_ID, 0);
                            hashMapTitle.put(Const.KEY_NAME, "Select Team");
                            hashMapTitle.put(Const.KEY_COACH, "Select Coach");

                            listCoachTeamDetail.add(hashMapTitle);



                            JSONObject jsonObjectTeam1 = jsonObjectGameData.getJSONObject(Const.KEY_TEAM1);
                            if (jsonObjectTeam1 != null) {
                                HashMap hashMapTeam1 = new HashMap();
                                hashMapTeam1.put(Const.KEY_ID, jsonObjectTeam1.getInt(Const.KEY_ID));
                                hashMapTeam1.put(Const.KEY_NAME, jsonObjectTeam1.getString(Const.KEY_NAME));
                                hashMapTeam1.put(Const.KEY_COACH, jsonObjectTeam1.getString(Const.KEY_COACH));

                                listCoachTeamDetail.add(hashMapTeam1);
                            }

                            JSONObject jsonObjectTeam2 = jsonObjectGameData.getJSONObject(Const.KEY_TEAM2);
                            if (jsonObjectTeam2 != null) {
                                HashMap hashMapTeam2 = new HashMap();
                                hashMapTeam2.put(Const.KEY_ID, jsonObjectTeam2.getInt(Const.KEY_ID));
                                hashMapTeam2.put(Const.KEY_NAME, jsonObjectTeam2.getString(Const.KEY_NAME));
                                hashMapTeam2.put(Const.KEY_COACH, jsonObjectTeam2.getString(Const.KEY_COACH));

                                listCoachTeamDetail.add(hashMapTeam2);
                            }

                        }


                    }

                }

            } catch (NullPointerException e) {
                e.printStackTrace();
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
                userSession.storeLoginDetail(email, password, hashMapLoginDetail.get(Const.KEY_ID));
                if (activeGame != 0) {

                    //calling method to start Asynchronous class to set Active User Status
                    commonAPI.setActiveUserStatus();

                    //Go to Home activity
                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                    intent.putExtra("TeamCoachDetail", listCoachTeamDetail);
                    startActivity(intent);
                    finish();


                } else {
                    Intent intent = new Intent(LoginActivity.this, NoGameActivity.class);
                    startActivity(intent);
                    finish();
                }
            } else if (result == 0) {
                alertDialogManager.showAlertDialog(LoginActivity.this, msg);
            } else {
                alertDialogManager.showAlertDialog(LoginActivity.this, getString(R.string.server_not_responding));
            }

            Progress.stop();
        }
    }


}
