package ru.merkulyevsasha.github.helpers.db;

import java.util.ArrayList;

import ru.merkulyevsasha.github.models.CommitInfo;
import ru.merkulyevsasha.github.models.Repo;
import rx.Observable;

public interface DbInterface {

    Observable<ArrayList<Repo>> getRepos(String login);
    Observable<ArrayList<Repo>> searchRepos(String login, String searchText);
    void saveRepos(String login, ArrayList<Repo> repos);
    void cleanRepos(String login);

    void cleanCommits(int repoId);
    void saveCommits(int repoId, ArrayList<CommitInfo> commits);
    Observable<ArrayList<CommitInfo>> getCommits(int repoId);
    Observable<ArrayList<CommitInfo>> searchCommits(int repoId, String searchText);

}
