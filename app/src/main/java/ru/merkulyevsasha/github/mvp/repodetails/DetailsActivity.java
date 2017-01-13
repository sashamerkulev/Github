package ru.merkulyevsasha.github.mvp.repodetails;

import android.os.Bundle;

import ru.merkulyevsasha.github.R;
import ru.merkulyevsasha.github.models.Credentials;
import ru.merkulyevsasha.github.helpers.prefs.PreferencesHelper;
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

    }

}
