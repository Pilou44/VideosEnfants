package com.freak.videosenfants.dagger;

import android.app.Application;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.freak.videosenfants.domain.content.DatabaseContent;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;

@Module
public class ApplicationModule {
    private final Context mContext;

    public ApplicationModule(Application context) {
        mContext = context.getApplicationContext();
    }

    @Provides
    @Singleton
    Context provideContext() {
        return mContext;
    }

    @Provides
    @Singleton
    Scheduler provideScheduler() {
        return AndroidSchedulers.mainThread();
    }

    @Provides
    @Singleton
    SharedPreferences provideSharedPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Provides
    @Singleton
    DatabaseContent provideDatabase(Context context) {
        return Room.databaseBuilder(context.getApplicationContext(),
                DatabaseContent.class, "videos").build();
    }
}
