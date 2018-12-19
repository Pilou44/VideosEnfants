package com.freak.videosenfants.app.core;

import android.content.Intent;

public abstract class BaseRouter implements BaseContract.Router {

    protected void startActivityForResult(BaseContract.View view, Intent intent, int requestCode) {
        if (view instanceof BaseActivity) {
            ((BaseActivity) view).startActivityForResult(intent, requestCode);
        } else if (view instanceof BaseFragment) {
            ((BaseFragment) view).startActivityForResult(intent, requestCode);
        }
    }

    protected BaseActivity getActivity(BaseContract.View view) {
        if (view instanceof BaseActivity) {
            return (BaseActivity) view;
        } else if (view instanceof BaseFragment) {
            return (BaseActivity) ((BaseFragment) view).getActivity();
        } else if (view instanceof BaseDialogFragment) {
            return (BaseActivity) ((BaseDialogFragment) view).getActivity();
        } else {
            return null;
        }
    }
}
