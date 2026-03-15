package com.sony.imaging.app.base.menu.layout;

import com.sony.imaging.app.base.shooting.camera.ISOSensitivityController;
import java.util.List;

/* loaded from: classes.dex */
public class ISOFn15LayerMenuLayout extends Fn15LayerMenuLayout {
    public static final String MENU_ID = "ID_ISOFN15LAYERMENULAYOUT";

    @Override // com.sony.imaging.app.base.menu.layout.Fn15LayerMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedSubDialNext() {
        int currentPos = this.mMainBeltView.getSelectedItemPosition();
        int count = this.mMainBeltView.getCount();
        if (count - 1 == currentPos) {
            this.mMainBeltView.setSelection(0, true);
        } else {
            int count2 = count - 1;
            ISOSensitivityController controller = ISOSensitivityController.getInstance();
            int nextPos = currentPos + 1;
            List<String> list = this.mService.getSupportedItemList();
            while (nextPos < count2) {
                String itemId = list.get(nextPos);
                String value = this.mService.getMenuItemValue(itemId);
                if (controller.isSupported1EVStepValueOrSpecial(value)) {
                    break;
                }
                nextPos++;
            }
            this.mMainBeltView.setSelection(nextPos, true);
        }
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.Fn15LayerMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedSubDialPrev() {
        int currentPos = this.mMainBeltView.getSelectedItemPosition();
        int count = this.mMainBeltView.getCount();
        if (currentPos == 0) {
            this.mMainBeltView.setSelection(count - 1, true);
        } else {
            int nextPos = currentPos - 1;
            ISOSensitivityController controller = ISOSensitivityController.getInstance();
            List<String> list = this.mService.getSupportedItemList();
            while (nextPos > 0) {
                String itemId = list.get(nextPos);
                String value = this.mService.getMenuItemValue(itemId);
                if (controller.isSupported1EVStepValueOrSpecial(value)) {
                    break;
                }
                nextPos--;
            }
            this.mMainBeltView.setSelection(nextPos, true);
        }
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.Fn15LayerMenuLayout
    public boolean isKeyHandled(int key) {
        return true;
    }
}
