package ru.merkulyevsasha.github.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import ru.merkulyevsasha.github.LoginActivity;


@SuppressLint("Registered")
public class BaseActivity extends AppCompatActivity{

    protected SwipeRefreshLayout mRefreshLayout;
    protected View mRootView;

    protected void startLoginActivityAndFinish(){
        Intent login = new Intent(this, LoginActivity.class);
        startActivity(login);
        finish();
    }

    public void showProgress() {
        mRefreshLayout.setRefreshing(true);
    }

    public void hideProgress() {
        mRefreshLayout.setRefreshing(false);
    }

    public void showMessage(int message) {
        Snackbar.make(mRootView, message, Snackbar.LENGTH_LONG).show();
    }

}
