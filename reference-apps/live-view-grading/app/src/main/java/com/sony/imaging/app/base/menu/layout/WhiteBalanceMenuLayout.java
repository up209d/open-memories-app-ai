package com.sony.imaging.app.base.menu.layout;

import android.R;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.sony.imaging.app.base.BaseBackUpKey;
import com.sony.imaging.app.base.menu.MenuDataParcelable;
import com.sony.imaging.app.base.menu.MenuTable;
import com.sony.imaging.app.base.shooting.camera.WhiteBalanceController;
import com.sony.imaging.app.util.BackUpUtil;
import java.util.ArrayList;

/* loaded from: classes.dex */
public class WhiteBalanceMenuLayout extends SpecialScreenMenuLayout {
    private static final String TAG = "WhiteBalanceMenuLayout";
    private String mItemIdForCWBreset;
    private ArrayList<String> mList;
    private String mMenuItemOptionGuideText;
    private String mMenuItemSubGuideText;
    private WhiteBalanceController.WhiteBalanceParam mOldParam;
    private WhiteBalanceController.WhiteBalanceParam mWBparam;
    private WhiteBalanceController wbc;
    private TextView mLightTextView = null;
    private TextView mCompTextView = null;
    protected TextView mTempTextView = null;
    private ImageView mTempTextBGView = null;
    private ImageView mArrowView = null;
    private ImageView mShadeArea = null;
    private ImageView mAjdustTempBGView = null;
    private ImageView mAdjustTempArrowTopView = null;
    private ImageView mAdjustTempArrowBottomView = null;
    private ImageView mAdjustTempArrowRightView = null;
    private int MIN_TEMP = -1;
    private int MAX_TEMP = -1;
    private final int TEMP_STEP = 100;
    protected Layout mLayoutStatus = Layout.SUB;

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes.dex */
    public enum Layout {
        SUB,
        OPTION
    }

