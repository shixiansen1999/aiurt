package com.aiurt.boot.modules.fault.utils;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.aiurt.boot.common.result.FaultResult;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Mr.zhao
 * @date 2021/12/29 18:22
 */
public class ExportUtils {


    public static final String FONT_ST = "宋体";

    /**
     * 导出的行列换算单位
     */
    public static final int HEIGHT_UNIT = 20;

    public static final int WIDTH_UNIT = 256;

    /**
     * 生成任务表
     *
     * @param exportList 导出清单
     * @param url        前缀url
     * @return {@code Workbook}
     */
    public static XSSFWorkbook generateTaskBook(List<FaultResult> exportList, String url) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        //每页条数
        int sizeLen = 1000;
        //控制左边空列数量
        int colLen = 0;


        int[] titleWeight = new int[]{
                15 * WIDTH_UNIT, 18 * WIDTH_UNIT,
                15 * WIDTH_UNIT, 15 * WIDTH_UNIT,
                15 * WIDTH_UNIT, 15 * WIDTH_UNIT,
                20 * WIDTH_UNIT, 10 * WIDTH_UNIT,
                20 * WIDTH_UNIT, 20 * WIDTH_UNIT,
                10 * WIDTH_UNIT, 10 * WIDTH_UNIT,
                20 * WIDTH_UNIT, 15 * WIDTH_UNIT,
                25 * WIDTH_UNIT, 25 * WIDTH_UNIT,
                25 * WIDTH_UNIT, 10 * WIDTH_UNIT};


        String head = "故障导出列表";

        String[] titleName = {"线路", "故障编号", "站点"
                , "班组", "系统", "设备"
                , "故障现象", "报修方式", "故障发生时间"
                , "维修完成时间", "状态", "维修人"
                , "挂起说明", "报修编号", "故障影响范围"
                , "故障分析", "维修措施", "查看"};

        int titleLength = titleName.length;

        XSSFWorkbook wb = new XSSFWorkbook();

        if (CollectionUtils.isEmpty(exportList)) {
            return wb;
        }

        XSSFCellStyle headStyle = setCenter(getStyle(wb, FONT_ST, 12, true, false));
        XSSFCellStyle titleStyle = setCenter(getStyle(wb, FONT_ST, 11, true, false));
        XSSFCellStyle textStyle = setCenter(getStyle(wb, FONT_ST, 11, false, false));
        XSSFCellStyle linkStyle = setCenter(getStyle(wb, FONT_ST, 11, false, true));

        textStyle.setWrapText(true);

        Map<Integer, List<FaultResult>> map = new LinkedHashMap<>();

        //分割
        if (exportList.size() > sizeLen) {
            //记录条数
            int i = 0;
            //记录数量
            int n = 0;
            int size = exportList.size();
            while (i < size) {
                List<FaultResult> voList = null;
                if (i + sizeLen < size) {
                    voList = exportList.subList(i, i + sizeLen);
                } else {
                    voList = exportList.subList(i, size);
                }
                map.put(n++, voList);
                i += sizeLen;
            }
        } else {
            //不足1000,则放入一个表里面
            map.put(0, exportList);
        }
        CreationHelper createHelper = wb.getCreationHelper();


