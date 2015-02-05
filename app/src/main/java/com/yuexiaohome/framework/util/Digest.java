package com.yuexiaohome.framework.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by harry on 10/22/14.
 */
public final class Digest
{

    private static ThreadLocal<MessageDigest> sMD5 = new ThreadLocal<MessageDigest>() {

        @Override
        protected MessageDigest initialValue() {
            try {
                return MessageDigest.getInstance("MD5");
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
                return null;
            }
        }
    };

    public static String getMD5(String str) {
        MessageDigest md5 = sMD5.get();
        if (md5 == null) {
            return null;
        }
        md5.update(str.getBytes());
        byte[] digest = md5.digest();
        StringBuilder builder = new StringBuilder(digest.length << 1);
        for (int i = 0; i < digest.length; i++) {
            builder.append(Character.forDigit((digest[i] >> 4) & 0xf, 16));
            builder.append(Character.forDigit(digest[i] & 0xf, 16));
        }
        return builder.toString();
    }

}
