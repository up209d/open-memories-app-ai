package com.github.up209d.cubelut;

import android.util.Log;

import com.github.ma1co.openmemories.framework.DeviceInfo;
import com.sony.scalar.hardware.CameraEx;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class IspController {
    private static final String TAG = "IspController";

    private CameraEx cameraEx;
    private boolean gammaActive;

    public IspController() {
        gammaActive = false;
    }

    public void setCameraEx(CameraEx camera) {
        this.cameraEx = camera;
    }

    public void apply(short[] gammaCurve, int[] rgbMatrix) {
        if (!DeviceInfo.getInstance().isCamera()) {
            Log.d(TAG, "ISP apply skipped (not camera device)");
            FileLogger.log("ISP", "Apply skipped (emulator mode)");
            return;
        }

        if (cameraEx == null) {
            Log.w(TAG, "CameraEx not set");
            return;
        }

        try {
            // Apply GammaTable
            if (gammaCurve != null) {
                CameraEx.GammaTable table = cameraEx.createGammaTable();
                table.setPictureEffectGammaForceOff(true);

                // Convert short[] to byte stream (1024 entries, big-endian shorts)
                ByteArrayOutputStream baos = new ByteArrayOutputStream(2048);
                DataOutputStream dos = new DataOutputStream(baos);
                for (int i = 0; i < gammaCurve.length; i++) {
                    dos.writeShort(gammaCurve[i]);
                }
                dos.flush();

                ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
                table.write(bais);
                cameraEx.setExtendedGammaTable(table);
                table.release();
                gammaActive = true;
                FileLogger.log("ISP", "GammaTable applied (1024 entries)");
            }

            // Apply RGB Matrix
            if (rgbMatrix != null) {
                android.hardware.Camera.Parameters params = cameraEx.getNormalCamera().getParameters();
                CameraEx.ParametersModifier modifier = cameraEx.createParametersModifier(params);
                if (modifier.isRGBMatrixSupported()) {
                    modifier.setRGBMatrix(rgbMatrix);
                    cameraEx.getNormalCamera().setParameters(params);
                    FileLogger.log("ISP", "RGBMatrix applied");
                } else {
                    FileLogger.log("ISP", "RGBMatrix not supported on this device");
                }
            }
        } catch (IOException e) {
            FileLogger.logError("ISP", e);
        } catch (Exception e) {
            FileLogger.logError("ISP", e);
        }
    }

    public void clear() {
        if (!DeviceInfo.getInstance().isCamera()) {
            Log.d(TAG, "ISP clear skipped (not camera device)");
            return;
        }

        if (cameraEx == null) return;

        try {
            cameraEx.setExtendedGammaTable(null);
            gammaActive = false;

            android.hardware.Camera.Parameters params = cameraEx.getNormalCamera().getParameters();
            CameraEx.ParametersModifier modifier = cameraEx.createParametersModifier(params);
            if (modifier.isRGBMatrixSupported()) {
                modifier.setRGBMatrix(null);
                cameraEx.getNormalCamera().setParameters(params);
            }
            FileLogger.log("ISP", "Cleared GammaTable + RGBMatrix");
        } catch (Exception e) {
            FileLogger.logError("ISP", e);
        }
    }

    public boolean isGammaActive() {
        return gammaActive;
    }
}
