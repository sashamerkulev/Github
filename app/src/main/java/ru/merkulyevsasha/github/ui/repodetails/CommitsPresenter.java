package ru.merkulyevsasha.github.ui.repodetails;


import java.util.ArrayList;

import ru.merkulyevsasha.github.R;
import ru.merkulyevsasha.github.data.CommitsDataModel;
import ru.merkulyevsasha.github.models.CommitInfo;
import ru.merkulyevsasha.github.models.Credentials;
import ru.merkulyevsasha.github.models.Repo;
import ru.merkulyevsasha.github.ui.MvpPresenter;
import ru.merkulyevsasha.github.ui.MvpView;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

class CommitsPresenter implements MvpPresenter {

    private MvpDetailsListView mView;
    private final Credentials mCredentials;
    private final Repo mRepo;

    private Subscription mSubscription;

    private CommitsDataModel mCommitsDataModel;

    public CommitsPresenter(Repo repo, Credentials credentials, CommitsDataModel commitsDataModel){
        mCredentials = credentials;
        mRepo = repo;
        mCommitsDataModel = commitsDataModel;
    }

    private void unsubscribe(){
        if (mSubscription !=null && !mSubscription.isUnsubscribed()){
            mSubscription.unsubscribe();
        }
    }

    @Override
    public void onPause(){
        mView = null;
        unsubscribe();
    }

    @Override
    public void onResume(MvpView view) {
        mView = (MvpDetailsListView)view;
    }

    @Override
    public void load() {
        mView.showProgress();
        unsubscribe();
        mSubscription = mCommitsDataModel.getCommits(mRepo.getId(), mRepo.getName(), mCredentials.getLogin(), mCredentials.getPassword())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getSubscriber());

    }

    @Override
    public void search(final String searchText) {
        mView.showProgress();
        unsubscribe();
        mSubscription = mCommitsDataModel.searchCommits(mRepo.getId(), searchText)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getSubscriber());
    }

    @Override
    public void loadFromHttp() {
        mView.showProgress();
        unsubscribe();
        mSubscription = mCommitsDataModel.getNewCommits(mRepo.getId(), mRepo.getName(), mCredentials.getLogin(), mCredentials.getPassword())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getSubscriber());
    }

    private Subscriber<ArrayList<CommitInfo>> getSubscriber(){
        return new Subscriber<ArrayList<CommitInfo>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                mView.hideProgress();
                mView.showMessage(R.string.read_data_message);
            }

            @Override
            public void onNext(ArrayList<CommitInfo> commits) {
                if (commits == null || commits.size() == 0){
                    mView.hideProgress();
                    mView.showMessage(R.string.search_nothing_found_message);
                } else {
                    mView.hideProgress();
                    mView.showList(commits);
                }
            }
        };
    }

}
