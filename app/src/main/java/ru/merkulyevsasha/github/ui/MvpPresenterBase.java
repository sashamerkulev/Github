package ru.merkulyevsasha.github.ui;


public interface MvpPresenterBase {

    void onPause();
    void onResume(MvpView view);

}
