package com.sony.imaging.app.base.menu.layout;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.sony.imaging.app.base.R;
import com.sony.imaging.app.base.common.DisplayModeObserver;
import com.sony.imaging.app.base.common.IModableLayout;
import com.sony.imaging.app.base.common.OnLayoutModeChangeListener;
import com.sony.imaging.app.base.menu.BaseMenuService;
import com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.base.shooting.widget.CompensationWheelAndInfoView;
import com.sony.imaging.app.base.shooting.widget.CompensationWheelView;
import com.sony.imaging.app.util.Environment;
import java.util.ArrayList;

/* loaded from: classes.dex */
public abstract class Fn15LayerCompensationLayout extends Fn15LayerAbsLayout implements IModableLayout {
    protected static final int COMP_MENU_KIND_EV = 1;
    protected static final int COMP_MENU_KIND_FLASH = 2;
    protected static final int COMP_MENU_KIND_FLASH_EVF = 3;
    protected static final int COMP_MENU_KIND_UNKNOWN = 0;
    private static final int OFFSET_ELEMENT_AND_INDEX = 1;
    private ImageView lArrow;
    protected DisplayMenuItemsMenuLayout.MenuUpdater mLevelSetter;
    private CompensationWheelView mSlider;
    private CompensationWheelAndInfoView mWheelAndInfo;
    private ImageView rArrow;
    private View mCurrentView = null;
    private int mLevel = 0;
    private int mCancelLevel = 0;
    protected OnLayoutModeChangeListener mDispModeListener = null;

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

    @Override // com.sony.imaging.app.base.menu.layout.Fn15LayerAbsLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        getMenuUpdater();
        int action = this.data.getInt(Fn15LayerAbsLayout.BUNDLE_KEY_ACTION);
        if (action == 3 || action == 4) {
            openPreviousMenu();
            return;
        }
        this.mLevel = getCompensationLevel();
        this.mCancelLevel = this.mLevel;
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
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean getMovieMode() {
        return Environment.isMovieAPISupported() && 2 == ExecutorCreator.getInstance().getRecordingMode();
    }

    protected void cancelValue() {
        this.mUpdater.cancelMenuUpdater();
        setCompensationLevel(this.mCancelLevel);
    }

    @Override // com.sony.imaging.app.base.menu.layout.Fn15LayerAbsLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMenuKey() {
        cancelValue();
        return super.pushedMenuKey();
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedMainDialNext() {
        valueUp();
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedMainDialPrev() {
        valueDown();
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedSubDialNext() {
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedSubDialPrev() {
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedThirdDialNext() {
        valueUp();
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedThirdDialPrev() {
        valueDown();
        return 1;
    }

    private void valueUp() {
        changeValue(this.mSlider.moveUp());
        if (this.mLevel >= getWheelRange() - 1) {
            this.rArrow.setVisibility(4);
            this.lArrow.setVisibility(0);
        } else {
            this.rArrow.setVisibility(0);
            this.lArrow.setVisibility(0);
        }
    }

    private void valueDown() {
        changeValue(this.mSlider.moveDown());
        if (this.mLevel == 0) {
            this.rArrow.setVisibility(0);
            this.lArrow.setVisibility(4);
        } else {
            this.rArrow.setVisibility(0);
            this.lArrow.setVisibility(0);
        }
    }

    private void changeValue(int value) {
        this.mLevel = value;
        this.mLevelSetter.restartMenuUpdater();
        updateInformation(value);
    }

    @Override // com.sony.imaging.app.base.menu.layout.Fn15LayerAbsLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        this.mLevelSetter.cancelMenuUpdater();
        this.mLevelSetter = null;
        super.onPause();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout
    public DisplayMenuItemsMenuLayout.MenuUpdater getMenuUpdater() {
        if (this.mLevelSetter == null) {
            this.mLevelSetter = new DisplayMenuItemsMenuLayout.MenuUpdater(new Handler()) { // from class: com.sony.imaging.app.base.menu.layout.Fn15LayerCompensationLayout.1
                @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout.MenuUpdater, java.lang.Runnable
                public void run() {
                    Fn15LayerCompensationLayout.this.setCompensationLevel(Fn15LayerCompensationLayout.this.mLevel);
                }
            };
            this.mLevelSetter.setDelayTime(200L);
        }
        return this.mLevelSetter;
    }

    private void updateInformation(int level) {
        this.mWheelAndInfo.setInformationText(getInformationText(level));
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

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout
    protected void setFunctionGuideResources(ArrayList<Object> guideResources) {
        String itemId = this.mService.getMenuItemId();
        guideResources.add(this.mService.getMenuItemText(itemId));
        guideResources.add(this.mService.getMenuItemGuideText(itemId));
        guideResources.add(Boolean.valueOf(this.mService.isMenuItemValid(itemId)));
    }
}
