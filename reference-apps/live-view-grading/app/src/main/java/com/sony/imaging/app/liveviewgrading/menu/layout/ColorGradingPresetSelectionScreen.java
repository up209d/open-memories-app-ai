package com.sony.imaging.app.liveviewgrading.menu.layout;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.sony.imaging.app.base.common.widget.FooterGuide;
import com.sony.imaging.app.base.common.widget.FooterGuideDataResId;
import com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout;
import com.sony.imaging.app.base.menu.layout.SpecialScreenView;
import com.sony.imaging.app.base.shooting.camera.MovieFormatController;
import com.sony.imaging.app.liveviewgrading.AppLog;
import com.sony.imaging.app.liveviewgrading.ColorGradingConstants;
import com.sony.imaging.app.liveviewgrading.R;
import com.sony.imaging.app.liveviewgrading.menu.controller.ColorGradingController;
import com.sony.imaging.app.liveviewgrading.menu.controller.LVGParameterValueController;
import com.sony.imaging.app.liveviewgrading.shooting.LVGEffectValueController;
import com.sony.imaging.app.util.BackUpUtil;

/* loaded from: classes.dex */
public class ColorGradingPresetSelectionScreen extends SpecialScreenMenuLayout {
    private static final int ITEM_COUNT_5 = 5;
    private String TAG = AppLog.getClassName();
    private ColorGradingController mController = null;
    private int mSelectedPosition = 0;
    private int mPreviousSelectedPos = 0;
    private FooterGuide mFooterGuide = null;
    private TextView mScreenTitleTxtVw = null;

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View currentView = super.onCreateView(inflater, container, savedInstanceState);
        this.mFooterGuide = (FooterGuide) currentView.findViewById(R.id.footer_guide);
        this.mFooterGuide.setData(new FooterGuideDataResId(getActivity(), R.string.STRID_FUNC_LVG_PRESET_SELECTION_LAYOUT_FOOTER, R.string.STRID_FUNC_LVG_PRESET_SELECTION_LAYOUT_FOOTER));
        this.mScreenTitleView.setVisibility(4);
        this.mScreenTitleTxtVw = (TextView) currentView.findViewById(R.id.screen_title);
        this.mScreenTitleTxtVw.setText(R.string.STRID_FUNC_LVG_MENU_ITEM_PRESET_SELETION);
        return currentView;
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout
    protected int getLayoutID() {
        return R.layout.colorgrading_menu_preset_selection;
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        this.mController = ColorGradingController.getInstance();
        String selectedGroup = this.mController.getLastSelectedGroup();
        String currPreset = getPreviousSelectedPreset(selectedGroup);
        Log.e("", "currPreset = " + currPreset);
        this.mSelectedPosition = this.mService.getMenuItemList().indexOf(currPreset);
        this.mPreviousSelectedPos = this.mSelectedPosition;
        this.mAdapter.getItem(this.mSelectedPosition);
        String preset = LVGParameterValueController.getInstance().getPresetValues();
        Log.e("", "BKUP preset = " + preset);
        applyEffect(preset);
        handleSelection(this.mSelectedPosition);
    }

