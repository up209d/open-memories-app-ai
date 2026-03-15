package com.sony.imaging.app.graduatedfilter.shooting.layout;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.sony.imaging.app.base.shooting.layout.ShootingLayout;
import com.sony.imaging.app.graduatedfilter.common.GFCommonUtil;

/* loaded from: classes.dex */
public class GFShootingLayout extends ShootingLayout {
    @Override // com.sony.imaging.app.base.shooting.layout.ShootingLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override // com.sony.imaging.app.base.shooting.layout.ShootingLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        if (GFCommonUtil.getInstance().disableShootingOnPlayback()) {
            Handler mHandler = new Handler();
            Runnable mRun = new Runnable() { // from class: com.sony.imaging.app.graduatedfilter.shooting.layout.GFShootingLayout.1
                @Override // java.lang.Runnable
                public void run() {
                    GFCommonUtil.getInstance().clearHoldKey();
                }
            };
            mHandler.postDelayed(mRun, 2000L);
        }
        super.onResume();
    }

    @Override // com.sony.imaging.app.base.shooting.layout.ShootingLayout, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        GFCommonUtil.getInstance().clearHoldKey();
        super.onPause();
    }
}
