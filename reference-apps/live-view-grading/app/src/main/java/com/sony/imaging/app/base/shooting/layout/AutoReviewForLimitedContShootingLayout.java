package com.sony.imaging.app.base.shooting.layout;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import com.sony.imaging.app.base.R;
import com.sony.imaging.app.fw.Layout;

/* loaded from: classes.dex */
public class AutoReviewForLimitedContShootingLayout extends Layout {
    private static final String TAG = "AutoReviewForLimitedContShootingLayout";

    @Override // com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        RelativeLayout layout = (RelativeLayout) obtainViewFromPool(R.layout.shooting_main_sid_autoreview_limited_continuous_shooting);
        return layout;
    }
}
