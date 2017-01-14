package ru.merkulyevsasha.github.mvp.repodetails;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import ru.merkulyevsasha.github.R;
import ru.merkulyevsasha.github.helpers.db.DbInterface;
import ru.merkulyevsasha.github.helpers.http.HttpDataInterface;
import ru.merkulyevsasha.github.models.CommitInfo;
import ru.merkulyevsasha.github.models.Credentials;
import ru.merkulyevsasha.github.models.Repo;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class CommitsPresenter {

    private final MvpDetailsListView mView;
    private final DbInterface mDb;
    private final HttpDataInterface mHttp;
    private final Credentials mCredentials;
    private final Repo mRepo;

    public CommitsPresenter(Repo repo, Credentials credentials, MvpDetailsListView view, DbInterface db, HttpDataInterface http){
        mView = view;
        mDb = db;
        mHttp = http;
        mCredentials = credentials;
        mRepo = repo;
    }


    public void load() {

//        mView.showProgress();
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
        List<CommitInfo> commits = mDb.getCommits(mRepo.getId());
        if (commits == null || commits.size() == 0){
            loadFromHttp();
        } else {
            //mView.hideProgress();
            mView.showList(commits);
        }
//            }
//        }).start();

    }

    public void search(final String searchText) {
//        mView.showProgress();
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
        List<CommitInfo> commits = mDb.searchCommits(mRepo.getId(), searchText);
        if (commits == null || commits.size() == 0){
            mView.hideProgress();
            mView.showMessage(R.string.search_nothing_found_message);
        } else {
            //mView.hideProgress();
            mView.showList(commits);
        }
//            }
//        }).start();

    }


    private Subscriber<ArrayList<CommitInfo>> getSubscriber() {
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

    public void loadFromHttp() {
        mView.showProgress();
        mHttp.getCommits(mCredentials.getLogin(), mCredentials.getPassword(), mRepo.getOwner().getLogin(), mRepo.getName())
                .doOnNext(saveCollectionToDb())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getSubscriber());

    }

}
