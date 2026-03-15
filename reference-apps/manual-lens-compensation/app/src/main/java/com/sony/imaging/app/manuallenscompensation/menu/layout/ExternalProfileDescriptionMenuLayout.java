package com.sony.imaging.app.manuallenscompensation.menu.layout;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.sony.imaging.app.base.menu.layout.BaseMenuLayout;
import com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.manuallenscompensation.R;
import com.sony.imaging.app.manuallenscompensation.commonUtil.AppLog;
import com.sony.imaging.app.manuallenscompensation.commonUtil.MenuHistory;
import com.sony.imaging.app.manuallenscompensation.commonUtil.OCBackUpKey;
import com.sony.imaging.app.manuallenscompensation.commonUtil.OCConstants;
import com.sony.imaging.app.manuallenscompensation.database.LensCompensationParameter;
import com.sony.imaging.app.manuallenscompensation.shooting.controller.OCController;

/* loaded from: classes.dex */
public class ExternalProfileDescriptionMenuLayout extends DisplayMenuItemsMenuLayout {
    public static final String TAG = "ID_EXTERNALPROFILEDESCRIPTIONMENULAYOUT";
    View mCurrentView = null;
    TextView mLabelProfileName = null;
    TextView mLabelFocalLength = null;
    TextView mLabelFMin = null;
    TextView mLabelFileName = null;
    TextView mTxtFValue = null;
    TextView mTxtShadingValue = null;
    TextView mTxtAbrationValue = null;
    TextView mTxtDistortionValue = null;
    ImageView mImageFValue = null;
    ImageView mImageShadingValue = null;
    ImageView mImageAbrationValue = null;
    ImageView mIageDistortionValue = null;

    protected int getLayoutResource() {
        AppLog.info(TAG, AppLog.getMethodName());
        return R.layout.external_profile_description_menu_layout;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        AppLog.enter(TAG, AppLog.getMethodName());
        super.onCreateView(inflater, container, savedInstanceState);
        this.mCurrentView = obtainViewFromPool(getLayoutResource());
        initialiseView();
        AppLog.exit(TAG, AppLog.getMethodName());
        return this.mCurrentView;
    }

