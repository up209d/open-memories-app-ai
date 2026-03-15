package com.sony.imaging.app.manuallenscompensation.menu.layout;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.menu.BaseMenuService;
import com.sony.imaging.app.base.menu.MenuDataParcelable;
import com.sony.imaging.app.base.menu.MenuTable;
import com.sony.imaging.app.base.menu.layout.BaseMenuLayout;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.widget.AppNameConstantView;
import com.sony.imaging.app.base.shooting.widget.AppNameView;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.fw.CustomizableFunction;
import com.sony.imaging.app.fw.IKeyFunction;
import com.sony.imaging.app.manuallenscompensation.OpticalCompensation;
import com.sony.imaging.app.manuallenscompensation.R;
import com.sony.imaging.app.manuallenscompensation.caution.layout.OCInfo;
import com.sony.imaging.app.manuallenscompensation.commonUtil.AppLog;
import com.sony.imaging.app.manuallenscompensation.commonUtil.OCBackUpKey;
import com.sony.imaging.app.manuallenscompensation.commonUtil.OCConstants;
import com.sony.imaging.app.manuallenscompensation.commonUtil.OCUtil;
import com.sony.imaging.app.manuallenscompensation.database.LensCompensationParameter;
import com.sony.imaging.app.manuallenscompensation.menu.controller.ExternalMediaStateController;
import com.sony.imaging.app.manuallenscompensation.widget.OCAppNameFocalValue;
import com.sony.imaging.app.util.BackUpUtil;

/* loaded from: classes.dex */
public class LensListMenuLayout extends LensListBaseMenuLayout implements View.OnTouchListener {
    private static final String BACK = "back";
    private static final String MSG_CAUTION_ALREADY_DISPLAYED = "Caution already displayed";
    private static final String MSG_DISAPPEAR_CAUTION_ON_PAUSED = "disappear Caution onPause";
    private static final String SELECTED_PROFILE_ID = "Selected profile id = ";
    public static final String TAG = "ID_LENSLISTMENULAYOUT";
    private static int mLastSelectedItemPos = 0;
    private static BaseAdapter mLensSelectorAdapter;
    protected CautionCallback mCautionCallback;
    private final String NEED_TO_CREATE_DEFAULT_PROFILE = "No profile exist so need to create new default profile";
    private final String NO_NEED_TO_CREATE_DEFAULT_PROFILE = "A profile already exist so no need to create new default profile";
    private final String LENS_LIST_SCREEN_BOOTED = "Start Lense List screen";
    private final String LENS_LIST_CONSTRUCTOR = "Lense List constructor";
    private final String Last_VALUE_SAVED = "Last value saved for cancel operations";
    private View.OnClickListener mOnOptionClickListener = null;
    private boolean isCustomOperationPerformed = false;
    private int mOptionItemPosition = 0;
    private int mDeletedProfileID = -1;
    private int mCautionID = 0;
    protected final String[] TAGS = {OCConstants.TAG_DELETE_PROCESS};
    boolean isMenuPressed = false;

    public LensListMenuLayout() {
        AppLog.enter(TAG, "Lense List constructor");
        AppLog.exit(TAG, "Lense List constructor");
    }

    @Override // com.sony.imaging.app.manuallenscompensation.menu.layout.LensListBaseMenuLayout
    public BaseAdapter getAdapter(Context context, Cursor cursor) {
        AppLog.enter(TAG, AppLog.getMethodName());
        createFooterView();
        this.mTitle.setText(R.string.STRID_FUNC_OPTICAL_COMPENSATION_PROFILE_LIST);
        mLensSelectorAdapter = new LensSelectionAdapter(context, cursor);
        AppLog.exit(TAG, AppLog.getMethodName());
        return mLensSelectorAdapter;
    }

    protected void checkForImportSuccess() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (this.data != null) {
            if (this.data.containsKey(OCConstants.TAG_IMPORT_SUCCESSFUL) && this.data.getBoolean(OCConstants.TAG_IMPORT_SUCCESSFUL)) {
                AppLog.trace(TAG, AppLog.getMethodName() + OCConstants.TAG_IMPORT_SUCCESSFUL);
                this.data.remove(OCConstants.TAG_IMPORT_SUCCESSFUL);
                mLastSelectedItemPos = mLensSelectorAdapter.getCount() - 1;
            } else if (this.data.containsKey(OCConstants.PROFILE_PROCESS_ERROR)) {
                if (2 == this.data.getInt(OCConstants.PROFILE_PROCESS_ERROR)) {
                    AppLog.trace(TAG, AppLog.getMethodName() + OCConstants.PROFILE_PROCESS_ERROR);
                    mLastSelectedItemPos = mLensSelectorAdapter.getCount() + 1;
                }
                this.data.remove(OCConstants.PROFILE_PROCESS_ERROR);
            } else {
                AppLog.trace(TAG, AppLog.getMethodName());
            }
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.manuallenscompensation.menu.layout.LensListBaseMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        AppLog.enter(TAG, AppLog.getMethodName());
        View view = super.onCreateView(inflater, container, savedInstanceState);
        setPreviousMenuID();
        this.mDeletedProfileID = -1;
        AppLog.exit(TAG, AppLog.getMethodName());
        return view;
    }

