package com.sony.imaging.app.manuallenscompensation.menu.layout;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.menu.BaseMenuService;
import com.sony.imaging.app.base.menu.HistoryItem;
import com.sony.imaging.app.base.menu.MenuDataParcelable;
import com.sony.imaging.app.base.menu.layout.BaseMenuLayout;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.manuallenscompensation.R;
import com.sony.imaging.app.manuallenscompensation.caution.layout.OCInfo;
import com.sony.imaging.app.manuallenscompensation.commonUtil.AppContext;
import com.sony.imaging.app.manuallenscompensation.commonUtil.AppLog;
import com.sony.imaging.app.manuallenscompensation.commonUtil.ByteDataAnalyser;
import com.sony.imaging.app.manuallenscompensation.commonUtil.MenuHistory;
import com.sony.imaging.app.manuallenscompensation.commonUtil.OCBackUpKey;
import com.sony.imaging.app.manuallenscompensation.commonUtil.OCConstants;
import com.sony.imaging.app.manuallenscompensation.database.LensCompensationParameter;
import com.sony.imaging.app.util.BackUpUtil;

/* loaded from: classes.dex */
public class ExternalProfileMenuLayout extends LensListBaseMenuLayout {
    public static String TAG = "ID_EXTERNALPROFILEMENULAYOUT";
    private static BaseAdapter mLensSelectorAdapter;
    private final String PARAM_NULL = "Parameter object is null so delete can't execute";
    private String mPrevLayoutId = null;
    private int mCurrentSelectedItemPosition = -1;
    private int mLastCount = -1;
    private String mExternalFileName = null;
    protected final String[] TAGS = {OCConstants.TAG_DELETE_PROCESS, OCConstants.TAG_DB_UPDATE_PROCESS};

    @Override // com.sony.imaging.app.base.menu.layout.MenuLayoutListener
    public void onClosed(Bundle bundle) {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.manuallenscompensation.menu.layout.LensListBaseMenuLayout
    public BaseAdapter getAdapter(Context context) {
        AppLog.enter(TAG, AppLog.getMethodName());
        this.mTitle.setText(R.string.STRID_FUNC_OPTICAL_COMPENSATION_PROFILE_LIST);
        mLensSelectorAdapter = new ExternalProfileAdapter(context);
        AppLog.exit(TAG, AppLog.getMethodName());
        return mLensSelectorAdapter;
    }

    @Override // com.sony.imaging.app.manuallenscompensation.menu.layout.LensListBaseMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        CameraNotificationManager.getInstance().setNotificationListener(this);
        this.mExternalFileName = null;
        getLastPushedItem();
        initializeView();
        initializeListener();
        setFooterguide();
        showFooterGuideDrawable(1);
        updateSelectedFocus();
        if (this.mCurrentSelectedItemPosition >= 0 && this.mListView.getCount() > 0) {
            updateSelectedBackground(this.mCurrentSelectedItemPosition);
        }
    }

    private void getLastPushedItem() {
        MenuDataParcelable parcelable = (MenuDataParcelable) this.data.getParcelable(MenuDataParcelable.KEY);
        BaseMenuService service = new BaseMenuService(parcelable.getMenuService());
        HistoryItem item = service.popMenuHistory();
        if (item != null) {
            service.pushMenuHistory(item);
            this.mPrevLayoutId = item.layoutId;
            this.mPreviousMenuLayoutID = this.mPrevLayoutId;
        }
    }

