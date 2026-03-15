package com.sony.imaging.app.srctrl.shooting.keyhandler;

import android.util.Log;
import android.view.KeyEvent;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.shooting.trigger.S1OnEEStateKeyHandler;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.srctrl.caution.InfoEx;
import com.sony.imaging.app.srctrl.util.SRCtrlEnvironment;
import com.sony.imaging.app.srctrl.util.cameraoperation.CameraOperationTouchAFPosition;
import com.sony.scalar.webapi.service.camera.v1_0.common.struct.TouchAFCurrentPositionParams;

/* loaded from: classes.dex */
public class S1OnEEStateKeyHandlerForTouchAF extends S1OnEEStateKeyHandler {
    private static final String tag = S1OnEEStateKeyHandlerForTouchAF.class.getName();

    @Override // com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyUp(int keyCode, KeyEvent event) {
        int code = event.getScanCode();
        switch (code) {
            case AppRoot.USER_KEYCODE.S1_ON /* 516 */:
            case AppRoot.USER_KEYCODE.LENS_ATTACH /* 530 */:
                CameraOperationTouchAFPosition.set(null, null, null);
                return 1;
            default:
                return -1;
        }
    }

    @Override // com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyDown(int keyCode, KeyEvent event) {
        int code = event.getScanCode();
        switch (code) {
            case AppRoot.USER_KEYCODE.S1_ON /* 516 */:
            case AppRoot.USER_KEYCODE.LENS_ATTACH /* 530 */:
                return -1;
            case 517:
            case AppRoot.USER_KEYCODE.S2_ON /* 518 */:
            case AppRoot.USER_KEYCODE.S2_OFF /* 774 */:
                return -1;
            case AppRoot.USER_KEYCODE.FOCUS_MODE_DIAL_CHANGED /* 597 */:
                break;
            case AppRoot.USER_KEYCODE.ZOOM_LEVER_TELE /* 610 */:
            case AppRoot.USER_KEYCODE.ZOOM_LEVER_WIDE /* 611 */:
            case AppRoot.USER_KEYCODE.UM_ZOOM_TELE /* 618 */:
            case AppRoot.USER_KEYCODE.UM_ZOOM_WIDE /* 619 */:
                int ret = super.onKeyDown(keyCode, event);
                return ret;
            case AppRoot.USER_KEYCODE.RING_STATUS /* 647 */:
            case AppRoot.USER_KEYCODE.LENS_APERTURE_RING_STATUS /* 650 */:
            case AppRoot.USER_KEYCODE.LENS_PARTS_OPERATION /* 655 */:
                int ret2 = super.onKeyDown(keyCode, event);
                return ret2;
            case AppRoot.USER_KEYCODE.RING_CLOCKWISE /* 648 */:
            case AppRoot.USER_KEYCODE.RING_COUNTERCW /* 649 */:
            case AppRoot.USER_KEYCODE.LENS_APERTURE_RING_RIGHT /* 651 */:
            case AppRoot.USER_KEYCODE.LENS_APERTURE_RING_LEFT /* 652 */:
                int ret3 = super.onKeyDown(keyCode, event);
                return ret3;
            case AppRoot.USER_KEYCODE.LENS_PARTS_STATE /* 654 */:
                Log.v(tag, "USER_KEYCODE.LENS_PARTS_STATE");
                break;
            default:
                if (!SRCtrlEnvironment.getInstance().getHandringKeys().contains(Integer.valueOf(code))) {
                    return -1;
                }
                Log.v(tag, "display caution. keyCode=" + code);
                CautionUtilityClass.getInstance().requestTrigger(InfoEx.CAUTION_ID_SMART_REMOTE_RSRC_ID_STRID_FUNC_SRPLUS_IN_TOUCH_AF_MODE);
                return -1;
        }
        TouchAFCurrentPositionParams param = CameraOperationTouchAFPosition.get();
        if (param.set.booleanValue()) {
            CameraOperationTouchAFPosition.leaveTouchAFMode(true);
        }
        int ret4 = super.onKeyDown(keyCode, event);
        return ret4;
    }
}
