package com.sony.imaging.app.pictureeffectplus.shooting.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.PictureEffectController;
import com.sony.imaging.app.base.shooting.widget.ActiveImage;
import com.sony.imaging.app.base.shooting.widget.PictureEffectIcon;
import com.sony.imaging.app.pictureeffectplus.R;
import com.sony.imaging.app.pictureeffectplus.shooting.camera.NotificationTag;
import com.sony.imaging.app.pictureeffectplus.shooting.camera.PictureEffectPlusController;
import com.sony.imaging.app.util.NotificationListener;
import java.util.ArrayList;
import java.util.HashMap;

/* loaded from: classes.dex */
public class PictureEffectPlusIcon extends PictureEffectIcon {
    private static final String ICON_CHANNNEL_0 = "ch0";
    private static final String ICON_CHANNNEL_1 = "ch1";
    private static final String PICTUREEFFECTPLUS = "ApplicationTop";
    private static final String SUB_MODE_MINIATURE_PLUS = "miniature_mode";
    private static final String SUB_MODE_PARTCOLOR_PLUS = "partcolor_mode";
    private static final String SUB_MODE_TOY_PLUS = "toy_mode";
    private static final String TAG = "PictureEffectPlusIcon";
    protected String FUNC_NAME;
    String iconChannel;
    String itemId;
    String itemIdMiniaturePlus;
    String itemIdPartcolorPlus;
    String itemIdToycameraPlus;
    private final CameraNotificationManager mCameraNotifier;
    private ActiveImage.ActiveImageListener mListener;
    protected static final StringBuilder STRBUILD = new StringBuilder();
    private static ArrayList<String> DOUBLE_OPTION_ID = new ArrayList<>();

    static {
        DOUBLE_OPTION_ID.add(PictureEffectPlusController.MODE_MINIATURE_AREA);
        DOUBLE_OPTION_ID.add(PictureEffectPlusController.MODE_MINIATURE_EFFECT);
        DOUBLE_OPTION_ID.add(PictureEffectPlusController.MODE_TOY_CAMERA_COLOR);
        DOUBLE_OPTION_ID.add(PictureEffectPlusController.MODE_TOY_CAMERA_DARKNESS);
        DOUBLE_OPTION_ID.add(PictureEffectPlusController.MODE_PART_COLOR_CH0);
        DOUBLE_OPTION_ID.add(PictureEffectPlusController.MODE_PART_COLOR_CH1);
    }

