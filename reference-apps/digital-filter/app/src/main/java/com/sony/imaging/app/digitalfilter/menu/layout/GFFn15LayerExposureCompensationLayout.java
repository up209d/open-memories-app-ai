package com.sony.imaging.app.digitalfilter.menu.layout;

import com.sony.imaging.app.base.menu.IController;
import com.sony.imaging.app.base.menu.layout.Fn15LayerAbsLayout;
import com.sony.imaging.app.base.menu.layout.Fn15LayerExposureCompensationLayout;
import com.sony.imaging.app.base.shooting.camera.ExposureCompensationController;
import com.sony.imaging.app.digitalfilter.R;
import com.sony.imaging.app.digitalfilter.common.GFLinkUtil;
import com.sony.imaging.app.digitalfilter.shooting.GFEffectParameters;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFEEAreaController;
import java.util.ArrayList;

/* loaded from: classes.dex */
public class GFFn15LayerExposureCompensationLayout extends Fn15LayerExposureCompensationLayout {
    public static final String MENU_ID = "ID_GFFN15LAYEREXPOSURECOMPENSATIONLAYOUT";
    private static boolean isCanceled = false;

    @Override // com.sony.imaging.app.base.menu.layout.Fn15LayerCompensationLayout, com.sony.imaging.app.base.menu.layout.Fn15LayerAbsLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        isCanceled = false;
        int action = this.data.getInt(Fn15LayerAbsLayout.BUNDLE_KEY_ACTION);
        if (action == 3 || action == 4) {
            if (action == 3) {
                this.data.putInt(Fn15LayerAbsLayout.BUNDLE_KEY_ACTION, 1);
            } else {
                this.data.putInt(Fn15LayerAbsLayout.BUNDLE_KEY_ACTION, 2);
            }
        }
        super.onResume();
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout
    public void closeLayout() {
        if (!isCanceled) {
            try {
                String value = ExposureCompensationController.getInstance().getValue("ExposureCompensation");
                GFEffectParameters.Parameters params = GFEffectParameters.getInstance().getParameters();
                boolean isLand = GFEEAreaController.getInstance().isLand();
                boolean isSky = GFEEAreaController.getInstance().isSky();
                boolean isLayer3 = GFEEAreaController.getInstance().isLayer3();
                int settingLayer = 0;
                if (isSky) {
                    settingLayer = 1;
                } else if (isLayer3) {
                    settingLayer = 2;
                }
                params.setExposureComp(settingLayer, value);
                GFLinkUtil.getInstance().saveLinkedExpComp(value, settingLayer, isLand, isSky, isLayer3);
            } catch (IController.NotSupportedException e) {
                e.printStackTrace();
            }
        }
        super.closeLayout();
    }

    @Override // com.sony.imaging.app.base.menu.layout.Fn15LayerCompensationLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public void onDestroyView() {
        super.onDestroyView();
        System.gc();
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout
    protected boolean isAELCancel() {
        return false;
    }

    @Override // com.sony.imaging.app.base.menu.layout.Fn15LayerCompensationLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout
    protected void setFunctionGuideResources(ArrayList<Object> guideResources) {
        int titleId = R.string.STRID_FUNC_DF_EXPOSURECOMP_LAND;
        if (GFEEAreaController.getInstance().isSky()) {
            titleId = R.string.STRID_FUNC_DF_EXPOSURECOMP_SKY;
        } else if (GFEEAreaController.getInstance().isLayer3()) {
            titleId = R.string.STRID_FUNC_DF_EXPOSURECOMP_LAYER3;
        }
        guideResources.add(getText(titleId));
        guideResources.add(getText(android.R.string.ext_media_unmounting_notification_message));
        guideResources.add(true);
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedSK1Key() {
        return pushedMenuKey();
    }

    @Override // com.sony.imaging.app.base.menu.layout.Fn15LayerCompensationLayout, com.sony.imaging.app.base.menu.layout.Fn15LayerAbsLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMenuKey() {
        isCanceled = true;
        return super.pushedMenuKey();
    }

    @Override // com.sony.imaging.app.base.menu.layout.Fn15LayerCompensationLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedSubDialNext() {
        return super.turnedMainDialNext();
    }

    @Override // com.sony.imaging.app.base.menu.layout.Fn15LayerCompensationLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedSubDialPrev() {
        return super.turnedMainDialPrev();
    }
}
