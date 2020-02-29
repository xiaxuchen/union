package com.originit.common.util;

import com.originit.common.exceptions.ParameterInvalidException;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class POIUtil {
    /**
     * 2003- 版本的excel
     */
    private final static String EXCEL_2003_L =".xls";
    /**
     * 2007+ 版本的excel
     */
    private final static String EXCEL_2007_U =".xlsx";


    public  static List<List<String>> getBankListByExcel(InputStream in, String fileName)
    {
        return getBankListByExcel(in,fileName,null,null);
    }

    /**
     * 描述：获取IO流中的数据，组装成List<List<Object>>对象
     * @param in excel的流
     * @param fileName 文件名
     * @param colCount 列数
     * @param startRow 开始行数
     * @return
     */
    public  static List<List<String>> getBankListByExcel(InputStream in,String fileName,Integer colCount,Integer startRow){
        List<List<String>> list = null;

        //创建Excel工作薄
        Workbook work = getWorkbook(in,fileName);
        if(null == work){
            throw new IllegalStateException("创建Excel工作薄为空！");
        }
        Sheet sheet = null;
        Row row = null;
        Cell cell = null;

        if (startRow == null) {
            startRow = 0;
        }

        list = new ArrayList<>();
        //遍历Excel中所有的sheet
        for (int i = 0; i < work.getNumberOfSheets(); i++) {
            sheet = work.getSheetAt(i);
            if(sheet==null){continue;}

            //遍历当前sheet中的所有行
            for (int j = sheet.getFirstRowNum(); j <= sheet.getLastRowNum(); j++) {
                if (j<startRow) {
                    continue;
                }
                row = sheet.getRow(j);
                if(row==null){continue;}
                //遍历所有的列
                List<String> li = new ArrayList<>();
                for (int y = row.getFirstCellNum(); y < (colCount==null?row.getLastCellNum():colCount); y++) {
                    cell = row.getCell(y);
                    if (cell != null) {
                        li.add(getCellValue(cell));
                    } else {
                        li.add("");
                    }
                }
                list.add(li);
            }
        }
        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalStateException();
        }
        return list;
    }

    /**
     * 获取List<Map>对象
     * @param in 文件流
     * @param fileName 文件名
     * @param header 表头所在的行
     * @return
     */
    public static List<Map<String,String>> getMap(InputStream in, String fileName, int header){
        List<List<String>> list = getBankListByExcel(in,fileName);
        List<String> head = list.get(header);
        List<Map<String,String>> mapList = new ArrayList<>();
        Map<String,String> map;
        for(List<String> l:list)
        {
            if (l == head) {
                continue;
            }
            map = new HashMap<>();
            int i = 0;
            for(String str:l)
            {
                map.put(head.get(i),str);
                i++;
            }
            mapList.add(map);
        }
        return mapList;
    }

    /**
     * 描述：根据文件后缀，自适应上传文件的版本
     */
    public static Workbook getWorkbook(InputStream inStr,String fileName){
        Workbook wb = null;
        String fileType = fileName.substring(fileName.lastIndexOf("."));
        try {
            if(EXCEL_2003_L.equals(fileType)){
                //2003-
                wb = new HSSFWorkbook(inStr);
            }else if(EXCEL_2007_U.equals(fileType)){
                //2007+
                wb = new XSSFWorkbook(inStr);
            }else{
                throw new Exception();
            }
        }catch (Exception e) {
            throw new ParameterInvalidException("解析的文件格式有误！");
        }
        return wb;
    }

    /**
     * 描述：对表格中数值进行格式化
     * @param cell
     * @return
     */
    public static String getCellValue(Cell cell){
        String value = "";
        //格式化number String字符
        DecimalFormat df = new DecimalFormat("0");
        //日期格式化
        SimpleDateFormat sdf = new SimpleDateFormat("yyy-MM-dd");
        //格式化数字
        DecimalFormat df2 = new DecimalFormat("0.00");

        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_STRING:
                value = cell.getRichStringCellValue().getString();
                break;
            case Cell.CELL_TYPE_NUMERIC:
                if("General".equals(cell.getCellStyle().getDataFormatString())){
                    value = df.format(cell.getNumericCellValue());
                }else if("m/d/yy".equals(cell.getCellStyle().getDataFormatString())){
                    value = sdf.format(cell.getDateCellValue());
                }else{
                    value = df2.format(cell.getNumericCellValue());
                }
                break;
            case Cell.CELL_TYPE_BOOLEAN:
                value = String.valueOf(cell.getBooleanCellValue());
                break;
            case Cell.CELL_TYPE_BLANK:
                value = "";
                break;
            default:
                break;
        }
        return value;
    }

    /**
     * 自定义查询
     * @param in excel流
     * @param filename 文件名
     * @param callback 回调
     * @return
     */
    public static <T> T customQuery (InputStream in,String filename,Callback<T> callback) {
        Workbook workbook = getWorkbook(in, filename);
        if (callback == null) {
            throw new IllegalArgumentException("callback can not be null");
        }
        T ret = callback.call(workbook);
        try {
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ret;
    }

    /**
     * 自定义查询的回调
     */
    public interface Callback <T> {

        T call(Workbook workbook);
    }
}