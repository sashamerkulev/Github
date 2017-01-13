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
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;

import ru.merkulyevsasha.github.R;
import ru.merkulyevsasha.github.helpers.db.DbHelper;
import ru.merkulyevsasha.github.helpers.http.HttpHelper;
import ru.merkulyevsasha.github.models.Credentials;
import ru.merkulyevsasha.github.helpers.prefs.PreferencesHelper;
import ru.merkulyevsasha.github.models.Repo;
import ru.merkulyevsasha.github.mvp.BaseActivity;
import ru.merkulyevsasha.github.mvp.repodetails.DetailsActivity;
import rx.schedulers.Schedulers;

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
    private Credentials mCred;

    private ReposPresenter mPresenter;

    public RecyclerView mRecyclerView;
    private RecyclerViewAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(KEY_SEARCHTEXT, mSearchText);
        //outState.putInt(KEY_POSITION, mLayoutManager.findFirstCompletelyVisibleItemPosition());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRootView = findViewById(R.id.activity_main);

        mRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_refresh);
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener(){
            @Override
            public void onRefresh() {
                refresh();
            }
        });

        mPref = new PreferencesHelper(this);
        mCred = mPref.getCredentials();
        if (mCred == null){
            startLoginActivityAndFinish();
        }

        mPresenter = new ReposPresenter(mCred.getLogin(), this, new DbHelper(this, Schedulers.io()), new HttpHelper());

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView = (RecyclerView)findViewById(R.id.recyclerview);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mAdapter = new RecyclerViewAdapter(this, new ArrayList<Repo>());
        mRecyclerView.setAdapter(mAdapter);

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
        mPresenter.loadFromHttp();
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        if (query.length() < 5) {
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
            mPresenter.loadFromDb();
        }
        return false;
    }

    @Override
    public void showDetails(Repo repo) {
        Intent activity = new Intent(this, DetailsActivity.class);
        activity.putExtra(KEY_REPO, repo.getId());
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
        mAdapter.mItems = repos;
        mAdapter.notifyDataSetChanged();
    }
}
