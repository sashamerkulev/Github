package ru.merkulyevsasha.github.data;


import java.util.ArrayList;

import ru.merkulyevsasha.github.models.CommitInfo;
import rx.Observable;

public interface CommitsDataModel {

    Observable<ArrayList<CommitInfo>> getCommits(int repoId, String repoName, String login, String password);
    Observable<ArrayList<CommitInfo>> getNewCommits(int repoId, String repoName, String login, String password);
    Observable<ArrayList<CommitInfo>> searchCommits(int repoId, String searchText);
}
