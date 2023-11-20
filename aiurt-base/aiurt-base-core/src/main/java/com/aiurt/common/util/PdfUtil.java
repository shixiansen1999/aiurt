package com.aiurt.common.util;


import com.aspose.cells.License;
import com.aspose.cells.PdfSaveOptions;
import com.aspose.cells.Workbook;
import com.aspose.words.Document;
import com.aspose.words.SaveFormat;
import me.zhyd.oauth.log.Log;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.Modifier;

public class PdfUtil {
    /**
     * word转pdf
     * @param wordFilePath word路径
     */
    public static void wordToPdf(String wordFilePath) {
        Document doc;
        try {
            FileInputStream in = new FileInputStream(wordFilePath);
            removeWaterMark();
            doc = new Document(in);
            String pdfFilePath = getPdfFilePath(wordFilePath);
            FileOutputStream fileOs = new FileOutputStream(pdfFilePath);
            doc.save(fileOs, SaveFormat.PDF);
            fileOs.flush();
            fileOs.close();
        } catch (Exception e) {
            Log.error(e.getMessage(), e);
        }
    }

    /**
     * 去除word转pdf水印
     * 使用反射替换变量
     * @throws Exception 异常
     */
    private static void removeWaterMark() throws Exception {
        Class<?> aClass = Class.forName("com.aspose.words.zzXyu");
        java.lang.reflect.Field zzzxg = aClass.getDeclaredField("zzZXG");
        zzzxg.setAccessible(true);
        java.lang.reflect.Field modifiersField = zzzxg.getClass().getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(zzzxg, zzzxg.getModifiers() & ~Modifier.FINAL);
        // 设置为检验通过
        zzzxg.set(null,new byte[]{76, 73, 67, 69, 78, 83, 69, 68});
    }

    /**
     * excel 转 pdf
     *
     * @param excelFilePath excel文件路径
     */
    public static void excel2pdf(String excelFilePath) {
        excel2pdf(excelFilePath, null, null);
    }

    /**
     * excel 转 pdf
     *
     * @param excelFilePath excel文件路径
     * @param convertSheets 需要转换的sheet
     */
    public static void excel2pdf(String excelFilePath, int[] convertSheets) {
        excel2pdf(excelFilePath, null, convertSheets);
    }

    /**
     * excel 转 pdf
     *
     * @param excelFilePath excel文件路径
     * @param pdfFilePath   pdf文件路径
     */
    public static void excel2pdf(String excelFilePath, String pdfFilePath) {
        excel2pdf(excelFilePath, pdfFilePath, null);
    }

    /**
     * excel 转 pdf
     *
     * @param excelFilePath excel文件路径
     * @param pdfFilePath   pdf文件路径
     * @param convertSheets 需要转换的sheet
     */
    public static void excel2pdf(String excelFilePath, String pdfFilePath, int[] convertSheets) {
        try {

            pdfFilePath = pdfFilePath == null ? getPdfFilePath(excelFilePath) : pdfFilePath;
            // 验证 License
            getLicense();
            Workbook wb = new Workbook(excelFilePath);
            FileOutputStream fileOS = new FileOutputStream(pdfFilePath);
            PdfSaveOptions pdfSaveOptions = new PdfSaveOptions();
            pdfSaveOptions.setOnePagePerSheet(false);
//            pdfSaveOptions.setOnePagePerSheet(true);
            if (null != convertSheets) {
                printSheetPage(wb, convertSheets);
            }
            wb.save(fileOS, pdfSaveOptions);
            fileOS.flush();
            fileOS.close();
            System.out.println("convert success");
        } catch (Exception e) {
            System.out.println("convert failed");
            e.printStackTrace();
        }
    }
    /**
     * 获取 生成的 pdf 文件路径，默认与源文件同一目录
     *
     * @param excelFilePath excel文件
     * @return 生成的 pdf 文件
     */
    private static String getPdfFilePath(String excelFilePath) {
        return excelFilePath.split("\\.")[0] + ".pdf";
    }

    /**
     * 获取 license 去除水印
     * 若不验证则转化出的pdf文档会有水印产生
     */
    private static void getLicense() {
        String licenseFilePath = "excel-license.xml";
        try {
            InputStream is = com.aiurt.common.util.PdfUtil.class.getClassLoader().getResourceAsStream(licenseFilePath);
            License license = new License();
            license.setLicense(is);
        } catch (Exception e) {
            System.out.println("license verify failed");
            e.printStackTrace();
        }
    }

    /**
     * 隐藏workbook中不需要的sheet页。
     *
     * @param sheets 显示页的sheet数组
     */
    private static void printSheetPage(Workbook wb, int[] sheets) {
        for (int i = 1; i < wb.getWorksheets().getCount(); i++) {
            wb.getWorksheets().get(i).setVisible(false);
        }
        if (null == sheets || sheets.length == 0) {
            wb.getWorksheets().get(0).setVisible(true);
        } else {
            for (int i = 0; i < sheets.length; i++) {
                wb.getWorksheets().get(i).setVisible(true);
            }
        }
    }
}
