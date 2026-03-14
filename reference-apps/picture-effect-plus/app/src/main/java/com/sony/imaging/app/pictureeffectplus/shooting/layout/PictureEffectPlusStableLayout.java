package com.sony.imaging.app.pictureeffectplus.shooting.layout;

import android.util.Log;
import android.util.SparseArray;
import com.sony.imaging.app.base.shooting.layout.StableLayout;
import com.sony.imaging.app.pictureeffectplus.R;

/* loaded from: classes.dex */
public class PictureEffectPlusStableLayout extends StableLayout {
    private static final SparseArray<Integer> LAYOUT_LIST_FOR_FINDER;
    private static final SparseArray<Integer> LAYOUT_LIST_FOR_HDMI;
    private static final String TAG = "PictureEffectPlusStableLayout";
    protected String FUNC_NAME = "";
    protected static final StringBuilder STRBUILD = new StringBuilder();
    private static final SparseArray<Integer> LAYOUT_LIST_FOR_PANEL = new SparseArray<>();

    static {
        LAYOUT_LIST_FOR_PANEL.append(1, Integer.valueOf(R.layout.pictureeffectplus_shooting_main_sid_info_on_panel));
        LAYOUT_LIST_FOR_PANEL.append(3, Integer.valueOf(R.layout.pictureeffectplus_shooting_main_sid_info_off_panel));
        LAYOUT_LIST_FOR_PANEL.append(5, Integer.valueOf(R.layout.pictureeffectplus_shooting_main_sid_histogram_panel));
        LAYOUT_LIST_FOR_PANEL.append(4, Integer.valueOf(R.layout.pictureeffectplus_shooting_main_sid_digitallevel_panel));
        LAYOUT_LIST_FOR_FINDER = new SparseArray<>();
        LAYOUT_LIST_FOR_FINDER.append(1, Integer.valueOf(R.layout.pictureeffectplus_shooting_main_sid_basic_info_evf));
        LAYOUT_LIST_FOR_FINDER.append(3, Integer.valueOf(R.layout.shooting_main_sid_info_off_evf));
        LAYOUT_LIST_FOR_FINDER.append(5, Integer.valueOf(R.layout.pictureeffectplus_shooting_main_sid_histogram_evf));
        LAYOUT_LIST_FOR_FINDER.append(4, Integer.valueOf(R.layout.pictureeffectplus_shooting_main_sid_digitallevel_evf));
        LAYOUT_LIST_FOR_HDMI = LAYOUT_LIST_FOR_PANEL;
    }

    @Override // com.sony.imaging.app.base.shooting.layout.StableLayout
    protected int getLayout(int device, int dispmode) {
        int layoutId = -1;
        if (device == 0) {
            layoutId = LAYOUT_LIST_FOR_PANEL.get(dispmode).intValue();
        } else if (device == 1) {
            layoutId = LAYOUT_LIST_FOR_FINDER.get(dispmode).intValue();
        } else if (device == 2) {
            layoutId = LAYOUT_LIST_FOR_HDMI.get(dispmode).intValue();
        }
        this.FUNC_NAME = "getLayout";
        STRBUILD.replace(0, STRBUILD.length(), this.FUNC_NAME);
        Log.i(TAG, STRBUILD.toString());
        return layoutId;
    }
}
