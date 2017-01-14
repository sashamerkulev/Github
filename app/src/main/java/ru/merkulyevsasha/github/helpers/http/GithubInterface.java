package ru.merkulyevsasha.github.helpers.http;


import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import ru.merkulyevsasha.github.models.Auth;
import ru.merkulyevsasha.github.models.Repo;
import rx.Observable;

public interface GithubInterface {

    @GET("user/repos")
    Observable<ArrayList<Repo>> getRepos(@Header("Authorization") String auth);

//    @GET("user/repos")
//    Call<ResponseBody> getRepos2(@Header("Authorization") String auth);
//
//    @GET("authorizations")
//    Call<ResponseBody> auth2(@Header("Authorization") String auth);

    @GET("authorizations")
    Observable<ArrayList<Auth>> auth(@Header("Authorization") String auth);

}
