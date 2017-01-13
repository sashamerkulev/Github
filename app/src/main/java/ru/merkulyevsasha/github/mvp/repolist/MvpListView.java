package ru.merkulyevsasha.github.mvp.repolist;

import ru.merkulyevsasha.github.models.Repo;
import ru.merkulyevsasha.github.mvp.MvpView;


public interface MvpListView extends MvpView{

    void showDetails(Repo repo);

}
