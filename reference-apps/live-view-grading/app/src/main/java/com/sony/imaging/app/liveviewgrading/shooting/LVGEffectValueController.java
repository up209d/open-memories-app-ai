package com.sony.imaging.app.liveviewgrading.shooting;

import android.content.Context;
import android.content.res.AssetManager;
import android.hardware.Camera;
import android.util.Log;
import android.util.Pair;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.base.shooting.camera.MovieFormatController;
import com.sony.imaging.app.base.shooting.camera.PictureEffectController;
import com.sony.imaging.app.liveviewgrading.ColorGradingConstants;
import com.sony.imaging.app.liveviewgrading.menu.controller.ColorGradingController;
import com.sony.imaging.app.liveviewgrading.menu.controller.LVGParameterValueController;
import com.sony.scalar.hardware.CameraEx;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

/* loaded from: classes.dex */
public class LVGEffectValueController {
    private static final int GAMMATABLE_INNER_FACTOR_NUM = 1024;
    private static String TAG = "LVGEffectValueController";
    private static LVGEffectValueController mEffectValueController;
    private CameraSetting mCamSet;
    private Context mContext = null;
    public final int CONTRAST = 0;
    public final int SATURATION = 1;
    public final int SHARPNESS = 2;
    public final int COLOR_TEMP = 3;
    public final int COLOR_TINT = 4;
    public final int COLOR_DEPTH_R = 5;
    public final int COLOR_DEPTH_G = 6;
    public final int COLOR_DEPTH_B = 7;

    private LVGEffectValueController() {
        this.mCamSet = null;
        this.mCamSet = CameraSetting.getInstance();
    }

    public static LVGEffectValueController getInstance() {
        if (mEffectValueController == null) {
            mEffectValueController = new LVGEffectValueController();
        }
        return mEffectValueController;
    }

    public void setParamValues(int[] vals) {
        CameraSetting mCamSet = CameraSetting.getInstance();
        Pair<Camera.Parameters, CameraEx.ParametersModifier> sup_params = mCamSet.getSupportedParameters(2);
        Pair<Camera.Parameters, CameraEx.ParametersModifier> emptyParams = mCamSet.getEmptyParameters();
        ((CameraEx.ParametersModifier) emptyParams.second).setContrast(vals[0]);
        ((CameraEx.ParametersModifier) emptyParams.second).setSaturation(vals[1]);
        if (((CameraEx.ParametersModifier) sup_params.second).isSharpnessGainModeSupported()) {
            ((CameraEx.ParametersModifier) emptyParams.second).setSharpnessGainMode(true);
            ((CameraEx.ParametersModifier) emptyParams.second).setSharpnessGain(vals[2]);
        }
        if (((CameraEx.ParametersModifier) sup_params.second).isWhiteBalanceShiftModeSupported()) {
            ((CameraEx.ParametersModifier) emptyParams.second).setWhiteBalanceShiftMode(true);
            ((CameraEx.ParametersModifier) emptyParams.second).setWhiteBalanceShiftLB(vals[3] * (-1));
            ((CameraEx.ParametersModifier) emptyParams.second).setWhiteBalanceShiftCC(vals[4] * (-1));
        }
        if (((CameraEx.ParametersModifier) sup_params.second).getSupportedColorDepthTypes().contains(PictureEffectController.PART_COLOR_RED)) {
            ((CameraEx.ParametersModifier) emptyParams.second).setColorDepth(PictureEffectController.PART_COLOR_RED, vals[5]);
        }
        if (((CameraEx.ParametersModifier) sup_params.second).getSupportedColorDepthTypes().contains("green")) {
            ((CameraEx.ParametersModifier) emptyParams.second).setColorDepth("green", vals[6]);
        }
        if (((CameraEx.ParametersModifier) sup_params.second).getSupportedColorDepthTypes().contains(PictureEffectController.PART_COLOR_BLUE)) {
            ((CameraEx.ParametersModifier) emptyParams.second).setColorDepth(PictureEffectController.PART_COLOR_BLUE, vals[7]);
        }
        setFixedValueParameters(emptyParams);
        mCamSet.setParameters(emptyParams);
    }

    public void applyGammaTable() {
        Pair<Camera.Parameters, CameraEx.ParametersModifier> sup_params = this.mCamSet.getSupportedParameters(2);
        if (((CameraEx.ParametersModifier) sup_params.second).isExtendedGammaTableSupported()) {
            readFile();
        }
    }

    public void clearGammmTable() {
        Pair<Camera.Parameters, CameraEx.ParametersModifier> sup_params = this.mCamSet.getSupportedParameters(2);
        if (((CameraEx.ParametersModifier) sup_params.second).isExtendedGammaTableSupported()) {
            CameraEx camera = CameraSetting.getInstance().getCamera();
            if (camera != null) {
                CameraSetting.getInstance().getCamera().setExtendedGammaTable((CameraEx.GammaTable) null);
            }
        }
    }

