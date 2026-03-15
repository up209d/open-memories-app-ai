package com.sony.imaging.app.lightgraffiti.shooting.controller;

import com.sony.imaging.app.base.menu.IController;
import com.sony.imaging.app.base.shooting.camera.AbstractController;
import com.sony.imaging.app.lightgraffiti.util.AppLog;
import com.sony.imaging.app.lightgraffiti.util.LGConstants;
import com.sony.imaging.app.lightgraffiti.util.LGUtility;
import com.sony.imaging.app.util.BackUpUtil;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class LGAppTopController extends AbstractController {
    private static LGAppTopController mInstance;
    private boolean isShootingScreenOpened = false;
    private BackUpUtil mBackUpUtil;
    private ArrayList<String> mParamList;
    private static String TAG = LGAppTopController.class.getSimpleName();
    public static String DURATION_SELECTION = "ApplicationTop";
    public static String DURATION_TIME_5 = "PaintingDuration5sec";
    public static String DURATION_TIME_10 = "PaintingDuration10sec";
    public static String DURATION_TIME_20 = "PaintingDuration20sec";
    public static String DURATION_TIME_30 = "PaintingDuration30sec";

    public static LGAppTopController getInstance() {
        if (mInstance == null) {
            new LGAppTopController();
        }
        return mInstance;
    }

    private static void setController(LGAppTopController controller) {
        if (mInstance == null) {
            mInstance = controller;
        }
    }

    protected LGAppTopController() {
        this.mBackUpUtil = null;
        setController(this);
        this.mBackUpUtil = BackUpUtil.getInstance();
        this.mParamList = new ArrayList<>();
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public void setValue(String tag, String value) throws IllegalArgumentException {
        AppLog.enter(TAG, "setValue tag: " + tag + ", value: " + value);
        if (tag.equals(DURATION_SELECTION)) {
            if (this.mBackUpUtil != null) {
                this.mBackUpUtil.setPreference(LGConstants.BACKUP_KEY_LAST_SELECTED_PAINTING_DURATION, value);
            }
            if (value.equals(DURATION_TIME_10)) {
                LGUtility.getInstance().setRecommanedSS(convertToShutterSpeedFromPaintDuration(DURATION_TIME_10));
                LGUtility.getInstance().outputKikilog(LGConstants.KIKILOG_ID_SELECTED_PAINT_TIME_10_SEC);
            } else if (value.equals(DURATION_TIME_20)) {
                LGUtility.getInstance().setRecommanedSS(convertToShutterSpeedFromPaintDuration(DURATION_TIME_20));
                LGUtility.getInstance().outputKikilog(LGConstants.KIKILOG_ID_SELECTED_PAINT_TIME_20_SEC);
            } else if (value.equals(DURATION_TIME_30)) {
                LGUtility.getInstance().setRecommanedSS(convertToShutterSpeedFromPaintDuration(DURATION_TIME_30));
                LGUtility.getInstance().outputKikilog(LGConstants.KIKILOG_ID_SELECTED_PAINT_TIME_30_SEC);
            } else {
                LGUtility.getInstance().setRecommanedSS(convertToShutterSpeedFromPaintDuration(DURATION_TIME_5));
                LGUtility.getInstance().outputKikilog(LGConstants.KIKILOG_ID_SELECTED_PAINT_TIME_5_SEC);
            }
        }
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public String getValue(String tag) throws IController.NotSupportedException {
        AppLog.enter(TAG, "getValue tag = " + tag);
        String value = getCurrentValue(tag);
        AppLog.exit(TAG, "getValue value = " + value);
        return value;
    }

    private String getCurrentValue(String tag) {
        AppLog.enter(TAG, AppLog.getMethodName() + " TAG ::" + tag);
        String retValue = null;
        if (DURATION_SELECTION.equals(tag)) {
            retValue = getLastSelectedDurationTime();
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return retValue;
    }

    public String getLastSelectedDurationTime() {
        if (this.mBackUpUtil == null) {
            return null;
        }
        String retDurationTime = this.mBackUpUtil.getPreferenceString(LGConstants.BACKUP_KEY_LAST_SELECTED_PAINTING_DURATION, DURATION_TIME_5);
        return retDurationTime;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getSupportedValue(String tag) {
        this.mParamList.clear();
        if (tag.equals(DURATION_SELECTION)) {
            this.mParamList.add(DURATION_TIME_5);
            this.mParamList.add(DURATION_TIME_10);
            this.mParamList.add(DURATION_TIME_20);
            this.mParamList.add(DURATION_TIME_30);
        }
        return this.mParamList;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getAvailableValue(String tag) {
        this.mParamList.clear();
        if (tag.equals(DURATION_SELECTION)) {
            this.mParamList.add(DURATION_TIME_5);
            this.mParamList.add(DURATION_TIME_10);
            this.mParamList.add(DURATION_TIME_20);
            this.mParamList.add(DURATION_TIME_30);
        }
        return this.mParamList;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public boolean isAvailable(String tag) {
        return true;
    }

    public boolean isShootingScreenOpened() {
        return this.isShootingScreenOpened;
    }

    public void setShootingScreenOpened(boolean isShootingScreenOpened) {
        this.isShootingScreenOpened = isShootingScreenOpened;
    }

    public static String convertToShutterSpeedFromPaintDuration(String value) {
        if (value.equals(DURATION_TIME_10)) {
            return LGConstants.DEFAULT_VALUE_SHUTTER_SPEED_10SEC;
        }
        if (value.equals(DURATION_TIME_20)) {
            return LGConstants.DEFAULT_VALUE_SHUTTER_SPEED_20SEC;
        }
        if (value.equals(DURATION_TIME_30)) {
            return LGConstants.DEFAULT_VALUE_SHUTTER_SPEED_30SEC;
        }
        return LGConstants.DEFAULT_VALUE_SHUTTER_SPEED_5SEC;
    }
}
