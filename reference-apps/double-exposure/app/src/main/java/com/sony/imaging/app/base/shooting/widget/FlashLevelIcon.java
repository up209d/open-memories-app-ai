package com.sony.imaging.app.base.shooting.widget;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import com.sony.imaging.app.base.common.DisplayModeObserver;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.FlashController;
import com.sony.imaging.app.doubleexposure.common.DoubleExposureConstant;
import com.sony.imaging.app.util.NotificationListener;
import java.util.HashMap;

/* loaded from: classes.dex */
public class FlashLevelIcon extends FlashAbstractIcon {
    private static final String LOG_ONNOTIFY = "onNotify tag = ";
    private static final String LOG_RESOURCE_NOT_FOUND = "Resource is not found. mode = ";
    private static final HashMap<String, Integer> RESID_DICTIONARY_EVF_0_3;
    private static final HashMap<String, Integer> RESID_DICTIONARY_EVF_0_5;
    private static final HashMap<String, Integer> RESID_DICTIONARY_PANEL_0_5;
    private static final String TAG = "FlashLevelIcon";
    private String[] TAGS;
    private static StringBuilder STRBUILD = new StringBuilder();
    private static final HashMap<String, Integer> RESID_DICTIONARY_PANEL_0_3 = new HashMap<>();

    static {
        RESID_DICTIONARY_PANEL_0_3.put("-9", 17306621);
        RESID_DICTIONARY_PANEL_0_3.put("-8", 17306699);
        RESID_DICTIONARY_PANEL_0_3.put("-7", 17306823);
        RESID_DICTIONARY_PANEL_0_3.put("-6", 17307081);
        RESID_DICTIONARY_PANEL_0_3.put("-5", 17307394);
        RESID_DICTIONARY_PANEL_0_3.put("-4", 17307469);
        RESID_DICTIONARY_PANEL_0_3.put("-3", 17307504);
        RESID_DICTIONARY_PANEL_0_3.put("-2", 17307404);
        RESID_DICTIONARY_PANEL_0_3.put("-1", 17307477);
        RESID_DICTIONARY_PANEL_0_3.put("0", 17306359);
        RESID_DICTIONARY_PANEL_0_3.put(DoubleExposureConstant.EXPOSURE_COMPENSATION_SKY_DEFAULT_VALUE, 17306392);
        RESID_DICTIONARY_PANEL_0_3.put("2", 17306462);
        RESID_DICTIONARY_PANEL_0_3.put("3", 17306495);
        RESID_DICTIONARY_PANEL_0_3.put("4", 17306529);
        RESID_DICTIONARY_PANEL_0_3.put(DoubleExposureConstant.EXPOSURE_COMPENSATION_TEXTURE_DEFAULT_VALUE_DSC, 17306471);
        RESID_DICTIONARY_PANEL_0_3.put("6", 17306507);
        RESID_DICTIONARY_PANEL_0_3.put(DoubleExposureConstant.EXPOSURE_COMPENSATION_TEXTURE_DEFAULT_VALUE_ILDC, 17306537);
        RESID_DICTIONARY_PANEL_0_3.put("8", 17306599);
        RESID_DICTIONARY_PANEL_0_3.put("9", 17306627);
        RESID_DICTIONARY_PANEL_0_5 = new HashMap<>();
        RESID_DICTIONARY_EVF_0_3 = new HashMap<>();
        RESID_DICTIONARY_EVF_0_3.put("-9", 17306534);
        RESID_DICTIONARY_EVF_0_3.put("-8", 17306737);
        RESID_DICTIONARY_EVF_0_3.put("-7", 17306551);
        RESID_DICTIONARY_EVF_0_3.put("-6", 17306487);
        RESID_DICTIONARY_EVF_0_3.put("-5", 17306698);
        RESID_DICTIONARY_EVF_0_3.put("-4", 17307111);
        RESID_DICTIONARY_EVF_0_3.put("-3", 17306785);
        RESID_DICTIONARY_EVF_0_3.put("-2", 17307470);
        RESID_DICTIONARY_EVF_0_3.put("-1", 17306631);
        RESID_DICTIONARY_EVF_0_3.put("0", 17306705);
        RESID_DICTIONARY_EVF_0_3.put(DoubleExposureConstant.EXPOSURE_COMPENSATION_SKY_DEFAULT_VALUE, 17306515);
        RESID_DICTIONARY_EVF_0_3.put("2", 17307432);
        RESID_DICTIONARY_EVF_0_3.put("3", 17307458);
        RESID_DICTIONARY_EVF_0_3.put("4", 17306772);
        RESID_DICTIONARY_EVF_0_3.put(DoubleExposureConstant.EXPOSURE_COMPENSATION_TEXTURE_DEFAULT_VALUE_DSC, 17306484);
        RESID_DICTIONARY_EVF_0_3.put("6", 17306676);
        RESID_DICTIONARY_EVF_0_3.put(DoubleExposureConstant.EXPOSURE_COMPENSATION_TEXTURE_DEFAULT_VALUE_ILDC, 17306373);
        RESID_DICTIONARY_EVF_0_3.put("8", 17306783);
        RESID_DICTIONARY_EVF_0_3.put("9", 17307482);
        RESID_DICTIONARY_EVF_0_5 = new HashMap<>();
    }

