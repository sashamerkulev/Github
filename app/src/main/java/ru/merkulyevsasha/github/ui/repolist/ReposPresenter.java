package ru.merkulyevsasha.github.ui.repolist;


import java.util.ArrayList;

import ru.merkulyevsasha.github.R;
import ru.merkulyevsasha.github.data.ReposDataModel;
import ru.merkulyevsasha.github.models.Credentials;
import ru.merkulyevsasha.github.models.Repo;
import ru.merkulyevsasha.github.ui.MvpPresenter;
import ru.merkulyevsasha.github.ui.MvpView;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class ReposPresenter  implements MvpPresenter {

    private MvpListView mView;
    private final Credentials mCredentials;
    private final ReposDataModel mReposDataModel;

    private Subscription mSubscription;

    public ReposPresenter(Credentials credentials, ReposDataModel reposDataModel) {
        mCredentials = credentials;
        mReposDataModel = reposDataModel;
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

    private void unsubscribe(){
        if (mSubscription !=null && !mSubscription.isUnsubscribed()){
            mSubscription.unsubscribe();
        }
    }

    @Override
    public void search(final String searchText) {
        mView.showProgress();
        unsubscribe();
        mSubscription = mReposDataModel.searchRepos(mCredentials.getLogin(), searchText)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getSubscriber());
    }

    @Override
    public void load(final String searchText) {

        if (mCredentials == null || mCredentials.getLogin() == null || mCredentials.getLogin().isEmpty()){
            mView.showLogin();
        }

        if (searchText == null || searchText.isEmpty()) {
            mView.showProgress();
            unsubscribe();
            mSubscription = mReposDataModel.getRepos(mCredentials.getLogin(), mCredentials.getPassword())
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(getSubscriber());
        } else {
            search(searchText);
        }
    }

    @Override
    public void loadFromHttp() {
        mView.showProgress();
        unsubscribe();
        mSubscription = mReposDataModel.getNewRepos(mCredentials.getLogin(), mCredentials.getPassword())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getSubscriber());
    }

    private Subscriber<ArrayList<Repo>> getSubscriber(){
        return new Subscriber<ArrayList<Repo>>() {
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
        };
    }

}
