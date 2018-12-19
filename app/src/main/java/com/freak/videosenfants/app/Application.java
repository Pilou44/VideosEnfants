package com.freak.videosenfants.app;

import com.freak.videosenfants.dagger.ApplicationComponent;

import dagger.android.AndroidInjector;
import dagger.android.DaggerApplication;

public class Application extends DaggerApplication {
    @Override
    protected AndroidInjector<? extends DaggerApplication> applicationInjector() {
        ApplicationComponent applicationComponent = ApplicationComponent.Initializer.init(this);
        applicationComponent.inject(this);
        return applicationComponent;
    }
}
