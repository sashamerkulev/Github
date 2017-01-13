package ru.merkulyevsasha.github.mvp.repolist;


import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import ru.merkulyevsasha.github.R;
import ru.merkulyevsasha.github.helpers.DataInterface;
import ru.merkulyevsasha.github.helpers.DatabaseInterface;
import ru.merkulyevsasha.github.models.Repo;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;


public class ReposPresenter {

    private MvpListView mView;
    private DatabaseInterface mDb;
    private DataInterface mHttp;
    private String mLogin;

    public ReposPresenter(String login, MvpListView view, DatabaseInterface db, DataInterface http) {
        mView = view;
        mDb = db;
        mHttp = http;
        mLogin = login;
    }

    public void search(String searchText) {
        mView.showProgress();

        mDb.searchRepos(mLogin, searchText)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getSubscriber());

    }

    public void load() {
        mView.showProgress();

        final Observable<ArrayList<Repo>> repoDb = mDb.getRepos(mLogin);
        final Observable<ArrayList<Repo>> repoHttp = mHttp.getRepos();

        if (repoDb == null){
            repoHttp
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(getHttpSubscriber());
        } else {
            final AtomicInteger size = new AtomicInteger(0);
            repoDb.flatMap(new Func1<ArrayList<Repo>, Observable<ArrayList<Repo>>>() {
                @Override
                public Observable<ArrayList<Repo>> call(ArrayList<Repo> repos) {
                    if (repos != null ){
                        size.set(repos.size());
                    }
                    return repos == null || repos.isEmpty()? repoHttp : Observable.just(repos);
                }
            })
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(size.get()==0? getHttpSubscriber() : getSubscriber());
        }
    }

    private Subscriber<ArrayList<Repo>> getSubscriber() {
        return new Subscriber<ArrayList<Repo>>() {
            @Override
            public void onCompleted() {
                mView.hideProgress();
            }

            @Override
            public void onError(Throwable e) {
                mView.hideProgress();
                mView.showMessage(R.string.read_data_message);
            }

            @Override
            public void onNext(ArrayList<Repo> repos) {
                mView.showList(repos);
                mView.hideProgress();
            }
        };
    }

    private void load(DataInterface data) {
        mView.showProgress();
        data.getRepos()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getSubscriber());
    }

    public void loadFromDb() {
        mView.showProgress();
        mDb.getRepos(mLogin)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getSubscriber());
    }

    public void loadFromHttp() {
        mView.showProgress();
        mHttp.getRepos()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getHttpSubscriber());

    }

    private Subscriber<ArrayList<Repo>> getHttpSubscriber() {
        return new Subscriber<ArrayList<Repo>>() {
            @Override
            public void onCompleted() {
                mView.hideProgress();
            }

            @Override
            public void onError(Throwable e) {
                mView.hideProgress();
                mView.showMessage(R.string.read_data_message);
            }

            @Override
            public void onNext(ArrayList<Repo> repos) {
                mDb.saveRepos(mLogin, repos);
                mView.showList(repos);
                mView.hideProgress();
            }
        };
    }

}
