package com.sony.imaging.app.soundphoto.playback.menu.layout;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.sony.imaging.app.base.common.widget.FooterGuide;
import com.sony.imaging.app.base.common.widget.FooterGuideDataResId;
import com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout;
import com.sony.imaging.app.base.playback.PlayRootContainer;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.fw.CustomizableFunction;
import com.sony.imaging.app.fw.IKeyFunction;
import com.sony.imaging.app.soundphoto.R;
import com.sony.imaging.app.soundphoto.menu.layout.controller.DeleteOptionController;
import com.sony.imaging.app.soundphoto.playback.SPPlayRootContainer;
import com.sony.imaging.app.soundphoto.util.AppLog;
import com.sony.imaging.app.soundphoto.util.SPUtil;
import java.util.ArrayList;

/* loaded from: classes.dex */
public class SPDeleteMenuConfirmLayout extends DisplayMenuItemsMenuLayout {
    private static final String TAG = "SPDeleteMenuConfirmLayout";
    View mCurrentView = null;
    TextView mDelete;
    TextView mDeleteAudioOnly;

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver
    protected String getKeyConvCategory() {
        return "Menu";
    }

    protected int getLayoutResource() {
        return R.layout.sp_play_menu_delete_cmn_dialog_two_button_at_center;
    }