    public FlashLevelIcon(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.TAGS = new String[]{CameraNotificationManager.FLASH_CHANGE, CameraNotificationManager.DEVICE_EXTERNAL_FLASH_CHANGED, CameraNotificationManager.DEVICE_INTERNAL_FLASH_CHANGED, CameraNotificationManager.SCENE_MODE, CameraNotificationManager.REC_MODE_CHANGED};
        this.LOGTAG = TAG;
    }

    @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage
    protected boolean isVisible() {
        this.mVisible = !this.mFlashController.isUnavailableSceneFactor(FlashController.FLASH_COMPENSATION);
        return this.mVisible;
    }

    @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage
    protected NotificationListener getNotificationListener() {
        return new NotificationListener() { // from class: com.sony.imaging.app.base.shooting.widget.FlashLevelIcon.1
            @Override // com.sony.imaging.app.util.NotificationListener
            public void onNotify(String tag) {
                Log.i(FlashLevelIcon.TAG, FlashLevelIcon.this.builder.replace(0, FlashLevelIcon.LOG_ONNOTIFY.length(), FlashLevelIcon.LOG_ONNOTIFY).append(tag).toString());
                if (CameraNotificationManager.DEVICE_EXTERNAL_FLASH_CHANGED.equals(tag) || CameraNotificationManager.DEVICE_INTERNAL_FLASH_CHANGED.equals(tag) || CameraNotificationManager.REC_MODE_CHANGED.equals(tag)) {
                    FlashLevelIcon.this.updateFlashDeviceStatus(tag);
                }
                FlashLevelIcon.this.refresh();
            }

            @Override // com.sony.imaging.app.util.NotificationListener
            public String[] getTags() {
                return FlashLevelIcon.this.TAGS;
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.widget.FlashAbstractIcon, com.sony.imaging.app.base.shooting.widget.ActiveImage
    public void refresh() {
        isVisible();
        if ((this.mVisible && this.isEnable) || this.mFnMode) {
            String mode = this.mFlashController.getValue(FlashController.FLASHMODE);
            if ((!"off".equals(mode) && !"auto".equals(mode)) || this.mFnMode) {
                setVisibility(0);
                String value = this.mFlashController.getValue(FlashController.FLASH_COMPENSATION);
                Drawable flashLevelImg = getDrawableFromPool(value);
                if (flashLevelImg == null && this.mFnMode) {
                    flashLevelImg = getResources().getDrawable(17305411);
                }
                if (flashLevelImg != null) {
                    setImageDrawable(flashLevelImg);
                    return;
                }
                return;
            }
            setVisibility(4);
            return;
        }
        setVisibility(4);
    }

    private Drawable getDrawableFromPool(String mode) {
        Integer resId;
        int device = DisplayModeObserver.getInstance().getActiveDevice();
        float step = this.mFlashController.getmStep();
        HashMap<String, Integer> RESID_DICTIONARY = getHashMap(device, step);
        if (RESID_DICTIONARY == null || (resId = RESID_DICTIONARY.get(mode)) == null) {
            return null;
        }
        try {
            Drawable d = getResources().getDrawable(resId.intValue());
            return d;
        } catch (Resources.NotFoundException e) {
            Log.i(TAG, STRBUILD.replace(0, STRBUILD.length(), LOG_RESOURCE_NOT_FOUND).append(mode).toString());
            return null;
        }
    }

    protected HashMap<String, Integer> getHashMap(int device, float step) {
        if (device == 0 || device == 2) {
            if (step == 5.0f) {
                HashMap<String, Integer> hash = RESID_DICTIONARY_PANEL_0_5;
                return hash;
            }
            HashMap<String, Integer> hash2 = RESID_DICTIONARY_PANEL_0_3;
            return hash2;
        }
        if (device != 1) {
            return null;
        }
        if (step == 5.0f) {
            HashMap<String, Integer> hash3 = RESID_DICTIONARY_EVF_0_5;
            return hash3;
        }
        HashMap<String, Integer> hash4 = RESID_DICTIONARY_EVF_0_3;
        return hash4;
    }
}
