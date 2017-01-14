package ru.merkulyevsasha.github.mvp.repodetails;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import ru.merkulyevsasha.github.R;
import ru.merkulyevsasha.github.helpers.db.RepoSQLiteOpenHelper;
import ru.merkulyevsasha.github.helpers.http.GithubService;
import ru.merkulyevsasha.github.models.CommitInfo;
import ru.merkulyevsasha.github.models.Credentials;
import ru.merkulyevsasha.github.helpers.prefs.PreferencesHelper;
import ru.merkulyevsasha.github.models.Repo;
import ru.merkulyevsasha.github.mvp.BaseActivity;
import ru.merkulyevsasha.github.mvp.repolist.ReposPresenter;


public class DetailsActivity extends BaseActivity implements MvpDetailsListView {

    public static String KEY_REPO = "repo";

    private PreferencesHelper mPref;
    private Credentials mCred;

    private SwipeRefreshLayout mRefreshLayout;
    private View mRootView;

    public RecyclerView mRecyclerView;
    private DetailsRecyclerViewAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;

    private CommitsPresenter mPresenter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        mPref = new PreferencesHelper(this);
        mCred = mPref.getCredentials();
        if (mCred == null){
            startLoginActivityAndFinish();
        }

        ActionBar ab = getSupportActionBar();
        if (ab != null){
            ab.setDisplayHomeAsUpEnabled(true);
        }

        mRootView = findViewById(R.id.details_root_view);

        mRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_refresh);
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener(){
            @Override
            public void onRefresh() {
                refresh();
            }
        });

        Intent intent = getIntent();
        final Repo repo = intent.getParcelableExtra(KEY_REPO);
        setTitle(repo.getName());

        TextView owner = (TextView)findViewById(R.id.textview_owner);
        TextView descr = (TextView)findViewById(R.id.textview_description);
        TextView forks = (TextView)findViewById(R.id.textview_forks);
        TextView watchers = (TextView)findViewById(R.id.textview_watchers);

        owner.setText(repo.getOwner().getLogin());
        descr.setText(repo.getDescription()==null?"":repo.getDescription());
        forks.setText(String.valueOf(repo.getForksCount()));
        watchers.setText(String.valueOf(repo.getWatchersCount()));

        String avatarUrl = repo.getOwner().getAvatarUrl();
        if (avatarUrl != null && !avatarUrl.isEmpty()) {
            ImageView avatar = (ImageView)findViewById(R.id.imageview_owner_avatar);
            Picasso.with(this).load(avatarUrl).into(avatar);
        }

        mPresenter = new CommitsPresenter(repo, mCred, this, new RepoSQLiteOpenHelper(this), new GithubService());

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView = (RecyclerView)findViewById(R.id.recyclerview);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mAdapter = new DetailsRecyclerViewAdapter(new ArrayList<CommitInfo>());
        mRecyclerView.setAdapter(mAdapter);

        mPresenter.load();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void refresh(){
        mPresenter.loadFromHttp();
    }

    @Override
    public void showList(List<CommitInfo> commits) {
        mAdapter.mItems = commits;
        mAdapter.notifyDataSetChanged();
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
}
