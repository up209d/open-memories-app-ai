package com.sony.imaging.app.portraitbeauty.common;

import android.view.View;
import android.view.ViewGroup;
import com.sony.imaging.app.base.common.IModableLayout;
import com.sony.imaging.app.base.common.OnLayoutModeChangeListener;
import com.sony.imaging.app.fw.Layout;
import com.sony.imaging.app.portraitbeauty.R;
import com.sony.imaging.app.portraitbeauty.shooting.PortraitBeautyDisplayModeObserver;
import com.sony.scalar.hardware.avio.DisplayManager;

/* loaded from: classes.dex */
public class EVFOffLayout extends Layout implements IModableLayout {
    private final String TAG = EVFOffLayout.class.getSimpleName();
    private View mDonotUseEVF = null;
    private boolean isToggled = true;
    private DisplayManager mDisplayManager = null;
    private OnLayoutModeChangeListener mLayoutChangeListener = new OnLayoutModeChangeListener(this, 1);

    @Override // com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        this.isToggled = true;
        onLayoutModeChanged(PortraitBeautyDisplayModeObserver.getInstance().getActiveDevice(), PortraitBeautyDisplayModeObserver.getInstance().getActiveDispMode(1));
        PortraitBeautyDisplayModeObserver.getInstance().setNotificationListener(this.mLayoutChangeListener);
        this.mDisplayManager = new DisplayManager();
        this.mDisplayManager.setDisplayStatusListener(new DisplayManager.DisplayEventListener() { // from class: com.sony.imaging.app.portraitbeauty.common.EVFOffLayout.1
            public void onDeviceStatusChanged(int eventId) {
                PortraitBeautyUtil.runnableCount++;
            }
        });
    }

    @Override // com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        PortraitBeautyDisplayModeObserver.getInstance().removeNotificationListener(this.mLayoutChangeListener);
        onLayoutModeChanged(0, PortraitBeautyDisplayModeObserver.getInstance().getActiveDispMode(1));
        if (this.mDisplayManager != null) {
            this.mDisplayManager.releaseDisplayStatusListener();
            this.mDisplayManager.finish();
            this.mDisplayManager = null;
        }
        super.onPause();
    }

    public boolean isFinder() {
        return 1 == PortraitBeautyDisplayModeObserver.getInstance().getActiveDevice();
    }

    @Override // com.sony.imaging.app.base.common.IModableLayout
    public void onLayoutModeChanged(int device, int displayMode) {
        ViewGroup vg = (ViewGroup) getView();
        if (device == 1) {
            if (this.isToggled) {
                View v = obtainViewFromPool(R.layout.donot_use_evf);
                vg.addView(v);
                this.mDonotUseEVF = v;
                this.isToggled = false;
                return;
            }
            return;
        }
        if (vg != null) {
            vg.removeView(this.mDonotUseEVF);
        }
        this.mDonotUseEVF = null;
        this.isToggled = true;
    }
}