    private void setFixedValueParameters(Pair<Camera.Parameters, CameraEx.ParametersModifier> params) {
        int preset = LVGParameterValueController.getInstance().getLastSelectedPreset();
        String fixedValueString = "";
        switch (preset) {
            case 1:
                fixedValueString = ColorGradingConstants.STD_CLEAR_FIXED_VALUES;
                break;
            case 2:
                fixedValueString = ColorGradingConstants.STD_VIVID_FIXED_VALUES;
                break;
            case 3:
                fixedValueString = ColorGradingConstants.STD_MONO_FIXED_VALUES;
                break;
            case 4:
                fixedValueString = ColorGradingConstants.STD_BOLD_FIXED_VALUES;
                break;
            case 5:
                fixedValueString = ColorGradingConstants.CINEMA_COAST_FIXED_VALUES;
                break;
            case 6:
                fixedValueString = ColorGradingConstants.CINEMA_SILKY_FIXED_VALUES;
                break;
            case 7:
                fixedValueString = ColorGradingConstants.CINEMA_MISTY_FIXED_VALUES;
                break;
            case 8:
                fixedValueString = ColorGradingConstants.CINEMA_VELVETY_FIXED_VALUES;
                break;
            case 9:
                fixedValueString = ColorGradingConstants.EXT_180_FIXED_VALUES;
                break;
            case 10:
                fixedValueString = ColorGradingConstants.EXT_SURF_FIXED_VALUES;
                break;
            case 11:
                fixedValueString = ColorGradingConstants.EXT_BIG_AIR_FIXED_VALUES;
                break;
            case 12:
                fixedValueString = ColorGradingConstants.EXT_SNOW_FIXED_VALUES;
                break;
        }
        unflattenFixedValues(fixedValueString, params);
        setRGBMatrix(preset, params);
    }

    private void unflattenFixedValues(String values, Pair<Camera.Parameters, CameraEx.ParametersModifier> params) {
        int kikilogSubId;
        Pair<Camera.Parameters, CameraEx.ParametersModifier> sup_params = this.mCamSet.getSupportedParameters(2);
        int cyan = 0;
        int magenta = 0;
        int yellow = 0;
        int exposureShift = 0;
        String shading = "OFF";
        String[] dev = values.split(MovieFormatController.Settings.SEMI_COLON, -1);
        for (String str : dev) {
            String[] element = str.split(MovieFormatController.Settings.EQUAL);
            if (ColorGradingConstants.COLOR_DEPTH_CYAN.equals(element[0])) {
                cyan = Integer.valueOf(element[1]).intValue();
            } else if (ColorGradingConstants.COLOR_DEPTH_MAGENTA.equals(element[0])) {
                magenta = Integer.valueOf(element[1]).intValue();
            } else if (ColorGradingConstants.COLOR_DEPTH_YELLOW.equals(element[0])) {
                yellow = Integer.valueOf(element[1]).intValue();
            } else if (ColorGradingConstants.EXPOSURE_SHIFT.equals(element[0])) {
                exposureShift = Integer.valueOf(element[1]).intValue();
            } else if (ColorGradingConstants.SHADING.equals(element[0])) {
                shading = element[1];
            }
        }
        if (((CameraEx.ParametersModifier) sup_params.second).getSupportedColorDepthTypes().contains("cyan")) {
            ((CameraEx.ParametersModifier) params.second).setColorDepth("cyan", cyan);
        }
        if (((CameraEx.ParametersModifier) sup_params.second).getSupportedColorDepthTypes().contains(PictureEffectController.TOY_CAMERA_MAGENTA)) {
            ((CameraEx.ParametersModifier) params.second).setColorDepth(PictureEffectController.TOY_CAMERA_MAGENTA, magenta);
        }
        if (((CameraEx.ParametersModifier) sup_params.second).getSupportedColorDepthTypes().contains(PictureEffectController.PART_COLOR_YELLOW)) {
            ((CameraEx.ParametersModifier) params.second).setColorDepth(PictureEffectController.PART_COLOR_YELLOW, yellow);
        }
        if (((CameraEx.ParametersModifier) sup_params.second).isPictureControlExposureShiftSupported()) {
            ((CameraEx.ParametersModifier) params.second).setPictureControlExposureShift(exposureShift);
        }
        if (shading.equalsIgnoreCase("ON")) {
            PictureEffectController.getInstance().setValue(PictureEffectController.PICTUREEFFECT, PictureEffectController.MODE_TOY_CAMERA);
            PictureEffectController.getInstance().setValue(PictureEffectController.MODE_TOY_CAMERA, "normal");
            kikilogSubId = 4237;
        } else {
            PictureEffectController.getInstance().setValue(PictureEffectController.PICTUREEFFECT, "off");
            kikilogSubId = 4237;
        }
        ColorGradingController.getInstance().startKikiLog(kikilogSubId);
    }

