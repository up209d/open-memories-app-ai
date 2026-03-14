package com.sony.imaging.app.base.menu.layout;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.sony.imaging.app.base.R;
import com.sony.imaging.app.base.menu.layout.Fn15LayerMenuLayout;
import com.sony.imaging.app.base.shooting.camera.CreativeStyleController;
import com.sony.imaging.app.base.shooting.widget.CreativeStyleOptionView;

/* loaded from: classes.dex */
public class Fn15LayerCreativeStyleLayout extends Fn15LayerMenuLayout {
    public static final String MENU_ID = "ID_FN15LAYERCREATIVESTYLELAYOUT";
    protected CreativeStyleOptionView mContrast;
    protected CreativeStyleOptionView mSaturation;
    protected CreativeStyleOptionView mSharpness;

    @Override // com.sony.imaging.app.base.menu.layout.Fn15LayerMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        this.mContrast = (CreativeStyleOptionView) view.findViewById(R.id.optionContrast);
        this.mSaturation = (CreativeStyleOptionView) view.findViewById(R.id.optionSaturation);
        this.mSharpness = (CreativeStyleOptionView) view.findViewById(R.id.optionSharpness);
        return view;
    }

    @Override // com.sony.imaging.app.base.menu.layout.Fn15LayerMenuLayout
    protected int getLayoutID() {
        return R.layout.menu_fn15layer_creativestyle;
    }

    @Override // com.sony.imaging.app.base.menu.layout.Fn15LayerMenuLayout, com.sony.imaging.app.base.menu.layout.Fn15LayerAbsLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        CreativeStyleController ctrl = CreativeStyleController.getInstance();
        this.mContrast.init(CreativeStyleController.CONTRAST, ctrl.getSupportedValue(CreativeStyleController.CONTRAST));
        this.mSaturation.init(CreativeStyleController.SATURATION, ctrl.getSupportedValue(CreativeStyleController.SATURATION));
        this.mSharpness.init(CreativeStyleController.SHARPNESS, ctrl.getSupportedValue(CreativeStyleController.SHARPNESS));
        String creativeStyle = ctrl.getValue(CreativeStyleController.CREATIVESTYLE);
        CreativeStyleController.CreativeStyleOptionsAvailable optionAvailable = (CreativeStyleController.CreativeStyleOptionsAvailable) ctrl.getOptionAvailable(creativeStyle);
        this.mContrast.setAvailable(optionAvailable.mContrast);
        this.mSaturation.setAvailable(optionAvailable.mSaturation);
        this.mSharpness.setAvailable(optionAvailable.mSharpness);
        CreativeStyleController.CreativeStyleOptions options = (CreativeStyleController.CreativeStyleOptions) ctrl.getDetailValue(creativeStyle);
        this.mContrast.setCurrentValue(options.contrast);
        this.mSaturation.setCurrentValue(options.saturation);
        this.mSharpness.setCurrentValue(options.sharpness);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.menu.layout.Fn15LayerMenuLayout
    public void onMainBeltItemSelected(BeltView arg0, View arg1, int index, long id) {
        Fn15LayerMenuLayout.AdapterItem item = (Fn15LayerMenuLayout.AdapterItem) arg0.getItemAtPosition(index);
        CreativeStyleController ctrl = CreativeStyleController.getInstance();
        String value = this.mService.getMenuItemValue(item.itemId);
        CreativeStyleController.CreativeStyleOptionsAvailable optionAvailable = (CreativeStyleController.CreativeStyleOptionsAvailable) ctrl.getOptionAvailable(value);
        this.mContrast.setAvailable(optionAvailable.mContrast);
        this.mSaturation.setAvailable(optionAvailable.mSaturation);
        this.mSharpness.setAvailable(optionAvailable.mSharpness);
        CreativeStyleController.CreativeStyleOptions optionValue = (CreativeStyleController.CreativeStyleOptions) ctrl.getDetailValue(value);
        this.mContrast.setCurrentValue(optionValue.contrast);
        this.mSaturation.setCurrentValue(optionValue.saturation);
        this.mSharpness.setCurrentValue(optionValue.sharpness);
    }

    @Override // com.sony.imaging.app.base.menu.layout.Fn15LayerMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public void onDestroyView() {
        this.mContrast = null;
        this.mSaturation = null;
        this.mSharpness = null;
        super.onDestroyView();
    }
}
