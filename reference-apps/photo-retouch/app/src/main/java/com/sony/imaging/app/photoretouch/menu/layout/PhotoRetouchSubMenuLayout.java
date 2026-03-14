package com.sony.imaging.app.photoretouch.menu.layout;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.caution.IkeyDispatchEach;
import com.sony.imaging.app.base.common.widget.FooterGuide;
import com.sony.imaging.app.base.common.widget.FooterGuideDataResId;
import com.sony.imaging.app.base.menu.BaseMenuService;
import com.sony.imaging.app.base.menu.layout.BaseMenuAdapter;
import com.sony.imaging.app.base.menu.layout.SpecialScreenArea;
import com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout;
import com.sony.imaging.app.base.menu.layout.SpecialScreenView;
import com.sony.imaging.app.base.menu.layout.SubMenuView;
import com.sony.imaging.app.base.playback.contents.ContentsManager;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.fw.IKeyFunction;
import com.sony.imaging.app.fw.Layout;
import com.sony.imaging.app.photoretouch.Constant;
import com.sony.imaging.app.photoretouch.R;
import com.sony.imaging.app.photoretouch.common.ImageEditor;
import com.sony.imaging.app.photoretouch.common.widget.EachButton;
import com.sony.imaging.app.photoretouch.playback.layout.PhotoRetouchBrowserSingleLayout;
import com.sony.imaging.app.util.BeepUtility;
import com.sony.imaging.app.util.BeepUtilityRsrcTable;
import com.sony.scalar.sysutil.didep.Gpelibrary;
import java.util.ArrayList;

/* loaded from: classes.dex */
public class PhotoRetouchSubMenuLayout extends SpecialScreenMenuLayout implements AdapterView.OnItemSelectedListener, AdapterView.OnItemClickListener, SubMenuView.OnItemUpdatedListener, SubMenuView.OnItemScrollListener, ImageEditor.SaveCallback, ImageEditor.Callback, View.OnClickListener {
    Context context;
    ArrayList<String> mList;
    private Button mSaveBtn;
    private TextView mToolNameView;
    private static String mItemId = "framing";
    public static View.OnTouchListener blockTouchEvent = new View.OnTouchListener() { // from class: com.sony.imaging.app.photoretouch.menu.layout.PhotoRetouchSubMenuLayout.2
        @Override // android.view.View.OnTouchListener
        public boolean onTouch(View v, MotionEvent event) {
            return true;
        }
    };
    private final String TAG = PhotoRetouchSubMenuLayout.class.getSimpleName();
    private FooterGuide mFooterGuide = null;
    private ImageView mShadeArea = null;
    private ArrayList<Layout> mToolKitLayoutArrayList = null;
    private int mWorkingToolKitPosition = -1;
    private ContentsManager mContentsManager = null;
    public Rect mRect = null;

    public int getWorkingToolKitPosition() {
        return this.mWorkingToolKitPosition;
    }

    public BaseMenuAdapter getPRSubMenuAdapter() {
        return this.mAdapter;
    }

