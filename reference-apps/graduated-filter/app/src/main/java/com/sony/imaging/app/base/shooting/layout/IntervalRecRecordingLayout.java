package com.sony.imaging.app.base.shooting.layout;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.sony.imaging.app.base.R;
import com.sony.imaging.app.fw.Layout;

/* loaded from: classes.dex */
public class IntervalRecRecordingLayout extends Layout {
    private static final String TAG = "IntervalRecRecordingLayout";

    @Override // com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return obtainViewFromPool(R.layout.shooting_main_sid_sublcd_interval_recording);
    }
}
