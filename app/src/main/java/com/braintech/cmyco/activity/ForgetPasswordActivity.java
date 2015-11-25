package com.braintech.cmyco.activity;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class ForgetPasswordActivity extends AppCompatActivity {

    @InjectView(R.id.btn_submit)
    Button submitButton;

    @InjectView(R.id.et_email)
    EditText emailEditText;

    @InjectView(R.id.coordinatorLayout)
    CoordinatorLayout coordinatorLayout;

    String email;

    AlertDialogManager alertDialogManager;

    SnakeOnClick snakeOnClick;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

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

    //method to set method of all view of forget password
    private void setFont() {
        Fonts.robotoRegularToButton(this, submitButton);

        Fonts.robotoRegularToEditText(this, emailEditText);

    }

    @OnClick(R.id.btn_submit)
    public void submit() {
        getData();
        validateData();
    }


    private void getData() {
        email = emailEditText.getText().toString().trim();
    }

    private void validateData() {
        if (email.length() == 0) {
            alertDialogManager.showAlertDialog(this, getString(R.string.empty_email));
        } else if (!Utility.isValidEmailAddress(email)) {
            alertDialogManager.showAlertDialog(this, getString(R.string.invalid_email));
        } else if (!Utility.isNetworkAvailable(this)) {
            SnackNotify.showSnakeBar(this, snakeOnClick, coordinatorLayout);
        } else {
            new GetNewPassword().execute();
        }
    }


    private class GetNewPassword extends AsyncTask<String, String, String> {
        int result = -1;
        String msg;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            Progress.start(ForgetPasswordActivity.this);
        }

        @Override
        protected String doInBackground(String... strings) {

            JsonParser jsonParser = new JsonParser(ForgetPasswordActivity.this);

            try {
                String url = Const.FORGET_PASSWORD + Const.TAG_EMAIL + URLEncoder.encode(email, "UTF-8");

                String jsonString = jsonParser.getJSONFromUrl(url);
               /* Log.d("url", url);
                Log.d("jsonString", jsonString);*/
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
                alertDialogManager.showAlertForFinish(ForgetPasswordActivity.this, msg);
            } else if (result == 0) {
                alertDialogManager.showAlertDialog(ForgetPasswordActivity.this, msg);
            } else {
                alertDialogManager.showAlertDialog(ForgetPasswordActivity.this, getString(R.string.server_not_responding));
            }

        }
    }
}
