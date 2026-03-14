package com.sony.imaging.app.photoretouch.playback.layout;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import com.sony.imaging.app.base.common.DisplayModeObserver;
import com.sony.imaging.app.base.common.widget.FooterGuide;
import com.sony.imaging.app.base.common.widget.FooterGuideDataResId;
import com.sony.imaging.app.base.playback.contents.ContentInfo;
import com.sony.imaging.app.base.playback.contents.ContentsManager;
import com.sony.imaging.app.base.playback.layout.BrowserIndexLayout;
import com.sony.imaging.app.photoretouch.Constant;
import com.sony.imaging.app.photoretouch.R;
import com.sony.imaging.app.photoretouch.common.ImageEditor;
import com.sony.imaging.app.photoretouch.playback.PhotoRetouchPlay;
import com.sony.scalar.graphics.OptimizedImage;
import com.sony.scalar.hardware.avio.DisplayManager;
import com.sony.scalar.sysutil.ScalarProperties;
import java.util.List;

/* loaded from: classes.dex */
public class PhotoRetouchIndexLayout extends BrowserIndexLayout {
    private final String TAG = PhotoRetouchIndexLayout.class.getSimpleName();

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.FakeFragment
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override // com.sony.imaging.app.base.playback.layout.BrowserIndexLayout, com.sony.imaging.app.base.playback.layout.IndexLayoutGridViewBase, com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        Log.e(this.TAG, "=====onCreateView...PhotoRetouchIndexLayout");
        this.mView = obtainViewFromPool(getLayoutResource());
        FooterGuide mFooterGuide = (FooterGuide) this.mView.findViewById(R.id.pr_footer_guide);
        Log.e(this.TAG, "=====onCreateView...mFooterGuide:" + mFooterGuide);
        mFooterGuide.setData(new FooterGuideDataResId(getActivity(), android.R.string.hour, android.R.string.harmful_app_warning_uninstall));
        return this.mView;
    }

    @Override // com.sony.imaging.app.base.playback.layout.BrowserIndexLayout, com.sony.imaging.app.base.playback.layout.PlayLayoutBase
    protected int getLayoutResource() {
        Log.e(this.TAG, "=====onCreateView...getLayoutResource");
        DisplayManager.DeviceInfo info = DisplayModeObserver.getInstance().getDeviceInfo(0);
        if (2 == info.aspect) {
            Log.e(this.TAG, "=====onCreateView... getLayoutResource>>>>>>>>>>>>1");
            return R.layout.photoretouch_pb_layout_browser_indexpb_image_4x3;
        }
        Log.e(this.TAG, "=====onCreateView...getLayoutResource>>>>>>>>>>>>2");
        return R.layout.photoretouch_pb_layout_browser_indexpb_image_3x3;
    }

    @Override // com.sony.imaging.app.base.playback.layout.IndexLayoutGridViewBase, com.sony.imaging.app.base.playback.layout.IndexLayoutBase, com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        Log.d(this.TAG, "=====onResume...isSpaceAvailableInMemoryCard= " + ImageEditor.isSpaceAvailableInMemoryCard());
        if (!ImageEditor.isSpaceAvailableInMemoryCard()) {
            showNoMemoryMessage();
        } else if (PhotoRetouchPlay.sIsPhotoRetouchAppStarted) {
            openLayout(Constant.ID_MESSAGEAPPSTART);
            PhotoRetouchPlay.sIsPhotoRetouchAppStarted = false;
        }
        setKeyBeepPattern(0);
    }

    @Override // com.sony.imaging.app.base.playback.layout.IndexLayoutGridViewBase, com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public void onDestroyView() {
        Log.d(this.TAG, "=====onPause...PhotoRetouchIndexLayout");
        closeAlertLayout();
        super.onDestroyView();
    }

    private void showNoMemoryMessage() {
        String message = getActivity().getResources().getString(R.string.STRID_AMC_STR_01956);
        Bundle bundle = new Bundle();
        bundle.putString(Constant.ALERT_MESSAGE, message);
        openLayout(Constant.ID_MESSAGEALERT, bundle);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.playback.layout.IndexLayoutBase
    public void highlightedItemChanged(int itemKind) {
        super.highlightedItemChanged(itemKind);
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedSK1Key() {
        getActivity().finish();
        return 1;
    }

    @Override // com.sony.imaging.app.base.playback.layout.BrowserIndexLayout, com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMenuKey() {
        getActivity().finish();
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int attachedLens() {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int detachedLens() {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedPlayBackKey() {
        return -1;
    }

    @Override // com.sony.imaging.app.base.playback.layout.IndexLayoutGridViewBase, com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedCenterKey() {
        Log.d(this.TAG, "=============center key...message layout= " + getLayout(Constant.ID_MESSAGEAPPSTART).getView());
        if (getLayout(Constant.ID_MESSAGEAPPSTART).getView() != null) {
            return 1;
        }
        return super.pushedCenterKey();
    }

    @Override // com.sony.imaging.app.base.playback.layout.BrowserIndexLayout, com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedPbZoomFuncPlus() {
        Log.d("", "Index PB pushedPbZoomFuncPlus");
        return -1;
    }

    @Override // com.sony.imaging.app.base.playback.layout.BrowserIndexLayout, android.widget.AdapterView.OnItemClickListener
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        super.onItemClick(arg0, arg1, arg2, arg3);
    }

    @Override // com.sony.imaging.app.base.playback.layout.BrowserIndexLayout, com.sony.imaging.app.base.playback.layout.IndexLayoutGridViewBase
    protected boolean onContentEntered(int position) {
        ImageEditor.updateSpaceAvailableInMemoryCard();
        if (!ImageEditor.isSpaceAvailableInMemoryCard()) {
            showNoMemoryMessage();
            return true;
        }
        ContentsManager mgr = ContentsManager.getInstance();
        OptimizedImage mainOptimizedImage = mgr.getOptimizedImageWithoutCache(mgr.getContentsId(), 1);
        int optImgHeight = 0;
        int optImgWidth = 0;
        boolean imageSupported = false;
        if (mainOptimizedImage != null) {
            optImgHeight = mainOptimizedImage.getHeight();
            optImgWidth = mainOptimizedImage.getWidth();
            mainOptimizedImage.release();
            imageSupported = isSupportedImage(optImgWidth, optImgHeight);
        }
        Log.d(this.TAG, "====height= " + optImgHeight);
        Log.d(this.TAG, "====width= " + optImgWidth);
        if (imageSupported) {
            return transitionSinglePb();
        }
        openLayout(Constant.ID_MESSAGEUNSUPPORTEDFILE);
        return true;
    }

    private boolean isSupportedImage(float optImgWidth, float optImgHeight) {
        ContentsManager contentManager = ContentsManager.getInstance();
        ContentInfo info = contentManager.getContentInfo(contentManager.getContentsId());
        int contentType = info.getInt("ContentType");
        Log.d("YES", "====contentType " + contentType);
        List<ScalarProperties.PictureSize> list = ScalarProperties.getSupportedPictureSizes();
        ScalarProperties.PictureSize large_32 = list.get(0);
        ScalarProperties.PictureSize large_169 = list.get(3);
        ScalarProperties.PictureSize large_43 = list.get(6);
        ScalarProperties.PictureSize large_11 = list.get(9);
        Log.d(this.TAG, "===supported 3:2 height= " + large_32.height + "\t optimizedImage height= " + optImgHeight);
        Log.d(this.TAG, "===supported 3:2 width= " + large_32.width + "\t optimizedImage width= " + optImgHeight);
        Log.d(this.TAG, "===supported 16:9 height= " + large_169.height + "\t optimizedImage height= " + optImgHeight);
        Log.d(this.TAG, "===supported 16:9 width= " + large_169.width + "\t optimizedImage width= " + optImgHeight);
        if (contentType == 5) {
            float aspectValue = optImgWidth / optImgHeight;
            if (aspectValue >= 1.48f && aspectValue <= 1.52f && optImgHeight <= large_32.height && optImgWidth <= large_32.width) {
                return true;
            }
            if (aspectValue >= 1.75f && aspectValue <= 1.8f && optImgHeight <= large_169.height && optImgWidth <= large_169.width) {
                return true;
            }
            if (aspectValue >= 1.31f && aspectValue <= 1.35f) {
                if (list.size() > 6 && optImgWidth <= large_43.width && optImgHeight <= large_43.height) {
                    return true;
                }
                return false;
            }
            if (aspectValue >= 0.98f && aspectValue <= 1.02f) {
                if (list.size() > 6 && optImgWidth <= large_11.width && optImgHeight <= large_11.height) {
                    return true;
                }
                return false;
            }
            Log.e(this.TAG, "===Image Aspect Ratio not supported");
        }
        return false;
    }

    @Override // com.sony.imaging.app.fw.Layout
    public void closeLayout() {
        closeAlertLayout();
        super.closeLayout();
    }

    private void closeAlertLayout() {
        if (getLayout(Constant.ID_MESSAGEALERT).getView() != null) {
            getLayout(Constant.ID_MESSAGEALERT).closeLayout();
        } else if (getLayout(Constant.ID_MESSAGEUNSUPPORTEDFILE).getView() != null) {
            getLayout(Constant.ID_MESSAGEUNSUPPORTEDFILE).closeLayout();
        } else if (getLayout(Constant.ID_MESSAGEAPPSTART).getView() != null) {
            getLayout(Constant.ID_MESSAGEAPPSTART).closeLayout();
        }
    }
}
