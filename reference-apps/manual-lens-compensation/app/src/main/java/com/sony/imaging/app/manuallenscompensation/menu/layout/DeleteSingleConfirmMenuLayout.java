package com.sony.imaging.app.manuallenscompensation.menu.layout;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.sony.imaging.app.base.common.widget.FooterGuide;
import com.sony.imaging.app.base.common.widget.FooterGuideDataResId;
import com.sony.imaging.app.base.menu.layout.BaseMenuLayout;
import com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.fw.CustomizableFunction;
import com.sony.imaging.app.fw.IKeyFunction;
import com.sony.imaging.app.manuallenscompensation.R;
import com.sony.imaging.app.manuallenscompensation.commonUtil.AppLog;
import com.sony.imaging.app.manuallenscompensation.commonUtil.ByteDataCreator;
import com.sony.imaging.app.manuallenscompensation.commonUtil.MenuHistory;
import com.sony.imaging.app.manuallenscompensation.commonUtil.OCBackUpKey;
import com.sony.imaging.app.manuallenscompensation.commonUtil.OCConstants;
import com.sony.imaging.app.manuallenscompensation.commonUtil.OCUtil;
import com.sony.imaging.app.manuallenscompensation.database.LensCompensationParameter;
import com.sony.imaging.app.manuallenscompensation.shooting.controller.OCController;
import com.sony.imaging.app.util.BackUpUtil;
import java.io.File;

