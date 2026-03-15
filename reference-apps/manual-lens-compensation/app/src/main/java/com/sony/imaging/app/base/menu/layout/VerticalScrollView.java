package com.sony.imaging.app.base.menu.layout;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.animation.Transformation;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Scroller;
import com.sony.imaging.app.util.BeepUtility;
import com.sony.imaging.app.util.BeepUtilityRsrcTable;
import java.util.LinkedList;

/* loaded from: classes.dex */
public class VerticalScrollView extends AdapterView<Adapter> implements GestureDetector.OnGestureListener {
    private static final float ALPHA_1 = 0.8f;
    private static final float ALPHA_2 = 0.6f;
    private static final int DEFAULT_ANIMATION_DURATION = 100;
    private static final int DEFAULT_CHILDVIEW_HEIGHT = 72;
    private static final int DEFAULT_CHILDVIEW_WIDTH = 95;
    private static final int DEFAULT_HORIZONTAL_SPACING = 1;
    private static final int GALLERY_PADDING_BOTTOM = 100;
    private static final int GALLERY_PADDING_TOP = 100;
    private static final int INVALID_POSITION = -1;
    private static final int ITEM_COUNT_3 = 3;
    private static final int ITEM_COUNT_4 = 4;
    private static int mAnimationDuration = 100;
    private final String TAG;
    boolean isFling;
    boolean isInitLayout;
    boolean isLongPress;
    boolean isMoveDown;
    boolean isMoveUp;
    boolean isScroll;
    boolean isSingleTapUp;
    private Adapter mAdapter;
    private boolean mBlockLayoutRequests;
    private int mChildViewHeight;
    private int mChildViewWidth;
    private boolean mDesiredFocusableInTouchModeState;
    private boolean mDesiredFocusableState;
    private int mDistanceViews;
    private int mDownPositionAD;
    private int mEndPositionAD;
    private int mFirstPositionAD;
    private FlingRunnable mFlingRunnable;
    protected int mGalleryPaddingBottom;
    protected int mGalleryPaddingTop;
    private GestureDetector mGestureDetector;
    private int mGravity;
    private int mHeightMeasureSpec;
    private int mHorizontalSpacing;
    protected boolean mIsTwoItemsMode;
    protected int mItemCount;
    private ViewGroup.LayoutParams mLayoutParams;
    protected int mNewSelectedChildIndex;
    int mOldSelectedPositionAD;
    private AdapterView.OnItemClickListener mOnItemClickListener;
    private OnItemScrollListener mOnItemScrollListener;
    private AdapterView.OnItemSelectedListener mOnItemSelectedListener;
    protected LinkedList<PositionADList> mPositionADList;
    private int mSelectedPositionAD;
    private View mSelectedView;
    private int mSelectionBottomPadding;
    private int mSelectionLeftPadding;
    private int mSelectionRightPadding;
    private int mSelectionTopPadding;
    private Rect mSpinnerPadding;
    private int mWidthMeasureSpec;

    /* loaded from: classes.dex */
    public interface OnItemClickListener {
        void onItemClick(VerticalScrollView verticalScrollView, View view, int i, long j);
    }

    /* loaded from: classes.dex */
    public interface OnItemScrollListener {
        void onScrollFinished();

        void onScrollStarted();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes.dex */
    public class PositionADList {
        private int next;
        private int prev;

        public PositionADList(int pos0, int pos1, int pos2) {
            this.prev = pos1;
            this.next = pos2;
        }

        public int getNext() {
            return this.next;
        }

        public int getPrev() {
            return this.prev;
        }
    }

    protected void initMenuItemViewList() {
        if (this.mItemCount == 1) {
            this.mIsTwoItemsMode = false;
            this.mGalleryPaddingTop = 200;
            this.mGalleryPaddingBottom = 200;
            this.mNewSelectedChildIndex = 0;
        } else if (this.mItemCount == 2) {
            this.mIsTwoItemsMode = true;
            this.mItemCount = 3;
            this.mGalleryPaddingTop = 100;
            this.mGalleryPaddingBottom = 100;
            this.mNewSelectedChildIndex = 1;
        } else if (this.mItemCount > 4) {
            this.mIsTwoItemsMode = false;
            this.mGalleryPaddingTop = getPaddingTop();
            this.mGalleryPaddingBottom = getPaddingBottom();
            this.mNewSelectedChildIndex = 2;
        } else {
            this.mIsTwoItemsMode = false;
            this.mGalleryPaddingTop = 100;
            this.mGalleryPaddingBottom = 100;
            this.mNewSelectedChildIndex = 1;
        }
        this.mPositionADList = new LinkedList<>();
        int i = 0;
        while (i < this.mItemCount) {
            int prev = i == 0 ? this.mItemCount - 1 : i - 1;
            int next = i == this.mItemCount + (-1) ? 0 : i + 1;
            PositionADList item = new PositionADList(i, prev, next);
            this.mPositionADList.add(item);
            i++;
        }
    }

