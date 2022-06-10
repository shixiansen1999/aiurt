package com.aiurt.boot.modules.patrol.utils;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.aiurt.boot.modules.patrol.vo.export.ExportTaskSubmitVO;
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
import java.util.Objects;

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
	public static XSSFWorkbook generateTaskBook(List<ExportTaskSubmitVO> exportList, String url) {

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		//每页条数
		int sizeLen = 1000;
		//控制左边空列数量
		int colLen = 0;


		int[] titleWeight = new int[]{
				20 * WIDTH_UNIT, 45 * WIDTH_UNIT,
				15 * WIDTH_UNIT, 25 * WIDTH_UNIT,
				20 * WIDTH_UNIT, 15 * WIDTH_UNIT,
				25 * WIDTH_UNIT, 20 * WIDTH_UNIT,
				15 * WIDTH_UNIT, 10 * WIDTH_UNIT};


		String head = "巡检单任务列表";

		String[] titleName = {"任务编号", "巡检表名称", "站点"
				, "所属系统", "班组", "巡检频率"
				, "巡检人", "提交时间", "是否异常"
				, "详情"};

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

		Map<Integer, List<ExportTaskSubmitVO>> map = new LinkedHashMap<>();

		//分割
		if (exportList.size() > sizeLen) {
			//记录条数
			int i = 0;
			//记录数量
			int n = 0;
			int size = exportList.size();
			while (i < size) {
				List<ExportTaskSubmitVO> voList = null;
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

			List<ExportTaskSubmitVO> voList = map.get(key);

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
			for (ExportTaskSubmitVO vo : voList) {
				Row row = sheet.createRow(len++);

				for (int i = 0; i < titleLength; i++) {

					Cell cell = row.createCell(colLen + i);
					cell.setCellStyle(i != titleLength - 1 ? textStyle : linkStyle);

					switch (i) {
						case 0:
							//任务编号
							cell.setCellValue(vo.getCode());
							break;
						case 1:
							//巡检表名称
							cell.setCellValue(vo.getName());
							break;
						case 2:
							//站点
							cell.setCellValue(vo.getStationName());
							break;
						case 3:
							//所属系统
							cell.setCellValue(vo.getSystemTypeName());
							break;
						case 4:
							//班组
							cell.setCellValue(vo.getOrganizationName());
							break;
						case 5:
							//巡检频率 1.一天1次 2.一周2次 3.一周1次 4.单次 (手动发放)
							Integer tactics = vo.getTactics();
							String str = null;
							if (vo.getTactics() != null) {
								if (Objects.equals(tactics, 1)) {
									str = "1次/天";
								} else if (Objects.equals(tactics, 2)) {
									str = "2次/周";
								} else if (Objects.equals(tactics, 3)) {
									str = "1次/周";
								} else if (Objects.equals(tactics, 4)) {
									str = "单次";
								}
							}
							cell.setCellValue(str);
							break;
						case 6:
							//巡检人
							cell.setCellValue(vo.getStaffName());
							break;
						case 7:
							//提交时间
							cell.setCellValue(vo.getSubmitTime() != null ? sdf.format(vo.getSubmitTime()) : null);
							break;
						case 8:
							//是否有异常情况
							if (vo.getWarningStatus() != null) {
								cell.setCellValue(vo.getWarningStatus() == 0 ? "否" : "是");
							}
							break;
						case 9:
							cell.setCellValue("查看");
							Hyperlink link = createHelper.createHyperlink(Hyperlink.LINK_URL);
							link.setAddress(url.concat(vo.getId().toString()));
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
	public static void exportResponse(HttpServletResponse response,String fileName){
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
			Class.forName("com.mysql.jdbc.Driver");//加载数据库驱动
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
