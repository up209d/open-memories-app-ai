package com.sony.imaging.app.base.playback.layout;

import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.sony.imaging.app.base.R;
import com.sony.imaging.app.base.backup.BackupReader;
import com.sony.imaging.app.base.playback.LogHelper;
import com.sony.imaging.app.base.playback.contents.ContentInfo;
import com.sony.imaging.app.base.playback.contents.ContentsManager;
import com.sony.imaging.app.fw.RunStatus;
import com.sony.imaging.app.util.Environment;
import com.sony.imaging.app.util.PTag;
import com.sony.scalar.graphics.OptimizedImage;
import com.sony.scalar.widget.OptimizedImageView;
import java.util.ArrayList;
import java.util.Iterator;

/* loaded from: classes.dex */
public abstract class OptimizedImageLayoutBase extends PlayLayoutBase {
    private static final String MSG_FAILED_TO_GET_CONTENT = "failed to get content";
    private static final String MSG_NEXT_CONTENTS = "nextContents";
    private static final String MSG_OPT_IMAGE_ON_DISPLAY = "OptimizedImageView onDisplay ";
    private static final String MSG_OPT_IMG_VIEW_NOT_FOUND = "OptimizedImageView not found";
    private static final String MSG_PREVIOUS_CONTENTS = "previousContents";
    private static final String MSG_VIEW_NOT_READY = "updateOptimizedImage view not ready";
    private static final int VIEW_HEIGHT_ON_ERR = 480;
    private static final int VIEW_WIDTH_ON_ERR = 640;
    protected boolean mMuteOptimizedImageViewOnPause = true;
    protected OptimizedImageView mOptImgView = null;
    protected ArrayList<View> viewOkList = null;
    protected ArrayList<View> viewErrList = null;

    /* JADX INFO: Access modifiers changed from: protected */
    public OptimizedImageView getOptimizedImageView() {
        return this.mOptImgView;
    }

