package ru.merkulyevsasha.github.mvp.repolist;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import ru.merkulyevsasha.github.R;
import ru.merkulyevsasha.github.helpers.db.RepoSQLiteOpenHelper;
import ru.merkulyevsasha.github.helpers.http.GithubService;
import ru.merkulyevsasha.github.models.Credentials;
import ru.merkulyevsasha.github.helpers.prefs.PreferencesHelper;
import ru.merkulyevsasha.github.models.Repo;
import ru.merkulyevsasha.github.mvp.BaseActivity;
import ru.merkulyevsasha.github.mvp.repodetails.DetailsActivity;

import static ru.merkulyevsasha.github.mvp.repodetails.DetailsActivity.KEY_REPO;

public class MainActivity extends BaseActivity
        implements SearchView.OnQueryTextListener
        , MvpListView {

    private static final String KEY_SEARCHTEXT = "searchtext";

    private SwipeRefreshLayout mRefreshLayout;
    private View mRootView;

    private MenuItem mSearchItem;
    private SearchView mSearchView;
    private String mSearchText;

    private PreferencesHelper mPref;

    private ReposPresenter mPresenter;

    private ListViewAdapter mListAdaper;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(KEY_SEARCHTEXT, mSearchText);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPref = new PreferencesHelper(this);
        Credentials mCred = mPref.getCredentials();
        if (mCred == null){
            startLoginActivityAndFinish();
        }

        mRootView = findViewById(R.id.activity_main);

        mRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_refresh);
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener(){
            @Override
            public void onRefresh() {
                refresh();
            }
        });

        mPresenter = new ReposPresenter(mCred, this, new RepoSQLiteOpenHelper(this), new GithubService());

        mListAdaper = new ListViewAdapter(this, new ArrayList<Repo>());
        ListView mListView = (ListView) findViewById(R.id.listview_listdata);
        mListView.setAdapter(mListAdaper);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showDetails(mListAdaper.getItem(position));
            }
        });

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

    private void searchViewText() {
        mSearchItem.expandActionView();
        mSearchView.setQuery(mSearchText, false);
    }

    private void refresh(){
        mSearchItem.collapseActionView();
        mSearchText = "";
        mSearchView.setQuery(mSearchText, false);

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

    @Override
    public void showDetails(Repo repo) {
        Intent activity = new Intent(this, DetailsActivity.class);
        activity.putExtra(KEY_REPO, repo);
        startActivity(activity);
    }

    @Override
    public void showProgress() {
        mRefreshLayout.setRefreshing(true);
    }

    @Override
    public void hideProgress() {
        mRefreshLayout.setRefreshing(false);
    }

    @Override
    public void showMessage(int message) {
        Snackbar.make(mRootView, message, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void showList(List<Repo> repos) {
        mListAdaper.clear();
        mListAdaper.addAll(repos);
        mListAdaper.notifyDataSetChanged();
    }
}
