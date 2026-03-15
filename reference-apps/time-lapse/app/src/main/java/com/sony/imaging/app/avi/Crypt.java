package com.sony.imaging.app.avi;

import android.util.Log;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/* loaded from: classes.dex */
public class Crypt {
    private static final String ALGO_AES = "AES/CBC/PKCS5Padding";
    private static final String ALGO_SHA = "SHA";
    public static final boolean DEBUG_CRYPT_OFF = false;
    public static final int HASH_LENGTH_SHA = 20;
    private static final int MIN_LENGTH_AES = 32;
    private static final int MIN_LENGTH_DATA = 52;
    private static final String TAG = "Crypt";
    private static final byte[] SECRET_WORDS_SHA = {83, 111, 110, 121, 32, 68, 105, 103, 105, 116, 97, 108, 32, 73, 109, 97, 103, 105, 110, 103, 32, 80, 108, 97, 121, 32, 77, 101, 109, 111, 114, 105, 101, 115, 32, 67, 97, 109, 101, 114, 97, 32, 65, 112, 112, 115, 32, 50, 48, 49, 50, 47, 49, 50, 47, 50, 49, 32, 70, 114, 105, 100, 97, 121};
    private static final byte[] SEED_AES = {112, 109, 99, 97, 75, 65, 78, 69, 85, 69, 75, 73, 77, 65, 82, 85, 89, 65, 77, 65, 49, 50, 50, 49};
    private static final SecretKeySpec KEY_AES = new SecretKeySpec(SEED_AES, "AES");

    public static byte[] encrypt(byte[] data) {
        return encrypt(data, null);
    }

    public static byte[] encrypt(byte[] data, byte[] hash) {
        byte[] encrypted;
        Log.i(TAG, "encrypt start");
        if (data == null) {
            Log.e(TAG, "encrypt failed!!!");
            return null;
        }
        long t0 = System.nanoTime();
        try {
            byte[] hashi = sha(data);
            Log.i(TAG, "data.length = " + data.length);
            Log.i(TAG, "hash.lenagth = " + hashi.length);
            Cipher cipher = Cipher.getInstance(ALGO_AES);
            cipher.init(1, KEY_AES);
            byte[] iv = cipher.getIV();
            byte[] body = cipher.doFinal(data);
            byte[] encrypted2 = arrayMerge(iv, body);
            encrypted = arrayMerge(encrypted2, hashi);
            if (hash != null) {
                for (int n = 0; n < hashi.length; n++) {
                    hash[n] = hashi[n];
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "encrypt failed!!!");
            encrypted = null;
        }
        long t1 = System.nanoTime();
        Log.i(TAG, "encrypt time = " + ((t1 - t0) / 1000000) + "ms");
        return encrypted;
    }

    public static byte[] decrypt(byte[] data) {
        return decrypt(data, null);
    }

    public static byte[] decrypt(byte[] data, byte[] hash) {
        byte[] decryptedData;
        Log.i(TAG, "data.length = " + data.length);
        Log.i(TAG, "decrypt start");
        if (data == null || data.length < MIN_LENGTH_DATA) {
            Log.e(TAG, "decrypt failed 1!!!");
            return null;
        }
        long t0 = System.nanoTime();
        try {
            Cipher cipher = Cipher.getInstance(ALGO_AES);
            byte[] iv = Arrays.copyOf(data, 16);
            byte[] body = Arrays.copyOfRange(data, 16, data.length - 20);
            byte[] hashEnc = Arrays.copyOfRange(data, data.length - 20, data.length);
            cipher.init(2, KEY_AES, new IvParameterSpec(iv));
            decryptedData = cipher.doFinal(body);
            byte[] hashDec = sha(decryptedData);
            if (!Arrays.equals(hashEnc, hashDec)) {
                decryptedData = null;
                Log.e(TAG, "!!!!!!!!!!!!!!!!!! falsification of data detected !!!!!!!!!!!!!!!!!!!!!!!!");
            } else if (hash != null) {
                for (int n = 0; n < hashDec.length; n++) {
                    hash[n] = hashDec[n];
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "decrypt failed 2!!!");
            Log.e(TAG, e.toString());
            decryptedData = null;
        }
        long t1 = System.nanoTime();
        Log.i(TAG, "decrypted time = " + ((t1 - t0) / 1000000) + "ms");
        return decryptedData;
    }

    private static byte[] sha(byte[] data) throws Exception {
        try {
            MessageDigest md = MessageDigest.getInstance(ALGO_SHA);
            System.out.println(md.getAlgorithm());
            md.reset();
            md.update(data);
            md.update(SECRET_WORDS_SHA);
            return md.digest();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private static byte[] arrayMerge(byte[] a, byte[] b) {
        byte[] c = new byte[a.length + b.length];
        System.arraycopy(a, 0, c, 0, a.length);
        System.arraycopy(b, 0, c, a.length, b.length);
        return c;
    }
}
