package ru.merkulyevsasha.github.helpers.http;


import java.util.ArrayList;

import ru.merkulyevsasha.github.models.Auth;
import ru.merkulyevsasha.github.models.Repo;
import rx.Observable;

public interface HttpDataInterface {

    Observable<ArrayList<Repo>> getRepos(String login, String password);

    Observable<ArrayList<Auth>> auth(String login, String password);

}
