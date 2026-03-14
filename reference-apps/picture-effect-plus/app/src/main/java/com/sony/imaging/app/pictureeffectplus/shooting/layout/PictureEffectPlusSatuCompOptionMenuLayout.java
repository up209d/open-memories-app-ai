package com.sony.imaging.app.pictureeffectplus.shooting.layout;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.sony.imaging.app.base.common.widget.FooterGuide;
import com.sony.imaging.app.base.common.widget.FooterGuideDataResId;
import com.sony.imaging.app.base.menu.BaseMenuService;
import com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout;
import com.sony.imaging.app.pictureeffectplus.AppLog;
import com.sony.imaging.app.pictureeffectplus.R;
import com.sony.imaging.app.pictureeffectplus.shooting.camera.PictureEffectPlusController;
import com.sony.imaging.app.pictureeffectplus.shooting.widget.VerticalSeekBar;

/* loaded from: classes.dex */
public class PictureEffectPlusSatuCompOptionMenuLayout extends DisplayMenuItemsMenuLayout {
    private PictureEffectPlusController mController;
    private static final String TAG = AppLog.getClassName();
    protected static final StringBuilder STRBUILD = new StringBuilder();
    protected String FUNC_NAME = "";
    private FooterGuide mFooterGuide = null;
    private ViewGroup mCurrentView = null;
    private VerticalSeekBar mVerticalSeekBar = null;
    private TextView mScreenTitle = null;
    private TextView mGaugeText = null;
    private int curValue = 0;
    private int STEP = 32;
    private boolean SK1_pushed = false;
    private int level = 1;
    private String nextMenuId = null;
    private boolean isValidMenuItem = false;
    private String openMenuItemId = null;

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.mCurrentView = (ViewGroup) super.onCreateView(inflater, container, savedInstanceState);
        this.mCurrentView = (ViewGroup) obtainViewFromPool(R.layout.pictureeffectplus_menu_exposure_comp);
        this.mScreenTitle = (TextView) this.mCurrentView.findViewById(R.id.menu_screen_title);
        this.mScreenTitle.setText(R.string.STRID_FUNC_MLTPARTCLR);
        this.mVerticalSeekBar = (VerticalSeekBar) this.mCurrentView.findViewById(R.id.vertical_Seekbar);
        this.mFooterGuide = (FooterGuide) this.mCurrentView.findViewById(R.id.menu_screen_footer_guide);
        this.mGaugeText = (TextView) this.mCurrentView.findViewById(R.id.gauge_text);
        this.mFooterGuide.setData(new FooterGuideDataResId(getActivity().getApplicationContext(), android.R.string.zen_mode_feature_name, android.R.string.httpErrorRedirectLoop));
        this.FUNC_NAME = "onCreateView";
        STRBUILD.replace(0, STRBUILD.length(), this.FUNC_NAME);
        Log.i(TAG, STRBUILD.toString());
        return this.mCurrentView;
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        AppLog.enter(TAG, AppLog.getMethodName());
        super.onResume();
        this.mService = new BaseMenuService(getActivity().getApplicationContext());
        this.mController = PictureEffectPlusController.getInstance();
        if (this.mController.getPartColorCurrentPlate() == 0) {
            this.mController.setPartColorCh(0);
        } else if (this.mController.getPartColorCurrentPlate() == 2) {
            this.mController.setPartColorCh(1);
        }
        this.mController.changeTargetChannelOnlyPartColor();
        this.mController.setColorCaptureStatus(true);
        this.mController.copyTargetColor2AdjustColor();
        this.level = this.mController.getCurrentSaturation(this.STEP);
        AppLog.info(TAG, "Saturation Level :: " + this.level + " For Color  " + this.level);
        if (this.level < 1) {
            this.level = 1;
        }
        this.curValue = this.level;
        this.mVerticalSeekBar.setProgressLevel(this.level);
        this.mGaugeText.setText("" + this.mVerticalSeekBar.getProgress());
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyUp(int keyCode, KeyEvent event) {
        if (VerticalSeekBar.sISTouchEnable) {
            if (event.getScanCode() == 103) {
                upMovement();
            } else if (event.getScanCode() == 108) {
                downMovement();
            }
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedUpKey() {
        VerticalSeekBar.sISTouchEnable = false;
        AppLog.enter(TAG, AppLog.getMethodName());
        upMovement();
        AppLog.exit(TAG, AppLog.getMethodName());
        return super.pushedUpKey();
    }

    private void upMovement() {
        this.curValue = this.mVerticalSeekBar.moveUp();
        AppLog.info(TAG, AppLog.getMethodName() + "Gauge Bar Cur Value :::::::::::::::: " + this.curValue);
        this.mGaugeText.setText("" + this.curValue);
        this.mController.setChangedSaturationValue(this.curValue, this.STEP);
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedGuideFuncKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedDownKey() {
        AppLog.enter(TAG, AppLog.getMethodName());
        VerticalSeekBar.sISTouchEnable = false;
        downMovement();
        AppLog.exit(TAG, AppLog.getMethodName());
        return super.pushedDownKey();
    }

    private void downMovement() {
        if (this.mVerticalSeekBar.getProgress() > 1) {
            this.curValue = this.mVerticalSeekBar.moveDown();
        }
        AppLog.info(TAG, AppLog.getMethodName() + "Gauge Bar Cur Value :::::::::::::::: " + this.curValue);
        this.mGaugeText.setText("" + this.curValue);
        this.mController.setChangedSaturationValue(this.curValue, this.STEP);
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedMainDialPrev() {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.exit(TAG, AppLog.getMethodName());
        return pushedUpKey();
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedMainDialNext() {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.exit(TAG, AppLog.getMethodName());
        return pushedDownKey();
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedCenterKey() {
        AppLog.enter(TAG, AppLog.getMethodName());
        openPreviousLayout();
        AppLog.exit(TAG, AppLog.getMethodName());
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMenuKey() {
        AppLog.enter(TAG, AppLog.getMethodName());
        this.SK1_pushed = true;
        openPreviousLayout();
        AppLog.exit(TAG, AppLog.getMethodName());
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout
    public void closeLayout() {
        setColorPlate();
        if (this.SK1_pushed) {
            this.mController.setOriginalColor();
        } else {
            this.mController.setChangedColor();
        }
        this.mController.setCustomSet2CustomOnBackupOptionValue();
        this.mController.setComeFromColorAdjustment(true);
        this.FUNC_NAME = "closeLayout";
        STRBUILD.replace(0, STRBUILD.length(), this.FUNC_NAME);
        Log.i(TAG, STRBUILD.toString());
        this.SK1_pushed = false;
        this.isValidMenuItem = false;
        VerticalSeekBar.sISTouchEnable = false;
        super.closeLayout();
    }

    private void setColorPlate() {
        switch (this.mController.getPartColorCurrentPlate()) {
            case 0:
            case 1:
                this.mController.setPartColorCh(0);
                this.mService.setMenuItemId(PictureEffectPlusController.MODE_PART_COLOR_CH0);
                this.nextMenuId = this.mService.getMenuItemNextMenuID(PictureEffectPlusController.MODE_PART_COLOR_CH0);
                this.isValidMenuItem = this.mService.isMenuItemValid(PictureEffectPlusController.MODE_PART_COLOR_CH0);
                this.openMenuItemId = PictureEffectPlusController.MODE_PART_COLOR_CH0;
                return;
            case 2:
            case 3:
                this.mController.setPartColorCh(1);
                this.mService.setMenuItemId(PictureEffectPlusController.MODE_PART_COLOR_CH1);
                this.nextMenuId = this.mService.getMenuItemNextMenuID(PictureEffectPlusController.MODE_PART_COLOR_CH1);
                this.isValidMenuItem = this.mService.isMenuItemValid(PictureEffectPlusController.MODE_PART_COLOR_CH1);
                this.openMenuItemId = PictureEffectPlusController.MODE_PART_COLOR_CH1;
                return;
            default:
                this.mController.setPartColorCh(0);
                this.mService.setMenuItemId(PictureEffectPlusController.MODE_PART_COLOR_CH0);
                this.nextMenuId = this.mService.getMenuItemNextMenuID(PictureEffectPlusController.MODE_PART_COLOR_CH0);
                this.isValidMenuItem = this.mService.isMenuItemValid(PictureEffectPlusController.MODE_PART_COLOR_CH0);
                this.openMenuItemId = PictureEffectPlusController.MODE_PART_COLOR_CH0;
                return;
        }
    }

    private void openPreviousLayout() {
        AppLog.enter(TAG, AppLog.getMethodName());
        setColorPlate();
        if (this.nextMenuId != null && this.isValidMenuItem) {
            openNextMenu(this.openMenuItemId, this.nextMenuId);
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }
}
