package com.sony.imaging.app.bracketpro.menu.layout;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.caution.IkeyDispatchEach;
import com.sony.imaging.app.base.common.FocusModeDialDetector;
import com.sony.imaging.app.base.common.MediaNotificationManager;
import com.sony.imaging.app.base.common.widget.FooterGuide;
import com.sony.imaging.app.base.common.widget.FooterGuideDataResId;
import com.sony.imaging.app.base.menu.BaseMenuService;
import com.sony.imaging.app.base.menu.HistoryItem;
import com.sony.imaging.app.base.menu.MenuDataParcelable;
import com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.base.shooting.camera.FocusModeController;
import com.sony.imaging.app.base.shooting.camera.parameters.AbstractSupportedChecker;
import com.sony.imaging.app.base.shooting.trigger.ModeDialDetector;
import com.sony.imaging.app.bracketpro.R;
import com.sony.imaging.app.bracketpro.caution.CautionInfo;
import com.sony.imaging.app.bracketpro.commonUtil.BracketMasterBackUpKey;
import com.sony.imaging.app.bracketpro.menu.controller.BMMenuController;
import com.sony.imaging.app.bracketpro.shooting.state.BMEEState;
import com.sony.imaging.app.bracketpro.widget.VerticalSeekBar;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.util.ApoWrapper;
import com.sony.imaging.app.util.AvailableInfo;
import com.sony.imaging.app.util.BackUpUtil;
import com.sony.imaging.app.util.NotificationListener;
import com.sony.scalar.hardware.CameraEx;

/* loaded from: classes.dex */
public class BMFocusRangeLayout extends DisplayMenuItemsMenuLayout implements NotificationListener {
    private static final int MID = 1;
    private static final int NARROW = 0;
    private static final String STR_FORMAT_INTPLUS = "＋%1d";
    private static final int WIDE = 2;
    private CameraSetting mCamSet;
    private TextView mGaugetext;
    private LinearLayout.LayoutParams mLayoutParams;
    private int mLevel;
    private LinearLayout mLinearLayout;
    private boolean mbMounted;
    private static final String TAG = BMFocusRangeLayout.class.getName();
    public static int mLastSavedRangeSteps = 1;
    private View mCurrentView = null;
    private FooterGuide mFooterGuide = null;
    private VerticalSeekBar verticalSeekBar = null;
    private ImageView squareCenter = null;
    private ImageView squareMid2 = null;
    private ImageView squareNarrow2 = null;
    private ImageView squareMid3 = null;
    private ImageView squareNarrow3 = null;
    private ImageView squareWide2 = null;
    private ImageView squareWide3 = null;
    private float mGaugeDisplacement = 0.0f;
    private int mGaugeCurrentLevel = 1;
    int currentRangeSteps = 1;
    private final int REQUIRED_IMAGE_SIZE = 100;
    private MediaNotificationManager mMediaNotifier = null;
    private final String[] tags = {MediaNotificationManager.TAG_MEDIA_STATUS_CHANGE};

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        if (this.mCurrentView == null) {
            this.mCurrentView = inflater.inflate(R.layout.bm_focus_range, (ViewGroup) null);
        }
        initializeView();
        Activity activity = getActivity();
        Context ctx = activity.getApplicationContext();
        BMView myview = new BMView(ctx);
        this.mLinearLayout.addView(myview, this.mLayoutParams);
        this.mMediaNotifier = MediaNotificationManager.getInstance();
        this.mbMounted = this.mMediaNotifier.isMounted();
        this.mMediaNotifier.setNotificationListener(this);
        this.mCurrentView.setVisibility(0);
        this.mCurrentView.setOnTouchListener(new View.OnTouchListener() { // from class: com.sony.imaging.app.bracketpro.menu.layout.BMFocusRangeLayout.1
            @Override // android.view.View.OnTouchListener
            public boolean onTouch(View view, MotionEvent motionEvent) {
                VerticalSeekBar.isTouchedScreen = true;
                return true;
            }
        });
        return this.mCurrentView;
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.fw.Layout
    public void onReopened() {
        super.onReopened();
        updateView();
    }

