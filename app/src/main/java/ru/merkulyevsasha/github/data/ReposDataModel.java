package ru.merkulyevsasha.github.data;

import java.util.ArrayList;

import ru.merkulyevsasha.github.models.Repo;
import rx.Observable;


public interface ReposDataModel {

    Observable<ArrayList<Repo>> getRepos(String login, String password);
    Observable<ArrayList<Repo>> getNewRepos(String login, String password);
    Observable<ArrayList<Repo>> searchRepos(String login, String searchText);
}
