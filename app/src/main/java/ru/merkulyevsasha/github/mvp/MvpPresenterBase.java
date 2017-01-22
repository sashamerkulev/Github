package ru.merkulyevsasha.github.mvp;


public interface MvpPresenterBase {

    void onPause();
    void onResume(MvpView view);

}
