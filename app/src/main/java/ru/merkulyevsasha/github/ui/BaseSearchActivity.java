package ru.merkulyevsasha.github.ui;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.MenuItem;

@SuppressLint("Registered")
public class BaseSearchActivity extends BaseActivity {

    protected static final String KEY_SEARCHTEXT = "searchtext";

    protected MenuItem mSearchItem;
    protected SearchView mSearchView;
    protected String mSearchText;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(KEY_SEARCHTEXT, mSearchText);
    }

    protected void searchViewText() {
        mSearchItem.expandActionView();
        mSearchView.setQuery(mSearchText, false);
    }

    protected void initSearchViewText(){
        mSearchItem.collapseActionView();
        mSearchText = "";
        mSearchView.setQuery(mSearchText, false);
    }

}