    public VerticalScrollView(Context context) {
        super(context);
        this.TAG = "VerticalScrollView";
        this.mSelectedPositionAD = -1;
        this.mFirstPositionAD = -1;
        this.mEndPositionAD = -1;
        this.mDownPositionAD = -1;
        this.mIsTwoItemsMode = false;
        this.mGravity = 5;
        this.mBlockLayoutRequests = false;
        this.mFlingRunnable = null;
        this.mSelectionLeftPadding = 0;
        this.mSelectionTopPadding = 0;
        this.mSelectionRightPadding = 0;
        this.mSelectionBottomPadding = 0;
        this.mChildViewWidth = 95;
        this.mChildViewHeight = DEFAULT_CHILDVIEW_HEIGHT;
        this.mHorizontalSpacing = 1;
        this.mSpinnerPadding = new Rect();
        this.mGalleryPaddingTop = getPaddingTop();
        this.mGalleryPaddingBottom = getPaddingBottom();
        this.mGestureDetector = null;
        this.mDistanceViews = this.mChildViewHeight + this.mHorizontalSpacing;
        this.isInitLayout = true;
        this.isScroll = false;
        this.isFling = false;
        this.isLongPress = false;
        this.isSingleTapUp = false;
        this.isMoveUp = false;
        this.isMoveDown = false;
        this.mOldSelectedPositionAD = -1;
        init(context);
    }

    public VerticalScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.TAG = "VerticalScrollView";
        this.mSelectedPositionAD = -1;
        this.mFirstPositionAD = -1;
        this.mEndPositionAD = -1;
        this.mDownPositionAD = -1;
        this.mIsTwoItemsMode = false;
        this.mGravity = 5;
        this.mBlockLayoutRequests = false;
        this.mFlingRunnable = null;
        this.mSelectionLeftPadding = 0;
        this.mSelectionTopPadding = 0;
        this.mSelectionRightPadding = 0;
        this.mSelectionBottomPadding = 0;
        this.mChildViewWidth = 95;
        this.mChildViewHeight = DEFAULT_CHILDVIEW_HEIGHT;
        this.mHorizontalSpacing = 1;
        this.mSpinnerPadding = new Rect();
        this.mGalleryPaddingTop = getPaddingTop();
        this.mGalleryPaddingBottom = getPaddingBottom();
        this.mGestureDetector = null;
        this.mDistanceViews = this.mChildViewHeight + this.mHorizontalSpacing;
        this.isInitLayout = true;
        this.isScroll = false;
        this.isFling = false;
        this.isLongPress = false;
        this.isSingleTapUp = false;
        this.isMoveUp = false;
        this.isMoveDown = false;
        this.mOldSelectedPositionAD = -1;
        init(context);
    }

