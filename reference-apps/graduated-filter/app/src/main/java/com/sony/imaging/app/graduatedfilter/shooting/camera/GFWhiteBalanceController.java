package com.sony.imaging.app.graduatedfilter.shooting.camera;

import android.hardware.Camera;
import android.util.Pair;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.caution.Info;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.base.shooting.camera.WhiteBalanceController;
import com.sony.imaging.app.graduatedfilter.GFBackUpKey;
import com.sony.imaging.app.graduatedfilter.caution.GFInfo;
import com.sony.imaging.app.graduatedfilter.common.GFCommonUtil;
import com.sony.imaging.app.graduatedfilter.menu.layout.GFWhiteBalanceAdjustmentMenuLayout;
import com.sony.imaging.app.graduatedfilter.menu.layout.GFWhiteBalanceMenuLayout;
import com.sony.imaging.app.graduatedfilter.shooting.GFEffectParameters;
import com.sony.imaging.app.util.StringBuilderThreadLocal;
import com.sony.scalar.hardware.CameraEx;
import com.sony.scalar.sysutil.ScalarProperties;
import java.util.List;

/* loaded from: classes.dex */
public class GFWhiteBalanceController extends WhiteBalanceController {
    public static final String SAME = "same";
    private static final int SUPPORTED_YCINFO_WB_GAIN = 16;
    private static GFWhiteBalanceController mInstance = null;
    public static boolean mOpenMenuLayout = false;
    private static boolean needPFDetailValue = false;

