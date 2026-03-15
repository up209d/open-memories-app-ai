package com.sony.imaging.app.base.menu.layout;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;

/* loaded from: classes.dex */
public class SpecialScreenView extends AdapterView<Adapter> {
    private static final int BLOCK_SPACE = 4;
    private static final int DEFAULT_BLOCK = 5;
    private static final int DEFAULT_CHILDVIEW_HEIGHT = 66;
    private static final int DEFAULT_CHILDVIEW_WIDTH = 112;
    private static final int INVALID_POSITION = -1;
    private static final int ITEM_COUNT_2 = 2;
    private static final int ITEM_COUNT_3 = 3;
    private static final int ITEM_COUNT_4 = 4;
    private static final int ITEM_COUNT_5 = 5;
    private static final int OFFSET_ABS_Y_LIST2 = 174;
    private static final int OFFSET_ABS_Y_LIST3 = 141;
    private static final int OFFSET_ABS_Y_LIST4 = 108;
    private static final int OFFSET_ABS_Y_LIST5 = 75;
    private static final int OFFSET_Y_LIST2 = 99;
    private static final int OFFSET_Y_LIST3 = 66;
    private static final int OFFSET_Y_LIST4 = 33;
    private static final int OFFSET_Y_LIST5 = 0;
    private static final int OFFSET_Y_VIEWS = 75;
    private boolean isInitLayout;
    private Adapter mAdapter;
    private int mBlock;
    private int mChildViewHeight;
    private int mChildViewWidth;
    private int mFirstPositionAD;
    private int mItemCount;
    private ViewGroup.LayoutParams mLayoutParams;
    private int mOldSelectedPositionAD;
    private OnItemSelectedListener mOnItemSelectedListener;

    @Deprecated
    private OnItemUpdatedListener mOnItemUpdatedListener;
    private Adapter mOptionAdapter;
    private int mSelectedOptionPositionAD;
    private int mSelectedPositionAD;
    private int mVerticalSpacing;

    /* loaded from: classes.dex */
    public interface OnItemSelectedListener {
        void onItemSelected(SpecialScreenView specialScreenView, View view, int i, long j);

        void onNothingSelected(SpecialScreenView specialScreenView);

        void onOptionItemSelected(SpecialScreenView specialScreenView, View view, int i, long j);
    }

    @Deprecated
    /* loaded from: classes.dex */
    public interface OnItemUpdatedListener {
        void onItemUpdated(SpecialScreenView specialScreenView, View view, int i, long j);
    }

