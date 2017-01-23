package ru.merkulyevsasha.github.data;

import java.util.ArrayList;

import ru.merkulyevsasha.github.data.db.DatabaseServiceInterface;
import ru.merkulyevsasha.github.data.http.HttpServiceInterface;
import ru.merkulyevsasha.github.models.CommitInfo;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;


public class CommitsDataModelImpl implements CommitsDataModel {

    private DatabaseServiceInterface mDatabase;
    private HttpServiceInterface mHttp;

    // https://habrahabr.ru/post/27108/
    private static volatile CommitsDataModelImpl mInstance;
    public static CommitsDataModelImpl getInstance(DatabaseServiceInterface db, HttpServiceInterface http) {
        if (mInstance == null) {
            synchronized (ReposDataModelImpl.class) {
                if (mInstance == null) {
                    mInstance = new CommitsDataModelImpl(db, http);
                }
            }
        }
        return mInstance;
    }

    private CommitsDataModelImpl(DatabaseServiceInterface db, HttpServiceInterface http){
        mDatabase = db;
        mHttp = http;
    }

    @Override
    public Observable<ArrayList<CommitInfo>> getCommits(final int repoId, final String repoName, final String login, final String password) {
        return mDatabase.getCommits(repoId)
                .flatMap(new Func1<ArrayList<CommitInfo>, Observable<ArrayList<CommitInfo>>>() {
                    @Override
                    public Observable<ArrayList<CommitInfo>> call(ArrayList<CommitInfo> commits) {
                        return commits.size()==0 ? getNewCommits(repoId, repoName, login, password)
                                : Observable.just(commits);
                    }
                }).cache();
    }

    private Action1<ArrayList<CommitInfo>> saveCollectionToDb(final int repoId){
        return new Action1<ArrayList<CommitInfo>>() {
            @Override
            public void call(ArrayList<CommitInfo> commits) {
                mDatabase.cleanCommits(repoId);
                mDatabase.saveCommits(repoId, commits);
            }
        };
    }

    @Override
    public Observable<ArrayList<CommitInfo>> getNewCommits(int repoId, String repoName, String login, String password) {
        return mHttp.getCommits(login, password, login, repoName)
                .doOnNext(saveCollectionToDb(repoId))
                .cache();
    }

    @Override
    public Observable<ArrayList<CommitInfo>> searchCommits(int repoId, String searchText) {
        return mDatabase.searchCommits(repoId, searchText);
    }
}
