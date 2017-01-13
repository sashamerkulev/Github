package ru.merkulyevsasha.github.helpers;


import java.util.ArrayList;

import ru.merkulyevsasha.github.models.Repo;
import rx.Observable;

public interface DataInterface {

    Observable<ArrayList<Repo>> getRepos();

}