    public PictureEffectPlusIcon(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.FUNC_NAME = "";
        this.mCameraNotifier = CameraNotificationManager.getInstance();
        TypedArray attr = context.obtainStyledAttributes(attrs, R.styleable.PictureEffectPlusIcon);
        this.itemIdToycameraPlus = attr.getString(0);
        this.itemIdMiniaturePlus = attr.getString(1);
        this.itemIdPartcolorPlus = attr.getString(2);
        this.iconChannel = attr.getString(3);
        this.FUNC_NAME = TAG;
        STRBUILD.replace(0, STRBUILD.length(), this.FUNC_NAME);
        Log.i(TAG, STRBUILD.toString());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.widget.PictureEffectIcon
    public HashMap<String, Integer> createResIdLcdDictionary() {
        HashMap<String, Integer> dictionary = super.createResIdLcdDictionary();
        dictionary.put("toy-cameratoy-camera-16", Integer.valueOf(R.drawable.p_16_dd_parts_pe_main_lcd_icon_toy_camera_plus_shading_hi));
        dictionary.put("toy-cameratoy-camera-0", Integer.valueOf(R.drawable.p_16_dd_parts_pe_main_lcd_icon_toy_camera_plus_shading_mid));
        dictionary.put("toy-cameratoy-camera--16", Integer.valueOf(R.drawable.p_16_dd_parts_pe_main_lcd_icon_toy_camera_plus_shading_low));
        dictionary.put("soft-high-keysofthighkeyblue", Integer.valueOf(R.drawable.p_16_dd_parts_pe_main_lcd_icon_soft_high_key_plus_color_blue));
        dictionary.put("soft-high-keysofthighkeygreen", Integer.valueOf(R.drawable.p_16_dd_parts_pe_main_lcd_icon_soft_high_key_plus_color_green));
        dictionary.put("soft-high-keysofthighkeypink", Integer.valueOf(R.drawable.p_16_dd_parts_pe_main_lcd_icon_soft_high_key_plus_color_pink));
        dictionary.put("part-colorpart-color-custom", Integer.valueOf(R.drawable.p_16_dd_parts_pe_main_lcd_icon_part_color_plus_custom));
        dictionary.put("part-colorpart-color-non-set", Integer.valueOf(R.drawable.p_16_dd_parts_pe_main_lcd_icon_part_color_plus_non_set));
        dictionary.put("part-colorpartgreen", Integer.valueOf(android.R.drawable.title_bar_shadow));
        dictionary.put("part-colorpartblue", Integer.valueOf(android.R.drawable.tooltip_frame));
        dictionary.put("part-colorpartredch0", Integer.valueOf(android.R.drawable.title_bar_portrait));
        dictionary.put("part-colorpartgreench0", Integer.valueOf(android.R.drawable.title_bar_shadow));
        dictionary.put("part-colorpartbluech0", Integer.valueOf(android.R.drawable.tooltip_frame));
        dictionary.put("part-colorpartyellowch0", Integer.valueOf(android.R.drawable.unknown_image));
        dictionary.put("part-colorpart-color-custom-ch0", Integer.valueOf(R.drawable.p_16_dd_parts_pe_main_lcd_icon_part_color_plus_custom));
        dictionary.put("part-colorpart-color-non-set-ch0", Integer.valueOf(R.drawable.p_16_dd_parts_pe_main_lcd_icon_part_color_plus_non_set));
        dictionary.put("toy-cameratoy-normal", 17305663);
        dictionary.put("toy-cameratoy-cool", 17305665);
        dictionary.put("toy-cameratoy-warm", 17305667);
        dictionary.put("toy-cameratoy-green", 17305668);
        dictionary.put("toy-cameratoy-magenta", 17305669);
        dictionary.put("miniaturecomb-off", Integer.valueOf(R.drawable.p_16_dd_parts_pe_main_lcd_icon_miniature_non_set));
        dictionary.put("miniaturemin_retro", 17305674);
        dictionary.put("miniaturenormal", 17305663);
        dictionary.put("miniaturecool", 17305665);
        dictionary.put("miniaturewarm", 17305667);
        dictionary.put("miniaturegreen", 17305668);
        dictionary.put("miniaturemagenta", 17305669);
        dictionary.put("watercolor", Integer.valueOf(R.drawable.p_16_dd_parts_pe_main_lcd_icon_watercolor));
        dictionary.put("illustillust-3", Integer.valueOf(R.drawable.p_16_dd_parts_pe_main_lcd_icon_illustration_hi));
        dictionary.put("illustillust-2", Integer.valueOf(R.drawable.p_16_dd_parts_pe_main_lcd_icon_illustration_mid));
        dictionary.put("illustillust-1", Integer.valueOf(R.drawable.p_16_dd_parts_pe_main_lcd_icon_illustration_low));
        return dictionary;
    }

    @Override // com.sony.imaging.app.base.shooting.widget.PictureEffectIcon, com.sony.imaging.app.base.shooting.widget.ActiveImage
    protected NotificationListener getNotificationListener() {
        if (this.mListener == null) {
            this.mListener = new ActiveImage.ActiveImageListener() { // from class: com.sony.imaging.app.pictureeffectplus.shooting.widget.PictureEffectPlusIcon.1
                private final String[] TAGS = {NotificationTag.PICTURE_EFFECT_PLUS_CHANGE, CameraNotificationManager.PICTURE_EFFECT_CHANGE};

                @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage.ActiveImageListener, com.sony.imaging.app.util.NotificationListener
                public void onNotify(String tag) {
                    super.onNotify(tag);
                    PictureEffectPlusIcon.this.refresh();
                }

                @Override // com.sony.imaging.app.base.shooting.widget.ActiveImage.ActiveImageListener
                public String[] addTags() {
                    return this.TAGS;
                }
            };
        }
        this.FUNC_NAME = "getNotificationListener";
        STRBUILD.replace(0, STRBUILD.length(), this.FUNC_NAME);
        Log.i(TAG, STRBUILD.toString());
        return this.mListener;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.widget.PictureEffectIcon, com.sony.imaging.app.base.shooting.widget.ActiveImage
    public void refresh() {
        String mode = PictureEffectPlusController.getInstance().getValue("ApplicationTop");
        this.itemId = null;
        if (mode.equals(PictureEffectPlusController.MODE_MINIATURE_PLUS)) {
            this.itemId = this.itemIdMiniaturePlus;
        } else if (mode.equals(PictureEffectPlusController.MODE_TOY_CAMERA_PLUS)) {
            this.itemId = this.itemIdToycameraPlus;
        } else if (mode.equals(PictureEffectPlusController.MODE_PART_COLOR_PLUS)) {
            this.itemId = this.itemIdPartcolorPlus;
        }
        super.refresh();
        if (this.iconChannel.equals(ICON_CHANNNEL_1) && !DOUBLE_OPTION_ID.contains(this.itemId)) {
            setImageDrawable(null);
        }
        this.FUNC_NAME = "refresh";
        STRBUILD.replace(0, STRBUILD.length(), this.FUNC_NAME);
        Log.i(TAG, STRBUILD.toString());
    }

    @Override // com.sony.imaging.app.base.shooting.widget.PictureEffectIcon
    protected String getValue() {
        String value;
        String effect = PictureEffectPlusController.getInstance().getValue("ApplicationTop");
        if (this.itemId != null) {
            if (effect.equals(PictureEffectPlusController.MODE_MINIATURE_PLUS)) {
                effect = PictureEffectController.MODE_MINIATURE;
            } else if (effect.equals(PictureEffectPlusController.MODE_TOY_CAMERA_PLUS)) {
                effect = PictureEffectController.MODE_TOY_CAMERA;
            } else if (effect.equals(PictureEffectPlusController.MODE_PART_COLOR_PLUS)) {
                effect = PictureEffectController.MODE_PART_COLOR;
            }
            value = effect + PictureEffectPlusController.getInstance().getValue(this.itemId);
        } else if (PictureEffectPlusController.NO_OPTION_VALUE.equals(PictureEffectPlusController.getInstance().getValue(effect))) {
            value = PictureEffectPlusController.getInstance().getValue();
        } else {
            value = effect + PictureEffectPlusController.getInstance().getValue(effect);
        }
        this.FUNC_NAME = "getValue";
        STRBUILD.replace(0, STRBUILD.length(), this.FUNC_NAME);
        Log.i(TAG, STRBUILD.toString());
        return value;
    }
}
