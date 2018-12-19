package com.freak.videosenfants.dagger;

import android.app.Application;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjector;
import dagger.android.DaggerApplication;
import dagger.android.support.AndroidSupportInjectionModule;


@Singleton
@Component(modules = {
        AndroidSupportInjectionModule.class,
        ActivityBuilder.class
})
public interface ApplicationComponent extends AndroidInjector<DaggerApplication> {

    @Component.Builder
    interface Builder {
        @BindsInstance
        Builder application(Application application);

        ApplicationComponent build();
    }

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
