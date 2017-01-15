package ru.merkulyevsasha.github.mvp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.MenuItem;
import android.view.View;

import ru.merkulyevsasha.github.LoginActivity;
import ru.merkulyevsasha.github.R;


@SuppressLint("Registered")
public class BaseActivity extends AppCompatActivity
        implements SearchView.OnQueryTextListener{

    protected static final String KEY_SEARCHTEXT = "searchtext";

    protected SwipeRefreshLayout mRefreshLayout;
    protected View mRootView;

    protected MenuItem mSearchItem;
    protected SearchView mSearchView;
    protected String mSearchText;

    protected MvpPresenter mPresenter;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(KEY_SEARCHTEXT, mSearchText);
    }

    protected void searchViewText() {
        mSearchItem.expandActionView();
        mSearchView.setQuery(mSearchText, false);
    }

    protected void refresh(){
        mSearchItem.collapseActionView();
        mSearchText = "";
        mSearchView.setQuery(mSearchText, false);

        mPresenter.loadFromHttp();
    }

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

    @Override
    public boolean onQueryTextSubmit(String query) {
        if (query.length() < 3) {
            showMessage(R.string.search_validation_message);
            return false;
        }
        mSearchText = query;
        mPresenter.search(query);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (newText.isEmpty()) {
            mSearchText = newText;
            mPresenter.search(newText);
        }
        return false;
    }

    protected void showData(Bundle savedInstanceState){
        if (savedInstanceState == null) {
            mPresenter.load();
        } else {
            mSearchText = savedInstanceState.getString(KEY_SEARCHTEXT);

            if (mSearchText == null || mSearchText.isEmpty()){
                mPresenter.load();
            } else {
                mPresenter.search(mSearchText);
            }
        }
    }

}