    private String getPreviousSelectedPreset(String selectedGroup) {
        if (selectedGroup.equals(ColorGradingController.STANDARD)) {
            String retPresetVal = this.mController.getGroup1SelectedPreset();
            return retPresetVal;
        }
        if (selectedGroup.equals(ColorGradingController.CINEMA)) {
            String retPresetVal2 = this.mController.getGroup2SelectedPreset();
            return retPresetVal2;
        }
        if (!selectedGroup.equals(ColorGradingController.EXTREME)) {
            return null;
        }
        String retPresetVal3 = this.mController.getGroup3SelectedPreset();
        return retPresetVal3;
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.SpecialScreenView.OnItemSelectedListener
    public void onItemSelected(SpecialScreenView adapterView, View view, int position, long id) {
        super.onItemSelected(adapterView, view, position, id);
        setLastPreset(position);
        this.mSelectedPosition = position;
        String preset = LVGParameterValueController.getInstance().getPresetValues();
        Log.e("", "BKUP preset = " + preset);
        applyEffect(preset);
        resetCursorPosition();
    }

    private void resetCursorPosition() {
        String selectedGroup = this.mController.getLastSelectedGroup();
        if (selectedGroup.equalsIgnoreCase(ColorGradingController.STANDARD)) {
            BackUpUtil.getInstance().setPreference(ColorGradingConstants.G1_LAST_PARAMETER, 1);
        } else if (selectedGroup.equalsIgnoreCase(ColorGradingController.CINEMA)) {
            BackUpUtil.getInstance().setPreference(ColorGradingConstants.G2_LAST_PARAMETER, 1);
        } else if (selectedGroup.equalsIgnoreCase(ColorGradingController.EXTREME)) {
            BackUpUtil.getInstance().setPreference(ColorGradingConstants.G3_LAST_PARAMETER, 1);
        }
    }

    private void setLastPreset(int pos) {
        String selectedGroup = this.mController.getLastSelectedGroup();
        int kikilogSubId = -1;
        if (selectedGroup.equalsIgnoreCase(ColorGradingController.STANDARD)) {
            switch (pos) {
                case 0:
                    BackUpUtil.getInstance().setPreference(ColorGradingConstants.G1_LAST_SELECTED_PRESET, ColorGradingController.CLEAR);
                    kikilogSubId = 4212;
                    break;
                case 1:
                    BackUpUtil.getInstance().setPreference(ColorGradingConstants.G1_LAST_SELECTED_PRESET, ColorGradingController.VIVID);
                    kikilogSubId = 4213;
                    break;
                case 2:
                    BackUpUtil.getInstance().setPreference(ColorGradingConstants.G1_LAST_SELECTED_PRESET, ColorGradingController.MONOCHROME);
                    kikilogSubId = 4214;
                    break;
                case 3:
                    BackUpUtil.getInstance().setPreference(ColorGradingConstants.G1_LAST_SELECTED_PRESET, ColorGradingController.BOLD);
                    kikilogSubId = 4215;
                    break;
            }
        } else if (selectedGroup.equalsIgnoreCase(ColorGradingController.CINEMA)) {
            switch (pos) {
                case 0:
                    BackUpUtil.getInstance().setPreference(ColorGradingConstants.G2_LAST_SELECTED_PRESET, ColorGradingController.COAST_SIDE_LIGHT);
                    kikilogSubId = 4216;
                    break;
                case 1:
                    BackUpUtil.getInstance().setPreference(ColorGradingConstants.G2_LAST_SELECTED_PRESET, ColorGradingController.SILKY);
                    kikilogSubId = 4217;
                    break;
                case 2:
                    BackUpUtil.getInstance().setPreference(ColorGradingConstants.G2_LAST_SELECTED_PRESET, ColorGradingController.MISTY_BLUE);
                    kikilogSubId = 4218;
                    break;
                case 3:
                    BackUpUtil.getInstance().setPreference(ColorGradingConstants.G2_LAST_SELECTED_PRESET, ColorGradingController.VELVETY_DEW);
                    kikilogSubId = 4219;
                    break;
            }
        } else if (selectedGroup.equalsIgnoreCase(ColorGradingController.EXTREME)) {
            switch (pos) {
                case 0:
                    BackUpUtil.getInstance().setPreference(ColorGradingConstants.G3_LAST_SELECTED_PRESET, ColorGradingController._180);
                    kikilogSubId = 4220;
                    break;
                case 1:
                    BackUpUtil.getInstance().setPreference(ColorGradingConstants.G3_LAST_SELECTED_PRESET, ColorGradingController.SURF_TRIP);
                    kikilogSubId = 4221;
                    break;
                case 2:
                    BackUpUtil.getInstance().setPreference(ColorGradingConstants.G3_LAST_SELECTED_PRESET, ColorGradingController.BIG_AIR);
                    kikilogSubId = 4222;
                    break;
                case 3:
                    BackUpUtil.getInstance().setPreference(ColorGradingConstants.G3_LAST_SELECTED_PRESET, ColorGradingController.SNOW_TRICKS);
                    kikilogSubId = 4223;
                    break;
            }
        }
        this.mController.startKikiLog(kikilogSubId);
    }

    private void applyEffect(String values) {
        int[] params = new int[8];
        String[] dev = values.split(MovieFormatController.Settings.SEMI_COLON, -1);
        for (String str : dev) {
            String[] element = str.split(MovieFormatController.Settings.EQUAL);
            if ("Contrast".equals(element[0])) {
                params[0] = Integer.valueOf(element[1]).intValue();
            } else if ("Saturation".equals(element[0])) {
                params[1] = Integer.valueOf(element[1]).intValue();
            } else if ("Sharpness".equals(element[0])) {
                params[2] = Integer.valueOf(element[1]).intValue();
            } else if (ColorGradingConstants.COLOR_TEMP.equals(element[0])) {
                params[3] = Integer.valueOf(element[1]).intValue();
            } else if (ColorGradingConstants.COLOR_TINT.equals(element[0])) {
                params[4] = Integer.valueOf(element[1]).intValue();
            } else if (ColorGradingConstants.COLOR_DEPTH_R.equals(element[0])) {
                params[5] = Integer.valueOf(element[1]).intValue();
            } else if (ColorGradingConstants.COLOR_DEPTH_G.equals(element[0])) {
                params[6] = Integer.valueOf(element[1]).intValue();
            } else if (ColorGradingConstants.COLOR_DEPTH_B.equals(element[0])) {
                params[7] = Integer.valueOf(element[1]).intValue();
            }
        }
        LVGEffectValueController.getInstance().setParamValues(params);
        LVGEffectValueController.getInstance().applyGammaTable();
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.FakeFragment
    public void onDestroy() {
        this.mFooterGuide = null;
        super.onDestroy();
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachKeyHandler
    public int onDeleteKeyPushed(KeyEvent event) {
        return super.pushedGuideFuncKey();
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachKeyHandler
    public int onDeleteKeyReleased(KeyEvent event) {
        return -1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedRightKey() {
        openNextMenu((String) this.mSpecialScreenView.getSelectedItem(), "ID_COLORGRADINGPRESETADJUSTMENTSCREEN", true, this.data);
        return 1;
    }

    private void handleSelection(int position) {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        int mBlock = getMenuItemList().size();
        if (mBlock > 5) {
            mBlock = 5;
        }
        int firstPosition = (position - mBlock) + 1;
        if (firstPosition < 0) {
            firstPosition = 0;
        }
        this.mSpecialScreenView.setSelection(firstPosition, position);
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMenuKey() {
        this.mSelectedPosition = this.mPreviousSelectedPos;
        return super.pushedMenuKey();
    }
}
