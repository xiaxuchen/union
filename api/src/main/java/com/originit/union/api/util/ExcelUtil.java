package com.originit.union.api.util;

import com.originit.union.business.bean.ExcelUserBean;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author super  //从指定路径读取Excel的数据
 * @date 2020/2/8 13:08
 * @description 执念
 */
public class ExcelUtil {
    public  static   List<ExcelUserBean> importXLS(String filename){
        ArrayList<ExcelUserBean> list = new ArrayList<>();
        try {
            //String filename="C:/Users/Super丶执念/Desktop/会员信息.xlsx";

            //1、获取文件输入流
            InputStream inputStream = new FileInputStream(filename);
            //2、获取Excel工作簿对象
            if (filename.endsWith("xlsx")){
                XSSFWorkbook workbook= new XSSFWorkbook(inputStream);
                //3、得到Excel工作表对象
                XSSFSheet sheetAt = workbook.getSheetAt(0);
                //4、循环读取表格数据
                for (Row row : sheetAt) {
                    //首行（即表头）不读取
                    if (row.getRowNum() == 0) {
                        continue;
                    }
                    //读取当前行中单元格数据，索引从0开始
                    row.getCell(0).setCellType(Cell.CELL_TYPE_STRING);
                    String userphone = row.getCell(0).getStringCellValue();
                  /*  row.getCell(1).setCellType(Cell.CELL_TYPE_STRING);
                    String usertag = row.getCell(1).getStringCellValue();*/
                    ExcelUserBean excelUserBean = new ExcelUserBean();
                    excelUserBean.setUserphone(userphone);
                   // excelUserBean.setUsertag(usertag);
                    list.add(excelUserBean);
                }
                //5、关闭流
                workbook.close();
            }
            if (filename.endsWith("xls")){
                HSSFWorkbook workbook = new HSSFWorkbook(inputStream);
                //3、得到Excel工作表对象
                HSSFSheet sheetAt = workbook.getSheetAt(0);
                //4、循环读取表格数据
                for (Row row : sheetAt) {
                    //首行（即表头）不读取
                    if (row.getRowNum() == 0) {
                        continue;
                    }
                    //读取当前行中单元格数据，索引从0开始
                    row.getCell(0).setCellType(Cell.CELL_TYPE_STRING);
                    String userphone = row.getCell(0).getStringCellValue();
                   /* row.getCell(1).setCellType(Cell.CELL_TYPE_STRING);
                    String usertag = row.getCell(1).getStringCellValue();*/
                    ExcelUserBean excelUserBean = new ExcelUserBean();
                    excelUserBean.setUserphone(userphone);
                    //excelUserBean.setUsertag(usertag);
                    list.add(excelUserBean);
                }
                //5、关闭流
                workbook.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(list.toString());
            return list;
    }
}
