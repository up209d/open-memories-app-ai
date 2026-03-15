package com.sony.imaging.app.base.shooting.widget;

import android.R;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import com.sony.imaging.app.base.common.DisplayModeObserver;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.FlashController;
import com.sony.imaging.app.util.NotificationListener;
import com.sony.imaging.app.util.StringBuilderThreadLocal;
import java.util.HashMap;
import java.util.List;

/* loaded from: classes.dex */
public class FlashModeIcon extends FlashAbstractIcon {
    private static final String FLASH_MODE_AUTO = "Flash_Auto";
    private static final String FLASH_MODE_AUTO_WITH_EYE = "Flash_AutowithEye";
    private static final String FLASH_MODE_FILL = "Flash_Fill";
    private static final String FLASH_MODE_FILL_WITH_EYE = "Flash_FillwithEye";
    private static final String FLASH_MODE_FILL_WITH_HSS = "Flash_FillwithHss";
    private static final String FLASH_MODE_OFF = "Flash_Off";
    private static final String FLASH_MODE_REAR_SYNC = "Flash_RearSync";
    private static final String FLASH_MODE_SLOW_SYNC = "Flash_SlowSync";
    private static final String FLASH_MODE_SLOW_SYNC_WITH_EYE = "Flash_SlowSyncwithEye";
    private static final String FLASH_MODE_SLOW_SYNC_WITH_HSS = "Flash_SlowSyncwithHss";
    private static final String FLASH_MODE_WIRELESS = "Flash_Wireless";
    private static final String FLASH_MODE_WIRELESS_WITH_HSS = "Flash_WirelesswithHss";
    private static final String LOG_FLASHMODE = "Flash mode is ";
    private static final String LOG_NOTIFYTAG = "NotifyTAG is ";
    private static final String LOG_RESOURCE_NOT_FOUND = "Resource is not found. id = ";
    private static final String LOG_UNKNOWN = "unknown flash type";
    private static final HashMap<String, Integer> RESID_DICTIONARY_EVF;
    private static final HashMap<String, Integer> RESID_DICTIONARY_PANEL = new HashMap<>();
    private static final String TAG = "FlashModeIcon";
    private String[] TAGS;

    static {
        RESID_DICTIONARY_PANEL.put(FLASH_MODE_OFF, Integer.valueOf(R.drawable.ic_input_extract_action_send));
        RESID_DICTIONARY_PANEL.put(FLASH_MODE_AUTO, Integer.valueOf(R.drawable.ic_instant_icon_badge_bolt));
        RESID_DICTIONARY_PANEL.put(FLASH_MODE_FILL, Integer.valueOf(R.drawable.ic_jog_dial_answer));
        RESID_DICTIONARY_PANEL.put(FLASH_MODE_SLOW_SYNC, Integer.valueOf(R.drawable.ic_jog_dial_answer_and_end));
        RESID_DICTIONARY_PANEL.put(FLASH_MODE_REAR_SYNC, Integer.valueOf(R.drawable.ic_jog_dial_decline));
        RESID_DICTIONARY_PANEL.put(FLASH_MODE_WIRELESS, Integer.valueOf(R.drawable.ic_lockscreen_handle_normal));
        RESID_DICTIONARY_PANEL.put(FLASH_MODE_FILL_WITH_HSS, Integer.valueOf(R.drawable.ic_media_route_connected_dark_15_mtrl));
        RESID_DICTIONARY_PANEL.put(FLASH_MODE_SLOW_SYNC_WITH_HSS, Integer.valueOf(R.drawable.ic_media_route_connected_dark_18_mtrl));
        RESID_DICTIONARY_PANEL.put(FLASH_MODE_WIRELESS_WITH_HSS, Integer.valueOf(R.drawable.ic_media_route_connected_dark_21_mtrl));
        RESID_DICTIONARY_PANEL.put(FLASH_MODE_AUTO_WITH_EYE, 17304762);
        RESID_DICTIONARY_PANEL.put(FLASH_MODE_FILL_WITH_EYE, 17304763);
        RESID_DICTIONARY_PANEL.put(FLASH_MODE_SLOW_SYNC_WITH_EYE, 17304764);
        RESID_DICTIONARY_EVF = new HashMap<>();
        RESID_DICTIONARY_EVF.put(FLASH_MODE_OFF, 17304801);
        RESID_DICTIONARY_EVF.put(FLASH_MODE_AUTO, Integer.valueOf(R.drawable.menuitem_checkbox));
        RESID_DICTIONARY_EVF.put(FLASH_MODE_FILL, Integer.valueOf(R.drawable.dialog_middle_holo_dark));
        RESID_DICTIONARY_EVF.put(FLASH_MODE_SLOW_SYNC, 17303944);
        RESID_DICTIONARY_EVF.put(FLASH_MODE_REAR_SYNC, Integer.valueOf(R.drawable.btn_toggle_off_disabled_holo_light));
        RESID_DICTIONARY_EVF.put(FLASH_MODE_WIRELESS, 17303942);
        RESID_DICTIONARY_EVF.put(FLASH_MODE_FILL_WITH_HSS, 17305940);
        RESID_DICTIONARY_EVF.put(FLASH_MODE_SLOW_SYNC_WITH_HSS, 17303898);
        RESID_DICTIONARY_EVF.put(FLASH_MODE_WIRELESS_WITH_HSS, 17305934);
        RESID_DICTIONARY_EVF.put(FLASH_MODE_AUTO_WITH_EYE, Integer.valueOf(R.drawable.minitab_lt_press));
        RESID_DICTIONARY_EVF.put(FLASH_MODE_FILL_WITH_EYE, 17305290);
        RESID_DICTIONARY_EVF.put(FLASH_MODE_SLOW_SYNC_WITH_EYE, Integer.valueOf(R.drawable.messaging_user));
    }

