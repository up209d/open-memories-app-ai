package com.sony.imaging.app.base.shooting.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;
import com.sony.imaging.app.base.R;
import com.sony.imaging.app.base.shooting.camera.WhiteBalanceController;
import java.util.List;

/* loaded from: classes.dex */
public class WhiteBalanceAdjustLayer extends ImageView {
    private static final boolean DEBUG = false;
    private static final String TAG = "WhiteBalanceAdjustLayer";
    private int mBG_ResourceId;
    private List<String> mComp;
    private Context mContext;
    private Bitmap mKnob;
    private List<String> mLight;
    private int mLocalComp;
    private int mLocalLight;
    private WhiteBalanceController mWBController;
    private Integer[] posX;
    private Integer[] posY;

    public WhiteBalanceAdjustLayer(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray attr = context.obtainStyledAttributes(attrs, R.styleable.WhiteBalance);
        this.mContext = context;
        this.mBG_ResourceId = getResources().getIdentifier(attr.getString(12), null, context.getPackageName());
        setImageResource(this.mBG_ResourceId);
        this.mKnob = BitmapFactory.decodeResource(this.mContext.getResources(), getResources().getIdentifier(attr.getString(13), null, context.getPackageName()));
    }

    public void setWBController(WhiteBalanceController controller) {
        this.mWBController = controller;
        WhiteBalanceController.WhiteBalanceParam mWBParam = (WhiteBalanceController.WhiteBalanceParam) this.mWBController.getDetailValue();
        this.mLocalLight = mWBParam.getLightBalance();
        this.mLocalComp = mWBParam.getColorComp();
    }

    public void setXYPosition(Integer[] X, Integer[] Y) {
        this.posX = X;
        this.posY = Y;
        this.mLight = this.mWBController.getSupportedValue(WhiteBalanceController.SETTING_LIGHTBALANCE);
        this.mComp = this.mWBController.getSupportedValue(WhiteBalanceController.SETTING_COMPENSATION);
        invalidate();
    }

    @Override // android.widget.ImageView, android.view.View
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        Bitmap bm = this.mKnob;
        int drawX = this.posX[this.mLocalLight + ((this.mLight.size() - 1) / 2)].intValue();
        int drawY = this.posY[this.mLocalComp + ((this.mComp.size() - 1) / 2)].intValue();
        canvas.drawBitmap(bm, drawX, drawY, (Paint) null);
        Log.d(TAG, "onDraw");
        canvas.restore();
    }

    public boolean moveX(int value) {
        this.mLocalLight = value;
        invalidate();
        return true;
    }

    public boolean moveY(int value) {
        this.mLocalComp = value;
        invalidate();
        return true;
    }

    public void reset(WhiteBalanceController.WhiteBalanceParam param) {
        this.mLocalLight = param.getLightBalance();
        this.mLocalComp = param.getColorComp();
        invalidate();
    }
}