    protected int getOptimizedImageViewResourceId() {
        return R.id.imageSingle;
    }

    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        int[] imageErrIdList;
        int[] imageOkIdList;
        this.mMuteOptimizedImageViewOnPause = true;
        View view = super.onCreateView(inflater, container, savedInstanceState);
        this.mOptImgView = view.findViewById(getOptimizedImageViewResourceId());
        this.mOptImgView.setOnDisplayEventListener(new OptimizedImageView.onDisplayEventListener() { // from class: com.sony.imaging.app.base.playback.layout.OptimizedImageLayoutBase.1
            public void onDisplay(int errCd) {
                PTag.end(OptimizedImageLayoutBase.MSG_OPT_IMAGE_ON_DISPLAY);
                Log.d(OptimizedImageLayoutBase.this.TAG, LogHelper.getScratchBuilder(OptimizedImageLayoutBase.MSG_OPT_IMAGE_ON_DISPLAY).append(errCd).toString());
            }
        });
        if (this.viewOkList == null && (imageOkIdList = getImageOkLayoutResourceId()) != null) {
            this.viewOkList = new ArrayList<>(imageOkIdList.length);
            for (int item : imageOkIdList) {
                View child = view.findViewById(item);
                if (child != null) {
                    this.viewOkList.add(child);
                }
            }
        }
        if (this.viewErrList == null && (imageErrIdList = getImageErrLayoutResourceId()) != null) {
            this.viewErrList = new ArrayList<>(imageErrIdList.length);
            for (int item2 : imageErrIdList) {
                View child2 = view.findViewById(item2);
                if (child2 != null) {
                    this.viewErrList.add(child2);
                }
            }
        }
        return view;
    }

    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public void onDestroyView() {
        this.mOptImgView = null;
        this.viewOkList = null;
        this.viewErrList = null;
        super.onDestroyView();
    }

    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        updateDisplay();
    }

    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        super.onPause();
        if (this.mOptImgView != null && this.mMuteOptimizedImageViewOnPause && 4 == RunStatus.getStatus()) {
            this.mOptImgView.setOptimizedImage((OptimizedImage) null);
        }
    }

    public void invalidate() {
        updateDisplay();
    }

    protected int[] getImageOkLayoutResourceId() {
        return null;
    }

    protected int[] getImageErrLayoutResourceId() {
        return null;
    }

    public void updateOptimizedImage(OptimizedImage image) {
        if (Environment.DEVICE_TYPE == 4 || Environment.DEVICE_TYPE == 3) {
            View containerView = getView();
            if (containerView == null) {
                Log.w(this.TAG, MSG_VIEW_NOT_READY);
                return;
            }
            OptimizedImageView imgView = this.mOptImgView;
            if (imgView == null) {
                Log.w(this.TAG, MSG_OPT_IMG_VIEW_NOT_FOUND);
                return;
            }
            imgView.setDisplayType(getDisplayType());
            ContentsManager mgr = ContentsManager.getInstance();
            if (image != null) {
                updateVPicDisplay(imgView, mgr);
                updateOptimizedImageLayoutParam(imgView, mgr);
            } else if (isErrorContent(image)) {
                updateOptimizedImageLayoutOnDecodeErr(imgView, mgr);
            }
            imgView.setOptimizedImage(image);
            if (isErrorContent(image)) {
                if (this.viewOkList != null) {
                    Iterator i$ = this.viewOkList.iterator();
                    while (i$.hasNext()) {
                        View view = i$.next();
                        view.setVisibility(8);
                    }
                }
                if (this.viewErrList != null) {
                    Iterator i$2 = this.viewErrList.iterator();
                    while (i$2.hasNext()) {
                        View view2 = i$2.next();
                        view2.setVisibility(0);
                    }
                    return;
                }
                return;
            }
            if (this.viewOkList != null) {
                Iterator i$3 = this.viewOkList.iterator();
                while (i$3.hasNext()) {
                    View view3 = i$3.next();
                    view3.setVisibility(0);
                }
            }
            if (this.viewErrList != null) {
                Iterator i$4 = this.viewErrList.iterator();
                while (i$4.hasNext()) {
                    View view4 = i$4.next();
                    view4.setVisibility(8);
                }
            }
        }
    }

    protected boolean isErrorContent(OptimizedImage image) {
        return image == null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void updateDisplay() {
        ContentsManager mgr = ContentsManager.getInstance();
        OptimizedImage image = mgr.getOptimizedImage(mgr.getContentsId(), getImageType());
        updateOptimizedImage(image);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public int getImageType() {
        return 2;
    }

    protected OptimizedImageView.DisplayType getDisplayType() {
        return OptimizedImageView.DisplayType.DISPLAY_TYPE_CENTER_INNER;
    }

    protected void updateVPicDisplay(OptimizedImageView img, ContentsManager mgr) {
        if (BackupReader.getVPicDisplay() == BackupReader.VPicDisplay.PORTRAIT) {
            ContentInfo info = mgr.getContentInfo(mgr.getContentsId());
            if (info == null) {
                Log.w(this.TAG, MSG_FAILED_TO_GET_CONTENT);
                img.setDisplayRotationAngle(0);
                return;
            }
            int angle = info.getInt("Orientation");
            switch (angle) {
                case 3:
                    img.setDisplayRotationAngle(180);
                    return;
                case 4:
                case 5:
                case 7:
                default:
                    img.setDisplayRotationAngle(0);
                    return;
                case 6:
                    img.setDisplayRotationAngle(90);
                    return;
                case 8:
                    img.setDisplayRotationAngle(270);
                    return;
            }
        }
        img.setDisplayRotationAngle(0);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void updateOptimizedImageLayoutParam(OptimizedImageView img, ContentsManager mgr) {
    }

    protected void updateOptimizedImageLayoutOnDecodeErr(OptimizedImageView imgView, ContentsManager mgr) {
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) imgView.getLayoutParams();
        params.leftMargin = 0;
        params.topMargin = 0;
        params.width = 640;
        params.height = 480;
        Point p = new Point(params.leftMargin + (params.width >> 1), params.topMargin + (params.height >> 1));
        imgView.setPivot(p);
        imgView.setLayoutParams(params);
    }

    public boolean previousContents() {
        PTag.start(MSG_PREVIOUS_CONTENTS);
        ContentsManager mgr = ContentsManager.getInstance();
        boolean isQueryDone = mgr.isInitialQueryDone();
        if (!isQueryDone) {
            return false;
        }
        if (!mgr.moveToPrevious()) {
            mgr.moveToLast();
        }
        invalidate();
        return true;
    }

    public boolean nextContents() {
        PTag.start(MSG_NEXT_CONTENTS);
        ContentsManager mgr = ContentsManager.getInstance();
        boolean isQueryDone = mgr.isInitialQueryDone();
        if (!isQueryDone) {
            return false;
        }
        if (!mgr.moveToNext()) {
            mgr.moveToFirst();
        }
        invalidate();
        return true;
    }
}
