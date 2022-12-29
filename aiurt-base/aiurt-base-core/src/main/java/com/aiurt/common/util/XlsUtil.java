package com.aiurt.common.util;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.TemplateExportParams;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.jeecg.common.api.vo.Result;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Slf4j
public class XlsUtil {

    //导入返回结果
    public static Result<?> importReturnRes(int errorLines, int successLines, List<String> errorMessage, boolean isType, String failReportUrl) {
        if (isType) {
            if (errorLines != 0) {
                JSONObject result = new JSONObject(5);
                result.put("isSucceed", false);
                result.put("errorCount", errorLines);
                result.put("successCount", successLines);
                int totalCount = successLines + errorLines;
                result.put("totalCount", totalCount);
                result.put("failReportUrl", failReportUrl);
                Result res = Result.ok(result);
                res.setMessage("文件失败，数据有错误。");
                res.setCode(200);
                return res;
            } else {
                //是否成功
                JSONObject result = new JSONObject(5);
                result.put("isSucceed", true);
                result.put("errorCount", errorLines);
                result.put("successCount", successLines);
                int totalCount = successLines + errorLines;
                result.put("totalCount", totalCount);
                Result res = Result.ok(result);
                res.setMessage("文件导入成功！");
                res.setCode(200);
                return res;
            }
        } else {
            JSONObject result = new JSONObject(5);
            result.put("isSucceed", false);
            result.put("errorCount", errorLines);
            result.put("successCount", successLines);
            int totalCount = successLines + errorLines;
            result.put("totalCount", totalCount);
            Result res = Result.ok(result);
            res.setMessage("导入失败，文件类型不对。");
            res.setCode(200);
            return res;
        }
    }

    //模板
    public static TemplateExportParams getExcelModel(String url) throws IOException {
        //进行模板导出
        Resource resource = new ClassPathResource(url);
        InputStream resourceAsStream = resource.getInputStream();

        //2.获取临时文件
        File fileTemp= new File(url);
        try {
            //将读取到的类容存储到临时文件中，后面就可以用这个临时文件访问了
            FileUtils.copyInputStreamToFile(resourceAsStream, fileTemp);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        String path = fileTemp.getAbsolutePath();
        TemplateExportParams exportParams = new TemplateExportParams(path);
        return exportParams;
    }

    //检查空行
    public static boolean checkObjAllFieldsIsNull(Object object) {
        if (null == object) {
            return true;
        }
        try {
            for (Field f : object.getClass().getDeclaredFields()) {
                f.setAccessible(true);
                if (f.get(object) != null && (StrUtil.isNotEmpty(f.get(object).toString()) && !"1".equals(f.get(object).toString()))) {
                    return false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    public static void getExcel(HttpServletResponse response,String url, String fileName){
        //进行模板导出
        Resource resource = new ClassPathResource(url);
        try {
            InputStream resourceAsStream  = resource.getInputStream();
            //2.获取临时文件
            File fileTemp= new File(url);
            try {
                //将读取到的类容存储到临时文件中，后面就可以用这个临时文件访问了
                FileUtils.copyInputStreamToFile(resourceAsStream, fileTemp);
            } catch (Exception e) {
                log.error(e.getMessage());
            }
            String path = fileTemp.getAbsolutePath();
            TemplateExportParams exportParams = new TemplateExportParams(path);

            Map<Integer, Map<String, Object>> sheetsMap = new HashMap<>();
            Workbook workbook =  ExcelExportUtil.exportExcel(sheetsMap, exportParams);

            response.setHeader("Content-Disposition",
                    "attachment;filename=" + new String(fileName.getBytes("UTF-8"), "iso8859-1"));
            response.setHeader("Content-Disposition", "attachment;filename="+fileName);
            BufferedOutputStream bufferedOutPut = new BufferedOutputStream(response.getOutputStream());
            workbook.write(bufferedOutPut);
            bufferedOutPut.flush();
            bufferedOutPut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public static void outZip(InputStream inputStream,String fileName, ZipOutputStream zipOut) {
        // 缓冲
        byte[] bufferArea = new byte[1024];
        // 将当前文件作为一个zip实体写入压缩流,fileName代表压缩文件中的文件名称
        try {
            zipOut.putNextEntry(new ZipEntry(fileName));
            int length = 0;
            // 最常规IO操作,不必紧张
            while ((length = inputStream.read(bufferArea)) != -1) {
                zipOut.write(bufferArea, 0, length);
            }
            // 解决剩余的
            int remain = inputStream.available();
            byte[] last = new byte[remain];
            inputStream.read(last);
            zipOut.write(last);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