    private void initializeView() {
        this.squareCenter = (ImageView) this.mCurrentView.findViewById(R.id.SquareCenter);
        this.squareMid2 = (ImageView) this.mCurrentView.findViewById(R.id.SquareMid2);
        this.squareMid3 = (ImageView) this.mCurrentView.findViewById(R.id.SquareMid3);
        this.squareNarrow2 = (ImageView) this.mCurrentView.findViewById(R.id.SquareNarrow2);
        this.squareNarrow3 = (ImageView) this.mCurrentView.findViewById(R.id.SquareNarrow3);
        this.squareWide2 = (ImageView) this.mCurrentView.findViewById(R.id.SquareWide2);
        this.squareWide3 = (ImageView) this.mCurrentView.findViewById(R.id.SquareWide3);
        this.mLinearLayout = (LinearLayout) this.mCurrentView.findViewById(R.id.linearLayout);
        this.mLayoutParams = new LinearLayout.LayoutParams(AppRoot.USER_KEYCODE.MODE_DIAL_MANUAL, 300);
        this.mFooterGuide = (FooterGuide) this.mCurrentView.findViewById(R.id.footer_guide);
        this.mFooterGuide.setData(new FooterGuideDataResId(getActivity(), android.R.string.zen_mode_feature_name, android.R.string.httpErrorRedirectLoop));
        this.mCamSet = CameraSetting.getInstance();
        CameraEx.LensInfo info = this.mCamSet.getLensInfo();
        if (info == null) {
            displayCaution();
        } else if (info.LensType.equalsIgnoreCase("A-mount")) {
            displayAMountLensCaution();
        } else {
            this.mCurrentView.setVisibility(0);
        }
    }

    /* loaded from: classes.dex */
    public static class BitmapResizer {
        private static final String TAG = "BitmapResizer";

