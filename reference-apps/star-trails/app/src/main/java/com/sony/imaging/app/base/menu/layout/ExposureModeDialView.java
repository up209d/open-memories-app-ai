package com.sony.imaging.app.base.menu.layout;

import android.content.Context;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Transformation;
import android.widget.ImageView;
import com.sony.imaging.app.base.R;
import com.sony.imaging.app.base.common.widget.BatteryIcon;
import com.sony.imaging.app.base.menu.layout.SubMenuView;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.base.shooting.camera.executor.IntervalRecExecutor;
import com.sony.imaging.app.base.shooting.trigger.ModeDialDetector;
import com.sony.imaging.app.fw.AppRoot;
import java.util.LinkedList;

/* loaded from: classes.dex */
public class ExposureModeDialView extends SubMenuView {
    private static final int ANIMATION_DURATION = 100;
    private static final int CENTER_ICON_HEIGHT = 72;
    private static final int CENTER_ICON_WIDTH = 102;
    private static final DialPositionInfo[] DIAL_POS_INFOS = {new DialPositionInfo(-69, -1, 145, 0, BatteryIcon.BATTERY_STATUS_CHARGING, 0), new DialPositionInfo(0, 2, 48, 0, BatteryIcon.BATTERY_STATUS_CHARGING, 0), new DialPositionInfo(69, 64, 28, 1, BatteryIcon.BATTERY_STATUS_CHARGING, 0), new DialPositionInfo(138, 126, 14, 2, BatteryIcon.BATTERY_STATUS_CHARGING, 0), new DialPositionInfo(AppRoot.USER_KEYCODE.PLAYBACK, IntervalRecExecutor.INTVL_REC_INITIALIZED, 3, 3, BatteryIcon.BATTERY_STATUS_CHARGING, 0), new DialPositionInfo(276, 282, 14, 4, BatteryIcon.BATTERY_STATUS_CHARGING, 0), new DialPositionInfo(345, 344, 28, 5, BatteryIcon.BATTERY_STATUS_CHARGING, 0), new DialPositionInfo(414, 406, 48, 6, BatteryIcon.BATTERY_STATUS_CHARGING, 0), new DialPositionInfo(480, 409, 145, 6, BatteryIcon.BATTERY_STATUS_CHARGING, 0)};
    private static final DialPositionInfo[] DIAL_POS_INFOS_LEFT = {new DialPositionInfo(-69, -1, 145, 0, BatteryIcon.BATTERY_STATUS_CHARGING, 0), new DialPositionInfo(0, 2, -6, 0, BatteryIcon.BATTERY_STATUS_CHARGING, 0), new DialPositionInfo(69, 64, 14, 1, BatteryIcon.BATTERY_STATUS_CHARGING, 0), new DialPositionInfo(138, 126, 28, 2, BatteryIcon.BATTERY_STATUS_CHARGING, 0), new DialPositionInfo(AppRoot.USER_KEYCODE.PLAYBACK, IntervalRecExecutor.INTVL_REC_INITIALIZED, 27, 3, BatteryIcon.BATTERY_STATUS_CHARGING, 0), new DialPositionInfo(276, 282, 28, 4, BatteryIcon.BATTERY_STATUS_CHARGING, 0), new DialPositionInfo(345, 344, 14, 5, BatteryIcon.BATTERY_STATUS_CHARGING, 0), new DialPositionInfo(414, 406, -6, 6, BatteryIcon.BATTERY_STATUS_CHARGING, 0), new DialPositionInfo(480, 409, 145, 6, BatteryIcon.BATTERY_STATUS_CHARGING, 0)};
    private static final int EXP_MENU_DISP_CNT = 7;
    private static final int GALLERY_PADDING_BOTTOM = 69;
    private static final int GALLERY_PADDING_TOP = 69;
    private static final int GRID_HEIGHT = 69;
    private static final int GRID_WIDTH = 102;
    private static final int ITEM_COUNT_3 = 3;
    private static final int ITEM_COUNT_5 = 5;
    private static final int ITEM_COUNT_7 = 7;
    private static final int OFFSET = 3;
    private static final int VIEW_SIZE = 480;
    private boolean mTouchEnabled;
    private DialPositionInfo mTransformDialPosInfo;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class DialPositionInfo {
        int mAlpha;
        int mGridY;
        int mRotate;
        int mSubIndex;
        int mX;
        int mY;

        DialPositionInfo(int gridY, int y, int x, int subIndex, int alpha, int rotate) {
            this.mGridY = gridY;
            this.mY = y;
            this.mX = x;
            this.mSubIndex = subIndex;
            this.mAlpha = alpha;
            this.mRotate = rotate;
        }
    }

    public ExposureModeDialView(Context context) {
        super(context);
        initialize();
    }

