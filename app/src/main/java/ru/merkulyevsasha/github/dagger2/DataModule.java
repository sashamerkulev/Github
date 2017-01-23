package ru.merkulyevsasha.github.dagger2;



import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import ru.merkulyevsasha.github.data.AuthDataModel;
import ru.merkulyevsasha.github.data.AuthDataModelImpl;
import ru.merkulyevsasha.github.data.CommitsDataModel;
import ru.merkulyevsasha.github.data.CommitsDataModelImpl;
import ru.merkulyevsasha.github.data.ReposDataModel;
import ru.merkulyevsasha.github.data.ReposDataModelImpl;
import ru.merkulyevsasha.github.data.db.DatabaseServiceHelper;
import ru.merkulyevsasha.github.data.db.DatabaseServiceInterface;
import ru.merkulyevsasha.github.data.http.GithubService;
import ru.merkulyevsasha.github.data.http.HttpServiceInterface;
import ru.merkulyevsasha.github.data.prefs.PreferencesHelper;
import ru.merkulyevsasha.github.ui.repodetails.CommitsPresenter;
import ru.merkulyevsasha.github.ui.repolist.ReposPresenter;
import rx.schedulers.Schedulers;

@Module
public class DataModule {

    @Singleton
    @Provides
    PreferencesHelper providesPreferencesHelper(Context context) {
        return new PreferencesHelper(context);
    }

    @Singleton
    @Provides
    DatabaseServiceInterface providesDatabaseService(Context context) {
        return new DatabaseServiceHelper(context, Schedulers.io());
    }

    @Singleton
    @Provides
    HttpServiceInterface providesHttpService() {
        return new GithubService();
    }

    @Singleton
    @Provides
    ReposDataModel providesReposDataModel(DatabaseServiceInterface db, HttpServiceInterface http) {
        return ReposDataModelImpl.getInstance(db, http);
    }

    @Singleton
    @Provides
    CommitsDataModel providesCommitsDataModel(DatabaseServiceInterface db, HttpServiceInterface http) {
        return CommitsDataModelImpl.getInstance(db, http);
    }

    @Singleton
    @Provides
    AuthDataModel providesAuthDataModel(HttpServiceInterface http) {
        return AuthDataModelImpl.getInstance(http);
    }

    @Singleton
    @Provides
    ReposPresenter providesReposPresenter(PreferencesHelper helper, ReposDataModel reposDataModel) {
        return new ReposPresenter(helper.getCredentials(), reposDataModel);
    }

    @Singleton
    @Provides
    CommitsPresenter providesCommitsPresenter(PreferencesHelper helper, CommitsDataModel commitsDataModel) {
        return new CommitsPresenter(helper.getCredentials(), commitsDataModel);
    }

}