    @Override // com.sony.imaging.app.manuallenscompensation.menu.layout.LensListBaseMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        AppLog.enter(TAG, AppLog.getMethodName());
        super.onResume();
        this.isMenuPressed = false;
        AppLog.trace(TAG, "Start Lense List screen");
        initializeView();
        showFooterGuideDrawable(0);
        initializeListener();
        if (mLensSelectorAdapter == null) {
            updateAdapter();
        }
        if (mLastSelectedItemPos < mLensSelectorAdapter.getCount()) {
            showRightPane(0);
            updateRightPane(mLastSelectedItemPos, mLensSelectorAdapter);
            displayOptionFooteGuide();
        } else {
            clearRightPane(0);
            disapearOptionFooterGuide();
        }
        CameraNotificationManager.getInstance().setNotificationListener(this);
        setTitleForSceeen(0, getResources().getString(R.string.STRID_FUNC_OPTICAL_COMPENSATION_PROFILE_LIST));
        this.mEditButton.setOnTouchListener(this);
        this.mDeleteButton.setOnTouchListener(this);
        this.mExportButton.setOnTouchListener(this);
        this.mListView.setOnTouchListener(this);
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.manuallenscompensation.menu.layout.LensListBaseMenuLayout
    protected void initializeView() {
        AppLog.enter(TAG, AppLog.getMethodName());
        updateAdapter();
        AppLog.info(TAG, "INSIE initializeView, position = " + mLastSelectedItemPos);
        setBackGroundImageDrawable(0);
        this.mListView.setAdapter((ListAdapter) mLensSelectorAdapter);
        this.mListView.setOnItemSelectedListener(this);
        checkForImportSuccess();
        this.mListView.requestFocus();
        this.mListView.setDividerHeight(0);
        this.mListView.setSelection(mLastSelectedItemPos);
        this.mListView.setFocusable(true);
        this.mListView.setEnabled(true);
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    private void updateAdapter() {
        Context context = getActivity().getApplicationContext();
        this.mService = new BaseMenuService(context);
        this.mCursor = LensCompensationParameter.queryAllProfilesFromDatabase(context);
        mLensSelectorAdapter = getAdapter(context, this.mCursor);
    }

    protected void setPreviousMenuID() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (this.data != null) {
            MenuDataParcelable parcelable = (MenuDataParcelable) this.data.getParcelable(MenuDataParcelable.KEY);
            if (!BACK.equals(parcelable.getItemId())) {
                this.mLastItemId = getMenuHistoryItem();
                if (this.mLastItemId != null) {
                    this.mPreviousMenuLayoutID = this.mLastItemId.layoutId;
                    this.mLastSelectedID = BackUpUtil.getInstance().getPreferenceInt(OCBackUpKey.KEY_BKUP_PREV_RECORD_ID, -1);
                    AppLog.info(TAG, AppLog.getMethodName() + "Last value saved for cancel operations");
                }
                mLastSelectedItemPos = BackUpUtil.getInstance().getPreferenceInt(OCBackUpKey.KEY_BKUP_SELECTED_POSITION, 0);
                this.mRestoreOldValue = mLastSelectedItemPos;
            }
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.manuallenscompensation.menu.layout.LensListBaseMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (this.mCautionCallback != null) {
            AppLog.trace(TAG, MSG_DISAPPEAR_CAUTION_ON_PAUSED);
            AppLog.trace(TAG, "" + this.mCautionID);
            CautionUtilityClass.getInstance().unregisterCallbackTriggerDisapper(this.mCautionCallback);
            CautionUtilityClass.getInstance().disapperTrigger(this.mCautionID);
            this.mCautionCallback = null;
        }
        CameraNotificationManager.getInstance().removeNotificationListener(this);
        this.isMenuPressed = false;
        this.isCustomOperationPerformed = false;
        super.onPause();
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.manuallenscompensation.menu.layout.LensListBaseMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public void onDestroyView() {
        mLensSelectorAdapter = null;
        super.onDestroyView();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.manuallenscompensation.menu.layout.LensListBaseMenuLayout
    public void initializeListener() {
        AppLog.enter(TAG, AppLog.getMethodName());
        super.initializeListener();
        if (this.mOnOptionClickListener == null) {
            this.mOnOptionClickListener = new OnOptionClickListener();
        }
        this.mEditButton.setOnClickListener(this.mOnOptionClickListener);
        this.mDeleteButton.setOnClickListener(this.mOnOptionClickListener);
        this.mExportButton.setOnClickListener(this.mOnOptionClickListener);
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    protected void createFooterView() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (this.mFooterCreateView != null) {
            this.mListView.removeFooterView(this.mFooterCreateView);
            this.mFooterCreateView = null;
        }
        if (this.mFooterImportView != null) {
            this.mListView.removeFooterView(this.mFooterImportView);
            this.mFooterImportView = null;
        }
        if (this.mFooterCreateView == null) {
            this.mFooterCreateView = obtainViewFromPool(R.layout.footer_view);
            this.mListView.addFooterView(this.mFooterCreateView, null, true);
            AppLog.info(TAG, AppLog.getMethodName() + "mFooterCreateView added");
            TextView txt_footerName = (TextView) this.mFooterCreateView.findViewById(R.id.footer);
            txt_footerName.setText(R.string.STRID_FUNC_OPTICAL_COMPENSATION_PROFILE_CREATE);
        }
        if (this.mFooterImportView == null) {
            this.mFooterImportView = obtainViewFromPool(R.layout.footer_view_import);
            this.mListView.addFooterView(this.mFooterImportView, null, true);
            AppLog.info(TAG, AppLog.getMethodName() + "mFooterImportView added");
            TextView txt_footerImportName = (TextView) this.mFooterImportView.findViewById(R.id.footer_import);
            txt_footerImportName.setText(R.string.STRID_FUNC_OPTICAL_COMPENSATION_PROFILE_IMPORT);
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    private void backTransition() {
        AppLog.enter(TAG, AppLog.getMethodName());
        setLastAppliedProfile();
        if (this.data != null && this.data.containsKey(LensCompensationParameter.ID_PARAMETER)) {
            this.data.remove(LensCompensationParameter.ID_PARAMETER);
        }
        openPreviousMenu();
        AppNameConstantView.setText((String) getResources().getText(R.string.STRID_FUNC_OPTICAL_COMPENSATION));
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    private void setLastAppliedProfile() {
        LensCompensationParameter params;
        AppLog.enter(TAG, AppLog.getMethodName());
        if (mLensSelectorAdapter != null && (params = (LensCompensationParameter) mLensSelectorAdapter.getItem(mLastSelectedItemPos)) != null && this.mLastSelectedID != params.getId()) {
            LensCompensationParameter params1 = LensCompensationParameter.queryPreviousProfileParam(getActivity().getApplicationContext(), this.mLastSelectedID);
            if (params1 == null) {
                applyParamOnClose(params);
            }
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMenuKey() {
        int ret;
        AppLog.enter(TAG, AppLog.getMethodName());
        if (this.mOptionView.getVisibility() == 0) {
            this.mOptionView.setVisibility(8);
            this.mListView.setEnabled(true);
            this.mListView.setFocusable(true);
            this.mListView.setOnItemSelectedListener(this);
            ret = 1;
        } else {
            mLastSelectedItemPos = this.mRestoreOldValue;
            if (this.mPreviousMenuLayoutID == null && this.mLastItemId == null) {
                if (this.mListView != null) {
                    this.mListView.setOnItemSelectedListener(null);
                }
                OCUtil.getInstance().setLauncherBooted(true);
                getActivity().finish();
            } else {
                backTransition();
            }
            ret = 1;
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return ret;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedDeleteKey() {
        AppLog.enter(TAG, AppLog.getMethodName());
        int ret = 0;
        if (mLastSelectedItemPos >= mLensSelectorAdapter.getCount() || mLensSelectorAdapter.getCount() <= this.mListView.getSelectedItemPosition()) {
            ret = -1;
        } else if (this.mOptionView.getVisibility() != 0) {
            this.mListView.setOnItemSelectedListener(null);
            this.mOptionView.setVisibility(0);
            this.mEditButton.setFocusableInTouchMode(true);
            this.mEditButton.setFocusable(true);
            this.mEditButton.requestFocus();
            this.mOptionItemPosition = 0;
            handleOptionMenueSelectPosition(this.mOptionItemPosition);
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return ret;
    }

    @Override // android.widget.AdapterView.OnItemSelectedListener
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.info(TAG, "INSIDE onItemSelected");
        AppLog.info(TAG, "INSIDE pos = " + pos);
        mLastSelectedItemPos = pos;
        if (mLensSelectorAdapter == null) {
            updateAdapter();
        }
        int totalCount = mLensSelectorAdapter.getCount();
        if (pos < totalCount) {
            showRightPane(0);
            updateRightPane(pos, mLensSelectorAdapter);
            displayOptionFooteGuide();
        } else if (pos >= totalCount) {
            clearRightPane(0);
            disapearOptionFooterGuide();
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    private void disapearOptionFooterGuide() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (this.mLastItemId == null) {
            showFooterGuide(android.R.string.hour, android.R.string.hour, android.R.string.hour_picker_description, android.R.string.hour_picker_description);
        } else {
            showFooterGuide(android.R.string.hour, android.R.string.zen_mode_feature_name, android.R.string.hour_picker_description, android.R.string.httpErrorRedirectLoop);
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    private void displayOptionFooteGuide() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (this.mLastItemId == null) {
            showFooterGuide(R.string.STRID_FOOTERGUIDE_OPTION_EXITAPP_BY_MENU, R.string.STRID_FOOTERGUIDE_OPTION_RETURN_BY_MENU, R.string.STRID_FOOTERGUIDE_OPTION_EXITAPP_FOR_SK, R.string.STRID_FOOTERGUIDE_OPTION_RETURN_FOR_SK);
        } else {
            showFooterGuide(R.string.STRID_FOOTERGUIDE_OPTION_EXITAPP_BY_MENU, R.string.STRID_FOOTERGUIDE_OPTION_RETURN_BY_MENU, R.string.STRID_FOOTERGUIDE_OPTION_RETURN_BY_MENU, R.string.STRID_FOOTERGUIDE_OPTION_RETURN_FOR_SK);
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class OnOptionClickListener implements View.OnClickListener {
        private OnOptionClickListener() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View v) {
            AppLog.enter(LensListMenuLayout.TAG, AppLog.getMethodName());
            LensListMenuLayout.this.optionActionPerform(v);
            AppLog.exit(LensListMenuLayout.TAG, AppLog.getMethodName());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void optionActionPerform(View v) {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (this.mEditButton == v) {
            editParam();
        } else if (this.mDeleteButton == v) {
            BackUpUtil.getInstance().setPreference(OCBackUpKey.KEY_BKUP_SELECTED_POSITION, Integer.valueOf(mLastSelectedItemPos));
            openNextActionMenuLayout(0);
        } else if (this.mExportButton == v) {
            if (OCUtil.getInstance().checkMediaStatus()) {
                if (OCUtil.getInstance().isMediaErr()) {
                    displayCaution(OCInfo.CAUTION_ID_DLAPP_MEMORY_CARD_LOCKED);
                } else if (!OCUtil.getInstance().checkMemorySpace()) {
                    if (OCUtil.getInstance().isMaxProfileLimitExceed()) {
                        displayCaution(OCInfo.CAUTION_ID_DLAPP_CANNOT_EXPORT_MORE_PROFILE);
                    } else {
                        openNextActionMenuLayout(1);
                    }
                }
            } else {
                displayCaution(OCInfo.CAUTION_ID_DLAPP_NO_MEMORY_CARD_INSERTED);
            }
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class CautionCallback implements CautionUtilityClass.triggerCautionCallback {
        CautionCallback() {
        }

        @Override // com.sony.imaging.app.base.caution.CautionUtilityClass.triggerCautionCallback
        public void onCallback() {
            AppLog.enter(LensListMenuLayout.TAG, AppLog.getMethodName());
            CautionUtilityClass.getInstance().unregisterCallbackTriggerDisapper(this);
            LensListMenuLayout.this.mCautionCallback = null;
            LensListMenuLayout.this.closeOptionScreen();
            AppLog.exit(LensListMenuLayout.TAG, AppLog.getMethodName());
        }
    }

    private void displayCaution(int cautionId) {
        AppLog.enter(TAG, AppLog.getMethodName());
        this.mCautionID = cautionId;
        if (this.mCautionCallback != null) {
            AppLog.trace(TAG, MSG_CAUTION_ALREADY_DISPLAYED);
        }
        this.mCautionCallback = new CautionCallback();
        CautionUtilityClass.getInstance().requestTrigger(cautionId);
        CautionUtilityClass.getInstance().registerCallbackTriggerDisapper(this.mCautionCallback);
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void closeOptionScreen() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (this.mOptionView.getVisibility() == 0) {
            this.mOptionView.setVisibility(8);
            this.mListView.setOnItemSelectedListener(this);
            this.mListView.setEnabled(true);
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout
    public void closeMenuLayout(Bundle bundle) {
        AppLog.enter(TAG, AppLog.getMethodName());
        OCUtil.getInstance().setCloseMenuLayoutExecuted(true);
        updateParametersOnClose(bundle);
        if (bundle == null) {
            OpticalCompensation.sIsFirstTimeLaunched = false;
        }
        super.closeMenuLayout(bundle);
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout
    public void closeLayout() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (!OCUtil.getInstance().isCloseMenuLayoutExecuted()) {
            updateParametersOnClose(this.data);
        }
        this.mOnOptionClickListener = null;
        super.closeLayout();
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    protected void updateParametersOnClose(Bundle bundle) {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.info(TAG, "INSIDE closeLayout, lastPos = " + mLastSelectedItemPos);
        if (mLensSelectorAdapter != null) {
            LensCompensationParameter params = (LensCompensationParameter) mLensSelectorAdapter.getItem(mLastSelectedItemPos);
            if (params != null) {
                if (this.isMenuPressed) {
                    AppNameView.setText((String) getResources().getText(R.string.STRID_FUNC_OPTICAL_COMPENSATION));
                    AppNameView.show(true);
                    OCAppNameFocalValue.setText(null);
                    OCAppNameFocalValue.show(true);
                    applyProfile(params);
                } else {
                    OCUtil.getInstance().setAppTitle(params);
                }
            }
            if (OCUtil.getInstance().checkIfMenuStateClosed(bundle) || this.isCustomOperationPerformed) {
                this.isCustomOperationPerformed = false;
                if (params != null) {
                    applyParamOnClose(params);
                } else if (this.isMenuPressed) {
                    this.isMenuPressed = false;
                } else if (mLastSelectedItemPos == mLensSelectorAdapter.getCount()) {
                    checkToCreateDefaultProfile(params);
                }
            }
        }
        if (mLensSelectorAdapter != null) {
            if (mLastSelectedItemPos > mLensSelectorAdapter.getCount() && bundle == null) {
                AppLog.trace(TAG, "Last applied profile remain selected");
                boolean isNoProfile = OCUtil.getInstance().checkforAppliedProfile();
                if (isNoProfile) {
                    OCUtil.getInstance().creatDefaultProfile(null);
                    AppLog.info(TAG, "No profile exist so need to create new default profile");
                    BackUpUtil.getInstance().setPreference(OCBackUpKey.KEY_BKUP_SELECTED_POSITION, 0);
                } else {
                    AppLog.info(TAG, "A profile already exist so no need to create new default profile");
                }
            } else if (mLastSelectedItemPos < mLensSelectorAdapter.getCount()) {
                BackUpUtil.getInstance().setPreference(OCBackUpKey.KEY_BKUP_SELECTED_POSITION, Integer.valueOf(mLastSelectedItemPos));
            }
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    private void checkToCreateDefaultProfile(LensCompensationParameter params) {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (!OCUtil.getInstance().isExitExecute()) {
            if (!this.isMenuPressed && (!isMaxLimitExceed() || this.mCursor.getCount() == 0)) {
                OCUtil.getInstance().creatDefaultProfile(params);
                BackUpUtil.getInstance().setPreference(OCBackUpKey.KEY_BKUP_SELECTED_POSITION, Integer.valueOf(mLastSelectedItemPos));
            } else if (this.mCursor.getCount() >= 100) {
                mLastSelectedItemPos = BackUpUtil.getInstance().getPreferenceInt(OCBackUpKey.KEY_BKUP_SELECTED_POSITION, -1);
                if (-1 == this.mLastSelectedID) {
                    mLastSelectedItemPos = 0;
                }
                LensCompensationParameter params2 = (LensCompensationParameter) mLensSelectorAdapter.getItem(mLastSelectedItemPos);
                applyParamOnClose(params2);
            }
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    private void applyParamOnClose(LensCompensationParameter params) {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (params != null) {
            BackUpUtil.getInstance().setPreference(OCBackUpKey.KEY_BKUP_PREV_RECORD_ID, Integer.valueOf(params.getId()));
            params.applyCompensationParameter();
            OCUtil.getInstance().setAppTitle(params);
        } else {
            BackUpUtil.getInstance().setPreference(OCBackUpKey.KEY_BKUP_PREV_RECORD_ID, -1);
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    private void openNextActionMenuLayout(int profileAction) {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (this.data == null) {
            this.data = new Bundle();
        }
        LensCompensationParameter params = null;
        if (mLensSelectorAdapter != null) {
            params = (LensCompensationParameter) mLensSelectorAdapter.getItem(mLastSelectedItemPos);
            if (profileAction == 0 && params != null) {
                this.mDeletedProfileID = params.getId();
            }
        }
        this.data.putInt(OCConstants.PROFILE_ACTION, profileAction);
        this.data.putParcelable(OCBackUpKey.KEY_BKUP_SELECTED_PARAM, params);
        openDoubleLayerMenuLayout("ID_DELETESINGLECONFIRMMENULAYOUT", this.data);
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    private void createParam() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (isMaxLimitExceed()) {
            if (this.mOptionView.getVisibility() == 0) {
                this.mOptionView.setVisibility(8);
                this.mListView.setEnabled(true);
            }
            clearRightPane(0);
            display_LimitExceedCaution();
        } else {
            LensCompensationParameter param = LensCompensationParameter.createLensCompensationParameter();
            param.applyCompensationParameter();
            this.mActionSelected = OCConstants.ACTION_SELECTED_NEW;
            openNextMenuLayout(param);
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    protected void openNextMenuLayout(LensCompensationParameter param) {
        AppLog.enter(TAG, AppLog.getMethodName());
        String nextMenu = this.mService.getMenuItemNextMenuID(OCConstants.MENU_SETTING_KIND_EDIT);
        if (this.data == null) {
            this.data = new Bundle();
        }
        this.isMenuPressed = true;
        this.data.putParcelable(LensCompensationParameter.ID_PARAMETER, param);
        this.data.putInt(TAG, mLastSelectedItemPos);
        openNextMenu(OCConstants.MENU_SETTING_KIND_EDIT, nextMenu, true, this.data);
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    private void display_LimitExceedCaution() {
        AppLog.enter(TAG, AppLog.getMethodName());
        try {
            if (mLastSelectedItemPos == mLensSelectorAdapter.getCount()) {
                CautionUtilityClass.getInstance().getCurrentCautionData();
                CautionUtilityClass.getInstance().requestTrigger(OCInfo.CAUTION_ID_DLAPP_LIMIT_EXCEED);
            } else {
                CautionUtilityClass.getInstance().requestTrigger(OCInfo.CAUTION_ID_DLAPP_CANNOT_IMPORT_MORE_PROFILE);
            }
        } catch (Exception e) {
            if (mLastSelectedItemPos == mLensSelectorAdapter.getCount()) {
                CautionUtilityClass.getInstance().requestTrigger(OCInfo.CAUTION_ID_DLAPP_LIMIT_EXCEED);
            } else {
                CautionUtilityClass.getInstance().requestTrigger(OCInfo.CAUTION_ID_DLAPP_CANNOT_IMPORT_MORE_PROFILE);
            }
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    private boolean isMaxLimitExceed() {
        AppLog.enter(TAG, AppLog.getMethodName());
        boolean maxLimitCrossed = false;
        if (this.mCursor.getCount() >= 100) {
            maxLimitCrossed = true;
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return maxLimitCrossed;
    }

    private void editParam() {
        AppLog.enter(TAG, AppLog.getMethodName());
        int position = mLastSelectedItemPos;
        if (position != mLensSelectorAdapter.getCount()) {
            LensCompensationParameter param = (LensCompensationParameter) mLensSelectorAdapter.getItem(position);
            param.applyCompensationParameter();
            this.mActionSelected = OCConstants.ACTION_SELECTED_EDIT;
            openNextMenuLayout(param);
            AppLog.exit(TAG, AppLog.getMethodName());
        }
    }

    @Override // com.sony.imaging.app.manuallenscompensation.menu.layout.LensListBaseMenuLayout, android.widget.AdapterView.OnItemClickListener
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.info("", "INSIDE onItemClick, position = " + position);
        mLastSelectedItemPos = position;
        if (position < mLensSelectorAdapter.getCount()) {
            closeMenuLayout(null);
        } else if (position == mLensSelectorAdapter.getCount()) {
            AppLog.info("", "INSIDE Create Button clicked");
            createParam();
        } else if (position > mLensSelectorAdapter.getCount()) {
            AppLog.info("", "INSIDE Import Button clicked");
            if (OCUtil.getInstance().checkMediaStatus()) {
                if (!isMaxLimitExceed()) {
                    if (OCUtil.getInstance().getFileArray().size() > 0) {
                        openNextMenu(ExternalMediaStateController.DELETE_EXTERNAL_PROFILE, ExternalProfileMenuLayout.TAG, true, null);
                    } else {
                        CautionUtilityClass.getInstance().requestTrigger(OCInfo.CAUTION_ID_DLAPP_NO_LENS_PROFILE);
                    }
                } else {
                    display_LimitExceedCaution();
                }
            } else {
                CautionUtilityClass.getInstance().requestTrigger(OCInfo.CAUTION_ID_DLAPP_NO_MEMORY_CARD_INSERTED);
            }
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.menu.layout.MenuLayoutListener
    public void onClosed(Bundle bundle) {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (bundle != null) {
            MenuDataParcelable menuData = (MenuDataParcelable) bundle.getParcelable(MenuDataParcelable.KEY);
            if (menuData == null) {
                closeLayout();
                getMenuLayoutListener().onClosed(bundle);
            } else if (MenuTable.NEXT_LAYOUT.equals(menuData.getExecType()) || MenuTable.NEXT_LAYOUT_WITHOUT_SET.equals(menuData.getExecType())) {
                String nextMenuId = menuData.getNextMenuId();
                menuData.setPreviousMenuLayoutId(getMenuLayoutID());
                bundle.putParcelable(MenuDataParcelable.KEY, menuData);
                BaseMenuLayout layout = (BaseMenuLayout) getLayout(nextMenuId);
                if (layout != null) {
                    layout.setMenuLayoutListener(getMenuLayoutListener());
                }
                openLayout(nextMenuId, bundle);
                closeLayout();
            }
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.base.menu.MenuStateClosedListener
    public void onStateClosed(Bundle bundle) {
        if (bundle == null) {
            this.isCustomOperationPerformed = true;
        }
        super.onStateClosed(bundle);
    }

    protected void openDoubleLayerMenuLayout(String nextMenuId, Bundle bundle) {
        AppLog.enter(TAG, AppLog.getMethodName());
        this.mOptionView.setVisibility(8);
        this.mListView.setOnItemSelectedListener(this);
        BaseMenuLayout layout = (BaseMenuLayout) getLayout(nextMenuId);
        if (layout != null) {
            layout.setMenuLayoutListener(getMenuLayoutListener());
        }
        openLayout(nextMenuId, bundle);
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.util.NotificationListener
    public String[] getTags() {
        return this.TAGS;
    }

    @Override // com.sony.imaging.app.util.NotificationListener
    public void onNotify(String tag) {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (OCConstants.TAG_DELETE_PROCESS.equals(tag)) {
            AppLog.trace(TAG, SELECTED_PROFILE_ID + this.mLastSelectedID);
            if (mLastSelectedItemPos != 0) {
                mLastSelectedItemPos--;
            }
            if (this.mDeletedProfileID < this.mLastSelectedID) {
                this.mRestoreOldValue--;
            }
            AppLog.trace(TAG, SELECTED_PROFILE_ID + this.mLastSelectedID);
            if (mLensSelectorAdapter != null) {
                AppLog.trace(TAG, SELECTED_PROFILE_ID + this.mLastSelectedID);
                LensCompensationParameter params = (LensCompensationParameter) mLensSelectorAdapter.getItem(mLastSelectedItemPos);
                AppLog.trace(TAG, SELECTED_PROFILE_ID + this.mLastSelectedID);
                if (params != null && isAppliedProfileDeleted()) {
                    this.mRestoreOldValue = mLastSelectedItemPos;
                    applyProfile(params);
                }
            }
            updateLayout(1);
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    private void applyProfile(LensCompensationParameter params) {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.trace(TAG, SELECTED_PROFILE_ID + this.mLastSelectedID);
        OCUtil.getInstance().setAppTitle(params);
        BackUpUtil.getInstance().setPreference(OCBackUpKey.KEY_BKUP_SELECTED_POSITION, Integer.valueOf(mLastSelectedItemPos));
        this.mLastSelectedID = params.getId();
        BackUpUtil.getInstance().setPreference(OCBackUpKey.KEY_BKUP_PREV_RECORD_ID, Integer.valueOf(this.mLastSelectedID));
        AppLog.trace(TAG, SELECTED_PROFILE_ID + this.mLastSelectedID);
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    private boolean isAppliedProfileDeleted() {
        AppLog.enter(TAG, AppLog.getMethodName());
        boolean isAppliedProfile = this.mLastSelectedID == this.mDeletedProfileID;
        AppLog.exit(TAG, AppLog.getMethodName());
        return isAppliedProfile;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IConvertibleKeyHandler
    public int onConvertedKeyDown(KeyEvent event, IKeyFunction func) {
        int result;
        AppLog.enter(TAG, AppLog.getMethodName());
        CustomizableFunction customFunc = (CustomizableFunction) func;
        switch (customFunc) {
            case MainPrev:
                if (this.mOptionView.getVisibility() == 0) {
                    handleUpOperation();
                }
                this.mListView.dispatchKeyEvent(new KeyEvent(0, 19));
                result = 1;
                break;
            case MainNext:
                if (this.mOptionView.getVisibility() == 0) {
                    handleDownOperation();
                }
                this.mListView.dispatchKeyEvent(new KeyEvent(0, 20));
                result = 1;
                break;
            case SubPrev:
                if (this.mOptionView.getVisibility() == 0) {
                    handleUpOperation();
                }
                this.mListView.dispatchKeyEvent(new KeyEvent(0, 19));
                result = 1;
                break;
            case SubNext:
                if (this.mOptionView.getVisibility() == 0) {
                    handleDownOperation();
                }
                this.mListView.dispatchKeyEvent(new KeyEvent(0, 20));
                result = 1;
                break;
            default:
                result = super.onConvertedKeyDown(event, func);
                AppLog.info(TAG, "default section of onKeyDown(), event key code:" + event.getKeyCode());
                break;
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return result;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyDown(int keyCode, KeyEvent event) {
        int result;
        AppLog.enter(TAG, AppLog.getMethodName());
        this.isMenuPressed = false;
        this.isCustomOperationPerformed = false;
        if (this.mOptionView.getVisibility() != 0 && this.mListView != null) {
            if (this.mListView.getOnItemSelectedListener() == null) {
                this.mListView.setOnItemSelectedListener(this);
            }
            if (!this.mListView.hasFocus()) {
                this.mListView.requestFocus();
            }
        }
        AppLog.info(TAG, "Checking KeyHandling:::::   onKeyDown(), event.getScanCode():" + event.getScanCode());
        switch (event.getScanCode()) {
            case 103:
                if (this.mOptionView.getVisibility() == 0) {
                    handleUpOperation();
                }
                this.mListView.dispatchKeyEvent(new KeyEvent(0, 19));
                result = 1;
                break;
            case AppRoot.USER_KEYCODE.DOWN /* 108 */:
                if (this.mOptionView.getVisibility() == 0) {
                    handleDownOperation();
                }
                this.mListView.dispatchKeyEvent(new KeyEvent(0, 20));
                result = 1;
                break;
            case AppRoot.USER_KEYCODE.SK1 /* 229 */:
            case AppRoot.USER_KEYCODE.MENU /* 514 */:
                this.isMenuPressed = true;
                pushedMenuKey();
                result = 1;
                break;
            case AppRoot.USER_KEYCODE.SK2 /* 513 */:
            case AppRoot.USER_KEYCODE.DELETE /* 595 */:
                pushedDeleteKey();
                result = 1;
                break;
            case AppRoot.USER_KEYCODE.FN /* 520 */:
            case AppRoot.USER_KEYCODE.AF_MF /* 533 */:
            case AppRoot.USER_KEYCODE.MODE_DIAL_CHANGED /* 572 */:
            case AppRoot.USER_KEYCODE.SLIDE_AEL_FOCUS_CHANGED /* 589 */:
            case AppRoot.USER_KEYCODE.CUSTOM1 /* 622 */:
            case AppRoot.USER_KEYCODE.CUSTOM2 /* 623 */:
            case AppRoot.USER_KEYCODE.AF_MF_AEL /* 638 */:
                this.isCustomOperationPerformed = true;
                result = super.onKeyDown(keyCode, event);
                break;
            case AppRoot.USER_KEYCODE.SHUTTLE_RIGHT /* 522 */:
                if (this.mOptionView.getVisibility() == 0) {
                    handleDownOperation();
                }
                this.mListView.dispatchKeyEvent(new KeyEvent(0, 20));
                result = 1;
                break;
            case AppRoot.USER_KEYCODE.SHUTTLE_LEFT /* 523 */:
                if (this.mOptionView.getVisibility() == 0) {
                    handleUpOperation();
                }
                this.mListView.dispatchKeyEvent(new KeyEvent(0, 19));
                result = 1;
                break;
            case AppRoot.USER_KEYCODE.LENS_ATTACH /* 530 */:
            case AppRoot.USER_KEYCODE.LENS_DETACH /* 786 */:
                result = -1;
                break;
            case AppRoot.USER_KEYCODE.EV_DIAL_CHANGED /* 620 */:
                result = super.onKeyDown(keyCode, event);
                closeMenuLayout(null);
                break;
            default:
                result = super.onKeyDown(keyCode, event);
                break;
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return result;
    }

    private void handleUpOperation() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (this.mOptionItemPosition > 0) {
            this.mOptionItemPosition--;
        }
        handleOptionMenueSelectPosition(this.mOptionItemPosition);
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    private void handleDownOperation() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (this.mOptionItemPosition < 2) {
            this.mOptionItemPosition++;
        }
        handleOptionMenueSelectPosition(this.mOptionItemPosition);
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    private void handleOptionMenueSelectPosition(int position) {
        AppLog.enter(TAG, AppLog.getMethodName());
        switch (position) {
            case 0:
                this.mEditButton.setFocusableInTouchMode(true);
                this.mEditButton.setFocusable(true);
                this.mEditButton.requestFocus();
                break;
            case 1:
                this.mExportButton.setFocusableInTouchMode(true);
                this.mExportButton.setFocusable(true);
                this.mExportButton.requestFocus();
                break;
            case 2:
                this.mDeleteButton.setFocusableInTouchMode(true);
                this.mDeleteButton.setFocusable(true);
                this.mDeleteButton.requestFocus();
                break;
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyUp(int keyCode, KeyEvent event) {
        AppLog.enter(TAG, AppLog.getMethodName());
        int scanCode = event.getScanCode();
        int ret = 1;
        switch (scanCode) {
            case AppRoot.USER_KEYCODE.SK1 /* 229 */:
            case AppRoot.USER_KEYCODE.MENU /* 514 */:
                ret = 0;
            case AppRoot.USER_KEYCODE.CENTER /* 232 */:
                if (this.mOptionView.getVisibility() != 0 && this.mListView != null && !this.mListView.hasFocus()) {
                    this.mListView.requestFocus();
                    break;
                }
                break;
            case AppRoot.USER_KEYCODE.SK2 /* 513 */:
            case AppRoot.USER_KEYCODE.DELETE /* 595 */:
                if (this.mOptionView.getVisibility() == 0) {
                    this.mEditButton.setFocusableInTouchMode(true);
                    this.mEditButton.setFocusable(true);
                    this.mEditButton.setSelected(true);
                }
                ret = -1;
                break;
            case AppRoot.USER_KEYCODE.LENS_ATTACH /* 530 */:
            case AppRoot.USER_KEYCODE.LENS_DETACH /* 786 */:
                ret = -1;
                break;
            default:
                if (this.mOptionView.getVisibility() != 0) {
                    if (this.mListView != null && this.mListView.getOnItemSelectedListener() == null) {
                        this.mListView.setOnItemSelectedListener(this);
                    }
                    ret = 0;
                    break;
                }
                break;
        }
        if (ret == 0) {
            ret = super.onKeyUp(keyCode, event);
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return ret;
    }

    @Override // android.view.View.OnTouchListener
    public boolean onTouch(View v, MotionEvent event) {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (v.getId() == R.id.list_view && this.mListView != null) {
            this.mListView.getParent().requestDisallowInterceptTouchEvent(true);
            this.mListView.requestFocus();
            this.mListView.setFocusableInTouchMode(false);
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return true;
    }

    @Override // com.sony.imaging.app.manuallenscompensation.menu.layout.LensListBaseMenuLayout
    public BaseAdapter getAdapter(Context context) {
        return null;
    }
}
