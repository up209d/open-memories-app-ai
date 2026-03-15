package com.sony.imaging.app.lightgraffiti.shooting.controller;

import android.util.Log;
import com.sony.imaging.app.base.shooting.camera.WhiteBalanceController;
import com.sony.imaging.app.lightgraffiti.shooting.LGStateHolder;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class LGWhiteBalanceController extends WhiteBalanceController {
    private static final String TAG = LGWhiteBalanceController.class.getSimpleName();
    private static LGWhiteBalanceController mInstance;

    public static LGWhiteBalanceController getInstance() {
        if (mInstance == null) {
            new LGWhiteBalanceController();
        }
        return mInstance;
    }

    private static void setController(LGWhiteBalanceController controller) {
        if (mInstance == null) {
            mInstance = controller;
        }
    }

    protected LGWhiteBalanceController() {
        setController(this);
    }

    @Override // com.sony.imaging.app.base.shooting.camera.WhiteBalanceController, com.sony.imaging.app.base.menu.IController
    public List<String> getAvailableValue(String tag) {
        if (tag == null) {
            tag = WhiteBalanceController.WHITEBALANCE;
        }
        new ArrayList();
        ArrayList<String> availables = (ArrayList) super.getAvailableValue(tag);
        if (LGStateHolder.getInstance().isShootingStage3rd()) {
            try {
                availables.remove(availables.indexOf(WhiteBalanceController.CUSTOM_SET));
            } catch (IndexOutOfBoundsException e) {
                Log.e(TAG, "getAvailableValue MenuItem delete fail." + e);
            }
        }
        if (availables.isEmpty()) {
            return null;
        }
        return availables;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.AbstractController, com.sony.imaging.app.base.menu.IController
    public int getCautionIndex(String itemId) {
        int ret = super.getCautionIndex(itemId);
        if (LGStateHolder.getInstance().isShootingStage3rd()) {
            return 1;
        }
        return ret;
    }
}