    private void initialiseView() {
        AppLog.enter(TAG, AppLog.getMethodName());
        this.mLabelProfileName = (TextView) this.mCurrentView.findViewById(R.id.labelprofilename);
        this.mLabelFocalLength = (TextView) this.mCurrentView.findViewById(R.id.labelfocallength);
        this.mLabelFMin = (TextView) this.mCurrentView.findViewById(R.id.labelfmin);
        this.mLabelFileName = (TextView) this.mCurrentView.findViewById(R.id.labelfilename);
        this.mTxtFValue = (TextView) this.mCurrentView.findViewById(R.id.txtfvalue);
        this.mTxtShadingValue = (TextView) this.mCurrentView.findViewById(R.id.txtshadingvalue);
        this.mTxtAbrationValue = (TextView) this.mCurrentView.findViewById(R.id.txtabrationvalue);
        this.mTxtDistortionValue = (TextView) this.mCurrentView.findViewById(R.id.txtdistortionvalue);
        this.mImageFValue = (ImageView) this.mCurrentView.findViewById(R.id.imagefvalue);
        this.mImageShadingValue = (ImageView) this.mCurrentView.findViewById(R.id.imageshadingvalue);
        this.mImageAbrationValue = (ImageView) this.mCurrentView.findViewById(R.id.imageabrationvalue);
        this.mIageDistortionValue = (ImageView) this.mCurrentView.findViewById(R.id.imagedistortionvalue);
        this.mImageFValue.setBackgroundResource(R.drawable.p_16_dd_parts_oc_aperture);
        this.mImageShadingValue.setBackgroundResource(R.drawable.p_16_dd_parts_oc_adjust_shading);
        this.mImageAbrationValue.setBackgroundResource(R.drawable.p_16_dd_parts_oc_adjust_chromatic);
        this.mIageDistortionValue.setBackgroundResource(R.drawable.p_16_dd_parts_oc_adjust_distortion);
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    private void deinitizeView() {
        AppLog.enter(TAG, AppLog.getMethodName());
        this.mLabelProfileName = null;
        this.mLabelFocalLength = null;
        this.mLabelFMin = null;
        this.mLabelFileName = null;
        this.mTxtFValue = null;
        this.mTxtShadingValue = null;
        this.mTxtAbrationValue = null;
        this.mTxtDistortionValue = null;
        if (this.mImageFValue != null) {
            releaseImageViewDrawable(this.mImageFValue);
            this.mImageFValue = null;
        }
        if (this.mImageShadingValue != null) {
            releaseImageViewDrawable(this.mImageShadingValue);
            this.mImageShadingValue = null;
        }
        if (this.mImageAbrationValue != null) {
            releaseImageViewDrawable(this.mImageAbrationValue);
            this.mImageAbrationValue = null;
        }
        if (this.mIageDistortionValue != null) {
            releaseImageViewDrawable(this.mIageDistortionValue);
            this.mIageDistortionValue = null;
        }
        if (this.mCurrentView != null) {
            this.mCurrentView.destroyDrawingCache();
        }
        this.mCurrentView = null;
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    private void releaseImageViewDrawable(ImageView imageView) {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (imageView != null) {
            imageView.setBackgroundResource(0);
            imageView.setImageBitmap(null);
            if (imageView.getDrawable() != null) {
                imageView.getDrawable().setCallback(null);
            }
            imageView.setImageDrawable(null);
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        AppLog.enter(TAG, AppLog.getMethodName());
        super.onResume();
        initialiseView();
        LensCompensationParameter param = null;
        String file_name = null;
        if (this.data != null) {
            param = (LensCompensationParameter) this.data.getParcelable(OCBackUpKey.KEY_BKUP_SELECTED_PARAM);
            file_name = this.data.getString(OCConstants.EXTERNAL_PROFILE_FILE_NAME);
        }
        if (param != null) {
            setViewValues(param, file_name);
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMenuKey() {
        AppLog.enter(TAG, AppLog.getMethodName());
        String layoutID = MenuHistory.getInstance().popMenuItem();
        if (layoutID != null) {
            openMenuLayout(layoutID, this.data);
        } else {
            openPreviousMenu();
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedDeleteFuncKey() {
        AppLog.enter(TAG, AppLog.getMethodName());
        openProfileDeleteConfirmation();
        AppLog.exit(TAG, AppLog.getMethodName());
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedDeleteKey() {
        AppLog.enter(TAG, AppLog.getMethodName());
        openProfileDeleteConfirmation();
        AppLog.exit(TAG, AppLog.getMethodName());
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyDown(int keyCode, KeyEvent event) {
        int result;
        AppLog.enter(TAG, AppLog.getMethodName());
        switch (event.getScanCode()) {
            case AppRoot.USER_KEYCODE.SK2 /* 513 */:
            case AppRoot.USER_KEYCODE.DELETE /* 595 */:
                openProfileDeleteConfirmation();
                result = 1;
                break;
            default:
                result = super.onKeyDown(keyCode, event);
                break;
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return result;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyUp(int keyCode, KeyEvent event) {
        int scanCode = event.getScanCode();
        int ret = 1;
        String layoutID = MenuHistory.getInstance().popMenuItem();
        if (scanCode == 530 || scanCode == 786) {
            ret = -1;
        } else if (scanCode != 595 && scanCode != 513 && layoutID != null) {
            ret = super.onKeyUp(keyCode, event);
        }
        if (layoutID != null) {
            MenuHistory.getInstance().pushMenuItem(layoutID);
        }
        return ret;
    }

    private void setViewValues(LensCompensationParameter params, String file_name) {
        AppLog.enter(TAG, AppLog.getMethodName());
        String lense_name = params.mLensName;
        String f_value = params.mFValue;
        String focal_length = params.mFocalLength;
        if (lense_name != null && !params.mLensName.equalsIgnoreCase("")) {
            this.mLabelProfileName.setText(params.mLensName);
        } else {
            this.mLabelProfileName.setText("--");
        }
        if (focal_length != null && !params.mFocalLength.equalsIgnoreCase(String.valueOf(0))) {
            this.mLabelFocalLength.setText(params.mFocalLength + OCConstants.FOCALLENGTH_UNIT_STRING);
        } else {
            this.mLabelFocalLength.setText(OCConstants.FOCALLENGTH_NULL_STRING);
        }
        this.mLabelFileName.setText(file_name);
        if (f_value != null) {
            this.mLabelFMin.setText(OCConstants.F_VAL_PREFIX_STRING + params.mFValue);
            this.mTxtFValue.setText(OCConstants.F_VAL_PREFIX_STRING + params.mFValue);
        } else {
            this.mLabelFMin.setText(OCConstants.EMPTY_STRING_FVALUE);
            this.mTxtFValue.setText(OCConstants.EMPTY_STRING_FVALUE);
        }
        int param1 = params.getLevel(OCController.LIGHT_VIGNETTING);
        int param2 = params.getLevel(OCController.RED_COLOR_VIGNETTING);
        int param3 = params.getLevel(OCController.BLUE_COLOR_VIGNETTING);
        int param4 = params.getLevel(OCController.RED_CHROMATIC_ABERRATION);
        int param5 = params.getLevel(OCController.BLUE_CHROMATIC_ABERRATION);
        int param6 = params.getLevel(OCController.DISTORTION);
        getSignedString(param1);
        this.mTxtShadingValue.setText("" + getSignedString(param1) + "," + getSignedString(param2) + "," + getSignedString(param3));
        this.mTxtAbrationValue.setText("" + getSignedString(param4) + "," + getSignedString(param5));
        this.mTxtDistortionValue.setText("" + getSignedString(param6));
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    private String getSignedString(int param1) {
        String str;
        AppLog.enter(TAG, AppLog.getMethodName());
        if (param1 > 0) {
            str = "+" + String.valueOf(param1);
        } else if (param1 < 0) {
            str = String.valueOf(param1);
        } else {
            str = String.valueOf(param1);
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return str;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout
    public void closeLayout() {
        deinitizeView();
        super.closeLayout();
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        deinitizeView();
        super.onPause();
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public void onDestroyView() {
        AppLog.enter(TAG, AppLog.getMethodName());
        deinitizeView();
        super.onDestroyView();
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    private void openProfileDeleteConfirmation() {
        AppLog.enter(TAG, AppLog.getMethodName());
        MenuHistory.getInstance().pushMenuItem(TAG);
        BaseMenuLayout layout = (BaseMenuLayout) getLayout("ID_DELETESINGLECONFIRMMENULAYOUT");
        if (layout != null) {
            layout.setMenuLayoutListener(getMenuLayoutListener());
        }
        openLayout("ID_DELETESINGLECONFIRMMENULAYOUT", this.data);
        AppLog.exit(TAG, AppLog.getMethodName());
    }
}
