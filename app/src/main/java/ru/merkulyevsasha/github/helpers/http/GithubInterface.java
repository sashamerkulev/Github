package ru.merkulyevsasha.github.helpers.http;


import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;
import ru.merkulyevsasha.github.models.Auth;
import ru.merkulyevsasha.github.models.CommitInfo;
import ru.merkulyevsasha.github.models.Repo;
import rx.Observable;

public interface GithubInterface {

    @GET("user/repos")
    Observable<ArrayList<Repo>> getRepos(@Header("Authorization") String auth);

    @GET("authorizations")
    Observable<ArrayList<Auth>> auth(@Header("Authorization") String auth);

    @GET("repos/{owner}/{repo}/commits")
    Observable<ArrayList<CommitInfo>> getCommits(@Header("Authorization") String auth, @Path("owner") String owner, @Path("repo") String repo);


//    @GET("user/repos")
//    Call<ResponseBody> getRepos2(@Header("Authorization") String auth);
//
//    @GET("authorizations")
//    Call<ResponseBody> auth2(@Header("Authorization") String auth);
//
//    @GET("repos/{owner}/{repo}/commits")
//    Call<ResponseBody> getCommits2(@Header("Authorization") String auth, @Path("owner") String owner, @Path("repo") String repo);


}
