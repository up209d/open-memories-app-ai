package com.sony.imaging.app.base.menu.layout;

import android.R;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Pair;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import java.util.ArrayList;
import java.util.Iterator;

/* loaded from: classes.dex */
public class SpecialScreenArea extends RelativeLayout {
    private static final int ARROW0_LIST2_MARGIN_TOP = 188;
    private static final int ARROW0_LIST3_MARGIN_TOP = 155;
    private static final int ARROW0_LIST4_MARGIN_TOP = 122;
    private static final int ARROW0_LIST5_MARGIN_TOP = 89;
    private static final int ARROWBOTTOM_MARGIN_LEFT = 39;
    private static final int ARROWBOTTOM_MARGIN_TOP = 407;
    private static final int ARROWLEFT_MARGIN_LEFT = 0;
    private static final int ARROWRIGHT_MARGIN_LEFT = 91;
    private static final int ARROWTOP_MARGIN_LEFT = 39;
    private static final int ARROWTOP_MARGIN_TOP = 55;
    private static final int ARROW_LEFT_RIGHT_HEIGHT = 38;
    private static final int ARROW_LEFT_RIGHT_WIDTH = 21;
    private static final int ARROW_LIST_OFFSET = 66;
    private static final int ARROW_TOP_BOTTOM_HEIGHT = 18;
    private static final int ARROW_TOP_BOTTOM_WIDTH = 34;
    private static final int BLOCK_COUNT_2 = 2;
    private static final int BLOCK_COUNT_3 = 3;
    private static final int BLOCK_COUNT_4 = 4;
    private static final int BLOCK_COUNT_5 = 5;
    private Context mContext;
    private ArrayList<ArrowViewPair> mLeftRightArrowsList;
    private ArrowViewPair mTopBottomArrows;
    private SpecialScreenView mView;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class ArrowView extends ImageView {
        private static final int BOTTOM = 1;
        private static final int LEFT = 2;
        private static final int RIGHT = 3;
        private static final int TOP = 0;
        private final int BOTTOM_RESID;
        private final int LEFT_RESID;
        private final int RIGHT_RESID;
        private final int TOP_RESID;

        public ArrowView(Context context, int direction) {
            super(context);
            int resid;
            this.TOP_RESID = 17304528;
            this.BOTTOM_RESID = R.drawable.btn_toggle_bg;
            this.LEFT_RESID = 17305144;
            this.RIGHT_RESID = 17304536;
            switch (direction) {
                case 0:
                    resid = 17304528;
                    break;
                case 1:
                    resid = R.drawable.btn_toggle_bg;
                    break;
                case 2:
                    resid = 17305144;
                    break;
                case 3:
                    resid = 17304536;
                    break;
                default:
                    resid = -1;
                    break;
            }
            setImageResource(resid);
        }

        public void setVisible() {
            setVisibility(0);
        }

        public void setInVisible() {
            setVisibility(4);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class ArrowViewPair extends Pair<ArrowView, ArrowView> {
        public ArrowViewPair(ArrowView first, ArrowView second) {
            super(first, second);
        }

        public void setInVisible() {
            ((ArrowView) this.first).setInVisible();
            ((ArrowView) this.second).setInVisible();
        }

        public void setVisible() {
            ((ArrowView) this.first).setVisible();
            ((ArrowView) this.second).setVisible();
        }

        public void setVisibleRight() {
            ((ArrowView) this.first).setInVisible();
            ((ArrowView) this.second).setVisible();
        }

        public void setVisibleBottom() {
            setVisibleLeft();
        }

        public void setVisibleLeft() {
            ((ArrowView) this.first).setVisible();
            ((ArrowView) this.second).setInVisible();
        }

        public void setVisibleUp() {
            setVisibleRight();
        }
    }

    private ArrowView makeArrowView(Context context, int direction, int left, int top, int width, int height) {
        ArrowView view = new ArrowView(context, direction);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, height);
        params.leftMargin = left;
        params.topMargin = top;
        addView(view, params);
        return view;
    }

    private ArrowView makeArrowViewLeft(Context context, int top) {
        return makeArrowView(context, 2, 0, top, 21, 38);
    }

    private ArrowView makeArrowViewRight(Context context, int top) {
        return makeArrowView(context, 3, ARROWRIGHT_MARGIN_LEFT, top, 21, 38);
    }

    private ArrowView makeArrowViewTop(Context context, int top) {
        return makeArrowView(context, 0, 39, top, 34, 18);
    }

    private ArrowView makeArrowViewBottom(Context context, int top) {
        return makeArrowView(context, 1, 39, top, 34, 18);
    }

    private void initViewsList2(Context context) {
        this.mTopBottomArrows = null;
        this.mLeftRightArrowsList = new ArrayList<>();
        ArrowView leftArrowView0 = makeArrowViewLeft(context, ARROW0_LIST2_MARGIN_TOP);
        ArrowView rightArrowView0 = makeArrowViewRight(context, ARROW0_LIST2_MARGIN_TOP);
        ArrowViewPair leftright0 = new ArrowViewPair(leftArrowView0, rightArrowView0);
        this.mLeftRightArrowsList.add(leftright0);
        ArrowView leftArrowView1 = makeArrowViewLeft(context, 254);
        ArrowView rightArrowView1 = makeArrowViewRight(context, 254);
        ArrowViewPair leftright1 = new ArrowViewPair(leftArrowView1, rightArrowView1);
        this.mLeftRightArrowsList.add(leftright1);
    }

    private void initViewsList3(Context context) {
        this.mTopBottomArrows = null;
        this.mLeftRightArrowsList = new ArrayList<>();
        ArrowView leftArrowView0 = makeArrowViewLeft(context, ARROW0_LIST3_MARGIN_TOP);
        ArrowView rightArrowView0 = makeArrowViewRight(context, ARROW0_LIST3_MARGIN_TOP);
        ArrowViewPair leftright0 = new ArrowViewPair(leftArrowView0, rightArrowView0);
        this.mLeftRightArrowsList.add(leftright0);
        ArrowView leftArrowView1 = makeArrowViewLeft(context, 221);
        ArrowView rightArrowView1 = makeArrowViewRight(context, 221);
        ArrowViewPair leftright1 = new ArrowViewPair(leftArrowView1, rightArrowView1);
        this.mLeftRightArrowsList.add(leftright1);
        ArrowView leftArrowView2 = makeArrowViewLeft(context, 287);
        ArrowView rightArrowView2 = makeArrowViewRight(context, 287);
        ArrowViewPair leftright2 = new ArrowViewPair(leftArrowView2, rightArrowView2);
        this.mLeftRightArrowsList.add(leftright2);
    }

    private void initViewsList4(Context context) {
        ArrowView topArrowView = makeArrowViewTop(context, ARROWTOP_MARGIN_TOP);
        ArrowView bottomArrowView = makeArrowViewBottom(context, ARROWBOTTOM_MARGIN_TOP);
        this.mTopBottomArrows = new ArrowViewPair(topArrowView, bottomArrowView);
        this.mLeftRightArrowsList = new ArrayList<>();
        ArrowView leftArrowView0 = makeArrowViewLeft(context, 122);
        ArrowView rightArrowView0 = makeArrowViewRight(context, 122);
        ArrowViewPair leftright0 = new ArrowViewPair(leftArrowView0, rightArrowView0);
        this.mLeftRightArrowsList.add(leftright0);
        ArrowView leftArrowView1 = makeArrowViewLeft(context, ARROW0_LIST2_MARGIN_TOP);
        ArrowView rightArrowView1 = makeArrowViewRight(context, ARROW0_LIST2_MARGIN_TOP);
        ArrowViewPair leftright1 = new ArrowViewPair(leftArrowView1, rightArrowView1);
        this.mLeftRightArrowsList.add(leftright1);
        ArrowView leftArrowView2 = makeArrowViewLeft(context, 254);
        ArrowView rightArrowView2 = makeArrowViewRight(context, 254);
        ArrowViewPair leftright2 = new ArrowViewPair(leftArrowView2, rightArrowView2);
        this.mLeftRightArrowsList.add(leftright2);
        ArrowView leftArrowView3 = makeArrowViewLeft(context, 320);
        ArrowView rightArrowView3 = makeArrowViewRight(context, 320);
        ArrowViewPair leftright3 = new ArrowViewPair(leftArrowView3, rightArrowView3);
        this.mLeftRightArrowsList.add(leftright3);
    }

    private void initViewsList5(Context context) {
        ArrowView topArrowView = makeArrowViewTop(context, ARROWTOP_MARGIN_TOP);
        ArrowView bottomArrowView = makeArrowViewBottom(context, ARROWBOTTOM_MARGIN_TOP);
        this.mTopBottomArrows = new ArrowViewPair(topArrowView, bottomArrowView);
        this.mLeftRightArrowsList = new ArrayList<>();
        ArrowView leftArrowView0 = makeArrowViewLeft(context, ARROW0_LIST5_MARGIN_TOP);
        ArrowView rightArrowView0 = makeArrowViewRight(context, ARROW0_LIST5_MARGIN_TOP);
        ArrowViewPair leftright0 = new ArrowViewPair(leftArrowView0, rightArrowView0);
        this.mLeftRightArrowsList.add(leftright0);
        ArrowView leftArrowView1 = makeArrowViewLeft(context, ARROW0_LIST3_MARGIN_TOP);
        ArrowView rightArrowView1 = makeArrowViewRight(context, ARROW0_LIST3_MARGIN_TOP);
        ArrowViewPair leftright1 = new ArrowViewPair(leftArrowView1, rightArrowView1);
        this.mLeftRightArrowsList.add(leftright1);
        ArrowView leftArrowView2 = makeArrowViewLeft(context, 221);
        ArrowView rightArrowView2 = makeArrowViewRight(context, 221);
        ArrowViewPair leftright2 = new ArrowViewPair(leftArrowView2, rightArrowView2);
        this.mLeftRightArrowsList.add(leftright2);
        ArrowView leftArrowView3 = makeArrowViewLeft(context, 287);
        ArrowView rightArrowView3 = makeArrowViewRight(context, 287);
        ArrowViewPair leftright3 = new ArrowViewPair(leftArrowView3, rightArrowView3);
        this.mLeftRightArrowsList.add(leftright3);
        ArrowView leftArrowView4 = makeArrowViewLeft(context, 353);
        ArrowView rightArrowView4 = makeArrowViewRight(context, 353);
        ArrowViewPair leftright4 = new ArrowViewPair(leftArrowView4, rightArrowView4);
        this.mLeftRightArrowsList.add(leftright4);
    }

    public SpecialScreenArea(Context context) {
        super(context);
        this.mTopBottomArrows = null;
        this.mLeftRightArrowsList = null;
        this.mView = null;
        this.mContext = null;
    }

    public SpecialScreenArea(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mTopBottomArrows = null;
        this.mLeftRightArrowsList = null;
        this.mView = null;
        this.mContext = null;
    }

    private void initViews(int count) {
        switch (count) {
            case 2:
                initViewsList2(this.mContext);
                return;
            case 3:
                initViewsList3(this.mContext);
                return;
            case 4:
                initViewsList4(this.mContext);
                return;
            default:
                initViewsList5(this.mContext);
                return;
        }
    }

    private void clear() {
        if (this.mTopBottomArrows != null) {
            this.mTopBottomArrows.setInVisible();
            this.mTopBottomArrows = null;
        }
        if (this.mLeftRightArrowsList != null) {
            Iterator i$ = this.mLeftRightArrowsList.iterator();
            while (i$.hasNext()) {
                ArrowViewPair list = i$.next();
                list.setInVisible();
            }
            this.mLeftRightArrowsList.clear();
            this.mLeftRightArrowsList = null;
        }
        removeAllViews();
    }

    public void intialize(Context context, SpecialScreenView view) {
        this.mContext = context;
        this.mView = view;
        clear();
        initViews(this.mView.getItemCount());
    }

    public void update() {
        updateTopAndBottomArrows();
        updateLeftAndRightArrows();
    }

    public void terminate() {
        this.mView = null;
        this.mContext = null;
        clear();
    }

    private void updateTopAndBottomArrows() {
        if (this.mTopBottomArrows != null) {
            this.mTopBottomArrows.setInVisible();
            if (this.mView.getItemCount() > 5) {
                if (this.mView.getFirstPosition() == 0) {
                    this.mTopBottomArrows.setVisibleUp();
                } else if (this.mView.getFirstPosition() == this.mView.getItemCount() - this.mView.getBlockCount()) {
                    this.mTopBottomArrows.setVisibleBottom();
                } else {
                    this.mTopBottomArrows.setVisible();
                }
            }
        }
    }

    private void updateLeftAndRightArrows() {
        int viewPosition = this.mView.getSelectedItemPosition() - this.mView.getFirstPosition();
        Iterator i$ = this.mLeftRightArrowsList.iterator();
        while (i$.hasNext()) {
            ArrowViewPair list = i$.next();
            list.setInVisible();
        }
        if (this.mView.getOptionItemCount() > 1) {
            if (this.mView.getSelectedOptionPosition() == 0) {
                this.mLeftRightArrowsList.get(viewPosition).setVisibleRight();
            } else if (this.mView.getSelectedOptionPosition() == this.mView.getOptionItemCount() - 1) {
                this.mLeftRightArrowsList.get(viewPosition).setVisibleLeft();
            } else {
                this.mLeftRightArrowsList.get(viewPosition).setVisible();
            }
        }
    }
}
