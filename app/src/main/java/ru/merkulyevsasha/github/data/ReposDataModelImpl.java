package ru.merkulyevsasha.github.data;


import java.util.ArrayList;

import ru.merkulyevsasha.github.data.db.DatabaseServiceInterface;
import ru.merkulyevsasha.github.data.http.HttpServiceInterface;
import ru.merkulyevsasha.github.models.Repo;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

public class ReposDataModelImpl implements ReposDataModel {

    private DatabaseServiceInterface mDatabase;
    private HttpServiceInterface mHttp;

    // https://habrahabr.ru/post/27108/
    private static volatile ReposDataModelImpl mInstance;
    public static ReposDataModelImpl getInstance(DatabaseServiceInterface db, HttpServiceInterface http) {
        if (mInstance == null) {
            synchronized (ReposDataModelImpl.class) {
                if (mInstance == null) {
                    mInstance = new ReposDataModelImpl(db, http);
                }
            }
        }
        return mInstance;
    }

    private ReposDataModelImpl(DatabaseServiceInterface db, HttpServiceInterface http){
        mDatabase = db;
        mHttp = http;
    }

    private Action1<ArrayList<Repo>> saveCollectionToDb(final String login){
        return new Action1<ArrayList<Repo>>() {
            @Override
            public void call(ArrayList<Repo> repos) {
                mDatabase.cleanRepos(login);
                mDatabase.saveRepos(login, repos);
            }
        };
    }

    @Override
    public Observable<ArrayList<Repo>> getRepos(final String login, final String password) {
        return mDatabase.getRepos(login)
                .flatMap(new Func1<ArrayList<Repo>, Observable<ArrayList<Repo>>>() {
                    @Override
                    public Observable<ArrayList<Repo>> call(ArrayList<Repo> repos) {
                        return repos.size()==0 ? getNewRepos(login, password)
                                : Observable.just(repos);
                    }
                });
    }

    @Override
    public Observable<ArrayList<Repo>> getNewRepos(String login, String password) {
        return mHttp.getRepos(login, password)
                .doOnNext(saveCollectionToDb(login))
                .sorted();
    }

    @Override
    public Observable<ArrayList<Repo>> searchRepos(String login, String searchText) {
        return mDatabase.searchRepos(login, searchText);
    }

}
