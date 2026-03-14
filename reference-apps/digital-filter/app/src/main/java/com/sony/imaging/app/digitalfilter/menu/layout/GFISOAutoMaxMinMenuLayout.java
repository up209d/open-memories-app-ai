package com.sony.imaging.app.digitalfilter.menu.layout;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout;
import com.sony.imaging.app.base.shooting.camera.ISOSensitivityController;
import com.sony.imaging.app.digitalfilter.R;
import com.sony.imaging.app.digitalfilter.common.AppLog;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.util.BackUpUtil;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class GFISOAutoMaxMinMenuLayout extends DisplayMenuItemsMenuLayout {
    private static final int MAX_POS = 0;
    private static final int MIN_POS = 1;
    private static final String TAG = AppLog.getClassName();
    private static int mCurrentPosition = 0;
    private static TextView mItemName = null;
    private static TextView mMaxValueText = null;
    private static TextView mMinValueText = null;
    private static ImageView mMaxValueButton = null;
    private static ImageView mMinValueButton = null;
    private static ImageView mUpArrow0 = null;
    private static ImageView mDownArrow0 = null;
    private static ImageView mUpArrow1 = null;
    private static ImageView mDownArrow1 = null;
    private static List<String> mList = null;
    private static String mMaxValue = null;
    private static String mMinValue = null;
    private static String mOrigMaxValue = null;
    private static String mOrigMinValue = null;

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = (ViewGroup) obtainViewFromPool(R.layout.iso_maxmin);
        mItemName = (TextView) view.findViewById(R.id.item_name);
        mMaxValueText = (TextView) view.findViewById(R.id.right_text);
        mMinValueText = (TextView) view.findViewById(R.id.left_text);
        mMaxValueButton = (ImageView) view.findViewById(R.id.right_button);
        mMinValueButton = (ImageView) view.findViewById(R.id.left_button);
        mUpArrow0 = (ImageView) view.findViewById(R.id.right_up_arrow);
        mDownArrow0 = (ImageView) view.findViewById(R.id.right_down_arrow);
        mUpArrow1 = (ImageView) view.findViewById(R.id.left_up_arrow);
        mDownArrow1 = (ImageView) view.findViewById(R.id.left_down_arrow);
        mCurrentPosition = BackUpUtil.getInstance().getPreferenceInt(getKey(), 1);
        mList = ISOSensitivityController.getInstance().getSupportedValue(ISOSensitivityController.MENU_ITEM_ID_ISO_AUTO_MAX);
        return view;
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        mOrigMaxValue = ISOSensitivityController.getInstance().getValue(ISOSensitivityController.MENU_ITEM_ID_ISO_AUTO_MAX);
        mOrigMinValue = ISOSensitivityController.getInstance().getValue(ISOSensitivityController.MENU_ITEM_ID_ISO_AUTO_MIN);
        updateItems();
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public void onDestroyView() {
        mItemName = null;
        mMaxValueText = null;
        mMinValueText = null;
        mMaxValueButton = null;
        mMinValueButton = null;
        mUpArrow0 = null;
        mDownArrow0 = null;
        mUpArrow1 = null;
        mDownArrow1 = null;
        mList = null;
        mMaxValue = null;
        mMinValue = null;
        mOrigMaxValue = null;
        mOrigMinValue = null;
        super.onDestroyView();
        System.gc();
    }

    private void updateItems() {
        if (mCurrentPosition == 0) {
            mItemName.setText(R.string.STRID_AMC_STR_06020);
            mMaxValueButton.setImageResource(17306035);
            mMinValueButton.setImageResource(R.drawable.p_16_dd_parts_skyhdr_transparent_base);
        } else {
            mItemName.setText(R.string.STRID_AMC_STR_06021);
            mMaxValueButton.setImageResource(R.drawable.p_16_dd_parts_skyhdr_transparent_base);
            mMinValueButton.setImageResource(17306035);
        }
        updateValues();
    }

    private void updateValues() {
        mMaxValue = ISOSensitivityController.getInstance().getValue(ISOSensitivityController.MENU_ITEM_ID_ISO_AUTO_MAX);
        mMinValue = ISOSensitivityController.getInstance().getValue(ISOSensitivityController.MENU_ITEM_ID_ISO_AUTO_MIN);
        mMaxValueText.setText("ISO " + mMaxValue);
        mMinValueText.setText("ISO " + mMinValue);
        if (mCurrentPosition == 0) {
            if (mList.indexOf(mMaxValue) == 0) {
                mUpArrow0.setVisibility(4);
            } else {
                mUpArrow0.setVisibility(0);
            }
            if (mList.indexOf(mMaxValue) == mList.size() - 1) {
                mDownArrow0.setVisibility(4);
            } else {
                mDownArrow0.setVisibility(0);
            }
            mUpArrow1.setVisibility(4);
            mDownArrow1.setVisibility(4);
            return;
        }
        mUpArrow0.setVisibility(4);
        mDownArrow0.setVisibility(4);
        if (mList.indexOf(mMinValue) == 0) {
            mUpArrow1.setVisibility(4);
        } else {
            mUpArrow1.setVisibility(0);
        }
        if (mList.indexOf(mMinValue) == mList.size() - 1) {
            mDownArrow1.setVisibility(4);
        } else {
            mDownArrow1.setVisibility(0);
        }
    }

    private void incrementValue() {
        if (mCurrentPosition == 0) {
            int index = mList.indexOf(mMaxValue);
            if (index < mList.size() - 1) {
                mMaxValue = mList.get(index + 1);
                ISOSensitivityController.getInstance().setValue(ISOSensitivityController.MENU_ITEM_ID_ISO_AUTO_MAX, mMaxValue);
            }
        } else {
            int index2 = mList.indexOf(mMinValue);
            if (index2 < mList.size() - 1) {
                mMinValue = mList.get(index2 + 1);
                ISOSensitivityController.getInstance().setValue(ISOSensitivityController.MENU_ITEM_ID_ISO_AUTO_MIN, mMinValue);
            }
        }
        updateValues();
    }

    private void decrementValue() {
        if (mCurrentPosition == 0) {
            int index = mList.indexOf(mMaxValue);
            if (index > 0) {
                mMaxValue = mList.get(index - 1);
                ISOSensitivityController.getInstance().setValue(ISOSensitivityController.MENU_ITEM_ID_ISO_AUTO_MAX, mMaxValue);
            }
        } else {
            int index2 = mList.indexOf(mMinValue);
            if (index2 > 0) {
                mMinValue = mList.get(index2 - 1);
                ISOSensitivityController.getInstance().setValue(ISOSensitivityController.MENU_ITEM_ID_ISO_AUTO_MIN, mMinValue);
            }
        }
        updateValues();
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout
    public void closeLayout() {
        BackUpUtil.getInstance().setPreference(getKey(), Integer.valueOf(mCurrentPosition));
        super.closeLayout();
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout
    protected void setFunctionGuideResources(ArrayList<Object> guideResources) {
        if (mCurrentPosition == 0) {
            setGuideResource(guideResources, R.string.STRID_AMC_STR_06020, R.string.STRID_AMC_STR_06286);
        } else {
            setGuideResource(guideResources, R.string.STRID_AMC_STR_06021, R.string.STRID_AMC_STR_06287);
        }
    }

    private void setGuideResource(ArrayList<Object> guideResources, int guideTitleID, int guideDefi) {
        guideResources.add(getText(guideTitleID));
        guideResources.add(getText(guideDefi));
        guideResources.add(true);
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout
    protected boolean isAELCancel() {
        return false;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMenuKey() {
        ISOSensitivityController.getInstance().setValue(ISOSensitivityController.MENU_ITEM_ID_ISO_AUTO_MAX, mOrigMaxValue);
        ISOSensitivityController.getInstance().setValue(ISOSensitivityController.MENU_ITEM_ID_ISO_AUTO_MIN, mOrigMinValue);
        openPreviousMenu();
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyDown(int keyCode, KeyEvent event) {
        int code = event.getScanCode();
        switch (code) {
            case 103:
            case AppRoot.USER_KEYCODE.SHUTTLE_LEFT /* 523 */:
            case AppRoot.USER_KEYCODE.DIAL1_LEFT /* 526 */:
            case AppRoot.USER_KEYCODE.DIAL2_LEFT /* 529 */:
            case AppRoot.USER_KEYCODE.DIAL3_LEFT /* 634 */:
                if (!isFunctionGuideShown()) {
                    decrementValue();
                }
                return 1;
            case AppRoot.USER_KEYCODE.LEFT /* 105 */:
            case AppRoot.USER_KEYCODE.RIGHT /* 106 */:
                if (!isFunctionGuideShown()) {
                    mCurrentPosition++;
                    mCurrentPosition &= 1;
                    updateItems();
                }
                return 1;
            case AppRoot.USER_KEYCODE.DOWN /* 108 */:
            case AppRoot.USER_KEYCODE.SHUTTLE_RIGHT /* 522 */:
            case AppRoot.USER_KEYCODE.DIAL1_RIGHT /* 525 */:
            case AppRoot.USER_KEYCODE.DIAL2_RIGHT /* 528 */:
            case AppRoot.USER_KEYCODE.DIAL3_RIGHT /* 635 */:
                if (!isFunctionGuideShown()) {
                    incrementValue();
                }
                return 1;
            case AppRoot.USER_KEYCODE.CENTER /* 232 */:
                if (!isFunctionGuideShown()) {
                    openPreviousMenu();
                    return 1;
                }
                int result = super.onKeyDown(keyCode, event);
                return result;
            default:
                int result2 = super.onKeyDown(keyCode, event);
                return result2;
        }
    }

    private static String getKey() {
        return "GF_ISOAUTO_MAXMIN";
    }
}
