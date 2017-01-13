package ru.merkulyevsasha.github.mvp;



public interface MvpView {

    void showProgress();
    void hideProgress();

    void showMessage(int message);

}
