package ru.merkulyevsasha.github.mvp;


public interface MvpPresenter extends MvpPresenterBase{

    void load();
    void loadFromHttp();
    void search(final String searchText);

}
