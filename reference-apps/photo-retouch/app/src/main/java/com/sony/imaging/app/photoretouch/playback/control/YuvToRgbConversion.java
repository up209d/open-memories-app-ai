package com.sony.imaging.app.photoretouch.playback.control;

import android.graphics.Bitmap;
import android.util.Log;
import com.sony.scalar.graphics.OptimizedImage;
import com.sony.scalar.hardware.DSP;
import com.sony.scalar.hardware.DeviceBuffer;
import com.sony.scalar.sysutil.ScalarProperties;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/* loaded from: classes.dex */
public class YuvToRgbConversion {
    private DSP mDsp;
    private DeviceBuffer sa_param;
    private ByteBuffer sa_param_buf;
    private final String TAG = YuvToRgbConversion.class.getSimpleName();
    private DeviceBuffer bootParam = null;
    private DeviceBuffer rgbbuff = null;
    private ByteBuffer bitmapData = null;
    private ByteBuffer bootParam_buf = null;

    public YuvToRgbConversion(DSP yuvDsp) {
        this.sa_param = null;
        this.sa_param_buf = null;
        this.mDsp = yuvDsp;
        this.sa_param = this.mDsp.createBuffer(32);
        this.sa_param_buf = ByteBuffer.allocateDirect(32);
        this.sa_param_buf.order(ByteOrder.nativeOrder());
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
        int size = width * height * 4;
        if (this.bitmapData == null || this.bitmapData.capacity() < size) {
            this.bitmapData = ByteBuffer.allocateDirect(size);
            this.bitmapData.order(ByteOrder.nativeOrder());
        } else {
            this.bitmapData.rewind();
        }
        this.rgbbuff.read(this.bitmapData);
        bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bmp.copyPixelsFromBuffer(this.bitmapData);
        if (this.rgbbuff != null) {
            this.rgbbuff.release();
            this.rgbbuff = null;
        }
        return bmp;
    }

    private synchronized void exec_sa(OptimizedImage optScaledImage) {
        int width = optScaledImage.getWidth();
        int height = optScaledImage.getHeight();
        prepare_sa_bootparam();
        this.sa_param_buf.rewind();
        this.sa_param_buf.putInt(getAXIAddr(this.mDsp.getPropertyAsInt(optScaledImage, "memory-address")));
        this.sa_param_buf.putInt(getAXIAddr(this.mDsp.getPropertyAsInt(this.rgbbuff, "memory-address")));
        int cwidth = this.mDsp.getPropertyAsInt(optScaledImage, "image-canvas-width");
        this.sa_param_buf.putInt(width);
        this.sa_param_buf.putInt(height);
        this.sa_param_buf.putInt(0);
        this.sa_param_buf.putInt(cwidth);
        this.sa_param_buf.putInt(width);
        this.sa_param_buf.putInt(0);
        this.sa_param.write(this.sa_param_buf);
        this.mDsp.setArg(0, this.bootParam);
        this.mDsp.setArg(1, this.sa_param);
        this.mDsp.setArg(2, optScaledImage);
        this.mDsp.setArg(3, this.rgbbuff);
        if (this.mDsp.execute()) {
            Log.i("sa", "success");
        } else {
            Log.i("sa", "Failed");
        }
    }

    private synchronized void prepare_sa_bootparam() {
        if (this.bootParam != null) {
            this.bootParam.write(this.bootParam_buf);
        } else {
            this.bootParam = this.mDsp.createBuffer(60);
            this.bootParam_buf = ByteBuffer.allocateDirect(60);
            this.bootParam_buf.order(ByteOrder.nativeOrder());
            this.bootParam_buf.putInt(this.mDsp.getPropertyAsInt("program-descriptor"));
            this.bootParam_buf.put((byte) 0);
            this.bootParam_buf.put((byte) 0);
            this.bootParam_buf.put((byte) 0);
            this.bootParam_buf.put((byte) 0);
            this.bootParam_buf.put((byte) 0);
            this.bootParam_buf.put((byte) 0);
            this.bootParam_buf.put((byte) 0);
            this.bootParam_buf.put((byte) 0);
            this.bootParam_buf.putInt(0);
            this.bootParam_buf.putInt(0);
            this.bootParam_buf.putInt(0);
            this.bootParam_buf.putInt(0);
            this.bootParam_buf.put((byte) 0);
            this.bootParam_buf.put((byte) 0);
            this.bootParam_buf.put((byte) 0);
            this.bootParam_buf.put((byte) 0);
            this.bootParam_buf.putInt(0);
            this.bootParam_buf.putInt(0);
            this.bootParam_buf.putInt(0);
            this.bootParam_buf.putInt(0);
            this.bootParam_buf.put((byte) 0);
            this.bootParam_buf.put((byte) 0);
            this.bootParam_buf.put((byte) 0);
            this.bootParam_buf.put((byte) 0);
            this.bootParam_buf.putInt(0);
            this.bootParam_buf.putInt(0);
            this.bootParam.write(this.bootParam_buf);
        }
    }

    private int getAXIAddr(int addr) {
        String version = ScalarProperties.getString("version.platform");
        int pfMajorVersion = Integer.parseInt(version.substring(0, version.indexOf(".")));
        if (pfMajorVersion == 1) {
            int axiAddr = addr & Integer.MAX_VALUE;
            return axiAddr;
        }
        int axiAddr2 = addr & 1073741823;
        return axiAddr2;
    }
}