    @Override // com.sony.imaging.app.manuallenscompensation.menu.layout.LensListBaseMenuLayout
    protected void initializeView() {
        Context context = getActivity().getApplicationContext();
        this.mService = new BaseMenuService(context);
        mLensSelectorAdapter = getAdapter(context);
        setBackGroundImageDrawable(1);
        this.mListView.setAdapter((ListAdapter) mLensSelectorAdapter);
        this.mListView.setOnItemSelectedListener(this);
        this.mListView.setDividerHeight(4);
        this.mListView.requestFocus();
        this.mListView.setFocusable(true);
        this.mListView.setEnabled(true);
        if (mLensSelectorAdapter.getCount() != 0) {
            this.mEmptyText.setVisibility(8);
            return;
        }
        this.mEmptyText.setVisibility(0);
        this.mCurrentSelectedItemPosition = -1;
        this.mLastCount = -1;
        if (!LensListMenuLayout.TAG.equalsIgnoreCase(this.mPrevLayoutId)) {
            updateSelectedPosition(3);
            BackUpUtil.getInstance().setPreference(OCBackUpKey.KEY_BKUP_SELECTED_POSITION_IMPORT_EXPORTED_LIST, -1);
        }
    }

    private void updateSelectedFocus() {
        if (LensListMenuLayout.TAG.equalsIgnoreCase(this.mPrevLayoutId)) {
            this.mCurrentSelectedItemPosition = BackUpUtil.getInstance().getPreferenceInt(OCBackUpKey.KEY_BKUP_SELECTED_POSITION_IMPORT_EXPORTED_LIST, -1);
            this.mLastCount = BackUpUtil.getInstance().getPreferenceInt(OCBackUpKey.KEY_BKUP_COUNT_IMPORT_EXPORTED_LIST, -1);
        } else {
            this.mCurrentSelectedItemPosition = BackUpUtil.getInstance().getPreferenceInt(OCBackUpKey.KEY_BKUP_SELECTED_POSITION_DELETE_EXPORTED_LIST, -1);
            this.mLastCount = BackUpUtil.getInstance().getPreferenceInt(OCBackUpKey.KEY_BKUP_COUNT_DELETE_EXPORTED_LIST, -1);
        }
        AppLog.info("FOCUS", "updateSelectedFocus mCurrentSelectedItemPosition " + this.mCurrentSelectedItemPosition);
        AppLog.info("FOCUS", "updateSelectedFocus mLastCount " + this.mLastCount);
        AppLog.info("FOCUS", "updateSelectedFocus getCount " + this.mListView.getCount());
        if (this.mCurrentSelectedItemPosition == -1) {
            this.mCurrentSelectedItemPosition = this.mListView.getCount() - 1;
        } else {
            this.mCurrentSelectedItemPosition = this.mListView.getCount() - (this.mLastCount - this.mCurrentSelectedItemPosition);
        }
        this.mListView.setSelection(this.mCurrentSelectedItemPosition);
        AppLog.info("FOCUS", "updateSelectedFocus _AFTER mCurrentSelectedItemPosition" + this.mCurrentSelectedItemPosition);
    }

    @Override // com.sony.imaging.app.manuallenscompensation.menu.layout.LensListBaseMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        AppLog.enter(TAG, AppLog.getMethodName());
        super.onPause();
        CameraNotificationManager.getInstance().removeNotificationListener(this);
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.manuallenscompensation.menu.layout.LensListBaseMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public void onDestroyView() {
        mLensSelectorAdapter = null;
        super.onDestroyView();
    }

