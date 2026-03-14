package com.sony.imaging.app.digitalfilter.menu.layout;

import android.os.Bundle;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.digitalfilter.GFBackUpKey;
import com.sony.imaging.app.digitalfilter.R;
import com.sony.imaging.app.digitalfilter.common.AppLog;
import com.sony.imaging.app.digitalfilter.common.GFConstants;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFThemeController;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.util.BackUpUtil;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class GFCopyThemeMenuLayout extends DisplayMenuItemsMenuLayout {
    private static final String TAG = AppLog.getClassName();
    private static TextView mThemeName = null;
    private static ArrayList<CharSequence> mThemeList = null;
    private static int mCurrentPosition = 0;
    private static String mCurrentTheme = null;
    private static int mSkipID = 0;
    private static List<Pair<String, Integer>> mThemeNameList = null;

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = (ViewGroup) obtainViewFromPool(R.layout.menu_import);
        mThemeName = (TextView) view.findViewById(R.id.copy_theme_name);
        return view;
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        mCurrentPosition = BackUpUtil.getInstance().getPreferenceInt(getKey(), 0);
        initList();
        update();
    }

    private void initList() {
        mThemeList = new ArrayList<>();
        mCurrentTheme = GFThemeController.getInstance().getValue();
        mThemeNameList = GFThemeController.getInstance().getThemeNameList();
        for (Pair<String, Integer> nameList : mThemeNameList) {
            CharSequence itemId = getResources().getString(((Integer) nameList.second).intValue());
            mThemeList.add(itemId);
            if (((String) nameList.first).equals(mCurrentTheme)) {
                mSkipID = mThemeNameList.indexOf(nameList);
            }
        }
        if (mCurrentPosition == mSkipID) {
            nextTheme();
        }
        AppLog.info(TAG, "mCurrentTheme : " + mCurrentTheme);
        AppLog.info(TAG, "mThemeList : " + mThemeList);
    }

    private void nextTheme() {
        mCurrentPosition++;
        if (mCurrentPosition == mSkipID) {
            mCurrentPosition++;
        }
        if (mCurrentPosition >= mThemeList.size()) {
            mCurrentPosition = 0;
            if (mCurrentPosition == mSkipID) {
                mCurrentPosition++;
            }
        }
        update();
    }

    private void previousTheme() {
        mCurrentPosition--;
        if (mCurrentPosition == mSkipID) {
            mCurrentPosition--;
        }
        if (mCurrentPosition < 0) {
            mCurrentPosition = mThemeList.size() - 1;
            if (mCurrentPosition == mSkipID) {
                mCurrentPosition--;
            }
        }
        update();
    }

    private void update() {
        AppLog.info(TAG, "mCurrentPosition : " + mCurrentPosition);
        AppLog.info(TAG, "mThemeName : " + ((Object) mThemeList.get(mCurrentPosition)));
        mThemeName.setText(mThemeList.get(mCurrentPosition));
    }

    private static String getKey() {
        String key = "GF_IMPORT_" + GFThemeController.getInstance().getValue();
        return key;
    }

    private void importParameters() {
        String effect = (String) mThemeNameList.get(mCurrentPosition).first;
        GFBackUpKey.getInstance().copyParameters(effect);
        CameraNotificationManager.getInstance().requestNotify(GFConstants.COPY_THEME_SETTINGS);
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout
    public void closeLayout() {
        BackUpUtil.getInstance().setPreference(getKey(), Integer.valueOf(mCurrentPosition));
        super.closeLayout();
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public void onDestroyView() {
        mThemeList = null;
        mThemeName = null;
        mCurrentTheme = null;
        mThemeNameList = null;
        super.onDestroyView();
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout
    protected boolean isAELCancel() {
        return false;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMenuKey() {
        mCurrentPosition = BackUpUtil.getInstance().getPreferenceInt(getKey(), 0);
        openPreviousMenu();
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyDown(int keyCode, KeyEvent event) {
        int code = event.getScanCode();
        switch (code) {
            case 103:
            case AppRoot.USER_KEYCODE.SHUTTLE_RIGHT /* 522 */:
            case AppRoot.USER_KEYCODE.DIAL1_RIGHT /* 525 */:
            case AppRoot.USER_KEYCODE.DIAL2_RIGHT /* 528 */:
            case AppRoot.USER_KEYCODE.DIAL3_RIGHT /* 635 */:
                previousTheme();
                return 1;
            case AppRoot.USER_KEYCODE.LEFT /* 105 */:
            case AppRoot.USER_KEYCODE.RIGHT /* 106 */:
                return -1;
            case AppRoot.USER_KEYCODE.DOWN /* 108 */:
            case AppRoot.USER_KEYCODE.SHUTTLE_LEFT /* 523 */:
            case AppRoot.USER_KEYCODE.DIAL1_LEFT /* 526 */:
            case AppRoot.USER_KEYCODE.DIAL2_LEFT /* 529 */:
            case AppRoot.USER_KEYCODE.DIAL3_LEFT /* 634 */:
                nextTheme();
                return 1;
            case AppRoot.USER_KEYCODE.CENTER /* 232 */:
                importParameters();
                openPreviousMenu();
                return 1;
            default:
                int result = super.onKeyDown(keyCode, event);
                return result;
        }
    }
}
