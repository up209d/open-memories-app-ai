package com.sony.imaging.app.base.menu.layout;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
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
import com.sony.imaging.app.base.common.widget.FooterGuide;
import com.sony.imaging.app.base.common.widget.IFooterGuideData;
import com.sony.imaging.app.base.menu.BaseMenuService;
import com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.base.shooting.widget.CompensationWheelAndInfoView;
import com.sony.imaging.app.base.shooting.widget.CompensationWheelView;
import com.sony.imaging.app.fw.ICustomKey;
import com.sony.imaging.app.util.Environment;
import java.util.ArrayList;

/* loaded from: classes.dex */
public abstract class CompensationMenuLayout extends DisplayMenuItemsMenuLayout implements View.OnTouchListener, IModableLayout {
    protected static final int COMP_MENU_KIND_EV = 1;
    protected static final int COMP_MENU_KIND_FLASH = 2;
    protected static final int COMP_MENU_KIND_FLASH_EVF = 3;
    protected static final int COMP_MENU_KIND_UNKNOWN = 0;
    private static final int OFFSET_ELEMENT_AND_INDEX = 1;
    private static final String TAG = "CompensationMenuLayout";
    private ImageView lArrow;
    private CompensationWheelView mSlider;
    private CompensationWheelAndInfoView mWheelAndInfo;
    private ImageView rArrow;
    private View mCurrentView = null;
    private Handler mHandler = new Handler();
    private int mCancelLevel = 0;
    private int mLevel = 0;
    protected OnLayoutModeChangeListener mDispModeListener = null;
    private ArrayList<String> mParentItems = new ArrayList<>();

    protected abstract View createCurrentView(LayoutInflater layoutInflater);

    protected abstract int getCompensationLevel();

    protected abstract int getGuideType();

    protected abstract String getInformationText(int i);

    protected abstract int getMenuKind();

    protected abstract int getWheelRange();

