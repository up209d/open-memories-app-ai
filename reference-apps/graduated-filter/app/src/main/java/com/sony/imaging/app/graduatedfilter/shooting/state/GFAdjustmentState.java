package com.sony.imaging.app.graduatedfilter.shooting.state;

import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.shooting.StateBase;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.fw.RunStatus;
import com.sony.imaging.app.graduatedfilter.GFBackUpKey;
import com.sony.imaging.app.graduatedfilter.caution.GFInfo;
import com.sony.imaging.app.graduatedfilter.common.AppLog;
import com.sony.imaging.app.graduatedfilter.common.GFCommonUtil;
import com.sony.imaging.app.graduatedfilter.common.GFConstants;
import com.sony.imaging.app.graduatedfilter.common.GFImageAdjustmentUtil;
import com.sony.imaging.app.graduatedfilter.menu.layout.GFBorderMenuLayout;
import com.sony.imaging.app.graduatedfilter.menu.layout.GFShadingMenuLayout;
import com.sony.imaging.app.graduatedfilter.sa.NDSA;
import com.sony.imaging.app.graduatedfilter.shooting.GFCompositProcess;
import com.sony.imaging.app.graduatedfilter.shooting.GFEffectParameters;
import com.sony.imaging.app.util.BatteryObserver;
import com.sony.imaging.app.util.NotificationListener;

/* loaded from: classes.dex */
public class GFAdjustmentState extends StateBase implements NotificationListener {
    private static final String TAG = AppLog.getClassName();
    private static String[] TAGS = {GFConstants.UPDATE_APPSETTING, BatteryObserver.TAG_LEVEL, BatteryObserver.TAG_PLUGGED, BatteryObserver.TAG_STATUS};

    @Override // com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        GFCommonUtil.getInstance().startAdjustmentSetting();
        GFImageAdjustmentUtil.getInstance().startAdjustmentPreview();
        CameraNotificationManager.getInstance().setNotificationListener(this);
        openLayout("PreviewLayout");
        CameraNotificationManager.getInstance().requestNotify(BatteryObserver.TAG_STATUS);
    }

    @Override // com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        AppLog.enter(TAG, "onPause");
        GFCommonUtil.getInstance().stopAdjustmentSetting();
        NDSA.getInstance().cancel();
        GFImageAdjustmentUtil.getInstance().terminateAdjustmentPreview();
        CameraNotificationManager.getInstance().removeNotificationListener(this);
        closeLayout("PreviewLayout");
        closeLayout("AdjustmentLayout");
        closeLayout(GFBorderMenuLayout.MENU_ID);
        closeLayout(GFShadingMenuLayout.MENU_ID);
        int runStatus = RunStatus.getStatus();
        if (2 == runStatus) {
            GFCompositProcess.storeCompositImage();
        }
        GFEffectParameters.Parameters parameter = GFEffectParameters.getInstance().getParameters();
        GFBackUpKey.getInstance().saveLastParameters(parameter.flatten());
        super.onPause();
        AppLog.exit(TAG, "onPause");
    }

    private void stopAdjustmentByBatteryPreEnd() {
        GFImageAdjustmentUtil.getInstance().terminateAdjustmentPreview();
        GFCompositProcess.storeCompositImage();
        setNextState("Development", null);
    }

    @Override // com.sony.imaging.app.util.NotificationListener
    public String[] getTags() {
        return TAGS;
    }

    @Override // com.sony.imaging.app.util.NotificationListener
    public void onNotify(String tag) {
        AppLog.info(TAG, "onNotify: " + tag);
        if (BatteryObserver.TAG_LEVEL.equalsIgnoreCase(tag) || BatteryObserver.TAG_PLUGGED.equalsIgnoreCase(tag) || BatteryObserver.TAG_STATUS.equalsIgnoreCase(tag)) {
            if (GFCommonUtil.getInstance().isBatteryPreEnd()) {
                CautionUtilityClass.getInstance().requestTrigger(GFInfo.CAUTION_ID_DLAPP_NOBATTERY);
                stopAdjustmentByBatteryPreEnd();
                return;
            }
            return;
        }
        if (GFConstants.UPDATE_APPSETTING.equalsIgnoreCase(tag)) {
            GFImageAdjustmentUtil.getInstance().updateAdjustmentPreview();
        }
    }
}
