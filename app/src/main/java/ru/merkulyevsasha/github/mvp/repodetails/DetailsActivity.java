package ru.merkulyevsasha.github.mvp.repodetails;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;

import ru.merkulyevsasha.github.R;
import ru.merkulyevsasha.github.models.Credentials;
import ru.merkulyevsasha.github.helpers.prefs.PreferencesHelper;
import ru.merkulyevsasha.github.models.Repo;
import ru.merkulyevsasha.github.mvp.BaseActivity;


public class DetailsActivity extends BaseActivity {

    public static String KEY_REPO = "repo";

    private PreferencesHelper mPref;
    private Credentials mCred;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPref = new PreferencesHelper(this);
        mCred = mPref.getCredentials();
        if (mCred == null){
            startLoginActivityAndFinish();
        }

        ActionBar ab = getSupportActionBar();
        if (ab != null){
            ab.setDisplayHomeAsUpEnabled(true);
        }

        Intent intent = getIntent();
        final Repo repo = intent.getParcelableExtra(KEY_REPO);
        setTitle(repo.getFullName());

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


}
