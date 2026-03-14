package com.sony.imaging.app.photoretouch.playback.layout.resize;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.photoretouch.Constant;
import com.sony.imaging.app.photoretouch.R;
import com.sony.imaging.app.photoretouch.common.EVFOffLayout;
import com.sony.imaging.app.photoretouch.common.ImageEditor;
import com.sony.imaging.app.photoretouch.common.widget.EachButton;
import com.sony.imaging.app.photoretouch.menu.layout.PhotoRetouchSubMenuLayout;
import com.sony.scalar.graphics.OptimizedImage;
import com.sony.scalar.sysutil.ScalarProperties;
import java.util.List;

/* loaded from: classes.dex */
public class ResizeLayout extends EVFOffLayout {
    public static final int RESIZE_MEDIUM = 2;
    public static final int RESIZE_NO = 0;
    public static final int RESIZE_SMALL = 1;
    private static final int SIZE_L = 101;
    private static final int SIZE_M = 102;
    private static final int SIZE_S = 103;
    private int mOptImgHeight;
    private int mOptImgWidth;
    public static ScalarProperties.PictureSize M32 = null;
    public static ScalarProperties.PictureSize S32 = null;
    public static ScalarProperties.PictureSize M169 = null;
    public static ScalarProperties.PictureSize S169 = null;
    public static ScalarProperties.PictureSize M43 = null;
    public static ScalarProperties.PictureSize S43 = null;
    public static ScalarProperties.PictureSize M11 = null;
    public static ScalarProperties.PictureSize S11 = null;
    private final String TAG = ResizeLayout.class.getSimpleName();
    private EachButton mResizeToM = null;
    private EachButton mResizeToS = null;
    private ImageView mCurrentSize = null;
    private TextView mResizeMessage = null;
    private int mCurrentImageSize = -1;
    private RelativeLayout mNoResize = null;
    private RelativeLayout mCanResize = null;
    private TextView mNoResizeDescription = null;
    private int mResizeParameter = 0;
    OptimizedImage mEditImage = null;

    private int getResizeParameter() {
        return this.mResizeParameter;
    }

    private void setResizeParameter(int mResizeParameter) {
        this.mResizeParameter = mResizeParameter;
    }

    /* loaded from: classes.dex */
    class ButtonHandler implements EachButton.Callback {
        ButtonHandler() {
        }

        @Override // com.sony.imaging.app.photoretouch.common.widget.EachButton.Callback
        public void onDown(EachButton view) {
            switch (view.getId()) {
                case R.id.BtnResizeToM /* 2131362104 */:
                    Log.d(ResizeLayout.this.TAG, "==========BtnResizeMedium is clicked");
                    if (ResizeLayout.SIZE_M != ResizeLayout.this.mCurrentImageSize) {
                        ResizeLayout.this.selectM();
                        return;
                    }
                    return;
                case R.id.BtnResizeToS /* 2131362105 */:
                    Log.d(ResizeLayout.this.TAG, "==========BtnResizeSmall is clicked");
                    ResizeLayout.this.selectS();
                    return;
                default:
                    return;
            }
        }

        @Override // com.sony.imaging.app.photoretouch.common.widget.EachButton.Callback
        public void onUp(EachButton view) {
        }

        @Override // com.sony.imaging.app.photoretouch.common.widget.EachButton.Callback
        public void onLongPress(EachButton view) {
        }
    }

