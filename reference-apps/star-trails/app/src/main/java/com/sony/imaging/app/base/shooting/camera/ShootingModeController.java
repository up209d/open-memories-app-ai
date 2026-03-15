package com.sony.imaging.app.base.shooting.camera;

import com.sony.imaging.app.util.Environment;
import java.util.List;

/* loaded from: classes.dex */
public abstract class ShootingModeController extends AbstractController {
    public abstract List<String> getSupportedValue(String str, int i);

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getSupportedValue(String tag) {
        List<String> list = getSupportedValue(tag, 1);
        if (isMovieMode()) {
            List<String> movieList = getSupportedValue(tag, 2);
            if (list != null && list.size() != 0) {
                if (movieList != null && movieList.size() != 0) {
                    for (String mode : movieList) {
                        if (list.indexOf(mode) == -1) {
                            list.add(mode);
                        }
                    }
                    return list;
                }
                return list;
            }
            return movieList;
        }
        return list;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.AbstractController, com.sony.imaging.app.base.menu.IController
    public int getCautionIndex(String itemId) {
        if (!isMovieMode()) {
            return 0;
        }
        List<String> movieList = getSupportedValue(itemId, 2);
        if (movieList != null && movieList.size() != 0 && movieList.indexOf(itemId) != -1) {
            return 0;
        }
        return -1;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean isMovieMode() {
        if (!Environment.isMovieAPISupported() || 2 != CameraSetting.getInstance().getCurrentMode()) {
            return false;
        }
        return true;
    }
}
