package ru.merkulyevsasha.github.helpers.prefs;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;

import ru.merkulyevsasha.github.models.Credentials;

public class PreferencesHelper {

    private static final String APP_PREFERENCES = "githubclient";
    private static final String KEY_LOGIN = "login";
    private static final String KEY_PASSWORD = "password";

    private final SharedPreferences mSharedPreferences;

    public PreferencesHelper(Context context){
        mSharedPreferences = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
    }

    public void cleanCredentials(){
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(KEY_LOGIN, "");
        editor.putString(KEY_PASSWORD, "");
        editor.apply();
    }

    public void saveCredentials(String login, String password){
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(KEY_LOGIN, login);
        editor.putString(KEY_PASSWORD, password);
        editor.apply();
    }

    @Nullable
    public Credentials getCredentials(){
        String login = mSharedPreferences.getString(KEY_LOGIN, "");
        String password = mSharedPreferences.getString(KEY_PASSWORD, "");

        return login.isEmpty()? null : new Credentials(login, password);
    }


}
