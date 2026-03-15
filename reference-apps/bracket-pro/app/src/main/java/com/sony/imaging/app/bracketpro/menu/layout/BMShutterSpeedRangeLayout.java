package com.sony.imaging.app.bracketpro.menu.layout;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import com.sony.imaging.app.base.common.widget.FooterGuide;
import com.sony.imaging.app.base.common.widget.FooterGuideDataResId;
import com.sony.imaging.app.base.menu.BaseMenuService;
import com.sony.imaging.app.base.menu.HistoryItem;
import com.sony.imaging.app.base.menu.MenuDataParcelable;
import com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.base.shooting.camera.ExposureModeController;
import com.sony.imaging.app.base.shooting.trigger.ModeDialDetector;
import com.sony.imaging.app.bracketpro.BMShutterSpeed;
import com.sony.imaging.app.bracketpro.R;
import com.sony.imaging.app.bracketpro.commonUtil.BracketMasterBackUpKey;
import com.sony.imaging.app.bracketpro.menu.controller.BMExposureModeController;
import com.sony.imaging.app.bracketpro.menu.controller.BMMenuController;
import com.sony.imaging.app.bracketpro.menu.controller.BracketMasterController;
import com.sony.imaging.app.bracketpro.widget.VerticalSeekBar;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.fw.CustomizableFunction;
import com.sony.imaging.app.fw.IKeyFunction;
import com.sony.imaging.app.util.ApoWrapper;
import com.sony.imaging.app.util.BackUpUtil;
import com.sony.imaging.app.util.NotificationListener;
import com.sony.scalar.hardware.CameraEx;

