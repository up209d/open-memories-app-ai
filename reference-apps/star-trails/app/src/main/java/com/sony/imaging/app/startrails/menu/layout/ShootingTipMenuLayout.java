package com.sony.imaging.app.startrails.menu.layout;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import com.sony.imaging.app.base.common.widget.FooterGuide;
import com.sony.imaging.app.base.common.widget.FooterGuideDataResId;
import com.sony.imaging.app.base.menu.layout.BaseMenuLayout;
import com.sony.imaging.app.startrails.R;
import com.sony.imaging.app.startrails.util.AppLog;

/* loaded from: classes.dex */
public class ShootingTipMenuLayout extends BaseMenuLayout {
    private static final String TAG = "ShootingTipMenuLayout";
    private static int pageindicator = 1;
    private View mCurrentView = null;
    private FooterGuide mFooterGuide = null;
    private ImageView mPageNoOne = null;
    private ImageView mPageNoTwo = null;
    private ImageView mPageNoThree = null;
    private TextView mMenuScreenTitle = null;
    private TextView mDescriptionText = null;
    private ImageView mImageOne = null;
    private ImageView mImageTwo = null;
    private ScrollView mScollView = null;

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        AppLog.enter(TAG, AppLog.getMethodName());
        this.mCurrentView = (ViewGroup) super.onCreateView(inflater, container, savedInstanceState);
        this.mCurrentView = (ViewGroup) obtainViewFromPool(R.layout.st_layout_shooting_tip);
        initializeView();
        AppLog.exit(TAG, AppLog.getMethodName());
        return this.mCurrentView;
    }

    private void initializeView() {
        AppLog.enter(TAG, AppLog.getMethodName());
        this.mPageNoOne = (ImageView) this.mCurrentView.findViewById(R.id.page_no_one);
        this.mPageNoTwo = (ImageView) this.mCurrentView.findViewById(R.id.page_no_two);
        this.mPageNoThree = (ImageView) this.mCurrentView.findViewById(R.id.page_no_three);
        this.mMenuScreenTitle = (TextView) this.mCurrentView.findViewById(R.id.menu_screen_title);
        this.mDescriptionText = (TextView) this.mCurrentView.findViewById(R.id.descriptiontext);
        this.mImageOne = (ImageView) this.mCurrentView.findViewById(R.id.image_one);
        this.mImageTwo = (ImageView) this.mCurrentView.findViewById(R.id.image_two);
        this.mFooterGuide = (FooterGuide) this.mCurrentView.findViewById(R.id.footer_guide_shooting_tip);
        this.mScollView = (ScrollView) this.mCurrentView.findViewById(R.id.scrollView);
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        AppLog.enter(TAG, AppLog.getMethodName());
        if (this.mScollView != null) {
            this.mScollView.scrollTo(0, 0);
        }
        this.mScollView.setOnTouchListener(new View.OnTouchListener() { // from class: com.sony.imaging.app.startrails.menu.layout.ShootingTipMenuLayout.1
            @Override // android.view.View.OnTouchListener
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        updateResources();
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    private void updateResources() {
        AppLog.enter(TAG, AppLog.getMethodName());
        switch (pageindicator) {
            case 1:
                this.mMenuScreenTitle.setText(getResources().getString(R.string.STRID_FUNC_STRS_SHOOTING_TIPS_PAGE1_HEADER));
                this.mPageNoOne.setImageResource(R.drawable.p_16_dd_parts_strl_shooting_tips_position_circle_selected_icon);
                this.mPageNoThree.setImageResource(R.drawable.p_16_dd_parts_strl_shooting_tips_position_circle_normal_icon);
                this.mPageNoTwo.setImageResource(R.drawable.p_16_dd_parts_strl_shooting_tips_position_circle_normal_icon);
                this.mDescriptionText.setText(getResources().getString(R.string.STRID_FUNC_STRS_SHOOTING_TIPS_PAGE1_CONTENTS));
                this.mImageOne.setImageResource(R.drawable.p_16_dd_parts_strl_shooting_tips_contents1_1);
                this.mImageTwo.setImageResource(R.drawable.p_16_dd_parts_strl_shooting_tips_contents1_1);
                this.mImageTwo.setVisibility(8);
                this.mScollView.smoothScrollTo(0, 0);
                this.mFooterGuide.setData(new FooterGuideDataResId(getActivity(), R.string.STRID_FUNC_RICH_HELP_GUIDE_FOOTER_CONTENT_GUIDE, R.string.STRID_FUNC_RICH_HELP_GUIDE_FOOTER_CONTENT_GUIDE_SK));
                break;
            case 2:
                this.mMenuScreenTitle.setText(getResources().getString(R.string.STRID_FUNC_STRS_SHOOTING_TIPS_PAGE2_HEADER));
                this.mPageNoOne.setImageResource(R.drawable.p_16_dd_parts_strl_shooting_tips_position_circle_normal_icon);
                this.mPageNoThree.setImageResource(R.drawable.p_16_dd_parts_strl_shooting_tips_position_circle_normal_icon);
                this.mPageNoTwo.setImageResource(R.drawable.p_16_dd_parts_strl_shooting_tips_position_circle_selected_icon);
                this.mDescriptionText.setText(getResources().getString(R.string.STRID_FUNC_STRS_SHOOTING_TIPS_PAGE2_CONTENTS));
                this.mImageOne.setImageResource(R.drawable.p_16_dd_parts_strl_shooting_tips_contents2_1);
                this.mImageTwo.setImageResource(R.drawable.p_16_dd_parts_strl_shooting_tips_contents2_2);
                this.mFooterGuide.setData(new FooterGuideDataResId(getActivity(), R.string.STRID_FUNC_RICH_HELP_GUIDE_FOOTER_CONTENT_GUIDE, R.string.STRID_FUNC_RICH_HELP_GUIDE_FOOTER_CONTENT_GUIDE_SK));
                this.mImageTwo.setVisibility(0);
                this.mScollView.smoothScrollTo(0, 0);
                break;
            case 3:
                this.mMenuScreenTitle.setText(getResources().getString(R.string.STRID_FUNC_STRS_SHOOTING_TIPS_PAGE3_HEADER));
                this.mPageNoOne.setImageResource(R.drawable.p_16_dd_parts_strl_shooting_tips_position_circle_normal_icon);
                this.mPageNoThree.setImageResource(R.drawable.p_16_dd_parts_strl_shooting_tips_position_circle_selected_icon);
                this.mPageNoTwo.setImageResource(R.drawable.p_16_dd_parts_strl_shooting_tips_position_circle_normal_icon);
                this.mDescriptionText.setText(getResources().getString(R.string.STRID_FUNC_STRS_SHOOTING_TIPS_PAGE3_CONTENTS));
                this.mImageOne.setImageResource(R.drawable.p_16_dd_parts_strl_shooting_tips_contents3_1);
                this.mImageTwo.setImageResource(R.drawable.p_16_dd_parts_strl_shooting_tips_contents3_2);
                this.mFooterGuide.setData(new FooterGuideDataResId(getActivity(), R.string.STRID_FUNC_RICH_HELP_GUIDE_FOOTER_CONTENT_GUIDE, R.string.STRID_FUNC_RICH_HELP_GUIDE_FOOTER_CONTENT_GUIDE_SK));
                this.mImageTwo.setVisibility(0);
                this.mScollView.smoothScrollTo(0, 0);
                break;
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedDownKey() {
        AppLog.enter(TAG, AppLog.getMethodName());
        this.mScollView.smoothScrollBy(0, 20);
        AppLog.exit(TAG, AppLog.getMethodName());
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedUpKey() {
        AppLog.enter(TAG, AppLog.getMethodName());
        this.mScollView.smoothScrollBy(0, -20);
        AppLog.exit(TAG, AppLog.getMethodName());
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedRightKey() {
        AppLog.enter(TAG, AppLog.getMethodName());
        pageindicator++;
        checkpagebounds();
        updateResources();
        AppLog.exit(TAG, AppLog.getMethodName());
        return super.pushedRightKey();
    }

    private void checkpagebounds() {
        AppLog.enter(TAG, AppLog.getMethodName());
        if (pageindicator > 3) {
            pageindicator = 1;
        } else if (pageindicator < 1) {
            pageindicator = 3;
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedLeftKey() {
        AppLog.enter(TAG, AppLog.getMethodName());
        pageindicator--;
        checkpagebounds();
        updateResources();
        AppLog.exit(TAG, AppLog.getMethodName());
        return super.pushedLeftKey();
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        super.onPause();
        releaseResources();
    }

    private void releaseResources() {
        releaseImageViewDrawable(this.mPageNoOne);
        releaseImageViewDrawable(this.mPageNoTwo);
        releaseImageViewDrawable(this.mPageNoThree);
        releaseImageViewDrawable(this.mImageOne);
        releaseImageViewDrawable(this.mImageTwo);
        AppLog.enter(TAG, AppLog.getMethodName());
        this.mFooterGuide = null;
        if (this.mDescriptionText != null) {
            this.mDescriptionText.setText("");
        }
        this.mDescriptionText = null;
        this.mScollView = null;
        if (this.mCurrentView != null) {
            this.mCurrentView.destroyDrawingCache();
            this.mCurrentView = null;
        }
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout
    public void closeLayout() {
        releaseResources();
        super.closeLayout();
    }

    private void releaseImageViewDrawable(ImageView imageView) {
        if (imageView != null) {
            imageView.setImageResource(0);
            if (imageView.getDrawable() != null) {
                imageView.getDrawable().setCallback(null);
            }
            imageView.setImageDrawable(null);
        }
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedCenterKey() {
        openPreviousMenu();
        return super.pushedCenterKey();
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMenuKey() {
        openPreviousMenu();
        return -1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedMainDialNext() {
        pushedDownKey();
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedMainDialPrev() {
        pushedUpKey();
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedSubDialNext() {
        return pushedRightKey();
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedSubDialPrev() {
        return pushedLeftKey();
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedDial3ToRight() {
        pushedDownKey();
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedDial3ToLeft() {
        pushedLeftKey();
        return 1;
    }
}
