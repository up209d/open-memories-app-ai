package com.sony.imaging.app.timelapse.angleshift.browser;

import android.os.Bundle;
import com.sony.imaging.app.base.menu.MenuState;
import com.sony.imaging.app.base.playback.browser.BrowserSingle;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.widget.AppIconView;
import com.sony.imaging.app.timelapse.R;
import com.sony.imaging.app.timelapse.angleshift.common.AngleShiftConstants;
import com.sony.imaging.app.timelapse.angleshift.common.AngleShiftSetting;
import com.sony.imaging.app.timelapse.angleshift.controller.AngleShiftThemeSelectionController;
import com.sony.imaging.app.timelapse.metadatamanager.TLCommonUtil;
import com.sony.imaging.app.timelapse.playback.controller.PlayBackController;
import com.sony.imaging.app.timelapse.playback.layout.ListViewLayout;
import com.sony.imaging.app.util.ApoWrapper;
import com.sony.imaging.app.util.BackUpUtil;
import com.sony.imaging.app.util.NotificationListener;
import com.sony.scalar.graphics.imagefilter.RotateImageFilter;

/* loaded from: classes.dex */
public class AngleShiftEditSingle extends BrowserSingle implements NotificationListener {
    private final String[] TAGS = {AngleShiftConstants.TAG_AS_SAVING_DONE};

    @Override // com.sony.imaging.app.base.playback.browser.BrowserSingle, com.sony.imaging.app.base.playback.base.SingleStateBase, com.sony.imaging.app.base.playback.base.PlayStateWithLayoutBase, com.sony.imaging.app.base.playback.base.PlayStateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        ApoWrapper.fixApoType(ApoWrapper.APO_TYPE.NONE);
        CameraNotificationManager.getInstance().setNotificationListener(this);
        int theme = BackUpUtil.getInstance().getPreferenceInt(AngleShiftConstants.AS_CURRENT_THEME, 0);
        if (theme == 4) {
            if (AngleShiftSetting.getInstance().isSameInputImageSizeForCustom()) {
                AngleShiftSetting.getInstance().applyBackupCustomSettings();
            } else {
                AngleShiftSetting.getInstance().setRotateDegree(RotateImageFilter.ROTATION_DEGREE.DEGREE_0);
                AngleShiftSetting.getInstance().setAngleShiftCustomDefaultRect(true);
                AngleShiftSetting.getInstance().setAngleShiftCustomStartRect(null);
                AngleShiftSetting.getInstance().setAngleShiftCustomEndRect(null);
            }
            AngleShiftSetting.getInstance().backupCustomInputImageSize();
        } else {
            AngleShiftSetting.getInstance().setRotateDegree(RotateImageFilter.ROTATION_DEGREE.DEGREE_0);
            AngleShiftSetting.getInstance().setAngleShiftCustomDefaultRect(true);
        }
        TLCommonUtil.getInstance().setSavingDoneLayout(null);
        AngleShiftSetting.getInstance().setframeId(0);
        super.onResume();
        AppIconView.setIcon(R.drawable.p_16_dd_parts_tm_as_appicon, R.drawable.p_16_aa_parts_tm_as_appicon);
        openThemeSelection(true);
    }

    @Override // com.sony.imaging.app.base.playback.browser.BrowserSingle, com.sony.imaging.app.base.playback.base.SingleStateBase, com.sony.imaging.app.base.playback.base.PlayStateWithLayoutBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        int theme = BackUpUtil.getInstance().getPreferenceInt(AngleShiftConstants.AS_CURRENT_THEME, 0);
        if (theme == 4) {
            AngleShiftSetting.getInstance().backupCustomInputImageSize();
            AngleShiftSetting.getInstance().backupCustomSettings();
        }
        ApoWrapper.clearFixedApoType();
        CameraNotificationManager.getInstance().removeNotificationListener(this);
        if (TLCommonUtil.getInstance().getSavingDoneLayout() != null) {
            TLCommonUtil.getInstance().getSavingDoneLayout().closeLayout();
        }
        TLCommonUtil.getInstance().setSavingDoneLayout(null);
        closeLayout("ID_ANGLESHIFTPREVIEWLAYOUT");
        closeLayout("ID_ANGLESHIFTPROGRESSLAYOUT");
        closeLayout("ID_ANGLESHIFTCONFIRMSAVINGLAYOUT");
        closeLayout("ID_ANGLESHIFTSAVINGDONELAYOUT");
        AppIconView.setIcon(R.drawable.p_16_dd_parts_tm_appicon, R.drawable.p_16_aa_parts_tm_appicon);
        System.gc();
        super.onPause();
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedCenterKey() {
        Bundle bundle = new Bundle();
        setNextState("Preview", bundle);
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMenuKey() {
        openThemeSelection(false);
        return 1;
    }

    private void openThemeSelection(boolean useDefaultValue) {
        Bundle bundle = new Bundle();
        bundle.putString(MenuState.ITEM_ID, AngleShiftThemeSelectionController.THEMESELECTION);
        if (PlayBackController.getInstance().getmTimeLapseBO() == null) {
            PlayBackController.getInstance().intializeCursorData(ListViewLayout.listPosition);
        }
        if (useDefaultValue) {
            AngleShiftSetting.getInstance().setDefaultValue();
        }
        addChildState("PlayBackMenu", bundle);
        CameraNotificationManager.getInstance().requestNotify(AngleShiftConstants.TAG_AS_THEME_LAYOUT);
    }

    private void exitAngleShift() {
        getRootContainer().changeToShooting();
    }

    @Override // com.sony.imaging.app.util.NotificationListener
    public String[] getTags() {
        return this.TAGS;
    }

    @Override // com.sony.imaging.app.util.NotificationListener
    public void onNotify(String tag) {
        if (tag.equalsIgnoreCase(AngleShiftConstants.TAG_AS_SAVING_DONE)) {
            exitAngleShift();
        }
    }
}
