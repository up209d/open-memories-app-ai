package com.sony.imaging.app.base.shooting.widget;

import android.R;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseIntArray;
import com.sony.imaging.app.base.common.widget.RotationalSubLcdTextView;
import com.sony.imaging.app.base.shooting.camera.WhiteBalanceController;
import com.sony.imaging.app.graduatedfilter.common.GFConstants;

/* loaded from: classes.dex */
public class RotationalSubLcdTextWhiteBalance extends RotationalSubLcdTextView {
    private static final String TAG = RotationalSubLcdTextWhiteBalance.class.getSimpleName();
    public static SparseIntArray mTextMap = null;
    private final int WB_TEMP;
    private WhiteBalanceController mWBc;

    public RotationalSubLcdTextWhiteBalance(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.WB_TEMP = WhiteBalanceController.DEF_TEMP;
        setMap();
    }

    public RotationalSubLcdTextWhiteBalance(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.WB_TEMP = WhiteBalanceController.DEF_TEMP;
        setMap();
    }

    public RotationalSubLcdTextWhiteBalance(Context context) {
        super(context);
        this.WB_TEMP = WhiteBalanceController.DEF_TEMP;
        setMap();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.common.widget.RotationalSubLcdTextView, com.sony.imaging.app.base.common.widget.SubLcdTextView, com.sony.imaging.app.base.common.widget.AbstractSubLCDView, android.view.View
    public void onAttachedToWindow() {
        if (this.mWBc == null) {
            this.mWBc = WhiteBalanceController.getInstance();
        }
        super.onAttachedToWindow();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.common.widget.RotationalSubLcdTextView, com.sony.imaging.app.base.common.widget.SubLcdTextView, com.sony.imaging.app.base.common.widget.AbstractSubLCDView, android.view.View
    public void onDetachedFromWindow() {
        if (this.mWBc != null) {
            this.mWBc = null;
        }
        super.onDetachedFromWindow();
    }

    @Override // com.sony.imaging.app.base.common.widget.RotationalSubLcdTextView
    protected String makeText() {
        String value = this.mWBc.getValue();
        int resID = 0;
        if (!WhiteBalanceController.COLOR_TEMP.equals(value) && !WhiteBalanceController.CUSTOM.equals(value)) {
            return "";
        }
        WhiteBalanceController.WhiteBalanceParam wbParam = (WhiteBalanceController.WhiteBalanceParam) this.mWBc.getDetailValue();
        if (wbParam != null) {
            int temp = wbParam.getColorTemp();
            resID = mTextMap.get(temp);
        }
        if (resID != 0) {
            String ret = getResources().getString(resID);
            return ret;
        }
        Log.w(TAG, "WBtemp is not supported");
        return "";
    }

    @Override // com.sony.imaging.app.base.common.widget.RotationalSubLcdTextView, com.sony.imaging.app.base.common.widget.Rotational
    public boolean isValidValue() {
        String value = this.mWBc.getValue();
        if (!"auto".equals(value)) {
            return true;
        }
        return false;
    }

    private void setMap() {
        mTextMap = new SparseIntArray();
        mTextMap.put(2500, R.string.package_updated_device_owner);
        mTextMap.put(2600, R.string.passwordIncorrect);
        mTextMap.put(2700, R.string.password_keyboard_label_alpha_key);
        mTextMap.put(2800, R.string.password_keyboard_label_alt_key);
        mTextMap.put(2900, R.string.peerTtyModeFull);
        mTextMap.put(3000, R.string.peerTtyModeOff);
        mTextMap.put(3100, R.string.peerTtyModeVco);
        mTextMap.put(3200, R.string.perm_costs_money);
        mTextMap.put(3300, R.string.permdesc_acceptHandovers);
        mTextMap.put(3400, R.string.permdesc_accessBackgroundLocation);
        mTextMap.put(3500, R.string.permdesc_accessCoarseLocation);
        mTextMap.put(3600, R.string.permdesc_accessDrmCertificates);
        mTextMap.put(3700, R.string.permdesc_accessFineLocation);
        mTextMap.put(3800, R.string.permdesc_accessImsCallService);
        mTextMap.put(3900, R.string.permdesc_accessLocationExtraCommands);
        mTextMap.put(4000, R.string.permdesc_accessNetworkConditions);
        mTextMap.put(4100, R.string.permdesc_accessNetworkState);
        mTextMap.put(4200, R.string.permdesc_accessNotifications);
        mTextMap.put(4300, R.string.permdesc_accessWifiState);
        mTextMap.put(4400, R.string.permdesc_accessWimaxState);
        mTextMap.put(4500, R.string.permdesc_access_notification_policy);
        mTextMap.put(4600, R.string.permdesc_activityRecognition);
        mTextMap.put(4700, R.string.permdesc_addVoicemail);
        mTextMap.put(4800, R.string.permdesc_answerPhoneCalls);
        mTextMap.put(4900, R.string.permdesc_audioWrite);
        mTextMap.put(5000, R.string.permdesc_bindCarrierMessagingService);
        mTextMap.put(5100, R.string.permdesc_bindCarrierServices);
        mTextMap.put(5200, R.string.permdesc_bindCellBroadcastService);
        mTextMap.put(5300, R.string.permdesc_bindConditionProviderService);
        mTextMap.put(5400, R.string.permdesc_bindNotificationListenerService);
        mTextMap.put(WhiteBalanceController.DEF_TEMP, R.string.permdesc_bluetooth);
        mTextMap.put(5600, R.string.permdesc_bluetoothAdmin);
        mTextMap.put(5700, R.string.permdesc_bodySensors);
        mTextMap.put(5800, R.string.permdesc_broadcastSticky);
        mTextMap.put(5900, R.string.permdesc_cameraOpenCloseListener);
        mTextMap.put(6000, R.string.permdesc_changeNetworkState);
        mTextMap.put(6100, R.string.permdesc_changeTetherState);
        mTextMap.put(6200, R.string.permdesc_changeWifiMulticastState);
        mTextMap.put(6300, R.string.permdesc_connection_manager);
        mTextMap.put(6400, R.string.permdesc_createNetworkSockets);
        mTextMap.put(6500, R.string.permdesc_disableKeyguard);
        mTextMap.put(6600, R.string.permdesc_enableCarMode);
        mTextMap.put(6700, R.string.permdesc_mediaLocation);
        mTextMap.put(6800, R.string.permdesc_modifyAudioSettings);
        mTextMap.put(6900, R.string.permdesc_modifyNetworkAccounting);
        mTextMap.put(7000, R.string.permdesc_nfc);
        mTextMap.put(7100, R.string.permdesc_persistentActivity);
        mTextMap.put(7200, R.string.permdesc_preferredPaymentInfo);
        mTextMap.put(7300, R.string.permdesc_processOutgoingCalls);
        mTextMap.put(7400, R.string.permdesc_readCalendar);
        mTextMap.put(7500, R.string.permdesc_readCallLog);
        mTextMap.put(7600, R.string.permdesc_readCellBroadcasts);
        mTextMap.put(7700, R.string.permdesc_readContacts);
        mTextMap.put(7800, R.string.permdesc_readInstallSessions);
        mTextMap.put(7900, R.string.permdesc_readSms);
        mTextMap.put(GFConstants.SHOOTING_GUIDE_DISP_TIME, R.string.permdesc_readSyncSettings);
        mTextMap.put(8100, R.string.permdesc_readSyncStats);
        mTextMap.put(8200, R.string.permdesc_receiveBootCompleted);
        mTextMap.put(8300, R.string.permdesc_receiveMms);
        mTextMap.put(8400, R.string.permdesc_receiveSms);
        mTextMap.put(8500, R.string.permdesc_route_media_output);
        mTextMap.put(8600, R.string.permdesc_runInBackground);
        mTextMap.put(8700, R.string.permdesc_sdcardRead);
        mTextMap.put(8800, R.string.permdesc_sdcardWrite);
        mTextMap.put(8900, R.string.permdesc_sendSms);
        mTextMap.put(9000, R.string.permdesc_setAlarm);
        mTextMap.put(9100, R.string.permdesc_setInputCalibration);
        mTextMap.put(9200, R.string.permgroupdesc_contacts);
        mTextMap.put(9300, R.string.permgroupdesc_microphone);
        mTextMap.put(9400, R.string.permgroupdesc_phone);
        mTextMap.put(9500, R.string.permgroupdesc_sensors);
        mTextMap.put(9600, R.string.permgroupdesc_sms);
        mTextMap.put(9700, R.string.permgroupdesc_storage);
        mTextMap.put(9800, R.string.permgrouplab_activityRecognition);
        mTextMap.put(9900, R.string.permgrouplab_calendar);
    }
}
