package com.sony.imaging.app.lightgraffiti.shooting.layout;

import android.os.Bundle;
import android.util.Log;
import com.sony.imaging.app.base.BaseApp;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.caution.Info;
import com.sony.imaging.app.base.menu.MenuTable;
import com.sony.imaging.app.base.menu.layout.PageMenuLayout;
import com.sony.imaging.app.base.shooting.trigger.ModeDialDetector;
import com.sony.imaging.app.fw.CustomizableFunction;
import com.sony.imaging.app.fw.IKeyFunction;
import com.sony.imaging.app.lightgraffiti.shooting.LGStateHolder;
import com.sony.imaging.app.lightgraffiti.shooting.layout.LGDiscardLightTrailDialogueLayout;
import com.sony.imaging.app.lightgraffiti.util.LGConstants;
import com.sony.imaging.app.util.PTag;

/* loaded from: classes.dex */
public class LGPageMenuLayout extends PageMenuLayout implements LGDiscardLightTrailDialogueLayout.Callback {
    private static final String TAG = PageMenuLayout.class.getSimpleName();
    private String tempItemid = "";

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout
    protected String getMenuLayoutID() {
        return PageMenuLayout.MENU_ID;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout
    public void doItemClickProcessing(String itemid) {
        Log.d(TAG, "doItemClickProcessing() enter with itemid " + itemid);
        if (this.mUpdater != null) {
            this.mUpdater.cancelMenuUpdater();
        }
        if (itemid.equals("ExitApplication")) {
            BaseApp baseApp = (BaseApp) getActivity();
            baseApp.finish();
            return;
        }
        if (this.mService != null) {
            if (!this.mService.isMenuItemValid(itemid)) {
                requestCautionTrigger(itemid);
                return;
            }
            String execType = this.mService.getMenuItemExecType(itemid);
            String nextFragmentID = this.mService.getMenuItemNextMenuID(itemid);
            if (MenuTable.NEXT_LAYOUT.equals(execType) || MenuTable.NEXT_LAYOUT_WITHOUT_SET.equals(execType)) {
                if (MenuTable.NEXT_LAYOUT.equals(execType)) {
                    this.mService.execCurrentMenuItem(itemid, false);
                }
                if (nextFragmentID != null) {
                    if (itemid.equals("ApplicationTop")) {
                        String LGexecType = this.mService.getMenuItemExecType(itemid);
                        String LGnextFragmentID = this.mService.getMenuItemNextMenuID(itemid);
                        if (LGStateHolder.getInstance().isShootingStage3rd()) {
                            LGDiscardLightTrailDialogueLayout layout = (LGDiscardLightTrailDialogueLayout) getLayout("ID_LGLAYOUT_DISCARD_DIALOGUE");
                            layout.setCallback(this);
                            Bundle discardData = new Bundle();
                            discardData.putString(LGConstants.TRANSIT_FROM_WHERE, LGConstants.TRANSIT_FROM_3RD_BY_APPTOP);
                            this.tempItemid = itemid;
                            openLayout("ID_LGLAYOUT_DISCARD_DIALOGUE", discardData);
                            return;
                        }
                        Log.d(TAG, "execType = " + LGexecType + ", nextFragmentID(nextState) = " + LGnextFragmentID);
                        openNextMenu(itemid, nextFragmentID);
                        return;
                    }
                    PTag.start("Menu open next MenuLayout");
                    openNextMenu(itemid, nextFragmentID);
                    return;
                }
                Log.e(TAG, "Can't open the next MenuLayout");
                return;
            }
            if (MenuTable.NEXT_STATE.equals(execType)) {
                PTag.start("MenuItem Clicked(NEXT_STATE)");
                openNextState(nextFragmentID, null);
            } else if (MenuTable.SET_VALUE.equals(execType) || MenuTable.SET_VALUE_ONLY_CLICK.equals(execType)) {
                PTag.start("MenuItem Clicked(SET_VALUE or SET_VALUE_ONLY_CLICK)");
                this.mService.execCurrentMenuItem(itemid, false);
                postSetValue();
            }
        }
    }

    @Override // com.sony.imaging.app.lightgraffiti.shooting.layout.LGDiscardLightTrailDialogueLayout.Callback
    public void onClose() {
        LGDiscardLightTrailDialogueLayout layout = (LGDiscardLightTrailDialogueLayout) getLayout("ID_LGLAYOUT_DISCARD_DIALOGUE");
        layout.closeLayout();
        layout.setCallback(null);
    }

    @Override // com.sony.imaging.app.lightgraffiti.shooting.layout.LGDiscardLightTrailDialogueLayout.Callback
    public void onFinish() {
        LGDiscardLightTrailDialogueLayout layout = (LGDiscardLightTrailDialogueLayout) getLayout("ID_LGLAYOUT_DISCARD_DIALOGUE");
        layout.closeLayout();
        layout.setCallback(null);
        String execType = this.mService.getMenuItemExecType(this.tempItemid);
        String nextFragmentID = this.mService.getMenuItemNextMenuID(this.tempItemid);
        Log.d(TAG, "execType = " + execType + ", nextFragmentID(nextState) = " + nextFragmentID);
        openNextMenu(this.tempItemid, nextFragmentID);
        this.tempItemid = null;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedSettingFuncCustomKey(IKeyFunction keyFunction) {
        if (CustomizableFunction.DigitalZoom.equals(keyFunction)) {
            CautionUtilityClass.getInstance().requestTrigger(Info.CAUTION_ID_DLAPP_INVALID_FUNCTION);
            return -1;
        }
        if (!ModeDialDetector.hasModeDial() && true == CustomizableFunction.ExposureMode.equals(keyFunction)) {
            CautionUtilityClass.getInstance().requestTrigger(Info.CAUTION_ID_DLAPP_INVALID_FUNCTION);
            return -1;
        }
        return super.pushedSettingFuncCustomKey(keyFunction);
    }
}
