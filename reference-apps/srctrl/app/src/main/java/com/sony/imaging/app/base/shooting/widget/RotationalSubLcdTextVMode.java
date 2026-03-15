package com.sony.imaging.app.base.shooting.widget;

import android.R;
import android.content.Context;
import android.util.AttributeSet;
import com.sony.imaging.app.base.common.widget.RotationalSubLcdTextView;
import com.sony.imaging.app.base.shooting.camera.MovieFormatController;
import java.util.HashMap;
import java.util.Map;

/* loaded from: classes.dex */
public class RotationalSubLcdTextVMode extends RotationalSubLcdTextView {
    private static final String TAG = RotationalSubLcdTextVMode.class.getSimpleName();
    private Map<String, String> mTextMap;

    public RotationalSubLcdTextVMode(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mTextMap = new HashMap();
        initView();
    }

    public RotationalSubLcdTextVMode(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mTextMap = new HashMap();
        initView();
    }

    public RotationalSubLcdTextVMode(Context context) {
        super(context);
        this.mTextMap = new HashMap();
        initView();
    }

    private void initView() {
        this.mTextMap.put(MovieFormatController.MP4_1080_PS, getResources().getString(R.string.menu_delete_shortcut_label));
        this.mTextMap.put(MovieFormatController.MP4_1080_HQ, getResources().getString(R.string.menu_enter_shortcut_label));
        this.mTextMap.put(MovieFormatController.MP4_1080, getResources().getString(R.string.menu_enter_shortcut_label));
        this.mTextMap.put(MovieFormatController.MP4_720, getResources().getString(R.string.menu_function_shortcut_label));
        this.mTextMap.put(MovieFormatController.XAVC_100M_100P, getResources().getString(R.string.new_sms_notification_title));
        this.mTextMap.put(MovieFormatController.XAVC_100M_120P, getResources().getString(R.string.new_sms_notification_title));
        this.mTextMap.put(MovieFormatController.XAVC_100M_200P, getResources().getString(R.string.new_sms_notification_title));
        this.mTextMap.put(MovieFormatController.XAVC_100M_240P, getResources().getString(R.string.new_sms_notification_title));
        this.mTextMap.put(MovieFormatController.XAVC_4K_100M_24P, getResources().getString(R.string.new_sms_notification_title));
        this.mTextMap.put(MovieFormatController.XAVC_4K_100M_25P, getResources().getString(R.string.new_sms_notification_title));
        this.mTextMap.put(MovieFormatController.XAVC_4K_100M_30P, getResources().getString(R.string.new_sms_notification_title));
        this.mTextMap.put(MovieFormatController.XAVC_60M_100P, getResources().getString(R.string.new_sms_notification_content));
        this.mTextMap.put(MovieFormatController.XAVC_60M_120P, getResources().getString(R.string.new_sms_notification_content));
        this.mTextMap.put(MovieFormatController.XAVC_60M_200P, getResources().getString(R.string.new_sms_notification_content));
        this.mTextMap.put(MovieFormatController.XAVC_60M_240P, getResources().getString(R.string.new_sms_notification_content));
        this.mTextMap.put(MovieFormatController.XAVC_4K_60M_24P, getResources().getString(R.string.new_sms_notification_content));
        this.mTextMap.put(MovieFormatController.XAVC_4K_60M_25P, getResources().getString(R.string.new_sms_notification_content));
        this.mTextMap.put(MovieFormatController.XAVC_4K_60M_30P, getResources().getString(R.string.new_sms_notification_content));
        this.mTextMap.put(MovieFormatController.XAVC_50M_24P, getResources().getString(R.string.new_app_description));
        this.mTextMap.put(MovieFormatController.XAVC_50M_25P, getResources().getString(R.string.new_app_description));
        this.mTextMap.put(MovieFormatController.XAVC_50M_30P, getResources().getString(R.string.new_app_description));
        this.mTextMap.put(MovieFormatController.XAVC_50M_50P, getResources().getString(R.string.new_app_description));
        this.mTextMap.put(MovieFormatController.XAVC_50M_60P, getResources().getString(R.string.new_app_description));
    }

    @Override // com.sony.imaging.app.base.common.widget.RotationalSubLcdTextView, com.sony.imaging.app.base.common.widget.Rotational
    public boolean isValidValue() {
        return true;
    }

    @Override // com.sony.imaging.app.base.common.widget.RotationalSubLcdTextView
    protected String makeText() {
        String vmode = MovieFormatController.getInstance().getValue("record_setting");
        String text = this.mTextMap.get(vmode);
        if (text == null) {
            if (MovieFormatController.MP4_HS120.equals(vmode)) {
                String broadcast = MovieFormatController.getBroadcastSystem();
                if (MovieFormatController.NTSC.equals(broadcast)) {
                    return getResources().getString(R.string.minutes);
                }
                if (MovieFormatController.PAL.equals(broadcast)) {
                    return getResources().getString(R.string.mmcc_authentication_reject_msim_template);
                }
            } else if (MovieFormatController.MP4_HS240.equals(vmode)) {
                String broadcast2 = MovieFormatController.getBroadcastSystem();
                if (MovieFormatController.NTSC.equals(broadcast2)) {
                    return getResources().getString(R.string.mismatchPin);
                }
                if (MovieFormatController.PAL.equals(broadcast2)) {
                    return getResources().getString(R.string.mmcc_illegal_me);
                }
            }
            return "";
        }
        return text;
    }
}
