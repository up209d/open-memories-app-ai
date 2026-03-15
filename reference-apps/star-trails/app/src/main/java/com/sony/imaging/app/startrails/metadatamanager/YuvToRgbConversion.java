package com.sony.imaging.app.startrails.metadatamanager;

import android.graphics.Bitmap;
import android.util.Log;
import com.sony.imaging.app.avi.sa.SaUtil;
import com.sony.scalar.graphics.OptimizedImage;
import com.sony.scalar.hardware.DSP;
import com.sony.scalar.hardware.DeviceBuffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/* loaded from: classes.dex */
public class YuvToRgbConversion {
    private DSP mDsp;
    private final String TAG = YuvToRgbConversion.class.getName();
    private DeviceBuffer bootParam = null;
    private DeviceBuffer sa_param = null;
    private DeviceBuffer rgbbuff = null;

    public YuvToRgbConversion(DSP yuvDsp) {
        this.mDsp = yuvDsp;
    }

    public synchronized void releaseYuvToRgbResources() {
        Log.d(this.TAG, "====releaseYuvToRgbResources +");
        if (this.mDsp != null) {
            if (this.bootParam != null) {
                this.bootParam.release();
                this.bootParam = null;
            }
            if (this.sa_param != null) {
                this.sa_param.release();
                this.sa_param = null;
            }
            if (this.rgbbuff != null) {
                this.rgbbuff.release();
                this.rgbbuff = null;
            }
        }
        Log.d(this.TAG, "====releaseYuvToRgbResources -");
    }

    public synchronized Bitmap yuv2rgb_main(OptimizedImage optScaledImage) {
        Bitmap bmp;
        int width = optScaledImage.getWidth();
        int height = optScaledImage.getHeight();
        this.rgbbuff = this.mDsp.createBuffer(width * height * 4);
        exec_sa(optScaledImage);
        ByteBuffer data = ByteBuffer.allocateDirect(width * height * 4);
        data.order(ByteOrder.nativeOrder());
        this.rgbbuff.read(data);
        bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bmp.copyPixelsFromBuffer(data);
        if (this.rgbbuff != null) {
            this.rgbbuff.release();
            this.rgbbuff = null;
        }
        data.clear();
        return bmp;
    }

    private synchronized void exec_sa(OptimizedImage optScaledImage) {
        int width = optScaledImage.getWidth();
        int height = optScaledImage.getHeight();
        prepare_sa_bootparam();
        if (this.sa_param == null) {
            this.sa_param = this.mDsp.createBuffer(32);
        }
        ByteBuffer buf = ByteBuffer.allocateDirect(32);
        buf.order(ByteOrder.nativeOrder());
        buf.putInt(SaUtil.convArmAddr2AxiAddr(this.mDsp.getPropertyAsInt(optScaledImage, "memory-address")));
        buf.putInt(SaUtil.convArmAddr2AxiAddr(this.mDsp.getPropertyAsInt(this.rgbbuff, "memory-address")));
        int cwidth = this.mDsp.getPropertyAsInt(optScaledImage, "image-canvas-width");
        buf.putInt(width);
        buf.putInt(height);
        buf.putInt(0);
        buf.putInt(cwidth);
        buf.putInt(width);
        buf.putInt(0);
        this.sa_param.write(buf);
        this.mDsp.setArg(0, this.bootParam);
        this.mDsp.setArg(1, this.sa_param);
        this.mDsp.setArg(2, optScaledImage);
        this.mDsp.setArg(3, this.rgbbuff);
        if (this.mDsp.execute()) {
            Log.i("sa", "success");
        } else {
            Log.i("sa", "Failed");
        }
        buf.clear();
        if (this.bootParam != null) {
            this.bootParam.release();
            this.bootParam = null;
        }
        if (this.sa_param != null) {
            this.sa_param.release();
            this.sa_param = null;
        }
    }

    private synchronized void prepare_sa_bootparam() {
        if (this.bootParam == null) {
            this.bootParam = this.mDsp.createBuffer(60);
        }
        ByteBuffer buf = ByteBuffer.allocateDirect(60);
        buf.order(ByteOrder.nativeOrder());
        buf.putInt(this.mDsp.getPropertyAsInt("program-descriptor"));
        buf.put((byte) 0);
        buf.put((byte) 0);
        buf.put((byte) 0);
        buf.put((byte) 0);
        buf.put((byte) 0);
        buf.put((byte) 0);
        buf.put((byte) 0);
        buf.put((byte) 0);
        buf.putInt(0);
        buf.putInt(0);
        buf.putInt(0);
        buf.putInt(0);
        buf.put((byte) 0);
        buf.put((byte) 0);
        buf.put((byte) 0);
        buf.put((byte) 0);
        buf.putInt(0);
        buf.putInt(0);
        buf.putInt(0);
        buf.putInt(0);
        buf.put((byte) 0);
        buf.put((byte) 0);
        buf.put((byte) 0);
        buf.put((byte) 0);
        buf.putInt(0);
        buf.putInt(0);
        this.bootParam.write(buf);
        buf.clear();
    }
}
