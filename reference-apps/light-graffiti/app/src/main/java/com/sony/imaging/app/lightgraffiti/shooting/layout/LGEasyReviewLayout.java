package com.sony.imaging.app.lightgraffiti.shooting.layout;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.sony.imaging.app.base.common.widget.FooterGuide;
import com.sony.imaging.app.base.common.widget.FooterGuideDataResId;
import com.sony.imaging.app.fw.Layout;
import com.sony.imaging.app.lightgraffiti.R;
import com.sony.imaging.app.lightgraffiti.shooting.LGStateHolder;
import com.sony.imaging.app.lightgraffiti.util.LGConstants;
import com.sony.imaging.app.lightgraffiti.util.LGUtility;

/* loaded from: classes.dex */
public class LGEasyReviewLayout extends Layout {
    private ViewGroup mCurrentView = null;
    private FooterGuide mFooterGuide = null;

    @Override // com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        this.mCurrentView = (ViewGroup) obtainViewFromPool(R.layout.lightgraffiti_layout_review);
        if (this.mCurrentView != null) {
            this.mFooterGuide = (FooterGuide) this.mCurrentView.findViewById(R.id.footer_guide);
        }
        LGStateHolder.getInstance().setShootingEnable(false);
        return this.mCurrentView;
    }

    @Override // com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        setFooterGuide();
        LGUtility.getInstance().outputKikilog(LGConstants.KIKILOG_ID_PREVIEW_SHOWN);
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.FakeFragment
    public void onDestroy() {
        this.mFooterGuide = null;
        super.onDestroy();
    }

    private void setFooterGuide() {
        if (this.mFooterGuide != null) {
            this.mFooterGuide.setData(new FooterGuideDataResId(getActivity(), R.string.STRID_STR_FOOTERGUIDE_CANCEL, R.string.STRID_STR_FOOTERGUIDE_CANCEL_FOR_SK));
        }
    }
}
