package ru.merkulyevsasha.github.ui.repodetails;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import ru.merkulyevsasha.github.GithubApp;
import ru.merkulyevsasha.github.R;
import ru.merkulyevsasha.github.data.CommitsDataModel;
import ru.merkulyevsasha.github.models.CommitInfo;
import ru.merkulyevsasha.github.data.prefs.PreferencesHelper;
import ru.merkulyevsasha.github.models.Repo;
import ru.merkulyevsasha.github.ui.BaseSearchActivity;


public class DetailsActivity extends BaseSearchActivity
        implements SearchView.OnQueryTextListener,
        MvpDetailsListView {

    public static final String KEY_REPO = "repo";

    @Inject
    public CommitsDataModel mCommitsDataModel;
    @Inject
    public PreferencesHelper mPref;
    @Inject
    public CommitsPresenter mPresenter;

    private Repo mRepo;

    private DetailsRecyclerViewAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        GithubApp.getComponent().inject(this);

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
        mRepo = intent.getParcelableExtra(KEY_REPO);
        setTitle(mRepo.getName());

        TextView owner = (TextView)findViewById(R.id.textview_owner);
        TextView descr = (TextView)findViewById(R.id.textview_description);
        TextView forks = (TextView)findViewById(R.id.textview_forks);
        TextView watchers = (TextView)findViewById(R.id.textview_watchers);

        owner.setText(mRepo.getOwner().getLogin());
        descr.setText(mRepo.getDescription()==null?"":mRepo.getDescription());
        forks.setText(String.valueOf(mRepo.getForksCount()));
        watchers.setText(String.valueOf(mRepo.getWatchersCount()));

        String avatarUrl = mRepo.getOwner().getAvatarUrl();
        if (avatarUrl != null && !avatarUrl.isEmpty()) {
            ImageView avatar = (ImageView)findViewById(R.id.imageview_owner_avatar);
            Picasso.with(this).load(avatarUrl).into(avatar);
        }

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mAdapter = new DetailsRecyclerViewAdapter(new ArrayList<CommitInfo>());
        mRecyclerView.setAdapter(mAdapter);

        if (savedInstanceState != null) {
            mSearchText = savedInstanceState.getString(KEY_SEARCHTEXT);
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
        super.onResume();
        if (mPresenter != null) {
            mPresenter.onResume(this);
            mPresenter.setRepo(mRepo);
            mPresenter.load(mSearchText);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.enter_from_left, R.anim.exit_out_right);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_details, menu);

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

        return true;
    }

    @Override
    public void showLogin() {
        startLoginActivityAndFinish();
    }

    @Override
    public void showList(List<CommitInfo> commits) {
        mAdapter.mItems = commits;
        mAdapter.notifyDataSetChanged();
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