    protected int getFooterGuideResource() {
        return R.id.footer_guide;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        this.mCurrentView = obtainViewFromPool(getLayoutResource());
        this.mDelete = (TextView) this.mCurrentView.findViewById(R.id.button_upper);
        if (this.mDelete != null) {
            this.mDelete.setText(R.string.STRID_FUNC_SOUNDPHOTO_SELECT_DELETE);
            this.mDelete.setSelected(DeleteOptionController.getInstance().isDeleteImageSelectd());
        }
        this.mDeleteAudioOnly = (TextView) this.mCurrentView.findViewById(R.id.button_lower);
        if (this.mDeleteAudioOnly != null) {
            this.mDeleteAudioOnly.setText(R.string.STRID_FUNC_SOUNDPHOTO_DELETE_SOUND);
            this.mDeleteAudioOnly.setSelected(!DeleteOptionController.getInstance().isDeleteImageSelectd());
        }
        return this.mCurrentView;
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        SPUtil.getInstance().setSoundDataDeleteCalled(false);
        updateFooterGuide();
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IConvertibleKeyHandler
    public int onConvertedKeyDown(KeyEvent event, IKeyFunction func) {
        int returnStatus;
        AppLog.enter(TAG, AppLog.getMethodName());
        if (func.equals(CustomizableFunction.Guide) || func.equals(CustomizableFunction.Unchanged) || func.equals(CustomizableFunction.MainNext) || func.equals(CustomizableFunction.MainPrev) || func.equals(CustomizableFunction.SubNext) || func.equals(CustomizableFunction.SubPrev)) {
            AppLog.trace("Custom Key", "func.equals(CustomizableFunction.Guide)");
            returnStatus = super.onConvertedKeyDown(event, func);
        } else {
            AppLog.trace("Custom Key", "!!func.equals(CustomizableFunction.Guide)");
            returnStatus = 1;
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return returnStatus;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyDown(int keyCode, KeyEvent event) {
        int scanCode = event.getScanCode();
        boolean handle = false;
        switch (scanCode) {
            case AppRoot.USER_KEYCODE.ZOOM_LEVER_TELE /* 610 */:
            case AppRoot.USER_KEYCODE.ZOOM_LEVER_WIDE /* 611 */:
                handle = true;
                break;
        }
        if (handle) {
            return 1;
        }
        return super.onKeyDown(keyCode, event);
    }

    protected void updateFooterGuide() {
        AppLog.exit(TAG, AppLog.getMethodName());
        FooterGuide mFooterGuide = (FooterGuide) getView().findViewById(getFooterGuideResource());
        if (mFooterGuide != null) {
            mFooterGuide.setData(new FooterGuideDataResId(getActivity(), android.R.string.autofill_phone_suffix_re, android.R.string.httpErrorRedirectLoop));
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    protected void removeFooterGuide() {
        AppLog.enter(TAG, AppLog.getMethodName());
        FooterGuide mFooterGuide = (FooterGuide) getView().findViewById(getFooterGuideResource());
        if (mFooterGuide != null) {
            mFooterGuide.setData(null);
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        deinitializeView();
        super.onPause();
    }

    private void deinitializeView() {
        if (getView() != null) {
            removeFooterGuide();
        }
        this.mDelete = null;
        this.mDeleteAudioOnly = null;
        if (this.mCurrentView != null) {
            this.mCurrentView.destroyDrawingCache();
            this.mCurrentView = null;
        }
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout
    public void closeLayout() {
        deinitializeView();
        super.closeLayout();
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public void onDestroyView() {
        deinitializeView();
        super.onDestroyView();
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.FakeFragment
    public void onDestroy() {
        deinitializeView();
        super.onDestroy();
    }

    private void toggleSelection() {
        if (this.mDelete != null && this.mDeleteAudioOnly != null) {
            this.mDelete.setSelected(!this.mDelete.isSelected());
            this.mDeleteAudioOnly.setSelected(this.mDeleteAudioOnly.isSelected() ? false : true);
        } else if (this.mDelete != null) {
            this.mDelete.setSelected(true);
        } else if (this.mDeleteAudioOnly != null) {
            this.mDeleteAudioOnly.setSelected(true);
        }
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedUpKey() {
        AppLog.enter(TAG, AppLog.getMethodName());
        toggleSelection();
        AppLog.exit(TAG, AppLog.getMethodName());
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedDownKey() {
        AppLog.enter(TAG, AppLog.getMethodName());
        toggleSelection();
        AppLog.exit(TAG, AppLog.getMethodName());
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedCenterKey() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (this.mDelete != null && this.mDelete.isSelected()) {
            DeleteOptionController.getInstance().setValue(DeleteOptionController.DETELE_SELECTOR, DeleteOptionController.DELETE_IMAGE);
            openNextState(SPPlayRootContainer.ID_DELETE_MULTIPLE, null);
        } else if (this.mDeleteAudioOnly != null && this.mDeleteAudioOnly.isSelected()) {
            DeleteOptionController.getInstance().setValue(DeleteOptionController.DETELE_SELECTOR, DeleteOptionController.DELETE_ONLY_AUDIO);
            openNextState(SPPlayRootContainer.ID_DELETE_SOUND_DATA, null);
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return super.pushedCenterKey();
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedMainDialPrev() {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.exit(TAG, AppLog.getMethodName());
        return pushedUpKey();
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedMainDialNext() {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.exit(TAG, AppLog.getMethodName());
        return pushedDownKey();
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedDeleteFuncKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout
    protected void setFunctionGuideResources(ArrayList<Object> guideResources) {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (this.mDelete != null && this.mDelete.isSelected()) {
            setGuideResource(guideResources, R.string.STRID_FUNC_SOUNDPHOTO_SELECT_DELETE, android.R.string.font_family_body_2_material, true);
        } else if (this.mDeleteAudioOnly != null && this.mDeleteAudioOnly.isSelected()) {
            setGuideResource(guideResources, R.string.STRID_FUNC_SOUNDPHOTO_DELETE_SOUND, R.string.STRID_FUNC_SOUNDPHOTO_GUIDE_DELETE_SOUND, true);
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    private void setGuideResource(ArrayList<Object> guideResources, int guideTitleID, int guideDefi, boolean isAvailble) {
        guideResources.add(getText(guideTitleID));
        guideResources.add(getText(guideDefi));
        guideResources.add(Boolean.valueOf(isAvailble));
    }

    @Override // com.sony.imaging.app.fw.Layout
    public Drawable onGetBackground() {
        return BACKGROUND_BLACK;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedPlayBackKey() {
        AppLog.enter(TAG, AppLog.getMethodName());
        openNextState(PlayRootContainer.ID_BROWSER, null);
        AppLog.exit(TAG, AppLog.getMethodName());
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedPbZoomFuncMinus() {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.exit(TAG, AppLog.getMethodName());
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedPbZoomFuncPlus() {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.exit(TAG, AppLog.getMethodName());
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMenuKey() {
        AppLog.enter(TAG, AppLog.getMethodName());
        SPUtil.getInstance().setSoundDataDeleteCalled(false);
        if (this.mDelete != null && this.mDelete.isSelected()) {
            DeleteOptionController.getInstance().setValue(DeleteOptionController.DETELE_SELECTOR, DeleteOptionController.DELETE_IMAGE);
        } else if (this.mDeleteAudioOnly != null && this.mDeleteAudioOnly.isSelected()) {
            DeleteOptionController.getInstance().setValue(DeleteOptionController.DETELE_SELECTOR, DeleteOptionController.DELETE_ONLY_AUDIO);
        }
        openPreviousMenu();
        AppLog.exit(TAG, AppLog.getMethodName());
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedPlayIndexKey() {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.exit(TAG, AppLog.getMethodName());
        return -1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedAFMFKey() {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.exit(TAG, AppLog.getMethodName());
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedAfMfHoldCustomKey() {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.exit(TAG, AppLog.getMethodName());
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int releasedAfMfHoldCustomKey() {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.exit(TAG, AppLog.getMethodName());
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedAfMfToggleCustomKey() {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.exit(TAG, AppLog.getMethodName());
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int releasedAELHoldCustomKey() {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.exit(TAG, AppLog.getMethodName());
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedAELHoldCustomKey() {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.exit(TAG, AppLog.getMethodName());
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedAELToggleCustomKey() {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.exit(TAG, AppLog.getMethodName());
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int attachedLens() {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.exit(TAG, AppLog.getMethodName());
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int detachedLens() {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.exit(TAG, AppLog.getMethodName());
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int releasedPbZoomFuncPlus() {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.exit(TAG, AppLog.getMethodName());
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedFnKey() {
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int releasedAFMFKey() {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.exit(TAG, AppLog.getMethodName());
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int releasedAELKey() {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.exit(TAG, AppLog.getMethodName());
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int releasedAfMfAelKey() {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.exit(TAG, AppLog.getMethodName());
        return 1;
    }
}
