package ru.merkulyevsasha.github.helpers.http;


import java.util.ArrayList;

import ru.merkulyevsasha.github.models.Repo;
import rx.Observable;

public class HttpHelper implements HttpDataInterface {

    @Override
    public Observable<ArrayList<Repo>> getRepos(){

        return Observable.just(new ArrayList<Repo>());
    }

}
