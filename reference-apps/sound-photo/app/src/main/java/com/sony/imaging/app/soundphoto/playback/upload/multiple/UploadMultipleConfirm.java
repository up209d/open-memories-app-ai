package com.sony.imaging.app.soundphoto.playback.upload.multiple;

import android.net.Uri;
import com.sony.imaging.app.base.playback.base.editor.Confirm;
import com.sony.imaging.app.base.playback.contents.ContentInfo;
import com.sony.imaging.app.base.playback.contents.ContentsManager;
import com.sony.imaging.app.base.playback.contents.EditService;
import com.sony.imaging.app.soundphoto.playback.upload.util.DirectUploadUtilManager;
import com.sony.imaging.app.soundphoto.util.AppLog;
import com.sony.imaging.app.soundphoto.util.SPConstants;
import java.util.ArrayList;

/* loaded from: classes.dex */
public class UploadMultipleConfirm extends Confirm {
    private static final String PREFIXFILE = "file://";

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.playback.base.editor.Confirm
    public EditService getEditService() {
        return super.getEditService();
    }

    @Override // com.sony.imaging.app.base.playback.base.editor.Confirm, com.sony.imaging.app.base.playback.layout.EditorConfirmLayoutBase.OnConfirmListener
    public void onOk() {
        new ArrayList();
        ArrayList<Uri> uriList = new ArrayList<>();
        ContentsManager mgr = ContentsManager.getInstance();
        EditService service = getEditService();
        AppLog.info(this.TAG, "mgr.getContentsCount() : " + mgr.getContentsCount() + ", mgr.getContentsTotalCount() : " + mgr.getContentsTotalCount() + ", mgr.getContentsTotalPosition() : " + mgr.getContentsTotalPosition() + ", service.countSelected():" + service.countSelected());
        AppLog.info(this.TAG, "mgr.getGroupCount() : " + mgr.getGroupCount());
        for (int groupCount = 0; groupCount < mgr.getGroupCount(); groupCount++) {
            AppLog.info(this.TAG, "groupCount: " + groupCount);
            mgr.moveGroupTo(groupCount);
            int selectNum = mgr.getContentsCount();
            for (int i = 0; i < selectNum; i++) {
                AppLog.info(this.TAG, "i: " + i);
                mgr.moveTo(i);
                int file = mgr.getContentsPosition();
                if (service.isChecked(file)) {
                    ContentInfo info = mgr.getContentInfo(mgr.getContentsIdAt(i));
                    String dirName = info.getString("DCF_TBLDirName");
                    String fileName = info.getString("DCF_TBLFileName");
                    String filePath = SPConstants.ROOT_FOLDER_PATH + dirName + SPConstants.FILE_SEPARATER + fileName;
                    AppLog.info(this.TAG, "filePath: " + filePath);
                    uriList.add(Uri.parse(PREFIXFILE + filePath));
                    AppLog.info(this.TAG, "i: " + i + "mgr.getContentsIdAt(i): " + mgr.getContentsIdAt(i) + ", filePath : " + filePath);
                }
            }
        }
        AppLog.info(this.TAG, "DirectUploadUtilManager call ");
        DirectUploadUtilManager.getInstance().launchMulti(getActivity(), uriList, null);
    }
}
