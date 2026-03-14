package com.sony.imaging.app.digitalfilter.shooting.camera;

import android.hardware.Camera;
import android.util.Pair;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.caution.Info;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.base.shooting.camera.WhiteBalanceController;
import com.sony.imaging.app.digitalfilter.GFBackUpKey;
import com.sony.imaging.app.digitalfilter.common.GFCommonUtil;
import com.sony.imaging.app.digitalfilter.menu.layout.GFWhiteBalanceAdjustmentMenuLayout;
import com.sony.imaging.app.digitalfilter.menu.layout.GFWhiteBalanceMenuLayout;
import com.sony.imaging.app.digitalfilter.shooting.GFEffectParameters;
import com.sony.imaging.app.util.StringBuilderThreadLocal;
import com.sony.scalar.hardware.CameraEx;
import com.sony.scalar.sysutil.ScalarProperties;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class GFWhiteBalanceController extends WhiteBalanceController {
    public static final String SAME = "same";
    private static final int SUPPORTED_YCINFO_WB_GAIN = 16;
    private static GFWhiteBalanceController mInstance = null;
    private static int mSettingLayer;
    private static boolean needPFDetailValue;
    private static ArrayList<String> sSupportedCTemp;
    private static ArrayList<String> sSupportedCTempAWB;

    static {
        if (sSupportedCTemp == null) {
            sSupportedCTemp = new ArrayList<>();
        }
        sSupportedCTemp.add(WhiteBalanceController.COLOR_TEMP);
        if (sSupportedCTempAWB == null) {
            sSupportedCTempAWB = new ArrayList<>();
        }
        sSupportedCTempAWB.add("auto");
        sSupportedCTempAWB.add(WhiteBalanceController.COLOR_TEMP);
        mSettingLayer = 0;
        needPFDetailValue = false;
    }

    public static GFWhiteBalanceController getInstance() {
        if (mInstance == null) {
            mInstance = new GFWhiteBalanceController();
        }
        return mInstance;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.WhiteBalanceController, com.sony.imaging.app.base.menu.IController
    public void setValue(String itemId, String value) {
        if (value.equals(WhiteBalanceController.CUSTOM_SET)) {
            mSettingLayer = GFCommonUtil.getInstance().getSettingLayer();
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
            int layer = GFCommonUtil.getInstance().getSettingLayer();
            if (!GFCommonUtil.getInstance().isLayerSetting()) {
                if (GFEEAreaController.getInstance().isLand()) {
                    layer = 0;
                } else if (GFEEAreaController.getInstance().isSky()) {
                    layer = 1;
                } else if (GFEEAreaController.getInstance().isLayer3()) {
                    layer = 2;
                }
            }
            String theme = GFThemeController.getInstance().getValue();
            String bkupValue = GFBackUpKey.getInstance().getWBOption(mode, layer, theme);
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
    public List<String> getSupportedValue(String tag) {
        List<String> supportedList = super.getSupportedValue(tag);
        if (tag.equals(WhiteBalanceController.WHITEBALANCE) || tag.equals(WhiteBalanceController.WHITEBALANCEFN)) {
            if (GFWhiteBalanceLimitController.getInstance().isLimitToCTempAWB()) {
                return sSupportedCTempAWB;
            }
            if (GFWhiteBalanceLimitController.getInstance().isLimitToCTemp()) {
                return sSupportedCTemp;
            }
            return supportedList;
        }
        return supportedList;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.WhiteBalanceController, com.sony.imaging.app.base.menu.IController
    public List<String> getAvailableValue(String tag) {
        List<String> availables = super.getAvailableValue(tag);
        if (!getInstance().isSupportedABGM() && GFCommonUtil.getInstance().isEnableFlash()) {
            availables.remove("auto");
            availables.remove(WhiteBalanceController.UNDERWATER_AUTO);
        }
        return availables;
    }

    @Override // com.sony.imaging.app.base.shooting.camera.WhiteBalanceController
    public void saveCustomWhiteBalance(String itemId) {
        GFEffectParameters.getInstance().getParameters().setWBMode(mSettingLayer, itemId);
        super.saveCustomWhiteBalance(itemId);
    }

    public int getCustomWhiteBalanceSettingLayer() {
        return mSettingLayer;
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
        if (!getInstance().isSupportedABGM() && GFCommonUtil.getInstance().isEnableFlash() && (itemId.equalsIgnoreCase("WhiteBalance_auto") || itemId.equalsIgnoreCase("WhiteBalance_underwater-auto"))) {
            CautionUtilityClass.getInstance().requestTrigger(Info.CAUTION_ID_DLAPP_INVALID_FUNCTION);
            return 1;
        }
        int ret = super.getCautionIndex(itemId);
        return ret;
    }

    public void setWBDetailValueFromPF(boolean needPF) {
        needPFDetailValue = needPF;
    }
}