    private void setRGBMatrix(int preset, Pair<Camera.Parameters, CameraEx.ParametersModifier> params) {
        int kikilogSubId = -1;
        int[] matrix = null;
        switch (preset) {
            case 1:
                matrix = ColorGradingConstants.STD_CLEAR_RGB_MATRIX;
                break;
            case 2:
                matrix = ColorGradingConstants.STD_VIVID_RGB_MATRIX;
                break;
            case 3:
                matrix = ColorGradingConstants.STD_MONO_RGB_MATRIX;
                break;
            case 4:
                matrix = ColorGradingConstants.STD_BOLD_RGB_MATRIX;
                break;
            case 5:
                matrix = ColorGradingConstants.CINEMA_COAST_RGB_MATRIX;
                break;
            case 6:
                matrix = ColorGradingConstants.CINEMA_SILKY_RGB_MATRIX;
                break;
            case 7:
                matrix = ColorGradingConstants.CINEMA_MISTY_RGB_MATRIX;
                break;
            case 8:
                matrix = ColorGradingConstants.CINEMA_VELVETY_RGB_MATRIX;
                break;
            case 9:
                matrix = ColorGradingConstants.EXT_180_RGB_MATRIX;
                break;
            case 10:
                matrix = ColorGradingConstants.EXT_SURF_RGB_MATRIX;
                break;
            case 11:
                matrix = ColorGradingConstants.EXT_BIG_AIR_RGB_MATRIX;
                break;
            case 12:
                matrix = ColorGradingConstants.EXT_SNOW_RGB_MATRIX;
                break;
        }
        Pair<Camera.Parameters, CameraEx.ParametersModifier> sup_params = this.mCamSet.getSupportedParameters(2);
        if (((CameraEx.ParametersModifier) sup_params.second).isRGBMatrixSupported()) {
            ((CameraEx.ParametersModifier) params.second).setRGBMatrix(matrix);
            kikilogSubId = 4232;
        }
        ColorGradingController.getInstance().startKikiLog(kikilogSubId);
    }

    public void readFile() {
        AssetManager assetManager = this.mContext.getAssets();
        try {
            InputStream input = assetManager.open(getPresetFileName());
            BufferedReader br = new BufferedReader(new InputStreamReader(input, "UTF-8"));
            int j = 0;
            short[] gamma_table = new short[GAMMATABLE_INNER_FACTOR_NUM];
            while (true) {
                String line = br.readLine();
                if (line != null) {
                    StringTokenizer token = new StringTokenizer(line, ",");
                    Log.i(TAG, "setValue: " + line);
                    while (token.hasMoreTokens()) {
                        gamma_table[j] = Short.valueOf(token.nextToken()).shortValue();
                        j++;
                    }
                } else {
                    setGammaTable(gamma_table);
                    br.close();
                    input.close();
                    return;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setContext(Context context) {
        this.mContext = context;
    }

    private void setGammaTable(short[] array) {
        CameraEx.GammaTable table = null;
        if (array != null) {
            InputStream byteArrayIn = new ByteArrayInputStream(convShortArrayToByteArray(array));
            table = this.mCamSet.getCamera().createGammaTable();
            table.setPictureEffectGammaForceOff(true);
            if (table != null) {
                Log.i(TAG, "setGammaTable createGammaTable rerurns null");
                table.write(byteArrayIn);
            }
        }
        Log.i(TAG, "setGammaTable:" + table);
        this.mCamSet.getCamera().setExtendedGammaTable(table);
        if (table != null) {
            table.release();
        }
        ColorGradingController.getInstance().startKikiLog(4236);
    }

    protected byte[] convShortArrayToByteArray(short[] table) {
        byte[] w = new byte[table.length * 2];
        for (int i = 0; table.length > i; i++) {
            convShortToByte(w, i * 2, table[i]);
        }
        return w;
    }

    protected void convShortToByte(byte[] data, int offset, short value) {
        for (int i = 0; i < 2; i++) {
            if (i == 0) {
                data[offset + i] = Integer.valueOf(value >> 0).byteValue();
            } else {
                data[offset + i] = Integer.valueOf(value >> 8).byteValue();
            }
        }
    }

    private String getPresetFileName() {
        int preset = LVGParameterValueController.getInstance().getLastSelectedPreset();
        switch (preset) {
            case 1:
                return ColorGradingConstants.STD_CLEAR_FILE;
            case 2:
                return ColorGradingConstants.STD_VIVID_FILE;
            case 3:
                return ColorGradingConstants.STD_MONOCHROME_FILE;
            case 4:
                return ColorGradingConstants.STD_BOLD_FILE;
            case 5:
                return ColorGradingConstants.CIN_COAST_SIDE_LIGHT_FILE;
            case 6:
                return ColorGradingConstants.CIN_SILKY_FILE;
            case 7:
                return ColorGradingConstants.CIN_MISTY_BLUE_FILE;
            case 8:
                return ColorGradingConstants.CIN_VELVETY_DEW_FILE;
            case 9:
                return ColorGradingConstants.EX_180_FILE;
            case 10:
                return ColorGradingConstants.EX_SURF_TRIP_FILE;
            case 11:
                return ColorGradingConstants.EX_BIG_AIR_FILE;
            case 12:
                return ColorGradingConstants.EX_SNOW_TRICKS_FILE;
            default:
                return ColorGradingConstants.STD_CLEAR_FILE;
        }
    }
}
