package ru.merkulyevsasha.github;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import ru.merkulyevsasha.github.helpers.prefs.PreferencesHelper;

public class LoginActivity extends AppCompatActivity {


    private PreferencesHelper mPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mPref = new PreferencesHelper(this);
        mPref.saveCredentials("sashamerkulev", "bl@ck,dr@g0n");

//        Intent login = new Intent(this, MainActivity.class);
//        startActivity(login);
//        finish();

    }

}