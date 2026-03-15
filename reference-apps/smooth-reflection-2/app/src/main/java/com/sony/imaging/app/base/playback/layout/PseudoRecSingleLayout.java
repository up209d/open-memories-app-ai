package com.sony.imaging.app.base.playback.layout;

import android.graphics.Rect;
import android.util.SparseIntArray;
import android.view.MotionEvent;
import com.sony.imaging.app.base.R;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.caution.Info;
import com.sony.imaging.app.base.common.DisplayModeObserver;
import com.sony.imaging.app.base.common.EventParcel;
import com.sony.imaging.app.base.playback.contents.ContentsManager;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.smoothreflection.common.SmoothReflectionConstants;
import com.sony.imaging.app.util.BeepUtility;
import com.sony.imaging.app.util.BeepUtilityRsrcTable;
import com.sony.imaging.app.util.Environment;
import com.sony.imaging.app.util.PTag;
import com.sony.scalar.widget.OptimizedImageView;

/* loaded from: classes.dex */
public class PseudoRecSingleLayout extends SingleLayoutBase {
    private static final int DISP_MODE_NUM = 3;
    private static final int[] IMAGE_ERR_LAYOUT_RESOURCE_ID;
    private static final int[] IMAGE_OK_LAYOUT_RESOURCE_ID_HISTGRAM;
    private static final int[] IMAGE_OK_LAYOUT_RESOURCE_ID_INFO_OFF;
    private static final int[] IMAGE_OK_LAYOUT_RESOURCE_ID_INFO_ON;
    private static final String MSG_TRANSITION_ZOOM = "Into playzoom from PseudoRecSingle. Start";
    private static SparseIntArray mLayoutListForFinder;
    private static SparseIntArray mLayoutListForHdmi;
    private static SparseIntArray mLayoutListForPanel = new SparseIntArray(3);

    static {
        mLayoutListForPanel.put(1, R.layout.pb_layout_cmn_singlepb_image_info);
        mLayoutListForPanel.put(5, 1 == Environment.getVersionOfHW() ? R.layout.pb_layout_cmn_singlepb_image_histo_legacy_emnt : R.layout.pb_layout_cmn_singlepb_image_histo);
        mLayoutListForPanel.put(3, R.layout.pb_layout_cmn_singlepb_image_noinfo);
        mLayoutListForFinder = new SparseIntArray(3);
        mLayoutListForFinder.put(1, R.layout.pb_layout_cmn_singlepb_image_info);
        mLayoutListForFinder.put(5, 1 == Environment.getVersionOfHW() ? R.layout.pb_layout_cmn_singlepb_image_histo_legacy_emnt : R.layout.pb_layout_cmn_singlepb_image_histo);
        mLayoutListForFinder.put(3, R.layout.pb_layout_cmn_singlepb_image_noinfo);
        mLayoutListForHdmi = new SparseIntArray(3);
        mLayoutListForHdmi.put(1, R.layout.pb_layout_cmn_singlepb_image_info);
        mLayoutListForHdmi.put(5, 1 == Environment.getVersionOfHW() ? R.layout.pb_layout_cmn_singlepb_image_histo_legacy_emnt : R.layout.pb_layout_cmn_singlepb_image_histo);
        mLayoutListForHdmi.put(3, R.layout.pb_layout_cmn_singlepb_image_noinfo);
        IMAGE_OK_LAYOUT_RESOURCE_ID_INFO_ON = new int[]{R.id.pb_parts_cmn_singlepb_metadata, R.id.pb_parts_cmn_header_contents_info, R.id.pb_parts_cmn_bottom_date};
        IMAGE_OK_LAYOUT_RESOURCE_ID_INFO_OFF = new int[0];
        IMAGE_OK_LAYOUT_RESOURCE_ID_HISTGRAM = new int[]{R.id.pb_parts_cmn_singlepb_histo_metadata, R.id.pb_parts_cmn_singlepb_histo, R.id.pb_parts_cmn_header_contents_info, R.id.pb_parts_cmn_bottom_date};
        IMAGE_ERR_LAYOUT_RESOURCE_ID = new int[]{R.id.pb_parts_cmn_singlepb_image_err};
    }

    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase
    protected int getLayoutResource() {
        int device = DisplayModeObserver.getInstance().getActiveDevice();
        int displayMode = DisplayModeObserver.getInstance().getActiveDispMode(1);
        if (device == 0) {
            int id = mLayoutListForPanel.get(displayMode);
            return id;
        }
        if (device == 1) {
            int id2 = mLayoutListForFinder.get(displayMode);
            return id2;
        }
        if (device != 2) {
            return -1;
        }
        int id3 = mLayoutListForHdmi.get(displayMode);
        return id3;
    }

    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase
    protected int getFooterGuideResource() {
        return R.id.footer_guide;
    }

