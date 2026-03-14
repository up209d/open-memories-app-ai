package com.sony.imaging.app.liveviewgrading.menu.layout;

import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.base.shooting.camera.MovieFormatController;
import com.sony.imaging.app.base.shooting.camera.PictureEffectController;
import com.sony.imaging.app.liveviewgrading.AppLog;
import com.sony.imaging.app.liveviewgrading.ColorGradingConstants;
import com.sony.imaging.app.liveviewgrading.R;
import com.sony.imaging.app.liveviewgrading.menu.controller.ColorGradingController;
import com.sony.imaging.app.liveviewgrading.menu.controller.LVGParameterValueController;
import com.sony.imaging.app.liveviewgrading.menu.widget.VerticalSeekBar;
import com.sony.imaging.app.liveviewgrading.shooting.LVGEffectValueController;
import com.sony.imaging.app.util.BackUpUtil;
import com.sony.scalar.hardware.CameraEx;

/* loaded from: classes.dex */
public class ColorGradingPresetAdjustmentScreen extends DisplayMenuItemsMenuLayout implements View.OnClickListener {
    private static final String STR_FORMAT_INTPLUS = "＋%1d";
    Pair<Camera.Parameters, CameraEx.ParametersModifier> emptyParams;
    private int lastPos;
    private RelativeLayout mDotLayout1;
    private RelativeLayout mDotLayout2;
    private RelativeLayout mDotLayout3;
    private TextView mGaugeBarProgressText;
    private ImageView mGaugeBarSource;
    private ImageView mGroup1RightArrow;
    private RelativeLayout mGroup1_bg;
    private ImageView mGroup2LeftArrow;
    private ImageView mGroup2RightArrow;
    private RelativeLayout mGroup2_bg;
    private ImageView mGroup3LeftArrow;
    private RelativeLayout mGroup3_bg;
    private int mParam1;
    private int mParam2;
    private int mParam3;
    private int mParam4;
    private int mParam5;
    private int mParam6;
    private int mParam7;
    private int mParam8;
    private RelativeLayout mParamBox1;
    private RelativeLayout mParamBox2;
    private RelativeLayout mParamBox3;
    private RelativeLayout mParamBox4;
    private RelativeLayout mParamBox5;
    private RelativeLayout mParamBox6;
    private RelativeLayout mParamBox7;
    private RelativeLayout mParamBox8;
    private TextView mParamText1;
    private TextView mParamText2;
    private TextView mParamText3;
    private TextView mParamText4;
    private TextView mParamText5;
    private TextView mParamText6;
    private TextView mParamText7;
    private TextView mParamText8;
    private VerticalSeekBar mParameterGauge;
    Pair<Camera.Parameters, CameraEx.ParametersModifier> sup_params;
    private String TAG = AppLog.getClassName();
    private TextView mParamName = null;
    private boolean mIsMenuPushed = false;
    private ColorGradingController mController = null;
    private int mPreset = 0;

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View currentView = obtainViewFromPool(R.layout.colorgrading_menu_preset_adjustment);
        this.mParamName = (TextView) currentView.findViewById(R.id.TextView);
        this.mParamBox1 = (RelativeLayout) currentView.findViewById(R.id.param1);
        this.mParamBox2 = (RelativeLayout) currentView.findViewById(R.id.param2);
        this.mParamBox3 = (RelativeLayout) currentView.findViewById(R.id.param3);
        this.mParamBox4 = (RelativeLayout) currentView.findViewById(R.id.param4);
        this.mParamBox5 = (RelativeLayout) currentView.findViewById(R.id.param5);
        this.mParamBox6 = (RelativeLayout) currentView.findViewById(R.id.param6);
        this.mParamBox7 = (RelativeLayout) currentView.findViewById(R.id.param7);
        this.mParamBox8 = (RelativeLayout) currentView.findViewById(R.id.param8);
        this.mDotLayout1 = (RelativeLayout) currentView.findViewById(R.id.dot1);
        this.mDotLayout2 = (RelativeLayout) currentView.findViewById(R.id.dot2);
        this.mDotLayout3 = (RelativeLayout) currentView.findViewById(R.id.dot3);
        this.mGroup1_bg = (RelativeLayout) currentView.findViewById(R.id.group1_bg);
        this.mGroup2_bg = (RelativeLayout) currentView.findViewById(R.id.group2_bg);
        this.mGroup3_bg = (RelativeLayout) currentView.findViewById(R.id.group3_bg);
        this.mGroup1RightArrow = (ImageView) currentView.findViewById(R.id.group_1_rightArrow);
        this.mGroup2LeftArrow = (ImageView) currentView.findViewById(R.id.group_2_left_arrow);
        this.mGroup2RightArrow = (ImageView) currentView.findViewById(R.id.group_2_right_arrow);
        this.mGroup3LeftArrow = (ImageView) currentView.findViewById(R.id.group_3_left_arrow);
        this.mParamText1 = (TextView) currentView.findViewById(R.id.paramtext1);
        this.mParamText2 = (TextView) currentView.findViewById(R.id.paramtext2);
        this.mParamText3 = (TextView) currentView.findViewById(R.id.paramtext3);
        this.mParamText4 = (TextView) currentView.findViewById(R.id.paramtext4);
        this.mParamText5 = (TextView) currentView.findViewById(R.id.paramtext5);
        this.mParamText6 = (TextView) currentView.findViewById(R.id.paramtext6);
        this.mParamText7 = (TextView) currentView.findViewById(R.id.paramtext7);
        this.mParamText8 = (TextView) currentView.findViewById(R.id.paramtext8);
        this.mGaugeBarProgressText = (TextView) currentView.findViewById(R.id.vertical_sb_progresstext);
        this.mParameterGauge = (VerticalSeekBar) currentView.findViewById(R.id.vertical_Seekbar);
        this.mGaugeBarSource = (ImageView) currentView.findViewById(R.id.vsb_src);
        this.mController = ColorGradingController.getInstance();
        setListenersOnParamViews();
        this.sup_params = CameraSetting.getInstance().getSupportedParameters(2);
        this.emptyParams = CameraSetting.getInstance().getEmptyParameters();
        return currentView;
    }

    private void getParamValues() {
        String values = LVGParameterValueController.getInstance().getPresetValues();
        Log.e("", "saved values = " + values);
        String[] dev = values.split(MovieFormatController.Settings.SEMI_COLON, -1);
        for (String str : dev) {
            String[] element = str.split(MovieFormatController.Settings.EQUAL);
            if ("Contrast".equals(element[0])) {
                this.mParam1 = Integer.valueOf(element[1]).intValue();
            } else if ("Saturation".equals(element[0])) {
                this.mParam2 = Integer.valueOf(element[1]).intValue();
            } else if ("Sharpness".equals(element[0])) {
                this.mParam3 = Integer.valueOf(element[1]).intValue();
            } else if (ColorGradingConstants.COLOR_TEMP.equals(element[0])) {
                this.mParam4 = Integer.valueOf(element[1]).intValue();
            } else if (ColorGradingConstants.COLOR_TINT.equals(element[0])) {
                this.mParam5 = Integer.valueOf(element[1]).intValue();
            } else if (ColorGradingConstants.COLOR_DEPTH_R.equals(element[0])) {
                this.mParam6 = Integer.valueOf(element[1]).intValue();
            } else if (ColorGradingConstants.COLOR_DEPTH_G.equals(element[0])) {
                this.mParam7 = Integer.valueOf(element[1]).intValue();
            } else if (ColorGradingConstants.COLOR_DEPTH_B.equals(element[0])) {
                this.mParam8 = Integer.valueOf(element[1]).intValue();
            }
        }
        setValuesInCamera();
    }

    private void makeAllBoxesInvisible() {
        Log.d("", "LVG INSIDE makeAllBoxesInvisible");
        this.mParamBox1.setVisibility(8);
        this.mParamBox2.setVisibility(8);
        this.mParamBox3.setVisibility(8);
        this.mParamBox4.setVisibility(8);
        this.mParamBox5.setVisibility(8);
        this.mParamBox6.setVisibility(8);
        this.mParamBox7.setVisibility(8);
        this.mParamBox8.setVisibility(8);
        this.mGroup1RightArrow.setVisibility(8);
        this.mGroup2LeftArrow.setVisibility(8);
        this.mGroup2RightArrow.setVisibility(8);
        this.mGroup3LeftArrow.setVisibility(8);
        this.mDotLayout1.setVisibility(8);
        this.mDotLayout2.setVisibility(8);
        this.mDotLayout3.setVisibility(8);
        this.mGroup1_bg.setVisibility(8);
        this.mGroup2_bg.setVisibility(8);
        this.mGroup3_bg.setVisibility(8);
    }

    private void showGroup1() {
        Log.d("", "LVG INSIDE showGroup1");
        makeAllBoxesInvisible();
        this.mParamBox1.setVisibility(0);
        this.mParamBox2.setVisibility(0);
        this.mParamBox3.setVisibility(0);
        this.mGroup1_bg.setVisibility(0);
        if (this.mPreset != 3) {
            this.mGroup1RightArrow.setVisibility(0);
            this.mDotLayout1.setVisibility(0);
        } else {
            this.mGroup1RightArrow.setVisibility(4);
            this.mDotLayout1.setVisibility(4);
        }
        this.mParamText1.setText(getSignedValue(this.mParam1));
        this.mParamText2.setText(getSignedValue(this.mParam2));
        this.mParamText3.setText(getSignedValue(this.mParam3));
    }

    private void showGroup2() {
        Log.d("", "LVG INSIDE showGroup2");
        makeAllBoxesInvisible();
        this.mParamBox4.setVisibility(0);
        this.mParamBox5.setVisibility(0);
        this.mGroup2_bg.setVisibility(0);
        this.mGroup2LeftArrow.setVisibility(0);
        this.mGroup2RightArrow.setVisibility(0);
        this.mDotLayout2.setVisibility(0);
        this.mParamText4.setText(getSignedValue(this.mParam4));
        this.mParamText5.setText(getSignedValue(this.mParam5));
    }

    private void showGroup3() {
        Log.d("", "LVG INSIDE showGroup3");
        makeAllBoxesInvisible();
        this.mParamBox6.setVisibility(0);
        this.mParamBox7.setVisibility(0);
        this.mParamBox8.setVisibility(0);
        this.mGroup3_bg.setVisibility(0);
        this.mGroup3LeftArrow.setVisibility(0);
        this.mDotLayout3.setVisibility(0);
        this.mParamText6.setText(getSignedValue(this.mParam6));
        this.mParamText7.setText(getSignedValue(this.mParam7));
        this.mParamText8.setText(getSignedValue(this.mParam8));
    }

    private void showGroup(int lastPos) {
        Log.d("", "LVG INSIDE showGroup; lastPos = " + lastPos);
        if (lastPos == 1 || lastPos == 2 || lastPos == 3) {
            showGroup1();
        } else if (lastPos == 4 || lastPos == 5) {
            showGroup2();
        } else {
            showGroup3();
        }
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        super.onResume();
        getParamValues();
        this.lastPos = getLastCursorPosition();
        this.mPreset = LVGParameterValueController.getInstance().getLastSelectedPreset();
        showGroup(this.lastPos);
        setSelection(this.lastPos);
        touchRemove();
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    private void touchRemove() {
        this.mParamBox1.setOnTouchListener(new View.OnTouchListener() { // from class: com.sony.imaging.app.liveviewgrading.menu.layout.ColorGradingPresetAdjustmentScreen.1
            @Override // android.view.View.OnTouchListener
            public boolean onTouch(View v, MotionEvent event) {
                VerticalSeekBar.sISTouchEnable = true;
                return true;
            }
        });
        this.mParamBox2.setOnTouchListener(new View.OnTouchListener() { // from class: com.sony.imaging.app.liveviewgrading.menu.layout.ColorGradingPresetAdjustmentScreen.2
            @Override // android.view.View.OnTouchListener
            public boolean onTouch(View v, MotionEvent event) {
                VerticalSeekBar.sISTouchEnable = true;
                return true;
            }
        });
        this.mParamBox3.setOnTouchListener(new View.OnTouchListener() { // from class: com.sony.imaging.app.liveviewgrading.menu.layout.ColorGradingPresetAdjustmentScreen.3
            @Override // android.view.View.OnTouchListener
            public boolean onTouch(View v, MotionEvent event) {
                VerticalSeekBar.sISTouchEnable = true;
                return true;
            }
        });
        this.mParamBox4.setOnTouchListener(new View.OnTouchListener() { // from class: com.sony.imaging.app.liveviewgrading.menu.layout.ColorGradingPresetAdjustmentScreen.4
            @Override // android.view.View.OnTouchListener
            public boolean onTouch(View v, MotionEvent event) {
                VerticalSeekBar.sISTouchEnable = true;
                return true;
            }
        });
        this.mParamBox5.setOnTouchListener(new View.OnTouchListener() { // from class: com.sony.imaging.app.liveviewgrading.menu.layout.ColorGradingPresetAdjustmentScreen.5
            @Override // android.view.View.OnTouchListener
            public boolean onTouch(View v, MotionEvent event) {
                VerticalSeekBar.sISTouchEnable = true;
                return true;
            }
        });
        this.mParamBox6.setOnTouchListener(new View.OnTouchListener() { // from class: com.sony.imaging.app.liveviewgrading.menu.layout.ColorGradingPresetAdjustmentScreen.6
            @Override // android.view.View.OnTouchListener
            public boolean onTouch(View v, MotionEvent event) {
                VerticalSeekBar.sISTouchEnable = true;
                return true;
            }
        });
        this.mParamBox7.setOnTouchListener(new View.OnTouchListener() { // from class: com.sony.imaging.app.liveviewgrading.menu.layout.ColorGradingPresetAdjustmentScreen.7
            @Override // android.view.View.OnTouchListener
            public boolean onTouch(View v, MotionEvent event) {
                VerticalSeekBar.sISTouchEnable = true;
                return true;
            }
        });
        this.mParamBox8.setOnTouchListener(new View.OnTouchListener() { // from class: com.sony.imaging.app.liveviewgrading.menu.layout.ColorGradingPresetAdjustmentScreen.8
            @Override // android.view.View.OnTouchListener
            public boolean onTouch(View v, MotionEvent event) {
                VerticalSeekBar.sISTouchEnable = true;
                return true;
            }
        });
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout
    public void closeLayout() {
        VerticalSeekBar.sISTouchEnable = false;
        if (!this.mIsMenuPushed) {
            String newSet = flattenParamValues();
            LVGParameterValueController.getInstance().setPresetValues(newSet);
            setLastCursorPosition();
        }
        this.mController.setComeFromMenu(false);
        super.closeLayout();
    }

    private String flattenParamValues() {
        Log.e("", "INSIDE flatten");
        StringBuilder f = new StringBuilder();
        f.append("Contrast").append(MovieFormatController.Settings.EQUAL).append(this.mParam1).append(MovieFormatController.Settings.SEMI_COLON);
        f.append("Saturation").append(MovieFormatController.Settings.EQUAL).append(this.mParam2).append(MovieFormatController.Settings.SEMI_COLON);
        f.append("Sharpness").append(MovieFormatController.Settings.EQUAL).append(this.mParam3).append(MovieFormatController.Settings.SEMI_COLON);
        f.append(ColorGradingConstants.COLOR_TEMP).append(MovieFormatController.Settings.EQUAL).append(this.mParam4).append(MovieFormatController.Settings.SEMI_COLON);
        f.append(ColorGradingConstants.COLOR_TINT).append(MovieFormatController.Settings.EQUAL).append(this.mParam5).append(MovieFormatController.Settings.SEMI_COLON);
        f.append(ColorGradingConstants.COLOR_DEPTH_R).append(MovieFormatController.Settings.EQUAL).append(this.mParam6).append(MovieFormatController.Settings.SEMI_COLON);
        f.append(ColorGradingConstants.COLOR_DEPTH_G).append(MovieFormatController.Settings.EQUAL).append(this.mParam7).append(MovieFormatController.Settings.SEMI_COLON);
        f.append(ColorGradingConstants.COLOR_DEPTH_B).append(MovieFormatController.Settings.EQUAL).append(this.mParam8).append(MovieFormatController.Settings.SEMI_COLON);
        Log.e("", "flattened String = " + ((Object) f));
        return f.toString();
    }

    private int getLastCursorPosition() {
        Log.e("", "INSIDE getLastCursorPosition");
        int lastPreset = LVGParameterValueController.getInstance().getLastSelectedPreset();
        Log.e("", "lastPreset = " + lastPreset);
        switch (lastPreset) {
            case 1:
            case 2:
            case 3:
            case 4:
                this.lastPos = BackUpUtil.getInstance().getPreferenceInt(ColorGradingConstants.G1_LAST_PARAMETER, 1);
                break;
            case 5:
            case 6:
            case 7:
            case 8:
                this.lastPos = BackUpUtil.getInstance().getPreferenceInt(ColorGradingConstants.G2_LAST_PARAMETER, 1);
                break;
            case 9:
            case 10:
            case 11:
            case 12:
                this.lastPos = BackUpUtil.getInstance().getPreferenceInt(ColorGradingConstants.G3_LAST_PARAMETER, 1);
                break;
        }
        return this.lastPos;
    }

    private void setLastCursorPosition() {
        Log.e("", "INSIDE setLastCursorPosition");
        int lastPreset = LVGParameterValueController.getInstance().getLastSelectedPreset();
        Log.e("", "lastPreset = " + lastPreset);
        switch (lastPreset) {
            case 1:
            case 2:
            case 3:
            case 4:
                BackUpUtil.getInstance().setPreference(ColorGradingConstants.G1_LAST_PARAMETER, Integer.valueOf(this.lastPos));
                return;
            case 5:
            case 6:
            case 7:
            case 8:
                BackUpUtil.getInstance().setPreference(ColorGradingConstants.G2_LAST_PARAMETER, Integer.valueOf(this.lastPos));
                return;
            case 9:
            case 10:
            case 11:
            case 12:
                BackUpUtil.getInstance().setPreference(ColorGradingConstants.G3_LAST_PARAMETER, Integer.valueOf(this.lastPos));
                return;
            default:
                return;
        }
    }

    private void setListenersOnParamViews() {
        this.mParamBox1.setOnClickListener(this);
        this.mParamBox2.setOnClickListener(this);
        this.mParamBox3.setOnClickListener(this);
        this.mParamBox4.setOnClickListener(this);
        this.mParamBox5.setOnClickListener(this);
        this.mParamBox6.setOnClickListener(this);
        this.mParamBox7.setOnClickListener(this);
        this.mParamBox8.setOnClickListener(this);
    }

    private void removeListeners() {
        this.mParamBox1.setOnClickListener(null);
        this.mParamBox2.setOnClickListener(null);
        this.mParamBox3.setOnClickListener(null);
        this.mParamBox4.setOnClickListener(null);
        this.mParamBox5.setOnClickListener(null);
        this.mParamBox6.setOnClickListener(null);
        this.mParamBox7.setOnClickListener(null);
        this.mParamBox8.setOnClickListener(null);
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.FakeFragment
    public void onDestroy() {
        deinitialize();
        super.onDestroy();
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMenuKey() {
        this.mIsMenuPushed = true;
        openPreviousMenu();
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedLeftKey() {
        Log.d("", "LVG INSIDE pushedLeftKey");
        int pos = 1;
        if (this.mParamBox1.isSelected()) {
            if (this.mPreset != 3) {
                showGroup3();
                pos = 8;
            } else {
                pos = 3;
            }
        } else if (this.mParamBox4.isSelected()) {
            showGroup1();
            pos = 3;
        } else if (this.mParamBox6.isSelected()) {
            showGroup2();
            pos = 5;
        } else if (this.mParamBox2.isSelected()) {
            pos = 1;
        } else if (this.mParamBox3.isSelected()) {
            pos = 2;
        } else if (this.mParamBox5.isSelected()) {
            pos = 4;
        } else if (this.mParamBox7.isSelected()) {
            pos = 6;
        } else if (this.mParamBox8.isSelected()) {
            pos = 7;
        }
        setSelection(pos);
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedRightKey() {
        Log.d("", "LVG INSIDE pushedRightKey");
        int pos = 1;
        if (this.mParamBox3.isSelected()) {
            if (this.mPreset != 3) {
                showGroup2();
                pos = 4;
            } else {
                pos = 1;
            }
        } else if (this.mParamBox5.isSelected()) {
            showGroup3();
            pos = 6;
        } else if (this.mParamBox8.isSelected()) {
            showGroup1();
            pos = 1;
        } else if (this.mParamBox1.isSelected()) {
            pos = 2;
        } else if (this.mParamBox2.isSelected()) {
            pos = 3;
        } else if (this.mParamBox4.isSelected()) {
            pos = 5;
        } else if (this.mParamBox6.isSelected()) {
            pos = 7;
        } else if (this.mParamBox7.isSelected()) {
            pos = 8;
        }
        setSelection(pos);
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedUpKey() {
        VerticalSeekBar.sISTouchEnable = false;
        if (this.mParameterGauge.getProgress() >= this.mParameterGauge.getMax()) {
            return -1;
        }
        this.mParameterGauge.incrementProgressBy(1);
        incrementParameterValue();
        setParamOnCamera();
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedDownKey() {
        VerticalSeekBar.sISTouchEnable = false;
        if (this.mParameterGauge.getProgress() <= 0) {
            return -1;
        }
        this.mParameterGauge.incrementProgressBy(-1);
        decrementParameterValue();
        setParamOnCamera();
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyUp(int keyCode, KeyEvent event) {
        if (VerticalSeekBar.sISTouchEnable) {
            if (event.getScanCode() == 103) {
                if (this.mParameterGauge.getProgress() >= this.mParameterGauge.getMax()) {
                    return -1;
                }
                this.mParameterGauge.incrementProgressBy(1);
                incrementParameterValue();
                setParamOnCamera();
                return 1;
            }
            if (event.getScanCode() == 108) {
                if (this.mParameterGauge.getProgress() <= 0) {
                    return -1;
                }
                this.mParameterGauge.incrementProgressBy(-1);
                decrementParameterValue();
                setParamOnCamera();
                return 1;
            }
        }
        return super.onKeyUp(keyCode, event);
    }

    private String getSignedValue(int paramValue) {
        if (paramValue > 0) {
            String ret = String.valueOf(Math.abs(paramValue));
            return String.format((String) getResources().getText(17041715), ret);
        }
        if (paramValue < 0) {
            String ret2 = String.valueOf(Math.abs(paramValue));
            return String.format((String) getResources().getText(android.R.string.resolver_work_tab_accessibility), ret2);
        }
        String ret3 = String.valueOf(Math.abs(paramValue));
        return String.format((String) getResources().getText(17041716), ret3);
    }

    private void setValuesInCamera() {
        int[] vals = {this.mParam1, this.mParam2, this.mParam3, this.mParam4, this.mParam5, this.mParam6, this.mParam7, this.mParam8};
        LVGEffectValueController.getInstance().setParamValues(vals);
        LVGEffectValueController.getInstance().applyGammaTable();
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedMainDialNext() {
        return pushedDownKey();
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedMainDialPrev() {
        return pushedUpKey();
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedSubDialNext() {
        return pushedRightKey();
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedSubDialPrev() {
        return pushedLeftKey();
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedCenterKey() {
        Log.d("", "LVG INSIDE pushedCenterKey");
        openPreviousMenu();
        return 1;
    }

    private void deinitialize() {
        Log.d("", "LVG INSIDE deinitialize");
        removeListeners();
        this.mParamBox1 = null;
        this.mParamBox2 = null;
        this.mParamBox3 = null;
        this.mParamBox4 = null;
        this.mParamBox5 = null;
        this.mParamBox6 = null;
        this.mParamBox7 = null;
        this.mParamBox8 = null;
        this.lastPos = 0;
        this.mParamName = null;
        this.mGaugeBarProgressText = null;
        this.mParamText1 = null;
        this.mParamText2 = null;
        this.mParamText3 = null;
        this.mParamText4 = null;
        this.mParamText5 = null;
        this.mParamText6 = null;
        this.mParamText7 = null;
        this.mParamText8 = null;
        this.mGroup1RightArrow = null;
        this.mGroup2LeftArrow = null;
        this.mGroup2RightArrow = null;
        this.mGroup3LeftArrow = null;
        this.mIsMenuPushed = false;
        this.mGaugeBarSource = null;
        this.mPreset = 0;
        this.sup_params = null;
        this.emptyParams = null;
    }

    private void setSelection(int pos) {
        Log.d("", "LVG INSIDE setSelection; pos = " + pos);
        switch (pos) {
            case 1:
                clearFocusFromAll();
                this.mParamBox1.setSelected(true);
                break;
            case 2:
                clearFocusFromAll();
                this.mParamBox2.setSelected(true);
                break;
            case 3:
                clearFocusFromAll();
                this.mParamBox3.setSelected(true);
                break;
            case 4:
                clearFocusFromAll();
                this.mParamBox4.setSelected(true);
                break;
            case 5:
                clearFocusFromAll();
                this.mParamBox5.setSelected(true);
                break;
            case 6:
                clearFocusFromAll();
                this.mParamBox6.setSelected(true);
                break;
            case 7:
                clearFocusFromAll();
                this.mParamBox7.setSelected(true);
                break;
            case 8:
                clearFocusFromAll();
                this.mParamBox8.setSelected(true);
                break;
        }
        this.lastPos = pos;
        changeGaugeBar(this.lastPos);
    }

    private void clearFocusFromAll() {
        this.mParamBox1.setSelected(false);
        this.mParamBox2.setSelected(false);
        this.mParamBox3.setSelected(false);
        this.mParamBox4.setSelected(false);
        this.mParamBox5.setSelected(false);
        this.mParamBox6.setSelected(false);
        this.mParamBox7.setSelected(false);
        this.mParamBox8.setSelected(false);
    }

    private void changeGaugeBar(int pos) {
        switch (pos) {
            case 1:
                this.mGaugeBarSource.setImageResource(R.drawable.p_16_dd_parts_oc_adjust_common_gauge_scale);
                this.mParameterGauge.setMax(6);
                this.mParameterGauge.setProgress(this.mParam1 + 3);
                this.mGaugeBarProgressText.setText(getSignedValue(this.mParam1));
                this.mParamName.setText(getResources().getString(R.string.STRID_AMC_STR_01054));
                return;
            case 2:
                this.mGaugeBarSource.setImageResource(R.drawable.p_16_dd_parts_oc_adjust_common_gauge_scale);
                this.mParameterGauge.setMax(14);
                this.mParameterGauge.setProgress(this.mParam2 + 7);
                this.mGaugeBarProgressText.setText(getSignedValue(this.mParam2));
                this.mParamName.setText(getResources().getString(R.string.STRID_AMC_STR_01055));
                return;
            case 3:
                this.mGaugeBarSource.setImageResource(R.drawable.p_16_dd_parts_oc_adjust_common_gauge_scale);
                this.mParameterGauge.setMax(14);
                this.mParameterGauge.setProgress(this.mParam3 + 7);
                this.mGaugeBarProgressText.setText(getSignedValue(this.mParam3));
                this.mParamName.setText(getResources().getString(R.string.STRID_AMC_STR_01056));
                return;
            case 4:
                this.mGaugeBarSource.setImageResource(R.drawable.p_16_dd_parts_lvg_param_gauge_color_temp);
                this.mParameterGauge.setMax(14);
                this.mParameterGauge.setProgress(this.mParam4 + 7);
                this.mGaugeBarProgressText.setText(getSignedValue(this.mParam4));
                this.mParamName.setText(getResources().getString(R.string.STRID_FUNC_LVG_PRESET_PARAM_COLOR_TEMP_NAME));
                return;
            case 5:
                this.mGaugeBarSource.setImageResource(R.drawable.p_16_dd_parts_lvg_param_gauge_color_tint);
                this.mParameterGauge.setMax(14);
                this.mParameterGauge.setProgress(this.mParam5 + 7);
                this.mGaugeBarProgressText.setText(getSignedValue(this.mParam5));
                this.mParamName.setText(getResources().getString(R.string.STRID_FUNC_LVG_PRESET_PARAM_COLOR_TINT_NAME));
                return;
            case 6:
                this.mGaugeBarSource.setImageResource(R.drawable.p_16_dd_parts_lvg_param_gauge_color_depth_r);
                this.mParameterGauge.setMax(14);
                this.mParameterGauge.setProgress(this.mParam6 + 7);
                this.mGaugeBarProgressText.setText(getSignedValue(this.mParam6));
                this.mParamName.setText(getResources().getString(R.string.STRID_FUNC_LVG_PRESET_PARAM_COLOR_DEPTH_R_NAME));
                return;
            case 7:
                this.mGaugeBarSource.setImageResource(R.drawable.p_16_dd_parts_lvg_param_gauge_color_depth_g);
                this.mParameterGauge.setMax(14);
                this.mParameterGauge.setProgress(this.mParam7 + 7);
                this.mGaugeBarProgressText.setText(getSignedValue(this.mParam7));
                this.mParamName.setText(getResources().getString(R.string.STRID_FUNC_LVG_PRESET_PARAM_COLOR_DEPTH_G_NAME));
                return;
            case 8:
                this.mGaugeBarSource.setImageResource(R.drawable.p_16_dd_parts_lvg_param_gauge_color_depth_b);
                this.mParameterGauge.setMax(14);
                this.mParameterGauge.setProgress(this.mParam8 + 7);
                this.mGaugeBarProgressText.setText(getSignedValue(this.mParam8));
                this.mParamName.setText(getResources().getString(R.string.STRID_FUNC_LVG_PRESET_PARAM_COLOR_DEPTH_B_NAME));
                return;
            default:
                return;
        }
    }

    private void incrementParameterValue() {
        switch (this.lastPos) {
            case 1:
                Log.e("", "param 1 case");
                this.mParam1++;
                this.mGaugeBarProgressText.setText(getSignedValue(this.mParam1));
                this.mParamText1.setText(getSignedValue(this.mParam1));
                return;
            case 2:
                Log.e("", "param 2 case");
                this.mParam2++;
                this.mGaugeBarProgressText.setText(getSignedValue(this.mParam2));
                this.mParamText2.setText(getSignedValue(this.mParam2));
                return;
            case 3:
                Log.e("", "param 3 case");
                this.mParam3++;
                this.mGaugeBarProgressText.setText(getSignedValue(this.mParam3));
                this.mParamText3.setText(getSignedValue(this.mParam3));
                return;
            case 4:
                Log.e("", "param 4 case");
                this.mParam4++;
                this.mGaugeBarProgressText.setText(getSignedValue(this.mParam4));
                this.mParamText4.setText(getSignedValue(this.mParam4));
                return;
            case 5:
                Log.e("", "param 5 case");
                this.mParam5++;
                this.mGaugeBarProgressText.setText(getSignedValue(this.mParam5));
                this.mParamText5.setText(getSignedValue(this.mParam5));
                return;
            case 6:
                Log.e("", "param 6 case");
                this.mParam6++;
                this.mGaugeBarProgressText.setText(getSignedValue(this.mParam6));
                this.mParamText6.setText(getSignedValue(this.mParam6));
                return;
            case 7:
                Log.e("", "param 7 case");
                this.mParam7++;
                this.mGaugeBarProgressText.setText(getSignedValue(this.mParam7));
                this.mParamText7.setText(getSignedValue(this.mParam7));
                return;
            case 8:
                Log.e("", "param 8 case");
                this.mParam8++;
                this.mGaugeBarProgressText.setText(getSignedValue(this.mParam8));
                this.mParamText8.setText(getSignedValue(this.mParam8));
                return;
            default:
                return;
        }
    }

    private void decrementParameterValue() {
        switch (this.lastPos) {
            case 1:
                Log.e("", "param 1 case");
                this.mParam1--;
                this.mGaugeBarProgressText.setText(getSignedValue(this.mParam1));
                this.mParamText1.setText(getSignedValue(this.mParam1));
                return;
            case 2:
                Log.e("", "param 2 case");
                this.mParam2--;
                this.mGaugeBarProgressText.setText(getSignedValue(this.mParam2));
                this.mParamText2.setText(getSignedValue(this.mParam2));
                return;
            case 3:
                Log.e("", "param 3 case");
                this.mParam3--;
                this.mGaugeBarProgressText.setText(getSignedValue(this.mParam3));
                this.mParamText3.setText(getSignedValue(this.mParam3));
                return;
            case 4:
                Log.e("", "param 4 case");
                this.mParam4--;
                this.mGaugeBarProgressText.setText(getSignedValue(this.mParam4));
                this.mParamText4.setText(getSignedValue(this.mParam4));
                return;
            case 5:
                Log.e("", "param 5 case");
                this.mParam5--;
                this.mGaugeBarProgressText.setText(getSignedValue(this.mParam5));
                this.mParamText5.setText(getSignedValue(this.mParam5));
                return;
            case 6:
                Log.e("", "param 6 case");
                this.mParam6--;
                this.mGaugeBarProgressText.setText(getSignedValue(this.mParam6));
                this.mParamText6.setText(getSignedValue(this.mParam6));
                return;
            case 7:
                Log.e("", "param 7 case");
                this.mParam7--;
                this.mGaugeBarProgressText.setText(getSignedValue(this.mParam7));
                this.mParamText7.setText(getSignedValue(this.mParam7));
                return;
            case 8:
                Log.e("", "param 8 case");
                this.mParam8--;
                this.mGaugeBarProgressText.setText(getSignedValue(this.mParam8));
                this.mParamText8.setText(getSignedValue(this.mParam8));
                return;
            default:
                return;
        }
    }

    private void setParamOnCamera() {
        switch (this.lastPos) {
            case 1:
                Log.e("", "param 1 set");
                ((CameraEx.ParametersModifier) this.emptyParams.second).setContrast(this.mParam1);
                break;
            case 2:
                Log.e("", "param 2 set");
                ((CameraEx.ParametersModifier) this.emptyParams.second).setSaturation(this.mParam2);
                break;
            case 3:
                Log.e("", "param 3 set");
                if (((CameraEx.ParametersModifier) this.sup_params.second).isSharpnessGainModeSupported()) {
                    ((CameraEx.ParametersModifier) this.emptyParams.second).setSharpnessGainMode(true);
                    ((CameraEx.ParametersModifier) this.emptyParams.second).setSharpnessGain(this.mParam3);
                    break;
                }
                break;
            case 4:
                Log.e("", "param 4 set");
                if (((CameraEx.ParametersModifier) this.sup_params.second).isWhiteBalanceShiftModeSupported()) {
                    ((CameraEx.ParametersModifier) this.emptyParams.second).setWhiteBalanceShiftMode(true);
                    ((CameraEx.ParametersModifier) this.emptyParams.second).setWhiteBalanceShiftLB(this.mParam4 * (-1));
                    break;
                }
                break;
            case 5:
                Log.e("", "param 5 set");
                if (((CameraEx.ParametersModifier) this.sup_params.second).isWhiteBalanceShiftModeSupported()) {
                    ((CameraEx.ParametersModifier) this.emptyParams.second).setWhiteBalanceShiftMode(true);
                    ((CameraEx.ParametersModifier) this.emptyParams.second).setWhiteBalanceShiftCC(this.mParam5 * (-1));
                    break;
                }
                break;
            case 6:
                Log.e("", "param 6 set");
                if (((CameraEx.ParametersModifier) this.sup_params.second).getSupportedColorDepthTypes().contains(PictureEffectController.PART_COLOR_RED)) {
                    ((CameraEx.ParametersModifier) this.emptyParams.second).setColorDepth(PictureEffectController.PART_COLOR_RED, this.mParam6);
                    break;
                }
                break;
            case 7:
                Log.e("", "param 7 set");
                if (((CameraEx.ParametersModifier) this.sup_params.second).getSupportedColorDepthTypes().contains("green")) {
                    ((CameraEx.ParametersModifier) this.emptyParams.second).setColorDepth("green", this.mParam7);
                    break;
                }
                break;
            case 8:
                Log.e("", "param 8 set");
                if (((CameraEx.ParametersModifier) this.sup_params.second).getSupportedColorDepthTypes().contains(PictureEffectController.PART_COLOR_BLUE)) {
                    ((CameraEx.ParametersModifier) this.emptyParams.second).setColorDepth(PictureEffectController.PART_COLOR_BLUE, this.mParam8);
                    break;
                }
                break;
        }
        CameraSetting.getInstance().setParameters(this.emptyParams);
    }

    private void makeAllTextVisible() {
        this.mParamText1.setVisibility(0);
        this.mParamText2.setVisibility(0);
        this.mParamText3.setVisibility(0);
        this.mParamText4.setVisibility(0);
        this.mParamText5.setVisibility(0);
        this.mParamText6.setVisibility(0);
        this.mParamText7.setVisibility(0);
        this.mParamText8.setVisibility(0);
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        if (this.mController != null) {
            if (this.mController.isComeFromMenu()) {
                this.mController.setComeFromMenu(false);
                closeMenuLayout(null);
            } else {
                openPreviousMenu();
            }
        }
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachKeyHandler
    public int onDeleteKeyPushed(KeyEvent event) {
        String[] guideData = getGuideData(this.lastPos);
        this.data.putString("param_name", guideData[0]);
        this.data.putString("param_guide", guideData[1]);
        openMenuLayout("ID_COLORGRADINGPARAMETERADJUSTMENTGUIDELAYOUT", this.data);
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachKeyHandler
    public int onDeleteKeyReleased(KeyEvent event) {
        return -1;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Code restructure failed: missing block: B:3:0x0008, code lost:            return r0;     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public java.lang.String[] getGuideData(int r6) {
        /*
            r5 = this;
            r4 = 1
            r3 = 0
            r1 = 2
            java.lang.String[] r0 = new java.lang.String[r1]
            switch(r6) {
                case 1: goto L9;
                case 2: goto L23;
                case 3: goto L3e;
                case 4: goto L59;
                case 5: goto L74;
                case 6: goto L90;
                case 7: goto Lac;
                case 8: goto Lc8;
                default: goto L8;
            }
        L8:
            return r0
        L9:
            android.content.res.Resources r1 = r5.getResources()
            r2 = 2131296256(0x7f090000, float:1.8210424E38)
            java.lang.String r1 = r1.getString(r2)
            r0[r3] = r1
            android.content.res.Resources r1 = r5.getResources()
            r2 = 17040938(0x104062a, float:2.4248993E-38)
            java.lang.String r1 = r1.getString(r2)
            r0[r4] = r1
            goto L8
        L23:
            android.content.res.Resources r1 = r5.getResources()
            r2 = 2131296257(0x7f090001, float:1.8210426E38)
            java.lang.String r1 = r1.getString(r2)
            r0[r3] = r1
            android.content.res.Resources r1 = r5.getResources()
            r2 = 17040939(0x104062b, float:2.4248996E-38)
            java.lang.String r1 = r1.getString(r2)
            r0[r4] = r1
            goto L8
        L3e:
            android.content.res.Resources r1 = r5.getResources()
            r2 = 2131296258(0x7f090002, float:1.8210428E38)
            java.lang.String r1 = r1.getString(r2)
            r0[r3] = r1
            android.content.res.Resources r1 = r5.getResources()
            r2 = 17040940(0x104062c, float:2.4249E-38)
            java.lang.String r1 = r1.getString(r2)
            r0[r4] = r1
            goto L8
        L59:
            android.content.res.Resources r1 = r5.getResources()
            r2 = 2131296292(0x7f090024, float:1.8210497E38)
            java.lang.String r1 = r1.getString(r2)
            r0[r3] = r1
            android.content.res.Resources r1 = r5.getResources()
            r2 = 2131296297(0x7f090029, float:1.8210507E38)
            java.lang.String r1 = r1.getString(r2)
            r0[r4] = r1
            goto L8
        L74:
            android.content.res.Resources r1 = r5.getResources()
            r2 = 2131296293(0x7f090025, float:1.8210499E38)
            java.lang.String r1 = r1.getString(r2)
            r0[r3] = r1
            android.content.res.Resources r1 = r5.getResources()
            r2 = 2131296298(0x7f09002a, float:1.8210509E38)
            java.lang.String r1 = r1.getString(r2)
            r0[r4] = r1
            goto L8
        L90:
            android.content.res.Resources r1 = r5.getResources()
            r2 = 2131296294(0x7f090026, float:1.82105E38)
            java.lang.String r1 = r1.getString(r2)
            r0[r3] = r1
            android.content.res.Resources r1 = r5.getResources()
            r2 = 2131296299(0x7f09002b, float:1.821051E38)
            java.lang.String r1 = r1.getString(r2)
            r0[r4] = r1
            goto L8
        Lac:
            android.content.res.Resources r1 = r5.getResources()
            r2 = 2131296295(0x7f090027, float:1.8210503E38)
            java.lang.String r1 = r1.getString(r2)
            r0[r3] = r1
            android.content.res.Resources r1 = r5.getResources()
            r2 = 2131296300(0x7f09002c, float:1.8210513E38)
            java.lang.String r1 = r1.getString(r2)
            r0[r4] = r1
            goto L8
        Lc8:
            android.content.res.Resources r1 = r5.getResources()
            r2 = 2131296296(0x7f090028, float:1.8210505E38)
            java.lang.String r1 = r1.getString(r2)
            r0[r3] = r1
            android.content.res.Resources r1 = r5.getResources()
            r2 = 2131296301(0x7f09002d, float:1.8210515E38)
            java.lang.String r1 = r1.getString(r2)
            r0[r4] = r1
            goto L8
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sony.imaging.app.liveviewgrading.menu.layout.ColorGradingPresetAdjustmentScreen.getGuideData(int):java.lang.String[]");
    }
}
