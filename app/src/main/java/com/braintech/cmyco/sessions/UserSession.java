package com.braintech.cmyco.sessions;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Braintech on 7/22/2015.
 */
public class UserSession {
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context context;
    int PRIVATE_MODE = 0;

    // Shared preferences file name
    private static final String PREF_NAME = "user_pref";

    // All Shared Preferences Keys
    private static final String IS_LOGIN = "is_user_logged_in";
    private static final String IS_REMEMBERED = "is_remembered";

    public static final String KEY_EMAIL = "email";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_USER_ID = "user_id";
    public static final String KEY_NAME = "name";
    public static final String KEY_PROFILE_IMAGE = "profile_image";


    public UserSession(Context _context) {
        this.context = _context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    /**
     * Create login session
     */
    public void createUserSession(String email, String user_id, String name) {
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);
        // Storing name in pref

        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_NAME, name);


        // commit changes
        editor.commit();
    }

    public void storeLoginDetail(String email,String password,String userId){
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_PASSWORD, password);
        editor.putString(KEY_USER_ID, userId);

        editor.commit();

    }

    /* Get User ID/ */
    public String getUserID() {
        return pref.getString(KEY_USER_ID, null);
    }

    public String getKeyEmail() {
        return pref.getString(KEY_EMAIL, null);
    }

    public String getKeyPassword() {
        return pref.getString(KEY_PASSWORD, null);
    }

    public String getKeyName(){return pref.getString(KEY_NAME, null);}

    public String getKeyProfileImage(){return pref.getString(KEY_PROFILE_IMAGE, null);}

    public void saveUserPassword(String password) {
        editor.putString(KEY_PASSWORD, password);
        editor.putBoolean(IS_REMEMBERED, true);
        editor.commit();
    }



    /**
     * Clear session details
     */

    public void clearUserSession() { // Clearing all data from Shared
        editor.clear();
        editor.commit();
    }

    public void logout() { // Clearing all data from Shared

        editor.remove(IS_LOGIN);
        editor.remove(KEY_USER_ID);
        editor.remove(KEY_NAME);
        editor.commit();
    }

}
