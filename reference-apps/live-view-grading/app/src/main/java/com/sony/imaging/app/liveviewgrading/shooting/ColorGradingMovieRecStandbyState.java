package com.sony.imaging.app.liveviewgrading.shooting;

import android.os.Bundle;
import android.os.Message;
import android.view.View;
import com.sony.imaging.app.base.menu.MenuState;
import com.sony.imaging.app.base.shooting.movie.MovieRecStandbyState;
import com.sony.imaging.app.fw.ICustomKey;
import com.sony.imaging.app.liveviewgrading.menu.controller.ColorGradingController;
import com.sony.imaging.app.liveviewgrading.shooting.layout.ColorGradingStableLayout;

/* loaded from: classes.dex */
public class ColorGradingMovieRecStandbyState extends MovieRecStandbyState {
    @Override // com.sony.imaging.app.base.shooting.movie.MovieRecStandbyState, com.sony.imaging.app.base.shooting.movie.MovieStateBase, com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        if (ColorGradingController.getInstance().isPlayBackKeyPressedOnMenu()) {
            openLayout("ID_COLORGRADINGPLAYBACKEXITSCREEN");
        } else {
            closeLayout("ID_COLORGRADINGPLAYBACKEXITSCREEN");
        }
    }

    @Override // com.sony.imaging.app.base.shooting.movie.MovieRecStandbyState, com.sony.imaging.app.base.shooting.movie.MovieStateBase, com.sony.imaging.app.base.shooting.StateBase, com.sony.imaging.app.fw.State, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        super.onPause();
        View view = getLayout("ID_COLORGRADINGPLAYBACKEXITSCREEN").getView();
        if (view != null) {
            closeLayout("ID_COLORGRADINGPLAYBACKEXITSCREEN");
        }
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case ColorGradingStableLayout.OPEN_PAYBACK_EXIT_SCREEN /* 111 */:
                openLayout("ID_COLORGRADINGPLAYBACKEXITSCREEN");
                break;
            case ColorGradingStableLayout.OPEN_PARAMETER_ADJUSTMENT_SCREEN /* 1221 */:
                Bundle bundle = new Bundle();
                bundle.putString(MenuState.ITEM_ID, "PresetAdjustmentSettings");
                setNextState(ICustomKey.CATEGORY_MENU, bundle);
                break;
        }
        return super.handleMessage(msg);
    }
}
