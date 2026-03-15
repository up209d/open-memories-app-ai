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
import com.sony.imaging.app.base.common.MediaNotificationManager;
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
import com.sony.imaging.app.bracketpro.BMAperture;
import com.sony.imaging.app.bracketpro.R;
import com.sony.imaging.app.bracketpro.commonUtil.BracketMasterBackUpKey;
import com.sony.imaging.app.bracketpro.commonUtil.BracketMasterUtil;
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

/* loaded from: classes.dex */
public class BMApertureRangeLayout extends DisplayMenuItemsMenuLayout {
    private static final String STR_FORMAT_INTPLUS = "＋%1d";
    private static final int endLeft = 0;
    private static final int endRight = 10;
    private static boolean isShiftChange = false;
    private static final int mGaugeStep1 = 1;
    private static final int mGaugeStep2 = 2;
    private static final int mGaugeStep3 = 3;
    private static final int mGaugeStep4 = 4;
    private static final int mGaugeStep5 = 5;
    private static final int mGaugeStep6 = 6;
    private static final int mGaugeStep7 = 7;
    private static final int mGaugeStep8 = 8;
    private static final int mGaugeStep9 = 9;
    private static final int trapMaxValue = 430;
    private static final double trapeziumApMax = 45.0d;
    private static final double trapeziumApMin = 1.4d;
    private TextView GaugeCurretValue;
    private String SecondApertureValue;
    BMAperture aa;
    private int apertureCounterbackup;
    private String centerApertureValue;
    private String firstApertureValue;
    private int firstValuebackup;
    boolean isAperturefile;
    BracketMasterController mController;
    private ImageView mGaugeBackground;
    private int mGaugeCurrentLevelBackup;
    private TextView mGaugetext;
    private LinearLayout.LayoutParams mLayoutParams;
    private int mLevel;
    private LinearLayout mLinearLayout;
    private ImageView mMinus;
    private ApertureNotificationListener mNotify;
    private ImageView mPlus;
    private int maxApertureValue;
    private boolean mbMounted;
    private int minApertureValue;
    BMView myview;
    private int secondValuebackup;
    double thirdShootXvalue;
    double thirdShootXvalue1;
    int thirdShootYvalue;
    private static final String TAG = BMApertureRangeLayout.class.getName();
    private static boolean ShiftChanged = true;
    static int mbootcount = 1;
    static int mbootcheck = 1;
    private View mCurrentView = null;
    private VerticalSeekBar verticalSeekBar = null;
    private FooterGuide mFooterGuide = null;
    private ImageView mGaugeBar = null;
    private TextView mFirstBracketValue = null;
    private TextView mSecondBracketValue = null;
    private TextView mThirdBracketValue = null;
    private float mPointerDisplacement = 0.0f;
    private int mGaugeNewLevel = 0;
    private int mGaugeNewLevelBackup = 0;
    private int mGaugeCurrentLevel = 2;
    private int mFirstYvalue = 0;
    private int mCenterYval = 0;
    private int mSecondYvalue = 0;
    private double mCenterXVal = 0.0d;
    private double mFirstXvalue = 0.0d;
    private double mSecondXvalue = 0.0d;
    private String centerSSValue = "";
    int SecondShootYvalue = 0;
    int SecondShootYvalue2 = 0;
    double SecondShootXvalue = 0.0d;
    double SecondShootXvalue1 = 0.0d;
    int apertureCounter = 7;
    private boolean isTap = false;
    private MediaNotificationManager mMediaNotifier = null;
    private final String[] tags = {MediaNotificationManager.TAG_MEDIA_STATUS_CHANGE};

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        Log.d(TAG, "ApertureRangeScreen : onCreateView()");
        if (this.mCurrentView == null) {
            this.mCurrentView = inflater.inflate(R.layout.bm_aperture_range, (ViewGroup) null);
        }
        this.mController = BracketMasterController.getInstance();
        this.aa = new BMAperture();
        this.mFirstBracketValue = (TextView) this.mCurrentView.findViewById(R.id.ValueA1_value);
        this.mSecondBracketValue = (TextView) this.mCurrentView.findViewById(R.id.ValueA2_value);
        this.mThirdBracketValue = (TextView) this.mCurrentView.findViewById(R.id.ValueA3_value);
        this.mLinearLayout = (LinearLayout) this.mCurrentView.findViewById(R.id.linearLayoutTrapezium);
        this.mLayoutParams = new LinearLayout.LayoutParams(451, 180);
        BracketMasterController.getInstance().refreshApertureValues();
        this.mFooterGuide = (FooterGuide) this.mCurrentView.findViewById(R.id.footer_guide);
        if (BracketMasterUtil.isIRISRingEnabledDevice()) {
            this.mFooterGuide.setData(new FooterGuideDataResId(getActivity(), android.R.string.autofill_save_no, android.R.string.httpErrorFailedSslHandshake));
        } else {
            this.mFooterGuide.setData(new FooterGuideDataResId(getActivity(), R.string.STRID_FOOTERGUIDE_AV_RETURN, R.string.STRID_FOOTERGUIDE_AV_RETURN_SK));
        }
        this.mCurrentView.setOnTouchListener(new View.OnTouchListener() { // from class: com.sony.imaging.app.bracketpro.menu.layout.BMApertureRangeLayout.1
            @Override // android.view.View.OnTouchListener
            public boolean onTouch(View view, MotionEvent motionEvent) {
                VerticalSeekBar.isTouchedScreen = true;
                return true;
            }
        });
        return this.mCurrentView;
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        if (this.mNotify == null) {
            this.mNotify = new ApertureNotificationListener();
            CameraNotificationManager.getInstance().setNotificationListener(this.mNotify);
        }
        BracketMasterController.getInstance().setMaxMinShutterSpeedValue();
        BracketMasterController.getInstance().setMaxMinApertureSpeedValue();
        this.isAperturefile = true;
        Log.d(TAG, "ApertureRangeScreen : onResume() power on");
        this.mGaugeCurrentLevel = BackUpUtil.getInstance().getPreferenceInt(BracketMasterBackUpKey.TAG_CURRENT_APERTURE_RANGE_BACKUP, this.mGaugeCurrentLevel);
        this.mGaugeCurrentLevelBackup = this.mGaugeCurrentLevel;
        this.apertureCounter = BackUpUtil.getInstance().getPreferenceInt(BracketMasterBackUpKey.TAG_CURRENT_APERTURECOUNTER_BACKUP, this.apertureCounter);
        this.apertureCounterbackup = this.apertureCounter;
        setVerticalSeekBar();
        this.mGaugeNewLevel = BackUpUtil.getInstance().getPreferenceInt(BracketMasterBackUpKey.TAG_CURRENT_APERTURE_RANGE, 7);
        this.mGaugeNewLevelBackup = this.mGaugeNewLevel;
        apertureLevelSelection();
        this.firstValuebackup = this.aa.getSecondShift();
        this.secondValuebackup = this.aa.getThirdShootingPosition();
        getHandler().sendEmptyMessage(150385);
        ApoWrapper.setApoType(ApoWrapper.APO_TYPE.SPECIAL);
        setKeyBeepPattern(0);
        if (!ModeDialDetector.hasModeDial()) {
            BMExposureModeController.getInstance().setValue(ExposureModeController.EXPOSURE_MODE, "Aperture");
        }
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.fw.Layout
    public void onReopened() {
        super.onReopened();
        updateView();
    }