        for (Integer key : map.keySet()) {
            //记录行数
            int len = 0;

            List<FaultResult> voList = map.get(key);

            Sheet sheet = wb.createSheet();


            //设置列高
            sheet.setDefaultRowHeight((short) (25 * HEIGHT_UNIT));

            //创建标题头合并单元格
            CellRangeAddress headRangeAddress = new CellRangeAddress(len, len, colLen, colLen + titleLength - 1);
            sheet.addMergedRegion(headRangeAddress);

            Row headRow = sheet.createRow(len++);


            //设置标题样式及值
            Cell headCell = headRow.createCell(colLen);
            headCell.setCellStyle(headStyle);
            headCell.setCellValue(head);

            //标题
            Row titleRow = sheet.createRow(len++);
            for (int i = 0; i < titleLength; i++) {
                Cell cell = titleRow.createCell(i);
                cell.setCellValue(titleName[i]);
                cell.setCellStyle(titleStyle);
                //设置列宽
                sheet.setColumnWidth(i, titleWeight[i]);
            }

            //正文
            for (FaultResult vo : voList) {
                Row row = sheet.createRow(len++);

                for (int i = 0; i < titleLength; i++) {

                    Cell cell = row.createCell(colLen + i);
                    cell.setCellStyle(i != titleLength - 1 ? textStyle : linkStyle);

                    switch (i) {
                        case 0:
                            //线路
                            cell.setCellValue(vo.getLineName());
                            break;
                        case 1:
                            //故障编号
                            cell.setCellValue(vo.getCode());
                            break;
                        case 2:
                            //站点
                            cell.setCellValue(vo.getStation());
                            break;
                        case 3:
                            //班组
                            cell.setCellValue(vo.getDepartName());
                            break;
                        case 4:
                            //系统
                            cell.setCellValue(vo.getSystemName());
                            break;
                        case 5:
                            //设备
                            cell.setCellValue(vo.getDevice());
                            break;
                        case 6:
                            //故障现象
                            cell.setCellValue(vo.getFaultPhenomenon());
                            break;
                        case 7:
                            //报修方式
                            cell.setCellValue(vo.getRepairWay());
                            break;
                        case 8:
                            //故障发生时间
                            cell.setCellValue(vo.getOccurrenceTime() !=null ? sdf.format(vo.getOccurrenceTime()) : null);
                            break;
                        case 9:
                            //维修完成时间
                            cell.setCellValue(vo.getOverTime()!=null ? sdf.format(vo.getOverTime()) : null);
                            break;
                        case 10:
                            //状态
                            cell.setCellValue(vo.getStatusDesc());
                            break;
                        case 11:
                            //维修人
                            cell.setCellValue(vo.getCreateByName());
                            break;
                        case 12:
                            //挂起说明
                            cell.setCellValue(StringUtils.isNotBlank(vo.getRemark()) ? vo.getRemark() : null);
                            break;
                        case 13:
                            //报修编号
                            cell.setCellValue(StringUtils.isNotBlank(vo.getRepairCode()) ? vo.getRepairCode() : null);
                            break;
                        case 14:
                            //故障影响范围
                            cell.setCellValue(StringUtils.isNotBlank(vo.getScope()) ? vo.getScope() :null);
                            break;
                        case 15:
                            //故障分析
                            cell.setCellValue(StringUtils.isNotBlank(vo.getFaultAnalysis()) ? vo.getFaultAnalysis() : null);
                            break;
                        case 16:
                            //维修措施
                            cell.setCellValue(StringUtils.isNotBlank(vo.getMaintenanceMeasures()) ? vo.getMaintenanceMeasures() : null);
                            break;
                        case 17:
                            cell.setCellValue("查看");
                            Hyperlink link = createHelper.createHyperlink(Hyperlink.LINK_URL);
                            link.setAddress(url.concat("?code="+vo.getCode()));
                            cell.setHyperlink(link);
                            break;
                        default:
                            break;
                    }
                }
            }
        }


        return wb;

    }


    /**
     * 设置字体格式
     *
     * @param wb       表对象
     * @param fontName 字体名称
     * @param fontSize 字号
     * @param isBold   是否加粗
     * @return {@link CellStyle}
     */
    private static XSSFCellStyle getStyle(XSSFWorkbook wb, String fontName, int fontSize, boolean isBold, boolean isLink) {
        XSSFCellStyle style = wb.createCellStyle();
        XSSFFont font = wb.createFont();
        //字体
        font.setFontName(fontName);
        //字号
        font.setFontHeightInPoints((short) fontSize);
        //加粗
        font.setBold(isBold);
        if (isLink) {
            //设置蓝色和下划线
            font.setColor((short) 12);
            font.setUnderline((byte) 1);
        }

        style.setFont(font);
        return style;
    }

    /**
     * 设置居中
     *
     * @param style 样式
     * @return {@link CellStyle}
     */
    private static XSSFCellStyle setCenter(XSSFCellStyle style) {
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }


    /**
     * 防转义
     *
     * @param response 响应
     * @param fileName 文件名称
     */
    public static void exportResponse(HttpServletResponse response, String fileName) {
        try {
            try {
                fileName = new String(fileName.getBytes(), "ISO8859-1");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            response.setContentType("application/octet-stream;charset=ISO8859-1");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
            response.addHeader("Pargam", "no-cache");
            response.addHeader("Cache-Control", "no-cache");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    public static void main(String[] args) throws Exception {
        try {
			//加载数据库驱动
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        String url = "jdbc:mysql://116.62.143.85:3306/swsc_copsms?characterEncoding=UTF-8&useUnicode=true&useSSL=false";
        String userName = "webuser";
        String password = "123456root";
        Connection con = null;
        try {
            con = DriverManager.getConnection(url, userName, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        PreparedStatement statement = con.prepareStatement("select * from sys_user");
        ResultSet resultSet = statement.executeQuery();

        while (resultSet.next()) {
            System.out.println(resultSet.getString(1));
        }

    }
}
