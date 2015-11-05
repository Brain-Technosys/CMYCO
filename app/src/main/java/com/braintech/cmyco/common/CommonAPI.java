package com.braintech.cmyco.common;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.util.Log;

import com.braintech.cmyco.R;
import com.braintech.cmyco.activity.LoginActivity;
import com.braintech.cmyco.my_interface.SnakeOnClick;
import com.braintech.cmyco.sessions.PollsPref;
import com.braintech.cmyco.sessions.UserSession;
import com.braintech.cmyco.utils.AlertDialogManager;
import com.braintech.cmyco.utils.Const;
import com.braintech.cmyco.utils.JsonParser;
import com.braintech.cmyco.utils.Progress;
import com.braintech.cmyco.utils.SnackNotify;
import com.braintech.cmyco.utils.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Braintech on 05-Nov-15.
 */
public class CommonAPI {
    AlertDialogManager alertDialogManager;
    Context context;

    UserSession userSession;
    PollsPref pollsPref;

    public CommonAPI(Context context) {
        this.context = context;
        alertDialogManager = new AlertDialogManager();
        userSession = new UserSession(context);
        pollsPref = new PollsPref(context);
    }


    public void logout(SnakeOnClick snakeOnClick, CoordinatorLayout coordinatorLayout) {

        if (Utility.isNetworkAvailable(context)) {
            new LogoutUser().execute(userSession.getUserID());
        } else {
            SnackNotify.showSnakeBar((Activity) context, snakeOnClick, coordinatorLayout);
        }

    }

    //Asynchronous class to call logout API

    private class LogoutUser extends AsyncTask<String, String, String> {
        int result = -1;
        String msg;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            Progress.start(context);
        }

        @Override
        protected String doInBackground(String... strings) {
            JsonParser jsonParser = new JsonParser(context);

            String url = Const.GET_LOG_OUT + strings[0];
            String jsonString = jsonParser.getJSONFromUrl(url);

            Log.e("jsonString", jsonString);

            try {
                JSONObject jsonObject = new JSONObject(jsonString);

                if (jsonObject != null) {
                    result = jsonObject.getInt(Const.KEY_RESULT);
                } else {
                    msg = jsonObject.getString(Const.KEY_MSG);
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
                userSession.logout();
                pollsPref.clearPollData();

                Intent intent = new Intent((Activity) context, LoginActivity.class);
                context.startActivity(intent);

            } else if (result == 0) {
                alertDialogManager.showAlertDialog(context, msg);
            } else {
                alertDialogManager.showAlertDialog(context, context.getString(R.string.server_not_responding));
            }
        }
    }


    public void getTimeOut(SnakeOnClick snakeOnClick, CoordinatorLayout coordinatorLayout) {

        if (Utility.isNetworkAvailable(context)) {
            new GetTimeOut().execute();
        } else {
            SnackNotify.showSnakeBar((Activity) context, snakeOnClick, coordinatorLayout);
        }

    }


//Asynchronous class to call GET_TIME_OUT API

    private class GetTimeOut extends AsyncTask<String, String, String> {
        int result = -1;
        String msg;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            Progress.start(context);
        }

        @Override
        protected String doInBackground(String... strings) {
            JsonParser jsonParser = new JsonParser(context);

            String url = Const.GET_PLAY_CALL_TIME;
            String jsonString = jsonParser.getJSONFromUrl(url);

            Log.e("jsonString", jsonString);

            try {
                JSONObject jsonObject = new JSONObject(jsonString);

                if (jsonObject != null) {
                    result = jsonObject.getInt(Const.KEY_RESULT);
                } else {
                    msg = jsonObject.getString(Const.KEY_MSG);
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
                //do something

            } else if (result == 0) {
                alertDialogManager.showAlertDialog(context, msg);
            } else {
                alertDialogManager.showAlertDialog(context, context.getString(R.string.server_not_responding));
            }
        }
    }


    public void getActiveUsers(SnakeOnClick snakeOnClick, CoordinatorLayout coordinatorLayout) {

        if (Utility.isNetworkAvailable(context)) {
            new GetActiveUsers().execute();
        } else {
            SnackNotify.showSnakeBar((Activity) context, snakeOnClick, coordinatorLayout);
        }

    }


//Asynchronous class to call GET_TIME_OUT API

    private class GetActiveUsers extends AsyncTask<String, String, String> {
        int result = -1;
        String msg;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            Progress.start(context);
        }

        @Override
        protected String doInBackground(String... strings) {
            JsonParser jsonParser = new JsonParser(context);

            String url = Const.GET_ACTIVE_USERS;
            String jsonString = jsonParser.getJSONFromUrl(url);

            Log.e("jsonString", jsonString);

            try {
                JSONObject jsonObject = new JSONObject(jsonString);

                if (jsonObject != null) {
                    result = jsonObject.getInt(Const.KEY_RESULT);
                } else {
                    msg = jsonObject.getString(Const.KEY_MSG);
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
                //do something
            } else if (result == 0) {
                alertDialogManager.showAlertDialog(context, msg);
            } else {
                alertDialogManager.showAlertDialog(context, context.getString(R.string.server_not_responding));
            }
        }

        //Asynchronous class to call GET_POLL_DATA API

        private class GetPollData extends AsyncTask<String, String, String> {
            int result = -1;
            String msg;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                Progress.start(context);
            }

            @Override
            protected String doInBackground(String... strings) {
                JsonParser jsonParser = new JsonParser(context);

                String url = Const.GET_ACTIVE_GAME_DETAIL;
                String jsonString = jsonParser.getJSONFromUrl(url);

                Log.e("jsonString", jsonString);

                try {
                    JSONObject jsonObject = new JSONObject(jsonString);

                    if (jsonObject != null) {
                        result = jsonObject.getInt(Const.KEY_RESULT);
                        if (result == 1) {
                            JSONObject jsonObjectPollData = jsonObject.getJSONObject(Const.KEY_POLL_DATA);

                            if (jsonObjectPollData != null) {
                                JSONArray jsonArrayPoleData = jsonObjectPollData.getJSONArray(Const.KEY_DATA);

                                for (int i = 0; i < jsonArrayPoleData.length(); i++) {
                                    JSONObject jsonObjectPoleOption = jsonArrayPoleData.getJSONObject(i);
                                    int id = jsonObjectPoleOption.getInt(Const.KEY_ID);

                                    if (id == 1) {
                                        pollsPref.storeDefenceJson(jsonObjectPoleOption.toString());
                                    }

                                    if (id == 2) {

                                    }

                                    if (id == 3) {

                                    }

                                    if (id == 4) {

                                    }

                                    if (id == 5) {

                                    }
                                }
                            }
                        }

                    } else {
                        msg = jsonObject.getString(Const.KEY_MSG);
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
                    //do something

                } else if (result == 0) {
                    alertDialogManager.showAlertDialog(context, msg);
                } else {
                    alertDialogManager.showAlertDialog(context, context.getString(R.string.server_not_responding));
                }
            }
        }


    }


}