    protected void setLayoutStatus(Layout layout) {
        this.mLayoutStatus = layout;
        if (this.mShadeArea != null) {
            this.mShadeArea.setVisibility(Layout.SUB == layout ? 4 : 0);
        }
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        this.wbc = WhiteBalanceController.getInstance();
        this.MIN_TEMP = this.wbc.getMinColorTemoerature();
        this.MAX_TEMP = this.wbc.getMaxColorTemoerature();
        this.mOldParam = (WhiteBalanceController.WhiteBalanceParam) this.wbc.getDetailValue();
        this.mItemIdForCWBreset = this.wbc.getValue();
        MenuDataParcelable parcelable = (MenuDataParcelable) this.data.getParcelable(MenuDataParcelable.KEY);
        String layout = parcelable.getPreviousMenuLayoutId();
        if (this.wbc.getValue().equals(WhiteBalanceController.COLOR_TEMP) && layout != null && layout.equals(WhiteBalanceAdjustmentMenuLayout.MENU_ID)) {
            setLayoutStatus(Layout.OPTION);
            this.mOldParam.setColorTemp(BackUpUtil.getInstance().getPreferenceInt(BaseBackUpKey.ID_WB_COLOR_TEMP, 0));
        } else {
            setLayoutStatus(Layout.SUB);
        }
        this.mMenuItemSubGuideText = getResources().getString(R.string.ext_media_status_bad_removal);
        this.mMenuItemOptionGuideText = getResources().getString(R.string.ext_media_status_checking);
        this.mList = this.mService.getSupportedItemList();
        updateRightArrow();
        if (this.mLayoutStatus == Layout.SUB) {
            disappearCTempAndTempArrow();
        } else {
            appearCTempAndTempArrow();
        }
        updateText();
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public ViewGroup onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup currentView = (ViewGroup) super.onCreateView(inflater, container, savedInstanceState);
        this.mShadeArea = (ImageView) currentView.findViewById(com.sony.imaging.app.base.R.id.specialscreen_shade_area);
        if (this.mShadeArea != null) {
            this.mShadeArea.setVisibility(Layout.SUB == this.mLayoutStatus ? 4 : 0);
        }
        this.mLightTextView = (TextView) currentView.findViewById(com.sony.imaging.app.base.R.id.light_text);
        this.mCompTextView = (TextView) currentView.findViewById(com.sony.imaging.app.base.R.id.comp_text);
        this.mTempTextView = (TextView) currentView.findViewById(com.sony.imaging.app.base.R.id.temp_text);
        this.mTempTextBGView = (ImageView) currentView.findViewById(com.sony.imaging.app.base.R.id.temp_background_sub);
        this.mArrowView = (ImageView) currentView.findViewById(com.sony.imaging.app.base.R.id.arrow_right);
        this.mAjdustTempBGView = (ImageView) currentView.findViewById(com.sony.imaging.app.base.R.id.temp_background_option);
        this.mAdjustTempArrowTopView = (ImageView) currentView.findViewById(com.sony.imaging.app.base.R.id.temp_arrow_top);
        this.mAdjustTempArrowBottomView = (ImageView) currentView.findViewById(com.sony.imaging.app.base.R.id.temp_arrow_bottom);
        this.mAdjustTempArrowRightView = (ImageView) currentView.findViewById(com.sony.imaging.app.base.R.id.temp_arrow_right);
        return currentView;
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout
    protected int getLayoutID() {
        return com.sony.imaging.app.base.R.layout.menu_wb_sub;
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public void onDestroyView() {
        this.mShadeArea = null;
        this.mLightTextView = null;
        this.mCompTextView = null;
        this.mTempTextView = null;
        this.mTempTextBGView = null;
        this.mArrowView = null;
        this.mAjdustTempBGView = null;
        this.mAdjustTempArrowTopView = null;
        this.mAdjustTempArrowBottomView = null;
        this.mAdjustTempArrowRightView = null;
        super.onDestroyView();
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        this.mList = null;
        this.mOldParam = null;
        this.mWBparam = null;
        this.wbc = null;
        super.onPause();
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.SpecialScreenView.OnItemSelectedListener
    public void onItemSelected(SpecialScreenView parent, View view, int position, long id) {
        super.onItemSelected(parent, view, position, id);
        int pos = this.mSpecialScreenView.getSelectedItemPosition();
        String itemId = this.mList.get(pos);
        if (this.mService.execCurrentMenuItem(itemId, false)) {
            this.mIsChangedValue = true;
            this.mWBparam = (WhiteBalanceController.WhiteBalanceParam) this.wbc.getDetailValue();
        }
    }

    protected void updateRightArrow() {
        int pos = this.mSpecialScreenView.getSelectedItemPosition();
        String itemId = this.mList.get(pos);
        String value = this.mService.getMenuItemValue(itemId);
        if (!WhiteBalanceController.CUSTOM_SET.equals(value)) {
            if (this.wbc.getSupportedValue(WhiteBalanceController.SETTING_LIGHTBALANCE) == null || this.wbc.getSupportedValue(WhiteBalanceController.SETTING_COMPENSATION) == null) {
                this.mArrowView.setVisibility(4);
                return;
            } else {
                this.mArrowView.setVisibility(0);
                return;
            }
        }
        this.mArrowView.setVisibility(4);
    }

    protected void updateText() {
        int pos = this.mSpecialScreenView.getSelectedItemPosition();
        String itemId = this.mList.get(pos);
        String value = this.mService.getMenuItemValue(itemId);
        this.mItemNameView.setText(this.mService.getMenuItemText(itemId));
        if ("custom1".equals(value) || "custom2".equals(value) || "custom3".equals(value)) {
            this.wbc.setCustomWhiteBalance(value);
        }
        if (this.wbc.getSupportedValue(WhiteBalanceController.SETTING_LIGHTBALANCE) == null || this.wbc.getSupportedValue(WhiteBalanceController.SETTING_COMPENSATION) == null) {
            this.mCompTextView.setVisibility(4);
            this.mLightTextView.setVisibility(4);
        } else {
            this.mCompTextView.setText(makeWBOptionText(value, WhiteBalanceController.COMP));
            this.mLightTextView.setText(makeWBOptionText(value, WhiteBalanceController.LIGHT));
            this.mCompTextView.setVisibility(0);
            this.mLightTextView.setVisibility(0);
        }
        this.mItemNameView.setVisibility(0);
        if (WhiteBalanceController.COLOR_TEMP.equals(value) || "custom".equals(value) || "custom1".equals(value) || "custom2".equals(value) || "custom3".equals(value)) {
            this.mTempTextBGView.setVisibility(0);
            this.mTempTextView.setText(makeWBOptionText(value, WhiteBalanceController.TEMP));
            this.mTempTextView.setVisibility(0);
        } else {
            this.mTempTextView.setVisibility(4);
            this.mTempTextBGView.setVisibility(4);
        }
    }

    protected String makeWBOptionText(String value, String option) {
        String ret = "";
        if (WhiteBalanceController.CUSTOM_SET.equals(value)) {
            return "";
        }
        this.mWBparam = (WhiteBalanceController.WhiteBalanceParam) this.wbc.getDetailValue();
        if (this.mWBparam != null) {
            if (WhiteBalanceController.TEMP.equals(option)) {
                ret = this.mWBparam.getColorTemp() + WhiteBalanceController.DISP_TEXT_K;
            } else if (WhiteBalanceController.LIGHT.equals(option)) {
                if (this.mWBparam.getLightBalance() > 0) {
                    ret = WhiteBalanceController.DISP_TEXT_AB_PLUS + Math.abs(this.mWBparam.getLightBalance());
                } else if (this.mWBparam.getLightBalance() == 0) {
                    ret = WhiteBalanceController.DISP_TEXT_AB_ZERO + Math.abs(this.mWBparam.getLightBalance());
                } else {
                    ret = WhiteBalanceController.DISP_TEXT_AB_MINUS + Math.abs(this.mWBparam.getLightBalance());
                }
            } else if (WhiteBalanceController.COMP.equals(option)) {
                if (this.mWBparam.getColorComp() > 0) {
                    ret = WhiteBalanceController.DISP_TEXT_GM_PLUS + Math.abs(this.mWBparam.getColorComp());
                } else if (this.mWBparam.getColorComp() == 0) {
                    ret = WhiteBalanceController.DISP_TEXT_GM_ZERO + Math.abs(this.mWBparam.getColorComp());
                } else {
                    ret = WhiteBalanceController.DISP_TEXT_GM_MINUS + Math.abs(this.mWBparam.getColorComp());
                }
            }
            return ret;
        }
        return "No Parameters";
    }

    protected void appearCTempAndTempArrow() {
        int pos = this.mSpecialScreenView.getSelectedItemPosition();
        String itemId = this.mList.get(pos);
        String value = this.mService.getMenuItemValue(itemId);
        this.mTempTextView.setText(makeWBOptionText(value, WhiteBalanceController.TEMP));
        this.mTempTextView.setVisibility(0);
        this.mAjdustTempBGView.setVisibility(0);
        if (this.wbc.getSupportedValue(WhiteBalanceController.SETTING_LIGHTBALANCE) == null || this.wbc.getSupportedValue(WhiteBalanceController.SETTING_COMPENSATION) == null) {
            this.mAdjustTempArrowRightView.setVisibility(4);
        } else {
            this.mAdjustTempArrowRightView.setVisibility(0);
        }
        updateCTempAndTempArrow(this.mWBparam.getColorTemp());
    }

    protected void disappearCTempAndTempArrow() {
        this.mTempTextView.setVisibility(4);
        this.mAjdustTempBGView.setVisibility(4);
        this.mAdjustTempArrowTopView.setVisibility(4);
        this.mAdjustTempArrowBottomView.setVisibility(4);
        this.mAdjustTempArrowRightView.setVisibility(4);
    }

    private void updateCTempAndTempArrow(int temp) {
        if (temp == this.MAX_TEMP) {
            this.mAdjustTempArrowTopView.setVisibility(4);
            this.mAdjustTempArrowBottomView.setVisibility(0);
        } else if (temp == this.MIN_TEMP) {
            this.mAdjustTempArrowTopView.setVisibility(0);
            this.mAdjustTempArrowBottomView.setVisibility(4);
        } else {
            this.mAdjustTempArrowTopView.setVisibility(0);
            this.mAdjustTempArrowBottomView.setVisibility(0);
        }
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedDownKey() {
        int temp;
        int pos = this.mSpecialScreenView.getSelectedItemPosition();
        String itemId = this.mList.get(pos);
        String value = this.mService.getMenuItemValue(itemId);
        if (WhiteBalanceController.COLOR_TEMP.equals(value) && this.mLayoutStatus == Layout.OPTION) {
            int temp2 = this.mWBparam.getColorTemp();
            if (-1 == this.MIN_TEMP) {
                return -1;
            }
            if (temp2 != this.MIN_TEMP) {
                temp = temp2 - 100;
            } else {
                temp = this.MAX_TEMP;
            }
            updateCTempAndTempArrow(temp);
            this.mWBparam.setColorTemp(temp);
            this.wbc.setDetailValue(this.mWBparam);
            this.mTempTextView.setText(makeWBOptionText(value, WhiteBalanceController.TEMP));
        } else {
            super.pushedDownKey();
            updateRightArrow();
            updateText();
        }
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedUpKey() {
        int temp;
        int pos = this.mSpecialScreenView.getSelectedItemPosition();
        String itemId = this.mList.get(pos);
        String value = this.mService.getMenuItemValue(itemId);
        if (WhiteBalanceController.COLOR_TEMP.equals(value) && this.mLayoutStatus == Layout.OPTION) {
            int temp2 = this.mWBparam.getColorTemp();
            if (-1 == this.MAX_TEMP) {
                return -1;
            }
            if (temp2 != this.MAX_TEMP) {
                temp = temp2 + 100;
            } else {
                temp = this.MIN_TEMP;
            }
            updateCTempAndTempArrow(temp);
            this.mWBparam.setColorTemp(temp);
            this.wbc.setDetailValue(this.mWBparam);
            this.mTempTextView.setText(makeWBOptionText(value, WhiteBalanceController.TEMP));
        } else {
            super.pushedUpKey();
            updateRightArrow();
            updateText();
        }
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedRightKey() {
        String nextMenu;
        int pos = this.mSpecialScreenView.getSelectedItemPosition();
        String itemId = this.mList.get(pos);
        String value = this.mService.getMenuItemValue(itemId);
        if (WhiteBalanceController.CUSTOM_SET.equals(value)) {
            return -1;
        }
        if (WhiteBalanceController.COLOR_TEMP.equals(value) && this.mLayoutStatus == Layout.SUB) {
            setLayoutStatus(Layout.OPTION);
            BackUpUtil.getInstance().setPreference(BaseBackUpKey.ID_WB_COLOR_TEMP, Integer.valueOf(this.mWBparam.getColorTemp()));
            appearCTempAndTempArrow();
            updateCTempAndTempArrow(this.mWBparam.getColorTemp());
            return 1;
        }
        if (this.wbc.getSupportedValue(WhiteBalanceController.SETTING_LIGHTBALANCE) == null || this.wbc.getSupportedValue(WhiteBalanceController.SETTING_COMPENSATION) == null || (nextMenu = this.mService.getMenuItemNextMenuID(itemId)) == null || !this.mService.isMenuItemValid(itemId)) {
            return -1;
        }
        openNextMenu(itemId, nextMenu);
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedCenterKey() {
        super.pushedCenterKey();
        int pos = this.mSpecialScreenView.getSelectedItemPosition();
        String itemId = this.mList.get(pos);
        String value = this.mService.getMenuItemValue(itemId);
        if (!WhiteBalanceController.COLOR_TEMP.equals(value) || this.mLayoutStatus != Layout.OPTION) {
            return -1;
        }
        setLayoutStatus(Layout.SUB);
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedLeftKey() {
        updateText();
        int pos = this.mSpecialScreenView.getSelectedItemPosition();
        String itemId = this.mList.get(pos);
        String value = this.mService.getMenuItemValue(itemId);
        if (!WhiteBalanceController.COLOR_TEMP.equals(value) || this.mLayoutStatus != Layout.OPTION) {
            return -1;
        }
        setLayoutStatus(Layout.SUB);
        disappearCTempAndTempArrow();
        this.mTempTextView.setText(makeWBOptionText(value, WhiteBalanceController.TEMP));
        this.mTempTextView.setVisibility(0);
        this.mTempTextBGView.setVisibility(0);
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedSubDialPrev() {
        return pushedUpKey();
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedSubDialNext() {
        return pushedDownKey();
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMenuKey() {
        if (this.mLayoutStatus == Layout.OPTION) {
            setLayoutStatus(Layout.SUB);
            disappearCTempAndTempArrow();
            this.mWBparam.setColorTemp(this.mOldParam.getColorTemp());
            this.wbc.setDetailValue(this.mOldParam);
            updateText();
            return 1;
        }
        super.pushedMenuKey();
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout
    protected void setFunctionGuideResources(ArrayList<Object> guideResources) {
        int position = this.mSpecialScreenView.getSelectedItemPosition();
        String itemId = this.mList.get(position);
        String value = this.mService.getMenuItemValue(itemId);
        guideResources.add(this.mService.getMenuItemText(itemId));
        if (WhiteBalanceController.COLOR_TEMP.equals(value)) {
            if (this.mLayoutStatus == Layout.SUB) {
                guideResources.add(this.mMenuItemSubGuideText);
                return;
            } else {
                guideResources.add(this.mMenuItemOptionGuideText);
                return;
            }
        }
        guideResources.add(this.mService.getMenuItemGuideText(itemId));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout
    public void doItemClickProcessing(String itemid) {
        String execType = this.mService.getMenuItemExecType(itemid);
        String nextFragmentID = this.mService.getMenuItemNextMenuID(itemid);
        if (this.mService.isMenuItemValid(itemid) && MenuTable.NEXT_STATE.equals(execType)) {
            Bundle bundle = new Bundle();
            bundle.putString(WhiteBalanceController.BUNDLE_RESET_ITEMID, this.mItemIdForCWBreset);
            openNextState(nextFragmentID, bundle);
            return;
        }
        super.doItemClickProcessing(itemid);
    }
}
