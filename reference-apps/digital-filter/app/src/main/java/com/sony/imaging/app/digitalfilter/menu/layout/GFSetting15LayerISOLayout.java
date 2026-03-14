package com.sony.imaging.app.digitalfilter.menu.layout;

import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.sony.imaging.app.base.menu.layout.Fn15LayerAbsLayout;
import com.sony.imaging.app.base.shooting.camera.ISOSensitivityController;
import com.sony.imaging.app.digitalfilter.GFBackUpKey;
import com.sony.imaging.app.digitalfilter.R;
import com.sony.imaging.app.digitalfilter.common.AppLog;
import com.sony.imaging.app.digitalfilter.common.GFCommonUtil;
import com.sony.imaging.app.digitalfilter.common.GFConstants;
import com.sony.imaging.app.digitalfilter.common.GFLinkUtil;
import com.sony.imaging.app.digitalfilter.sa.GFHistgramTask;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFFilterSetController;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFLinkAreaController;
import com.sony.imaging.app.digitalfilter.shooting.widget.GFGraphView;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.fw.CustomizableFunction;
import com.sony.imaging.app.fw.IKeyFunction;
import com.sony.imaging.app.util.BackUpUtil;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class GFSetting15LayerISOLayout extends Fn15LayerAbsLayout {
    public static final String MENU_ID = "ID_GFSETTING15LAYERISOLAYOUT";
    private static final String TAG = AppLog.getClassName();
    private static boolean isCanceled = false;
    private static int mSetting = 0;
    private static boolean isLand = false;
    private static boolean isSky = false;
    private static boolean isLayer3 = false;
    private static TextView mIsoText = null;
    private static TextView mIsoValue = null;
    private static ImageView mRightArrow = null;
    private static ImageView mLeftArrow = null;
    private static GFGraphView mGFGraphViewFilter = null;
    private static GFGraphView mGFGraphViewBase = null;
    private static boolean isHistgramView = false;
    private static String mCurrentIso = null;
    private static List<String> mAvailableIso = null;
    private static Handler mHandler = null;
    private static Runnable mRunnable = null;

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View currentView = obtainViewFromPool(R.layout.menu_setting15_iso);
        mIsoText = (TextView) currentView.findViewById(R.id.iso_text);
        mIsoValue = (TextView) currentView.findViewById(R.id.iso_value);
        mLeftArrow = (ImageView) currentView.findViewById(R.id.left_arrow);
        mRightArrow = (ImageView) currentView.findViewById(R.id.right_arrow);
        mGFGraphViewFilter = (GFGraphView) currentView.findViewById(R.id.filter_histgram);
        mGFGraphViewBase = (GFGraphView) currentView.findViewById(R.id.base_histgram);
        isHistgramView = BackUpUtil.getInstance().getPreferenceBoolean(GFBackUpKey.KEY_HISTGRAM_VIEW, true);
        if (!isHistgramView) {
            mGFGraphViewFilter.setVisibility(4);
            mGFGraphViewBase.setVisibility(4);
        } else {
            mGFGraphViewFilter.setVisibility(0);
            mGFGraphViewBase.setVisibility(0);
        }
        GFHistgramTask.getInstance().startHistgramUpdating();
        GFHistgramTask.getInstance().setEnableUpdating(isHistgramView);
        mCurrentIso = ISOSensitivityController.getInstance().getValue();
        createTable();
        return currentView;
    }

    private void createTable() {
        if (mAvailableIso == null) {
            mAvailableIso = ISOSensitivityController.getInstance().getAvailableValue(ISOSensitivityController.MENU_ITEM_ID_ISO);
            List<String> availableIso = ISOSensitivityController.getInstance().getAvailableValue(ISOSensitivityController.MENU_ITEM_ID_ISO);
            for (String iso : availableIso) {
                if (iso.contains("Exp")) {
                    mAvailableIso.remove(iso);
                }
            }
        }
    }

    @Override // com.sony.imaging.app.base.menu.layout.Fn15LayerAbsLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        isCanceled = false;
        mSetting = GFCommonUtil.getInstance().getSettingLayer();
        isLand = GFCommonUtil.getInstance().isLand();
        isSky = GFCommonUtil.getInstance().isSky();
        isLayer3 = GFCommonUtil.getInstance().isLayer3();
        updateISO();
        super.onResume();
        if (!BackUpUtil.getInstance().getPreferenceBoolean(GFBackUpKey.KEY_SHOW_ISO_LINK_MSG, false)) {
            showLinkMsg();
        }
    }

    @Override // com.sony.imaging.app.base.menu.layout.Fn15LayerAbsLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        mGFGraphViewFilter = null;
        mGFGraphViewBase = null;
        BackUpUtil.getInstance().setPreference(GFBackUpKey.KEY_HISTGRAM_VIEW, Boolean.valueOf(isHistgramView));
        GFHistgramTask.getInstance().stopHistgramUpdating();
        mCurrentIso = null;
        mAvailableIso = null;
        mGFGraphViewBase = null;
        if (mHandler != null) {
            mHandler.removeCallbacks(mRunnable);
            mHandler = null;
        }
        mRunnable = null;
        super.onPause();
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout
    public void closeLayout() {
        if (isCanceled) {
            GFCommonUtil.getInstance().setIso(mSetting);
        } else {
            GFLinkUtil.getInstance().saveLinkedISO(mSetting, isLand, isSky, isLayer3);
        }
        super.closeLayout();
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public void onDestroyView() {
        mIsoText = null;
        mIsoValue = null;
        mRightArrow = null;
        mLeftArrow = null;
        super.onDestroyView();
        System.gc();
    }

    private void showLinkMsg() {
        boolean isValidLink = false;
        if (GFLinkAreaController.getInstance().isISOLink(mSetting)) {
            if (isLand) {
                if (GFLinkAreaController.getInstance().isISOLink(1) || (GFFilterSetController.getInstance().need3rdShooting() && GFLinkAreaController.getInstance().isISOLink(2))) {
                    isValidLink = true;
                }
            } else if (isSky) {
                if (GFLinkAreaController.getInstance().isISOLink(0) || (GFFilterSetController.getInstance().need3rdShooting() && GFLinkAreaController.getInstance().isISOLink(2))) {
                    isValidLink = true;
                }
            } else if (GFLinkAreaController.getInstance().isISOLink(0) || GFLinkAreaController.getInstance().isISOLink(1)) {
                isValidLink = true;
            }
        }
        if (isValidLink) {
            Bundle bundle = new Bundle();
            if (GFCommonUtil.getInstance().isRX100()) {
                bundle.putString(GFGuideLayout.ID_GFGUIDELAYOUT, "LINK_MSG_ISO125");
            } else {
                bundle.putString(GFGuideLayout.ID_GFGUIDELAYOUT, "LINK_MSG_ISO100");
            }
            openLayout(GFGuideLayout.ID_GFGUIDELAYOUT, bundle);
        }
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout
    protected boolean isAELCancel() {
        return false;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout
    protected void setFunctionGuideResources(ArrayList<Object> guideResources) {
        String value = ISOSensitivityController.getInstance().getValue(ISOSensitivityController.MENU_ITEM_ID_ISO);
        String itemID = "Iso_" + value;
        guideResources.add(this.mService.getMenuItemText(itemID));
        guideResources.add(this.mService.getMenuItemGuideText(itemID));
        guideResources.add(true);
    }

    private void incrementISO() {
        int nowIndex = mAvailableIso.indexOf(mCurrentIso);
        int newIndex = nowIndex + 1;
        if (newIndex > mAvailableIso.size() - 1) {
            newIndex = 0;
        }
        updateISO(newIndex);
    }

    private void decrementISO() {
        int nowIndex = mAvailableIso.indexOf(mCurrentIso);
        int newIndex = nowIndex - 1;
        if (newIndex < 0) {
            newIndex = mAvailableIso.size() - 1;
        }
        updateISO(newIndex);
    }

    private void updateISO(int index) {
        mCurrentIso = mAvailableIso.get(index);
        ISOSensitivityController.getInstance().setValue(ISOSensitivityController.MENU_ITEM_ID_ISO, mCurrentIso);
        if (mCurrentIso.equals(ISOSensitivityController.ISO_AUTO)) {
            mIsoValue.setText(17042019);
        } else {
            mIsoValue.setText(mCurrentIso);
        }
        mIsoText.setVisibility(0);
        mIsoValue.setVisibility(0);
        mLeftArrow.setVisibility(0);
        mRightArrow.setVisibility(0);
        disappearInfo();
    }

    private void updateISO() {
        if (mCurrentIso.equals(ISOSensitivityController.ISO_AUTO)) {
            mIsoValue.setText(17042019);
        } else {
            mIsoValue.setText(mCurrentIso);
        }
        mIsoText.setVisibility(0);
        mIsoValue.setVisibility(0);
        mLeftArrow.setVisibility(0);
        mRightArrow.setVisibility(0);
        disappearInfo();
    }

    private void disappearInfo() {
        if (mHandler == null) {
            mHandler = new Handler();
        } else {
            mHandler.removeCallbacks(mRunnable);
        }
        if (mRunnable == null) {
            mRunnable = new Runnable() { // from class: com.sony.imaging.app.digitalfilter.menu.layout.GFSetting15LayerISOLayout.1
                @Override // java.lang.Runnable
                public void run() {
                    GFSetting15LayerISOLayout.this.setInfoVisibility(4);
                }
            };
        }
        mHandler.postDelayed(mRunnable, 1000L);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setInfoVisibility(int visibility) {
        mIsoText.setVisibility(visibility);
        mIsoValue.setVisibility(visibility);
        mLeftArrow.setVisibility(visibility);
        mRightArrow.setVisibility(visibility);
    }

    private void toggleDispMode() {
        if (!isHistgramView) {
            isHistgramView = true;
            mGFGraphViewFilter.setVisibility(0);
            mGFGraphViewBase.setVisibility(0);
        } else {
            isHistgramView = false;
            mGFGraphViewFilter.setVisibility(4);
            mGFGraphViewBase.setVisibility(4);
        }
        GFHistgramTask.getInstance().setEnableUpdating(isHistgramView);
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedMainDialNext() {
        incrementISO();
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedMainDialPrev() {
        decrementISO();
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedSubDialNext() {
        incrementISO();
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedSubDialPrev() {
        decrementISO();
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedThirdDialNext() {
        incrementISO();
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedThirdDialPrev() {
        decrementISO();
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedSK1Key() {
        return pushedMenuKey();
    }

    @Override // com.sony.imaging.app.base.menu.layout.Fn15LayerAbsLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMenuKey() {
        isCanceled = true;
        return super.pushedMenuKey();
    }

    @Override // com.sony.imaging.app.base.menu.layout.Fn15LayerAbsLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedFnKey() {
        return 0;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMovieRecKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int movedAelFocusSlideKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedAFMFKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedEVDial() {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IConvertibleKeyHandler
    public int onConvertedKeyDown(KeyEvent event, IKeyFunction func) {
        int code = event.getScanCode();
        if (GFConstants.Setting15LayerCustomizableFunction.contains(func)) {
            int ret = super.onConvertedKeyDown(event, func);
            return ret;
        }
        if (GFCommonUtil.getInstance().isTriggerMfAssist(code)) {
            return 0;
        }
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IConvertibleKeyHandler
    public int onConvertedKeyUp(KeyEvent event, IKeyFunction func) {
        if (CustomizableFunction.Unchanged.equals(func)) {
            return super.onConvertedKeyUp(event, func);
        }
        return -1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyDown(int keyCode, KeyEvent event) {
        int code = event.getScanCode();
        switch (code) {
            case 103:
            case AppRoot.USER_KEYCODE.DOWN /* 108 */:
            case AppRoot.USER_KEYCODE.DELETE /* 595 */:
                if (isFunctionGuideShown()) {
                    return 1;
                }
                closeMenuLayout(new Bundle());
                return 0;
            default:
                int ret = super.onKeyDown(keyCode, event);
                return ret;
        }
    }
}