    public static GFWhiteBalanceController getInstance() {
        if (mInstance == null) {
            mInstance = new GFWhiteBalanceController();
        }
        return mInstance;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.WhiteBalanceController, com.sony.imaging.app.base.menu.IController
    public void setValue(String itemId, String value) {
        GFEffectParameters.Parameters params = GFEffectParameters.getInstance().getParameters();
        if (SAME.equalsIgnoreCase(value)) {
            String wbMode = params.getWBMode(true);
            super.setValue(itemId, wbMode);
            WhiteBalanceController.WhiteBalanceParam options = (WhiteBalanceController.WhiteBalanceParam) getDetailValue();
            String[] optArray = params.getWBOption(true).split("/");
            int light = Integer.parseInt(optArray[0]);
            int comp = Integer.parseInt(optArray[1]);
            int temp = Integer.parseInt(optArray[2]);
            options.setLightBalance(light);
            options.setColorComp(comp);
            if (!WhiteBalanceController.CUSTOM.equalsIgnoreCase(wbMode) && !"custom1".equalsIgnoreCase(wbMode) && !"custom2".equalsIgnoreCase(wbMode) && !"custom3".equalsIgnoreCase(wbMode)) {
                options.setColorTemp(temp);
            }
            setDetailValue(options);
            params.setColorFilter("OFF");
            return;
        }
        if (isSetByFilterMenu()) {
            params.setColorFilter("ON");
            super.setValue(itemId, value);
            if (WhiteBalanceController.COLOR_TEMP.equalsIgnoreCase(value)) {
                WhiteBalanceController.WhiteBalanceParam options2 = (WhiteBalanceController.WhiteBalanceParam) getDetailValue();
                String[] optArray2 = GFBackUpKey.getInstance().getCTempFilterWBOption(false, GFEffectParameters.Parameters.getEffect()).split("/");
                int light2 = Integer.parseInt(optArray2[0]);
                int comp2 = Integer.parseInt(optArray2[1]);
                int temp2 = Integer.parseInt(optArray2[2]);
                options2.setLightBalance(light2);
                options2.setColorComp(comp2);
                options2.setColorTemp(temp2);
                setDetailValue(options2);
            }
        }
        super.setValue(itemId, value);
    }

    @Override // com.sony.imaging.app.base.shooting.camera.WhiteBalanceController, com.sony.imaging.app.base.shooting.camera.AbstractController, com.sony.imaging.app.base.menu.IController
    public void setDetailValue(Object obj) {
        super.setDetailValue(obj);
    }

    @Override // com.sony.imaging.app.base.shooting.camera.WhiteBalanceController, com.sony.imaging.app.base.shooting.camera.AbstractController, com.sony.imaging.app.base.menu.IController
    public Object getDetailValue() {
        if (needPFDetailValue) {
            return getDetailValueFromPF();
        }
        return (WhiteBalanceController.WhiteBalanceParam) super.getDetailValue();
    }

    public Object getDetailValueFromPF() {
        WhiteBalanceController.WhiteBalanceParam wbParam = (WhiteBalanceController.WhiteBalanceParam) super.getDetailValue();
        Pair<Camera.Parameters, CameraEx.ParametersModifier> p = CameraSetting.getInstance().getParameters();
        wbParam.setColorTemp(((CameraEx.ParametersModifier) p.second).getColorTemperatureForWhiteBalance());
        wbParam.setLightBalance(((CameraEx.ParametersModifier) p.second).getLightBalanceForWhiteBalance());
        wbParam.setColorComp(((CameraEx.ParametersModifier) p.second).getColorCompensationForWhiteBalance());
        return wbParam;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.shooting.camera.WhiteBalanceController
    public WhiteBalanceController.WhiteBalanceParam getWBOptionFromBackUp(String mode) {
        if (!GFWhiteBalanceMenuLayout.isCTempOpened && !GFWhiteBalanceAdjustmentMenuLayout.isLayoutOpened) {
            WhiteBalanceController.WhiteBalanceParam options = super.getWBOptionFromBackUp(mode);
            String bkupValue = GFBackUpKey.getInstance().getWBOption(mode, !GFCommonUtil.getInstance().isFilterSetting(), GFEffectParameters.Parameters.getEffect());
            String[] optArray = bkupValue.split("/");
            int light = Integer.parseInt(optArray[0]);
            int comp = Integer.parseInt(optArray[1]);
            int temp = Integer.parseInt(optArray[2]);
            options.setLightBalance(light);
            options.setColorComp(comp);
            if (!WhiteBalanceController.CUSTOM.equalsIgnoreCase(mode) && !"custom1".equalsIgnoreCase(mode) && !"custom2".equalsIgnoreCase(mode) && !"custom3".equalsIgnoreCase(mode)) {
                options.setColorTemp(temp);
                return options;
            }
            return options;
        }
        return super.getWBOptionFromBackUp(mode);
    }

    @Override // com.sony.imaging.app.base.shooting.camera.WhiteBalanceController, com.sony.imaging.app.base.menu.IController
    public String getValue(String itemId) {
        GFEffectParameters.Parameters param = GFEffectParameters.getInstance().getParameters();
        return (param.getColorFilter().equalsIgnoreCase("OFF") && !SAME.equalsIgnoreCase(itemId) && isSetByFilterMenu()) ? SAME : super.getValue(itemId);
    }

    @Override // com.sony.imaging.app.base.shooting.camera.WhiteBalanceController, com.sony.imaging.app.base.menu.IController
    public List<String> getSupportedValue(String tag) {
        List<String> supportedList = super.getSupportedValue(tag);
        if (tag.equals(WhiteBalanceController.WHITEBALANCE) && GFCommonUtil.getInstance().isFilterSetting()) {
            supportedList.add(SAME);
        }
        return supportedList;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.WhiteBalanceController, com.sony.imaging.app.base.menu.IController
    public List<String> getAvailableValue(String tag) {
        List<String> availables = super.getAvailableValue(tag);
        if (GFCommonUtil.getInstance().isFilterSetting()) {
            availables.remove(WhiteBalanceController.CUSTOM_SET);
        }
        if (!getInstance().isSupportedABGM() && GFCommonUtil.getInstance().isEnableFlash()) {
            availables.remove("auto");
            availables.remove(WhiteBalanceController.UNDERWATER_AUTO);
        }
        return availables;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.WhiteBalanceController
    public void saveCustomWhiteBalance(String itemId) {
        GFEffectParameters.getInstance().getParameters().setWBMode(true, itemId);
        super.saveCustomWhiteBalance(itemId);
    }

    public boolean isSupportedABGM() {
        String version = ScalarProperties.getString("version.platform");
        int pfMajorVersion = Integer.parseInt(version.substring(0, version.indexOf(StringBuilderThreadLocal.PERIOD)));
        int pfAPIVersion = Integer.parseInt(version.substring(version.indexOf(StringBuilderThreadLocal.PERIOD) + 1));
        if (pfMajorVersion >= 3) {
            return true;
        }
        if (pfMajorVersion == 2 && pfAPIVersion >= 15) {
            return true;
        }
        int modificationBit = ScalarProperties.getInt("ui.modification.patch.bits0");
        return (modificationBit & 16) == 16;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.AbstractController, com.sony.imaging.app.base.menu.IController
    public int getCautionIndex(String itemId) {
        if (GFCommonUtil.getInstance().isFilterSetting() && itemId.equalsIgnoreCase("WhiteBalance_custom-set")) {
            CautionUtilityClass.getInstance().requestTrigger(GFInfo.CAUTION_ID_DLAPP_INVALID_FUNCTION_IN_APPSETTING);
            return 1;
        }
        if (!getInstance().isSupportedABGM() && GFCommonUtil.getInstance().isEnableFlash() && (itemId.equalsIgnoreCase("WhiteBalance_auto") || itemId.equalsIgnoreCase("WhiteBalance_underwater-auto"))) {
            CautionUtilityClass.getInstance().requestTrigger(Info.CAUTION_ID_DLAPP_INVALID_FUNCTION);
            return 1;
        }
        int ret = super.getCautionIndex(itemId);
        return ret;
    }

    public void setByFilterMenu(boolean isOpend) {
        mOpenMenuLayout = isOpend;
    }

    public boolean isSetByFilterMenu() {
        return mOpenMenuLayout;
    }

    public void setWBDetailValueFromPF(boolean needPF) {
        needPFDetailValue = needPF;
    }
}
