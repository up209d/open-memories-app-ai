package com.sony.imaging.app.liveviewgrading.shooting.layout;

import android.os.Handler;
import android.util.Log;
import android.util.SparseArray;
import android.view.KeyEvent;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.shooting.camera.MovieFormatController;
import com.sony.imaging.app.base.shooting.layout.StableLayout;
import com.sony.imaging.app.liveviewgrading.AppLog;
import com.sony.imaging.app.liveviewgrading.ColorGradingConstants;
import com.sony.imaging.app.liveviewgrading.R;
import com.sony.imaging.app.liveviewgrading.menu.controller.LVGParameterValueController;
import com.sony.imaging.app.liveviewgrading.shooting.LVGEffectValueController;

/* loaded from: classes.dex */
public class ColorGradingStableLayout extends StableLayout {
    private static final SparseArray<Integer> LAYOUT_LIST_FOR_FINDER;
    private static final SparseArray<Integer> LAYOUT_LIST_FOR_HDMI;
    public static final int OPEN_PARAMETER_ADJUSTMENT_SCREEN = 1221;
    public static final int OPEN_PAYBACK_EXIT_SCREEN = 111;
    private static int TIMEOUT = 100;
    private static final SparseArray<Integer> LAYOUT_LIST_FOR_PANEL = new SparseArray<>();
    private String TAG = AppLog.getClassName();
    private Handler mHandler = null;
    private Runnable mGammaTableUpdater = new Runnable() { // from class: com.sony.imaging.app.liveviewgrading.shooting.layout.ColorGradingStableLayout.1
        @Override // java.lang.Runnable
        public void run() {
            ColorGradingStableLayout.this.setGammaTable();
        }
    };

    static {
        LAYOUT_LIST_FOR_PANEL.append(1, Integer.valueOf(R.layout.colorgrading_shooting_main_sid_info_on_panel));
        LAYOUT_LIST_FOR_PANEL.append(3, Integer.valueOf(R.layout.colorgrading_shooting_main_sid_info_off_panel));
        LAYOUT_LIST_FOR_PANEL.append(5, Integer.valueOf(R.layout.colorgrading_shooting_main_sid_histogram_panel));
        LAYOUT_LIST_FOR_PANEL.append(4, Integer.valueOf(R.layout.colorgrading_shooting_main_sid_digitallevel_panel));
        LAYOUT_LIST_FOR_FINDER = new SparseArray<>();
        LAYOUT_LIST_FOR_FINDER.append(1, Integer.valueOf(R.layout.colorgrading_shooting_main_sid_basic_info_evf));
        LAYOUT_LIST_FOR_FINDER.append(3, Integer.valueOf(R.layout.colorgrading_shooting_main_sid_info_off_evf));
        LAYOUT_LIST_FOR_FINDER.append(5, Integer.valueOf(R.layout.colorgrading_shooting_main_sid_histogram_evf));
        LAYOUT_LIST_FOR_FINDER.append(4, Integer.valueOf(R.layout.colorgrading_shooting_main_sid_digitallevel_evf));
        LAYOUT_LIST_FOR_HDMI = LAYOUT_LIST_FOR_PANEL;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setGammaTable() {
        int[] params = new int[8];
        String preset = LVGParameterValueController.getInstance().getPresetValues();
        String[] dev = preset.split(MovieFormatController.Settings.SEMI_COLON, -1);
        for (String str : dev) {
            String[] element = str.split(MovieFormatController.Settings.EQUAL);
            if ("Contrast".equals(element[0])) {
                params[0] = Integer.valueOf(element[1]).intValue();
            } else if ("Saturation".equals(element[0])) {
                params[1] = Integer.valueOf(element[1]).intValue();
            } else if ("Sharpness".equals(element[0])) {
                params[2] = Integer.valueOf(element[1]).intValue();
            } else if (ColorGradingConstants.COLOR_TEMP.equals(element[0])) {
                params[3] = Integer.valueOf(element[1]).intValue();
            } else if (ColorGradingConstants.COLOR_TINT.equals(element[0])) {
                params[4] = Integer.valueOf(element[1]).intValue();
            } else if (ColorGradingConstants.COLOR_DEPTH_R.equals(element[0])) {
                params[5] = Integer.valueOf(element[1]).intValue();
            } else if (ColorGradingConstants.COLOR_DEPTH_G.equals(element[0])) {
                params[6] = Integer.valueOf(element[1]).intValue();
            } else if (ColorGradingConstants.COLOR_DEPTH_B.equals(element[0])) {
                params[7] = Integer.valueOf(element[1]).intValue();
            }
        }
        try {
            LVGEffectValueController.getInstance().setParamValues(params);
            LVGEffectValueController.getInstance().applyGammaTable();
        } catch (Exception e) {
            Log.i(this.TAG, "applyGammaTable is not possible");
        }
    }

    @Override // com.sony.imaging.app.base.shooting.layout.StableLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        this.mHandler = getHandler();
        if (this.mHandler != null) {
            this.mHandler.removeCallbacks(this.mGammaTableUpdater);
            this.mHandler.postDelayed(this.mGammaTableUpdater, TIMEOUT);
        }
    }

    @Override // com.sony.imaging.app.base.shooting.layout.StableLayout, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        if (this.mHandler != null) {
            this.mHandler.removeCallbacks(this.mGammaTableUpdater);
        }
        super.onPause();
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedPlayBackKey() {
        getHandler().sendEmptyMessage(OPEN_PAYBACK_EXIT_SCREEN);
        return 1;
    }

    @Override // com.sony.imaging.app.base.shooting.layout.StableLayout
    protected int getLayout(int device, int dispmode) {
        if (device == 0) {
            int layout = LAYOUT_LIST_FOR_PANEL.get(dispmode).intValue();
            return layout;
        }
        if (device == 1) {
            int layout2 = LAYOUT_LIST_FOR_FINDER.get(dispmode).intValue();
            return layout2;
        }
        if (device != 2) {
            return -1;
        }
        int layout3 = LAYOUT_LIST_FOR_HDMI.get(dispmode).intValue();
        return layout3;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachKeyHandler
    public int onDeleteKeyPushed(KeyEvent event) {
        boolean isS2CautionShown = false;
        int[] cur = CautionUtilityClass.getInstance().CurrentCautionId();
        for (int i : cur) {
            if (i != 2058) {
                if (i != 65535) {
                    isS2CautionShown = true;
                }
            } else {
                isS2CautionShown = true;
            }
        }
        if (isS2CautionShown) {
            CautionUtilityClass.getInstance().executeTerminate();
            return 1;
        }
        getHandler().sendEmptyMessage(OPEN_PARAMETER_ADJUSTMENT_SCREEN);
        return 1;
    }
}
