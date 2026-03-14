package com.sony.imaging.app.digitalfilter.shooting.layout;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.sony.imaging.app.base.common.widget.FooterGuide;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.layout.ShootingLayout;
import com.sony.imaging.app.digitalfilter.R;
import com.sony.imaging.app.digitalfilter.common.AppLog;
import com.sony.imaging.app.digitalfilter.common.GFCommonUtil;
import com.sony.imaging.app.digitalfilter.common.GFConstants;
import com.sony.imaging.app.digitalfilter.common.GFImageAdjustmentUtil;
import com.sony.imaging.app.digitalfilter.common.GFKikiLogUtil;
import com.sony.imaging.app.digitalfilter.shooting.widget.UpdatingImage;
import com.sony.imaging.app.digitalfilter.shooting.widget.UpdatingText;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.util.ApoWrapper;
import com.sony.imaging.app.util.NotificationListener;

/* loaded from: classes.dex */
public class GFPreviewLayout extends ShootingLayout implements NotificationListener {
    private static final int EDIT = 1;
    private static final int SAVE = 0;
    private final String TAG = AppLog.getClassName();
    private String[] TAGS = {GFConstants.CHECKE_UPDATE_STATUS};
    private static View mCurrentView = null;
    private static int mCurrentFocus = 0;
    private static TextView mSaveButton = null;
    private static TextView mEditButton = null;
    private static UpdatingImage mUpdatingImage = null;
    private static UpdatingText mUpdatingText = null;
    private static FooterGuide mFooterGuide = null;

    @Override // com.sony.imaging.app.base.shooting.layout.ShootingLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mCurrentView = (ViewGroup) obtainViewFromPool(R.layout.previewlayout);
        mSaveButton = (TextView) mCurrentView.findViewById(R.id.saveBtn);
        mEditButton = (TextView) mCurrentView.findViewById(R.id.editBtn);
        mCurrentFocus = GFCommonUtil.getInstance().getPreivewButtonPostion();
        GFCommonUtil.getInstance().setPreivewButtonPostion(0);
        mUpdatingImage = (UpdatingImage) mCurrentView.findViewById(R.id.updating);
        mUpdatingText = (UpdatingText) mCurrentView.findViewById(R.id.updating_text);
        mFooterGuide = (FooterGuide) mCurrentView.findViewById(R.id.footer_guide);
        if (GFImageAdjustmentUtil.getInstance().isConformedToUISetting()) {
            mUpdatingImage.setVisibility(4);
            mUpdatingText.setVisibility(4);
            mFooterGuide.setVisibility(0);
        } else {
            mUpdatingImage.setVisibility(0);
            mUpdatingText.setVisibility(0);
            mFooterGuide.setVisibility(4);
        }
        return mCurrentView;
    }

    @Override // com.sony.imaging.app.base.shooting.layout.ShootingLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        CameraNotificationManager.getInstance().setNotificationListener(this);
        CameraNotificationManager.getInstance().requestNotify(GFConstants.CHECKE_UPDATE_STATUS);
        ApoWrapper.setApoType(ApoWrapper.APO_TYPE.NORMAL);
        updateFocus();
        super.onResume();
    }

    @Override // com.sony.imaging.app.base.shooting.layout.ShootingLayout, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        CameraNotificationManager.getInstance().removeNotificationListener(this);
        mFooterGuide = null;
        mUpdatingImage = null;
        mUpdatingText = null;
        mCurrentView = null;
        ApoWrapper.setApoType(ApoWrapper.APO_TYPE.NONE);
        super.onPause();
    }

    private void updateFocus() {
        if (mCurrentFocus == 0) {
            mSaveButton.setBackgroundResource(17306030);
            mEditButton.setBackgroundResource(17306045);
        } else {
            mSaveButton.setBackgroundResource(17306045);
            mEditButton.setBackgroundResource(17306030);
        }
    }

    private void toggleButton() {
        if (mCurrentFocus == 0) {
            mCurrentFocus = 1;
        } else {
            mCurrentFocus = 0;
        }
        updateFocus();
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int attachedLens() {
        return 0;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int detachedLens() {
        return 0;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyDown(int keyCode, KeyEvent event) {
        int code = event.getScanCode();
        switch (code) {
            case 103:
            case AppRoot.USER_KEYCODE.DOWN /* 108 */:
            case AppRoot.USER_KEYCODE.SHUTTLE_RIGHT /* 522 */:
            case AppRoot.USER_KEYCODE.SHUTTLE_LEFT /* 523 */:
            case AppRoot.USER_KEYCODE.DIAL1_RIGHT /* 525 */:
            case AppRoot.USER_KEYCODE.DIAL1_LEFT /* 526 */:
            case AppRoot.USER_KEYCODE.DIAL2_RIGHT /* 528 */:
            case AppRoot.USER_KEYCODE.DIAL2_LEFT /* 529 */:
            case AppRoot.USER_KEYCODE.DIAL3_LEFT /* 634 */:
            case AppRoot.USER_KEYCODE.DIAL3_RIGHT /* 635 */:
                toggleButton();
                return 1;
            case AppRoot.USER_KEYCODE.CENTER /* 232 */:
                if (mCurrentFocus == 0) {
                    int retVal = super.onKeyDown(keyCode, event);
                    return retVal;
                }
                GFCommonUtil.getInstance().setPreivewButtonPostion(1);
                closeLayout();
                openLayout("AdjustmentLayout");
                GFKikiLogUtil.getInstance().countAdjustmentTransition();
                return 1;
            case AppRoot.USER_KEYCODE.LENS_FOCUS_HOLD /* 653 */:
                return -1;
            default:
                int retVal2 = super.onKeyDown(keyCode, event);
                return retVal2;
        }
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyUp(int keyCode, KeyEvent event) {
        int code = event.getScanCode();
        if (code == 653) {
            return -1;
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override // com.sony.imaging.app.util.NotificationListener
    public String[] getTags() {
        return this.TAGS;
    }

    @Override // com.sony.imaging.app.util.NotificationListener
    public void onNotify(String tag) {
        if (GFImageAdjustmentUtil.getInstance().isConformedToUISetting()) {
            mFooterGuide.setVisibility(0);
        } else {
            mFooterGuide.setVisibility(4);
        }
    }
}
