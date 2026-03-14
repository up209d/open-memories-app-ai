package com.sony.imaging.app.base.menu.layout;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.sony.imaging.app.base.R;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.caution.IkeyDispatchEach;
import com.sony.imaging.app.base.caution.Info;
import com.sony.imaging.app.base.common.widget.FooterGuide;
import com.sony.imaging.app.base.menu.ExposureModeMenuService;
import com.sony.imaging.app.base.shooting.camera.ExposureModeController;
import com.sony.imaging.app.base.shooting.camera.FocusModeController;
import com.sony.imaging.app.base.shooting.trigger.ModeDialDetector;
import com.sony.imaging.app.fw.IKeyFunction;
import com.sony.imaging.app.portraitbeauty.playback.catchlight.layout.CatchLightPlayBackLayout;
import com.sony.imaging.app.util.Environment;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* loaded from: classes.dex */
public class ExposureModeMenuLayout extends SubMenuLayout {
    private static final int BACKGROUND_H = 480;
    private static final int BACKGROUND_W = 556;
    private static final int BACKGROUND_X_L = 84;
    private static final int BACKGROUND_X_R = 0;
    private static final int BACKGROUND_Y_L = 0;
    private static final int BACKGROUND_Y_R = 0;
    private static final int ENABLE_TRANSITION_TO_MENU_VER = 2;
    private static final int FOOTER_H = 40;
    private static final int FOOTER_W = 484;
    private static final int FOOTER_X_L = 148;
    private static final int FOOTER_X_R = 8;
    private static final int FOOTER_Y_L = 432;
    private static final int FOOTER_Y_R = 432;
    private static final int GUIDE_VIEW_H = 272;
    private static final int GUIDE_VIEW_W = 484;
    private static final int GUIDE_VIEW_X_L = 148;
    private static final int GUIDE_VIEW_X_R = 8;
    private static final int GUIDE_VIEW_Y_L = 100;
    private static final int GUIDE_VIEW_Y_R = 100;
    private static final int INVISIBLE_ALPHA = 128;
    public static final String MENU_ID = "ID_EXPOSUREMODEMENULAYOUT";
    private static final int MODEDIAL_H = 480;
    private static final int MODEDIAL_W = 144;
    private static final int MODEDIAL_X_L = 0;
    private static final int MODEDIAL_X_R = 496;
    private static final int MODEDIAL_Y_L = 0;
    private static final int MODEDIAL_Y_R = 0;
    private static final int MODE_NAME_H = 46;
    private static final int MODE_NAME_W = 484;
    private static final int MODE_NAME_X_L = 148;
    private static final int MODE_NAME_X_R = 8;
    private static final int MODE_NAME_Y_L = 5;
    private static final int MODE_NAME_Y_R = 5;
    private static final String TAG = "ExposureModeSubMenuLayout";
    private static ArrayList<String> mSortedDisplayMenuItems;
    private ImageView mBackgroundImageView;
    private CautionUtilityClass.triggerCautionCallback mCallback = null;
    private int mDialPosition;
    private FooterGuide mFooterTextView;
    private SparseArray<String> mGuidePool;
    private TextView mGuideTextView;
    private boolean mHasModeDial;
    protected boolean mIsCanceling;
    private TextView mModeNameTextView;
    private SparseArray<String> mNamePool;

    @Override // com.sony.imaging.app.base.menu.layout.SubMenuLayout
    public ViewGroup createView() {
        this.mHasModeDial = ModeDialDetector.hasModeDial();
        this.mNamePool = new SparseArray<>();
        this.mGuidePool = new SparseArray<>();
        ViewGroup currentView = (ViewGroup) obtainViewFromPool(R.layout.menu_exposure_mode_option);
        Context context = getActivity().getApplicationContext();
        this.mSubMenuView = (SubMenuView) currentView.findViewById(R.id.SubMenuView);
        ((ExposureModeDialView) this.mSubMenuView).enableTouchMode(false);
        ExposureModeMenuService service = new ExposureModeMenuService(context);
        this.mDialPosition = ModeDialDetector.getDialPosition();
        this.mService = service;
        this.mAdapter = new ExposureModeMenuAdapter(context, R.layout.menu_exposure_mode_adapter, this.mService);
        this.mScreenTitleView = (TextView) currentView.findViewById(R.id.menu_screen_title);
        this.mModeNameTextView = (TextView) currentView.findViewById(R.id.mode_name);
        this.mGuideTextView = (TextView) currentView.findViewById(R.id.guide_view);
        this.mFooterTextView = (FooterGuide) currentView.findViewById(R.id.footer_guide);
        this.mBackgroundImageView = (ImageView) currentView.findViewById(R.id.background_icon);
        setUpChildViews(this.mDialPosition, context);
        return currentView;
    }

