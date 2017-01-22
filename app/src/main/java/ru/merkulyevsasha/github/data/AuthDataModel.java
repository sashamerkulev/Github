package ru.merkulyevsasha.github.data;

import java.util.ArrayList;

import ru.merkulyevsasha.github.models.Auth;
import rx.Observable;


public interface AuthDataModel {
    Observable<ArrayList<Auth>> auth(String login, String password);
}
