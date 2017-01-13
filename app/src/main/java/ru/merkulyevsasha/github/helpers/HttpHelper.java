package ru.merkulyevsasha.github.helpers;


import java.util.ArrayList;

import ru.merkulyevsasha.github.models.Repo;
import rx.Observable;

public class HttpHelper implements DataInterface{

    @Override
    public Observable<ArrayList<Repo>> getRepos(){

        return Observable.just(new ArrayList<Repo>());
    }

}