    @Override // com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View currentView = obtainViewFromPool(R.layout.resize);
        ImageView backGround = (ImageView) currentView.findViewById(R.id.resize_background);
        backGround.setOnTouchListener(PhotoRetouchSubMenuLayout.blockTouchEvent);
        this.mCanResize = (RelativeLayout) currentView.findViewById(R.id.CanResize);
        this.mCurrentSize = (ImageView) currentView.findViewById(R.id.CurrentSize);
        this.mResizeMessage = (TextView) currentView.findViewById(R.id.ResizeMessage);
        this.mNoResize = (RelativeLayout) currentView.findViewById(R.id.NoResize);
        this.mNoResizeDescription = (TextView) currentView.findViewById(R.id.NoResizeDesc);
        this.mNoResizeDescription.setText(R.string.STRID_CAU_CANNOT_RESIZE);
        this.mResizeToM = (EachButton) currentView.findViewById(R.id.BtnResizeToM);
        this.mResizeToS = (EachButton) currentView.findViewById(R.id.BtnResizeToS);
        ButtonHandler bHandler = new ButtonHandler();
        this.mResizeToM.setEventListener(bHandler);
        this.mResizeToS.setEventListener(bHandler);
        this.mCanResize.setVisibility(8);
        this.mNoResize.setVisibility(8);
        return currentView;
    }

    @Override // com.sony.imaging.app.photoretouch.common.EVFOffLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        Log.d(this.TAG, "==== onResume:optImageCount = " + ImageEditor.optImageCount);
        OptimizedImage optimizedImage = ImageEditor.getImage();
        this.mOptImgHeight = optimizedImage.getHeight();
        this.mOptImgWidth = optimizedImage.getWidth();
        getSupportedPictureSizes();
        this.mCurrentImageSize = getImageSize(this.mOptImgHeight, this.mOptImgWidth);
        Log.d(this.TAG, "====Image Size= " + this.mCurrentImageSize);
        switch (this.mCurrentImageSize) {
            case SIZE_L /* 101 */:
                showLSizeImageUI();
                break;
            case SIZE_M /* 102 */:
                showMSizeImageUI();
                break;
            case 103:
                showSSizeImageUI();
                break;
        }
        setKeyBeepPattern(0);
    }

    private void getSupportedPictureSizes() {
        List<ScalarProperties.PictureSize> list = ScalarProperties.getSupportedPictureSizes();
        M32 = list.get(1);
        S32 = list.get(2);
        M169 = list.get(4);
        S169 = list.get(5);
        M43 = list.get(7);
        S43 = list.get(8);
        M11 = list.get(10);
        S11 = list.get(11);
        Log.d("YES", "opt image mOptImgHeight = " + this.mOptImgHeight);
        Log.d("YES", "opt image mOptImgWidth = " + this.mOptImgWidth);
        Log.d("YES", "Size=====M32 = " + M32.height + " \t" + M32.width);
        Log.d("YES", "Size=====S32 = " + S32.height + " \t" + S32.width);
        Log.d("YES", "Size=====M169 = " + M169.height + " \t" + M169.width);
        Log.d("YES", "Size=====S169 = " + S169.height + " \t" + S169.width);
        Log.d("YES", "Size=====M43 = " + M43.height + " \t" + M43.width);
        Log.d("YES", "Size=====S43 = " + S43.height + " \t" + S43.width);
        Log.d("YES", "Size=====M11 = " + M11.height + " \t" + M11.width);
        Log.d("YES", "Size=====S11 = " + S11.height + " \t" + S11.width);
        list.clear();
    }

    private int getImageSize(int height, int width) {
        if (ImageEditor.getAspectRatio() == 0) {
            if (height <= S32.height && width <= S32.width) {
                return 103;
            }
            if (height <= M32.height && width <= M32.width) {
                return SIZE_M;
            }
            return SIZE_L;
        }
        if (1 == ImageEditor.getAspectRatio()) {
            if (height <= S169.height && width <= S169.width) {
                return 103;
            }
            if (height <= M169.height && width <= M169.width) {
                return SIZE_M;
            }
            return SIZE_L;
        }
        if (2 == ImageEditor.getAspectRatio()) {
            if (height <= S43.height && width <= S43.width) {
                return 103;
            }
            if (height <= M43.height && width <= M43.width) {
                return SIZE_M;
            }
            return SIZE_L;
        }
        if (3 != ImageEditor.getAspectRatio()) {
            return 103;
        }
        if (height <= S11.height && width <= S11.width) {
            Log.e("", "11 small");
            return 103;
        }
        if (height <= M11.height && width <= M11.width) {
            Log.e("", "11 medium");
            return SIZE_M;
        }
        Log.e("", "11 large");
        return SIZE_L;
    }

    private void setMBtnEnable() {
        this.mResizeToM.setBackgroundResource(R.drawable.btnimage_focused);
        this.mResizeToM.setOnTouchListener(null);
    }

    private void setMBtnDisable() {
        this.mResizeToM.setBackgroundResource(0);
        this.mResizeToM.setOnTouchListener(PhotoRetouchSubMenuLayout.blockTouchEvent);
    }

    private void showLSizeImageUI() {
        this.mCurrentSize.setImageResource(R.drawable.p_16_dd_parts_pr_resizel_normal);
        this.mCanResize.setVisibility(0);
        this.mNoResize.setVisibility(8);
        setMBtnEnable();
        selectM();
    }

    private void showMSizeImageUI() {
        this.mCurrentSize.setImageResource(R.drawable.p_16_dd_parts_pr_resizem_normal);
        this.mCanResize.setVisibility(0);
        this.mNoResize.setVisibility(8);
        setMBtnDisable();
        selectS();
    }

    private void showSSizeImageUI() {
        this.mCanResize.setVisibility(8);
        this.mNoResize.setVisibility(0);
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyDown(int keyCode, KeyEvent event) {
        if (isFinder()) {
            return -1;
        }
        Log.d(this.TAG, "onKeyDown....code= " + keyCode);
        int keyCode2 = event.getScanCode();
        switch (keyCode2) {
            case 103:
            case AppRoot.USER_KEYCODE.DOWN /* 108 */:
            case AppRoot.USER_KEYCODE.FN /* 520 */:
            case AppRoot.USER_KEYCODE.AEL /* 532 */:
            case AppRoot.USER_KEYCODE.AF_MF /* 533 */:
            case AppRoot.USER_KEYCODE.MODE_DIAL_CHANGED /* 572 */:
            case AppRoot.USER_KEYCODE.CUSTOM /* 588 */:
            case AppRoot.USER_KEYCODE.AF_RANGE /* 614 */:
            case AppRoot.USER_KEYCODE.EV_DIAL_CHANGED /* 620 */:
            case AppRoot.USER_KEYCODE.CUSTOM1 /* 622 */:
            case AppRoot.USER_KEYCODE.CUSTOM2 /* 623 */:
            case AppRoot.USER_KEYCODE.MOVIE_REC_2ND /* 637 */:
            case AppRoot.USER_KEYCODE.AF_MF_AEL /* 638 */:
                return -1;
            case AppRoot.USER_KEYCODE.LEFT /* 105 */:
            case AppRoot.USER_KEYCODE.SHUTTLE_LEFT /* 523 */:
            case AppRoot.USER_KEYCODE.DIAL1_LEFT /* 526 */:
            case AppRoot.USER_KEYCODE.DIAL2_LEFT /* 529 */:
            case AppRoot.USER_KEYCODE.DIAL3_LEFT /* 634 */:
                if (103 == this.mCurrentImageSize) {
                    return -1;
                }
                handleLeftKey();
                return 1;
            case AppRoot.USER_KEYCODE.RIGHT /* 106 */:
            case AppRoot.USER_KEYCODE.SHUTTLE_RIGHT /* 522 */:
            case AppRoot.USER_KEYCODE.DIAL1_RIGHT /* 525 */:
            case AppRoot.USER_KEYCODE.DIAL2_RIGHT /* 528 */:
            case AppRoot.USER_KEYCODE.DIAL3_RIGHT /* 635 */:
                if (103 == this.mCurrentImageSize) {
                    return -1;
                }
                handleRightKey();
                return 1;
            case AppRoot.USER_KEYCODE.SK1 /* 229 */:
            case AppRoot.USER_KEYCODE.MENU /* 514 */:
                setResizeParameter(0);
                openLayout(Constant.ID_PHOTORETOUCHSUBMENULAYOUT, this.data);
                closeLayout();
                return 1;
            case AppRoot.USER_KEYCODE.CENTER /* 232 */:
                if (PhotoRetouchSubMenuLayout.isRepeat(event)) {
                    return -1;
                }
                Log.d("YES", "====USER_KEYCODE.CENTER");
                if (103 == this.mCurrentImageSize) {
                    return -1;
                }
                editImage();
                openLayout(Constant.ID_PHOTORETOUCHSUBMENULAYOUT, this.data);
                closeLayout();
                saveImage();
                return 1;
            case AppRoot.USER_KEYCODE.SK2 /* 513 */:
            case AppRoot.USER_KEYCODE.DELETE /* 595 */:
                return -1;
            default:
                return 0;
        }
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyUp(int keyCode, KeyEvent event) {
        int keyCode2 = event.getScanCode();
        switch (keyCode2) {
            case AppRoot.USER_KEYCODE.AEL /* 532 */:
            case AppRoot.USER_KEYCODE.AF_MF /* 533 */:
            case AppRoot.USER_KEYCODE.CUSTOM /* 588 */:
            case AppRoot.USER_KEYCODE.AF_RANGE /* 614 */:
            case AppRoot.USER_KEYCODE.CUSTOM1 /* 622 */:
            case AppRoot.USER_KEYCODE.CUSTOM2 /* 623 */:
            case AppRoot.USER_KEYCODE.AF_MF_AEL /* 638 */:
                Log.d(this.TAG, "UP");
                return -1;
            default:
                return 0;
        }
    }

    private void handleLeftKey() {
        if (SIZE_L == this.mCurrentImageSize) {
            if (this.mResizeToM.isSelected()) {
                selectS();
                return;
            } else {
                selectM();
                return;
            }
        }
        selectS();
    }

    private void handleRightKey() {
        if (SIZE_L == this.mCurrentImageSize) {
            if (this.mResizeToM.isSelected()) {
                selectS();
                return;
            } else {
                selectM();
                return;
            }
        }
        selectS();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void selectS() {
        Log.d("INSIDE", "small");
        this.mResizeMessage.setText(android.R.string.face_error_no_space);
        this.mResizeToM.setSelected(false);
        this.mResizeToS.setSelected(true);
        setBackGroundButton();
        this.mResizeToS.setBackgroundResource(R.drawable.layer_list_resize_small_selected);
        setResizeParameter(1);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void selectM() {
        Log.d("INSIDE", "medium");
        this.mResizeMessage.setText(android.R.string.face_error_no_space);
        this.mResizeToM.setSelected(true);
        this.mResizeToS.setSelected(false);
        setBackGroundButton();
        this.mResizeToM.setBackgroundResource(R.drawable.layer_list_resize_med_selected);
        setResizeParameter(2);
    }

    private void editImage() {
        Log.d(this.TAG, "==== editImage:optImageCount = " + ImageEditor.optImageCount);
        this.mEditImage = ImageEditor.scaleImageWithResizeParam(ImageEditor.getImage(), getResizeParameter());
    }

    private void saveImage() {
        ResizeAndSaveTask task = new ResizeAndSaveTask(this, this.mEditImage);
        ImageEditor.executeResultReflection(task, this.mEditImage);
    }

    @Override // com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public void onDestroyView() {
        deinitializeAllValues();
        Log.d(this.TAG, "==== onDestroyView:optImageCount = " + ImageEditor.optImageCount);
        super.onDestroyView();
    }

    private void setBackGroundButton() {
        if (SIZE_M == this.mCurrentImageSize) {
            Drawable d = getResources().getDrawable(R.drawable.p_16_dd_parts_pr_resizem_gray);
            d.setAlpha(77);
            this.mResizeToM.setBackgroundDrawable(d);
        } else {
            this.mResizeToM.setBackgroundResource(R.drawable.layer_list_resize_med_unselected);
        }
        this.mResizeToS.setBackgroundResource(R.drawable.layer_list_resize_small_unselected);
    }

    private void deinitializeAllValues() {
        this.mResizeToM.setSelected(false);
        this.mResizeToS.setSelected(false);
        setBackGroundButton();
        setResizeParameter(0);
        this.mResizeParameter = 0;
        this.mCanResize = null;
        this.mResizeToM = null;
        this.mResizeToS = null;
        this.mCurrentSize = null;
        this.mResizeMessage = null;
        this.mNoResize = null;
        this.mCanResize = null;
        this.mNoResizeDescription = null;
    }
}
