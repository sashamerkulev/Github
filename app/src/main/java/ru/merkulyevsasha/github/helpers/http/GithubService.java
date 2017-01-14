package ru.merkulyevsasha.github.helpers.http;


import android.util.Base64;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.merkulyevsasha.github.models.Auth;
import ru.merkulyevsasha.github.models.CommitInfo;
import ru.merkulyevsasha.github.models.Repo;
import rx.Observable;
import rx.schedulers.Schedulers;


public class GithubService implements HttpDataInterface{


    private final GithubInterface githubInterface;

    public GithubService(){

        RxJavaCallAdapterFactory adapter = RxJavaCallAdapterFactory.createWithScheduler(Schedulers.io());

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.github.com/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(adapter)
                .build();

        githubInterface = retrofit.create(GithubInterface.class);
    }

    @Override
    public Observable<ArrayList<Repo>> getRepos(String login, String password) {
        String credentials = login + ":" + password;
        final String basic = "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);

        return githubInterface.getRepos(basic);
    }

    @Override
    public Observable<ArrayList<Auth>> auth(String login, String password) {
        String credentials = login + ":" + password;
        final String basic = "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);

        return githubInterface.auth(basic);
    }

    @Override
    public Observable<ArrayList<CommitInfo>> getCommits(String login, String password, String owner, String repo) {
        String credentials = login + ":" + password;
        final String basic = "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);

        return githubInterface.getCommits(basic, owner, repo);
    }

//    public Call<ResponseBody> getRepos2(String login, String password) {
//
//        String credentials = login + ":" + password;
//        final String basic = "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
//
//        return githubInterface.getRepos2(basic);
//    }

//    public Call<ResponseBody> auth2(String login, String password) {
//
//        String credentials = login + ":" + password;
//        final String basic = "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
//
//        return githubInterface.auth2(basic);
//    }

//    public Call<ResponseBody> getCommits2(String login, String password, String owner, String repo) {
//
//        String credentials = login + ":" + password;
//        final String basic = "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
//
//        return githubInterface.getCommits2(basic, owner, repo);
//    }

}