    public FlashModeIcon(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.TAGS = new String[]{CameraNotificationManager.FLASH_CHANGE, CameraNotificationManager.DEVICE_INTERNAL_FLASH_CHANGED, CameraNotificationManager.DEVICE_EXTERNAL_FLASH_CHANGED, CameraNotificationManager.SCENE_MODE, CameraNotificationManager.REC_MODE_CHANGED};
        this.LOGTAG = TAG;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage
    public boolean isVisible() {
        boolean z = false;
        if (this.mFnMode) {
            this.mVisible = true;
        } else {
            List<String> supported = this.mFlashController.getSupportedValue(FlashController.FLASHMODE);
            if (supported == null || supported.size() == 0) {
                this.mVisible = false;
            } else {
                boolean mode = this.mFlashController.isUnavailableSceneFactor(FlashController.FLASHMODE);
                boolean type = this.mFlashController.isUnavailableSceneFactor(FlashController.FLASHTYPE);
                if (!mode && !type) {
                    z = true;
                }
                this.mVisible = z;
                if (!this.mVisible) {
                    String value = this.mFlashController.getValue(FlashController.FLASHMODE);
                    if (!"off".equals(value)) {
                        this.mVisible = true;
                    }
                }
            }
        }
        return this.mVisible;
    }

    @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage
    protected NotificationListener getNotificationListener() {
        return new NotificationListener() { // from class: com.sony.imaging.app.base.shooting.widget.FlashModeIcon.1
            @Override // com.sony.imaging.app.util.NotificationListener
            public void onNotify(String tag) {
                StringBuilder builder = StringBuilderThreadLocal.getScratchBuilder();
                Log.i(FlashModeIcon.TAG, builder.replace(0, builder.length(), FlashModeIcon.LOG_NOTIFYTAG).append(tag).toString());
                StringBuilderThreadLocal.releaseScratchBuilder(builder);
                if (CameraNotificationManager.DEVICE_EXTERNAL_FLASH_CHANGED.equals(tag) || CameraNotificationManager.DEVICE_INTERNAL_FLASH_CHANGED.equals(tag) || CameraNotificationManager.REC_MODE_CHANGED.equals(tag)) {
                    FlashModeIcon.this.updateFlashDeviceStatus(tag);
                }
                FlashModeIcon.this.refresh();
            }

            @Override // com.sony.imaging.app.util.NotificationListener
            public String[] getTags() {
                return FlashModeIcon.this.TAGS;
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.widget.FlashAbstractIcon, com.sony.imaging.app.base.shooting.widget.ActiveImage
    public void refresh() {
        super.refresh();
        isVisible();
        if ((this.mVisible && this.isEnable) || this.mFnMode) {
            setVisibility(0);
            String flashMode = FLASH_MODE_OFF;
            String mode = this.mFlashController.getValue(FlashController.FLASHMODE);
            String redEye = this.mFlashController.getValue(FlashController.FLASH_REDEYE);
            boolean hss = this.isHss;
            if (this.mFnMode) {
                redEye = "off";
                hss = false;
            }
            StringBuilder builder = StringBuilderThreadLocal.getScratchBuilder();
            Log.d(TAG, builder.replace(0, builder.length(), LOG_FLASHMODE).append(mode).toString());
            StringBuilderThreadLocal.releaseScratchBuilder(builder);
            if ("off".equals(mode)) {
                flashMode = FLASH_MODE_OFF;
            } else if ("auto".equals(mode)) {
                if ("off".equals(redEye)) {
                    flashMode = FLASH_MODE_AUTO;
                } else {
                    flashMode = FLASH_MODE_AUTO_WITH_EYE;
                }
            } else if (FlashController.FRONTSYNC.equals(mode)) {
                if (!hss) {
                    if ("off".equals(redEye)) {
                        flashMode = FLASH_MODE_FILL;
                    } else {
                        flashMode = FLASH_MODE_FILL_WITH_EYE;
                    }
                } else {
                    flashMode = FLASH_MODE_FILL_WITH_HSS;
                }
            } else if (FlashController.SLOWSYN.equals(mode)) {
                if (!hss) {
                    if ("off".equals(redEye)) {
                        flashMode = FLASH_MODE_SLOW_SYNC;
                    } else {
                        flashMode = FLASH_MODE_SLOW_SYNC_WITH_EYE;
                    }
                } else {
                    flashMode = FLASH_MODE_SLOW_SYNC_WITH_HSS;
                }
            } else if (FlashController.SLOWREARSYN.equals(mode)) {
                flashMode = FLASH_MODE_REAR_SYNC;
            } else if (FlashController.REARSYN.equals(mode)) {
                flashMode = FLASH_MODE_REAR_SYNC;
            } else if (FlashController.WIRELESS.equals(mode)) {
                if (!hss) {
                    flashMode = FLASH_MODE_WIRELESS;
                } else {
                    flashMode = FLASH_MODE_WIRELESS_WITH_HSS;
                }
            }
            Drawable flashModeImg = getDrawableFromPool(flashMode);
            if (flashModeImg != null) {
                setImageDrawable(flashModeImg);
                return;
            } else {
                Log.e(TAG, LOG_UNKNOWN);
                return;
            }
        }
        setVisibility(4);
    }

    private Drawable getDrawableFromPool(String mode) {
        Integer resId;
        int device = DisplayModeObserver.getInstance().getActiveDevice();
        HashMap<String, Integer> RESID_DICTIONARY = getHashMap(device);
        if (RESID_DICTIONARY == null || (resId = RESID_DICTIONARY.get(mode)) == null) {
            return null;
        }
        try {
            Drawable d = getResources().getDrawable(resId.intValue());
            return d;
        } catch (Resources.NotFoundException e) {
            StringBuilder builder = StringBuilderThreadLocal.getScratchBuilder();
            Log.i(TAG, builder.replace(0, builder.length(), LOG_RESOURCE_NOT_FOUND).append(mode).toString());
            StringBuilderThreadLocal.releaseScratchBuilder(builder);
            return null;
        }
    }

    protected HashMap<String, Integer> getHashMap(int device) {
        if (device == 0 || device == 2) {
            HashMap<String, Integer> hash = RESID_DICTIONARY_PANEL;
            return hash;
        }
        if (device != 1) {
            return null;
        }
        HashMap<String, Integer> hash2 = RESID_DICTIONARY_EVF;
        return hash2;
    }
}
