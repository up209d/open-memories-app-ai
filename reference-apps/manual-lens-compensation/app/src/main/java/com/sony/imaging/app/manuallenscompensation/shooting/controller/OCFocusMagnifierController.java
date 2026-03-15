package com.sony.imaging.app.manuallenscompensation.shooting.controller;

import com.sony.imaging.app.base.menu.IController;
import com.sony.imaging.app.base.shooting.camera.AbstractController;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class OCFocusMagnifierController extends AbstractController {
    private static OCFocusMagnifierController mInstance;
    private ArrayList<String> mList;
    private String mSelectedValue = null;

    public static OCFocusMagnifierController getInstance() {
        if (mInstance != null) {
            mInstance = new OCFocusMagnifierController();
        }
        return mInstance;
    }

    private static void setController(OCFocusMagnifierController controller) {
        if (mInstance == null) {
            mInstance = controller;
        }
    }

    protected OCFocusMagnifierController() {
        setController(this);
        this.mList = new ArrayList<>();
        this.mList.add(DeleteFocusMagnifierController.FOCUS_MAGNIFIER_SWITCH_IC);
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public void setValue(String tag, String value) throws IllegalArgumentException {
        this.mSelectedValue = value;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public String getValue(String tag) throws IController.NotSupportedException {
        return this.mSelectedValue;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getSupportedValue(String tag) {
        return this.mList;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getAvailableValue(String tag) {
        return this.mList;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public boolean isAvailable(String tag) {
        return true;
    }
}
