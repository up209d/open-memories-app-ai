package com.sony.imaging.app.base.shooting.widget;

import android.R;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import com.sony.imaging.app.base.common.DisplayModeObserver;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.PictureEffectController;
import com.sony.imaging.app.base.shooting.widget.ActiveImage;
import com.sony.imaging.app.util.NotificationListener;
import java.util.HashMap;

/* loaded from: classes.dex */
public class PictureEffectIcon extends ActiveImage {
    private static final String LOG_RESOURCE_NOT_FOUND = "Resource is not found. key = ";
    private static final HashMap<String, Integer> RESID_DICTIONARY_EVF;
    private static final String TAG = "PictureEffectIcon";
    private final HashMap<String, Integer> RESID_DICTIONARY;
    protected final DisplayModeObserver mDisplayObserver;
    private ActiveImage.ActiveImageListener mListener;
    private static StringBuilder STRBUILD = new StringBuilder();
    private static final HashMap<String, Integer> RESID_DICTIONARY_DEFAULT = new HashMap<>();

    static {
        RESID_DICTIONARY_DEFAULT.put("off", 17303850);
        RESID_DICTIONARY_DEFAULT.put("toy-cameranormal", 17305663);
        RESID_DICTIONARY_DEFAULT.put("toy-cameracool", 17305665);
        RESID_DICTIONARY_DEFAULT.put("toy-camerawarm", 17305667);
        RESID_DICTIONARY_DEFAULT.put("toy-cameragreen", 17305668);
        RESID_DICTIONARY_DEFAULT.put("toy-cameramagenta", 17305669);
        RESID_DICTIONARY_DEFAULT.put(PictureEffectController.MODE_POP_COLOR, 17305670);
        RESID_DICTIONARY_DEFAULT.put("posterizationposterization-mono", 17305671);
        RESID_DICTIONARY_DEFAULT.put("posterizationposterization-color", 17305672);
        RESID_DICTIONARY_DEFAULT.put(PictureEffectController.MODE_RETRO_PHOTO, 17305674);
        RESID_DICTIONARY_DEFAULT.put(PictureEffectController.MODE_SOFT_HIGH_KEY, Integer.valueOf(R.drawable.title_bar_medium));
        RESID_DICTIONARY_DEFAULT.put("part-colorred", Integer.valueOf(R.drawable.title_bar_portrait));
        RESID_DICTIONARY_DEFAULT.put("part-colorgreen", Integer.valueOf(R.drawable.title_bar_shadow));
        RESID_DICTIONARY_DEFAULT.put("part-colorblue", Integer.valueOf(R.drawable.tooltip_frame));
        RESID_DICTIONARY_DEFAULT.put("part-coloryellow", Integer.valueOf(R.drawable.unknown_image));
        RESID_DICTIONARY_DEFAULT.put(PictureEffectController.MODE_ROUGH_MONO, 17303851);
        RESID_DICTIONARY_DEFAULT.put("soft-focussoft-focus-3", 17305692);
        RESID_DICTIONARY_DEFAULT.put("soft-focussoft-focus-2", 17305691);
        RESID_DICTIONARY_DEFAULT.put("soft-focussoft-focus-1", Integer.valueOf(R.drawable.vector_drawable_progress_indeterminate_horizontal));
        RESID_DICTIONARY_DEFAULT.put("hdr-arthdr-art-3", 17305696);
        RESID_DICTIONARY_DEFAULT.put("hdr-arthdr-art-2", 17305694);
        RESID_DICTIONARY_DEFAULT.put("hdr-arthdr-art-1", 17305693);
        RESID_DICTIONARY_DEFAULT.put(PictureEffectController.MODE_RICH_TONE_MONOCHROME, 17305698);
        RESID_DICTIONARY_DEFAULT.put("miniatureauto", 17305702);
        RESID_DICTIONARY_DEFAULT.put("miniatureupper", 17305705);
        RESID_DICTIONARY_DEFAULT.put("miniaturehcenter", 17303837);
        RESID_DICTIONARY_DEFAULT.put("miniaturelower", 17303840);
        RESID_DICTIONARY_DEFAULT.put("miniatureright", 17303849);
        RESID_DICTIONARY_DEFAULT.put("miniaturevcenter", 17303845);
        RESID_DICTIONARY_DEFAULT.put("miniatureleft", 17303842);
        RESID_DICTIONARY_DEFAULT.put(PictureEffectController.WATERCOLOR, 17305608);
        RESID_DICTIONARY_DEFAULT.put("illustillustration-3", 17304197);
        RESID_DICTIONARY_DEFAULT.put("illustillustration-2", 17305143);
        RESID_DICTIONARY_DEFAULT.put("illustillustration-1", Integer.valueOf(R.drawable.media_button_background));
        RESID_DICTIONARY_EVF = new HashMap<>();
        RESID_DICTIONARY_EVF.put("off", Integer.valueOf(R.drawable.pointer_wait_19));
        RESID_DICTIONARY_EVF.put("toy-cameranormal", Integer.valueOf(R.drawable.sym_keyboard_enter));
        RESID_DICTIONARY_EVF.put("toy-cameracool", 17305314);
        RESID_DICTIONARY_EVF.put("toy-camerawarm", 17304892);
        RESID_DICTIONARY_EVF.put("toy-cameragreen", 17305285);
        RESID_DICTIONARY_EVF.put("toy-cameramagenta", 17304832);
        RESID_DICTIONARY_EVF.put(PictureEffectController.MODE_POP_COLOR, Integer.valueOf(R.drawable.panel_picture_frame_bg_focus_blue));
        RESID_DICTIONARY_EVF.put("posterizationposterization-mono", 17304827);
        RESID_DICTIONARY_EVF.put("posterizationposterization-color", Integer.valueOf(R.drawable.list_selector_pressed_holo_dark));
        RESID_DICTIONARY_EVF.put(PictureEffectController.MODE_RETRO_PHOTO, 17304739);
        RESID_DICTIONARY_EVF.put(PictureEffectController.MODE_SOFT_HIGH_KEY, 17305785);
        RESID_DICTIONARY_EVF.put("part-colorred", Integer.valueOf(R.drawable.sym_keyboard_feedback_delete));
        RESID_DICTIONARY_EVF.put("part-colorgreen", 17305781);
        RESID_DICTIONARY_EVF.put("part-colorblue", Integer.valueOf(R.drawable.sym_app_on_sd_unavailable_icon));
        RESID_DICTIONARY_EVF.put("part-coloryellow", 17305745);
        RESID_DICTIONARY_EVF.put(PictureEffectController.MODE_ROUGH_MONO, 17304886);
        RESID_DICTIONARY_EVF.put("soft-focussoft-focus-3", Integer.valueOf(R.drawable.ic_chooser_pin));
        RESID_DICTIONARY_EVF.put("soft-focussoft-focus-2", 17304974);
        RESID_DICTIONARY_EVF.put("soft-focussoft-focus-1", Integer.valueOf(R.drawable.slice_remote_input_bg));
        RESID_DICTIONARY_EVF.put("hdr-arthdr-art-3", 17304889);
        RESID_DICTIONARY_EVF.put("hdr-arthdr-art-2", Integer.valueOf(R.drawable.ic_checkmark_holo_light));
        RESID_DICTIONARY_EVF.put("hdr-arthdr-art-1", 17304969);
        RESID_DICTIONARY_EVF.put(PictureEffectController.MODE_RICH_TONE_MONOCHROME, Integer.valueOf(R.drawable.fastscroll_track_pressed_holo_dark));
        RESID_DICTIONARY_EVF.put("miniatureauto", 17304820);
        RESID_DICTIONARY_EVF.put("miniatureupper", 17303986);
        RESID_DICTIONARY_EVF.put("miniaturehcenter", Integer.valueOf(R.drawable.textfield_activated_holo_light));
        RESID_DICTIONARY_EVF.put("miniaturelower", 17305365);
        RESID_DICTIONARY_EVF.put("miniatureright", 17304890);
        RESID_DICTIONARY_EVF.put("miniaturevcenter", 17305310);
        RESID_DICTIONARY_EVF.put("miniatureleft", Integer.valueOf(R.drawable.tab_unselected_v4));
        RESID_DICTIONARY_EVF.put(PictureEffectController.WATERCOLOR, 17305608);
        RESID_DICTIONARY_EVF.put("illustillustration-3", 17304197);
        RESID_DICTIONARY_EVF.put("illustillustration-2", 17305143);
        RESID_DICTIONARY_EVF.put("illustillustration-1", Integer.valueOf(R.drawable.media_button_background));
    }

