package com.sony.imaging.app.base.menu.layout;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Scroller;
import com.sony.imaging.app.base.R;

/* loaded from: classes.dex */
public class BeltView extends HorizonalListView {
    private static final String TAG = BeltView.class.getSimpleName();
    private int DURATION;
    private int mPoolCt;
    private Scroller mScroller;
    private int mVisibleItemNum;
    MenuUpdater updater;

    public BeltView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mVisibleItemNum = 5;
        this.DURATION = 700;
        this.mPoolCt = 1;
        TypedArray tArray = context.obtainStyledAttributes(attrs, R.styleable.BeltView);
        this.mVisibleItemNum = tArray.getInt(0, this.mVisibleItemNum);
        this.mScroller = new Scroller(context);
        Handler handler = new Handler();
        this.updater = new MenuUpdater(handler) { // from class: com.sony.imaging.app.base.menu.layout.BeltView.1
            @Override // com.sony.imaging.app.base.menu.layout.BeltView.MenuUpdater, java.lang.Runnable
            public void run() {
                Log.d(BeltView.TAG, "Rest speed");
                BeltView.this.mPoolCt = 1;
            }
        };
    }

    @Override // android.view.View
    public void computeScroll() {
        if (this.mScroller.computeScrollOffset()) {
            int curX = this.mScroller.getCurrX();
            int curY = this.mScroller.getCurrY();
            int finalX = this.mScroller.getFinalX();
            this.mScroller.getFinalY();
            if (curX == finalX) {
                Log.d(TAG, "Finished scroll");
            }
            Log.d(TAG, "computeScroll:: curX:" + curX + " curY: " + curY);
            scrollTo(curX, curY);
            postInvalidate();
        }
    }

    public void setSelectionFromTop(int selectPos, int y, boolean animation) {
        int cur = getSelectedItemPosition();
        Log.d(TAG, "selectPos: " + selectPos + "  curPos: " + cur);
        if (cur != selectPos && isShown()) {
            if (animation) {
                int duration = this.DURATION;
                if (!this.mScroller.isFinished()) {
                    this.mPoolCt++;
                    Log.d(TAG, "Not finished. Speed up: " + this.mPoolCt);
                    duration -= Math.min(this.mPoolCt, 3) * 130;
                }
                this.updater.restartMenuUpdater();
                View child = getChildAt(0);
                int distance = 0;
                if (child != null) {
                    distance = child.getWidth();
                }
                int posDifference = Math.abs(selectPos - cur);
                int count = getCount();
                if (selectPos > cur) {
                    if (posDifference == count - 1 && count != 2) {
                        distance = -(getMeasuredWidth() / 2);
                    } else if (posDifference != 1) {
                        distance = getMeasuredWidth() / 2;
                    }
                } else if (posDifference == 1) {
                    distance = -distance;
                } else if (posDifference == count - 1 && count != 2) {
                    distance = getMeasuredWidth() / 2;
                } else {
                    distance = -(getMeasuredWidth() / 2);
                }
                this.mScroller.startScroll(-distance, 0, distance, 0, duration);
            }
            super.setSelectionFromTop(selectPos, y);
        }
    }

    public void setSelection(int selectPos, boolean animation) {
        setSelectionFromTop(selectPos, 0, animation);
    }

    @Override // android.widget.ListView, android.widget.AdapterView
    public void setSelection(int position) {
        setSelection(position, true);
    }

    @Override // com.sony.imaging.app.base.menu.layout.HorizonalListView, android.widget.AbsListView
    public void setSelectionFromTop(int selectPos, int y) {
        setSelectionFromTop(selectPos, y, true);
    }

    @Override // com.sony.imaging.app.base.menu.layout.HorizonalListView
    protected int getChildrenDrawOffSet() {
        int offset = getWidth() / 2;
        int curPos = getSelectedItemPosition();
        View child = getChildAt(0);
        int childMeasuredWidth = 0;
        if (child != null) {
            childMeasuredWidth = View.MeasureSpec.getSize(child.getMeasuredWidth());
        }
        return offset - ((childMeasuredWidth / 2) + (childMeasuredWidth * curPos));
    }

    @Override // com.sony.imaging.app.base.menu.layout.HorizonalListView, android.view.ViewGroup, android.view.View
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return true;
    }

    @Override // com.sony.imaging.app.base.menu.layout.HorizonalListView, android.view.View
    public boolean isInTouchMode() {
        return false;
    }

    @Override // com.sony.imaging.app.base.menu.layout.HorizonalListView, android.widget.AbsListView, android.view.ViewTreeObserver.OnTouchModeChangeListener
    public void onTouchModeChanged(boolean isInTouchMode) {
        super.onTouchModeChanged(false);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes.dex */
    public class MenuUpdater implements Runnable {
        private Handler handler;
        public boolean isDone = true;
        private long delayTime = 400;

        public MenuUpdater(Handler handler) {
            this.handler = handler;
        }

        @Override // java.lang.Runnable
        public void run() {
        }

        public void setDelayTime(long time) {
            this.delayTime = time;
        }

        public long getDelayTime() {
            return this.delayTime;
        }

        public void restartMenuUpdater() {
            if (!this.isDone) {
                this.handler.removeCallbacks(this);
            }
            this.isDone = false;
            this.handler.postDelayed(this, this.delayTime);
        }

        public void finishMenuUpdater() {
            if (!this.isDone) {
                this.handler.removeCallbacks(this);
                run();
            }
        }

        public void cancelMenuUpdater() {
            if (!this.isDone) {
                this.handler.removeCallbacks(this);
                this.isDone = true;
            }
        }
    }
}
