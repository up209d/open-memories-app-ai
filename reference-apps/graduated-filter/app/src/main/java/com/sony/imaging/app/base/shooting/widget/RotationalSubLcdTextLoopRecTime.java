package com.sony.imaging.app.base.shooting.widget;

import android.R;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import com.sony.imaging.app.base.common.widget.RotationalSubLcdTextView;
import com.sony.imaging.app.base.menu.IController;
import com.sony.imaging.app.base.shooting.camera.LoopRecController;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import java.util.HashMap;
import java.util.Map;

/* loaded from: classes.dex */
public class RotationalSubLcdTextLoopRecTime extends RotationalSubLcdTextView {
    private static final String TAG = RotationalSubLcdTextLoopRecTime.class.getSimpleName();
    private Map<String, String> mTextMap;

    public RotationalSubLcdTextLoopRecTime(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mTextMap = new HashMap();
        initView();
    }

    public RotationalSubLcdTextLoopRecTime(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mTextMap = new HashMap();
        initView();
    }

    public RotationalSubLcdTextLoopRecTime(Context context) {
        super(context);
        this.mTextMap = new HashMap();
        initView();
    }

    private void initView() {
        this.mTextMap.put(LoopRecController.MAX_DURATION_5min, getResources().getString(R.string.permlab_bindCarrierServices));
        this.mTextMap.put(LoopRecController.MAX_DURATION_20min, getResources().getString(R.string.permlab_bindConditionProviderService));
        this.mTextMap.put(LoopRecController.MAX_DURATION_60min, getResources().getString(R.string.permlab_bindDreamService));
        this.mTextMap.put(LoopRecController.MAX_DURATION_120min, getResources().getString(R.string.permlab_bindNotificationListenerService));
        this.mTextMap.put(LoopRecController.MAX_DURATION_UNLIMITED, getResources().getString(R.string.permlab_bind_connection_service));
    }

    @Override // com.sony.imaging.app.base.common.widget.RotationalSubLcdTextView, com.sony.imaging.app.base.common.widget.Rotational
    public boolean isValidValue() {
        return ExecutorCreator.isLoopRecEnable();
    }

    @Override // com.sony.imaging.app.base.common.widget.RotationalSubLcdTextView
    protected String makeText() {
        String value = "";
        try {
            value = LoopRecController.getInstance().getValue(LoopRecController.MAX_DURATION);
        } catch (IController.NotSupportedException e) {
            Log.e(TAG, "Loop Rec Time isn't supported");
        }
        String text = this.mTextMap.get(value);
        return text != null ? text : "";
    }
}
