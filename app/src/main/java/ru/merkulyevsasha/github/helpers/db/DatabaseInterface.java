package ru.merkulyevsasha.github.helpers.db;

import java.util.ArrayList;

import ru.merkulyevsasha.github.models.Repo;
import rx.Observable;


public interface DatabaseInterface  {

    Observable<ArrayList<Repo>> getRepos(String login);
    Observable<ArrayList<Repo>> searchRepos(String login, String searchText);
    void saveRepos(String login, ArrayList<Repo> repos);
    void cleanRepos(String login);

}
