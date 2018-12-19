package com.freak.videosenfants.dagger;

import android.app.Application;

import dagger.android.AndroidInjector;
import dagger.android.DaggerApplication;

public interface ApplicationComponent extends AndroidInjector<DaggerApplication> {

    final class Initializer {

        private Initializer() {
            throw new UnsupportedOperationException();
        }

        public static ApplicationComponent init(Application application) {
            return DaggerApplicationComponent.builder()
                    .application(application)
                    .build();
        }
    }
}
