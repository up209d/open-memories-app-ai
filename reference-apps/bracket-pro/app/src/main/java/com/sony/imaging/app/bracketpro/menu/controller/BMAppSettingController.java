package com.sony.imaging.app.bracketpro.menu.controller;

import com.sony.imaging.app.base.menu.IController;
import com.sony.imaging.app.base.shooting.camera.AbstractController;
import com.sony.imaging.app.bracketpro.commonUtil.BracketMasterBackUpKey;
import com.sony.imaging.app.util.BackUpUtil;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class BMAppSettingController extends AbstractController {
    private static BMAppSettingController mInstance = null;
    private ArrayList<String> mSupportedList;

    public static BMAppSettingController getInstance() {
        if (mInstance == null) {
            mInstance = new BMAppSettingController();
        }
        return mInstance;
    }

    private BMAppSettingController() {
        this.mSupportedList = null;
        this.mSupportedList = new ArrayList<>();
        this.mSupportedList.add("ApplicationSettings");
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public void setValue(String tag, String value) throws IllegalArgumentException {
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public String getValue(String tag) throws IController.NotSupportedException {
        return null;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getSupportedValue(String tag) {
        return this.mSupportedList;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getAvailableValue(String tag) {
        return this.mSupportedList;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public boolean isAvailable(String tag) {
        String curGroup = BackUpUtil.getInstance().getPreferenceString(BracketMasterBackUpKey.TAG_CURRENT_BRACKET_TYPE, BMMenuController.FocusBracket);
        return !curGroup.equals(BMMenuController.FlashBracket);
    }
}
