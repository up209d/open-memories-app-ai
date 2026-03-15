package com.sony.imaging.app.manuallenscompensation.menu.layout;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.caution.IkeyDispatchEach;
import com.sony.imaging.app.base.menu.layout.BaseMenuLayout;
import com.sony.imaging.app.base.shooting.trigger.ModeDialDetector;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.manuallenscompensation.caution.layout.OCInfo;
import com.sony.imaging.app.util.BeepUtility;
import com.sony.imaging.app.util.BeepUtilityRsrcTable;

/* loaded from: classes.dex */
public class LastBastionMenuLayout extends BaseMenuLayout {
    protected static final String TAG = "LastBastionMenuLayout";

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return new View(getActivity());
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        displayCaution();
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        CautionUtilityClass.getInstance().disapperTrigger(OCInfo.CAUTION_ID_DLAPP_PASMSCN_CLOSE_KEY);
        CautionUtilityClass.getInstance().setDispatchKeyEvent(OCInfo.CAUTION_ID_DLAPP_PASMSCN_CLOSE_KEY, null);
        super.onPause();
    }

    protected void displayCaution() {
        CautionUtilityClass.getInstance().setDispatchKeyEvent(OCInfo.CAUTION_ID_DLAPP_PASMSCN_CLOSE_KEY, new IkeyDispatchEach(null, 0 == true ? 1 : 0) { // from class: com.sony.imaging.app.manuallenscompensation.menu.layout.LastBastionMenuLayout.1
            @Override // com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IKeyHandler
            public int onKeyDown(int keyCode, KeyEvent event) {
                switch (event.getScanCode()) {
                    case AppRoot.USER_KEYCODE.PLAYBACK /* 207 */:
                        CautionUtilityClass.getInstance().executeTerminate();
                        return 0;
                    case AppRoot.USER_KEYCODE.SK1 /* 229 */:
                    case AppRoot.USER_KEYCODE.MENU /* 514 */:
                        LastBastionMenuLayout.this.getActivity().finish();
                        BeepUtility.getInstance().playBeep(BeepUtilityRsrcTable.BEEP_ID_SELECT);
                        return 1;
                    case AppRoot.USER_KEYCODE.MOVIE_REC /* 515 */:
                        return 0;
                    case AppRoot.USER_KEYCODE.MODE_DIAL_CHANGED /* 572 */:
                        if (ModeDialDetector.getModeDialPosition() != -1) {
                            CautionUtilityClass.getInstance().executeTerminate();
                            turnedModeDial();
                        }
                        return 0;
                    default:
                        return 1;
                }
            }
        });
        CautionUtilityClass.getInstance().requestTrigger(OCInfo.CAUTION_ID_DLAPP_PASMSCN_CLOSE_KEY);
    }
}
