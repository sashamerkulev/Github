package ru.merkulyevsasha.github.data;

import java.util.ArrayList;

import ru.merkulyevsasha.github.data.http.HttpServiceInterface;
import ru.merkulyevsasha.github.models.Auth;
import rx.Observable;


public class AuthDataModelImpl implements AuthDataModel {

    private HttpServiceInterface mHttp;

    // https://habrahabr.ru/post/27108/
    private static volatile AuthDataModelImpl mInstance;
    public static AuthDataModelImpl getInstance(HttpServiceInterface http) {
        if (mInstance == null) {
            synchronized (ReposDataModelImpl.class) {
                if (mInstance == null) {
                    mInstance = new AuthDataModelImpl(http);
                }
            }
        }
        return mInstance;
    }

    private AuthDataModelImpl(HttpServiceInterface http){
        mHttp = http;
    }


    @Override
    public Observable<ArrayList<Auth>> auth(String login, String password) {
        return mHttp.auth(login, password);
    }
}
