package com.usac.brayan.mensajeriaarquitectura;

import java.util.HashMap;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class SessionManager {
    // Shared Preferences
    SharedPreferences pref;

    // Editor for Shared preferences
    Editor editor;

    // Context
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Sharedpref file name
    private static final String PREF_NAME = "AndroidHivePref";

    // All Shared Preferences Keys
    private static final String IS_LOGIN = "IsLoggedIn";

    // User name (make variable public to access from outside)
    public static final String KEY_NAME = "name";

    public static final String ROLE = "ROLE"; //1. Estudiante, 2.Maestro, 3.Super
    public static final String CARNE = "CARNE";
    public static final String LASTPUBLICATIONREGISTER = "LASTPUBLICATIONREGISTER";

    // Constructor
    public SessionManager(Context context){
        this._context = context;
        pref = PreferenceManager.getDefaultSharedPreferences(_context);
        editor = pref.edit();
    }

    /**
     * Create login session
     * */
    public void createLoginSession(String name,int role,String carne){
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);

        // Storing name in pref
        editor.putString(KEY_NAME, name);

        editor.putInt(ROLE, role);
        editor.putString(CARNE, carne);


        // commit changes
        editor.commit();
    }

    public int getLastPublicationRegister(){
        return pref.getInt(LASTPUBLICATIONREGISTER,-1);
    }

    public void setLastPublicationRegister(int value){
        editor.putInt(LASTPUBLICATIONREGISTER,value);
    }

    /**
     * Check login method wil check user login status
     * If false it will redirect user to login page
     * Else won't do anything
     * */
    public void checkLogin(){
        // Check login status
        if(!this.isLoggedIn()){
            // user is not logged in redirect him to Login Activity
            Intent i = new Intent(_context, Autenticacion.class);
            // Closing all the Activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Staring Login Activity
            _context.startActivity(i);
        }

    }



    /**
     * Get stored session data
     * */
    public String getName(){
        // user name

        return pref.getString(KEY_NAME, null);
    }

    public String getId(){
        // user name

        return pref.getString(CARNE, "");
    }

    public int getRole(){

        return pref.getInt(ROLE,-1);
    }

    public void logoutUser(){
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();

        // After logout redirect user to Loing Activity
        Intent i = new Intent(_context, Autenticacion.class);
        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Add new Flag to start new Activity
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Staring Login Activity
        _context.startActivity(i);
        _context.stopService(new Intent(_context,ServicioNotificacionesFARUSAC.class));
    }

    /**
     * Quick check for login
     * **/
    // Get Login State
    public boolean isLoggedIn(){
        return pref.getBoolean(IS_LOGIN, false);
    }
}