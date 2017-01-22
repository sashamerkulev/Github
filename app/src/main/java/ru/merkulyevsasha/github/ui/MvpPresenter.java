package ru.merkulyevsasha.github.ui;


public interface MvpPresenter extends MvpPresenterBase{

    void load();
    void loadFromHttp();
    void search(final String searchText);

}
