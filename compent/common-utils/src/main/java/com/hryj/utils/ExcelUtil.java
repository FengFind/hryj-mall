package com.hryj.utils;

import org.apache.poi.ss.usermodel.Workbook;
import sun.misc.BASE64Encoder;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;

/**
 * @author 代廷波
 * @className: ExcelUtil
 * @description:
 * @create 2018/9/27 0027-9:24
 **/
public class ExcelUtil {

    public static String encodeDownloadFilename(String filename, String agent)
            throws IOException {
        if (agent.contains("Firefox")) { // 火狐浏览器
            filename = "=?UTF-8?B?"
                    + new BASE64Encoder().encode(filename.getBytes("utf-8"))
                    + "?=";
            filename = filename.replaceAll("\r\n", "");
        } else { // IE及其他浏览器
            filename = URLEncoder.encode(filename, "utf-8");
            filename = filename.replace("+"," ");
        }
        return filename;
    }

    public static void excelExport(HttpServletResponse response, Workbook workbook, String fileName) throws IOException {

        ServletOutputStream outputStream = null;
        try {
            outputStream = response.getOutputStream();
            response.reset();
            response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(fileName, "utf-8"));
            response.setContentType("application/vnd.ms-excel");
            workbook.write(outputStream);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            // 关闭
            try {
                workbook.close();
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    /**
     * @author 代廷波
     * @description: 是否是2003的excel，返回true是2003
     * @param: filePath
     * @return boolean
     * @create 2018/09/27 15:36
     **/
    public static boolean isExcel2003(String filePath)  {


        return filePath.matches("^.+\\.(?i)(xls)$");
    }

    /**
     * @author 代廷波
     * @description: ：是否是2007的excel，返回true是2007
     * @param: filePath
     * @return boolean
     * @create 2018/09/27 15:36
     **/
    public static boolean isExcel2007(String filePath)  {

        return filePath.matches("^.+\\.(?i)(xlsx)$");
    }

    /**
     * 验证EXCEL文件
     * @param filePath
     * @return
     */
    public static boolean validateExcel(String filePath){
        if (filePath == null || !(isExcel2003(filePath) || isExcel2007(filePath))){
            return false;
        }
        return true;
    }

}
