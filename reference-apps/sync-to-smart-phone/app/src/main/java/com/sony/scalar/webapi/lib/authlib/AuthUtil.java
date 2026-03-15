package com.sony.scalar.webapi.lib.authlib;

import com.sony.imaging.app.base.shooting.camera.ISOSensitivityController;
import com.sony.imaging.app.synctosmartphone.webapi.AutoSyncConstants;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.TimeZone;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/* loaded from: classes.dex */
public class AuthUtil {
    public static String generatePinCode() {
        Random rnd = new Random();
        int ran = rnd.nextInt(10000);
        if (ran >= 0 && ran <= 9) {
            String pinCode = "000" + String.valueOf(ran);
            return pinCode;
        }
        if (10 <= ran && ran <= 99) {
            String pinCode2 = "00" + String.valueOf(ran);
            return pinCode2;
        }
        if (100 <= ran && ran <= 999) {
            String pinCode3 = ISOSensitivityController.ISO_AUTO + String.valueOf(ran);
            return pinCode3;
        }
        String pinCode4 = String.valueOf(ran);
        return pinCode4;
    }

    public static String getExpirationTimeFormat(long timeMsec) {
        Date date = new Date(timeMsec);
        SimpleDateFormat sd = new SimpleDateFormat("EEE, d-MMM-yyyy HH:mm:ss");
        sd.setTimeZone(TimeZone.getTimeZone("GMT"));
        String ret = String.valueOf(sd.format(date)) + " GMT";
        return ret;
    }

    public static String generateRandomStr(String salt) {
        Random rnd = new Random(Runtime.getRuntime().freeMemory());
        int ran = rnd.nextInt(Integer.MAX_VALUE);
        long now = System.currentTimeMillis();
        String key = String.valueOf(String.valueOf(ran)) + String.valueOf(now);
        String sessionId = generateHashCode(key, salt);
        return sessionId;
    }

    public static String generateHashCode(String str, String key) {
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(AutoSyncConstants.TEXT_ENCODING_UTF8), AuthConfig.ALGORISM);
            Mac mac = Mac.getInstance(AuthConfig.ALGORISM);
            mac.init(secretKeySpec);
            byte[] result = mac.doFinal(str.getBytes(AutoSyncConstants.TEXT_ENCODING_UTF8));
            return toHexString(result);
        } catch (UnsupportedEncodingException e) {
            DevLog.stackTrace(e);
            return "";
        } catch (InvalidKeyException e2) {
            DevLog.stackTrace(e2);
            return "";
        } catch (NoSuchAlgorithmException e3) {
            DevLog.stackTrace(e3);
            return "";
        }
    }

    public static byte[] generateDevHash2(String str) {
        try {
            MessageDigest digest = MessageDigest.getInstance(AuthConfig.DEVHASH2_ALGORISM);
            digest.update(str.getBytes(AutoSyncConstants.TEXT_ENCODING_UTF8));
            return digest.digest();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        } catch (NoSuchAlgorithmException e2) {
            e2.printStackTrace();
            return null;
        }
    }

    public static String toHexString(byte[] bytes) {
        StringBuffer buff = new StringBuffer(bytes.length * 2);
        for (byte b : bytes) {
            String b2 = Integer.toHexString(b & 255);
            if (b2.length() == 1) {
                buff.append(ISOSensitivityController.ISO_AUTO);
            }
            buff.append(b2);
        }
        return buff.toString();
    }
}