    @Override // com.sony.imaging.app.base.menu.layout.SubMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public void onDestroyView() {
        this.mSubMenuView = null;
        this.mService = null;
        this.mAdapter = null;
        this.mScreenTitleView = null;
        this.mModeNameTextView = null;
        this.mGuideTextView = null;
        this.mBackgroundImageView = null;
        this.mFooterTextView = null;
        super.onDestroyView();
    }

    @Override // com.sony.imaging.app.base.menu.layout.SubMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        this.mIsCanceling = false;
        if (this.mHasModeDial) {
            moveToDialPosition();
        }
        FocusModeController.getInstance().initFocusControl();
    }

    @Override // com.sony.imaging.app.base.menu.layout.SubMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout
    public void closeLayout() {
        if (this.mService != null) {
            if (ModeDialDetector.hasModeDial() && !ExposureModeController.getInstance().isValidDialPosition()) {
                this.mIsCanceling = true;
            }
            if (this.mIsCanceling) {
                cancelSetValue();
            } else {
                setCurrentMenuItem();
            }
        }
        super.closeLayout();
    }

    @Override // com.sony.imaging.app.base.menu.layout.SubMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout
    protected ArrayList<String> getMenuItemList() {
        if (mSortedDisplayMenuItems == null) {
            if (this.mHasModeDial) {
                ArrayList<String> items = this.mService.getMenuItemList();
                List<String> supporteds = ExposureModeController.getInstance().getSupportedValue(ExposureModeController.EXPOSURE_MODE);
                mSortedDisplayMenuItems = new ArrayList<>();
                int length = supporteds.size();
                for (int i = 0; i < length; i++) {
                    String mode = supporteds.get(i);
                    if (mode.startsWith("unknown")) {
                        String unknownItemId = null;
                        ArrayList<String> list = this.mService.getMenuItemList();
                        Iterator i$ = list.iterator();
                        while (i$.hasNext()) {
                            String itemId = i$.next();
                            if ("unknown".equals(this.mService.getMenuItemValue(itemId))) {
                                unknownItemId = itemId;
                            }
                        }
                        if (unknownItemId != null) {
                            mSortedDisplayMenuItems.add(unknownItemId);
                        }
                    } else {
                        Iterator i$2 = items.iterator();
                        while (true) {
                            if (i$2.hasNext()) {
                                String itemId2 = i$2.next();
                                String value = this.mService.getMenuItemValue(itemId2);
                                if (mode.equals(value)) {
                                    mSortedDisplayMenuItems.add(itemId2);
                                    break;
                                }
                            }
                        }
                    }
                }
            } else {
                mSortedDisplayMenuItems = this.mService.getSupportedItemList();
            }
        }
        this.mService.updateValueItemsAvailable(mSortedDisplayMenuItems);
        return mSortedDisplayMenuItems;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.menu.layout.SubMenuLayout
    public void update(int position) {
        super.update(position);
        updateModeName(this.mAdapter, position);
        updateGuide(this.mAdapter, position);
        updateBackground((ExposureModeMenuAdapter) this.mAdapter, position);
    }

    @Override // com.sony.imaging.app.base.menu.layout.SubMenuLayout, android.widget.AdapterView.OnItemSelectedListener
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        this.mAdapter.getItem(position);
        super.onItemSelected(parent, view, position, id);
        update(position);
    }

    private void updateModeName(BaseMenuAdapter adapter, int position) {
        String itemId = adapter.getItem(position);
        String name = this.mNamePool.get(position);
        if (name == null) {
            name = (String) this.mService.getMenuItemText(itemId);
            this.mNamePool.put(position, name);
        }
        this.mModeNameTextView.setText(name);
        int nameColor = getResources().getColor(R.color.RESID_FONTSTYLE_STD_NORMAL);
        if (!this.mService.isMenuItemValid(itemId)) {
            nameColor = Color.argb(INVISIBLE_ALPHA, Color.red(nameColor), Color.green(nameColor), Color.blue(nameColor));
        }
        this.mModeNameTextView.setTextColor(nameColor);
    }

    private void updateGuide(BaseMenuAdapter adapter, int position) {
        String itemId = adapter.getItem(position);
        String guide = this.mGuidePool.get(position);
        if (guide == null) {
            guide = (String) this.mService.getMenuItemGuideText(itemId);
            this.mGuidePool.put(position, guide);
        }
        this.mGuideTextView.setText(guide);
        int guideColor = getResources().getColor(R.color.RESID_FONTSTYLE_CMN);
        if (!this.mService.isMenuItemValid(itemId)) {
            guideColor = Color.argb(INVISIBLE_ALPHA, Color.red(guideColor), Color.green(guideColor), Color.blue(guideColor));
        }
        this.mGuideTextView.setTextColor(guideColor);
        this.mGuideTextView.setVisibility(0);
    }

    private void disappearGuide() {
        this.mGuideTextView.setVisibility(4);
    }

    private void updateBackground(ExposureModeMenuAdapter adapter, int position) {
        Drawable background = adapter.getBackgroundDrawable(position);
        if (background == null) {
            this.mBackgroundImageView.setBackgroundResource(android.R.color.black);
        } else {
            this.mBackgroundImageView.setBackgroundResource(0);
        }
        this.mBackgroundImageView.setImageDrawable(background);
    }

    private void setUpChildViews(int dialPosition, Context context) {
        RelativeLayout.LayoutParams modename_param = new RelativeLayout.LayoutParams(484, MODE_NAME_H);
        RelativeLayout.LayoutParams guideview_param = new RelativeLayout.LayoutParams(484, GUIDE_VIEW_H);
        RelativeLayout.LayoutParams footer_param = new RelativeLayout.LayoutParams(484, 40);
        RelativeLayout.LayoutParams background_param = new RelativeLayout.LayoutParams(BACKGROUND_W, CatchLightPlayBackLayout.mLayoutParams_faceZoom__height);
        RelativeLayout.LayoutParams modedial_param = new RelativeLayout.LayoutParams(MODEDIAL_W, CatchLightPlayBackLayout.mLayoutParams_faceZoom__height);
        if (dialPosition == 2) {
            modename_param.leftMargin = 148;
            modename_param.topMargin = 5;
            guideview_param.leftMargin = 148;
            guideview_param.topMargin = 100;
            footer_param.leftMargin = 148;
            footer_param.topMargin = 432;
            background_param.leftMargin = BACKGROUND_X_L;
            background_param.topMargin = 0;
            modedial_param.leftMargin = 0;
            modedial_param.topMargin = 0;
        } else {
            modename_param.leftMargin = 8;
            modename_param.topMargin = 5;
            guideview_param.leftMargin = 8;
            guideview_param.topMargin = 100;
            footer_param.leftMargin = 8;
            footer_param.topMargin = 432;
            background_param.leftMargin = 0;
            background_param.topMargin = 0;
            modedial_param.leftMargin = MODEDIAL_X_R;
            modedial_param.topMargin = 0;
        }
        this.mModeNameTextView.setLayoutParams(modename_param);
        this.mGuideTextView.setLayoutParams(guideview_param);
        this.mFooterTextView.setLayoutParams(footer_param);
        this.mBackgroundImageView.setLayoutParams(background_param);
        this.mSubMenuView.setLayoutParams(modedial_param);
    }

    @Override // com.sony.imaging.app.base.menu.layout.SubMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedUpKey() {
        if (this.mHasModeDial) {
            return -1;
        }
        return super.pushedUpKey();
    }

    @Override // com.sony.imaging.app.base.menu.layout.SubMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedDownKey() {
        if (this.mHasModeDial) {
            return -1;
        }
        return super.pushedDownKey();
    }

    @Override // com.sony.imaging.app.base.menu.layout.SubMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMenuKey() {
        if (Environment.getVersionPfAPI() >= 2) {
            String itemId = getCurrentMenuItem();
            if (!this.mService.isMenuItemValid(itemId)) {
                closeMenuLayout(null);
            } else {
                CautionUtilityClass.getInstance().executeTerminate();
                if (ModeDialDetector.hasModeDial()) {
                    if (!ExposureModeController.getInstance().isValidDialPosition()) {
                        this.mIsCanceling = true;
                    }
                } else {
                    this.mIsCanceling = true;
                }
                if (this.mIsCanceling) {
                    cancelSetValue();
                } else {
                    setCurrentMenuItem();
                }
                if (!this.mService.isEmptyMenuHistory()) {
                    openPreviousMenu();
                } else if (ModeDialDetector.hasModeDial()) {
                    openRootMenu();
                } else {
                    closeMenuLayout(null);
                }
            }
        } else {
            CautionUtilityClass.getInstance().executeTerminate();
            if (ModeDialDetector.hasModeDial()) {
                if (!ExposureModeController.getInstance().isValidDialPosition()) {
                    this.mIsCanceling = true;
                }
            } else {
                this.mIsCanceling = true;
            }
            if (this.mIsCanceling) {
                cancelSetValue();
            } else {
                setCurrentMenuItem();
            }
            if (!this.mService.isEmptyMenuHistory()) {
                openPreviousMenu();
            } else {
                closeMenuLayout(null);
            }
        }
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.SubMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedMainDialPrev() {
        if (this.mHasModeDial) {
            return -1;
        }
        return super.turnedMainDialPrev();
    }

    @Override // com.sony.imaging.app.base.menu.layout.SubMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedMainDialNext() {
        if (this.mHasModeDial) {
            return -1;
        }
        return super.turnedMainDialNext();
    }

    @Override // com.sony.imaging.app.base.menu.layout.SubMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedSubDialPrev() {
        if (this.mHasModeDial) {
            return -1;
        }
        return super.turnedMainDialNext();
    }

    @Override // com.sony.imaging.app.base.menu.layout.SubMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedSubDialNext() {
        if (this.mHasModeDial) {
            return -1;
        }
        return super.turnedMainDialPrev();
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedModeDial() {
        if (this.mHasModeDial) {
            moveToDialPosition();
        }
        return -1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedSettingFuncCustomKey(IKeyFunction keyFunction) {
        return -1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMfAssistCustomKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedAfMfHoldCustomKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int releasedAfMfHoldCustomKey() {
        return 0;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedAfMfToggleCustomKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int releasedAELHoldCustomKey() {
        return 0;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int releasedFocusHoldCustomKey() {
        return 0;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedAELHoldCustomKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedFocusHoldCustomKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedAELToggleCustomKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedAFMFKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedEVKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedIsoKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedWBKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedDriveModeKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedSmartTeleconKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedExpandFocusKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedPreviewKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedDigitalZoomKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedAfRangeKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedPeakingKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedProjectorKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedZebraKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedFocusKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedFnKey() {
        if (Environment.getVersionPfAPI() >= 2) {
            String itemId = getCurrentMenuItem();
            if (!this.mService.isMenuItemValid(itemId)) {
                closeMenuLayout(null);
                return 1;
            }
        }
        CautionUtilityClass.getInstance().executeTerminate();
        boolean isEnableMode = false;
        if (ModeDialDetector.hasModeDial()) {
            if (ExposureModeController.getInstance().isValidDialPosition()) {
                isEnableMode = true;
            }
        } else {
            isEnableMode = true;
        }
        if (isEnableMode) {
            return super.pushedFnKey();
        }
        closeMenuLayout(null);
        return 1;
    }

    private void setCurrentMenuItem() {
        String itemId = getCurrentMenuItem();
        String value = this.mService.getMenuItemValue(itemId);
        ExposureModeController.getInstance().setValue(ExposureModeController.EXPOSURE_MODE, value);
    }

    private String getCurrentMenuItem() {
        int code;
        String mode;
        ArrayList<String> list = getMenuItemList();
        String itemId = list.get(this.mPosition);
        if (this.mHasModeDial && -1 != (code = ModeDialDetector.getModeDialPosition()) && (mode = ExposureModeController.scancode2Value(code)) != null) {
            return "ExposureMode_" + mode;
        }
        return itemId;
    }

    private void moveToDialPosition() {
        String mode;
        int code = ModeDialDetector.getModeDialPosition();
        if (-1 != code && (mode = ExposureModeController.scancode2Value(code)) != null) {
            ArrayList<String> list = getMenuItemList();
            int position = 0;
            while (position < list.size()) {
                String value = this.mService.getMenuItemValue(list.get(position));
                if (mode.equals(value)) {
                    break;
                } else {
                    position++;
                }
            }
            Log.d(TAG, "mode dial = " + position);
            ((ExposureModeDialView) this.mSubMenuView).moveTo(position);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout
    public void requestCautionTrigger(String itemId) {
        ExposureModeController emc = ExposureModeController.getInstance();
        if (emc.getSupportedValue(ExposureModeController.EXPOSURE_MODE).contains(itemId)) {
            if (!emc.isAvailable(itemId)) {
                displayNotSupportedCaution();
                super.requestCautionTrigger(itemId);
                return;
            }
            return;
        }
        displayNotSupportedCaution();
    }

    protected void displayNotSupportedCaution() {
        if (getCautionId() != 65535) {
            this.mCallback = new disappearCautionCallback();
            CautionUtilityClass.getInstance().setDispatchKeyEvent(getCautionId(), getKeyHandler());
            CautionUtilityClass.getInstance().requestTrigger(getCautionId());
            CautionUtilityClass.getInstance().registerCallbackTriggerDisapper(this.mCallback);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class disappearCautionCallback implements CautionUtilityClass.triggerCautionCallback {
        disappearCautionCallback() {
        }

        @Override // com.sony.imaging.app.base.caution.CautionUtilityClass.triggerCautionCallback
        public void onCallback() {
            CautionUtilityClass.getInstance().unregisterCallbackTriggerDisapper(this);
            CautionUtilityClass.getInstance().disapperTrigger(ExposureModeMenuLayout.this.getCautionId());
            CautionUtilityClass.getInstance().setDispatchKeyEvent(ExposureModeMenuLayout.this.getCautionId(), null);
            ExposureModeMenuLayout.this.mCallback = null;
        }
    }

    protected int getCautionId() {
        return Info.CAUTION_ID_DLAPP_PASM_IAUTO_SCN_OK;
    }

    protected IkeyDispatchEach getKeyHandler() {
        IkeyDispatchEach changeModeKeyHandler = new IkeyDispatchEach() { // from class: com.sony.imaging.app.base.menu.layout.ExposureModeMenuLayout.1
            @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
            public int pushedS1Key() {
                CautionUtilityClass.getInstance().executeTerminate();
                return 0;
            }

            @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
            public int pushedPlayBackKey() {
                CautionUtilityClass.getInstance().executeTerminate();
                return 0;
            }

            @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
            public int pushedMovieRecKey() {
                CautionUtilityClass.getInstance().executeTerminate();
                return 0;
            }

            @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
            public int pushedUmRemoteRecKey() {
                CautionUtilityClass.getInstance().executeTerminate();
                return 0;
            }

            @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
            public int pushedIRRecKey() {
                CautionUtilityClass.getInstance().executeTerminate();
                return 0;
            }

            @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
            public int pushedShootingModeKey() {
                CautionUtilityClass.getInstance().executeTerminate();
                return -1;
            }

            @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
            public int turnedModeDial() {
                CautionUtilityClass.getInstance().executeTerminate();
                return 0;
            }

            @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
            public int pushedCenterKey() {
                CautionUtilityClass.getInstance().executeTerminate();
                return 1;
            }

            @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
            public int turnedEVDial() {
                CautionUtilityClass.getInstance().executeTerminate();
                return 0;
            }

            @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
            public int turnedFocusModeDial() {
                CautionUtilityClass.getInstance().executeTerminate();
                return 0;
            }
        };
        return changeModeKeyHandler;
    }
}
