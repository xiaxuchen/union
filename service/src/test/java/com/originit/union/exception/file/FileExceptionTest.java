package com.originit.union.exception.file;

import org.junit.Test;

import java.io.FileNotFoundException;

import static org.junit.Assert.*;

public class FileExceptionTest {

    @Test
    public void name() {
        // 这里表明构造方法只会调用一个
        new FileException("sadfsda");
    }
}