    protected abstract void setCompensationLevel(int i);

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        this.mCurrentView = createCurrentView(inflater);
        Context context = getActivity().getApplicationContext();
        this.mService = new BaseMenuService(context);
        this.mWheelAndInfo = (CompensationWheelAndInfoView) this.mCurrentView.findViewById(R.id.CompensationWheelAndInfoView);
        this.mWheelAndInfo.initialize(getMenuKind(), getMovieMode(), getGuideType(), getWheelRange());
        this.mSlider = this.mWheelAndInfo.getCompensationWheelView();
        this.mSlider.setOnTouchListener(this);
        this.mDispModeListener = new OnLayoutModeChangeListener(this, 0);
        DisplayModeObserver.getInstance().setNotificationListener(this.mDispModeListener);
        return this.mCurrentView;
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public void onDestroyView() {
        this.mCurrentView = null;
        this.mWheelAndInfo = null;
        this.mSlider = null;
        this.rArrow = null;
        this.lArrow = null;
        DisplayModeObserver.getInstance().removeNotificationListener(this.mDispModeListener);
        this.mDispModeListener = null;
        super.onDestroyView();
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout
    public void closeLayout() {
        if (this.mUpdater != null) {
            this.mUpdater.finishMenuUpdater();
        }
        super.closeLayout();
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        this.mLevel = getCompensationLevel();
        this.mCancelLevel = getCompensationLevel();
        this.mSlider.setPosition(this.mLevel);
        this.rArrow = (ImageView) this.mCurrentView.findViewById(R.id.right_arrow);
        this.lArrow = (ImageView) this.mCurrentView.findViewById(R.id.left_arrow);
        if (this.mLevel >= getWheelRange() - 1) {
            this.rArrow.setVisibility(4);
            this.lArrow.setVisibility(0);
        } else if (this.mLevel == 0) {
            this.rArrow.setVisibility(0);
            this.lArrow.setVisibility(4);
        } else {
            this.rArrow.setVisibility(0);
            this.lArrow.setVisibility(0);
        }
        this.mWheelAndInfo.setInformationText(getInformationText(this.mLevel));
        String itemId = this.mService.getMenuItemId();
        getTitleTextView().setText(this.mService.getMenuItemText(itemId));
    }

    protected TextView getTitleTextView() {
        return (TextView) this.mCurrentView.findViewById(R.id.menu_screen_title);
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout
    protected DisplayMenuItemsMenuLayout.MenuUpdater getMenuUpdater() {
        return new DisplayMenuItemsMenuLayout.MenuUpdater(this.mHandler) { // from class: com.sony.imaging.app.base.menu.layout.CompensationMenuLayout.1
            @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout.MenuUpdater, java.lang.Runnable
            public void run() {
                if (!this.isDone) {
                    this.isDone = true;
                    CompensationMenuLayout.this.setCompensationLevel(CompensationMenuLayout.this.mLevel);
                }
            }
        };
    }

    private void updateInformation(int level) {
        this.mWheelAndInfo.setInformationText(getInformationText(level));
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout
    protected void setFunctionGuideResources(ArrayList<Object> guideResources) {
        String itemId = this.mService.getMenuItemId();
        guideResources.add(this.mService.getMenuItemText(itemId));
        guideResources.add(this.mService.getMenuItemGuideText(itemId));
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver
    protected String getKeyConvCategory() {
        return ICustomKey.CATEGORY_MENU;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMenuKey() {
        this.mUpdater.cancelMenuUpdater();
        setCompensationLevel(this.mCancelLevel);
        openPreviousMenu();
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedCenterKey() {
        closeMenuLayout(null);
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedRightKey() {
        changeValue(this.mSlider.moveUp());
        changeArror();
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedLeftKey() {
        changeValue(this.mSlider.moveDown());
        changeArror();
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedMainDialNext() {
        return pushedRightKey();
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedMainDialPrev() {
        return pushedLeftKey();
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedSubDialNext() {
        return pushedRightKey();
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedSubDialPrev() {
        return pushedLeftKey();
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedFuncRingNext() {
        return pushedLeftKey();
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedFuncRingPrev() {
        return pushedRightKey();
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedDownKey() {
        return -1;
    }

    @Override // android.view.View.OnTouchListener
    public boolean onTouch(View v, MotionEvent event) {
        return true;
    }

    private void changeValue(int value) {
        this.mLevel = value;
        this.mUpdater.restartMenuUpdater();
        updateInformation(value);
    }

    private void changeArror() {
        if (this.mLevel + 2 > getWheelRange()) {
            this.rArrow.setVisibility(4);
            this.lArrow.setVisibility(0);
        } else if (this.mLevel == 0) {
            this.rArrow.setVisibility(0);
            this.lArrow.setVisibility(4);
        } else {
            this.rArrow.setVisibility(0);
            this.lArrow.setVisibility(0);
        }
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout
    protected boolean isParentItemAvailable() {
        if (this.mParentItems == null || this.mService == null) {
            return true;
        }
        this.mParentItems.clear();
        this.mParentItems.add(this.mService.getMenuItemId());
        this.mService.updateSettingItemsAvailable(this.mParentItems);
        return this.mService.isMenuItemValid(this.mService.getMenuItemId());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean getMovieMode() {
        return Environment.isMovieAPISupported() && 2 == ExecutorCreator.getInstance().getRecordingMode();
    }

    public void setFooterGuideData(IFooterGuideData data) {
        FooterGuide guide;
        if (this.mCurrentView != null && (guide = (FooterGuide) this.mCurrentView.findViewById(getFooterGuideResource())) != null) {
            guide.setData(data);
        }
    }

    protected int getFooterGuideResource() {
        return 0;
    }

    @Override // com.sony.imaging.app.base.common.IModableLayout
    public void onLayoutModeChanged(int device, int displayMode) {
        refresh();
    }

    protected void refresh() {
        if (this.mWheelAndInfo != null) {
            this.mWheelAndInfo.initialize(getMenuKind(), getMovieMode(), getGuideType(), getWheelRange());
        }
    }
}
