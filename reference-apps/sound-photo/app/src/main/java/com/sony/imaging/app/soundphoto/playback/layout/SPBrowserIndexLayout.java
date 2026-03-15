package com.sony.imaging.app.soundphoto.playback.layout;

import android.view.KeyEvent;
import com.sony.imaging.app.base.common.widget.FooterGuide;
import com.sony.imaging.app.base.common.widget.FooterGuideDataResId;
import com.sony.imaging.app.base.playback.contents.ContentsManager;
import com.sony.imaging.app.base.playback.layout.BrowserIndexLayout;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.soundphoto.R;
import com.sony.imaging.app.soundphoto.database.DataBaseOperations;
import com.sony.imaging.app.soundphoto.playback.SPPlayRootContainer;
import com.sony.imaging.app.soundphoto.util.AppLog;
import com.sony.imaging.app.soundphoto.util.SPConstants;
import com.sony.imaging.app.soundphoto.util.SPUtil;
import com.sony.imaging.app.util.NotificationListener;

/* loaded from: classes.dex */
public class SPBrowserIndexLayout extends BrowserIndexLayout implements NotificationListener {
    private static String[] TAGS = {SPPlayRootContainer.ID_DELETE_SOUND_DATA, SPConstants.DELETE_IMAGE_DATABASE_UPDATE};
    com.sony.imaging.app.soundphoto.playback.menu.layout.ITransitToPlaybackMenu mEachPbTrigger;

    public void setEachPbTrigger(com.sony.imaging.app.soundphoto.playback.menu.layout.ITransitToPlaybackMenu trigger) {
        this.mEachPbTrigger = trigger;
    }

    @Override // com.sony.imaging.app.base.playback.layout.IndexLayoutGridViewBase, com.sony.imaging.app.base.playback.layout.IndexLayoutBase, com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        initializeViews();
        highlightedItemChanged(1);
    }

    @Override // com.sony.imaging.app.base.playback.layout.BrowserIndexLayout, com.sony.imaging.app.base.playback.layout.PlayLayoutBase
    protected int getFooterGuideResource() {
        return R.id.cmn_footer_guide;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.playback.layout.IndexLayoutBase
    public void highlightedItemChanged(int itemKind) {
        super.highlightedItemChanged(itemKind);
        updateFooterGuide(itemKind);
    }

    private void updateFooterGuide(int itemKind) {
        AppLog.info(this.TAG, "updateFooterGuide = itemKind= " + itemKind);
        boolean isMenuGuideShow = 2 != itemKind;
        if (isMenuGuideShow) {
            setFooterGuideData(new FooterGuideDataResId(getActivity(), R.string.STRID_FUNC_SOUNDPHOTO_FOOTER_MENU, R.string.STRID_FUNC_SOUNDPHOTO_FOOTER_MENU));
        } else {
            setFooterGuideData(new FooterGuideDataResId(getActivity().getApplicationContext(), android.R.string.httpError));
        }
    }

    private void initializeViews() {
        CameraNotificationManager.getInstance().setNotificationListener(this);
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyDown(int keyCode, KeyEvent event) {
        if (event.getScanCode() == 595) {
            SPUtil.getInstance().setSoundDataDeleteCalled(false);
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override // com.sony.imaging.app.base.playback.layout.IndexLayoutGridViewBase, com.sony.imaging.app.fw.Layout
    public void onReopened() {
        initializeViews();
        super.onReopened();
    }

    @Override // com.sony.imaging.app.base.playback.layout.IndexLayoutGridViewBase, com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        deinitializeViews();
        CameraNotificationManager.getInstance().removeNotificationListener(this);
        super.onPause();
    }

    private void deinitializeViews() {
        AppLog.enter(this.TAG, AppLog.getMethodName());
        FooterGuide guide = (FooterGuide) getView().findViewById(getFooterGuideResource());
        if (guide != null) {
            guide.setData(null);
        }
        AppLog.exit(this.TAG, AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.playback.layout.BrowserIndexLayout, com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMenuKey() {
        return this.mEachPbTrigger.transitionToMenuState() ? 1 : -1;
    }

    @Override // com.sony.imaging.app.util.NotificationListener
    public String[] getTags() {
        return TAGS;
    }

    @Override // com.sony.imaging.app.util.NotificationListener
    public void onNotify(String tag) {
        AppLog.enter(this.TAG, "onNotify tag=  " + tag);
        if (SPPlayRootContainer.ID_DELETE_SOUND_DATA.equals(tag)) {
            SPUtil.getInstance().setSoundDataDeleteCalled(true);
            if (getHighlightedItemKind() == 2) {
                updateView();
                transitionDeleteThis();
            } else {
                pushedDeleteFuncKey();
            }
        } else if (SPConstants.DELETE_IMAGE_DATABASE_UPDATE.equals(tag) && DataBaseOperations.getInstance().getTotalFiles() >= 0) {
            updatePosition();
            updateView();
        }
        AppLog.exit(this.TAG, "onNotify tag=  " + tag);
    }

    private void updatePosition() {
        ContentsManager mgr = ContentsManager.getInstance();
        if (mgr.getContentsId()._id == -1) {
            mgr.moveToFirst();
            if (SPConstants.CURRENT_SELECTED_ID_POSITION == mgr.getContentsTotalCount()) {
                SPConstants.CURRENT_SELECTED_ID_POSITION--;
                mgr.moveToLast();
            } else if (SPConstants.CURRENT_SELECTED_ID_POSITION >= 0) {
                mgr.moveTo(SPConstants.CURRENT_SELECTED_ID_POSITION);
            }
        }
    }
}