    public void apertureLevelSelection() {
        this.mGaugeCurrentLevel = this.verticalSeekBar.getProgress();
        apertureTrapezium();
        saveApertureData(this.mGaugeNewLevel, this.mGaugeCurrentLevel);
    }

    public void apertureTrapezium() {
        BMAperture.mGauge = this.mGaugeCurrentLevel;
        this.mController.apertureValues(this.mGaugeCurrentLevel);
        showCurrentValues();
        if (this.centerApertureValue == null) {
            Log.d(TAG, "apertureTrapezium() null case ");
            this.centerApertureValue = "F--";
            this.firstApertureValue = this.centerApertureValue;
            this.SecondApertureValue = this.centerApertureValue;
            this.mLinearLayout.removeView(this.myview);
            this.mLinearLayout.setVisibility(4);
        } else if (this.centerApertureValue.equalsIgnoreCase("F--")) {
            Log.d(TAG, "apertureTrapezium Graph invisibled ");
            this.firstApertureValue = this.centerApertureValue;
            this.SecondApertureValue = this.centerApertureValue;
            this.mLinearLayout.removeView(this.myview);
            this.mLinearLayout.setVisibility(4);
        } else if (Double.parseDouble(this.mController.getCurrentApertureAvailMin().substring(1)) < trapeziumApMin || Double.parseDouble(this.mController.getCurrentApertureAvailMax().substring(1)) > trapeziumApMax) {
            Log.d(TAG, "apertureTrapezium unsupported lens ");
            this.mLinearLayout.removeView(this.myview);
            this.mLinearLayout.setVisibility(4);
        } else {
            Log.d(TAG, " apertureTrapezium Graph visibled ");
            this.mLinearLayout.setVisibility(0);
            Activity activity = getActivity();
            Context ctx = activity.getApplicationContext();
            if (this.myview == null) {
                this.myview = new BMView(ctx);
                this.mLinearLayout.addView(this.myview, this.mLayoutParams);
            } else {
                this.mLinearLayout.removeView(this.myview);
                this.mLinearLayout.addView(this.myview, this.mLayoutParams);
            }
        }
        setApertureText(this.centerApertureValue, this.firstApertureValue, this.SecondApertureValue);
    }