    public PictureEffectIcon(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mDisplayObserver = DisplayModeObserver.getInstance();
        this.RESID_DICTIONARY = createResIdLcdDictionary();
    }

    protected HashMap<String, Integer> createResIdLcdDictionary() {
        int device = this.mDisplayObserver.getActiveDevice();
        return device != 1 ? RESID_DICTIONARY_DEFAULT : RESID_DICTIONARY_EVF;
    }

    @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage
    protected NotificationListener getNotificationListener() {
        if (this.mListener == null) {
            this.mListener = new ActiveImage.ActiveImageListener() { // from class: com.sony.imaging.app.base.shooting.widget.PictureEffectIcon.1
                private final String[] TAGS = {CameraNotificationManager.PICTURE_EFFECT_CHANGE};

                @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage.ActiveImageListener, com.sony.imaging.app.util.NotificationListener
                public void onNotify(String tag) {
                    super.onNotify(tag);
                    PictureEffectIcon.this.refresh();
                }

                @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage.ActiveImageListener
                public String[] addTags() {
                    return this.TAGS;
                }
            };
        }
        return this.mListener;
    }

    @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage
    protected boolean isVisible() {
        return !PictureEffectController.getInstance().isUnavailableSceneFactor(PictureEffectController.PICTUREEFFECT);
    }

    @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage
    protected void refresh() {
        String key = getValue();
        Drawable d = obtainDrawable(key);
        setImageDrawable(d);
    }

    protected String getValue() {
        PictureEffectController controller = PictureEffectController.getInstance();
        String value = controller.getValue();
        String mode = controller.getValue(PictureEffectController.PICTUREEFFECT);
        if (value != null && mode != null && !value.equals(mode)) {
            return mode + value;
        }
        return value;
    }

    private Drawable obtainDrawable(String key) {
        Integer resId = this.RESID_DICTIONARY.get(key);
        if (resId == null) {
            return null;
        }
        try {
            Drawable d = getResources().getDrawable(resId.intValue());
            return d;
        } catch (Resources.NotFoundException e) {
            Log.i(TAG, STRBUILD.replace(0, STRBUILD.length(), LOG_RESOURCE_NOT_FOUND).append(key).toString());
            return null;
        }
    }
}
