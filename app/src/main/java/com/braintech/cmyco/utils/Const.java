package com.braintech.cmyco.utils;

/**
 * Created by Braintech on 29-Oct-15.
 */
public class Const {

    public static int TIME_OUT = 6000;

    public static String BASE_URL = "http://braintechnosys.net/cmyco/";


    public static String SIGN_IN = BASE_URL + "users/app_login1?";
    public static String SIGN_UP = BASE_URL + "users/app_user_registration1?";
    public static String FORGET_PASSWORD = BASE_URL + "users/app_forgot_password1?";
    public static String GET_COACH_RESULT = BASE_URL + "coaches/listall";
    public static String GET_TEAM_RESULT = BASE_URL + "teams/listall?coach=";
    public static String GET_INSTRUCTION = BASE_URL + "pages/content?page_id=";
    public static String GET_INSTRUCTION2 = BASE_URL + "pages/content?page_id=2";
    public static String GET_LOG_OUT = BASE_URL + "users/app_logout1?user_id=";
    public static String GET_PLAY_CALL_TIME = BASE_URL + "users/app_settings1";
    public static String GET_ACTIVE_USERS = BASE_URL + "users/app_active_users1";
    public static String GET_ACTIVE_GAME_DETAIL = BASE_URL + "games/app_active_game1";
    public static String RATING = BASE_URL + "ratings/app_add1";
    public static String GET_GRAPH=BASE_URL+ "ratings/app_ratings1?";
    public static String POST_BACKGROUND=BASE_URL+"users/app_lastactivity?";




    //Tag login activity
    public static String TAG_USERNAME = "username=";
    public static String TAG_PASSWORD = "&password=";
    public static String KEY_NAME = "name";
    public static String KEY_TEXT = "text";
    public static String KEY_TEAM1 = "team1";
    public static String KEY_TEAM2 = "team2";
    public static String KEY_GAME_TITLE = "title";
    public static String KEY_COACH = "coach";
    public static String KEY_PLAYERS = "players";
    public static String KEY_GAME_DATA = "gamedata";
    public static String KEY_GAME = "game";
    public static String KEY_ACTIVE_GAME = "active_game";

    //key common activity
    public static String KEY_RESULT = "result";
    public static String KEY_MSG = "message";
    public static String KEY_DATA = "data";
    public static String KEY_ID = "id";


    //key Login activity

    public static String KEY_USERNAME = "username";
    public static String KEY_EMAIL = "email";

    //registration Tag
    public static String TAG_REG_USERNAME = "&username=";
    public static String TAG_REG_CONFIRM_PASSWORD = "&cpassword=";
    public static String TAG_EMAIL = "email=";

    //Active game
    public static String TAG_TEAMID = "team_id";

    public static String KEY_POLL_OPTION = "poll_options";
    public static String KEY_POLL_DATA = "polldata";
    public static String KEY_POLL_ID = "poll_id";
    public static String KEY_POLL_NAME = "poll_name";
    public static String KEY_START_TIME = "poll_start_time";
    public static String KEY_END_TIME = "poll_end_time";
    public static String KEY_POLL_DURATION = "poll_duration";
    public static String KEY_POLL_SERVER_TIME = "server_time";
    public static String KEY_MAX_ID = "max_id";
    public static String KEY_MAX = "max";
    public static String KEY_TIME_ZONE ="timezone";


    //Rating TAG
    public static String TAG_USER_ID = "user_id=";
    public static String TAG_GAME_ID = "&game_id=";
    public static String TAG_POLL_ID = "&poll_id=";
    public static String TAG_POLL_OPTION = "&poll_option=";
    public static String TAG_TEAM_ID = " &team_id=";

    //Active Users
    public static String KEY_ACTIVE_USER = "active_user";

    //Background variables for logout
    public static String KEY_STATUS="&status=B";

    //Intent varaibles
    public static String KEY_BUTTON_CLICKED="isButtonClicked";




}
