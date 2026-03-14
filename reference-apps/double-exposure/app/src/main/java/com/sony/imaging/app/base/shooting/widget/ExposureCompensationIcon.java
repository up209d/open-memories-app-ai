package com.sony.imaging.app.base.shooting.widget;

import android.R;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import com.sony.imaging.app.base.shooting.camera.ExposureCompensationController;
import com.sony.imaging.app.base.shooting.camera.ISOSensitivityController;
import com.sony.imaging.app.base.shooting.widget.ActiveImage;
import com.sony.imaging.app.doubleexposure.common.DoubleExposureConstant;
import com.sony.imaging.app.util.NotificationListener;
import java.util.HashMap;

/* loaded from: classes.dex */
public class ExposureCompensationIcon extends ActiveImage {
    private static final HashMap<String, Integer> RESID_DICTIONARY_EV_COMP = new HashMap<>();
    private static final String TAG = "ExposureCompensationIcon";
    private ActiveImage.ActiveImageListener mListener;

    static {
        RESID_DICTIONARY_EV_COMP.put(ISOSensitivityController.ISO_50, Integer.valueOf(R.drawable.ic_doc_folder));
        RESID_DICTIONARY_EV_COMP.put("47", Integer.valueOf(R.drawable.progressbar_indeterminate_holo2));
        RESID_DICTIONARY_EV_COMP.put("45", Integer.valueOf(R.drawable.progressbar_indeterminate2));
        RESID_DICTIONARY_EV_COMP.put("43", Integer.valueOf(R.drawable.progress_small_material));
        RESID_DICTIONARY_EV_COMP.put("40", Integer.valueOf(R.drawable.ic_doc_document));
        RESID_DICTIONARY_EV_COMP.put("37", 17305308);
        RESID_DICTIONARY_EV_COMP.put("35", 17305307);
        RESID_DICTIONARY_EV_COMP.put("33", 17305306);
        RESID_DICTIONARY_EV_COMP.put("30", Integer.valueOf(R.drawable.ic_doc_codes));
        RESID_DICTIONARY_EV_COMP.put("27", Integer.valueOf(R.drawable.search_dropdown_dark));
        RESID_DICTIONARY_EV_COMP.put("25", Integer.valueOf(R.drawable.search_bar_default_color));
        RESID_DICTIONARY_EV_COMP.put("23", Integer.valueOf(R.drawable.scrubber_track_mtrl_alpha));
        RESID_DICTIONARY_EV_COMP.put("20", Integer.valueOf(R.drawable.ic_dialog_time));
        RESID_DICTIONARY_EV_COMP.put("17", 17305429);
        RESID_DICTIONARY_EV_COMP.put("15", 17305426);
        RESID_DICTIONARY_EV_COMP.put("13", 17305422);
        RESID_DICTIONARY_EV_COMP.put("10", Integer.valueOf(R.drawable.ic_dialog_close_pressed_holo));
        RESID_DICTIONARY_EV_COMP.put(DoubleExposureConstant.EXPOSURE_COMPENSATION_TEXTURE_DEFAULT_VALUE_ILDC, Integer.valueOf(R.drawable.stat_sys_upload_anim5));
        RESID_DICTIONARY_EV_COMP.put(DoubleExposureConstant.EXPOSURE_COMPENSATION_TEXTURE_DEFAULT_VALUE_DSC, Integer.valueOf(R.drawable.stat_sys_upload_anim4));
        RESID_DICTIONARY_EV_COMP.put("3", Integer.valueOf(R.drawable.stat_sys_upload_anim1));
        RESID_DICTIONARY_EV_COMP.put("0", Integer.valueOf(R.drawable.ic_dialog_alert_material));
        RESID_DICTIONARY_EV_COMP.put("-3", 17304102);
        RESID_DICTIONARY_EV_COMP.put("-5", 17304103);
        RESID_DICTIONARY_EV_COMP.put("-7", 17304104);
        RESID_DICTIONARY_EV_COMP.put("-10", 17305857);
        RESID_DICTIONARY_EV_COMP.put("-13", 17305869);
        RESID_DICTIONARY_EV_COMP.put("-15", 17305871);
        RESID_DICTIONARY_EV_COMP.put("-17", 17305872);
        RESID_DICTIONARY_EV_COMP.put("-20", 17305860);
        RESID_DICTIONARY_EV_COMP.put("-23", 17305677);
        RESID_DICTIONARY_EV_COMP.put("-25", 17305680);
        RESID_DICTIONARY_EV_COMP.put("-27", 17305681);
        RESID_DICTIONARY_EV_COMP.put("-30", 17305861);
        RESID_DICTIONARY_EV_COMP.put("-33", Integer.valueOf(R.drawable.tab_unselected_pressed_holo));
        RESID_DICTIONARY_EV_COMP.put("-35", Integer.valueOf(R.drawable.text_cursor_holo_light));
        RESID_DICTIONARY_EV_COMP.put("-37", Integer.valueOf(R.drawable.text_edit_side_paste_window));
        RESID_DICTIONARY_EV_COMP.put("-40", 17305862);
        RESID_DICTIONARY_EV_COMP.put("-43", 17305541);
        RESID_DICTIONARY_EV_COMP.put("-45", 17305546);
        RESID_DICTIONARY_EV_COMP.put("-47", 17305549);
        RESID_DICTIONARY_EV_COMP.put("-50", 17305865);
    }

    public ExposureCompensationIcon(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage
    protected boolean isVisible() {
        ExposureCompensationController controller = ExposureCompensationController.getInstance();
        boolean visible = controller.isExposureCompensationAvailable();
        return visible;
    }

    @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage
    protected NotificationListener getNotificationListener() {
        if (this.mListener == null) {
            this.mListener = new ActiveImage.ActiveImageListener() { // from class: com.sony.imaging.app.base.shooting.widget.ExposureCompensationIcon.1
                @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage.ActiveImageListener, com.sony.imaging.app.util.NotificationListener
                public void onNotify(String tag) {
                    if ("ExposureCompensation".equals(tag)) {
                        ExposureCompensationIcon.this.refresh();
                    } else {
                        super.onNotify(tag);
                    }
                }

                @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage.ActiveImageListener
                public String[] addTags() {
                    return new String[]{"ExposureCompensation"};
                }
            };
        }
        return this.mListener;
    }

    @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage
    protected void refresh() {
        Drawable exposureCompImg = null;
        ExposureCompensationController controller = ExposureCompensationController.getInstance();
        if (controller != null) {
            float value = controller.getExternalCompensation();
            Integer key = Integer.valueOf(((((int) value) / 1) * 10) + (((int) (10.0f * value)) % 10));
            Integer resId = RESID_DICTIONARY_EV_COMP.get(key.toString());
            if (resId != null) {
                exposureCompImg = getResources().getDrawable(resId.intValue());
            } else {
                Log.e(TAG, "Invalid value of ExpousureCompensation Value");
            }
            if (exposureCompImg != null) {
                setImageDrawable(exposureCompImg);
            }
        }
    }
}
