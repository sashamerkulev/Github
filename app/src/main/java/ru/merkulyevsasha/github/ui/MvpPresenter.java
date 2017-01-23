package ru.merkulyevsasha.github.ui;


public interface MvpPresenter extends MvpPresenterBase{

    void load(final String searchText);
    void loadFromHttp();
    void search(final String searchText);

}