    public SpecialScreenView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mChildViewWidth = DEFAULT_CHILDVIEW_WIDTH;
        this.mChildViewHeight = 66;
        this.mVerticalSpacing = 0;
        this.mBlock = 5;
        this.mSelectedPositionAD = -1;
        this.mSelectedOptionPositionAD = -1;
        this.mFirstPositionAD = -1;
        this.mOldSelectedPositionAD = -1;
        this.mItemCount = 0;
        this.isInitLayout = true;
        this.mLayoutParams = null;
        this.mAdapter = null;
        this.mOptionAdapter = null;
    }

    @Override // android.view.ViewGroup
    protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new ViewGroup.LayoutParams(this.mChildViewWidth, this.mChildViewHeight);
    }

    public void setOnSpecialScreenItemSelectedListener(OnItemSelectedListener listener) {
        this.mOnItemSelectedListener = listener;
    }

    public void setOnItemUpdatedListener(OnItemUpdatedListener listener) {
        this.mOnItemUpdatedListener = listener;
    }

    @Override // android.view.View
    public void setFocusable(boolean focusable) {
        Adapter adapter = getAdapter();
        boolean empty = adapter == null || adapter.getCount() == 0;
        super.setFocusable(focusable && !empty);
    }

    @Override // android.widget.AdapterView, android.view.View
    public void setFocusableInTouchMode(boolean focusable) {
        Adapter adapter = getAdapter();
        boolean empty = adapter == null || adapter.getCount() == 0;
        super.setFocusableInTouchMode(focusable && !empty);
    }

    @Override // android.widget.AdapterView
    public Adapter getAdapter() {
        return this.mAdapter;
    }

    public Adapter getOptionAdapter() {
        return this.mOptionAdapter;
    }

    @Override // android.widget.AdapterView
    public View getSelectedView() {
        return getChildAt(this.mSelectedPositionAD);
    }

    @Override // android.widget.AdapterView
    public int getSelectedItemPosition() {
        return this.mSelectedPositionAD;
    }

    public String getSelectedOptionItem() {
        Adapter adapter = getOptionAdapter();
        int selection = getSelectedOptionPosition();
        if (adapter == null || adapter.getCount() <= 0 || selection < 0) {
            return null;
        }
        return (String) adapter.getItem(selection);
    }

    public String getOptionItemAtPosition(int position) {
        Adapter adapter = getOptionAdapter();
        return (String) ((adapter == null || position < 0) ? null : adapter.getItem(position));
    }

    public int getSelectedOptionPosition() {
        return this.mSelectedOptionPositionAD;
    }

    public int getFirstPosition() {
        return this.mFirstPositionAD;
    }

    public int getBlockCount() {
        return this.mBlock;
    }

    public int getItemCount() {
        return this.mItemCount;
    }

    public int getOptionItemCount() {
        if (this.mOptionAdapter != null) {
            return this.mOptionAdapter.getCount();
        }
        return 0;
    }

    @Override // android.widget.AdapterView
    public void setAdapter(Adapter adapter) {
        this.mAdapter = adapter;
        this.isInitLayout = true;
        if (this.mAdapter != null) {
            this.mSelectedPositionAD = 0;
            this.mOldSelectedPositionAD = 0;
            this.mItemCount = this.mAdapter.getCount();
        } else {
            checkSelectionChanged();
            resetList();
        }
        requestLayout();
    }

    public void setOptionAdapter(Adapter adapter, int selectedOptionPosition) {
        this.mOptionAdapter = adapter;
        this.mSelectedOptionPositionAD = selectedOptionPosition;
    }

    @Override // android.widget.AdapterView
    public void setSelection(int selectedPosition) {
        this.mSelectedPositionAD = selectedPosition;
        this.mOldSelectedPositionAD = selectedPosition;
        this.isInitLayout = true;
        requestLayout();
    }

    @Override // android.view.View, android.view.ViewParent
    public void requestLayout() {
        this.isInitLayout = true;
        super.requestLayout();
    }

    public void setSelection(int firstPosition, int selectedPosition) {
        this.mFirstPositionAD = firstPosition;
        this.mSelectedPositionAD = selectedPosition;
        this.mOldSelectedPositionAD = selectedPosition;
        this.isInitLayout = true;
        requestLayout();
    }

    @Override // android.widget.AdapterView, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (this.mAdapter != null && this.isInitLayout) {
            initLayout();
            this.isInitLayout = false;
        }
    }

    private void resetList() {
        removeAllViewsInLayout();
        invalidate();
    }

    private void setUpChildViews(int firstPositionAD) {
        int height;
        View child;
        Log.d("HOGE", "mItemCount =" + this.mItemCount);
        switch (this.mItemCount) {
            case 2:
                height = OFFSET_Y_LIST2;
                this.mBlock = 2;
                break;
            case 3:
                height = 66;
                this.mBlock = 3;
                break;
            case 4:
                height = 33;
                this.mBlock = 4;
                break;
            default:
                height = 0;
                this.mBlock = 5;
                break;
        }
        if (this.mLayoutParams == null) {
            this.mLayoutParams = generateDefaultLayoutParams();
        }
        this.mFirstPositionAD = firstPositionAD;
        Log.d("HOGE", "mFirstPositionAD =" + firstPositionAD);
        for (int i = 0; i < this.mItemCount; i++) {
            if (this.mOptionAdapter != null && this.mOptionAdapter.getCount() > 0 && this.mSelectedPositionAD == i) {
                child = this.mOptionAdapter.getView(this.mSelectedOptionPositionAD, null, this);
            } else {
                child = this.mAdapter.getView(i, null, this);
            }
            child.setLayoutParams(this.mLayoutParams);
            addViewInLayout(child, -1, this.mLayoutParams, true);
            child.measure(this.mChildViewWidth, this.mChildViewHeight);
            child.layout(0, height, child.getMeasuredWidth(), this.mChildViewHeight + height);
            height += this.mChildViewHeight + this.mVerticalSpacing;
        }
        offsetChildrenTopAndBottom(this.mFirstPositionAD * (-1) * (this.mChildViewHeight + this.mVerticalSpacing));
    }

    private void offsetChildrenTopAndBottom(int offset) {
        for (int i = getChildCount() - 1; i >= 0; i--) {
            getChildAt(i).offsetTopAndBottom(offset);
        }
    }

    private void initLayout() {
        if (this.mItemCount == 0) {
            resetList();
            return;
        }
        detachAllViewsFromParent();
        setUpChildViews(this.mFirstPositionAD);
        setSelected(this.mSelectedPositionAD);
        invalidate();
    }

    private void checkSelectionChanged() {
        if (this.mSelectedPositionAD != this.mOldSelectedPositionAD || this.isInitLayout) {
            selectionChanged();
            this.mOldSelectedPositionAD = this.mSelectedPositionAD;
        }
    }

    private void selectionChanged() {
        if (this.mOnItemSelectedListener != null) {
            if (this.mSelectedPositionAD >= 0) {
                this.mOnItemSelectedListener.onItemSelected(this, getSelectedView(), this.mSelectedPositionAD, getAdapter().getItemId(this.mSelectedPositionAD));
            } else {
                this.mOnItemSelectedListener.onNothingSelected(this);
            }
        }
    }

    private void selectionOptionChanged() {
        if (this.mOnItemSelectedListener != null) {
            if (this.mSelectedOptionPositionAD >= 0 && this.mOptionAdapter != null) {
                this.mOnItemSelectedListener.onOptionItemSelected(this, getSelectedView(), this.mSelectedOptionPositionAD, this.mOptionAdapter.getItemId(this.mSelectedOptionPositionAD));
            } else {
                this.mOnItemSelectedListener.onNothingSelected(this);
            }
        }
    }

    private void detachAllAndSetupViews(boolean isNext) {
        detachAllViewsFromParent();
        setUpChildViews(0);
        if (isNext) {
            this.mFirstPositionAD = 0;
            this.mSelectedPositionAD = 0;
        } else {
            offsetChildrenTopAndBottom((this.mItemCount - this.mBlock) * (-1) * (this.mChildViewHeight + this.mVerticalSpacing));
            this.mFirstPositionAD = this.mItemCount - this.mBlock;
            this.mSelectedPositionAD = this.mItemCount - 1;
        }
    }

    private void setSelected(int position) {
        if (getChildAt(position) != null) {
            getChildAt(position).setSelected(true);
        }
    }

    private void refreshSelected() {
        for (int i = 0; i < this.mItemCount; i++) {
            getChildAt(i).setSelected(false);
        }
    }

    private void offsetChildViews(boolean isNext, int times) {
        if (isNext) {
            this.mFirstPositionAD += times;
            this.mSelectedPositionAD = (this.mFirstPositionAD + this.mBlock) - 1;
            offsetChildrenTopAndBottom(times * (-1) * (this.mChildViewHeight + this.mVerticalSpacing));
        } else {
            int i = this.mFirstPositionAD - times;
            this.mFirstPositionAD = i;
            this.mSelectedPositionAD = i;
            offsetChildrenTopAndBottom(times * 1 * (this.mChildViewHeight + this.mVerticalSpacing));
        }
    }

    public boolean moveRight() {
        if (this.mSelectedOptionPositionAD == getOptionItemCount() - 1) {
            this.mSelectedOptionPositionAD = 0;
        } else {
            this.mSelectedOptionPositionAD++;
        }
        selectionOptionChanged();
        return true;
    }

    public boolean moveLeft() {
        if (this.mSelectedOptionPositionAD == 0) {
            this.mSelectedOptionPositionAD = getOptionItemCount() - 1;
        } else {
            this.mSelectedOptionPositionAD--;
        }
        selectionOptionChanged();
        return true;
    }

    public boolean moveNext() {
        if (this.mItemCount > this.mBlock) {
            if (this.mSelectedPositionAD == this.mItemCount - 1) {
                detachAllAndSetupViews(true);
            } else if (this.mSelectedPositionAD - this.mFirstPositionAD > this.mBlock - 2) {
                offsetChildViews(true, 1);
            } else {
                this.mSelectedPositionAD++;
            }
        } else if (this.mSelectedPositionAD == this.mItemCount - 1) {
            this.mSelectedPositionAD = 0;
        } else {
            this.mSelectedPositionAD++;
        }
        refreshSelected();
        setSelected(this.mSelectedPositionAD);
        invalidate();
        checkSelectionChanged();
        return true;
    }

    public boolean movePrevious() {
        if (this.mItemCount > this.mBlock) {
            if (this.mSelectedPositionAD == 0) {
                detachAllAndSetupViews(false);
            } else if (this.mSelectedPositionAD - this.mFirstPositionAD < this.mBlock - 4) {
                offsetChildViews(false, 1);
            } else {
                this.mSelectedPositionAD--;
            }
        } else if (this.mSelectedPositionAD == 0) {
            this.mSelectedPositionAD = this.mItemCount - 1;
        } else {
            this.mSelectedPositionAD--;
        }
        refreshSelected();
        setSelected(this.mSelectedPositionAD);
        invalidate();
        checkSelectionChanged();
        return true;
    }

    public boolean moveTo(int position) {
        if (this.mItemCount <= this.mBlock) {
            this.mSelectedPositionAD = position;
        } else if (position < this.mFirstPositionAD) {
            int diff = this.mFirstPositionAD - position;
            offsetChildViews(false, diff);
        } else if (position - this.mFirstPositionAD < this.mBlock) {
            this.mSelectedPositionAD = position;
        } else {
            int diff2 = ((position - this.mFirstPositionAD) - this.mBlock) + 1;
            offsetChildViews(true, diff2);
        }
        refreshSelected();
        setSelected(this.mSelectedPositionAD);
        invalidate();
        checkSelectionChanged();
        return true;
    }

    @Override // android.widget.AdapterView
    public int getCount() {
        Adapter adapter = getAdapter();
        if (adapter == null) {
            return 0;
        }
        return adapter.getCount();
    }
}
