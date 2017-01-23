package ru.merkulyevsasha.github.ui.repolist;

import android.content.Intent;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import ru.merkulyevsasha.github.GithubApp;
import ru.merkulyevsasha.github.R;
import ru.merkulyevsasha.github.data.prefs.PreferencesHelper;
import ru.merkulyevsasha.github.models.Repo;
import ru.merkulyevsasha.github.ui.BaseSearchActivity;
import ru.merkulyevsasha.github.ui.repodetails.DetailsActivity;

import static ru.merkulyevsasha.github.ui.repodetails.DetailsActivity.KEY_REPO;

public class MainActivity extends BaseSearchActivity
        implements SearchView.OnQueryTextListener
        , MvpListView {

    @Inject
    public PreferencesHelper mPref;
    @Inject
    public ReposPresenter mPresenter;

    private ListViewAdapter mListAdaper;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean(KEY_REFRESHING, mRefreshLayout.isRefreshing());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GithubApp.getComponent().inject(this);

        mRootView = findViewById(R.id.activity_main);

        mRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_refresh);
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener(){
            @Override
            public void onRefresh() {
                refresh();
            }
        });

        mListAdaper = new ListViewAdapter(this, new ArrayList<Repo>());
        ListView mListView = (ListView) findViewById(R.id.listview_listdata);
        mListView.setAdapter(mListAdaper);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showDetails(mListAdaper.getItem(position));
            }
        });

        if (savedInstanceState != null) {
            mSearchText = savedInstanceState.getString(KEY_SEARCHTEXT);
            mRefreshLayout.setRefreshing(savedInstanceState.getBoolean(KEY_REFRESHING, false));
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mPresenter != null) {
            mPresenter.onPause();
        }
    }

    @Override
    protected void onResume() {
        super.onPause();
        if (mPresenter != null) {
            mPresenter.onResume(this);
            if (mRefreshLayout.isRefreshing()){
                mPresenter.loadFromHttp();
            } else {
                mPresenter.load(mSearchText);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        mSearchItem = menu.findItem(R.id.action_search);
        mSearchView = (SearchView) MenuItemCompat.getActionView(mSearchItem);
        if (mSearchText != null && !mSearchText.isEmpty()) {
            searchViewText();
        }
        mSearchView.setOnQueryTextListener(this);

        MenuItem refreshItem = menu.findItem(R.id.action_refresh);
        refreshItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if (!mRefreshLayout.isRefreshing()) {
                    refresh();
                }
                return false;
            }
        });

        MenuItem logoutItem = menu.findItem(R.id.action_logout);
        logoutItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {

                mPref.cleanCredentials();
                startLoginActivityAndFinish();
                return false;
            }
        });
        return true;
    }

    @Override
    public void showDetails(Repo repo) {
        Intent activity = new Intent(this, DetailsActivity.class);
        activity.putExtra(KEY_REPO, repo);
        startActivity(activity);
        overridePendingTransition(R.anim.enter_from_left, R.anim.exit_out_right);
    }

    @Override
    public void showLogin() {
        startLoginActivityAndFinish();
    }

    @Override
    public void showList(List<Repo> repos) {
        mListAdaper.clear();
        mListAdaper.addAll(repos);
        mListAdaper.notifyDataSetChanged();
    }

    protected void refresh(){
        initSearchViewText();
        mPresenter.loadFromHttp();
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

}