    public ExposureModeDialView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public ExposureModeDialView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initialize();
    }

    private void initialize() {
        setStaticTransformationsEnabled(true);
        setGravity(3);
        setPadding(0, 0, 0, 0);
        setLayoutAttributes(ExecutorCreator.FINALIZED, CENTER_ICON_HEIGHT, -3, 69);
        this.mTransformDialPosInfo = new DialPositionInfo(0, 0, 0, 0, 0, 0);
        setAnimationDuration(100);
        this.mTouchEnabled = false;
        if (ModeDialDetector.getDialPosition() == 2) {
            setBackgroundResource(17305643);
        } else {
            setBackgroundResource(17306114);
        }
    }

    @Override // com.sony.imaging.app.base.menu.layout.SubMenuView
    protected void initMenuItemViewList() {
        if (this.mItemCount == 1) {
            this.mIsTwoItemsMode = false;
            this.mGalleryPaddingTop = AppRoot.USER_KEYCODE.PLAYBACK;
            this.mGalleryPaddingBottom = AppRoot.USER_KEYCODE.PLAYBACK;
            this.mNewSelectedChildIndex = 0;
        } else if (this.mItemCount == 2) {
            this.mIsTwoItemsMode = true;
            this.mItemCount = 3;
            this.mGalleryPaddingTop = 138;
            this.mGalleryPaddingBottom = 138;
            this.mNewSelectedChildIndex = 1;
        } else if (this.mItemCount == 3 || this.mItemCount == 4) {
            this.mIsTwoItemsMode = false;
            this.mGalleryPaddingTop = 138;
            this.mGalleryPaddingBottom = 138;
            this.mNewSelectedChildIndex = 1;
        } else if (this.mItemCount == 5 || this.mItemCount == 6) {
            this.mIsTwoItemsMode = false;
            this.mGalleryPaddingTop = 69;
            this.mGalleryPaddingBottom = 69;
            this.mNewSelectedChildIndex = 2;
        } else if (this.mItemCount >= 7) {
            this.mIsTwoItemsMode = false;
            this.mGalleryPaddingTop = getPaddingTop() + 3;
            this.mGalleryPaddingBottom = getPaddingBottom() + 3;
            this.mNewSelectedChildIndex = 3;
        }
        this.mPositionADList = new LinkedList<>();
        int i = 0;
        while (i < this.mItemCount) {
            int prev = i == 0 ? this.mItemCount - 1 : i - 1;
            int next = i == this.mItemCount + (-1) ? 0 : i + 1;
            SubMenuView.PositionADList item = new SubMenuView.PositionADList(i, prev, next);
            this.mPositionADList.add(item);
            i++;
        }
    }

    @Override // com.sony.imaging.app.base.menu.layout.SubMenuView, android.view.ViewGroup
    protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new ViewGroup.LayoutParams(ExecutorCreator.FINALIZED, CENTER_ICON_HEIGHT);
    }

    @Override // android.view.View
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override // com.sony.imaging.app.base.menu.layout.SubMenuView, android.view.ViewGroup
    protected boolean getChildStaticTransformation(View child, Transformation t) {
        t.clear();
        t.setTransformationType(Transformation.TYPE_MATRIX);
        transformImageBitmap(child, t);
        return true;
    }

    private void transformImageBitmap(View child, Transformation t) {
        int childTop = child.getTop();
        Matrix imageMatrix = t.getMatrix();
        updateTransformDialPosInfo(childTop);
        imageMatrix.postTranslate(this.mTransformDialPosInfo.mX, this.mTransformDialPosInfo.mY - childTop);
        ImageView iv = (ImageView) child.findViewById(R.id.submenu_list_icon);
        if (iv != null) {
            iv.setScaleType(ImageView.ScaleType.CENTER);
        }
    }

    void updateTransformDialPosInfo(int top) {
        DialPositionInfo[] dialPosInfo;
        if (ModeDialDetector.getDialPosition() == 2) {
            dialPosInfo = DIAL_POS_INFOS_LEFT;
        } else {
            dialPosInfo = DIAL_POS_INFOS;
        }
        int length = dialPosInfo.length;
        for (int i = 0; i < length - 1; i++) {
            DialPositionInfo now = dialPosInfo[i];
            DialPositionInfo next = dialPosInfo[i + 1];
            if (now.mGridY < top && top <= next.mGridY) {
                float ratio = (top - now.mGridY) / (next.mGridY - now.mGridY);
                int x = (int) (((next.mX - now.mX) * ratio) + now.mX);
                int y = (int) (((next.mY - now.mY) * ratio) + now.mY);
                this.mTransformDialPosInfo.mX = x;
                this.mTransformDialPosInfo.mY = y;
                this.mTransformDialPosInfo.mSubIndex = next.mSubIndex;
                return;
            }
        }
    }

    public void moveTo(int position) {
        int selectedPos = getSelectedItemPosition();
        if (position != selectedPos) {
            ExposureModeMenuAdapter adapter = (ExposureModeMenuAdapter) getAdapter();
            int length = adapter.getCount();
            int distance = Math.abs(selectedPos - position);
            int anotherDistance = Math.abs(length - distance);
            int direction = 0;
            if (distance < anotherDistance) {
                if (position > selectedPos) {
                    direction = 1;
                }
            } else if (position < selectedPos) {
                direction = 1;
            }
            int amount = Math.min(distance, anotherDistance);
            if (direction == 0) {
                movePrevious(amount);
            } else {
                moveNext(amount);
            }
        }
    }

    public void enableTouchMode(boolean enable) {
        this.mTouchEnabled = enable;
    }

    @Override // com.sony.imaging.app.base.menu.layout.SubMenuView, android.view.View
    public boolean onTouchEvent(MotionEvent event) {
        if (this.mTouchEnabled) {
            return super.onTouchEvent(event);
        }
        return true;
    }
}
