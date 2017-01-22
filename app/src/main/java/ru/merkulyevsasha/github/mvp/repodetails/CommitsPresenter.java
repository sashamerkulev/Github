package ru.merkulyevsasha.github.mvp.repodetails;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import ru.merkulyevsasha.github.R;
import ru.merkulyevsasha.github.helpers.db.DbInterface;
import ru.merkulyevsasha.github.helpers.http.HttpDataInterface;
import ru.merkulyevsasha.github.models.CommitInfo;
import ru.merkulyevsasha.github.models.Credentials;
import ru.merkulyevsasha.github.models.Repo;
import ru.merkulyevsasha.github.mvp.MvpPresenter;
import ru.merkulyevsasha.github.mvp.MvpView;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

class CommitsPresenter implements MvpPresenter {

    private MvpDetailsListView mView;
    private final DbInterface mDb;
    private final HttpDataInterface mHttp;
    private final Credentials mCredentials;
    private final Repo mRepo;

    private Subscription mDbSubscription;
    private Subscription mHttpSubscription;

    public CommitsPresenter(Repo repo, Credentials credentials, DbInterface db, HttpDataInterface http){
        mDb = db;
        mHttp = http;
        mCredentials = credentials;
        mRepo = repo;
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
        mDbSubscription = mDb.getCommits(mRepo.getId())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ArrayList<CommitInfo>>() {
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
                            loadFromHttp();
                        } else {
                            mView.hideProgress();
                            mView.showList(commits);
                        }
                    }
                });

    }

    @Override
    public void search(final String searchText) {
        mView.showProgress();
        unsubscribe();
        mDbSubscription = mDb.searchCommits(mRepo.getId(), searchText)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ArrayList<CommitInfo>>() {
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
                            mView.showList(commits);
                            mView.hideProgress();
                        }
                    }
                });

    }


    private Subscriber<ArrayList<CommitInfo>> getHttpSubscriber() {
        return new Subscriber<ArrayList<CommitInfo>>() {
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
            public void onNext(ArrayList<CommitInfo> commits) {

                Collections.sort(commits, new Comparator<CommitInfo>() {
                    @Override
                    public int compare(CommitInfo o1, CommitInfo o2) {
                        return -o1.getCommit().getAuthor().getDate().compareTo(o2.getCommit().getAuthor().getDate());
                    }
                });

                mView.showList(commits);
                mView.hideProgress();
            }
        };
    }

    private Action1<ArrayList<CommitInfo>> saveCollectionToDb(){
        return new Action1<ArrayList<CommitInfo>>() {
            @Override
            public void call(ArrayList<CommitInfo> commits) {
                mDb.cleanCommits(mRepo.getId());
                mDb.saveCommits(mRepo.getId(), commits);
            }
        };
    }

    @Override
    public void loadFromHttp() {
        mView.showProgress();
        unsubscribe();
        mHttpSubscription = mHttp.getCommits(mCredentials.getLogin(), mCredentials.getPassword(), mRepo.getOwner().getLogin(), mRepo.getName())
                .doOnNext(saveCollectionToDb())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getHttpSubscriber());

    }

}