    public void setWorkingToolKitPosition(int workingToolKitPosition) {
        this.mWorkingToolKitPosition = workingToolKitPosition;
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Gpelibrary.changeFrameBufferPixel(Gpelibrary.GS_FRAMEBUFFER_TYPE.ABGR8888);
        ImageEditor.refreshIndexView();
        ViewGroup mCurrentView = (ViewGroup) obtainViewFromPool(R.layout.menu_sub);
        this.context = getActivity().getApplicationContext();
        this.mSpecialScreenView = (SpecialScreenView) mCurrentView.findViewById(R.id.SubMenuView);
        this.mViewArea = (SpecialScreenArea) mCurrentView.findViewById(R.id.FocusSubMenu);
        this.mSaveBtn = (Button) mCurrentView.findViewById(R.id.saveBtn);
        this.mFooterGuide = (FooterGuide) mCurrentView.findViewById(R.id.footer_guide);
        this.mToolNameView = (TextView) mCurrentView.findViewById(R.id.menu_item_name);
        this.mShadeArea = (ImageView) mCurrentView.findViewById(R.id.specialscreen_shade_area);
        this.mService = new BaseMenuService(this.context);
        this.mAdapter = new SpecialScreenMenuLayout.SpecialBaseMenuAdapter(this.context, R.layout.menu_sub_adapter, this.mService);
        return mCurrentView;
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout
    protected ArrayList<String> getMenuItemList() {
        ArrayList<String> items = this.mService.getMenuItemList();
        return items;
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        Log.d(this.TAG, "==== onResume:optImageCount = " + ImageEditor.optImageCount);
        this.mAdapter = new SpecialScreenMenuLayout.SpecialBaseMenuAdapter(this.context, R.layout.menu_sub_adapter, this.mService);
        ImageEditor.setOnSaveStatusChanged(this);
        this.mToolKitLayoutArrayList = new ArrayList<>();
        this.mToolKitLayoutArrayList.add(getLayout(Constant.ID_FRAMINGLAYOUT));
        this.mToolKitLayoutArrayList.add(getLayout(Constant.ID_HORIZONTALADJUSTMENTLAYOUT));
        this.mToolKitLayoutArrayList.add(getLayout(Constant.ID_BRIGHTNESSCONTROL));
        this.mToolKitLayoutArrayList.add(getLayout(Constant.ID_CONTRASTCONTROL));
        this.mToolKitLayoutArrayList.add(getLayout(Constant.ID_SATURATIONCONTROL));
        this.mToolKitLayoutArrayList.add(getLayout(Constant.ID_SOFTSKINLAYOUT));
        this.mToolKitLayoutArrayList.add(getLayout(Constant.ID_RESIZELAYOUT));
        this.mToolKitLayoutArrayList.add(getLayout(Constant.ID_MANUALFRAMING));
        this.mSaveBtn.setOnClickListener(this);
        this.mList = this.mService.getSupportedItemList();
        Log.d(this.TAG, "mList = " + this.mList + " size = " + this.mList.size());
        setLastToolSelected();
        updateText();
        if (this.mViewArea != null) {
            Log.e("", "special area");
            this.mViewArea.update();
        }
        this.mSaveBtn.setOnTouchListener(new View.OnTouchListener() { // from class: com.sony.imaging.app.photoretouch.menu.layout.PhotoRetouchSubMenuLayout.1
            @Override // android.view.View.OnTouchListener
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });
        this.mSaveBtn.setFocusable(false);
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        ImageEditor.setOnSaveStatusChanged(null);
        Log.d("YES", "====sub menu closing");
        int i = 0;
        while (true) {
            if (i >= this.mToolKitLayoutArrayList.size()) {
                break;
            }
            if (this.mToolKitLayoutArrayList.get(i).getView() == null) {
                i++;
            } else {
                Log.d("YES", "====closing previous kit");
                this.mToolKitLayoutArrayList.get(i).closeLayout();
                break;
            }
        }
        closeOpenedLayout();
        super.onPause();
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout
    protected String getCurrentValueItemId() {
        return mItemId;
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedCenterKey() {
        Log.d(this.TAG, "CHECKING =====pushedCenterKey....photo retouch submenu");
        setGuideInvisible();
        ImageEditor.updateSpaceAvailableAfterSaving();
        if (!ImageEditor.isSpaceAvailableInMemoryCard()) {
            String message = getResources().getString(R.string.STRID_AMC_STR_01956);
            Bundle bundle = new Bundle();
            bundle.putString(Constant.ALERT_MESSAGE, message);
            openLayout(Constant.ID_MESSAGEALERT, bundle);
            return 1;
        }
        selectToolKit(this.mSpecialScreenView.getSelectedItemPosition());
        closeOpenedLayout();
        if (this.mSaveBtn.isFocused()) {
            this.mSaveBtn.setFocusable(false);
            return 1;
        }
        return 1;
    }

    private void setGuideInvisible() {
    }

    @Override // com.sony.imaging.app.photoretouch.common.ImageEditor.SaveCallback
    public void onSuccess() {
        getLayout(Constant.ID_SAVINGLAYOUT).closeLayout();
        if (this.mSaveBtn.isFocused()) {
            this.mSaveBtn.setFocusable(false);
        }
        ImageEditor.refreshIndexView();
    }

    @Override // com.sony.imaging.app.photoretouch.common.ImageEditor.SaveCallback
    public void onFail() {
        getLayout(Constant.ID_SAVINGLAYOUT).closeLayout();
        String message = getResources().getString(R.string.STRID_CAU_FILE_MAX_CHANGE_CARD);
        Bundle bundle = new Bundle();
        bundle.putString(Constant.ALERT_MESSAGE, message);
        bundle.putBoolean(Constant.ALERT_SWITCHTOINDEXVIEW, true);
        openLayout(Constant.ID_MESSAGEALERT, bundle);
    }

    @Override // com.sony.imaging.app.photoretouch.common.ImageEditor.Callback
    public void onSaveStatusChanged() {
        Log.d(this.TAG, "CHECKING INSIDE onSaveStatusChanged");
        if (ImageEditor.isSaved()) {
            this.mSaveBtn.setTextColor(getResources().getColor(R.color.RESID_FONTSTYLE_MENU_ITEM_DISABLE));
            this.mSaveBtn.clearFocus();
            this.mSpecialScreenView.setEnabled(true);
            this.mSpecialScreenView.setFocusable(true);
            this.mSpecialScreenView.requestFocus();
            this.mShadeArea.setVisibility(8);
            this.mFooterGuide.setData(new FooterGuideDataResId(this.context, android.R.string.zen_mode_feature_name, android.R.string.httpErrorFailedSslHandshake));
            return;
        }
        this.mSaveBtn.setTextColor(getResources().getColor(R.color.RESID_FONTSTYLE_CMN_WHITE_NO_SKIN));
    }

    @Override // com.sony.imaging.app.base.menu.layout.SubMenuView.OnItemScrollListener
    public void onScrollStarted() {
    }

    @Override // com.sony.imaging.app.base.menu.layout.SubMenuView.OnItemScrollListener
    public void onScrollFinished() {
    }

    @Override // android.widget.AdapterView.OnItemClickListener
    public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
        Log.d(this.TAG, "=====onItemClick..pos= " + position);
        Log.d(this.TAG, "=====onItemClick..mPos= " + this.mSpecialScreenView.getSelectedItemPosition());
        BeepUtility.getInstance().playBeep(BeepUtilityRsrcTable.BEEP_ID_SELECT);
        pushedCenterKey();
    }

    @Override // android.widget.AdapterView.OnItemSelectedListener
    public void onNothingSelected(AdapterView<?> arg0) {
    }

    public void selectToolKit(int position) {
        setWorkingToolKitPosition(position);
        setItemId(position);
        switch (position) {
            case 0:
                openCurrentAndClosePreviousKitLayout(Constant.ID_SOFTSKINLAYOUT);
                return;
            case 1:
                openCurrentAndClosePreviousKitLayout(Constant.ID_RESIZELAYOUT);
                return;
            case 2:
                openCurrentAndClosePreviousKitLayout(Constant.ID_FRAMINGLAYOUT);
                return;
            case 3:
                openCurrentAndClosePreviousKitLayout(Constant.ID_HORIZONTALADJUSTMENTLAYOUT);
                return;
            case 4:
                openCurrentAndClosePreviousKitLayout(Constant.ID_BRIGHTNESSCONTROL);
                return;
            case 5:
                openCurrentAndClosePreviousKitLayout(Constant.ID_CONTRASTCONTROL);
                return;
            case 6:
                openCurrentAndClosePreviousKitLayout(Constant.ID_SATURATIONCONTROL);
                return;
            default:
                Log.d(this.TAG, "no kit selected");
                return;
        }
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.fw.Layout
    public void onReopened() {
        PhotoRetouchBrowserSingleLayout.showImage(true);
        super.onReopened();
        this.mSaveBtn.setFocusable(false);
    }

    private void openCurrentAndClosePreviousKitLayout(String ID_CURRENTKIT) {
        Log.d(this.TAG, "===Current kit id= " + ID_CURRENTKIT);
        PhotoRetouchBrowserSingleLayout.showImage(false);
        int i = 0;
        while (true) {
            if (i >= this.mToolKitLayoutArrayList.size()) {
                break;
            }
            if (this.mToolKitLayoutArrayList.get(i).getView() == null) {
                i++;
            } else {
                Log.d("YES", "====closing previous kit");
                this.mToolKitLayoutArrayList.get(i).closeLayout();
                break;
            }
        }
        openLayout(ID_CURRENTKIT, this.data);
    }

    private void closeOpenedLayout() {
        if (getLayout(Constant.ID_MESSAGEALERT).getView() != null) {
            getLayout(Constant.ID_MESSAGEALERT).closeLayout();
        } else if (getLayout(Constant.ID_MESSAGENOFACE).getView() != null) {
            getLayout(Constant.ID_MESSAGENOFACE).closeLayout();
        } else if (getLayout(Constant.ID_SAVINGLAYOUT).getView() != null) {
            getLayout(Constant.ID_SAVINGLAYOUT).closeLayout();
        } else if (getLayout(Constant.ID_MANUALSTARTUPMESSAGE).getView() != null) {
            getLayout(Constant.ID_MANUALSTARTUPMESSAGE).closeLayout();
        }
        if (getLayout(Constant.ID_CONFIRMSAVINGLAYOUT).getView() != null) {
            getLayout(Constant.ID_CONFIRMSAVINGLAYOUT).closeLayout();
        }
    }

    private void goBackFromToolSelection() {
        Log.e("", "INSIDE goBackFromToolSelection");
        if (ImageEditor.isSaved()) {
            Log.e("", "CASE BACK");
            getHandler().sendEmptyMessage(Constant.TRANSITION_INDEXPB);
        } else {
            Log.e("", "CASE CNF LYT");
            openLayout(Constant.ID_CONFIRMSAVINGLAYOUT);
        }
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMenuKey() {
        Log.e("", "INSIDE pushedMenuKey");
        goBackFromToolSelection();
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedSK1Key() {
        Log.e("", "INSIDE pushedSK1Key");
        goBackFromToolSelection();
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedUpKey() {
        if (this.mSaveBtn.isFocused()) {
            return -1;
        }
        return super.pushedUpKey();
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedDownKey() {
        if (this.mSaveBtn.isFocused()) {
            return -1;
        }
        return super.pushedDownKey();
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedS1Key() {
        Log.e("", "pushedS1Key");
        return super.pushedS1Key();
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedSettingFuncCustomKey(IKeyFunction keyFunction) {
        Log.e("", "pushedSettingFuncCustomKey");
        return -1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedAELHoldCustomKey() {
        Log.e("", "pushedAELHoldCustomKey");
        return -1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedAELToggleCustomKey() {
        Log.e("", "pushedAELToggleCustomKey");
        return -1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedAfMfHoldCustomKey() {
        Log.e("", "pushedAfMfHoldCustomKey");
        return -1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedAfMfToggleCustomKey() {
        Log.e("", "pushedAfMfToggleCustomKey");
        return -1;
    }

    private void setLastToolSelected() {
        int position = this.mService.getMenuItemList().indexOf(mItemId);
        int mBlock = getMenuItemList().size();
        if (mBlock > 5) {
            mBlock = 5;
        }
        int firstPosition = (position - mBlock) + 1;
        if (firstPosition < 0) {
            firstPosition = 0;
        }
        this.mSpecialScreenView.setSelection(firstPosition, position);
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Failed to find 'out' block for switch in B:2:0x0048. Please report as an issue. */
    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyDown(int keyCode, KeyEvent event) {
        Log.d("YES", "onKeyDown....code=  " + event.getKeyCode() + "\t ....scan code= " + event.getScanCode());
        int code = event.getScanCode();
        Log.d(this.TAG, "Tool Selection key code = " + code);
        switch (code) {
            case AppRoot.USER_KEYCODE.LEFT /* 105 */:
            case AppRoot.USER_KEYCODE.RIGHT /* 106 */:
            case AppRoot.USER_KEYCODE.DIAL2_RIGHT /* 528 */:
            case AppRoot.USER_KEYCODE.DIAL2_LEFT /* 529 */:
                if (this.mSpecialScreenView.isFocused()) {
                    bringFocusToSaveBtn();
                } else if (this.mSaveBtn.isFocused()) {
                    bringFocusToToolList();
                }
                return 1;
            case AppRoot.USER_KEYCODE.PLAYBACK /* 207 */:
            case AppRoot.USER_KEYCODE.FN /* 520 */:
            case AppRoot.USER_KEYCODE.LENS_ATTACH /* 530 */:
            case AppRoot.USER_KEYCODE.AEL /* 532 */:
            case AppRoot.USER_KEYCODE.AF_MF /* 533 */:
            case AppRoot.USER_KEYCODE.MODE_DIAL_CHANGED /* 572 */:
            case AppRoot.USER_KEYCODE.CUSTOM /* 588 */:
            case AppRoot.USER_KEYCODE.AF_RANGE /* 614 */:
            case AppRoot.USER_KEYCODE.EV_DIAL_CHANGED /* 620 */:
            case AppRoot.USER_KEYCODE.CUSTOM1 /* 622 */:
            case AppRoot.USER_KEYCODE.CUSTOM2 /* 623 */:
            case AppRoot.USER_KEYCODE.AF_MF_AEL /* 638 */:
            case AppRoot.USER_KEYCODE.LENS_DETACH /* 786 */:
                return -1;
            case AppRoot.USER_KEYCODE.SK1 /* 229 */:
            case AppRoot.USER_KEYCODE.MENU /* 514 */:
                Log.e("", "INSIDE MENU onKeyDown");
                Log.d("", "tool selection onkeyDown");
                return super.onKeyDown(keyCode, event);
            case AppRoot.USER_KEYCODE.CENTER /* 232 */:
                if (isRepeat(event)) {
                    return -1;
                }
                Log.d("", "tool selection onkeyDown");
                return super.onKeyDown(keyCode, event);
            case AppRoot.USER_KEYCODE.SK2 /* 513 */:
            case AppRoot.USER_KEYCODE.S1_ON /* 516 */:
            case AppRoot.USER_KEYCODE.S2_ON /* 518 */:
            case AppRoot.USER_KEYCODE.DELETE /* 595 */:
            case AppRoot.USER_KEYCODE.S1_OFF /* 772 */:
            case AppRoot.USER_KEYCODE.S2_OFF /* 774 */:
            default:
                Log.d("", "tool selection onkeyDown");
                return super.onKeyDown(keyCode, event);
            case AppRoot.USER_KEYCODE.MOVIE_REC /* 515 */:
            case AppRoot.USER_KEYCODE.UM_MOVIE_REC /* 616 */:
            case AppRoot.USER_KEYCODE.MOVIE_REC_2ND /* 637 */:
            case AppRoot.USER_KEYCODE.IR_MOVIE_REC /* 643 */:
                showMovieRecCaution();
                return 1;
        }
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyUp(int keyCode, KeyEvent event) {
        int keyCode2 = event.getScanCode();
        switch (keyCode2) {
            case AppRoot.USER_KEYCODE.AEL /* 532 */:
            case AppRoot.USER_KEYCODE.AF_MF /* 533 */:
            case AppRoot.USER_KEYCODE.CUSTOM /* 588 */:
            case AppRoot.USER_KEYCODE.AF_RANGE /* 614 */:
            case AppRoot.USER_KEYCODE.CUSTOM1 /* 622 */:
            case AppRoot.USER_KEYCODE.CUSTOM2 /* 623 */:
            case AppRoot.USER_KEYCODE.AF_MF_AEL /* 638 */:
                Log.d(this.TAG, "UP");
                return -1;
            default:
                return 0;
        }
    }

    public static boolean isRepeat(KeyEvent event) {
        return event.getRepeatCount() != 0;
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public void onDestroyView() {
        Gpelibrary.changeFrameBufferPixel(Gpelibrary.GS_FRAMEBUFFER_TYPE.RGBA4444);
        Log.d(this.TAG, "===onDestroyView");
        deinitializeAllValues();
        Log.d(this.TAG, "==== onDestroyView:optImageCount = " + ImageEditor.optImageCount);
        super.onDestroyView();
    }

    public static void resetItemId() {
        mItemId = "framing";
    }

    public void setItemId(int position) {
        ArrayList<String> itemId = getMenuItemList();
        mItemId = itemId.get(position);
    }

    public void closeApplication() {
    }

    protected void updateText() {
        Log.d(this.TAG, "INSIDE onItemSelected");
        int pos = this.mSpecialScreenView.getSelectedItemPosition();
        String itemId = this.mList.get(pos);
        if (this.mToolNameView != null) {
            this.mToolNameView.setText(this.mService.getMenuItemText(itemId));
        }
    }

    private void bringFocusToToolList() {
        this.mSpecialScreenView.setEnabled(true);
        this.mShadeArea.setVisibility(8);
        this.mSpecialScreenView.setFocusable(true);
        this.mSpecialScreenView.requestFocus();
        this.mFooterGuide.setData(new FooterGuideDataResId(this.context, android.R.string.zen_mode_feature_name, android.R.string.httpErrorFailedSslHandshake));
    }

    private void bringFocusToSaveBtn() {
        this.mSpecialScreenView.setEnabled(false);
        this.mShadeArea.setVisibility(0);
        this.mSaveBtn.setFocusableInTouchMode(true);
        this.mSaveBtn.setFocusable(true);
        this.mSaveBtn.requestFocus();
        this.mFooterGuide.setData(new FooterGuideDataResId(this.context, android.R.string.zen_mode_feature_name, android.R.string.httpErrorRedirectLoop));
    }

    private void deinitializeAllValues() {
        this.mToolKitLayoutArrayList = null;
        this.mAdapter = null;
        this.mService = null;
        this.mWorkingToolKitPosition = -1;
        this.mContentsManager = null;
        this.mRect = null;
        this.mSaveBtn = null;
        this.mFooterGuide = null;
        this.mToolNameView = null;
        this.mShadeArea = null;
        this.mViewArea = null;
        this.mSpecialScreenView = null;
    }

    public void deAllocateImageView(ImageView v) {
        Drawable toRecycle;
        if (v != null && (toRecycle = v.getDrawable()) != null) {
            ((BitmapDrawable) toRecycle).getBitmap().recycle();
        }
        System.gc();
    }

    public void deAllocateImageView(EachButton v) {
        Drawable toRecycle;
        if (v != null && (toRecycle = v.getBackground()) != null) {
            ((BitmapDrawable) toRecycle).getBitmap().recycle();
        }
        System.gc();
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout
    protected boolean isAELCancel() {
        return false;
    }

    @Override // com.sony.imaging.app.base.menu.layout.SpecialScreenMenuLayout, com.sony.imaging.app.base.menu.layout.SpecialScreenView.OnItemSelectedListener
    public void onItemSelected(SpecialScreenView parent, View view, int position, long id) {
        Log.d(this.TAG, "INSIDE onItemSelected");
        updateText();
        if (this.mViewArea != null) {
            Log.e("", "special area");
            this.mViewArea.update();
        }
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.saveBtn /* 2131361968 */:
                if (!ImageEditor.isSaved()) {
                    openLayout(Constant.ID_SAVINGLAYOUT);
                    ImageEditor.saveEditedImage(ImageEditor.getOrientationInfo(), this);
                    return;
                } else {
                    showInvalidFuncCaution();
                    return;
                }
            default:
                return;
        }
    }

    @Override // com.sony.imaging.app.base.menu.layout.SubMenuView.OnItemUpdatedListener
    public void onItemUpdated(SubMenuView parent, View view, int position, long id) {
    }

    @Override // android.widget.AdapterView.OnItemSelectedListener
    public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
    }

    protected void showInvalidFuncCaution() {
        IkeyDispatchEach mKey = new IkeyDispatchEach() { // from class: com.sony.imaging.app.photoretouch.menu.layout.PhotoRetouchSubMenuLayout.3
            @Override // com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IKeyHandler
            public int onKeyDown(int keyCode, KeyEvent event) {
                switch (event.getScanCode()) {
                    case AppRoot.USER_KEYCODE.CENTER /* 232 */:
                        CautionUtilityClass.getInstance().executeTerminate();
                        return 1;
                    default:
                        return 1;
                }
            }
        };
        CautionUtilityClass.getInstance().requestTrigger(Constant.CAUTION_ID_DLAPP_FUNCTION_DISABLE);
        CautionUtilityClass.getInstance().setDispatchKeyEvent(Constant.CAUTION_ID_DLAPP_FUNCTION_DISABLE, mKey);
    }

    private void showMovieRecCaution() {
        IkeyDispatchEach mKey = new IkeyDispatchEach() { // from class: com.sony.imaging.app.photoretouch.menu.layout.PhotoRetouchSubMenuLayout.4
            @Override // com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IKeyHandler
            public int onKeyDown(int keyCode, KeyEvent event) {
                return -1;
            }
        };
        CautionUtilityClass.getInstance().requestTrigger(131077);
        CautionUtilityClass.getInstance().setDispatchKeyEvent(131077, mKey);
    }
}