    void getValue(int shiftS, int shiftT) {
        this.firstApertureValue = this.mController.getFirstApertureValue(this.aa.getCenterApertureValue(), this.mGaugeCurrentLevel);
        this.SecondApertureValue = this.mController.getSecondApertureValue(this.aa.getCenterApertureValue(), shiftT);
        BackUpUtil.getInstance().setPreference(BracketMasterBackUpKey.TAG_CURRENT_APERTURE_RANGE_MAIN_shiftS, Integer.valueOf(this.mGaugeCurrentLevel));
        BackUpUtil.getInstance().setPreference(BracketMasterBackUpKey.TAG_CURRENT_APERTURE_RANGE_MAIN_shiftT, Integer.valueOf(shiftT));
        this.aa.setmFirstXvalue(this.mController.getShutterFirstValuesForTrapezium(this.aa.getCenterSSValue(), shiftS));
        this.aa.setmSecondXvalue(this.mController.getShutterSecondValuesForTrapezium(this.aa.getCenterSSValue(), shiftT));
        this.aa.setmFirstYvalue(this.mController.getFirstApertureTrapeziumValue(this.aa.getCenterApertureValue(), shiftS));
        this.aa.setmSecondYvalue(this.mController.getSecondApertureTrapeziumValue(this.aa.getCenterApertureValue(), shiftT));
    }

