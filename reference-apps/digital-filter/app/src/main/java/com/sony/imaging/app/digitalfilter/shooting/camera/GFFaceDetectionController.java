package com.sony.imaging.app.digitalfilter.shooting.camera;

import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.base.shooting.camera.FaceDetectionController;
import com.sony.imaging.app.base.shooting.camera.NDfilterController;
import java.util.List;

/* loaded from: classes.dex */
public class GFFaceDetectionController extends FaceDetectionController {
    @Override // com.sony.imaging.app.base.shooting.camera.FaceDetectionController, com.sony.imaging.app.base.menu.IController
    public List<String> getSupportedValue(String tag) {
        if (6 > CameraSetting.getPfApiVersion() || NDfilterController.getInstance().getSupportedValueList() == null) {
            return super.getSupportedValue(tag);
        }
        return null;
    }
}
