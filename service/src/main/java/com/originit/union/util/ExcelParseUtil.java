package com.originit.union.util;

import com.originit.common.util.POIUtil;
import com.originit.common.util.StringUtil;
import jdk.internal.util.xml.impl.Input;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author xxc、
 */
public class ExcelParseUtil {

    /**
     * 解析电话号码的excel模板
     * @return
     */
    public static List<String> parsePhoneTemplate (InputStream inputStream, String fileName) {
        return POIUtil.customQuery(inputStream,fileName, workbook -> {
            List<String> list = new ArrayList<>();
            Sheet sheet = workbook.getSheetAt(0);
            // 从第二行开始,获取第一列的所有电话
            for (int row = 1; row <= sheet.getLastRowNum(); row++) {
                Row rowObj = sheet.getRow(row);
                if(rowObj == null) {
                    continue;
                }
                // 只要第一列的数据
                final String phone = POIUtil.getCellValue(rowObj.getCell(0));
                // 如果是空的就下一个
                if (StringUtil.isEmpty(phone)) {
                    continue;
                }
                list.add(phone);
            }
            return list;
        });
    }
}
