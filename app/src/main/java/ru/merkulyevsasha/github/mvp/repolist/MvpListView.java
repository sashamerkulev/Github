package ru.merkulyevsasha.github.mvp.repolist;

import java.util.List;

import ru.merkulyevsasha.github.models.Repo;
import ru.merkulyevsasha.github.mvp.MvpView;


public interface MvpListView extends MvpView{

    void showList(List<Repo> repos);
    void showDetails(Repo repo);

}
