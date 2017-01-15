package ru.merkulyevsasha.github.mvp.repolist;

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


    private PreferencesHelper mPref;

    private ListViewAdapter mListAdaper;

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

        showData(savedInstanceState);

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mPresenter != null) {
            mPresenter.onDestroy();
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
    public void showList(List<Repo> repos) {
        mListAdaper.clear();
        mListAdaper.addAll(repos);
        mListAdaper.notifyDataSetChanged();
    }
}
