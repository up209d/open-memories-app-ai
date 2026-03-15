package com.sony.imaging.app.bracketpro.menu.layout;

import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.menu.BaseMenuService;
import com.sony.imaging.app.base.menu.IController;
import com.sony.imaging.app.base.menu.layout.PageMenuLayout;
import com.sony.imaging.app.base.shooting.widget.AppNameConstantView;
import com.sony.imaging.app.base.shooting.widget.AppNameView;
import com.sony.imaging.app.bracketpro.AppLog;
import com.sony.imaging.app.bracketpro.R;
import com.sony.imaging.app.bracketpro.caution.CautionInfo;
import com.sony.imaging.app.bracketpro.commonUtil.BracketMasterBackUpKey;
import com.sony.imaging.app.bracketpro.menu.controller.BMAppSettingController;
import com.sony.imaging.app.bracketpro.menu.controller.BMMenuController;
import com.sony.imaging.app.util.BackUpUtil;

/* loaded from: classes.dex */
public class BMPageMenuLayout extends PageMenuLayout {
    private static final String TAG = AppLog.getClassName();
    private BMMenuController mController = BMMenuController.getInstance();

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout
    public String getMenuLayoutID() {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.exit(TAG, AppLog.getMethodName());
        return PageMenuLayout.MENU_ID;
    }

    @Override // com.sony.imaging.app.base.menu.layout.PageMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        AppNameConstantView.setText(getResources().getString(R.string.STRID_FUNC_BRKMASTER));
        AppNameView.show(true);
        super.onResume();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout
    public void doItemClickProcessing(String itemid) {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (itemid.equalsIgnoreCase("ApplicationSettings")) {
            String curGroup = BackUpUtil.getInstance().getPreferenceString(BracketMasterBackUpKey.TAG_CURRENT_BRACKET_TYPE, BMMenuController.FocusBracket);
            if (curGroup.equals(BMMenuController.FocusBracket)) {
                itemid = BMMenuController.FocusBracket;
            } else if (curGroup.equals(BMMenuController.ApertureBracket)) {
                itemid = BMMenuController.ApertureBracket;
            } else if (curGroup.equals(BMMenuController.ShutterSpeedBracket)) {
                itemid = BMMenuController.ShutterSpeedBracket;
            } else if (curGroup.equals(BMMenuController.FlashBracket)) {
                itemid = BMMenuController.FlashBracket;
            }
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        super.doItemClickProcessing(itemid);
    }

    private void displayCaution() {
        CautionUtilityClass.getInstance().setDispatchKeyEvent(CautionInfo.CAUTION_INVALID_FUNCTION, null);
        CautionUtilityClass.getInstance().requestTrigger(CautionInfo.CAUTION_INVALID_FUNCTION);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout
    public void requestCautionTrigger(String itemId) {
        Integer intId;
        if (!BMAppSettingController.getInstance().isAvailable("") && itemId.equalsIgnoreCase("ApplicationSettings")) {
            displayCaution();
            return;
        }
        int[] cautionId = this.mService.getMenuItemCautionId(itemId);
        if (cautionId != null) {
            IController controller = this.mService.getExecClassInstance(itemId);
            int index = controller.getCautionIndex(itemId);
            if (index < 0) {
                if (BaseMenuService.CAUTION_ID_DICTIONARY.containsKey(Integer.valueOf(index)) && (intId = BaseMenuService.CAUTION_ID_DICTIONARY.get(Integer.valueOf(index))) != null) {
                    CautionUtilityClass.getInstance().requestTrigger(intId.intValue());
                    return;
                }
                return;
            }
            if (index >= 0 && index < cautionId.length) {
                CautionUtilityClass.getInstance().requestTrigger(cautionId[index]);
            }
        }
    }
}
