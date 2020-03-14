package com.originit.union.api.util;

import org.apache.shiro.util.ByteSource;
import org.junit.Test;

import static org.junit.Assert.*;

public class SHA256UtilTest {

    @Test
    public void sha256() {
        String salt = ByteSource.Util.bytes("xxcisbest").toString();
        System.out.println(SHA256Util.sha256("123456", salt));
        System.out.println(salt);
    }
}