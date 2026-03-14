package com.sony.imaging.app.base.shooting.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import com.sony.imaging.app.base.menu.BaseMenuService;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.ISOAutoMinSSController;
import com.sony.imaging.app.util.NotificationListener;

/* loaded from: classes.dex */
public class ISOAutoMinSSIconFn extends ActiveImage {
    private static final String TAG = "ISOAutoMinSSIconFn";
    private String[] TAGS;
    private BaseMenuService mService;

    public ISOAutoMinSSIconFn(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.TAGS = new String[]{CameraNotificationManager.ISO_AUTO_MIN_SS_MODE, CameraNotificationManager.ISO_AUTO_MIN_SS_VALUE};
        this.mService = new BaseMenuService(context);
    }

    @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage
    protected boolean isVisible() {
        ISOAutoMinSSController controller = ISOAutoMinSSController.getInstance();
        boolean visible = controller.isAvailable(ISOAutoMinSSController.MENU_ITEM_ID_ISO_AUTO_MIN_SS);
        return visible;
    }

    @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage
    protected NotificationListener getNotificationListener() {
        return new NotificationListener() { // from class: com.sony.imaging.app.base.shooting.widget.ISOAutoMinSSIconFn.1
            @Override // com.sony.imaging.app.util.NotificationListener
            public void onNotify(String tag) {
                if (CameraNotificationManager.ISO_AUTO_MIN_SS_MODE.equals(tag) || CameraNotificationManager.ISO_AUTO_MIN_SS_VALUE.equals(tag)) {
                    ISOAutoMinSSIconFn.this.refresh();
                }
            }

            @Override // com.sony.imaging.app.util.NotificationListener
            public String[] getTags() {
                return ISOAutoMinSSIconFn.this.TAGS;
            }
        };
    }

    @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage
    protected void refresh() {
        String itemId;
        ISOAutoMinSSController controller = ISOAutoMinSSController.getInstance();
        if (controller != null) {
            String itemId2 = controller.getValue(ISOAutoMinSSController.MENU_ITEM_ID_ISO_AUTO_MIN_SS);
            if (ISOAutoMinSSController.MENU_ITEM_ID_ISO_AUTO_MIN_SS_MODE.equals(itemId2)) {
                itemId = "isoautominss-mode-" + controller.getValue(ISOAutoMinSSController.MENU_ITEM_ID_ISO_AUTO_MIN_SS_MODE);
            } else {
                itemId = "isoautominss-value-" + itemId2;
            }
            Drawable Img = this.mService.getMenuItemDrawable(itemId);
            if (Img != null) {
                setImageDrawable(Img);
            } else {
                Log.e(TAG, "Invalid value of ISOAutoMinSS Value");
            }
        }
    }
}
