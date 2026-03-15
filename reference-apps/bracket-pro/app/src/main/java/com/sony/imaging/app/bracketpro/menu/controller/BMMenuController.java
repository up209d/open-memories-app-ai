package com.sony.imaging.app.bracketpro.menu.controller;

import com.sony.imaging.app.base.menu.IController;
import com.sony.imaging.app.base.shooting.camera.AbstractController;
import com.sony.imaging.app.bracketpro.AppLog;
import com.sony.imaging.app.bracketpro.commonUtil.BracketMasterBackUpKey;
import com.sony.imaging.app.util.BackUpUtil;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class BMMenuController extends AbstractController {
    private static BMMenuController mInstance = null;
    private static ArrayList<String> mBracketProList = null;
    public static String BRACKETPRO = "ApplicationTop";
    public static String FocusBracket = "group1";
    public static String ApertureBracket = "group2";
    public static String ShutterSpeedBracket = "group3";
    public static String FlashBracket = "group4";
    private String TAG = AppLog.getClassName();
    private ArrayList<String> mSupportedgList = null;
    private boolean showFlashCaution = false;
    public String mSelectedGroup = FocusBracket;
    private boolean isShootingScreenOpened = false;
    private boolean isRangeSet = true;

    public static BMMenuController getInstance() {
        if (mInstance == null) {
            mInstance = new BMMenuController();
        }
        return mInstance;
    }

    private BMMenuController() {
        mBracketProList = new ArrayList<>();
        mBracketProList.add(BRACKETPRO);
        mBracketProList.add(FocusBracket);
        mBracketProList.add(ApertureBracket);
        mBracketProList.add(ShutterSpeedBracket);
        mBracketProList.add(FlashBracket);
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public void setValue(String tag, String value) throws IllegalArgumentException {
        AppLog.enter(this.TAG, AppLog.getMethodName() + "TAG ::" + tag + " Value ::" + value);
        this.mSelectedGroup = value;
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public String getValue(String tag) throws IController.NotSupportedException {
        this.mSelectedGroup = getSelectedBracket();
        return this.mSelectedGroup;
    }

    public String getSelectedBracket() {
        String currentBracket = BackUpUtil.getInstance().getPreferenceString(BracketMasterBackUpKey.TAG_CURRENT_BRACKET_TYPE, FocusBracket);
        return currentBracket;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getSupportedValue(String tag) {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        AppLog.info(this.TAG, AppLog.getMethodName() + "TAG :::" + tag);
        this.mSupportedgList = mBracketProList;
        AppLog.exit(this.TAG, AppLog.getMethodName());
        return this.mSupportedgList;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getAvailableValue(String tag) {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        AppLog.info(this.TAG, AppLog.getMethodName() + "TAG :::" + tag);
        this.mSupportedgList = mBracketProList;
        AppLog.exit(this.TAG, AppLog.getMethodName());
        return this.mSupportedgList;
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

    public void setRangeStatus(boolean setRangeStatus) {
        this.isRangeSet = setRangeStatus;
    }

    public boolean isRangeSet() {
        return this.isRangeSet;
    }

    public boolean isShowFlashCaution() {
        return this.showFlashCaution;
    }

    public void setShowFlashCaution(boolean showFlashCaution) {
        this.showFlashCaution = showFlashCaution;
    }
}
