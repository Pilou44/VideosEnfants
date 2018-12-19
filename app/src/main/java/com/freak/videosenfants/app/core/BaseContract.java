package com.freak.videosenfants.app.core;

import android.os.Bundle;

public interface BaseContract {

    interface View {

    }

    interface Presenter {
        void subscribe(BaseContract.View view);

        void unsubscribe(BaseContract.View view);

        void onRestore(Bundle bundle);

        void onSave(Bundle bundle);

    }

    interface Router {

    }
}