        public static Bitmap decodeImage(Resources res, int id, int requiredSize) {
            try {
                BitmapFactory.Options o = new BitmapFactory.Options();
                o.inJustDecodeBounds = true;
                BitmapFactory.decodeResource(res, id, o);
                int width_tmp = o.outWidth;
                int height_tmp = o.outHeight;
                int scale = 1;
                while (width_tmp / 2 >= requiredSize && height_tmp / 2 >= requiredSize) {
                    width_tmp /= 2;
                    height_tmp /= 2;
                    scale *= 2;
                }
                BitmapFactory.Options o2 = new BitmapFactory.Options();
                o2.inSampleSize = scale;
                return BitmapFactory.decodeResource(res, id, o2);
            } catch (Exception e) {
                Log.d(TAG, "@@@@@ Exception occuered in Bitmap Resizer." + e.getMessage());
                return null;
            }
        }
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        this.currentRangeSteps = BackUpUtil.getInstance().getPreferenceInt(BracketMasterBackUpKey.TAG_CURRENT_FOCUS_RANGE, 1);
        this.mGaugeCurrentLevel = this.currentRangeSteps;
        mLastSavedRangeSteps = this.currentRangeSteps;
        setVerticalSeekBar();
        initializeView();
        FocusRangeRectangle();
        getHandler().sendEmptyMessage(150385);
        ApoWrapper.setApoType(ApoWrapper.APO_TYPE.SPECIAL);
        setKeyBeepPattern(0);
        this.mMediaNotifier = MediaNotificationManager.getInstance();
        this.mbMounted = this.mMediaNotifier.isMounted();
        this.mMediaNotifier.setNotificationListener(this);
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedMainDialNext() {
        VerticalSeekBar.isTouchedScreen = false;
        this.mGaugeCurrentLevel = this.verticalSeekBar.moveUp();
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedMainDialPrev() {
        VerticalSeekBar.isTouchedScreen = false;
        this.mGaugeCurrentLevel = this.verticalSeekBar.moveDown();
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyUp(int keyCode, KeyEvent event) {
        this.verticalSeekBar.setSelected(true);
        if (VerticalSeekBar.isTouchedScreen) {
            if (event.getScanCode() == 103) {
                this.mGaugeCurrentLevel = this.verticalSeekBar.moveUp();
                FocusRangeRectangle();
                return 1;
            }
            if (event.getScanCode() == 108) {
                this.mGaugeCurrentLevel = this.verticalSeekBar.moveDown();
                FocusRangeRectangle();
                return 1;
            }
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyDown(int keyCode, KeyEvent event) {
        int retValue = 0;
        this.verticalSeekBar.setSelected(true);
        VerticalSeekBar.isTouchedScreen = false;
        int code = event.getScanCode();
        super.onKeyDown(keyCode, event);
        Log.d("onKeyDown", "onKeyDown***************************** event key code  :" + code);
        switch (code) {
            case 103:
                this.mGaugeCurrentLevel = this.verticalSeekBar.moveUp();
                retValue = 1;
                break;
            case AppRoot.USER_KEYCODE.LEFT /* 105 */:
            case AppRoot.USER_KEYCODE.RIGHT /* 106 */:
            case AppRoot.USER_KEYCODE.SK2 /* 513 */:
            case AppRoot.USER_KEYCODE.DIAL1_RIGHT /* 525 */:
            case AppRoot.USER_KEYCODE.DIAL1_LEFT /* 526 */:
                retValue = -1;
                break;
            case AppRoot.USER_KEYCODE.DOWN /* 108 */:
                this.mGaugeCurrentLevel = this.verticalSeekBar.moveDown();
                retValue = 1;
                break;
            case AppRoot.USER_KEYCODE.SK1 /* 229 */:
                retValue = pushedMenuKey();
                break;
            case AppRoot.USER_KEYCODE.CENTER /* 232 */:
                closeLayout();
                retValue = 1;
                break;
            case AppRoot.USER_KEYCODE.S1_ON /* 516 */:
            case AppRoot.USER_KEYCODE.S2_ON /* 518 */:
            case AppRoot.USER_KEYCODE.S1_OFF /* 772 */:
            case AppRoot.USER_KEYCODE.S2_OFF /* 774 */:
                retValue = 0;
                break;
            case AppRoot.USER_KEYCODE.LENS_ATTACH /* 530 */:
                retValue = 0;
                break;
            case AppRoot.USER_KEYCODE.AEL /* 532 */:
                retValue = 1;
                break;
            case AppRoot.USER_KEYCODE.MODE_DIAL_CHANGED /* 572 */:
                BMMenuController.getInstance().setRangeStatus(true);
                retValue = 0;
                break;
            case AppRoot.USER_KEYCODE.LENS_DETACH /* 786 */:
                retValue = 0;
                break;
            default:
                Log.i(TAG, AbstractSupportedChecker.COLON + code);
                break;
        }
        FocusRangeRectangle();
        return retValue;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMovieRecKey() {
        closeLayout();
        return super.pushedMovieRecKey();
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedPlayBackKey() {
        closeLayout();
        return super.pushedPlayBackKey();
    }

    public void FocusRangeRectangle() {
        this.squareCenter.setVisibility(4);
        this.squareMid2.setVisibility(4);
        this.squareNarrow2.setVisibility(4);
        this.squareMid3.setVisibility(4);
        this.squareNarrow3.setVisibility(4);
        this.squareWide2.setVisibility(4);
        this.squareWide3.setVisibility(4);
        switch (this.mGaugeCurrentLevel) {
            case 0:
                this.squareCenter.setVisibility(0);
                this.squareNarrow2.setVisibility(0);
                this.squareNarrow3.setVisibility(0);
                break;
            case 1:
                this.squareCenter.setVisibility(0);
                this.squareMid2.setVisibility(0);
                this.squareMid3.setVisibility(0);
                break;
            case 2:
                this.squareCenter.setVisibility(0);
                this.squareWide2.setVisibility(0);
                this.squareWide3.setVisibility(0);
                break;
        }
        saveToPreference(this.mGaugeCurrentLevel);
    }

    /* loaded from: classes.dex */
    private class BMView extends View implements Drawable.Callback {
        public BMView(Context context) {
            super(context);
        }
    }

    private void saveToPreference(int value) {
        BackUpUtil.getInstance().setPreference(BracketMasterBackUpKey.TAG_CURRENT_FOCUS_RANGE, Integer.valueOf(value));
        BackUpUtil.getInstance().setPreference(BracketMasterBackUpKey.TAG_CURRENT_BRACKET_TYPE, BMMenuController.FocusBracket);
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        saveToPreference(this.mGaugeCurrentLevel);
        if (this.mMediaNotifier != null) {
            this.mMediaNotifier.removeNotificationListener(this);
        }
        this.mMediaNotifier = null;
        freeResource();
        VerticalSeekBar.isTouchedScreen = false;
        super.onPause();
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout
    public void closeLayout() {
        isValidLens();
        super.closeLayout();
        AvailableInfo.update();
        if (AvailableInfo.isFactor("INH_FACTOR_CAM_LENS_NONE_TYPE_C") || !AvailableInfo.isFactor("INH_FACTOR_CAM_LENS_TYPE_AMOUNT_TYPE_C") || BracketMasterSubMenu.sLastSelectedItem.equalsIgnoreCase(BMMenuController.FocusBracket)) {
        }
    }

    private void freeResource() {
        this.squareCenter = null;
        this.squareMid2 = null;
        this.squareNarrow2 = null;
        this.squareMid3 = null;
        this.squareNarrow3 = null;
        this.squareWide2 = null;
        this.squareWide3 = null;
        this.mCurrentView = null;
        this.mFooterGuide = null;
        this.verticalSeekBar = null;
        this.mGaugetext = null;
        this.mLayoutParams = null;
        this.mLinearLayout = null;
        this.mMediaNotifier = null;
    }

    @Override // com.sony.imaging.app.util.NotificationListener
    public String[] getTags() {
        return this.tags;
    }

    @Override // com.sony.imaging.app.util.NotificationListener
    public void onNotify(String tag) {
        if (tag != null && tag.equals(MediaNotificationManager.TAG_MEDIA_STATUS_CHANGE) && this.mMediaNotifier != null && this.mMediaNotifier.isMounted() != this.mbMounted) {
            closeMenuLayout(null);
        }
    }

    private void setVerticalSeekBar() {
        this.verticalSeekBar = (VerticalSeekBar) this.mCurrentView.findViewById(R.id.vertical_Seekbar);
        this.verticalSeekBar.setProgressDrawable(null);
        this.mGaugetext = (TextView) this.mCurrentView.findViewById(R.id.vertical_sb_progresstext);
        this.verticalSeekBar.setMax(2);
        this.verticalSeekBar.setProgress(this.mGaugeCurrentLevel);
        getInformationText(this.mGaugeCurrentLevel);
        this.verticalSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() { // from class: com.sony.imaging.app.bracketpro.menu.layout.BMFocusRangeLayout.2
            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                BMFocusRangeLayout.this.getInformationText(progress);
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
        String ret = String.format(STR_FORMAT_INTPLUS, Integer.valueOf(this.mLevel));
        this.mGaugetext.setText("");
        return ret;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMenuKey() {
        this.mGaugeCurrentLevel = mLastSavedRangeSteps;
        openPreviousMenu();
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedCenterKey() {
        closeMenuLayout(null);
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
        if (this.mCurrentView != null) {
            this.mCurrentView.setVisibility(0);
        }
        return 0;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachKeyHandler
    public int onLensDetached(KeyEvent event) {
        displayCaution();
        return -1;
    }

    private void displayCaution() {
        IkeyDispatchEach mKey = new IkeyDispatchEach() { // from class: com.sony.imaging.app.bracketpro.menu.layout.BMFocusRangeLayout.3
            @Override // com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IKeyHandler
            public int onKeyDown(int keyCode, KeyEvent event) {
                switch (event.getScanCode()) {
                    case AppRoot.USER_KEYCODE.PLAYBACK /* 207 */:
                        CautionUtilityClass.getInstance().executeTerminate();
                        return 0;
                    case AppRoot.USER_KEYCODE.CENTER /* 232 */:
                        BMFocusRangeLayout.this.mGaugeCurrentLevel = BMFocusRangeLayout.mLastSavedRangeSteps;
                        CautionUtilityClass.getInstance().executeTerminate();
                        BMFocusRangeLayout.this.openPreviousMenu();
                        return 1;
                    case AppRoot.USER_KEYCODE.LENS_ATTACH /* 530 */:
                        BMFocusRangeLayout.this.mCurrentView.setVisibility(0);
                        break;
                    case AppRoot.USER_KEYCODE.MODE_DIAL_CHANGED /* 572 */:
                        break;
                    default:
                        return -1;
                }
                if (ModeDialDetector.getModeDialPosition() != -1) {
                    CautionUtilityClass.getInstance().executeTerminate();
                    turnedModeDial();
                }
                return 0;
            }
        };
        if (this.mCurrentView != null) {
            this.mCurrentView.setVisibility(4);
        }
        CautionUtilityClass.getInstance().setDispatchKeyEvent(CautionInfo.CAUTION_ID_INH_FACTOR_DETACH_EMOUNT_LENS, mKey);
        CautionUtilityClass.getInstance().requestTrigger(CautionInfo.CAUTION_ID_INH_FACTOR_DETACH_EMOUNT_LENS);
    }

    private void displayAMountLensCaution() {
        IkeyDispatchEach mKey = new IkeyDispatchEach() { // from class: com.sony.imaging.app.bracketpro.menu.layout.BMFocusRangeLayout.4
            @Override // com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IKeyHandler
            public int onKeyDown(int keyCode, KeyEvent event) {
                switch (event.getScanCode()) {
                    case AppRoot.USER_KEYCODE.PLAYBACK /* 207 */:
                        CautionUtilityClass.getInstance().executeTerminate();
                        BMFocusRangeLayout.this.mCurrentView.setVisibility(0);
                        return 0;
                    case AppRoot.USER_KEYCODE.CENTER /* 232 */:
                        BMFocusRangeLayout.this.mGaugeCurrentLevel = BMFocusRangeLayout.mLastSavedRangeSteps;
                        CautionUtilityClass.getInstance().executeTerminate();
                        BMFocusRangeLayout.this.mCurrentView.setVisibility(0);
                        BMFocusRangeLayout.this.openPreviousMenu();
                        return 1;
                    case AppRoot.USER_KEYCODE.LENS_ATTACH /* 530 */:
                        BMFocusRangeLayout.this.mCurrentView.setVisibility(0);
                        return -1;
                    case AppRoot.USER_KEYCODE.UM_MOVIE_REC /* 616 */:
                        CautionUtilityClass.getInstance().executeTerminate();
                        return 0;
                    default:
                        return -1;
                }
            }
        };
        if (this.mCurrentView != null) {
            this.mCurrentView.setVisibility(4);
        }
        CautionUtilityClass.getInstance().setDispatchKeyEvent(CautionInfo.CAUTION_ID_INH_FACTOR_INVALID_WITH_MOUNT_ADAPTER, mKey);
        CautionUtilityClass.getInstance().requestTrigger(CautionInfo.CAUTION_ID_INH_FACTOR_INVALID_WITH_MOUNT_ADAPTER);
    }

    private void isValidLens() {
        CameraSetting mCamSet = CameraSetting.getInstance();
        CameraEx.LensInfo info = mCamSet.getLensInfo();
        if (info == null) {
            BMEEState.isBMCautionStateBooted = true;
            return;
        }
        if (info.LensType.equalsIgnoreCase("A-mount")) {
            BMEEState.isBMCautionStateBooted = true;
        } else if (isMFModeSet()) {
            BMEEState.isBMCautionStateBooted = true;
        } else {
            BMEEState.isBMCautionStateBooted = false;
        }
    }

    private boolean isMFModeSet() {
        if (!FocusModeDialDetector.hasFocusModeDial() && !"af-s".equals(FocusModeController.getInstance().getValue())) {
            return true;
        }
        if (!FocusModeDialDetector.hasFocusModeDial() || "af-s".equals(FocusModeController.getInstance().getValue())) {
            return false;
        }
        return true;
    }
}
