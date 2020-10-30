package com.example.demo;

import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;



public class ExcelToBasDicts {
    public static void main(String[] args) throws Exception {

        Connection conn = DBUtils.getConn();
        ExcelToBasDicts test = new ExcelToBasDicts();
        String filepath = "/Users/liu/bingzhong/123.xlsx"; //文件夹路径
        try {
            test.excelToOracle(conn, new File(filepath));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBUtils.closeConn(conn);
        }


    }


    public void excelToOracle(Connection conn, File file) {
        String code = "";
        String name = "";
        String sql = "insert into BAS_DICTS (DICTID, HOSNUM, SYSNAME, NEKEY, CONTENTS, OPTION01, OPTION02, OPTION08, INPUTCPY, INPUTCWB,\n" +
                "                       ISDELETED, TRACELOG, UPDATE_TIME)\n" +
                "values (sys_guid(), '2901349',\n" +
                "        '基础', '23', ?, ?, ?, 3, ?, ?, 'N', sysdate, sysdate);";
        PreparedStatement pstmt = DBUtils.getPstmt(conn, sql);
        BufferedInputStream in = null;
        try {
            in = new BufferedInputStream(new FileInputStream(file));
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        }
        // 创建新的Excel 工作簿
        XSSFWorkbook workbook = null;
        try {
            workbook = new XSSFWorkbook(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 得到工作簿中的第一个表索引即为excel下的sheet1,sheet2,sheet3...
        Sheet sheet = workbook.getSheetAt(0);
        int firstRowIndex = sheet.getFirstRowNum() + 1;   //第一行是列名，所以不读
        int lastRowIndex = sheet.getLastRowNum();
        for (int rIndex = firstRowIndex; rIndex <= lastRowIndex; rIndex++) {   //遍历行
            System.out.println("rIndex: " + rIndex);
            Row row = sheet.getRow(rIndex);
            if (row == null) {
                continue;
            }
            Cell cell = null;
            for (short columnIndex = 0; columnIndex <= row.getLastCellNum(); columnIndex++) {
                String value = "";
                cell = row.getCell(columnIndex);
                if (cell != null) {
                    // 注意：一定要设成这个，否则可能会出现乱码
                    // cell.setEncoding(HSSFCell.ENCODING_UTF_16);
                    switch (cell.getCellType()) {
                        case XSSFCell.CELL_TYPE_STRING:
                            value = cell.getStringCellValue();
                            break;
                        case XSSFCell.CELL_TYPE_NUMERIC:
                            if (HSSFDateUtil.isCellDateFormatted(cell)) {
                                Date date = cell.getDateCellValue();
                                if (date != null) {
                                    value = new SimpleDateFormat("yyyy-MM-dd")
                                            .format(date);
                                } else {
                                    value = "";
                                }
                            } else {
                                value = new DecimalFormat("0").format(cell
                                        .getNumericCellValue());
                            }
                            break;
                        case XSSFCell.CELL_TYPE_FORMULA:
                            // 导入时如果为公式生成的数据则无值
                            if (!cell.getStringCellValue().equals("")) {
                                value = cell.getStringCellValue();
                            } else {
                                value = cell.getNumericCellValue() + "";
                            }
                            break;
                        case XSSFCell.CELL_TYPE_BLANK:
                            break;
                        case XSSFCell.CELL_TYPE_ERROR:
                            value = "";
                            break;
                        case XSSFCell.CELL_TYPE_BOOLEAN:
                            value = (cell.getBooleanCellValue() == true ? "Y" : "N");
                            break;
                        default:
                            value = "";
                    }
                }
                if (columnIndex == 0 && value.trim().equals("")) {
                    break;
                }
                //excel表格中字段顺序为:用户名,密码,电话和地址,为方便起见假设字段一一对应
                if (columnIndex == 0) {
                    code = value;
                } else if (columnIndex == 1) {
                    name = value;
                }
                System.out.println(value);
            }
            try {
                pstmt.setString(1, code);
                pstmt.setString(2, name);
                pstmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        DBUtils.closePstmt(pstmt);
    }

}

