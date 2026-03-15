package com.sony.imaging.app.base.menu.layout;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.sony.imaging.app.base.R;
import com.sony.imaging.app.base.common.DisplayModeObserver;
import com.sony.imaging.app.base.common.IModableLayout;
import com.sony.imaging.app.base.common.OnLayoutModeChangeListener;
import com.sony.imaging.app.base.menu.BaseMenuService;
import com.sony.imaging.app.base.menu.MenuDataParcelable;
import com.sony.imaging.app.base.shooting.camera.WhiteBalanceController;
import com.sony.imaging.app.base.shooting.widget.WhiteBalanceAdjustLayerAndArrowView;
import java.util.ArrayList;

/* loaded from: classes.dex */
public class WhiteBalanceAdjustmentMenuLayout extends DisplayMenuItemsMenuLayout implements View.OnTouchListener, IModableLayout {
    private static final String LOG_MSG_SEPARATE = "/ ColorCompensation: ";
    private static final String LOG_MSG_UPDATE_PARAM = "Update param LightBalance: ";
    public static final String MENU_ID = "ID_WHITEBALANCEADJUSTMENTMENULAYOUT";
    private static final String RESOUCE_NAME_EVF_CHART = "android:drawable/p_wbtuningchart_with_lv_evf";
    private static final String TAG = "WhiteBalanceAdjustmentMenuLayout";
    private TextView mAbTextView;
    private WhiteBalanceController mController;
    private TextView mGmTextView;
    protected boolean mIsEvfpartsExist;
    private String mItemId;
    protected OnLayoutModeChangeListener mListener;
    private String mMenuItemGuideText;
    private String mMenuItemText;
    private WhiteBalanceAdjustLayerAndArrowView mPoint;
    private WhiteBalanceController.WhiteBalanceParam mResetParam;
    private TextView mScreenTitleView;
    private TextView mTempTextView;
    private WhiteBalanceController.WhiteBalanceParam mWBParam;
    private String value;
    private final String CWB = "CustomWhiteBalance";
    private ViewGroup mCurrentView = null;
    private StringBuilder mBuilder = new StringBuilder();
    private final int DEFAULT_LIGHT = 0;
    private final int DEFAULT_COMP = 0;
    private final int DEFAULT_TEMP = WhiteBalanceController.DEF_TEMP;

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        ViewGroup currentView = createView();
        return currentView;
    }

    public ViewGroup createView() {
        int device = DisplayModeObserver.getInstance().getActiveDevice();
        int viewId = getLayout(device);
        this.mCurrentView = (ViewGroup) obtainViewFromPool(viewId);
        Initialize();
        return this.mCurrentView;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.FakeFragment
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mController = WhiteBalanceController.getInstance();
        this.mResetParam = (WhiteBalanceController.WhiteBalanceParam) this.mController.getDetailValue();
        int resId = getResources().getIdentifier(RESOUCE_NAME_EVF_CHART, null, getActivity().getApplicationContext().getPackageName());
        if (resId == 0) {
            this.mIsEvfpartsExist = false;
        } else {
            this.mIsEvfpartsExist = true;
        }
    }

    protected int getLayout(int device) {
        if (this.mIsEvfpartsExist) {
            if (1 == device) {
                int layoutid = R.layout.menu_wb_option_evf;
                return layoutid;
            }
            int layoutid2 = R.layout.menu_wb_option;
            return layoutid2;
        }
        int layoutid3 = R.layout.menu_wb_option;
        return layoutid3;
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public void onDestroyView() {
        this.mWBParam = null;
        this.mCurrentView = null;
        this.mService = null;
        this.mPoint = null;
        this.mAbTextView = null;
        this.mGmTextView = null;
        this.mTempTextView = null;
        this.mScreenTitleView = null;
        super.onDestroyView();
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.FakeFragment
    public void onDestroy() {
        this.mResetParam = null;
        this.mController = null;
        this.mBuilder.setLength(0);
        this.mBuilder.trimToSize();
        super.onDestroy();
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        if (WhiteBalanceController.CUSTOM.equals(this.value) || "custom1".equals(this.value) || "custom2".equals(this.value) || "custom3".equals(this.value) || WhiteBalanceController.COLOR_TEMP.equals(this.value)) {
            this.mTempTextView.setVisibility(0);
        } else {
            this.mTempTextView.setVisibility(4);
        }
        this.mPoint.setWBController(this.mController);
        ImageView iView = (ImageView) this.mCurrentView.findViewById(R.id.wb_option_icon_mode);
        int resId = getOptionIcon(this.value);
        iView.setImageResource(resId);
        TextView mTextView = (TextView) this.mCurrentView.findViewById(R.id.wb_option_text_mode);
        mTextView.setText(this.mService.getMenuItemText(this.mItemId));
        this.mWBParam = (WhiteBalanceController.WhiteBalanceParam) this.mController.getDetailValue();
        updateOptionValueText();
        this.mMenuItemText = getResources().getString(android.R.string.safeMode);
        this.mMenuItemGuideText = getResources().getString(android.R.string.status_bar_mobile);
        if (this.mListener == null) {
            this.mListener = new OnLayoutModeChangeListener(this, 0);
        }
        DisplayModeObserver.getInstance().setNotificationListener(this.mListener);
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        if (this.mListener != null) {
            DisplayModeObserver.getInstance().removeNotificationListener(this.mListener);
            this.mListener = null;
        }
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout
    protected ArrayList<String> getMenuItemList() {
        ArrayList<String> items = this.mService.getMenuItemList();
        return items;
    }

    private void Initialize() {
        this.mService = new BaseMenuService(getActivity().getApplicationContext());
        MenuDataParcelable parcelable = (MenuDataParcelable) this.data.getParcelable(MenuDataParcelable.KEY);
        this.mItemId = parcelable.getItemId();
        this.value = this.mService.getMenuItemValue(this.mItemId);
        this.mPoint = (WhiteBalanceAdjustLayerAndArrowView) this.mCurrentView.findViewById(R.id.wb_adjust_layout);
        this.mPoint.setOnTouchListener(this);
        this.mAbTextView = (TextView) this.mCurrentView.findViewById(R.id.wb_option_text_ab);
        this.mGmTextView = (TextView) this.mCurrentView.findViewById(R.id.wb_option_text_gm);
        this.mTempTextView = (TextView) this.mCurrentView.findViewById(R.id.wb_option_text_color);
        this.mScreenTitleView = (TextView) this.mCurrentView.findViewById(R.id.menu_screen_title);
    }

    private void update() {
        String temp = this.mController.getValue(WhiteBalanceController.COLOR_TEMP);
        if (temp != null) {
            this.mWBParam.setColorTemp(Integer.parseInt(temp));
        }
        setOption();
        updateOptionValueText();
    }

    private void setOption() {
        this.mController.setDetailValue(this.mWBParam);
        this.mBuilder.replace(0, this.mBuilder.length(), LOG_MSG_UPDATE_PARAM);
        this.mBuilder.append(this.mWBParam.getLightBalance());
        this.mBuilder.append(LOG_MSG_SEPARATE);
        this.mBuilder.append(this.mWBParam.getColorComp());
        logcat(this.mBuilder.toString());
    }

    private void updateOptionValueText() {
        int abValue = this.mWBParam.getLightBalance();
        int gmValue = this.mWBParam.getColorComp();
        if (abValue > 0) {
            this.mBuilder.replace(0, this.mBuilder.length(), WhiteBalanceController.DISP_TEXT_AB_PLUS);
            this.mAbTextView.setText(this.mBuilder.append(String.valueOf(Math.abs(abValue))).toString());
        } else if (abValue == 0) {
            this.mBuilder.replace(0, this.mBuilder.length(), WhiteBalanceController.DISP_TEXT_AB_ZERO);
            this.mAbTextView.setText(this.mBuilder.append(String.valueOf(Math.abs(abValue))).toString());
        } else {
            this.mBuilder.replace(0, this.mBuilder.length(), WhiteBalanceController.DISP_TEXT_AB_MINUS);
            this.mAbTextView.setText(this.mBuilder.append(String.valueOf(Math.abs(abValue))).toString());
        }
        if (gmValue > 0) {
            this.mBuilder.replace(0, this.mBuilder.length(), WhiteBalanceController.DISP_TEXT_GM_PLUS);
            this.mGmTextView.setText(this.mBuilder.append(String.valueOf(Math.abs(gmValue))).toString());
        } else if (gmValue == 0) {
            this.mBuilder.replace(0, this.mBuilder.length(), WhiteBalanceController.DISP_TEXT_GM_ZERO);
            this.mGmTextView.setText(this.mBuilder.append(String.valueOf(Math.abs(gmValue))).toString());
        } else {
            this.mBuilder.replace(0, this.mBuilder.length(), WhiteBalanceController.DISP_TEXT_GM_MINUS);
            this.mGmTextView.setText(this.mBuilder.append(String.valueOf(Math.abs(gmValue))).toString());
        }
        if (this.value.equals(WhiteBalanceController.COLOR_TEMP) || this.value.equals(WhiteBalanceController.CUSTOM) || this.value.equals("custom1") || this.value.equals("custom2") || this.value.equals("custom3")) {
            int tmpValue = this.mWBParam.getColorTemp();
            this.mBuilder.replace(0, this.mBuilder.length(), String.valueOf(tmpValue));
            this.mTempTextView.setText(this.mBuilder.append(WhiteBalanceController.DISP_TEXT_K).toString());
        }
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedLeftKey() {
        int value = this.mWBParam.getLightBalance() - 1;
        if (this.mPoint.moveX(value)) {
            this.mWBParam.setLightBalance(value);
            update();
            return 1;
        }
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedRightKey() {
        int value = this.mWBParam.getLightBalance() + 1;
        if (this.mPoint.moveX(value)) {
            this.mWBParam.setLightBalance(value);
            update();
            return 1;
        }
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedUpKey() {
        int value = this.mWBParam.getColorComp() - 1;
        if (this.mPoint.moveY(value)) {
            this.mWBParam.setColorComp(value);
            update();
            return 1;
        }
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedDownKey() {
        int value = this.mWBParam.getColorComp() + 1;
        if (this.mPoint.moveY(value)) {
            this.mWBParam.setColorComp(value);
            update();
            return 1;
        }
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedMainDialPrev() {
        return pushedUpKey();
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedMainDialNext() {
        return pushedDownKey();
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedSubDialPrev() {
        return pushedLeftKey();
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedSubDialNext() {
        return pushedRightKey();
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedCenterKey() {
        doItemClickProcessing(this.mItemId);
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMenuKey() {
        this.mController.setDetailValue(this.mResetParam);
        openPreviousMenu();
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout
    protected void setFunctionGuideResources(ArrayList<Object> guideResources) {
        guideResources.add(this.mMenuItemText);
        guideResources.add(this.mMenuItemGuideText);
    }

    @Override // android.view.View.OnTouchListener
    public boolean onTouch(View v, MotionEvent event) {
        return true;
    }

    private int getOptionIcon(String value) {
        if (value.equals("auto")) {
            return 17307412;
        }
        if (value.equals(WhiteBalanceController.DAYLIGHT)) {
            return 17307445;
        }
        if (value.equals(WhiteBalanceController.SHADE)) {
            return 17307486;
        }
        if (value.equals(WhiteBalanceController.CLOUDY)) {
            return 17306363;
        }
        if (value.equals(WhiteBalanceController.INCANDESCENT)) {
            return 17306395;
        }
        if (value.equals(WhiteBalanceController.WARM_FLUORESCENT)) {
            return 17306428;
        }
        if (value.equals(WhiteBalanceController.FLUORESCENT_COOLWHITE)) {
            return 17306466;
        }
        if (value.equals(WhiteBalanceController.FLUORESCENT_DAYWHITE)) {
            return 17306504;
        }
        if (value.equals(WhiteBalanceController.FLUORESCENT_DAYLIGHT)) {
            return 17306533;
        }
        if (value.equals(WhiteBalanceController.FLASH)) {
            return 17306442;
        }
        if (value.equals(WhiteBalanceController.UNDERWATER_AUTO)) {
            return 17307567;
        }
        if (value.equals(WhiteBalanceController.COLOR_TEMP)) {
            return 17306409;
        }
        if (value.equals(WhiteBalanceController.CUSTOM)) {
            return 17307426;
        }
        if (value.equals("custom1")) {
            return 17307461;
        }
        if (value.equals("custom2")) {
            return 17307498;
        }
        if (value.equals("custom3")) {
            return 17306378;
        }
        Log.e(TAG, "error");
        return 0;
    }

    private void logcat(String str) {
        Log.i(TAG, str);
    }

    @Override // com.sony.imaging.app.base.common.IModableLayout
    public void onLayoutModeChanged(int device, int displayMode) {
        updateView();
    }
}
