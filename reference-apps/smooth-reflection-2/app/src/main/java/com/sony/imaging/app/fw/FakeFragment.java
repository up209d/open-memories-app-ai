package com.sony.imaging.app.fw;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/* loaded from: classes.dex */
public class FakeFragment {
    private static final String BOOTED = " booted";
    private static final String BOOTING = " booting";
    private static final String DETACHED = " detached";
    private static final String DETACHING = " detaching";
    private static final String FULL_RESTARTED = " restarted full";
    private static final String FULL_RESTARTING = " restarting full";
    private static final String RESTARTED = " restarted";
    private static final String RESTARTING = " restarting";
    private static final String SHUT = " shut";
    private static final String SHUTTING = " shutting";
    private static final String TAG = "FakeFragment";
    private static final StringBuilder log_string = new StringBuilder();
    Activity mActivity = null;
    private View mView;

    /* JADX INFO: Access modifiers changed from: package-private */
    public void start(Activity act) {
        String className = getClass().getSimpleName();
        log_string.replace(0, log_string.length(), className).append(BOOTING);
        Log.i(TAG, log_string.toString());
        this.mActivity = act;
        onCreate(null);
        this.mView = onCreateView(this.mActivity.getLayoutInflater(), null, null);
        add();
        onViewCreated(this.mView, null);
        onResume();
        log_string.replace(0, log_string.length(), className).append(BOOTED);
        Log.i(TAG, log_string.toString());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void stop() {
        String className = getClass().getSimpleName();
        log_string.replace(0, log_string.length(), className).append(SHUTTING);
        Log.i(TAG, log_string.toString());
        onPause();
        remove();
        onDestroyView();
        onDestroy();
        this.mActivity = null;
        this.mView = null;
        log_string.replace(0, log_string.length(), className).append(SHUT);
        Log.i(TAG, log_string.toString());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void restartPartial() {
        String className = getClass().getSimpleName();
        log_string.replace(0, log_string.length(), className).append(RESTARTING);
        Log.i(TAG, log_string.toString());
        onPause();
        onResume();
        log_string.replace(0, log_string.length(), className).append(RESTARTED);
        Log.i(TAG, log_string.toString());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void restartFull() {
        String className = getClass().getSimpleName();
        log_string.replace(0, log_string.length(), className).append(FULL_RESTARTING);
        Log.i(TAG, log_string.toString());
        onPause();
        int index = remove();
        onDestroyView();
        onDestroy();
        this.mView = null;
        onCreate(null);
        this.mView = onCreateView(this.mActivity.getLayoutInflater(), null, null);
        add(index);
        onViewCreated(this.mView, null);
        onResume();
        log_string.replace(0, log_string.length(), className).append(FULL_RESTARTED);
        Log.i(TAG, log_string.toString());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void detach() {
        String className = getClass().getSimpleName();
        log_string.replace(0, log_string.length(), className).append(DETACHING);
        Log.i(TAG, log_string.toString());
        onPause();
        int index = remove();
        onDestroyView();
        this.mView = onCreateView(this.mActivity.getLayoutInflater(), null, null);
        add(index);
        onViewCreated(this.mView, null);
        onResume();
        log_string.replace(0, log_string.length(), className).append(DETACHED);
        Log.i(TAG, log_string.toString());
    }

    public void onCreate(Bundle bundle) {
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return null;
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
    }

    public void onResume() {
    }

    public void onPause() {
    }

    public void onDestroyView() {
    }

    public void onDestroy() {
    }

    private final int add(int index) {
        if (this.mView != null) {
            ViewGroup view = (ViewGroup) ((AppRoot) this.mActivity).getView();
            view.addView(this.mView, index);
        }
        return index;
    }

    private final int add() {
        if (this.mView == null) {
            return -1;
        }
        ViewGroup view = (ViewGroup) ((AppRoot) this.mActivity).getView();
        view.addView(this.mView);
        int index = view.indexOfChild(this.mView);
        return index;
    }

    private final int remove() {
        if (this.mView == null) {
            return -1;
        }
        ViewGroup view = (ViewGroup) ((AppRoot) this.mActivity).getView();
        int index = view.indexOfChild(this.mView);
        view.removeView(this.mView);
        this.mView = null;
        return index;
    }

    public View getView() {
        return this.mView;
    }

    public final Activity getActivity() {
        return this.mActivity;
    }

    public final Resources getResources() {
        return this.mActivity.getResources();
    }

    public final CharSequence getText(int resId) {
        return getResources().getText(resId);
    }

    public final String getString(int resId) {
        return getResources().getString(resId);
    }

    public final String getString(int resId, Object... formatArgs) {
        return getResources().getString(resId, formatArgs);
    }
}
