package com.braintech.cmyco.activity;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.braintech.cmyco.R;
import com.braintech.cmyco.my_interface.SnakeOnClick;
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
import java.net.URL;
import java.net.URLEncoder;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class RegistrationActivity extends AppCompatActivity {


    @InjectView(R.id.et_name)
    EditText nameEditText;

    @InjectView(R.id.et_email)
    EditText emailEditText;

    @InjectView(R.id.et_password)
    EditText passwordEditText;

    @InjectView(R.id.et_confirm_password)
    EditText confirmPasswordEditText;

    @InjectView(R.id.btn_sign_up)
    Button signUpButton;

    @InjectView(R.id.coordinatorLayout)
    CoordinatorLayout coordinatorLayout;

    String name;
    String email;
    String password;
    String confirmPassword;

    AlertDialogManager alertDialogManager;

    SnakeOnClick snakeOnClick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        ButterKnife.inject(this);

        alertDialogManager = new AlertDialogManager();

        setFont();

        handleSnakeRetryCall();

    }

    private void handleSnakeRetryCall() {
        snakeOnClick = new SnakeOnClick() {
            @Override
            public void onRetryClick() {
                validateData();
            }
        };
    }

    private void setFont() {

        Fonts.robotoRegularToEditText(this, nameEditText);
        Fonts.robotoRegularToEditText(this, emailEditText);
        Fonts.robotoRegularToEditText(this, passwordEditText);
        Fonts.robotoRegularToEditText(this, confirmPasswordEditText);

        Fonts.robotoRegularToButton(this, signUpButton);

    }

    @OnClick(R.id.btn_sign_up)
    void doSignUp() {
        setData();

        validateData();
    }


    private void setData() {

        name = nameEditText.getText().toString().trim();
        email = emailEditText.getText().toString().trim();
        password = passwordEditText.getText().toString().trim();
        confirmPassword = confirmPasswordEditText.getText().toString().trim();
    }

    private void validateData() {
        if (name.length() == 0 || email.length() == 0 || password.length() == 0 || confirmPassword.length() == 0) {
            alertDialogManager.showAlertDialog(RegistrationActivity.this, getString(R.string.empty_fields));
        } else if (!Utility.isValidEmailAddress(email)) {
            alertDialogManager.showAlertDialog(RegistrationActivity.this, getString(R.string.invalid_email));
        } else if (password.length() < 6) {
            alertDialogManager.showAlertDialog(RegistrationActivity.this, getString(R.string.alert_pwd_length));
        } else if (!confirmPassword.equals(password)) {
            alertDialogManager.showAlertDialog(RegistrationActivity.this, getString(R.string.password_match));
        } else if (!Utility.isNetworkAvailable(this)) {
            SnackNotify.showSnakeBar(this, snakeOnClick, coordinatorLayout);
        } else {
            new SignUp().execute();
        }
    }


    private class SignUp extends AsyncTask<String, String, String> {

        int result = -1;
        String msg;
        JsonParser jsonParser;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            Progress.start(RegistrationActivity.this);
        }

        @Override
        protected String doInBackground(String... strings) {
            jsonParser = new JsonParser(RegistrationActivity.this);
            try {
                String url = Const.SIGN_UP + Const.TAG_EMAIL + URLEncoder.encode(email, "UTF-8") + Const.TAG_REG_USERNAME +
                        URLEncoder.encode(name, "UTF-8") + Const.TAG_PASSWORD + URLEncoder.encode(password, "UTF-8") +
                        Const.TAG_REG_CONFIRM_PASSWORD + URLEncoder.encode(confirmPassword, "UTF-8");

                String jsonString = jsonParser.getJSONFromUrl(url);
                JSONObject jsonObject = new JSONObject(jsonString);

                if (jsonObject != null) {
                    result = jsonObject.getInt(Const.KEY_RESULT);
                    msg = jsonObject.getString(Const.KEY_MSG);

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

            Progress.stop();

            if (result == 1) {
                alertDialogManager.showAlertForFinish(RegistrationActivity.this, msg);
            } else if (result == 0) {
                alertDialogManager.showAlertDialog(RegistrationActivity.this, msg);
            } else {
                alertDialogManager.showAlertDialog(RegistrationActivity.this, getString(R.string.server_not_responding));
            }
        }
    }
}
