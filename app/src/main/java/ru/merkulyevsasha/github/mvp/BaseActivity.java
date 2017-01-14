package ru.merkulyevsasha.github.mvp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import ru.merkulyevsasha.github.LoginActivity;


public class BaseActivity extends AppCompatActivity {

    protected void startLoginActivityAndFinish(){
        Intent login = new Intent(this, LoginActivity.class);
        startActivity(login);
        finish();
    }


}
