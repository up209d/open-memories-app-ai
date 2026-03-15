package com.sony.imaging.app.util;

import android.util.Log;
import java.security.InvalidParameterException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/* loaded from: classes.dex */
public class Crypt {
    private static final String ALGO_AES = "AES/CBC/PKCS5Padding";
    private static final String ALGO_SHA = "SHA";
    static final boolean DEBUG_CRYPT_OFF = false;
    public static final int HASH_LENGTH_SHA = 20;
    private static final int MIN_LENGTH_AES = 32;
    private static final int MIN_LENGTH_DATA = 52;
    private static final String TAG = "Crypt";
    private static byte[] sSecretWordsSHA = null;
    private static SecretKeySpec sKeyAES = null;

    public static void setKeys(byte[] seed, byte[] word) {
        if (seed != null && word != null) {
            if (checkKeys(seed, word)) {
                sKeyAES = new SecretKeySpec(seed, "AES");
                sSecretWordsSHA = word;
                return;
            }
            return;
        }
        sKeyAES = null;
        sSecretWordsSHA = null;
    }

    private static boolean checkKeys(byte[] seed, byte[] word) {
        if (24 != seed.length || 64 != word.length) {
            throw new InvalidParameterException();
        }
        return true;
    }

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
        System.nanoTime();
        try {
            byte[] hashi = sha(data);
            Log.i(TAG, "data.length = " + data.length);
            Log.i(TAG, "hash.lenagth = " + hashi.length);
            Cipher cipher = Cipher.getInstance(ALGO_AES);
            cipher.init(1, sKeyAES, getRandom());
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
        System.nanoTime();
        return encrypted;
    }

    public static byte[] getSeed() {
        return null;
    }

    public static SecureRandom getRandom() {
        SecureRandom random = new SecureRandom();
        byte[] seed = getSeed();
        if (seed != null) {
            random.setSeed(seed);
        }
        return random;
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
            cipher.init(2, sKeyAES, new IvParameterSpec(iv), getRandom());
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
            md.update(sSecretWordsSHA);
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
