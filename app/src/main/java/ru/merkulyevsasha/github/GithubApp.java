package ru.merkulyevsasha.github;

import android.app.Application;

import ru.merkulyevsasha.github.dagger2.AppModule;
import ru.merkulyevsasha.github.dagger2.DaggerDataComponent;
import ru.merkulyevsasha.github.dagger2.DataComponent;
import ru.merkulyevsasha.github.dagger2.DataModule;


public class GithubApp extends Application {

    private static DataComponent component;

    @Override
    public void onCreate() {
        super.onCreate();
        component = DaggerDataComponent.builder()
                .appModule(new AppModule(this))
                .dataModule(new DataModule())
                .build();
    }

    public static DataComponent getComponent() {
        return component;
    }

}
