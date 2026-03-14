package com.sony.imaging.app.pictureeffectplus.shooting.layout;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.sony.imaging.app.base.common.widget.FooterGuide;
import com.sony.imaging.app.base.common.widget.FooterGuideDataResId;
import com.sony.imaging.app.base.menu.BaseMenuService;
import com.sony.imaging.app.base.menu.HistoryItem;
import com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout;
import com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout;
import com.sony.imaging.app.base.menu.layout.SpecialScreenView;
import com.sony.imaging.app.pictureeffectplus.AppLog;
import com.sony.imaging.app.pictureeffectplus.R;
import com.sony.imaging.app.pictureeffectplus.shooting.camera.PictureEffectPlusController;
import java.util.ArrayList;

/* loaded from: classes.dex */
public class PictureEffectPlusOptionMenuLayoutSub extends SpecialScreenMenuLayout {
    private static final String TAG = AppLog.getClassName();
    protected static final StringBuilder STRBUILD = new StringBuilder();
    protected String FUNC_NAME = "";
    private ViewGroup mCurrentView = null;
    private FooterGuide mFooterGuide = null;
    private ArrayList<String> mList = null;
    private String mCurSelectedEffect = null;
    private PictureEffectPlusController mController = null;
    private String lastItemID = null;

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.mCurrentView = (ViewGroup) super.onCreateView(inflater, container, savedInstanceState);
        Context context = getActivity().getApplicationContext();
        this.mService = new BaseMenuService(context);
        this.lastItemID = this.mService.getMenuItemId();
        this.mController = PictureEffectPlusController.getInstance();
        this.mCurSelectedEffect = this.mController.getBackupEffectValue();
        this.mService.setMenuItemId(this.mCurSelectedEffect);
        this.mOptionService = new BaseMenuService(context);
        this.mAdapter = new SpecialScreenMenuLayout.SpecialBaseMenuAdapter(context, R.layout.menu_sub_adapter, this.mService);
        this.mFooterGuide = (FooterGuide) this.mCurrentView.findViewById(R.id.footer_guide);
        this.mFooterGuide.setData(new FooterGuideDataResId(context, android.R.string.zen_mode_feature_name, android.R.string.httpErrorRedirectLoop));
        this.mOptionAdapter = new SpecialScreenMenuLayout.SpecialBaseMenuAdapter(context, R.layout.menu_sub_adapter, this.mOptionService);
        this.mList = this.mService.getMenuItemList();
        return this.mCurrentView;
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout
    protected int getLayoutID() {
        return R.layout.pictureeffectplus_menu_special;
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        AppLog.enter(TAG, AppLog.getMethodName());
        super.onResume();
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.SpecialScreenView.OnItemSelectedListener
    public void onItemSelected(SpecialScreenView parent, View view, int position, long id) {
        AppLog.enter(TAG, AppLog.getMethodName());
        super.onItemSelected(parent, view, position, id);
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedCenterKey() {
        if (this.mController.isComingFromApplicationSettings()) {
            this.mController.setComingFromApplicationSettings(false);
        }
        closeMenuLayout(null);
        Log.d(TAG, "pressed SK1!");
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMenuKey() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (!this.mController.isComingFromApplicationSettings()) {
            return super.pushedMenuKey();
        }
        cancelValue();
        String prevLayoutID = getMenuData().getPreviousMenuLayoutId();
        HistoryItem historyItem = new HistoryItem(this.lastItemID, prevLayoutID);
        this.mService.pushMenuHistory(historyItem);
        openPreviousMenu();
        this.mController.setComingFromApplicationSettings(false);
        AppLog.exit(TAG, AppLog.getMethodName());
        return 1;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout
    public DisplayMenuItemsMenuLayout.MenuUpdater getMenuUpdater() {
        return super.getMenuUpdater();
    }
}
