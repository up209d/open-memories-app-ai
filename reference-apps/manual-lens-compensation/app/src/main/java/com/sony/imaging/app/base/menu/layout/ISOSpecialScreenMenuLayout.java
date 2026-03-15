package com.sony.imaging.app.base.menu.layout;

import com.sony.imaging.app.base.shooting.camera.ISOSensitivityController;
import java.util.ArrayList;

/* loaded from: classes.dex */
public class ISOSpecialScreenMenuLayout extends SpecialScreenMenuLayout {
    public static final String MENU_ID = "ID_ISOSPECIALSCREENMENULAYOUT";

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedSubDialNext() {
        int currentPos = this.mSpecialScreenView.getSelectedItemPosition();
        int count = this.mSpecialScreenView.getCount();
        if (count - 1 == currentPos) {
            this.mSpecialScreenView.moveNext();
            return 1;
        }
        int count2 = count - 1;
        ISOSensitivityController controller = ISOSensitivityController.getInstance();
        int nextPos = currentPos + 1;
        ArrayList<String> list = this.mService.getSupportedItemList();
        while (nextPos < count2) {
            String itemId = list.get(nextPos);
            String value = this.mService.getMenuItemValue(itemId);
            if (controller.isSupported1EVStepValueOrSpecial(value)) {
                break;
            }
            nextPos++;
        }
        this.mSpecialScreenView.moveTo(nextPos);
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedSubDialPrev() {
        int currentPos = this.mSpecialScreenView.getSelectedItemPosition();
        if (currentPos == 0) {
            this.mSpecialScreenView.movePrevious();
            return 1;
        }
        int nextPos = currentPos - 1;
        ISOSensitivityController controller = ISOSensitivityController.getInstance();
        ArrayList<String> list = this.mService.getSupportedItemList();
        while (nextPos > 0) {
            String itemId = list.get(nextPos);
            String value = this.mService.getMenuItemValue(itemId);
            if (controller.isSupported1EVStepValueOrSpecial(value)) {
                break;
            }
            nextPos--;
        }
        this.mSpecialScreenView.moveTo(nextPos);
        return 1;
    }
}
