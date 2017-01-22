package ru.merkulyevsasha.github.mvp.repolist;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import ru.merkulyevsasha.github.R;
import ru.merkulyevsasha.github.helpers.db.DbInterface;
import ru.merkulyevsasha.github.helpers.http.HttpDataInterface;
import ru.merkulyevsasha.github.models.Credentials;
import ru.merkulyevsasha.github.models.Repo;
import ru.merkulyevsasha.github.mvp.MvpPresenter;
import ru.merkulyevsasha.github.mvp.MvpView;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;


public class ReposPresenter  implements MvpPresenter {

    private MvpListView mView;
    private final DbInterface mDb;
    private final HttpDataInterface mHttp;
    private final Credentials mCredentials;

    private Subscription mDbSubscription;
    private Subscription mHttpSubscription;

    public ReposPresenter(Credentials credentials, DbInterface db, HttpDataInterface http) {
        mDb = db;
        mHttp = http;
        mCredentials = credentials;
    }

    @Override
    public void onPause(){
        mView = null;
        unsubscribe();
    }

    @Override
    public void onResume(MvpView view){
        mView = (MvpListView)view;
    }


    private void unsubscribe(Subscription subscription){
        if (subscription !=null && !subscription.isUnsubscribed()){
            subscription.unsubscribe();
        }
    }

    private void unsubscribe(){
        unsubscribe(mDbSubscription);
        unsubscribe(mHttpSubscription);
    }

    @Override
    public void search(final String searchText) {

        mView.showProgress();
        unsubscribe();
        mDbSubscription = mDb.searchRepos(mCredentials.getLogin(), searchText)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ArrayList<Repo>>() {
                    @Override
                    public void onCompleted() {

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

    @Override
    public void load() {

        mView.showProgress();
        unsubscribe();
        mDbSubscription = mDb.getRepos(mCredentials.getLogin())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ArrayList<Repo>>() {
                    @Override
                    public void onCompleted() {

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

    @Override
    public void loadFromHttp() {
        mView.showProgress();
        unsubscribe();
        mHttpSubscription = mHttp.getRepos(mCredentials.getLogin(), mCredentials.getPassword())
                .doOnNext(saveCollectionToDb())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getHttpSubscriber());

    }

}