/* loaded from: classes.dex */
public class BMShutterSpeedRangeLayout extends DisplayMenuItemsMenuLayout {
    private static final String STR_FORMAT_INTPLUS = "＋%1d";
    private static final String TAG = "BMShutterSpeedRangeLayout";
    private static final double trapeziumApMax = 45.0d;
    private static final double trapeziumApMin = 1.4d;
    private static final int trapeziumMaxheight = 155;
    private static final int trapeziumMaxheightThirdPointer = 135;
    private TextView GaugeCurretValue;
    private String SecondShutterSpeedValue;
    private int ShutterSpeedCounterbackup;
    String centerApertureValue;
    private String centerShutterSpeedValue;
    private String firstShutterSpeedValue1;
    private int firstValuebackup;
    boolean isShutterSpeedfile;
    BracketMasterController mController;
    private ImageView mGaugeBackground;
    private int mGaugeCurrentLevelBackup;
    private TextView mGaugetext;
    private LinearLayout.LayoutParams mLayoutParams;
    private int mLevel;
    private LinearLayout mLinearLayout;
    ShutterSpeedNotificationListener mNotify;
    int maxApertureValue;
    double maxSSValue;
    int minApertureValue;
    double minSSValue;
    BMShutterSpeedView myview;
    double secondShootXvalue;
    private int secondValuebackup;
    double thirdShootXvalue;
    int thirdShootYvalue;
    static int mbootcount_ss = 1;
    static int mbootcheck_ss = 1;
    private static boolean mShiftChanged = true;
    private static boolean isShiftChange = true;
    private View mCurrentView = null;
    private VerticalSeekBar verticalSeekBar = null;
    private FooterGuide mFooterGuide = null;
    private TextView mFirstBracketValue = null;
    private TextView mSecondBracketValue = null;
    private TextView mThirdBracketValue = null;
    private float mPointerDisplacement = 0.0f;
    private ImageView trapeziumBG = null;
    private int mGaugeCurrentLevel = 2;
    private int mGaugeNewLevelBackup = 2;
    private boolean mIsGaugeLevelChanged = false;
    private int mSecondYvalue = 0;
    private int mCenterYval = 0;
    private int mThirdYvalue = 0;
    private double mCenterXVal = 0.0d;
    private double mThirdXvalue = 0.0d;
    private double mSecondXvalue = 0.0d;
    int secondShootYvalue = 0;
    private boolean isTap = false;
    int ShutterSpeedCounter = 13;
    BMShutterSpeed ss = new BMShutterSpeed();

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        this.mController = BracketMasterController.getInstance();
        if (this.mCurrentView == null) {
            this.mCurrentView = inflater.inflate(R.layout.bm_shutterspeed_range, (ViewGroup) null);
        }
        initializeView();
        this.mCurrentView.setOnTouchListener(new View.OnTouchListener() { // from class: com.sony.imaging.app.bracketpro.menu.layout.BMShutterSpeedRangeLayout.1
            @Override // android.view.View.OnTouchListener
            public boolean onTouch(View view, MotionEvent motionEvent) {
                VerticalSeekBar.isTouchedScreen = true;
                return true;
            }
        });
        return this.mCurrentView;
    }

    public void initializeView() {
        this.mFirstBracketValue = (TextView) this.mCurrentView.findViewById(R.id.ValueA1_value);
        this.mSecondBracketValue = (TextView) this.mCurrentView.findViewById(R.id.ValueA2_value);
        this.mThirdBracketValue = (TextView) this.mCurrentView.findViewById(R.id.ValueA3_value);
        this.mLinearLayout = (LinearLayout) this.mCurrentView.findViewById(R.id.linearLayoutTrapezium);
        this.mLayoutParams = new LinearLayout.LayoutParams(AppRoot.USER_KEYCODE.MODE_DIAL_MANUAL, 222);
        this.mFooterGuide = (FooterGuide) this.mCurrentView.findViewById(R.id.footer_guide);
        this.mFooterGuide.setData(new FooterGuideDataResId(getActivity(), R.string.STRID_FOOTERGUIDE_TV_RETURN, R.string.STRID_FOOTERGUIDE_TV_RETURN_SK));
        this.trapeziumBG = (ImageView) this.mCurrentView.findViewById(R.id.imgTrapezium);
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        if (this.mNotify == null) {
            this.mNotify = new ShutterSpeedNotificationListener();
        }
        CameraNotificationManager.getInstance().setNotificationListener(this.mNotify);
        BracketMasterController.getInstance().refreshShutterSpeedValues();
        BracketMasterController.getInstance().refreshApertureValues();
        BracketMasterController.getInstance().setMaxMinShutterSpeedValue();
        BracketMasterController.getInstance().setMaxMinApertureSpeedValue();
        this.isShutterSpeedfile = true;
        this.mGaugeCurrentLevel = BackUpUtil.getInstance().getPreferenceInt(BracketMasterBackUpKey.TAG_CURRENT_SS_RANGE_BACKUP, this.mGaugeCurrentLevel);
        this.mGaugeCurrentLevelBackup = this.mGaugeCurrentLevel;
        this.ShutterSpeedCounter = BackUpUtil.getInstance().getPreferenceInt(BracketMasterBackUpKey.TAG_CURRENT_SS_COUNTER_BACKUP, this.ShutterSpeedCounter);
        this.ShutterSpeedCounterbackup = this.ShutterSpeedCounter;
        setVerticalSeekBar();
        ShutterSpeedLevelSelection();
        ApoWrapper.setApoType(ApoWrapper.APO_TYPE.SPECIAL);
        setKeyBeepPattern(0);
        if (!ModeDialDetector.hasModeDial()) {
            BMExposureModeController.getInstance().setValue(ExposureModeController.EXPOSURE_MODE, "Shutter");
        }
    }

    public void ShutterSpeedLevelSelection() {
        this.mGaugeCurrentLevel = this.verticalSeekBar.getProgress();
        saveShutterSpeedData(this.mGaugeCurrentLevel);
        ShutterSpeedTrapezium();
    }

    void getValue(int shiftT, int shiftS) {
        BackUpUtil.getInstance().setPreference(BracketMasterBackUpKey.TAG_CURRENT_Shutter_RANGE_MAIN_shiftS, Integer.valueOf(shiftS));
        BackUpUtil.getInstance().setPreference(BracketMasterBackUpKey.TAG_CURRENT_Shutter_RANGE_MAIN_shiftT, Integer.valueOf(shiftT));
    }

    public void ShutterSpeedTrapezium() {
        Log.d(TAG, "AAAAAAA>>>>> ShutterSpeedTrapezium called ");
        this.mController.ShutterSpeedValues(this.mGaugeCurrentLevel);
        BMShutterSpeed.mGaureSS = this.mGaugeCurrentLevel;
        this.centerApertureValue = this.ss.getCenterApertureValue();
        this.centerShutterSpeedValue = this.ss.getCenterShutterSpeedValue();
        this.firstShutterSpeedValue1 = this.ss.getFirstShutterSpeedValue1();
        this.SecondShutterSpeedValue = this.ss.getSecondShutterSpeedValue();
        this.maxApertureValue = this.ss.getMaxApertureValue();
        this.minApertureValue = this.ss.getMinApertureValue();
        this.maxSSValue = this.ss.getMaxSSValue();
        this.minSSValue = this.ss.getMinSSValue();
        this.mCenterYval = this.ss.getmCenterYval();
        this.mCenterXVal = this.ss.getmCenterXVal();
        this.mThirdXvalue = this.ss.getmFirstXvalue();
        this.mSecondXvalue = this.ss.getmSecondXvalue();
        this.mSecondYvalue = this.ss.getmFirstYvalue();
        this.mThirdYvalue = this.ss.getmThirdYvalue();
        setShutterSpeedText(this.centerShutterSpeedValue, this.firstShutterSpeedValue1, this.SecondShutterSpeedValue);
        if (this.centerApertureValue == null || this.centerApertureValue.equalsIgnoreCase("F--")) {
            this.mLinearLayout.removeView(this.myview);
            this.mLinearLayout.setVisibility(4);
            return;
        }
        if (Double.parseDouble(this.mController.getCurrentApertureAvailMin().substring(1)) < trapeziumApMin || Double.parseDouble(this.mController.getCurrentApertureAvailMax().substring(1)) > trapeziumApMax) {
            Log.d(TAG, "apertureTrapezium unsupported lens ");
            this.mLinearLayout.removeView(this.myview);
            this.mLinearLayout.setVisibility(4);
            return;
        }
        this.mLinearLayout.setVisibility(0);
        Activity activity = getActivity();
        Context ctx = activity.getApplicationContext();
        if (this.myview == null) {
            this.myview = new BMShutterSpeedView(ctx);
            this.mLinearLayout.addView(this.myview, this.mLayoutParams);
        } else {
            this.mLinearLayout.removeView(this.myview);
            this.mLinearLayout.addView(this.myview, this.mLayoutParams);
        }
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IConvertibleKeyHandler
    public int onConvertedKeyDown(KeyEvent event, IKeyFunction func) {
        int result = super.onConvertedKeyDown(event, func);
        CustomizableFunction customFunc = (CustomizableFunction) func;
        switch (customFunc) {
            case MainNext:
                CameraSetting.getInstance().incrementShutterSpeed();
                ShutterSpeedLevelSelection();
                return 1;
            case MainPrev:
                CameraSetting.getInstance().decrementShutterSpeed();
                ShutterSpeedLevelSelection();
                return 1;
            case Guide:
            case DoNothing:
                return -1;
            default:
                return result;
        }
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyUp(int keyCode, KeyEvent event) {
        this.verticalSeekBar.setSelected(true);
        if (VerticalSeekBar.isTouchedScreen) {
            if (event.getScanCode() == 103) {
                this.verticalSeekBar.moveUp();
                ShutterSpeedLevelSelection();
                return 1;
            }
            if (event.getScanCode() == 108) {
                this.verticalSeekBar.moveDown();
                ShutterSpeedLevelSelection();
                return 1;
            }
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyDown(int keyCode, KeyEvent event) {
        this.verticalSeekBar.setSelected(true);
        VerticalSeekBar.isTouchedScreen = false;
        int code = event.getScanCode();
        super.onKeyDown(keyCode, event);
        switch (code) {
            case 103:
                this.verticalSeekBar.moveUp();
                ShutterSpeedLevelSelection();
                return 1;
            case AppRoot.USER_KEYCODE.LEFT /* 105 */:
            case AppRoot.USER_KEYCODE.RIGHT /* 106 */:
            case AppRoot.USER_KEYCODE.SK2 /* 513 */:
                return -1;
            case AppRoot.USER_KEYCODE.DOWN /* 108 */:
                this.verticalSeekBar.moveDown();
                ShutterSpeedLevelSelection();
                return 1;
            case AppRoot.USER_KEYCODE.SK1 /* 229 */:
                int retValue = pushedMenuKey();
                return retValue;
            case AppRoot.USER_KEYCODE.CENTER /* 232 */:
                saveShutterSpeedData(this.mGaugeCurrentLevel);
                closeMenuLayout(null);
                return 1;
            case AppRoot.USER_KEYCODE.S1_ON /* 516 */:
            case AppRoot.USER_KEYCODE.S1_OFF /* 772 */:
                saveShutterSpeedData(this.mGaugeCurrentLevel);
                return 0;
            case AppRoot.USER_KEYCODE.S2_ON /* 518 */:
            case AppRoot.USER_KEYCODE.S2_OFF /* 774 */:
                saveShutterSpeedData(this.mGaugeCurrentLevel);
                return 0;
            case AppRoot.USER_KEYCODE.SHUTTLE_RIGHT /* 522 */:
            case AppRoot.USER_KEYCODE.DIAL1_LEFT /* 526 */:
            case AppRoot.USER_KEYCODE.DIAL2_LEFT /* 529 */:
                CameraSetting.getInstance().decrementShutterSpeed();
                ShutterSpeedLevelSelection();
                return 0;
            case AppRoot.USER_KEYCODE.SHUTTLE_LEFT /* 523 */:
            case AppRoot.USER_KEYCODE.DIAL1_RIGHT /* 525 */:
            case AppRoot.USER_KEYCODE.DIAL2_RIGHT /* 528 */:
                CameraSetting.getInstance().incrementShutterSpeed();
                ShutterSpeedLevelSelection();
                return 0;
            case AppRoot.USER_KEYCODE.LENS_ATTACH /* 530 */:
            case AppRoot.USER_KEYCODE.LENS_DETACH /* 786 */:
                return 0;
            case AppRoot.USER_KEYCODE.AEL /* 532 */:
                return 0;
            case AppRoot.USER_KEYCODE.MODE_DIAL_CHANGED /* 572 */:
                BMMenuController.getInstance().setRangeStatus(true);
                return 0;
            default:
                return 0;
        }
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMovieRecKey() {
        saveShutterSpeedData(this.mGaugeCurrentLevel);
        closeLayout();
        return super.pushedMovieRecKey();
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedPlayBackKey() {
        closeLayout();
        return super.pushedPlayBackKey();
    }

    public void saveShutterSpeedData(int currentlevel) {
        BackUpUtil.getInstance().setPreference(BracketMasterBackUpKey.TAG_CURRENT_BRACKET_TYPE, BMMenuController.ShutterSpeedBracket);
        BackUpUtil.getInstance().setPreference(BracketMasterBackUpKey.TAG_CURRENT_SS_RANGE_BACKUP, Integer.valueOf(currentlevel));
        BackUpUtil.getInstance().setPreference(BracketMasterBackUpKey.TAG_CURRENT_SS_COUNTER_BACKUP, Integer.valueOf(this.ShutterSpeedCounter));
        BackUpUtil.getInstance().setPreference(BracketMasterBackUpKey.TAG_CURRENT_SHUTTER_RANGE, Integer.valueOf(currentlevel));
        BackUpUtil.getInstance().setPreference(BracketMasterBackUpKey.TAG_CURRENT_SHUTTER_RANGE_MAIN, Integer.valueOf(currentlevel));
    }

    public void setShutterSpeedText(String SSCentreVal, String SSVal1, String SSVal2) {
        this.mFirstBracketValue.setText(SSCentreVal);
        this.mSecondBracketValue.setText(SSVal1);
        this.mThirdBracketValue.setText(SSVal2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class BMShutterSpeedView extends View implements Drawable.Callback {
        double mCenterXVal1;

        public BMShutterSpeedView(Context context) {
            super(context);
        }

        @Override // android.view.View
        protected void onDraw(Canvas canvas) {
            int minYValue;
            int Y2Value;
            int[] aryForRectXValues = {0, 450, 52};
            BMShutterSpeedRangeLayout.this.drawThirdshootingPosition();
            BMShutterSpeedRangeLayout.this.drawSecondshootingPosition();
            if (BMShutterSpeedRangeLayout.this.mCenterXVal <= 217.0d) {
                this.mCenterXVal1 = BMShutterSpeedRangeLayout.this.mController.getxp(BMShutterSpeedRangeLayout.this.mCenterXVal, BMShutterSpeedRangeLayout.this.mCenterYval);
            } else if (BMShutterSpeedRangeLayout.this.mCenterXVal >= BMShutterSpeedRangeLayout.this.secondShootXvalue) {
                this.mCenterXVal1 = BMShutterSpeedRangeLayout.this.mController.getxp(BMShutterSpeedRangeLayout.this.secondShootXvalue, BMShutterSpeedRangeLayout.this.secondShootYvalue) - 2.0f;
            } else if (BMShutterSpeedRangeLayout.this.mCenterXVal < 441.0d) {
                this.mCenterXVal1 = BMShutterSpeedRangeLayout.this.mController.getxpn(BMShutterSpeedRangeLayout.this.mCenterXVal, BMShutterSpeedRangeLayout.this.mCenterYval);
            } else {
                this.mCenterXVal1 = BMShutterSpeedRangeLayout.this.mController.getxp(BMShutterSpeedRangeLayout.this.secondShootXvalue, BMShutterSpeedRangeLayout.this.secondShootYvalue) - 2.0f;
            }
            int centerYValue = BMShutterSpeedRangeLayout.this.mCenterYval;
            Paint paintRectangle = new Paint();
            paintRectangle.setColor(Color.parseColor("#99804200"));
            paintRectangle.setStyle(Paint.Style.STROKE);
            paintRectangle.setStrokeWidth(1.0f);
            paintRectangle.setStyle(Paint.Style.FILL);
            Path pathToDrawRectangle = new Path();
            pathToDrawRectangle.moveTo(BMShutterSpeedRangeLayout.this.mController.getxp(BMShutterSpeedRangeLayout.this.thirdShootXvalue, BMShutterSpeedRangeLayout.this.maxApertureValue) - 1.0f, BMShutterSpeedRangeLayout.this.maxApertureValue);
            pathToDrawRectangle.lineTo(BMShutterSpeedRangeLayout.this.mController.getxp(BMShutterSpeedRangeLayout.this.thirdShootXvalue, BMShutterSpeedRangeLayout.this.minApertureValue), BMShutterSpeedRangeLayout.this.minApertureValue);
            pathToDrawRectangle.lineTo(BMShutterSpeedRangeLayout.this.mController.getxp(BMShutterSpeedRangeLayout.this.secondShootXvalue, BMShutterSpeedRangeLayout.this.minApertureValue), BMShutterSpeedRangeLayout.this.minApertureValue);
            pathToDrawRectangle.lineTo(BMShutterSpeedRangeLayout.this.mController.getxp(BMShutterSpeedRangeLayout.this.secondShootXvalue, BMShutterSpeedRangeLayout.this.maxApertureValue) - 3.0f, BMShutterSpeedRangeLayout.this.maxApertureValue);
            pathToDrawRectangle.close();
            canvas.drawPath(pathToDrawRectangle, paintRectangle);
            for (int i = 0; i <= 1; i++) {
                if (i == 0) {
                    minYValue = BMShutterSpeedRangeLayout.this.minApertureValue;
                    Y2Value = 210;
                } else {
                    minYValue = BMShutterSpeedRangeLayout.this.maxApertureValue;
                    Y2Value = 30;
                }
                Paint paintGrayedRectangle = new Paint();
                paintGrayedRectangle.setColor(Color.parseColor("#66252525"));
                paintGrayedRectangle.setStyle(Paint.Style.STROKE);
                paintGrayedRectangle.setStrokeWidth(3.0f);
                paintGrayedRectangle.setStyle(Paint.Style.FILL);
                Path pathToDrawGrayedRectangle = new Path();
                pathToDrawGrayedRectangle.moveTo(aryForRectXValues[0] + BMShutterSpeedRangeLayout.this.mController.getXposition(Y2Value), Y2Value);
                pathToDrawGrayedRectangle.lineTo(aryForRectXValues[0] + BMShutterSpeedRangeLayout.this.mController.getXposition(minYValue), minYValue);
                pathToDrawGrayedRectangle.lineTo(aryForRectXValues[1] - BMShutterSpeedRangeLayout.this.mController.getXposition(minYValue), minYValue);
                pathToDrawGrayedRectangle.lineTo(aryForRectXValues[1] - BMShutterSpeedRangeLayout.this.mController.getXposition(Y2Value), Y2Value);
                pathToDrawGrayedRectangle.close();
                canvas.drawPath(pathToDrawGrayedRectangle, paintGrayedRectangle);
            }
            Paint paintRectangleB = new Paint();
            paintRectangleB.setStrokeWidth(1.0f);
            paintRectangleB.setStyle(Paint.Style.STROKE);
            paintRectangleB.setColor(Color.parseColor("#FFFF9500"));
            Path pathToDrawRectangleB = new Path();
            pathToDrawRectangleB.moveTo(BMShutterSpeedRangeLayout.this.mController.getxp(BMShutterSpeedRangeLayout.this.thirdShootXvalue, BMShutterSpeedRangeLayout.this.maxApertureValue) - 1.0f, BMShutterSpeedRangeLayout.this.maxApertureValue);
            pathToDrawRectangleB.lineTo(BMShutterSpeedRangeLayout.this.mController.getxp(BMShutterSpeedRangeLayout.this.thirdShootXvalue, BMShutterSpeedRangeLayout.this.minApertureValue), BMShutterSpeedRangeLayout.this.minApertureValue);
            pathToDrawRectangleB.lineTo(BMShutterSpeedRangeLayout.this.mController.getxp(BMShutterSpeedRangeLayout.this.secondShootXvalue, BMShutterSpeedRangeLayout.this.minApertureValue), BMShutterSpeedRangeLayout.this.minApertureValue);
            pathToDrawRectangleB.lineTo((float) (BMShutterSpeedRangeLayout.this.mController.getxp(BMShutterSpeedRangeLayout.this.secondShootXvalue, BMShutterSpeedRangeLayout.this.maxApertureValue) - 3.5d), BMShutterSpeedRangeLayout.this.maxApertureValue);
            pathToDrawRectangleB.close();
            canvas.drawPath(pathToDrawRectangleB, paintRectangleB);
            Paint paintLine = new Paint();
            paintLine.setColor(Color.parseColor("#FFFF9500"));
            paintLine.setStyle(Paint.Style.STROKE);
            paintLine.setStrokeWidth(2.0f);
            if (BMShutterSpeedRangeLayout.this.mCenterYval > BMShutterSpeedRangeLayout.trapeziumMaxheight) {
                BMShutterSpeedRangeLayout.this.mCenterYval = BMShutterSpeedRangeLayout.trapeziumMaxheight;
            }
            canvas.drawLine(BMShutterSpeedRangeLayout.this.mController.getxp(BMShutterSpeedRangeLayout.this.secondShootXvalue, BMShutterSpeedRangeLayout.this.secondShootYvalue), (BMShutterSpeedRangeLayout.this.secondShootYvalue - 3) + 5, (float) this.mCenterXVal1, (BMShutterSpeedRangeLayout.this.mCenterYval - 3) + 5, paintLine);
            canvas.drawLine((float) this.mCenterXVal1, (BMShutterSpeedRangeLayout.this.mCenterYval - 3) + 5, BMShutterSpeedRangeLayout.this.mController.getxp(BMShutterSpeedRangeLayout.this.thirdShootXvalue, BMShutterSpeedRangeLayout.this.thirdShootYvalue) - 1.0f, (BMShutterSpeedRangeLayout.this.thirdShootYvalue - 3) + 5, paintLine);
            Paint paintImage = new Paint();
            Bitmap positionLevel3 = BitmapFactory.decodeResource(getResources(), R.drawable.p_16_dd_parts_bm_positonlabel_3);
            canvas.drawBitmap(positionLevel3, BMShutterSpeedRangeLayout.this.mController.getxp(BMShutterSpeedRangeLayout.this.thirdShootXvalue, BMShutterSpeedRangeLayout.this.thirdShootYvalue) - 29, BMShutterSpeedRangeLayout.this.thirdShootYvalue - 52, paintImage);
            if (centerYValue > BMShutterSpeedRangeLayout.trapeziumMaxheight) {
                centerYValue = BMShutterSpeedRangeLayout.trapeziumMaxheight;
            }
            Bitmap positionLevel1 = BitmapFactory.decodeResource(getResources(), R.drawable.p_16_dd_parts_bm_positonlabel_1);
            canvas.drawBitmap(positionLevel1, ((float) this.mCenterXVal1) - 27, centerYValue - 53, paintImage);
            Bitmap positionLevel2 = BitmapFactory.decodeResource(getResources(), R.drawable.p_16_dd_parts_bm_positonlabel_2);
            canvas.drawBitmap(positionLevel2, BMShutterSpeedRangeLayout.this.mController.getxp(BMShutterSpeedRangeLayout.this.secondShootXvalue, BMShutterSpeedRangeLayout.this.secondShootYvalue) - 29, BMShutterSpeedRangeLayout.this.secondShootYvalue - 53, paintImage);
        }
    }

    public void drawSecondshootingPosition() {
        if (this.minApertureValue > trapeziumMaxheight) {
            this.minApertureValue = trapeziumMaxheight;
        }
        if (this.minApertureValue <= this.mSecondYvalue) {
            this.secondShootYvalue = this.minApertureValue;
            this.secondShootXvalue = checkTrapeziumXvalue(this.mThirdXvalue);
        } else {
            this.secondShootYvalue = this.mSecondYvalue;
            this.secondShootXvalue = checkTrapeziumXvalue(this.mThirdXvalue);
        }
    }

    private double checkTrapeziumXvalue(double mXvalue) {
        double mTrapWidth = this.trapeziumBG.getWidth();
        CameraEx.ShutterSpeedInfo infoSS = CameraSetting.getInstance().getShutterSpeedInfo();
        int mShutterMax = infoSS.currentAvailableMax_d;
        if (mShutterMax == 8000) {
            if (mXvalue >= mTrapWidth - (mTrapWidth / 20.0d)) {
                double trapXValue = mTrapWidth - (mTrapWidth / 20.0d);
                return trapXValue;
            }
            return mXvalue;
        }
        if (mShutterMax >= 12800) {
            if (mXvalue >= mTrapWidth) {
                return mTrapWidth;
            }
            return mXvalue;
        }
        if (mShutterMax != 4000) {
            return mXvalue;
        }
        if (mXvalue >= mTrapWidth - (mTrapWidth / 16.0d)) {
            double trapXValue2 = mTrapWidth - (mTrapWidth / 16.0d);
            return trapXValue2;
        }
        return mXvalue;
    }

    public void drawThirdshootingPosition() {
        if (this.mThirdYvalue > trapeziumMaxheightThirdPointer) {
            this.mThirdYvalue = trapeziumMaxheightThirdPointer;
        }
        if (this.maxApertureValue >= this.mThirdYvalue) {
            this.thirdShootYvalue = this.maxApertureValue;
            this.thirdShootXvalue = checkTrapeziumXvalue(this.mSecondXvalue);
        } else {
            this.thirdShootYvalue = this.mThirdYvalue;
            this.thirdShootXvalue = checkTrapeziumXvalue(this.mSecondXvalue);
        }
    }

    public void drawThird() {
        if (1.0d >= this.thirdShootXvalue) {
            int shift = (int) (this.ss.getmCenterXVal() / 8.65d);
            this.thirdShootYvalue = this.mController.getSecondApertureTrapeziumValue(this.centerApertureValue, shift);
            this.thirdShootXvalue = this.mController.getShutterSecondValuesForTrapezium(this.centerShutterSpeedValue, shift);
        } else {
            this.thirdShootYvalue = this.mThirdYvalue;
            this.thirdShootXvalue = this.mSecondXvalue;
        }
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        this.isShutterSpeedfile = false;
        freeResources();
        VerticalSeekBar.isTouchedScreen = false;
        super.onPause();
    }

    private void freeResources() {
        this.mController = null;
        this.mLayoutParams = null;
        this.mCurrentView = null;
        this.mLinearLayout = null;
        this.myview = null;
        this.verticalSeekBar = null;
        this.mGaugeBackground = null;
        this.mGaugetext = null;
        this.mFooterGuide = null;
        this.mFirstBracketValue = null;
        this.mSecondBracketValue = null;
        this.mThirdBracketValue = null;
        this.GaugeCurretValue = null;
        this.firstShutterSpeedValue1 = null;
        this.centerShutterSpeedValue = null;
        this.SecondShutterSpeedValue = null;
        if (this.mNotify != null) {
            CameraNotificationManager.getInstance().removeNotificationListener(this.mNotify);
            this.mNotify = null;
        }
    }

    private void removeBitmap(ImageView v) {
        Bitmap b = null;
        if (v != null) {
            b = v.getDrawingCache();
        }
        if (b != null) {
            b.recycle();
            v.setImageResource(0);
        }
    }

    /* loaded from: classes.dex */
    public class ShutterSpeedNotificationListener implements NotificationListener {
        private final String[] tags = {CameraNotificationManager.SHUTTER_SPEED, "Aperture"};

        public ShutterSpeedNotificationListener() {
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public void onNotify(String tag) {
            BracketMasterController.getInstance().refreshApertureValues();
            BracketMasterController.getInstance().refreshShutterSpeedValues();
            if ((tag.equalsIgnoreCase(CameraNotificationManager.SHUTTER_SPEED) || tag.equalsIgnoreCase("Aperture")) && BMShutterSpeedRangeLayout.this.isShutterSpeedfile) {
                BMShutterSpeedRangeLayout.this.ShutterSpeedLevelSelection();
            }
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public String[] getTags() {
            return this.tags;
        }
    }

    float mathe(double y3, double y4, double x, double x3, double x4) {
        double y2 = this.minApertureValue;
        double y1 = this.maxApertureValue;
        return (float) (y1 + (((((x4 - x3) * (y1 - y3)) - ((y4 - y3) * (x - x3))) / (((y4 - y3) * (x - x)) - ((x4 - x3) * (y2 - y1)))) * (y2 - y1)));
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public void onDestroyView() {
        if (this.mNotify != null) {
            CameraNotificationManager.getInstance().removeNotificationListener(this.mNotify);
            this.mNotify = null;
        }
        super.onDestroyView();
    }

    private void setVerticalSeekBar() {
        this.verticalSeekBar = (VerticalSeekBar) this.mCurrentView.findViewById(R.id.vertical_Seekbar);
        this.verticalSeekBar.setProgressDrawable(null);
        this.mGaugetext = (TextView) this.mCurrentView.findViewById(R.id.vertical_sb_progresstext);
        this.verticalSeekBar.setMax(14);
        this.verticalSeekBar.setProgress(this.mGaugeCurrentLevel);
        getInformationText(this.mGaugeCurrentLevel);
        this.verticalSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() { // from class: com.sony.imaging.app.bracketpro.menu.layout.BMShutterSpeedRangeLayout.2
            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                BMShutterSpeedRangeLayout.this.getInformationText(progress);
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStartTrackingTouch(SeekBar arg0) {
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStopTrackingTouch(SeekBar arg0) {
            }
        });
    }

    protected String getInformationText(int level) {
        this.mLevel = level;
        String ret = this.mController.rangeValues[14 - level];
        this.mGaugetext.setText(ret);
        return ret;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMenuKey() {
        if (mbootcheck_ss == 1) {
            mbootcount_ss = 1;
        }
        saveShutterSpeedData(this.mGaugeCurrentLevelBackup);
        BackUpUtil.getInstance().setPreference(BracketMasterBackUpKey.TAG_CURRENT_SS_COUNTER_BACKUP, Integer.valueOf(this.ShutterSpeedCounterbackup));
        mShiftChanged = isShiftChange;
        this.ss.setShiftSecond(this.secondValuebackup);
        this.ss.setShiftThird(this.firstValuebackup);
        openPreviousMenu();
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout
    protected void onAFMFSwitchChanged() {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout
    public void openPreviousMenu() {
        MenuDataParcelable parcelable = (MenuDataParcelable) this.data.getParcelable(MenuDataParcelable.KEY);
        BaseMenuService service = new BaseMenuService(parcelable.getMenuService());
        HistoryItem item = service.popMenuHistory();
        if (item == null) {
            closeMenuLayout(null);
            return;
        }
        String nextLayoutId = item.layoutId;
        String myLayoutId = getMenuLayoutID();
        service.setMenuItemId(item.itemId);
        if (nextLayoutId.equals("ID_EXPOSUREMODESUBMENULAYOUT")) {
            nextLayoutId = "ID_BRACKETMASTERSUBMENU";
            service.setMenuItemId("ApplicationTop");
        }
        parcelable.setMenuData(parcelable.getMenuDataFile(), "back", myLayoutId, service.getMenuItemExecType(item.itemId), nextLayoutId, service);
        this.data.putParcelable(MenuDataParcelable.KEY, parcelable);
        if (myLayoutId.equals(nextLayoutId)) {
            updateLayout();
        } else {
            openMenuLayout(nextLayoutId, this.data);
        }
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachKeyHandler
    public int onLensAttached(KeyEvent event) {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachKeyHandler
    public int onLensDetached(KeyEvent event) {
        return -1;
    }
}