    @Override // com.sony.imaging.app.manuallenscompensation.menu.layout.LensListBaseMenuLayout
    protected void setFooterguide() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (LensListMenuLayout.TAG.equalsIgnoreCase(this.mPrevLayoutId)) {
            showFooterGuide(android.R.string.hour, android.R.string.zen_mode_feature_name, android.R.string.hour_picker_description, android.R.string.httpErrorRedirectLoop);
            setTitleForSceeen(1, getResources().getString(R.string.STRID_FUNC_OPTICAL_COMPENSATION_PROFILE_IMPORT));
        } else {
            if (mLensSelectorAdapter != null && (!mLensSelectorAdapter.isEmpty() || mLensSelectorAdapter.getCount() != 0)) {
                showFooterGuide(R.string.STRID_FOOTERGUIDE_OPTION_EXITAPP_BY_MENU, R.string.STRID_FOOTERGUIDE_DELETE_FILE, R.string.STRID_FOOTERGUIDE_OPTION_EXITAPP_FOR_SK, R.string.STRID_FOOTERGUIDE_DELETE_FILE_FOR_SK);
            } else {
                showFooterGuide(R.string.STRID_FOOTERGUIDE_OPTION_EXITAPP_BY_MENU, android.R.string.zen_mode_feature_name, R.string.STRID_FOOTERGUIDE_OPTION_EXITAPP_FOR_SK, android.R.string.httpErrorRedirectLoop);
            }
            setTitleForSceeen(1, getResources().getString(R.string.STRID_FUNC_OPTICAL_COMPENSATION_EXPORT_FILE_DELETE));
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.manuallenscompensation.menu.layout.LensListBaseMenuLayout, android.widget.AdapterView.OnItemClickListener
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.info(TAG, "position = " + position);
        int validity_Digit = ((ExternalProfileAdapter) mLensSelectorAdapter).getItemValidity(position);
        if (LensListMenuLayout.TAG.equalsIgnoreCase(this.mPrevLayoutId)) {
            if (validity_Digit == 0) {
                ByteDataAnalyser params = (ByteDataAnalyser) mLensSelectorAdapter.getItem(position);
                LensCompensationParameter params1 = LensCompensationParameter.getCompensationParamterFromByteAnalyzer(params);
                openImportConfirmationLayout(params1, 2);
            } else if (validity_Digit == 1) {
                CautionUtilityClass.getInstance().requestTrigger(OCInfo.CAUTION_ID_DLAPP_IMPORT_UNSUPPORTED_PROFILE);
            } else if (validity_Digit == 2) {
                CautionUtilityClass.getInstance().requestTrigger(OCInfo.CAUTION_ID_DLAPP_IMPORT_ERROR_PROFILE);
            }
        } else {
            if (validity_Digit == 0) {
                openProfileDelete();
            }
            updateSelectedPosition(3);
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    private void openDeteteConfirmScreen(int position) {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (mLensSelectorAdapter.getCount() > 0) {
            ByteDataAnalyser params = (ByteDataAnalyser) mLensSelectorAdapter.getItem(position);
            if (params != null) {
                this.mExternalFileName = ((ExternalProfileAdapter) mLensSelectorAdapter).getItemFileName(position);
                LensCompensationParameter params1 = LensCompensationParameter.getCompensationParamterFromByteAnalyzer(params);
                openImportConfirmationLayout(params1, 3);
            } else {
                AppLog.info(TAG, "Parameter object is null so delete can't execute");
            }
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    @Override // android.widget.AdapterView.OnItemSelectedListener
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (mLensSelectorAdapter == null) {
            mLensSelectorAdapter = new ExternalProfileAdapter(AppContext.getAppContext());
        }
        updateSelectedBackground(position);
        if (this.mTitle != null) {
            AppLog.info(TAG, AppLog.getMethodName() + ((Object) this.mTitle.getText()));
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    private void updateSelectedBackground(int position) {
        int validity_Digit = ((ExternalProfileAdapter) mLensSelectorAdapter).getItemValidity(position);
        this.mCurrentSelectedItemPosition = position;
        if (position < mLensSelectorAdapter.getCount()) {
            if (validity_Digit == 0) {
                showRightPane(1);
                updateRightPane(position, mLensSelectorAdapter);
            } else {
                clearRightPane(1);
            }
        }
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMenuKey() {
        AppLog.enter(TAG, AppLog.getMethodName());
        openPreviousMenu();
        AppLog.exit(TAG, AppLog.getMethodName());
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedDeleteFuncKey() {
        AppLog.enter(TAG, AppLog.getMethodName());
        int returnState = 1;
        if (LensListMenuLayout.TAG.equalsIgnoreCase(this.mPrevLayoutId)) {
            returnState = super.pushedDeleteFuncKey();
        } else {
            openDeteteConfirmScreen(this.mCurrentSelectedItemPosition);
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return returnState;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedDeleteKey() {
        AppLog.enter(TAG, AppLog.getMethodName());
        int returnState = 1;
        if (LensListMenuLayout.TAG.equalsIgnoreCase(this.mPrevLayoutId)) {
            returnState = super.pushedDeleteKey();
        } else {
            openDeteteConfirmScreen(this.mCurrentSelectedItemPosition);
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return returnState;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyDown(int keyCode, KeyEvent event) {
        AppLog.enter(TAG, AppLog.getMethodName());
        int scanCode = event.getScanCode();
        int result = 1;
        if (!LensListMenuLayout.TAG.equalsIgnoreCase(this.mPrevLayoutId) && (scanCode == 595 || scanCode == 513)) {
            openDeteteConfirmScreen(this.mCurrentSelectedItemPosition);
        } else {
            AppLog.info(TAG, "Checking KeyHandling:::::   onKeyDown(), event.getScanCode():" + event.getScanCode());
            switch (scanCode) {
                case 103:
                    this.mListView.dispatchKeyEvent(new KeyEvent(0, 19));
                    result = 1;
                    break;
                case AppRoot.USER_KEYCODE.DOWN /* 108 */:
                    this.mListView.dispatchKeyEvent(new KeyEvent(0, 20));
                    result = 1;
                    break;
                case AppRoot.USER_KEYCODE.SHUTTLE_RIGHT /* 522 */:
                case AppRoot.USER_KEYCODE.DIAL1_RIGHT /* 525 */:
                case AppRoot.USER_KEYCODE.DIAL2_RIGHT /* 528 */:
                case AppRoot.USER_KEYCODE.DIAL3_RIGHT /* 635 */:
                    this.mListView.dispatchKeyEvent(new KeyEvent(0, 20));
                    result = 1;
                    break;
                case AppRoot.USER_KEYCODE.SHUTTLE_LEFT /* 523 */:
                case AppRoot.USER_KEYCODE.DIAL1_LEFT /* 526 */:
                case AppRoot.USER_KEYCODE.DIAL2_LEFT /* 529 */:
                case AppRoot.USER_KEYCODE.DIAL3_LEFT /* 634 */:
                    this.mListView.dispatchKeyEvent(new KeyEvent(0, 19));
                    result = 1;
                    break;
                case AppRoot.USER_KEYCODE.LENS_ATTACH /* 530 */:
                case AppRoot.USER_KEYCODE.LENS_DETACH /* 786 */:
                    result = -1;
                    break;
                default:
                    result = super.onKeyDown(keyCode, event);
                    break;
            }
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return result;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyUp(int keyCode, KeyEvent event) {
        AppLog.enter(TAG, AppLog.getMethodName());
        int scanCode = event.getScanCode();
        int ret = 1;
        if (scanCode == 530 || scanCode == 786 || scanCode == 595 || scanCode == 513) {
            AppLog.exit(TAG, "invalid event in on key UP");
        } else {
            if (scanCode == 232 && this.mListView != null) {
                this.mListView.requestFocus();
            }
            ret = super.onKeyUp(keyCode, event);
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return ret;
    }

    private void openProfileDelete() {
        AppLog.enter(TAG, AppLog.getMethodName());
        LensCompensationParameter params = null;
        this.data.putInt(OCConstants.PROFILE_ACTION, 3);
        if (mLensSelectorAdapter != null) {
            ByteDataAnalyser byteAnalyser = (ByteDataAnalyser) mLensSelectorAdapter.getItem(this.mCurrentSelectedItemPosition);
            params = LensCompensationParameter.getCompensationParamterFromByteAnalyzer(byteAnalyser);
            this.mExternalFileName = ((ExternalProfileAdapter) mLensSelectorAdapter).getItemFileName(this.mCurrentSelectedItemPosition);
            this.data.putString(OCConstants.EXTERNAL_PROFILE_FILE_NAME, this.mExternalFileName);
        }
        this.data.putParcelable(OCBackUpKey.KEY_BKUP_SELECTED_PARAM, params);
        MenuHistory.getInstance().pushMenuItem(TAG);
        openMenuLayout(ExternalProfileDescriptionMenuLayout.TAG, this.data);
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    private void openImportConfirmationLayout(LensCompensationParameter params, int profileAction) {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.enter(TAG, "params =" + params);
        if (this.data == null) {
            this.data = new Bundle();
        }
        updateSelectedPosition(profileAction);
        params.setId(0);
        this.data.putString(OCConstants.EXTERNAL_PROFILE_FILE_NAME, this.mExternalFileName);
        this.data.putInt(OCConstants.PROFILE_ACTION, profileAction);
        this.data.putParcelable(OCBackUpKey.KEY_BKUP_SELECTED_PARAM, params);
        BaseMenuLayout layout = (BaseMenuLayout) getLayout("ID_DELETESINGLECONFIRMMENULAYOUT");
        if (layout != null) {
            layout.setMenuLayoutListener(getMenuLayoutListener());
        }
        MenuHistory.getInstance().pushMenuItem(TAG);
        openLayout("ID_DELETESINGLECONFIRMMENULAYOUT", this.data);
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    private void updateSelectedPosition(int profileAction) {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.info("FOCUS", "updateSelectedPosition mCurrentSelectedItemPosition " + this.mCurrentSelectedItemPosition);
        AppLog.info("FOCUS", "updateSelectedPosition getCount " + this.mListView.getCount());
        if (2 == profileAction) {
            BackUpUtil.getInstance().setPreference(OCBackUpKey.KEY_BKUP_SELECTED_POSITION_IMPORT_EXPORTED_LIST, Integer.valueOf(this.mCurrentSelectedItemPosition));
            BackUpUtil.getInstance().setPreference(OCBackUpKey.KEY_BKUP_COUNT_IMPORT_EXPORTED_LIST, Integer.valueOf(this.mListView.getCount()));
        } else if (3 == profileAction) {
            BackUpUtil.getInstance().setPreference(OCBackUpKey.KEY_BKUP_COUNT_DELETE_EXPORTED_LIST, Integer.valueOf(this.mListView.getCount()));
            BackUpUtil.getInstance().setPreference(OCBackUpKey.KEY_BKUP_SELECTED_POSITION_DELETE_EXPORTED_LIST, Integer.valueOf(this.mCurrentSelectedItemPosition));
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.util.NotificationListener
    public String[] getTags() {
        return this.TAGS;
    }

    @Override // com.sony.imaging.app.util.NotificationListener
    public void onNotify(String tag) {
        if (OCConstants.TAG_DELETE_PROCESS.equals(tag)) {
            updateLayout(1);
            return;
        }
        if (OCConstants.TAG_DB_UPDATE_PROCESS.equals(tag)) {
            AppLog.info(OCConstants.TAG_DB_UPDATE_PROCESS, "syncExternalMemoryWithDB notified start");
            int c = this.mListView.getCount();
            int i = this.mListView.getSelectedItemPosition();
            mLensSelectorAdapter.notifyDataSetChanged();
            int new_position = this.mListView.getCount() - (c - i);
            this.mListView.setSelection(new_position);
            AppLog.info(OCConstants.TAG_DB_UPDATE_PROCESS, "syncExternalMemoryWithDB notified finish");
        }
    }

    @Override // com.sony.imaging.app.manuallenscompensation.menu.layout.LensListBaseMenuLayout
    public BaseAdapter getAdapter(Context context, Cursor cursor) {
        return null;
    }
}
