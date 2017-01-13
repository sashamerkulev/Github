package ru.merkulyevsasha.github.mvp;


import java.util.List;

import ru.merkulyevsasha.github.models.Repo;

public interface MvpView {

    void showProgress();
    void hideProgress();

    void showMessage(int message);

    void showList(List<Repo> repos);

}
