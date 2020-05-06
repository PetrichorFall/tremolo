package com.ttd.common.util;

import java.security.MessageDigest;
import org.apache.commons.codec.binary.Base64;

public class MD5Utils {

    public static String getMD5Str(String str) throws Exception {
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        String newStr = Base64.encodeBase64String(md5.digest(str.getBytes()));
        return newStr;
    }

}
