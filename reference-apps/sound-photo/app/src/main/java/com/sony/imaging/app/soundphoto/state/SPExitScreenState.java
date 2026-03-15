package com.sony.imaging.app.soundphoto.state;

import com.sony.imaging.app.base.BaseApp;
import com.sony.imaging.app.base.common.ExitScreenState;
import com.sony.imaging.app.soundphoto.database.DataBaseOperations;
import com.sony.imaging.app.soundphoto.util.AppLog;
import com.sony.imaging.app.soundphoto.util.SPUtil;

/* loaded from: classes.dex */
public class SPExitScreenState extends ExitScreenState {
    @Override // com.sony.imaging.app.base.common.ExitScreenState, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        AppLog.info("SPExitScreenState", "TESTTAG SPExitScreenState onPause  == ");
        closeLayout(ExitScreenProcessingLayout.TAG);
        super.onPause();
    }

    @Override // com.sony.imaging.app.base.common.ExitScreenState, com.sony.imaging.app.base.common.layout.ExitScreenLayout.Callback
    public void onFinish() {
        AppLog.info("SPExitScreenState", "TESTTAG onFinish");
        if (!SPUtil.getInstance().isRecoverDone() && SPUtil.getInstance().getMediaCardStatus()) {
            String fileClause = DataBaseOperations.getInstance().getTotalFileFromLocalDB();
            AppLog.info("SPExitScreenState", "TESTTAG fileClause==" + fileClause);
            if (fileClause == null || fileClause.length() < 1) {
                closeAllLayout();
                return;
            }
            openLayout(ExitScreenProcessingLayout.TAG);
            ExitScreenProcessingLayout layout = (ExitScreenProcessingLayout) getLayout(ExitScreenProcessingLayout.TAG);
            layout.setCallback(this);
            return;
        }
        closeAllLayout();
    }

    private void closeAllLayout() {
        closeLayout("ExitScreen");
        closeLayout(ExitScreenProcessingLayout.TAG);
        BaseApp app = (BaseApp) getActivity();
        removeState();
        if (app != null) {
            app.finish(false);
        }
    }
}
