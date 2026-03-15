package com.sony.imaging.app.startrails.playback.layout;

import com.sony.imaging.app.base.common.EventParcel;
import com.sony.imaging.app.base.playback.contents.ContentsManager;
import com.sony.imaging.app.base.playback.layout.PseudoRecZoomLayout;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.startrails.R;
import com.sony.imaging.app.startrails.util.AppLog;
import com.sony.imaging.app.util.ApoWrapper;
import com.sony.scalar.widget.OptimizedImageView;

/* loaded from: classes.dex */
public class STPseudoRecZoomLayout extends PseudoRecZoomLayout {
    private static final String TAG = "STPseudoRecZoomLayout";

    @Override // com.sony.imaging.app.base.playback.layout.OptimizedImageLayoutBase
    protected void updateVPicDisplay(OptimizedImageView img, ContentsManager mgr) {
        img.setDisplayRotationAngle(0);
    }

    @Override // com.sony.imaging.app.base.playback.layout.PseudoRecZoomLayout, com.sony.imaging.app.base.playback.layout.PlayLayoutBase
    protected int getLayoutResource() {
        return R.layout.pb_layout_zoom_play;
    }

    @Override // com.sony.imaging.app.base.playback.layout.PseudoRecZoomLayout, com.sony.imaging.app.base.playback.layout.ZoomLayoutBase, com.sony.imaging.app.base.playback.layout.OptimizedImageLayoutBase, com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        ApoWrapper.setApoType(ApoWrapper.APO_TYPE.NONE);
    }

    @Override // com.sony.imaging.app.base.playback.layout.ZoomLayoutBase, com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedMainDialNext() {
        zoomIn();
        return 1;
    }

    @Override // com.sony.imaging.app.base.playback.layout.ZoomLayoutBase, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedSubDialPrev() {
        zoomOut();
        return 1;
    }

    @Override // com.sony.imaging.app.base.playback.layout.ZoomLayoutBase, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedSubDialNext() {
        zoomIn();
        return 1;
    }

    @Override // com.sony.imaging.app.base.playback.layout.ZoomLayoutBase, com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedMainDialPrev() {
        zoomOut();
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedShuttleToLeft() {
        zoomIn();
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedShuttleToRight() {
        zoomOut();
        return 1;
    }

    @Override // com.sony.imaging.app.base.playback.layout.PseudoRecZoomLayout
    protected void displayCaution() {
        AppLog.info(TAG, "no caution will display");
    }

    @Override // com.sony.imaging.app.base.playback.layout.ZoomLayoutBase, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedPlayBackKey() {
        transitionShooting(new EventParcel(AppRoot.USER_KEYCODE.PLAYBACK));
        return -1;
    }
}