    public VerticalScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.TAG = "VerticalScrollView";
        this.mSelectedPositionAD = -1;
        this.mFirstPositionAD = -1;
        this.mEndPositionAD = -1;
        this.mDownPositionAD = -1;
        this.mIsTwoItemsMode = false;
        this.mGravity = 5;
        this.mBlockLayoutRequests = false;
        this.mFlingRunnable = null;
        this.mSelectionLeftPadding = 0;
        this.mSelectionTopPadding = 0;
        this.mSelectionRightPadding = 0;
        this.mSelectionBottomPadding = 0;
        this.mChildViewWidth = 95;
        this.mChildViewHeight = DEFAULT_CHILDVIEW_HEIGHT;
        this.mHorizontalSpacing = 1;
        this.mSpinnerPadding = new Rect();
        this.mGalleryPaddingTop = getPaddingTop();
        this.mGalleryPaddingBottom = getPaddingBottom();
        this.mGestureDetector = null;
        this.mDistanceViews = this.mChildViewHeight + this.mHorizontalSpacing;
        this.isInitLayout = true;
        this.isScroll = false;
        this.isFling = false;
        this.isLongPress = false;
        this.isSingleTapUp = false;
        this.isMoveUp = false;
        this.isMoveDown = false;
        this.mOldSelectedPositionAD = -1;
        init(context);
    }

    private void init(Context context) {
        setWillNotDraw(false);
        setAnimationDuration(mAnimationDuration);
        setGravity(5);
        if (this.mFlingRunnable == null) {
            this.mFlingRunnable = new FlingRunnable();
        }
        if (this.mGestureDetector == null) {
            this.mGestureDetector = new GestureDetector(context, this);
            this.mGestureDetector.setIsLongpressEnabled(isLongPressEnabled());
        }
        setStaticTransformationsEnabled(true);
    }

    public void setAnimationDuration(int animationDurationMillis) {
        mAnimationDuration = animationDurationMillis;
    }

    public void setGravity(int gravity) {
        if (this.mGravity != gravity) {
            this.mGravity = gravity;
        }
    }

    @Override // android.view.ViewGroup
    protected boolean getChildStaticTransformation(View child, Transformation t) {
        t.clear();
        int distance = Math.abs(indexOfChild(child) - this.mNewSelectedChildIndex);
        if (1 <= distance) {
            t.setAlpha(1 == distance ? ALPHA_1 : ALPHA_2);
            t.setTransformationType(Transformation.TYPE_ALPHA);
            return true;
        }
        return false;
    }

    @Override // android.view.ViewGroup
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof ViewGroup.LayoutParams;
    }

    @Override // android.view.ViewGroup
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new ViewGroup.LayoutParams(p);
    }

    @Override // android.view.ViewGroup
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new ViewGroup.LayoutParams(getContext(), attrs);
    }

    @Override // android.view.ViewGroup
    protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new ViewGroup.LayoutParams(this.mChildViewWidth, this.mChildViewHeight);
    }

    @Override // android.widget.AdapterView, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (this.isInitLayout) {
            initLayout();
            this.isInitLayout = false;
        }
    }

    private int getCenterOfGallery() {
        return (((getHeight() - this.mGalleryPaddingTop) - this.mGalleryPaddingBottom) / 2) + this.mGalleryPaddingTop;
    }

    private static int getCenterOfView(View view) {
        return view.getTop() + (view.getHeight() / 2);
    }

    @Override // android.widget.AdapterView
    public void setOnItemClickListener(AdapterView.OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public boolean performItemClick() {
        if (this.mOnItemClickListener == null) {
            return false;
        }
        this.mOnItemClickListener.onItemClick(this, getSelectedView(), this.mSelectedPositionAD, this.mAdapter.getItemId(this.mSelectedPositionAD));
        return true;
    }

    @Override // android.widget.AdapterView
    public void setOnItemSelectedListener(AdapterView.OnItemSelectedListener listener) {
        this.mOnItemSelectedListener = listener;
    }

    public void setOnItemScrollListener(OnItemScrollListener listener) {
        this.mOnItemScrollListener = listener;
    }

    @Override // android.widget.AdapterView
    public void setSelection(int selectedPosition) {
        this.mSelectedPositionAD = selectedPosition;
        this.isInitLayout = true;
        requestLayout();
        invalidate();
    }

    @Override // android.view.View, android.view.ViewParent
    public void requestLayout() {
        if (!this.mBlockLayoutRequests) {
            super.requestLayout();
        }
    }

    @Override // android.view.View
    public void setFocusable(boolean focusable) {
        Adapter adapter = getAdapter();
        boolean empty = adapter == null || adapter.getCount() == 0;
        this.mDesiredFocusableState = focusable;
        if (!focusable) {
            this.mDesiredFocusableInTouchModeState = false;
        }
        super.setFocusable(focusable && !empty);
    }

    @Override // android.widget.AdapterView, android.view.View
    public void setFocusableInTouchMode(boolean focusable) {
        Adapter adapter = getAdapter();
        boolean empty = adapter == null || adapter.getCount() == 0;
        this.mDesiredFocusableInTouchModeState = focusable;
        if (focusable) {
            this.mDesiredFocusableState = true;
        }
        super.setFocusableInTouchMode(focusable && !empty);
    }

    void checkFocus() {
        Adapter adapter = getAdapter();
        boolean empty = adapter == null || adapter.getCount() == 0;
        boolean focusable = !empty;
        super.setFocusableInTouchMode(focusable && this.mDesiredFocusableInTouchModeState);
        super.setFocusable(focusable && this.mDesiredFocusableState);
    }

    @Override // android.widget.AdapterView
    public Adapter getAdapter() {
        return this.mAdapter;
    }

    @Override // android.widget.AdapterView
    public void setAdapter(Adapter adapter) {
        this.mAdapter = adapter;
        this.isInitLayout = true;
        if (this.mAdapter != null) {
            this.mItemCount = this.mAdapter.getCount();
            checkFocus();
            initMenuItemViewList();
        } else {
            checkSelectionChanged();
            checkFocus();
            resetList();
        }
        requestLayout();
    }

    private void resetList() {
        removeAllViewsInLayout();
        invalidate();
    }

    @Override // android.widget.AdapterView
    public View getSelectedView() {
        return getChildAt(this.mNewSelectedChildIndex);
    }

    private void checkSelectionChanged() {
        if (this.mSelectedPositionAD != this.mOldSelectedPositionAD || this.isInitLayout) {
            selectionChanged();
            this.mOldSelectedPositionAD = this.mSelectedPositionAD;
        }
    }

    private void selectionChanged() {
        if (this.mOnItemSelectedListener != null) {
            if (!this.mIsTwoItemsMode || this.mSelectedPositionAD != 2) {
                if (this.mSelectedPositionAD >= 0) {
                    this.mOnItemSelectedListener.onItemSelected(this, getSelectedView(), this.mSelectedPositionAD, getAdapter().getItemId(this.mSelectedPositionAD));
                } else {
                    this.mOnItemSelectedListener.onNothingSelected(this);
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void trackMotionScroll(int deltaY) {
        boolean toTop;
        if (getChildCount() != 0) {
            if (deltaY < 0) {
                toTop = true;
            } else {
                toTop = false;
            }
            offsetChildrenTopAndBottom(deltaY);
            detachOffScreenChildren(toTop);
            if (toTop) {
                fillToGalleryBottom();
            } else {
                fillToGalleryTop();
            }
            setSelectionToCenterChild();
            checkSelectionChanged();
            updateSelectedItemMetadata();
            invalidate();
        }
    }

    private void offsetChildrenTopAndBottom(int offset) {
        for (int i = getChildCount() - 1; i >= 0; i--) {
            getChildAt(i).offsetTopAndBottom(offset);
        }
    }

    private void detachOffScreenChildren(boolean toTop) {
        int numChildren = getChildCount();
        int start = 0;
        int count = 0;
        if (toTop) {
            int galleryTop = this.mGalleryPaddingTop;
            for (int i = 0; i < numChildren; i++) {
                View child = getChildAt(i);
                if (child.getBottom() > galleryTop) {
                    break;
                }
                if (i == this.mNewSelectedChildIndex) {
                    this.mOldSelectedPositionAD = -1;
                }
                count++;
            }
            for (int i2 = 0; i2 < count; i2++) {
                this.mFirstPositionAD = this.mPositionADList.get(this.mFirstPositionAD).getNext();
            }
            this.mNewSelectedChildIndex -= count;
        } else {
            int galleryBottom = getHeight() - this.mGalleryPaddingBottom;
            for (int i3 = numChildren - 1; i3 >= 0; i3--) {
                View child2 = getChildAt(i3);
                if (child2.getTop() < galleryBottom) {
                    break;
                }
                if (i3 == this.mNewSelectedChildIndex) {
                    this.mOldSelectedPositionAD = -1;
                }
                start = i3;
                count++;
            }
            for (int i4 = 0; i4 < count; i4++) {
                this.mEndPositionAD = this.mPositionADList.get(this.mEndPositionAD).getPrev();
            }
            this.mNewSelectedChildIndex += count;
        }
        detachViewsFromParent(start, count);
    }

    private void setSelectionToCenterChild() {
        int closestDistance = Integer.MAX_VALUE;
        for (int i = getChildCount() - 1; i >= 0; i--) {
            View child = getChildAt(i);
            int distanceY = getCenterOfView(child) - getCenterOfGallery();
            if (Math.abs(distanceY) < closestDistance) {
                closestDistance = Math.abs(distanceY);
                this.mNewSelectedChildIndex = i;
            }
        }
        this.mSelectedPositionAD = this.mFirstPositionAD;
        for (int i2 = 0; i2 < this.mNewSelectedChildIndex; i2++) {
            this.mSelectedPositionAD = this.mPositionADList.get(this.mSelectedPositionAD).getNext();
        }
    }

    private void initLayout() {
        if (this.mItemCount == 0) {
            resetList();
            return;
        }
        detachAllViewsFromParent();
        View sel = makeAndAddView(this.mSelectedPositionAD, 0, true);
        int childrenTop = this.mSpinnerPadding.top;
        int childrenHeight = ((getBottom() - getTop()) - this.mSpinnerPadding.top) - this.mSpinnerPadding.bottom;
        int selectedOffset = ((childrenHeight / 2) + childrenTop) - (sel.getHeight() / 2);
        sel.offsetTopAndBottom(selectedOffset);
        this.mEndPositionAD = this.mSelectedPositionAD;
        fillToGalleryBottom();
        this.mFirstPositionAD = this.mSelectedPositionAD;
        fillToGalleryTop();
        updateSelectedItemMetadata();
        invalidate();
    }

    private void fillToGalleryTop() {
        int galleryTop = this.mGalleryPaddingTop;
        View prevIterationView = getChildAt(0);
        int curItemPos = this.mEndPositionAD;
        int curBottomEdge = (getBottom() - getTop()) - this.mGalleryPaddingBottom;
        if (prevIterationView != null) {
            curItemPos = this.mPositionADList.get(this.mFirstPositionAD).getPrev();
            curBottomEdge = prevIterationView.getTop() - this.mHorizontalSpacing;
        }
        while (curBottomEdge > galleryTop) {
            View prevIterationView2 = makeAndAddView(curItemPos, curBottomEdge, false);
            this.mFirstPositionAD = curItemPos;
            curBottomEdge = prevIterationView2.getTop() - this.mHorizontalSpacing;
            curItemPos = this.mPositionADList.get(curItemPos).getPrev();
        }
    }

    private void fillToGalleryBottom() {
        int galleryBottom = (getBottom() - getTop()) - this.mGalleryPaddingBottom;
        View prevIterationView = getChildAt(getChildCount() - 1);
        int curItemPos = this.mFirstPositionAD;
        int curTopEdge = this.mGalleryPaddingTop;
        if (prevIterationView != null) {
            curItemPos = this.mPositionADList.get(this.mEndPositionAD).getNext();
            curTopEdge = prevIterationView.getBottom() + this.mHorizontalSpacing;
        }
        while (curTopEdge < galleryBottom) {
            View prevIterationView2 = makeAndAddView(curItemPos, curTopEdge, true);
            this.mEndPositionAD = curItemPos;
            curTopEdge = prevIterationView2.getBottom() + this.mHorizontalSpacing;
            curItemPos = this.mPositionADList.get(curItemPos).getNext();
        }
    }

    private View makeAndAddView(int position, int y, boolean toTop) {
        View child;
        if (this.mIsTwoItemsMode && position == 2) {
            child = new View(getContext());
        } else {
            child = this.mAdapter.getView(position, null, this);
        }
        if (this.mLayoutParams == null) {
            this.mLayoutParams = generateDefaultLayoutParams();
        }
        child.setLayoutParams(this.mLayoutParams);
        setUpChild(child, y, toTop);
        return child;
    }

    private void setUpChild(View child, int y, boolean toTop) {
        int childTop;
        int childBottom;
        if (this.mLayoutParams == null) {
            this.mLayoutParams = generateDefaultLayoutParams();
        }
        addViewInLayout(child, toTop ? -1 : 0, this.mLayoutParams);
        int childHeightSpec = ViewGroup.getChildMeasureSpec(this.mHeightMeasureSpec, this.mSpinnerPadding.top + this.mSpinnerPadding.bottom, this.mLayoutParams.height);
        int childWidthSpec = ViewGroup.getChildMeasureSpec(this.mWidthMeasureSpec, this.mSpinnerPadding.left + this.mSpinnerPadding.right, this.mLayoutParams.width);
        child.measure(childWidthSpec, childHeightSpec);
        int childLeft = calculateLeft(child, true);
        int childRight = childLeft + child.getMeasuredWidth();
        int height = child.getMeasuredHeight();
        if (toTop) {
            childTop = y;
            childBottom = childTop + height;
        } else {
            childTop = y - height;
            childBottom = y;
        }
        child.layout(childLeft, childTop, childRight, childBottom);
    }

    private int calculateLeft(View child, boolean duringLayout) {
        int myWidth = duringLayout ? getMeasuredWidth() : getWidth();
        int childWidth = duringLayout ? child.getMeasuredWidth() : child.getWidth();
        switch (this.mGravity) {
            case 1:
                int availableSpace = ((myWidth - this.mSpinnerPadding.right) - this.mSpinnerPadding.left) - childWidth;
                int childLeft = this.mSpinnerPadding.left + (availableSpace / 2);
                return childLeft;
            case 2:
            case 4:
            default:
                return 0;
            case 3:
                int childLeft2 = this.mSpinnerPadding.left;
                return childLeft2;
            case 5:
                int childLeft3 = (myWidth - this.mSpinnerPadding.right) - childWidth;
                return childLeft3;
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void dispatchSetPressed(boolean pressed) {
        if (this.mSelectedView != null) {
            this.mSelectedView.setPressed(pressed);
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    public boolean dispatchKeyEvent(KeyEvent event) {
        return false;
    }

    public boolean moveNext() {
        return moveNext(1);
    }

    public boolean moveNext(int amount) {
        if (this.mItemCount <= 1) {
            return false;
        }
        scrollToChild(this.mSelectedPositionAD, this.mDistanceViews * (-1) * amount, false);
        return true;
    }

    public boolean movePrevious() {
        return movePrevious(1);
    }

    public boolean movePrevious(int amount) {
        if (this.mItemCount <= 1) {
            return false;
        }
        scrollToChild(this.mSelectedPositionAD, this.mDistanceViews * amount, true);
        return true;
    }

    @Override // android.view.View, android.view.KeyEvent.Callback
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        moveNext();
        return false;
    }

    private boolean isEmptyCellSelected() {
        return this.mIsTwoItemsMode && this.mSelectedPositionAD == 2;
    }

    private boolean isNextEmptyView(boolean toTop) {
        return this.mIsTwoItemsMode && ((toTop && this.mPositionADList.get(this.mSelectedPositionAD).getPrev() == 2) || (!toTop && this.mPositionADList.get(this.mSelectedPositionAD).getNext() == 2));
    }

    private boolean scrollToChild(int nextSelectedPosition, int distance, boolean toTop) {
        if (isNextEmptyView(toTop)) {
            this.mFlingRunnable.startUsingDistance(distance * 2);
            return true;
        }
        this.mFlingRunnable.startUsingDistance(distance);
        return true;
    }

    private void updateSelectedItemMetadata() {
        View child = getSelectedView();
        this.mSelectedView = child;
        if (child != null) {
            child.setSelected(true);
            child.setFocusable(false);
        }
    }

    @Override // android.view.View
    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
        if (gainFocus && this.mSelectedView != null) {
            this.mSelectedView.requestFocus(direction);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void scrollIntoSlots() {
        if (getChildCount() != 0 && this.mSelectedView != null) {
            int scrollAmount = getCenterOfGallery() - getCenterOfView(this.mSelectedView);
            if (isEmptyCellSelected()) {
                scrollAmount += scrollAmount < 0 ? this.mDistanceViews : -this.mDistanceViews;
            }
            if (scrollAmount != 0) {
                this.mFlingRunnable.startUsingDistance(scrollAmount);
            } else {
                onFinishedMovement();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void stopIntoSlots() {
        if (getChildCount() != 0 && this.mSelectedView != null) {
            int scrollAmount = getCenterOfGallery() - getCenterOfView(this.mSelectedView);
            if (isEmptyCellSelected()) {
                scrollAmount += scrollAmount < 0 ? this.mDistanceViews : -this.mDistanceViews;
            }
            if (scrollAmount != 0) {
                trackMotionScroll(scrollAmount);
            }
            onFinishedMovement();
        }
    }

    private void onFinishedMovement() {
        invalidate();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class FlingRunnable implements Runnable {
        private int mDeltaAmount;
        private int mDistance;
        private int mLastFlingY;
        private Scroller mScroller;

        public FlingRunnable() {
            this.mScroller = new Scroller(VerticalScrollView.this.getContext(), new LinearInterpolator());
        }

        private void startCommon() {
            VerticalScrollView.this.removeCallbacks(this);
        }

        public void startUsingVelocity(int initialVelocity) {
            if (initialVelocity != 0) {
                startCommon();
                int initialY = initialVelocity < 0 ? Integer.MAX_VALUE : 0;
                this.mLastFlingY = initialY;
                this.mDistance = 0;
                this.mDeltaAmount = 0;
                this.mScroller.fling(0, initialY, 0, initialVelocity, 0, Integer.MAX_VALUE, 0, Integer.MAX_VALUE);
                VerticalScrollView.this.post(this);
            }
        }

        public void startUsingDistance(int distance) {
            if (distance != 0) {
                startCommon();
                this.mLastFlingY = 0;
                this.mDistance = distance;
                this.mDeltaAmount = 0;
                this.mScroller.startScroll(0, 0, 0, -this.mDistance, VerticalScrollView.mAnimationDuration);
                VerticalScrollView.this.post(this);
            }
        }

        public void stop(boolean scrollIntoSlots) {
            VerticalScrollView.this.removeCallbacks(this);
            endFling(scrollIntoSlots);
        }

        private void endFling(boolean scrollIntoSlots) {
            this.mScroller.forceFinished(true);
            VerticalScrollView.this.invalidate();
            if (scrollIntoSlots) {
                VerticalScrollView.this.scrollIntoSlots();
            } else {
                VerticalScrollView.this.stopIntoSlots();
            }
        }

        @Override // java.lang.Runnable
        public void run() {
            if (VerticalScrollView.this.mItemCount == 0) {
                endFling(true);
                return;
            }
            Scroller scroller = this.mScroller;
            boolean more = scroller.computeScrollOffset();
            int y = scroller.getCurrY();
            int delta = this.mLastFlingY - y;
            if (delta != 0) {
                VerticalScrollView.this.trackMotionScroll(delta);
                this.mDeltaAmount += delta;
            }
            if (!more) {
                endFling(true);
            } else if (this.mDistance == 0 || this.mDeltaAmount != this.mDistance) {
                this.mLastFlingY = y;
                VerticalScrollView.this.post(this);
            } else {
                endFling(true);
            }
        }
    }

    @Override // android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = View.MeasureSpec.getMode(widthMeasureSpec);
        this.mSpinnerPadding.left = getPaddingLeft() > this.mSelectionLeftPadding ? getPaddingLeft() : this.mSelectionLeftPadding;
        this.mSpinnerPadding.top = this.mGalleryPaddingTop > this.mSelectionTopPadding ? this.mGalleryPaddingTop : this.mSelectionTopPadding;
        this.mSpinnerPadding.right = getPaddingRight() > this.mSelectionRightPadding ? getPaddingRight() : this.mSelectionRightPadding;
        this.mSpinnerPadding.bottom = this.mGalleryPaddingBottom > this.mSelectionBottomPadding ? this.mGalleryPaddingBottom : this.mSelectionBottomPadding;
        int preferredHeight = 0;
        int preferredWidth = 0;
        boolean needsMeasuring = true;
        int selectedPosition = this.mSelectedPositionAD;
        if (selectedPosition >= 0 && this.mAdapter != null) {
            View view = getSelectedView();
            if (view == null && this.mItemCount != 0) {
                view = this.mAdapter.getView(selectedPosition, null, this);
            }
            if (view != null) {
                if (view.getLayoutParams() == null) {
                    this.mBlockLayoutRequests = true;
                    if (this.mLayoutParams == null) {
                        this.mLayoutParams = generateDefaultLayoutParams();
                    }
                    view.setLayoutParams(this.mLayoutParams);
                    this.mBlockLayoutRequests = false;
                }
                measureChild(view, widthMeasureSpec, heightMeasureSpec);
                preferredHeight = view.getMeasuredHeight() + this.mSpinnerPadding.top + this.mSpinnerPadding.bottom;
                preferredWidth = view.getMeasuredWidth() + this.mSpinnerPadding.left + this.mSpinnerPadding.right;
                needsMeasuring = false;
            }
        }
        if (needsMeasuring) {
            preferredHeight = this.mSpinnerPadding.top + this.mSpinnerPadding.bottom;
            if (widthMode == 0) {
                preferredWidth = this.mSpinnerPadding.left + this.mSpinnerPadding.right;
            }
        }
        int preferredHeight2 = Math.max(preferredHeight, getSuggestedMinimumHeight());
        int preferredWidth2 = Math.max(preferredWidth, getSuggestedMinimumWidth());
        int heightSize = resolveSize(preferredHeight2, heightMeasureSpec);
        int widthSize = resolveSize(preferredWidth2, widthMeasureSpec);
        setMeasuredDimension(widthSize, heightSize);
        this.mHeightMeasureSpec = heightMeasureSpec;
        this.mWidthMeasureSpec = widthMeasureSpec;
    }

    public boolean isLongPressEnabled() {
        return false;
    }

    @Override // android.view.GestureDetector.OnGestureListener
    public boolean onDown(MotionEvent e) {
        if (this.mPositionADList != null) {
            this.mFlingRunnable.stop(false);
            this.mDownPositionAD = TouchpositionToSelectedPosition((int) e.getX(), (int) e.getY());
            if (!this.mIsTwoItemsMode || this.mDownPositionAD != 2) {
                PositionADList pl = this.mPositionADList.get(this.mSelectedPositionAD);
                if (pl != null) {
                    if (this.mDownPositionAD == this.mPositionADList.get(this.mSelectedPositionAD).getNext()) {
                        this.isMoveDown = true;
                    } else if (this.mDownPositionAD == this.mPositionADList.get(this.mSelectedPositionAD).getPrev()) {
                        this.isMoveUp = true;
                    }
                }
            }
        }
        return false;
    }

    private int TouchpositionToSelectedPosition(int x, int y) {
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child.getTop() < y && y < child.getBottom() && child.getLeft() < x && x < child.getRight()) {
                int position = this.mFirstPositionAD;
                for (int j = 0; j < i; j++) {
                    PositionADList pl = this.mPositionADList.get(position);
                    if (pl == null) {
                        return -1;
                    }
                    position = pl.getNext();
                }
                return position;
            }
        }
        return -1;
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent event) {
        if (getSelectedView() != null) {
            this.mGestureDetector.onTouchEvent(event);
            switch (event.getAction()) {
                case 1:
                    if (this.isFling) {
                        if (this.isScroll) {
                            this.isScroll = false;
                            if (this.mOnItemScrollListener != null) {
                                this.mOnItemScrollListener.onScrollFinished();
                            }
                        }
                        this.isFling = false;
                        this.isMoveUp = false;
                        this.isMoveDown = false;
                    } else {
                        if (this.isScroll || this.isFling) {
                            int closestDistance = Integer.MAX_VALUE;
                            int closestdiff = 0;
                            for (int i = getChildCount() - 1; i >= 0; i--) {
                                View child = getChildAt(i);
                                int distanceY = getCenterOfView(child) - getCenterOfGallery();
                                if (Math.abs(distanceY) < closestDistance) {
                                    closestDistance = Math.abs(distanceY);
                                    closestdiff = distanceY;
                                }
                            }
                            if (isEmptyCellSelected()) {
                                closestdiff += closestdiff < 0 ? this.mDistanceViews : -this.mDistanceViews;
                            }
                            this.mFlingRunnable.startUsingDistance(closestdiff * (-1));
                            if (this.isScroll) {
                                this.isScroll = false;
                                if (this.mOnItemScrollListener != null) {
                                    this.mOnItemScrollListener.onScrollFinished();
                                }
                            }
                            if (this.isFling) {
                                this.isFling = false;
                            }
                            this.isMoveUp = false;
                            this.isMoveDown = false;
                        }
                        if (this.isSingleTapUp) {
                            this.mOnItemClickListener.onItemClick(this, getSelectedView(), this.mDownPositionAD, this.mAdapter.getItemId(this.mDownPositionAD));
                            this.isSingleTapUp = false;
                        } else if (this.isLongPress) {
                            this.mOnItemClickListener.onItemClick(this, getSelectedView(), this.mDownPositionAD, this.mAdapter.getItemId(this.mDownPositionAD));
                            this.isLongPress = false;
                        } else if (this.isMoveUp) {
                            if (this.mItemCount > 1) {
                                this.mFlingRunnable.startUsingDistance(this.mDistanceViews);
                                BeepUtility.getInstance().playBeep(BeepUtilityRsrcTable.BEEP_ID_MOVE_UP);
                            }
                            this.isMoveUp = false;
                        } else if (this.isMoveDown) {
                            if (this.mItemCount > 1) {
                                this.mFlingRunnable.startUsingDistance(this.mDistanceViews * (-1));
                                BeepUtility.getInstance().playBeep(BeepUtilityRsrcTable.BEEP_ID_MOVE_DOWN);
                            }
                            this.isMoveDown = false;
                        }
                    }
                    break;
                case 0:
                case 2:
                default:
                    return true;
            }
        }
        return true;
    }

    @Override // android.view.GestureDetector.OnGestureListener
    public boolean onFling(MotionEvent arg0, MotionEvent arg1, float velocityX, float velocityY) {
        if (this.mItemCount == 1) {
            this.isFling = false;
        } else {
            this.mFlingRunnable.startUsingVelocity((int) (-velocityY));
            this.isFling = true;
        }
        return true;
    }

    @Override // android.view.GestureDetector.OnGestureListener
    public boolean onSingleTapUp(MotionEvent arg0) {
        if (this.mOnItemClickListener == null || this.mDownPositionAD != this.mSelectedPositionAD) {
            return false;
        }
        this.isSingleTapUp = true;
        return true;
    }

    @Override // android.view.GestureDetector.OnGestureListener
    public void onLongPress(MotionEvent arg0) {
        if (this.mOnItemClickListener != null && this.mDownPositionAD == this.mSelectedPositionAD) {
            this.isLongPress = true;
        }
    }

    @Override // android.view.GestureDetector.OnGestureListener
    public boolean onScroll(MotionEvent arg0, MotionEvent arg1, float arg2, float distanceY) {
        if (this.mItemCount == 1) {
            this.isScroll = false;
        } else {
            getParent().requestDisallowInterceptTouchEvent(true);
            trackMotionScroll(((int) distanceY) * (-1));
            this.isScroll = true;
            if (this.mOnItemScrollListener != null) {
                this.mOnItemScrollListener.onScrollStarted();
            }
        }
        return true;
    }

    @Override // android.view.GestureDetector.OnGestureListener
    public void onShowPress(MotionEvent arg0) {
    }

    public int getFirstPosition() {
        return this.mFirstPositionAD;
    }

    @Override // android.widget.AdapterView
    public int getSelectedItemPosition() {
        return this.mSelectedPositionAD;
    }

    @Override // android.view.View
    public void setVisibility(int visible) {
        super.setVisibility(visible);
    }

    public void setLayoutAttributes(int childWidth, int childHeight, int spacing) {
        this.mChildViewWidth = childWidth;
        this.mChildViewHeight = childHeight;
        this.mHorizontalSpacing = spacing;
        this.mDistanceViews = this.mChildViewHeight + this.mHorizontalSpacing;
    }

    public void setLayoutAttributes(int childWidth, int childHeight, int spacing, int distanceOfViews) {
        this.mChildViewWidth = childWidth;
        this.mChildViewHeight = childHeight;
        this.mHorizontalSpacing = spacing;
        this.mDistanceViews = distanceOfViews;
    }
}
