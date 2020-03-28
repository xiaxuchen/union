package com.originit.union.util;

import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import static org.junit.Assert.*;

public class ExcelParseUtilTest {

    @Test
    public void parsePhoneTemplate() {
        try {
            System.out.println(ExcelParseUtil.parsePhoneTemplate(new FileInputStream("D:/phone_template.xls"), "hone_template.xls"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}