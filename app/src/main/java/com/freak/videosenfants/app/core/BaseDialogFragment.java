package com.freak.videosenfants.app.core;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;

import butterknife.Unbinder;
import icepick.Icepick;

public class BaseDialogFragment extends DialogFragment implements BaseContract.View {

    protected Unbinder mUnbinder;

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            Icepick.restoreInstanceState(this, savedInstanceState);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Icepick.restoreInstanceState(this, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (mUnbinder != null) {
            mUnbinder.unbind();
        } else {
            throw new UnsupportedOperationException("Unbinder not instantiated, can't unbind Fragment's views");
        }
    }
}
