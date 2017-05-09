package com.pandaq.mvpdemo.download;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by PandaQ on 2017/5/9.
 * 加密
 */

public class SecretUtils {

    public static String getMD5(String val) {
        byte[] m = null;
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(val.getBytes("UTF-8"));
            m = md5.digest();//加密
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return getString(m);
    }

    private static String getString(byte[] result) {
        StringBuilder sb = new StringBuilder();
        for (byte b : result) {
            int i = b & 0xff;
            if (i <= 0xf) {
                sb.append(0);
            }
            sb.append(Integer.toHexString(i));
        }
        return sb.toString().toLowerCase();
    }

}
