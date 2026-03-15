package com.sony.imaging.app.lightgraffiti.shooting.layout;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.sony.imaging.app.base.shooting.layout.FocusLayout;
import com.sony.imaging.app.base.shooting.widget.AFViews;

/* loaded from: classes.dex */
public class LGFakeFocusLayout extends FocusLayout {
    private View mMainView = null;

    @Override // com.sony.imaging.app.base.shooting.layout.FocusLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        if (this.mMainView == null) {
            this.mMainView = new AFViews(getActivity(), null);
        }
        this.mCurrentLayout.addView(this.mMainView);
        return this.mCurrentLayout;
    }
}
