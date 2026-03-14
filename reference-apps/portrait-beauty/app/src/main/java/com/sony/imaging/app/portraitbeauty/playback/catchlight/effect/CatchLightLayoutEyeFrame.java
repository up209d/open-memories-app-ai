package com.sony.imaging.app.portraitbeauty.playback.catchlight.effect;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import com.sony.imaging.app.base.common.DisplayModeObserver;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.portraitbeauty.R;
import com.sony.imaging.app.portraitbeauty.playback.catchlight.layout.CatchLightPlayBackLayout;
import com.sony.imaging.app.portraitbeauty.shooting.PortraitBeautyDisplayModeObserver;

/* loaded from: classes.dex */
public class CatchLightLayoutEyeFrame extends View {
    public CatchLightLayoutEyeFrame(Context context) {
        super(context);
    }

    @Override // android.view.View
    public void setLayoutParams(ViewGroup.LayoutParams params) {
        super.setLayoutParams(params);
    }

    @Override // android.view.View
    public void onDraw(Canvas canvas) {
        Drawable drawable;
        int left;
        int top;
        int right;
        int bottom;
        Drawable drawable2;
        int left2;
        int top2;
        int right2;
        int bottom2;
        Drawable drawable3;
        int left3;
        int top3;
        int right3;
        int bottom3;
        Drawable drawable4;
        int left4;
        int top4;
        int right4;
        int bottom4;
        Log.d(getClass().getSimpleName(), "onDraw run");
        Log.d(getClass().getSimpleName(), "onDraw CatchLightPlayBackLayout.mCatchLightLayoutEyeFrame_draw:" + CatchLightPlayBackLayout.mCatchLightLayoutEyeFrame_draw);
        Log.d(getClass().getSimpleName(), "onDraw CatchLightPlayBackLayout.mIsLeftEyeSelected:" + CatchLightPlayBackLayout.mIsLeftEyeSelected);
        if (CatchLightPlayBackLayout.mCatchLightLayoutEyeFrame_draw) {
            Color.parseColor("#FFDDDDDD");
            getResources().getDrawable(R.drawable.p_16_dd_parts_portraitbeauty_catchlight_blackframe);
            getResources().getDrawable(R.drawable.p_16_dd_parts_portraitbeauty_catchlight_frame);
            getResources().getDrawable(R.drawable.p_16_dd_parts_portraitbeauty_catchlight_blackframe);
            int device = PortraitBeautyDisplayModeObserver.getInstance().getActiveDevice();
            if (device == 2 && 3 == DisplayModeObserver.getInstance().getDeviceInfo(0).aspect) {
                if (CatchLightPlayBackLayout.mCatchLightEyeFrame_draw_black) {
                    getResources().getDrawable(R.drawable.p_16_dd_parts_portraitbeauty_catchlight_blackframe);
                    int left5 = 42 - 3;
                    getResources().getDrawable(R.drawable.p_16_dd_parts_portraitbeauty_catchlight_blackframe);
                }
                if (CatchLightPlayBackLayout.mIsLeftEyeSelected && !CatchLightPlayBackLayout.mCatchLightEyeFrame_draw_black) {
                    drawable3 = getResources().getDrawable(R.drawable.p_16_dd_parts_portraitbeauty_catchlight_frame);
                    left3 = 42 - 3;
                    top3 = 234 - 2;
                    right3 = 294;
                    bottom3 = 2 + 356;
                } else {
                    drawable3 = getResources().getDrawable(R.drawable.p_16_dd_parts_portraitbeauty_catchlight_blackframe);
                    left3 = 42 - 3;
                    top3 = CatchLightPlayBackLayout.mOutput_eye_top;
                    right3 = 294;
                    bottom3 = 356;
                }
                drawable3.setBounds(left3, top3, right3, bottom3);
                drawable3.draw(canvas);
                if (!CatchLightPlayBackLayout.mIsLeftEyeSelected && !CatchLightPlayBackLayout.mCatchLightEyeFrame_draw_black) {
                    drawable4 = getResources().getDrawable(R.drawable.p_16_dd_parts_portraitbeauty_catchlight_frame);
                    left4 = 294 - 3;
                    top4 = 234 - 2;
                    right4 = 546 - 4;
                    bottom4 = 2 + 356;
                } else {
                    drawable4 = getResources().getDrawable(R.drawable.p_16_dd_parts_portraitbeauty_catchlight_blackframe);
                    left4 = 32;
                    top4 = CatchLightPlayBackLayout.mOutput_eye_top;
                    right4 = AppRoot.USER_KEYCODE.MODE_DIAL_BOKS;
                    bottom4 = 356;
                }
                drawable4.setBounds(left4, top4, right4, bottom4);
                drawable4.draw(canvas);
                return;
            }
            if (CatchLightPlayBackLayout.mCatchLightEyeFrame_draw_black) {
                getResources().getDrawable(R.drawable.p_16_dd_parts_portraitbeauty_catchlight_blackframe);
                int left6 = 82 - 3;
                getResources().getDrawable(R.drawable.p_16_dd_parts_portraitbeauty_catchlight_blackframe);
            }
            if (CatchLightPlayBackLayout.mIsLeftEyeSelected && !CatchLightPlayBackLayout.mCatchLightEyeFrame_draw_black) {
                drawable = getResources().getDrawable(R.drawable.p_16_dd_parts_portraitbeauty_catchlight_frame);
                left = 82 - 3;
                top = 234 - 2;
                right = 314;
                bottom = 2 + 356;
            } else {
                drawable = getResources().getDrawable(R.drawable.p_16_dd_parts_portraitbeauty_catchlight_blackframe);
                left = 82 - 3;
                top = CatchLightPlayBackLayout.mOutput_eye_top;
                right = 314;
                bottom = 356;
            }
            drawable.setBounds(left, top, right, bottom);
            drawable.draw(canvas);
            if (!CatchLightPlayBackLayout.mIsLeftEyeSelected && !CatchLightPlayBackLayout.mCatchLightEyeFrame_draw_black) {
                drawable2 = getResources().getDrawable(R.drawable.p_16_dd_parts_portraitbeauty_catchlight_frame);
                left2 = 314 - 3;
                top2 = 234 - 2;
                right2 = 546 - 4;
                bottom2 = 2 + 356;
            } else {
                drawable2 = getResources().getDrawable(R.drawable.p_16_dd_parts_portraitbeauty_catchlight_blackframe);
                left2 = 82;
                top2 = CatchLightPlayBackLayout.mOutput_eye_top;
                right2 = AppRoot.USER_KEYCODE.MODE_DIAL_BOKS;
                bottom2 = 356;
            }
            drawable2.setBounds(left2, top2, right2, bottom2);
            drawable2.draw(canvas);
        }
    }
}