/* loaded from: classes.dex */
public class DeleteSingleConfirmMenuLayout extends DisplayMenuItemsMenuLayout {
    private static final String PROFILE_NOT_DELETED = "Profile not deleted and exception occurs";
    public static final String TAG = "ID_DELETESINGLECONFIRMMENULAYOUT";
    private final String NO_NEED_TO_UPDATE_POSITION = "No need to update";
    ViewGroup mView = null;
    TextView mOk = null;
    TextView mCancel = null;
    FooterGuide mFooterGuide = null;
    private int mProfileAction = OCConstants.profileAction;

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        AppLog.enter("ID_DELETESINGLECONFIRMMENULAYOUT", AppLog.getMethodName());
        super.onCreateView(inflater, container, savedInstanceState);
        this.mView = (ViewGroup) obtainViewFromPool(getLayoutResource());
        if (this.data != null) {
            this.mProfileAction = this.data.getInt(OCConstants.PROFILE_ACTION);
        }
        this.mOk = (TextView) this.mView.findViewById(R.id.button_upper);
        if (this.mOk != null) {
            if (this.mProfileAction == 0 || this.mProfileAction == 3) {
                this.mOk.setText(android.R.string.permlab_foregroundService);
            } else if (this.mProfileAction == 1) {
                this.mOk.setText(R.string.STRID_FUNC_OPTICAL_COMPENSATION_PROFILE_EXPORT);
            } else if (this.mProfileAction == 2) {
                this.mOk.setText(R.string.STRID_FUNC_OPTICAL_COMPENSATION_PROFILE_IMPORT);
            }
            this.mOk.setSelected(true);
        }
        this.mCancel = (TextView) this.mView.findViewById(R.id.button_lower);
        if (this.mCancel != null) {
            this.mCancel.setText(android.R.string.config_customAdbWifiNetworkConfirmationSecondaryUserComponent);
            this.mCancel.setSelected(1 == 0);
        }
        AppLog.enter("ID_DELETESINGLECONFIRMMENULAYOUT", AppLog.getMethodName());
        return this.mView;
    }

    protected int getLayoutResource() {
        AppLog.enter("ID_DELETESINGLECONFIRMMENULAYOUT", AppLog.getMethodName());
        AppLog.exit("ID_DELETESINGLECONFIRMMENULAYOUT", AppLog.getMethodName());
        return R.layout.menu_ok_cancel_key;
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        AppLog.enter("ID_DELETESINGLECONFIRMMENULAYOUT", AppLog.getMethodName());
        super.onResume();
        if (this.mView != null) {
            this.mFooterGuide = (FooterGuide) this.mView.findViewById(R.id.cmn_footer_guide_menu);
        }
        if (this.mFooterGuide != null) {
            this.mFooterGuide.setData(new FooterGuideDataResId(getActivity(), android.R.string.zen_mode_feature_name, android.R.string.httpErrorRedirectLoop));
        }
        setMessageText();
        AppLog.exit("ID_DELETESINGLECONFIRMMENULAYOUT", AppLog.getMethodName());
    }

    private void setMessageText() {
        AppLog.enter("ID_DELETESINGLECONFIRMMENULAYOUT", AppLog.getMethodName());
        TextView label = (TextView) getView().findViewById(R.id.dialog_message);
        if (label != null) {
            LensCompensationParameter param = null;
            switch (this.mProfileAction) {
                case 0:
                    label.setText(R.string.STRID_FUNC_OPTICAL_COMPENSATION_DELETE_CONFIRMING_SINGLE);
                    break;
                case 1:
                    label.setText(getResources().getString(R.string.STRID_FUNC_OPTICAL_COMPENSATION_EXPORT_CONF));
                    break;
                case 2:
                    label.setText(getResources().getString(R.string.STRID_FUNC_OPTICAL_COMPENSATION_IMPORT_CONF));
                    break;
                case 3:
                    if (this.data != null) {
                        param = (LensCompensationParameter) this.data.getParcelable(OCBackUpKey.KEY_BKUP_SELECTED_PARAM);
                    }
                    if (param != null) {
                        label.setText(getResources().getString(R.string.STRID_FUNC_OPTICAL_COMPENSATION_DELETE_CONFIRMING_SINGLE));
                        break;
                    }
                    break;
            }
        }
        AppLog.exit("ID_DELETESINGLECONFIRMMENULAYOUT", AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public void onDestroyView() {
        AppLog.enter("ID_DELETESINGLECONFIRMMENULAYOUT", AppLog.getMethodName());
        super.onDestroyView();
        deinitializeView();
        AppLog.enter("ID_DELETESINGLECONFIRMMENULAYOUT", AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.FakeFragment
    public void onDestroy() {
        AppLog.enter("ID_DELETESINGLECONFIRMMENULAYOUT", AppLog.getMethodName());
        deinitializeView();
        super.onDestroy();
        AppLog.enter("ID_DELETESINGLECONFIRMMENULAYOUT", AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        AppLog.enter("ID_DELETESINGLECONFIRMMENULAYOUT", AppLog.getMethodName());
        deinitializeView();
        super.onPause();
        AppLog.exit("ID_DELETESINGLECONFIRMMENULAYOUT", AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout
    public void closeLayout() {
        AppLog.enter("ID_DELETESINGLECONFIRMMENULAYOUT", AppLog.getMethodName());
        deinitializeView();
        super.closeLayout();
        AppLog.exit("ID_DELETESINGLECONFIRMMENULAYOUT", AppLog.getMethodName());
    }

    private void deinitializeView() {
        AppLog.enter("ID_DELETESINGLECONFIRMMENULAYOUT", AppLog.getMethodName());
        if (this.mView != null && (this.mView instanceof ViewGroup) && this.mFooterGuide != null) {
            this.mFooterGuide.setData(null);
            this.mView = null;
        }
        this.mFooterGuide = null;
        this.mOk = null;
        this.mCancel = null;
        this.mView = null;
        AppLog.exit("ID_DELETESINGLECONFIRMMENULAYOUT", AppLog.getMethodName());
    }

    private void toggleSelection() {
        AppLog.enter("ID_DELETESINGLECONFIRMMENULAYOUT", AppLog.getMethodName());
        if (this.mOk != null && this.mCancel != null) {
            this.mOk.setSelected(!this.mOk.isSelected());
            this.mCancel.setSelected(this.mCancel.isSelected() ? false : true);
        } else if (this.mOk != null) {
            this.mOk.setSelected(true);
        } else if (this.mCancel != null) {
            this.mCancel.setSelected(true);
        }
        AppLog.exit("ID_DELETESINGLECONFIRMMENULAYOUT", AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int releasedCenterKey() {
        AppLog.enter("ID_DELETESINGLECONFIRMMENULAYOUT", AppLog.getMethodName());
        if (this.mOk != null && this.mOk.isSelected()) {
            switch (this.mProfileAction) {
                case 0:
                    deleteprocess();
                    this.data.putInt(OCConstants.PROFILE_PROCESS_ERROR, -1);
                    break;
                case 1:
                    try {
                        exportProcess();
                        this.data.putInt(OCConstants.PROFILE_PROCESS_ERROR, -1);
                        int lastdeleteCount = BackUpUtil.getInstance().getPreferenceInt(OCBackUpKey.KEY_BKUP_COUNT_DELETE_EXPORTED_LIST, -1);
                        int lastImportCount = BackUpUtil.getInstance().getPreferenceInt(OCBackUpKey.KEY_BKUP_COUNT_IMPORT_EXPORTED_LIST, -1);
                        if (-1 != lastdeleteCount) {
                            BackUpUtil.getInstance().setPreference(OCBackUpKey.KEY_BKUP_COUNT_DELETE_EXPORTED_LIST, Integer.valueOf(lastdeleteCount + 1));
                        }
                        if (-1 != lastImportCount) {
                            BackUpUtil.getInstance().setPreference(OCBackUpKey.KEY_BKUP_COUNT_IMPORT_EXPORTED_LIST, Integer.valueOf(lastImportCount + 1));
                            break;
                        }
                    } catch (Exception e) {
                        AppLog.error("ID_DELETESINGLECONFIRMMENULAYOUT", "Exception is : " + e.toString());
                        this.data.putInt(OCConstants.PROFILE_PROCESS_ERROR, 1);
                        closeLayout();
                        break;
                    }
                    break;
                case 2:
                    try {
                        importProcess();
                        this.data.putInt(OCConstants.PROFILE_PROCESS_ERROR, -1);
                        break;
                    } catch (Exception e2) {
                        this.data.putInt(OCConstants.PROFILE_PROCESS_ERROR, 2);
                        AppLog.error("ID_DELETESINGLECONFIRMMENULAYOUT", "Exception is : " + e2.toString());
                        closeLayout();
                        break;
                    }
                case 3:
                    try {
                        deleteExternalProfile();
                        updatePositionPostDelete();
                        this.data.putInt(OCConstants.PROFILE_PROCESS_ERROR, -1);
                        break;
                    } catch (Exception e3) {
                        this.data.putInt(OCConstants.PROFILE_PROCESS_ERROR, 0);
                        closeLayout();
                        AppLog.trace("ID_DELETESINGLECONFIRMMENULAYOUT", "Exception is :" + e3.toString());
                        break;
                    }
            }
            openOCNextMenuLayout(OCConstants.ID_DELETEPROFILEPROCESSING, this.data);
        } else if (this.mCancel != null && this.mCancel.isSelected()) {
            performCancelOperation();
        }
        AppLog.exit("ID_DELETESINGLECONFIRMMENULAYOUT", AppLog.getMethodName());
        return 1;
    }

    private void updatePositionPostDelete() {
        AppLog.enter("ID_DELETESINGLECONFIRMMENULAYOUT", AppLog.getMethodName());
        int lastdeletePosition = BackUpUtil.getInstance().getPreferenceInt(OCBackUpKey.KEY_BKUP_SELECTED_POSITION_DELETE_EXPORTED_LIST, -1);
        int lastdeleteCount = BackUpUtil.getInstance().getPreferenceInt(OCBackUpKey.KEY_BKUP_COUNT_DELETE_EXPORTED_LIST, -1);
        int lastImportPosition = BackUpUtil.getInstance().getPreferenceInt(OCBackUpKey.KEY_BKUP_SELECTED_POSITION_IMPORT_EXPORTED_LIST, -1);
        int lastImportCount = BackUpUtil.getInstance().getPreferenceInt(OCBackUpKey.KEY_BKUP_COUNT_IMPORT_EXPORTED_LIST, -1);
        if (lastImportPosition != 0) {
            if (lastdeletePosition <= lastImportPosition) {
                BackUpUtil.getInstance().setPreference(OCBackUpKey.KEY_BKUP_SELECTED_POSITION_IMPORT_EXPORTED_LIST, Integer.valueOf(lastImportPosition - 1));
            }
            BackUpUtil.getInstance().setPreference(OCBackUpKey.KEY_BKUP_COUNT_IMPORT_EXPORTED_LIST, Integer.valueOf(lastImportCount - 1));
        } else {
            AppLog.info("ID_DELETESINGLECONFIRMMENULAYOUT", "No need to update");
        }
        if (lastdeletePosition != 0) {
            BackUpUtil.getInstance().setPreference(OCBackUpKey.KEY_BKUP_SELECTED_POSITION_DELETE_EXPORTED_LIST, Integer.valueOf(lastdeletePosition - 1));
            BackUpUtil.getInstance().setPreference(OCBackUpKey.KEY_BKUP_COUNT_DELETE_EXPORTED_LIST, Integer.valueOf(lastdeleteCount - 1));
        } else {
            AppLog.info("ID_DELETESINGLECONFIRMMENULAYOUT", "No need to update");
        }
        AppLog.exit("ID_DELETESINGLECONFIRMMENULAYOUT", AppLog.getMethodName());
    }

    private void deleteExternalProfile() throws Exception {
        AppLog.enter("ID_DELETESINGLECONFIRMMENULAYOUT", AppLog.getMethodName());
        String fileName = null;
        if (this.data != null) {
            fileName = this.data.getString(OCConstants.EXTERNAL_PROFILE_FILE_NAME);
        }
        if (fileName != null) {
            try {
                try {
                    OCUtil.getInstance().deleteProfile(fileName);
                    boolean returnState = isDeleteSucceedOnMedia(fileName);
                    AppLog.info("ID_DELETESINGLECONFIRMMENULAYOUT", "External Profile deleted " + returnState);
                } catch (Exception e) {
                    AppLog.info("ID_DELETESINGLECONFIRMMENULAYOUT", PROFILE_NOT_DELETED);
                    throw new Exception(PROFILE_NOT_DELETED);
                }
            } finally {
                AppLog.info("ID_DELETESINGLECONFIRMMENULAYOUT", AppLog.getMethodName());
            }
        }
        AppLog.exit("ID_DELETESINGLECONFIRMMENULAYOUT", AppLog.getMethodName());
    }

    private boolean isDeleteSucceedOnMedia(String fileName) {
        AppLog.enter("ID_DELETESINGLECONFIRMMENULAYOUT", AppLog.getMethodName());
        boolean deleteFileState = false;
        File externalProfileFile = new File(OCUtil.getInstance().getFilePathOnMedia() + File.separator + fileName);
        if (externalProfileFile.exists()) {
            deleteFileState = externalProfileFile.delete();
        }
        AppLog.exit("ID_DELETESINGLECONFIRMMENULAYOUT", AppLog.getMethodName());
        return deleteFileState;
    }

    private void performCancelOperation() {
        AppLog.enter("ID_DELETESINGLECONFIRMMENULAYOUT", AppLog.getMethodName());
        switch (this.mProfileAction) {
            case 2:
            case 3:
                MenuHistory.getInstance().popMenuItem();
                closeLayout();
                break;
            default:
                closeLayoutAndCancelDeleteProcess();
                break;
        }
        AppLog.exit("ID_DELETESINGLECONFIRMMENULAYOUT", AppLog.getMethodName());
    }

    protected void openOCNextMenuLayout(String nextMenuId, Bundle bundle) {
        AppLog.enter("ID_DELETESINGLECONFIRMMENULAYOUT", AppLog.getMethodName());
        BaseMenuLayout layout = (BaseMenuLayout) getLayout(nextMenuId);
        if (layout != null) {
            layout.setMenuLayoutListener(getMenuLayoutListener());
            closeLayout();
        }
        openLayout(nextMenuId, bundle);
        AppLog.exit("ID_DELETESINGLECONFIRMMENULAYOUT", AppLog.getMethodName());
    }

    private void importProcess() throws Exception {
        AppLog.enter("ID_DELETESINGLECONFIRMMENULAYOUT", AppLog.getMethodName());
        LensCompensationParameter param = (LensCompensationParameter) this.data.getParcelable(OCBackUpKey.KEY_BKUP_SELECTED_PARAM);
        LensCompensationParameter.saveCompensationParameter(getActivity().getApplicationContext(), param);
        AppLog.exit("ID_DELETESINGLECONFIRMMENULAYOUT", AppLog.getMethodName());
    }

    private void exportProcess() throws Exception {
        AppLog.enter("ID_DELETESINGLECONFIRMMENULAYOUT", AppLog.getMethodName());
        LensCompensationParameter param = (LensCompensationParameter) this.data.getParcelable(OCBackUpKey.KEY_BKUP_SELECTED_PARAM);
        int mSBright = param.getLevel(OCController.LIGHT_VIGNETTING);
        int mSRed = param.getLevel(OCController.RED_COLOR_VIGNETTING);
        int mSBlue = param.getLevel(OCController.BLUE_COLOR_VIGNETTING);
        int mChromaRed = param.getLevel(OCController.RED_CHROMATIC_ABERRATION);
        int mChromaBlue = param.getLevel(OCController.BLUE_CHROMATIC_ABERRATION);
        int mDistWhite = param.getLevel(OCController.DISTORTION);
        ByteDataCreator byteDataCreator = new ByteDataCreator();
        byteDataCreator.setAppVersion("01.00");
        byteDataCreator.setDbVersion("01.00");
        byteDataCreator.setNextVersionOffset(Short.valueOf(OCConstants.NEXT_VER_OFFSET));
        byteDataCreator.setLensName(param.mLensName);
        if (param.mFocalLength.equalsIgnoreCase("")) {
            byteDataCreator.setFocalLength(Short.valueOf(Short.parseShort("0")));
        } else {
            byteDataCreator.setFocalLength(Short.valueOf(Short.parseShort(param.mFocalLength)));
        }
        byteDataCreator.setMinFNumber(param.mFValue);
        byteDataCreator.setFMinLightVignettingCorrection(mSBright);
        byteDataCreator.setFMinRedColorVignettingCorrection(mSRed);
        byteDataCreator.setFMinBlueColorVignettingCorrection(mSBlue);
        byteDataCreator.setFMinRedChromaticAberationCorrection(mChromaRed);
        byteDataCreator.setFMinBlueChromaticAberationCorrection(mChromaBlue);
        byteDataCreator.setFMinDistortionCorrection(mDistWhite);
        String fileName = OCUtil.getInstance().writeToExternalMedia(byteDataCreator.getDataBytes());
        OCUtil.getInstance().setLastExportedProfileText(fileName);
        byteDataCreator.releaseAllocatedMemory();
    }

    private void deleteprocess() {
        AppLog.enter("ID_DELETESINGLECONFIRMMENULAYOUT", AppLog.getMethodName());
        AppLog.info(OCConstants.ID_DELETEPROFILEPROCESSING, AppLog.getMethodName());
        LensCompensationParameter param = (LensCompensationParameter) this.data.getParcelable(OCBackUpKey.KEY_BKUP_SELECTED_PARAM);
        LensCompensationParameter.deleteCompensationParameter(getActivity().getApplicationContext(), param);
        AppLog.exit("ID_DELETESINGLECONFIRMMENULAYOUT", AppLog.getMethodName());
    }

    public int closeLayoutAndCancelDeleteProcess() {
        AppLog.enter("ID_DELETESINGLECONFIRMMENULAYOUT", AppLog.getMethodName());
        if (this.data == null) {
            this.data = new Bundle();
        }
        if (this.mProfileAction != 0) {
            String previousMenuID = MenuHistory.getInstance().popMenuItem();
            if (previousMenuID != null && !ExternalProfileDescriptionMenuLayout.TAG.equals(previousMenuID)) {
                MenuHistory.getInstance().pushMenuItem(previousMenuID);
            }
            closeLayout();
        } else {
            CameraNotificationManager.getInstance().requestNotify(OCConstants.TAG_DELETE_PROCESS_CANCEL);
            closeLayout();
        }
        AppLog.exit("ID_DELETESINGLECONFIRMMENULAYOUT", AppLog.getMethodName());
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMenuKey() {
        AppLog.enter("ID_DELETESINGLECONFIRMMENULAYOUT", AppLog.getMethodName());
        performCancelOperation();
        AppLog.exit("ID_DELETESINGLECONFIRMMENULAYOUT", AppLog.getMethodName());
        return 1;
    }

    @Override // com.sony.imaging.app.fw.Layout
    public Drawable onGetBackground() {
        AppLog.enter("ID_DELETESINGLECONFIRMMENULAYOUT", AppLog.getMethodName());
        return BACKGROUND_FOLLOW;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyDown(int keyCode, KeyEvent event) {
        AppLog.enter("ID_DELETESINGLECONFIRMMENULAYOUT", AppLog.getMethodName());
        int ret = -1;
        int code = event.getScanCode();
        switch (code) {
            case AppRoot.USER_KEYCODE.SK2 /* 513 */:
            case AppRoot.USER_KEYCODE.FN /* 520 */:
            case AppRoot.USER_KEYCODE.LENS_ATTACH /* 530 */:
            case AppRoot.USER_KEYCODE.DELETE /* 595 */:
            case AppRoot.USER_KEYCODE.LENS_DETACH /* 786 */:
                break;
            default:
                ret = super.onKeyDown(keyCode, event);
                break;
        }
        if (ret == 0) {
            closeLayoutAndCancelDeleteProcess();
        }
        AppLog.exit("ID_DELETESINGLECONFIRMMENULAYOUT", AppLog.getMethodName());
        return ret;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyUp(int keyCode, KeyEvent event) {
        int scanCode = event.getScanCode();
        if (scanCode == 530 || scanCode == 786) {
            return -1;
        }
        if (scanCode == 595 || scanCode == 513) {
            return 1;
        }
        int ret = super.onKeyUp(keyCode, event);
        return ret;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedUpKey() {
        AppLog.enter("ID_DELETESINGLECONFIRMMENULAYOUT", AppLog.getMethodName());
        toggleSelection();
        AppLog.exit("ID_DELETESINGLECONFIRMMENULAYOUT", AppLog.getMethodName());
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedDownKey() {
        AppLog.enter("ID_DELETESINGLECONFIRMMENULAYOUT", AppLog.getMethodName());
        toggleSelection();
        AppLog.enter("ID_DELETESINGLECONFIRMMENULAYOUT", AppLog.getMethodName());
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedLeftKey() {
        AppLog.enter("ID_DELETESINGLECONFIRMMENULAYOUT", AppLog.getMethodName());
        AppLog.exit("ID_DELETESINGLECONFIRMMENULAYOUT", AppLog.getMethodName());
        return -1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedRightKey() {
        AppLog.enter("ID_DELETESINGLECONFIRMMENULAYOUT", AppLog.getMethodName());
        AppLog.exit("ID_DELETESINGLECONFIRMMENULAYOUT", AppLog.getMethodName());
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IConvertibleKeyHandler
    public int onConvertedKeyDown(KeyEvent event, IKeyFunction func) {
        AppLog.enter("ID_DELETESINGLECONFIRMMENULAYOUT", AppLog.getMethodName());
        int result = super.onConvertedKeyDown(event, func);
        CustomizableFunction customFunc = (CustomizableFunction) func;
        switch (customFunc) {
            case Guide:
            case DoNothing:
                result = -1;
                break;
            default:
                AppLog.info("ID_DELETESINGLECONFIRMMENULAYOUT", "default section of onKeyDown(), event key code:" + event.getKeyCode());
                break;
        }
        AppLog.exit("ID_DELETESINGLECONFIRMMENULAYOUT", AppLog.getMethodName());
        return result;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout
    public void closeMenuLayout(Bundle bundle) {
        AppLog.enter("ID_DELETESINGLECONFIRMMENULAYOUT", AppLog.getMethodName());
        OCUtil.getInstance().checkIfMenuStateClosed(bundle);
        super.closeMenuLayout(bundle);
        AppLog.exit("ID_DELETESINGLECONFIRMMENULAYOUT", AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedMainDialPrev() {
        AppLog.enter("ID_DELETESINGLECONFIRMMENULAYOUT", AppLog.getMethodName());
        AppLog.exit("ID_DELETESINGLECONFIRMMENULAYOUT", AppLog.getMethodName());
        return pushedUpKey();
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedMainDialNext() {
        AppLog.enter("ID_DELETESINGLECONFIRMMENULAYOUT", AppLog.getMethodName());
        AppLog.exit("ID_DELETESINGLECONFIRMMENULAYOUT", AppLog.getMethodName());
        return pushedDownKey();
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedSubDialPrev() {
        AppLog.enter("ID_DELETESINGLECONFIRMMENULAYOUT", AppLog.getMethodName());
        AppLog.exit("ID_DELETESINGLECONFIRMMENULAYOUT", AppLog.getMethodName());
        return pushedUpKey();
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedSubDialNext() {
        AppLog.enter("ID_DELETESINGLECONFIRMMENULAYOUT", AppLog.getMethodName());
        AppLog.exit("ID_DELETESINGLECONFIRMMENULAYOUT", AppLog.getMethodName());
        return pushedDownKey();
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedThirdDialPrev() {
        AppLog.enter("ID_DELETESINGLECONFIRMMENULAYOUT", AppLog.getMethodName());
        AppLog.exit("ID_DELETESINGLECONFIRMMENULAYOUT", AppLog.getMethodName());
        return pushedUpKey();
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedThirdDialNext() {
        AppLog.enter("ID_DELETESINGLECONFIRMMENULAYOUT", AppLog.getMethodName());
        AppLog.exit("ID_DELETESINGLECONFIRMMENULAYOUT", AppLog.getMethodName());
        return pushedDownKey();
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver
    protected String getKeyConvCategory() {
        AppLog.enter("ID_DELETESINGLECONFIRMMENULAYOUT", AppLog.getMethodName());
        AppLog.exit("ID_DELETESINGLECONFIRMMENULAYOUT", AppLog.getMethodName());
        return "Menu";
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachKeyHandler
    public int onLensPartsOperated(KeyEvent event) {
        return -1;
    }
}
