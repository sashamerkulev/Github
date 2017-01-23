package ru.merkulyevsasha.github.ui;



public interface MvpView {

    void showProgress();
    void hideProgress();

    void showMessage(int message);
    void showLogin();

}
