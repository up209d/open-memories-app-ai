package com.sony.imaging.app.base.menu.layout.list;

import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.sony.imaging.app.base.R;
import com.sony.imaging.app.base.menu.layout.list.AbstractListMenuLayout;
import com.sony.imaging.app.fw.Layout;

/* loaded from: classes.dex */
public class MenuGuideLayout extends Layout {
    public static final String TAG = MenuGuideLayout.class.getSimpleName();
    private AbstractListMenuLayout.MenuActionListener listener = null;
    private int titleStringId = -1;
    private int contentStringId = -1;
    private boolean delayedItemSet = false;
    private Handler handler = new Handler();

    @Override // com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = obtainViewFromPool(getGuideLayoutId());
        return view;
    }

    protected int getGuideLayoutId() {
        return R.layout.layout_listmenu_guide;
    }

    protected int getGuideTitleViewId() {
        return R.id.guide_title;
    }

    protected int getGuideContentViewId() {
        return R.id.guide_contents;
    }

    protected int getGuideForegroundWrapperViewId() {
        return R.id.guide_wrapper;
    }

    @Override // com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        if (this.titleStringId == -1 || this.contentStringId == -1) {
            this.delayedItemSet = true;
        } else {
            displayGuideContent();
        }
        View view = getView().findViewById(getGuideForegroundWrapperViewId());
        view.setOnClickListener(new View.OnClickListener() { // from class: com.sony.imaging.app.base.menu.layout.list.MenuGuideLayout.1
            @Override // android.view.View.OnClickListener
            public void onClick(View arg0) {
                if (MenuGuideLayout.this.listener != null) {
                    MenuGuideLayout.this.listener.onGuideClosed();
                }
            }
        });
        super.onResume();
    }

    @Override // com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        this.listener = null;
        super.onPause();
    }

    public void registerMenuActionListener(AbstractListMenuLayout.MenuActionListener listener) {
        this.listener = listener;
    }

    public void setGuideLayoutData(int titleStringId, int contentStringId) {
        this.titleStringId = titleStringId;
        this.contentStringId = contentStringId;
        if (this.delayedItemSet) {
            this.handler.post(new Runnable() { // from class: com.sony.imaging.app.base.menu.layout.list.MenuGuideLayout.2
                @Override // java.lang.Runnable
                public void run() {
                    MenuGuideLayout.this.displayGuideContent();
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void displayGuideContent() {
        int guideTitleViewId = getGuideTitleViewId();
        int guideContentViewId = getGuideContentViewId();
        if (guideTitleViewId > 0 && guideContentViewId > 0) {
            ((TextView) getView().findViewById(guideTitleViewId)).setText(this.titleStringId);
            ((TextView) getView().findViewById(guideContentViewId)).setText(this.contentStringId);
        }
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyDown(int keyCode, KeyEvent event) {
        if (this.listener != null) {
            this.listener.onGuideClosed();
            return 0;
        }
        return 0;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyUp(int keyCode, KeyEvent event) {
        if (this.listener != null) {
            this.listener.onGuideClosed();
            return 0;
        }
        return 0;
    }
}
