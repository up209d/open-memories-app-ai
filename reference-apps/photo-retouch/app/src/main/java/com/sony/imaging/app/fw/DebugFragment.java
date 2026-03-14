package com.sony.imaging.app.fw;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/* loaded from: classes.dex */
public class DebugFragment extends FakeFragment {
    @Override // com.sony.imaging.app.fw.FakeFragment
    public void onCreate(Bundle savedInstanceState) {
        Log.d(getClass().getSimpleName(), "onCreate");
        super.onCreate(savedInstanceState);
    }

    @Override // com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(getClass().getSimpleName(), "onCreateView");
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override // com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        Log.d(getClass().getSimpleName(), "onResume");
        super.onResume();
    }

    @Override // com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        Log.d(getClass().getSimpleName(), "onPause");
        super.onPause();
    }

    @Override // com.sony.imaging.app.fw.FakeFragment
    public void onDestroyView() {
        Log.d(getClass().getSimpleName(), "onDestroyView");
        super.onDestroyView();
    }

    @Override // com.sony.imaging.app.fw.FakeFragment
    public void onDestroy() {
        Log.d(getClass().getSimpleName(), "onDestroy");
        super.onDestroy();
    }

    @Override // com.sony.imaging.app.fw.FakeFragment
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Log.d(getClass().getSimpleName(), "onViewCreated");
        super.onViewCreated(view, savedInstanceState);
    }
}
