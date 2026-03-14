package com.sony.imaging.app.base.shooting.widget;

import android.R;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.ISOSensitivityController;
import com.sony.imaging.app.util.NotificationListener;
import java.util.HashMap;

/* loaded from: classes.dex */
public class ISOIconFn extends ActiveImage {
    private static final HashMap<String, Integer> RESID_DICTIONARY_ISO = new HashMap<>();
    private static final String TAG = "ISOIconFn";
    private String[] TAGS;

    static {
        RESID_DICTIONARY_ISO.put(ISOSensitivityController.ISO_AUTO, Integer.valueOf(R.drawable.numberpicker_input));
        RESID_DICTIONARY_ISO.put(ISOSensitivityController.ISO_50Expanded, Integer.valueOf(R.drawable.numberpicker_input_selected));
        RESID_DICTIONARY_ISO.put(ISOSensitivityController.ISO_64Expanded, Integer.valueOf(R.drawable.numberpicker_selection_divider));
        RESID_DICTIONARY_ISO.put(ISOSensitivityController.ISO_80Expanded, Integer.valueOf(R.drawable.numberpicker_up_disabled));
        RESID_DICTIONARY_ISO.put(ISOSensitivityController.ISO_100Expanded, 17306259);
        RESID_DICTIONARY_ISO.put(ISOSensitivityController.ISO_64, 17305616);
        RESID_DICTIONARY_ISO.put(ISOSensitivityController.ISO_80, 17305638);
        RESID_DICTIONARY_ISO.put(ISOSensitivityController.ISO_100, Integer.valueOf(R.drawable.numberpicker_up_disabled_focused_holo_dark));
        RESID_DICTIONARY_ISO.put(ISOSensitivityController.ISO_125, Integer.valueOf(R.drawable.numberpicker_up_disabled_focused_holo_light));
        RESID_DICTIONARY_ISO.put(ISOSensitivityController.ISO_160, Integer.valueOf(R.drawable.numberpicker_up_focused_holo_light));
        RESID_DICTIONARY_ISO.put(ISOSensitivityController.ISO_200, Integer.valueOf(R.drawable.numberpicker_up_normal_holo_dark));
        RESID_DICTIONARY_ISO.put(ISOSensitivityController.ISO_250, Integer.valueOf(R.drawable.panel_bg_holo_light));
        RESID_DICTIONARY_ISO.put(ISOSensitivityController.ISO_320, 17305109);
        RESID_DICTIONARY_ISO.put(ISOSensitivityController.ISO_400, 17305110);
        RESID_DICTIONARY_ISO.put(ISOSensitivityController.ISO_500, 17305111);
        RESID_DICTIONARY_ISO.put(ISOSensitivityController.ISO_640, 17305112);
        RESID_DICTIONARY_ISO.put(ISOSensitivityController.ISO_800, 17305116);
        RESID_DICTIONARY_ISO.put(ISOSensitivityController.ISO_1000, 17305122);
        RESID_DICTIONARY_ISO.put(ISOSensitivityController.ISO_1250, 17305125);
        RESID_DICTIONARY_ISO.put(ISOSensitivityController.ISO_1600, 17305127);
        RESID_DICTIONARY_ISO.put(ISOSensitivityController.ISO_2000, 17305130);
        RESID_DICTIONARY_ISO.put(ISOSensitivityController.ISO_2500, 17305134);
        RESID_DICTIONARY_ISO.put(ISOSensitivityController.ISO_3200, Integer.valueOf(R.drawable.pointer_context_menu_large));
        RESID_DICTIONARY_ISO.put(ISOSensitivityController.ISO_4000, Integer.valueOf(R.drawable.pointer_copy_large_icon));
        RESID_DICTIONARY_ISO.put(ISOSensitivityController.ISO_5000, Integer.valueOf(R.drawable.pointer_crosshair_large_icon));
        RESID_DICTIONARY_ISO.put(ISOSensitivityController.ISO_6400, Integer.valueOf(R.drawable.pointer_grab_large_icon));
        RESID_DICTIONARY_ISO.put(ISOSensitivityController.ISO_8000, Integer.valueOf(R.drawable.pointer_hand));
        RESID_DICTIONARY_ISO.put(ISOSensitivityController.ISO_10000, Integer.valueOf(R.drawable.pointer_hand_large_icon));
        RESID_DICTIONARY_ISO.put(ISOSensitivityController.ISO_12800, Integer.valueOf(R.drawable.pointer_help_large));
        RESID_DICTIONARY_ISO.put(ISOSensitivityController.ISO_16000, Integer.valueOf(R.drawable.pointer_help_large_icon));
        RESID_DICTIONARY_ISO.put(ISOSensitivityController.ISO_20000, Integer.valueOf(R.drawable.pointer_horizontal_double_arrow));
        RESID_DICTIONARY_ISO.put(ISOSensitivityController.ISO_25600, Integer.valueOf(R.drawable.pointer_horizontal_double_arrow_icon));
        RESID_DICTIONARY_ISO.put("32000", 17306083);
        RESID_DICTIONARY_ISO.put("40000", 17306084);
        RESID_DICTIONARY_ISO.put("51200", Integer.valueOf(R.drawable.ic_bt_pointing_hid));
    }

    public ISOIconFn(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.TAGS = new String[]{CameraNotificationManager.ISO_SENSITIVITY};
    }

    @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage
    protected boolean isVisible() {
        ISOSensitivityController controller = ISOSensitivityController.getInstance();
        boolean visible = controller.isAvailable(ISOSensitivityController.MENU_ITEM_ID_ISO);
        return visible;
    }

    @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage
    protected NotificationListener getNotificationListener() {
        return new NotificationListener() { // from class: com.sony.imaging.app.base.shooting.widget.ISOIconFn.1
            @Override // com.sony.imaging.app.util.NotificationListener
            public void onNotify(String tag) {
                if (CameraNotificationManager.ISO_SENSITIVITY.equals(tag)) {
                    ISOIconFn.this.refresh();
                }
            }

            @Override // com.sony.imaging.app.util.NotificationListener
            public String[] getTags() {
                return ISOIconFn.this.TAGS;
            }
        };
    }

    @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage
    protected void refresh() {
        Drawable isoImg = null;
        ISOSensitivityController controller = ISOSensitivityController.getInstance();
        if (controller != null) {
            String value = controller.getValue(ISOSensitivityController.MENU_ITEM_ID_ISO);
            Integer resId = RESID_DICTIONARY_ISO.get(value);
            if (resId != null) {
                isoImg = getResources().getDrawable(resId.intValue());
            } else {
                Log.e(TAG, "Invalid value of ISO Value");
            }
            if (isoImg != null) {
                setImageDrawable(isoImg);
            }
        }
    }
}
