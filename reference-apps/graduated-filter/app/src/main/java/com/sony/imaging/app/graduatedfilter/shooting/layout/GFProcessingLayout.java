package com.sony.imaging.app.graduatedfilter.shooting.layout;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.sony.imaging.app.fw.Layout;
import com.sony.imaging.app.graduatedfilter.R;
import com.sony.imaging.app.graduatedfilter.common.AppLog;

/* loaded from: classes.dex */
public class GFProcessingLayout extends Layout {
    private static final String TAG = AppLog.getClassName();

    @Override // com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = obtainViewFromPool(R.layout.shooting_processing);
        return view;
    }
}
