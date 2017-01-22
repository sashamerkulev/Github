package ru.merkulyevsasha.github.dagger2;


import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {

    private final Context mAppContext;

    public AppModule(Context appContext){
        mAppContext = appContext;
    }

    @Singleton
    @Provides
    Context providesContext() {
        return mAppContext;
    }


}