    public void showCurrentValues() {
        this.centerApertureValue = this.aa.getCenterApertureValue();
        this.maxApertureValue = this.aa.getMaxApertureValue();
        this.minApertureValue = this.aa.getMinApertureValue();
        this.firstApertureValue = this.aa.getFirstApertureValue();
        this.SecondApertureValue = this.aa.getSecondApertureValue();
        this.mCenterYval = this.aa.getmCenterYval();
        this.centerSSValue = this.aa.getCenterSSValue();
        this.mCenterXVal = this.aa.getmCenterXVal();
        getValue(this.aa.getSecondShift(), this.aa.getThirdShootingPosition());
        this.mFirstYvalue = this.aa.getmFirstYvalue();
        this.mSecondYvalue = this.aa.getmSecondYvalue();
        this.mFirstXvalue = this.aa.getmFirstXvalue();
        this.mSecondXvalue = this.aa.getmSecondXvalue();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class BMView extends View implements Drawable.Callback {
        double mCenterXVal1;

        public BMView(Context context) {
            super(context);
        }

        @Override // android.view.View
        protected void onDraw(Canvas canvas) {
            int minYValue;
            int Y2Value;
            BMApertureRangeLayout.this.drawSecondshootingPosition();
            BMApertureRangeLayout.this.drawThirdshootingPosition();
            Paint paintImage = new Paint();
            Bitmap positionLevel1 = BitmapFactory.decodeResource(getResources(), R.drawable.p_16_dd_parts_bm_positonlabel_1);
            int centerYValue = BMApertureRangeLayout.this.mCenterYval;
            if (BMApertureRangeLayout.this.mCenterXVal <= 217.0d) {
                this.mCenterXVal1 = BMApertureRangeLayout.this.mController.getxp(BMApertureRangeLayout.this.mCenterXVal, BMApertureRangeLayout.this.mCenterYval);
            } else if (BMApertureRangeLayout.this.mCenterXVal >= BMApertureRangeLayout.this.SecondShootXvalue) {
                this.mCenterXVal1 = BMApertureRangeLayout.this.mController.getxp(BMApertureRangeLayout.this.SecondShootXvalue, BMApertureRangeLayout.this.SecondShootYvalue) - 2.0f;
            } else if (BMApertureRangeLayout.this.mCenterXVal < 441.0d) {
                this.mCenterXVal1 = BMApertureRangeLayout.this.mController.getxpn(BMApertureRangeLayout.this.mCenterXVal, BMApertureRangeLayout.this.mCenterYval);
            } else {
                this.mCenterXVal1 = BMApertureRangeLayout.this.mController.getxp(BMApertureRangeLayout.this.SecondShootXvalue, BMApertureRangeLayout.this.SecondShootYvalue) - 2.0f;
            }
            int[] aryForRectXValues = {0, 451, 150, 250, 120, 210, 172};
            Paint paintRectangle = new Paint();
            paintRectangle.setColor(Color.parseColor("#99804200"));
            paintRectangle.setStyle(Paint.Style.STROKE);
            paintRectangle.setStrokeWidth(3.0f);
            paintRectangle.setStyle(Paint.Style.FILL);
            Path pathToDrawRectangle = new Path();
            pathToDrawRectangle.moveTo(aryForRectXValues[0] + BMApertureRangeLayout.this.mController.getXposition(BMApertureRangeLayout.this.SecondShootYvalue), BMApertureRangeLayout.this.SecondShootYvalue);
            pathToDrawRectangle.lineTo(aryForRectXValues[0] + BMApertureRangeLayout.this.mController.getXposition(BMApertureRangeLayout.this.thirdShootYvalue), BMApertureRangeLayout.this.thirdShootYvalue);
            pathToDrawRectangle.lineTo(aryForRectXValues[1] - BMApertureRangeLayout.this.mController.getXposition(BMApertureRangeLayout.this.thirdShootYvalue), BMApertureRangeLayout.this.thirdShootYvalue + 0);
            pathToDrawRectangle.lineTo(aryForRectXValues[1] - BMApertureRangeLayout.this.mController.getXposition(BMApertureRangeLayout.this.SecondShootYvalue), BMApertureRangeLayout.this.SecondShootYvalue + 0);
            pathToDrawRectangle.close();
            canvas.drawPath(pathToDrawRectangle, paintRectangle);
            for (int i = 0; i <= 1; i++) {
                if (i == 0) {
                    minYValue = BMApertureRangeLayout.this.minApertureValue + 1;
                    Y2Value = 177;
                } else {
                    minYValue = BMApertureRangeLayout.this.maxApertureValue;
                    Y2Value = 42;
                }
                Paint paintGrayedRectangle = new Paint();
                paintGrayedRectangle.setColor(Color.parseColor("#66252525"));
                paintGrayedRectangle.setStyle(Paint.Style.STROKE);
                paintGrayedRectangle.setStrokeWidth(3.0f);
                paintGrayedRectangle.setStyle(Paint.Style.FILL);
                Path pathToDrawGrayedRectangle = new Path();
                pathToDrawGrayedRectangle.moveTo(aryForRectXValues[0] + BMApertureRangeLayout.this.mController.getXposition(Y2Value), Y2Value);
                pathToDrawGrayedRectangle.lineTo(aryForRectXValues[0] + BMApertureRangeLayout.this.mController.getXposition(minYValue), minYValue);
                pathToDrawGrayedRectangle.lineTo(aryForRectXValues[1] - BMApertureRangeLayout.this.mController.getXposition(minYValue), minYValue);
                pathToDrawGrayedRectangle.lineTo(aryForRectXValues[1] - BMApertureRangeLayout.this.mController.getXposition(Y2Value), Y2Value);
                pathToDrawGrayedRectangle.close();
                canvas.drawPath(pathToDrawGrayedRectangle, paintGrayedRectangle);
            }
            Paint paintRectangleB = new Paint();
            paintRectangleB.setStrokeWidth(1.0f);
            paintRectangleB.setStyle(Paint.Style.STROKE);
            paintRectangleB.setColor(Color.parseColor("#FFFF9500"));
            Path pathToDrawRectangleB = new Path();
            pathToDrawRectangleB.moveTo(aryForRectXValues[0] + BMApertureRangeLayout.this.mController.getXposition(BMApertureRangeLayout.this.SecondShootYvalue), BMApertureRangeLayout.this.SecondShootYvalue);
            pathToDrawRectangleB.lineTo(aryForRectXValues[0] + BMApertureRangeLayout.this.mController.getXposition(BMApertureRangeLayout.this.thirdShootYvalue), BMApertureRangeLayout.this.thirdShootYvalue);
            pathToDrawRectangleB.lineTo(aryForRectXValues[1] - BMApertureRangeLayout.this.mController.getXposition(BMApertureRangeLayout.this.thirdShootYvalue), BMApertureRangeLayout.this.thirdShootYvalue + 0);
            pathToDrawRectangleB.lineTo(aryForRectXValues[1] - BMApertureRangeLayout.this.mController.getXposition(BMApertureRangeLayout.this.SecondShootYvalue), BMApertureRangeLayout.this.SecondShootYvalue + 0);
            pathToDrawRectangleB.close();
            canvas.drawPath(pathToDrawRectangleB, paintRectangleB);
            BMApertureRangeLayout.this.drawSecondshootingPosition();
            BMApertureRangeLayout.this.drawThirdshootingPosition();
            Paint paintLine = new Paint();
            paintLine.setColor(Color.parseColor("#FFFF9500"));
            paintLine.setStyle(Paint.Style.STROKE);
            paintLine.setStrokeWidth(2.0f);
            canvas.drawLine(0 + ((float) BMApertureRangeLayout.this.SecondShootXvalue1), BMApertureRangeLayout.this.SecondShootYvalue + 0, 0 + ((float) this.mCenterXVal1), BMApertureRangeLayout.this.mCenterYval + 0, paintLine);
            canvas.drawLine(0 + ((float) this.mCenterXVal1), BMApertureRangeLayout.this.mCenterYval + 0, 0 + ((float) BMApertureRangeLayout.this.thirdShootXvalue1), BMApertureRangeLayout.this.thirdShootYvalue + 0, paintLine);
            Bitmap positionLevel3 = BitmapFactory.decodeResource(getResources(), R.drawable.p_16_dd_parts_bm_positonlabel_3);
            canvas.drawBitmap(positionLevel3, ((float) BMApertureRangeLayout.this.thirdShootXvalue1) - 27, BMApertureRangeLayout.this.thirdShootYvalue - 54, paintImage);
            float leftPosition1 = ((float) this.mCenterXVal1) - 27;
            if (leftPosition1 > 430.0f) {
                leftPosition1 = 430.0f;
            }
            canvas.drawBitmap(positionLevel1, leftPosition1, centerYValue - 54, paintImage);
            Bitmap positionLevel2 = BitmapFactory.decodeResource(getResources(), R.drawable.p_16_dd_parts_bm_positonlabel_2);
            canvas.drawBitmap(positionLevel2, ((float) BMApertureRangeLayout.this.SecondShootXvalue1) - 27, BMApertureRangeLayout.this.SecondShootYvalue - 53, paintImage);
        }
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IConvertibleKeyHandler
    public int onConvertedKeyDown(KeyEvent event, IKeyFunction func) {
        int result = super.onConvertedKeyDown(event, func);
        CustomizableFunction customFunc = (CustomizableFunction) func;
        switch (customFunc) {
            case MainNext:
                CameraSetting.getInstance().incrementAperture();
                return 1;
            case MainPrev:
                CameraSetting.getInstance().decrementAperture();
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
                apertureLevelSelection();
                return 1;
            }
            if (event.getScanCode() == 108) {
                this.verticalSeekBar.moveDown();
                apertureLevelSelection();
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
        Log.d(TAG, "onKeyDown for keycode:" + code);
        switch (code) {
            case 103:
                this.verticalSeekBar.moveUp();
                apertureLevelSelection();
                return 1;
            case AppRoot.USER_KEYCODE.LEFT /* 105 */:
            case AppRoot.USER_KEYCODE.RIGHT /* 106 */:
            case AppRoot.USER_KEYCODE.SK2 /* 513 */:
                return -1;
            case AppRoot.USER_KEYCODE.DOWN /* 108 */:
                this.verticalSeekBar.moveDown();
                apertureLevelSelection();
                return 1;
            case AppRoot.USER_KEYCODE.SK1 /* 229 */:
                int retValue = pushedMenuKey();
                return retValue;
            case AppRoot.USER_KEYCODE.CENTER /* 232 */:
                saveApertureData(this.mGaugeNewLevel, this.mGaugeCurrentLevel);
                closeMenuLayout(null);
                return 0;
            case AppRoot.USER_KEYCODE.S1_ON /* 516 */:
            case AppRoot.USER_KEYCODE.S1_OFF /* 772 */:
                saveApertureData(this.mGaugeNewLevel, this.mGaugeCurrentLevel);
                return 0;
            case AppRoot.USER_KEYCODE.S2_ON /* 518 */:
            case AppRoot.USER_KEYCODE.S2_OFF /* 774 */:
                saveApertureData(this.mGaugeNewLevel, this.mGaugeCurrentLevel);
                return 0;
            case AppRoot.USER_KEYCODE.SHUTTLE_RIGHT /* 522 */:
            case AppRoot.USER_KEYCODE.DIAL1_LEFT /* 526 */:
            case AppRoot.USER_KEYCODE.DIAL2_LEFT /* 529 */:
                CameraSetting.getInstance().decrementAperture();
                return 0;
            case AppRoot.USER_KEYCODE.SHUTTLE_LEFT /* 523 */:
            case AppRoot.USER_KEYCODE.DIAL1_RIGHT /* 525 */:
            case AppRoot.USER_KEYCODE.DIAL2_RIGHT /* 528 */:
                CameraSetting.getInstance().incrementAperture();
                return 0;
            case AppRoot.USER_KEYCODE.LENS_ATTACH /* 530 */:
                apertureLevelSelection();
                updateView();
                return 1;
            case AppRoot.USER_KEYCODE.AEL /* 532 */:
                return 0;
            case AppRoot.USER_KEYCODE.MODE_DIAL_CHANGED /* 572 */:
                BMMenuController.getInstance().setRangeStatus(true);
                return 0;
            case AppRoot.USER_KEYCODE.DELETE /* 595 */:
                return 1;
            case AppRoot.USER_KEYCODE.LENS_DETACH /* 786 */:
                return 0;
            default:
                return 0;
        }
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMovieRecKey() {
        saveApertureData(this.mGaugeNewLevel, this.mGaugeCurrentLevel);
        closeLayout();
        return super.pushedMovieRecKey();
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedPlayBackKey() {
        closeLayout();
        return super.pushedPlayBackKey();
    }

    public void saveApertureData(int bracketRangeSteps, int currentlevel) {
        BackUpUtil.getInstance().setPreference(BracketMasterBackUpKey.TAG_CURRENT_BRACKET_TYPE, BMMenuController.ApertureBracket);
        BackUpUtil.getInstance().setPreference(BracketMasterBackUpKey.TAG_CURRENT_APERTURE_RANGE_BACKUP, Integer.valueOf(currentlevel));
        BackUpUtil.getInstance().setPreference(BracketMasterBackUpKey.TAG_CURRENT_APERTURECOUNTER_BACKUP, Integer.valueOf(this.apertureCounter));
        BackUpUtil.getInstance().setPreference(BracketMasterBackUpKey.TAG_CURRENT_APERTURE_RANGE, Integer.valueOf(bracketRangeSteps));
        BackUpUtil.getInstance().setPreference(BracketMasterBackUpKey.TAG_CURRENT_APERTURE_RANGE_MAIN, Integer.valueOf(currentlevel));
    }

    public void setApertureText(String apertureCentreVal, String apertureVal1, String apertureVal2) {
        if (apertureVal1.equalsIgnoreCase("F1.7") && this.mController.getCurrentApertureAvailMin().equalsIgnoreCase("F1.8")) {
            apertureVal1 = "F1.8";
        }
        if (apertureVal2.equalsIgnoreCase("F1.7") && this.mController.getCurrentApertureAvailMax().equalsIgnoreCase("F1.8")) {
            apertureVal2 = "F1.8";
        }
        this.mFirstBracketValue.setText(apertureCentreVal);
        this.mSecondBracketValue.setText(apertureVal1);
        this.mThirdBracketValue.setText(apertureVal2);
    }

    public void setCenterY() {
        if (this.minApertureValue <= this.mCenterYval) {
            this.mCenterYval = this.minApertureValue;
        } else if (this.maxApertureValue >= this.mCenterYval) {
            this.mCenterYval = this.maxApertureValue;
        }
    }

    public void drawSecondshootingPosition() {
        setCenterY();
        if (this.minApertureValue <= this.mFirstYvalue) {
            this.SecondShootYvalue = this.minApertureValue;
            int shift = (this.minApertureValue - this.mCenterYval) / 6;
            this.SecondShootXvalue = this.mController.getShutterFirstValuesForTrapezium(this.centerSSValue, shift);
            Log.d("SecondShootXvalue", "8888888888888888888" + this.SecondShootXvalue);
        } else {
            this.SecondShootYvalue = this.mFirstYvalue;
            this.SecondShootXvalue = this.mFirstXvalue;
        }
        if (this.SecondShootXvalue > 430.0d) {
            this.SecondShootXvalue = 430.0d;
        }
        this.SecondShootXvalue1 = this.mController.getxp(this.SecondShootXvalue, this.SecondShootYvalue);
    }

    public void drawThirdshootingPosition() {
        setCenterY();
        if (this.maxApertureValue >= this.mSecondYvalue) {
            this.thirdShootYvalue = this.maxApertureValue;
            int shift = (this.mCenterYval - this.maxApertureValue) / 6;
            this.thirdShootXvalue = this.mController.getShutterSecondValuesForTrapezium(this.centerSSValue, shift);
            Log.d("thirdShootXvalue", "8888888888888888888" + this.thirdShootXvalue);
        } else {
            this.thirdShootYvalue = this.mSecondYvalue;
            this.thirdShootXvalue = this.mSecondXvalue;
        }
        if (this.thirdShootXvalue > 430.0d) {
            this.thirdShootXvalue = 430.0d;
        }
        this.thirdShootXvalue1 = this.mController.getxp(this.thirdShootXvalue, this.thirdShootYvalue);
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        if (this.mNotify != null) {
            CameraNotificationManager.getInstance().removeNotificationListener(this.mNotify);
            this.mNotify = null;
        }
        this.isAperturefile = false;
        this.mNotify = null;
        freeResources();
        VerticalSeekBar.isTouchedScreen = false;
        super.onPause();
    }

    private void freeResources() {
        this.mCurrentView = null;
        this.mController = null;
        this.mLinearLayout = null;
        this.myview = null;
        this.aa = null;
        this.verticalSeekBar = null;
        this.mGaugeBackground = null;
        this.mPlus = null;
        this.mMinus = null;
        this.mFooterGuide = null;
        this.mGaugetext = null;
        this.mGaugeBar = null;
        this.mFirstBracketValue = null;
        this.mSecondBracketValue = null;
        this.mThirdBracketValue = null;
        this.GaugeCurretValue = null;
        this.mLayoutParams = null;
        this.firstApertureValue = null;
        this.centerApertureValue = null;
        this.SecondApertureValue = null;
        this.mMediaNotifier = null;
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
    class ApertureNotificationListener implements NotificationListener {
        private final String[] tags = {"Aperture", CameraNotificationManager.SHUTTER_SPEED};

        ApertureNotificationListener() {
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public void onNotify(String tag) {
            BracketMasterController.getInstance().refreshApertureValues();
            if ((tag.equalsIgnoreCase("Aperture") || tag.equalsIgnoreCase(CameraNotificationManager.SHUTTER_SPEED)) && BMApertureRangeLayout.this.isAperturefile) {
                BMApertureRangeLayout.this.apertureLevelSelection();
            }
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public String[] getTags() {
            return this.tags;
        }
    }

    float mathe(double y3, double y4, double y, double x3, double x4) {
        double x = 0.0d + (((((x4 - x3) * (y - y3)) - ((y4 - y3) * (0.0d - x3))) / (((y4 - y3) * (400.0d - 0.0d)) - ((x4 - x3) * (y - y)))) * (400.0d - 0.0d));
        return (float) x;
    }

    private void setVerticalSeekBar() {
        this.verticalSeekBar = (VerticalSeekBar) this.mCurrentView.findViewById(R.id.vertical_Seekbar);
        this.verticalSeekBar.setProgressDrawable(null);
        this.mGaugetext = (TextView) this.mCurrentView.findViewById(R.id.vertical_sb_progresstext);
        this.verticalSeekBar.setMax(8);
        this.verticalSeekBar.setProgress(this.mGaugeCurrentLevel);
        getInformationText(this.mGaugeCurrentLevel);
        this.verticalSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() { // from class: com.sony.imaging.app.bracketpro.menu.layout.BMApertureRangeLayout.2
            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                BMApertureRangeLayout.this.getInformationText(progress);
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
        revertChangeAndUpdateScreenInfo();
        BackUpUtil.getInstance().setPreference(BracketMasterBackUpKey.TAG_CURRENT_APERTURECOUNTER_BACKUP, Integer.valueOf(this.apertureCounterbackup));
        this.aa.setShiftSecond(this.secondValuebackup);
        this.aa.setShiftThird(this.firstValuebackup);
        openPreviousMenu();
        return 1;
    }

    private void revertChangeAndUpdateScreenInfo() {
        this.mGaugeNewLevel = this.mGaugeNewLevelBackup;
        this.mGaugeCurrentLevel = this.mGaugeCurrentLevelBackup;
        this.mController.apertureValues(this.mGaugeCurrentLevel);
        showCurrentValues();
        saveApertureData(this.mGaugeNewLevelBackup, this.mGaugeCurrentLevelBackup);
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

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout
    protected void onAFMFSwitchChanged() {
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachKeyHandler
    public int onLensAttached(KeyEvent event) {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachKeyHandler
    public int onLensDetached(KeyEvent event) {
        return -1;
    }

    private void incrementAperture() {
        CameraSetting.getInstance().incrementAperture();
    }

    private void decrementAperture() {
        CameraSetting.getInstance().incrementAperture();
    }
}