    @Override // com.sony.imaging.app.base.playback.layout.SingleLayoutBase, com.sony.imaging.app.base.playback.layout.OptimizedImageLayoutBase, com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        CautionUtilityClass.getInstance().setMode(9);
        if (this.data != null) {
            EventParcel userCodeAutoReview = (EventParcel) this.data.getParcelable(EventParcel.KEY_KEYCODE);
            EventParcel touchEventAutoReview = (EventParcel) this.data.getParcelable(EventParcel.KEY_TOUCH);
            if (touchEventAutoReview != null) {
                String action = touchEventAutoReview.mAction;
                if (action.equals(EventParcel.FLICK)) {
                    if (touchEventAutoReview.mVelocityX > SmoothReflectionConstants.INVALID_APERTURE_VALUE) {
                        previousContents();
                    } else if (touchEventAutoReview.mVelocityX < SmoothReflectionConstants.INVALID_APERTURE_VALUE) {
                        nextContents();
                    }
                } else if (action.equals(EventParcel.SCROLL)) {
                    if (touchEventAutoReview.mDistanceX > SmoothReflectionConstants.INVALID_APERTURE_VALUE) {
                        previousContents();
                    } else if (touchEventAutoReview.mDistanceX < SmoothReflectionConstants.INVALID_APERTURE_VALUE) {
                        nextContents();
                    }
                }
                this.data.putParcelable(EventParcel.KEY_TOUCH, null);
            } else if (userCodeAutoReview != null) {
                if (userCodeAutoReview.mKeyFunction != null) {
                    switch (userCodeAutoReview.mKeyFunction) {
                        case MainPrev:
                        case SubPrev:
                            previousContents();
                            break;
                        case MainNext:
                        case SubNext:
                            nextContents();
                            break;
                        case PbZoomMinus:
                            pushedPbZoomFuncMinus();
                            break;
                    }
                } else {
                    switch (userCodeAutoReview.mKeyEvent) {
                        case AppRoot.USER_KEYCODE.LEFT /* 105 */:
                        case AppRoot.USER_KEYCODE.SHUTTLE_LEFT /* 523 */:
                        case AppRoot.USER_KEYCODE.DIAL1_LEFT /* 526 */:
                        case AppRoot.USER_KEYCODE.DIAL2_LEFT /* 529 */:
                            previousContents();
                            break;
                        case AppRoot.USER_KEYCODE.RIGHT /* 106 */:
                        case AppRoot.USER_KEYCODE.SHUTTLE_RIGHT /* 522 */:
                        case AppRoot.USER_KEYCODE.DIAL1_RIGHT /* 525 */:
                        case AppRoot.USER_KEYCODE.DIAL2_RIGHT /* 528 */:
                            nextContents();
                            break;
                    }
                }
                this.data.putParcelable(EventParcel.KEY_KEYCODE, null);
            }
        }
        if (Environment.getVersionOfHW() == 1) {
            setFooterGuideData(null);
        }
    }

    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase
    public int getResumeKeyBeepPattern() {
        return 14;
    }

    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedPbZoomFuncPlus() {
        thinZoomKey();
        return transitionZoom(null) ? 1 : -1;
    }

    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedCenterKey() {
        return Environment.getVersionOfHW() == 1 ? pushedPbZoomFuncPlus() : super.pushedCenterKey();
    }

    @Override // com.sony.imaging.app.base.playback.layout.SingleLayoutBase, com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedPbZoomFuncMinus() {
        displayCaution();
        return -1;
    }

    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedDeleteFuncKey() {
        displayCaution();
        return -1;
    }

    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMenuKey() {
        CautionUtilityClass.getInstance().requestTrigger(Info.CAUTION_ID_DLAPP_INVALID_FUNCTION);
        return -1;
    }

    @Override // com.sony.imaging.app.base.playback.layout.OptimizedImageLayoutBase
    public boolean previousContents() {
        displayCaution();
        return false;
    }

    @Override // com.sony.imaging.app.base.playback.layout.OptimizedImageLayoutBase
    public boolean nextContents() {
        displayCaution();
        return false;
    }

    protected void displayCaution() {
        ContentsManager mgr = ContentsManager.getInstance();
        if (!mgr.isVirtualEmpty()) {
            CautionUtilityClass.getInstance().requestTrigger(585);
        }
    }

    @Override // com.sony.imaging.app.base.playback.widget.TouchArea.OnTouchAreaListener
    public boolean onTouchUp(MotionEvent e, boolean isReleasedInside) {
        PTag.start(MSG_TRANSITION_ZOOM);
        if (isReleasedInside) {
            int displayMode = DisplayModeObserver.getInstance().getActiveDispMode(1);
            if (displayMode == 5) {
                transitionZoom(null);
            } else {
                OptimizedImageView imgView = getOptimizedImageView();
                Rect viewRect = new Rect(0, 0, imgView.getWidth(), imgView.getHeight());
                EventParcel touch = new EventParcel(null, e, null, isReleasedInside, SmoothReflectionConstants.INVALID_APERTURE_VALUE, SmoothReflectionConstants.INVALID_APERTURE_VALUE, SmoothReflectionConstants.INVALID_APERTURE_VALUE, SmoothReflectionConstants.INVALID_APERTURE_VALUE, viewRect);
                transitionZoom(touch);
            }
            BeepUtility.getInstance().playBeep(BeepUtilityRsrcTable.BEEP_ID_OPTION_ON);
        }
        return true;
    }

    @Override // com.sony.imaging.app.base.playback.layout.OptimizedImageLayoutBase
    protected int[] getImageOkLayoutResourceId() {
        int displayMode = DisplayModeObserver.getInstance().getActiveDispMode(1);
        switch (displayMode) {
            case 1:
                return IMAGE_OK_LAYOUT_RESOURCE_ID_INFO_ON;
            case 2:
            case 4:
            default:
                return null;
            case 3:
                return IMAGE_OK_LAYOUT_RESOURCE_ID_INFO_OFF;
            case 5:
                return IMAGE_OK_LAYOUT_RESOURCE_ID_HISTGRAM;
        }
    }

    @Override // com.sony.imaging.app.base.playback.layout.OptimizedImageLayoutBase
    protected int[] getImageErrLayoutResourceId() {
        return IMAGE_ERR_LAYOUT_RESOURCE_ID;
    }
}
