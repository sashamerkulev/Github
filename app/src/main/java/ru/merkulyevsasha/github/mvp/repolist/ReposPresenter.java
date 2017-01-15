package ru.merkulyevsasha.github.mvp.repolist;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import ru.merkulyevsasha.github.R;
import ru.merkulyevsasha.github.helpers.db.DbInterface;
import ru.merkulyevsasha.github.helpers.http.HttpDataInterface;
import ru.merkulyevsasha.github.models.Credentials;
import ru.merkulyevsasha.github.models.Repo;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;


public class ReposPresenter {

    private final MvpListView mView;
    private final DbInterface mDb;
    private final HttpDataInterface mHttp;
    private final Credentials mCredentials;

    public ReposPresenter(Credentials credentials, MvpListView view, DbInterface db, HttpDataInterface http) {
        mView = view;
        mDb = db;
        mHttp = http;
        mCredentials = credentials;
    }

    public void search(final String searchText) {

        mView.showProgress();
        Observable.just(mDb.searchRepos(mCredentials.getLogin(), searchText))
                .subscribe(new Subscriber<ArrayList<Repo>>() {
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
                        if (repos == null || repos.size() == 0) {
                            mView.hideProgress();
                            mView.showMessage(R.string.search_nothing_found_message);
                        } else {
                            mView.showList(repos);
                            mView.hideProgress();
                        }
                    }
                });

    }

    public void load() {

        mView.showProgress();
        Observable.just(mDb.getRepos(mCredentials.getLogin()))
                .subscribe(new Subscriber<ArrayList<Repo>>() {
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
                        if (repos == null || repos.size() == 0) {
                            loadFromHttp();
                        } else {
                            mView.showList(repos);
                            mView.hideProgress();
                        }
                    }
                });

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

                Collections.sort(repos, new Comparator<Repo>() {
                    @Override
                    public int compare(Repo o1, Repo o2) {
                        return o1.getName().compareTo(o2.getName());
                    }
                });

                mView.showList(repos);
                mView.hideProgress();
            }
        };
    }

    private Action1<ArrayList<Repo>> saveCollectionToDb(){
        return new Action1<ArrayList<Repo>>() {
            @Override
            public void call(ArrayList<Repo> repos) {
                mDb.cleanRepos(mCredentials.getLogin());
                mDb.saveRepos(mCredentials.getLogin(), repos);
            }
        };
    }

    public void loadFromHttp() {
        mView.showProgress();
        mHttp.getRepos(mCredentials.getLogin(), mCredentials.getPassword())
                .doOnNext(saveCollectionToDb())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getHttpSubscriber());

    }

}
