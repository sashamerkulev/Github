package ru.merkulyevsasha.github.mvp;


public interface MvpPresenter {

    void load();
    void loadFromHttp();
    void search(final String searchText);

}
