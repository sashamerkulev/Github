package ru.merkulyevsasha.github.data.http;


import java.util.ArrayList;

import ru.merkulyevsasha.github.models.Auth;
import ru.merkulyevsasha.github.models.CommitInfo;
import ru.merkulyevsasha.github.models.Repo;
import rx.Observable;

public interface HttpServiceInterface {

    Observable<ArrayList<Repo>> getRepos(String login, String password);

    Observable<ArrayList<Auth>> auth(String login, String password);

    Observable<ArrayList<CommitInfo>> getCommits(String login, String password, String owner, String repo);

}
