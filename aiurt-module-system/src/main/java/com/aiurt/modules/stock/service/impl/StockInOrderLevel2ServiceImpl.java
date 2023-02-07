package com.aiurt.modules.stock.service.impl;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import cn.afterturn.easypoi.excel.entity.TemplateExportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import cn.afterturn.easypoi.util.PoiMergeCellUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.boot.standard.dto.InspectionCodeContentDTO;
import com.aiurt.boot.standard.dto.InspectionCodeExcelDTO;
import com.aiurt.boot.standard.dto.InspectionCodeImportDTO;
import com.aiurt.boot.team.entity.EmergencyTeam;
import com.aiurt.boot.team.entity.EmergencyTrainingProgram;
import com.aiurt.common.aspect.annotation.Dict;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.util.XlsExport;
import com.aiurt.common.util.XlsUtil;
import com.aiurt.common.util.oConvertUtils;
import com.aiurt.modules.major.entity.CsMajor;
import com.aiurt.modules.major.service.ICsMajorService;
import com.aiurt.modules.material.entity.MaterialBase;
import com.aiurt.modules.material.service.IMaterialBaseService;
import com.aiurt.modules.stock.dto.StockInOrderLevel2DTO;
import com.aiurt.modules.stock.dto.StockInOrderLevel2ExportDTO;
import com.aiurt.modules.stock.dto.StockIncomingMaterialsDTO;
import com.aiurt.modules.stock.dto.StockIncomingMaterialsExportDTO;
import com.aiurt.modules.stock.entity.*;
import com.aiurt.modules.stock.entity.StockIncomingMaterials;
import com.aiurt.modules.stock.mapper.StockInOrderLevel2Mapper;
import com.aiurt.modules.stock.mapper.StockIncomingMaterialsMapper;
import com.aiurt.modules.stock.service.*;
import com.aiurt.modules.subsystem.entity.CsSubsystem;
import com.aiurt.modules.subsystem.service.ICsSubsystemService;
import com.aiurt.modules.system.entity.SysDepart;
import com.aiurt.modules.system.service.ISysDepartService;
import com.aiurt.modules.system.service.impl.SysBaseApiImpl;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFDataValidationConstraint;
import org.apache.poi.xssf.usermodel.XSSFDataValidationHelper;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.DictModel;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @Description:
 * @Author: swsc
 * @Date:   2021-09-15
 * @Version: V1.0
 */
@Service
public class StockInOrderLevel2ServiceImpl extends ServiceImpl<StockInOrderLevel2Mapper, StockInOrderLevel2> implements IStockInOrderLevel2Service {

	@Autowired
	private StockInOrderLevel2Mapper stockInOrderLevel2Mapper;
	@Autowired
	private IStockIncomingMaterialsService stockIncomingMaterialsService;
	@Autowired
	private IMaterialBaseService materialBaseService;
	@Autowired
	private IStockLevel2Service stockLevel2Service;
	@Autowired
	private SysBaseApiImpl sysBaseApi;
	@Autowired
	private ICsMajorService csMajorService;
	@Autowired
	private ICsSubsystemService csSubsystemService;
	@Autowired
	private IStockLevel2CheckService iStockLevel2CheckService;
	@Autowired
	private IStockLevel2CheckDetailService iStockLevel2CheckDetailService;
	@Autowired
	private IStockLevel2InfoService iStockLevel2InfoService;
	@Autowired
	private ISysDepartService iSysDepartService;
	@Autowired
	private ISysBaseAPI  iSysBaseAPI;
	@Autowired
	private IMaterialBaseService iMaterialBaseService;
	@Value("${jeecg.path.errorExcelUpload}")
	private String errorExcelUpload;

	@Override
	public StockInOrderLevel2 getInOrderCode() throws ParseException {
		QueryWrapper<StockInOrderLevel2> queryWrapper = new QueryWrapper<>();
		String str = "RK";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		str += sdf.format(new Date());
		queryWrapper.eq("del_flag", CommonConstant.DEL_FLAG_0);
		queryWrapper.likeRight("order_code",str);
		queryWrapper.orderByDesc("create_time");
		queryWrapper.last("limit 1");
		StockInOrderLevel2 stockInOrderLevel2 = stockInOrderLevel2Mapper.selectOne(queryWrapper);
		String format = "";
		if(stockInOrderLevel2 != null){
			String code = stockInOrderLevel2.getOrderCode();
			String numstr = code.substring(code.length()-3);
			format = String.format("%03d", Long.parseLong(numstr) + 1);
		}else{
			format = "001";
		}
		StockInOrderLevel2 stockInOrderLevel2res = new StockInOrderLevel2();
		stockInOrderLevel2res.setOrderCode(str + format);;
		return stockInOrderLevel2res;
	}

	@Override
	public void add(StockInOrderLevel2 stockInOrderLevel2) {
		String userId = stockInOrderLevel2.getUserId();
		String[] ids = new String[1];
		ids[0] = userId;
		List<LoginUser> loginUsers = sysBaseApi.queryAllUserByIds(ids);
		if(loginUsers != null && loginUsers.size()>0){
			stockInOrderLevel2.setOrgCode(loginUsers.get(0).getOrgCode());
		}
		this.save(stockInOrderLevel2);
		List<StockIncomingMaterials> stockIncomingMaterialsList = stockInOrderLevel2.getStockIncomingMaterialsList();
		if(stockIncomingMaterialsList != null && stockIncomingMaterialsList.size()>0){
			stockIncomingMaterialsList.stream().forEach(s ->s.setInOrderCode(stockInOrderLevel2.getOrderCode()));
			stockIncomingMaterialsService.saveBatch(stockIncomingMaterialsList);
		}
	}

	@Override
	public boolean edit(StockInOrderLevel2 stockInOrderLevel2) {
		String code = stockInOrderLevel2.getOrderCode();
		QueryWrapper<StockIncomingMaterials> queryWrapper = new QueryWrapper<StockIncomingMaterials>();
		queryWrapper.eq("in_order_code",code);
		stockIncomingMaterialsService.remove(queryWrapper);
		List<StockIncomingMaterials> stockIncomingMaterialsList = stockInOrderLevel2.getStockIncomingMaterialsList();
		if(stockIncomingMaterialsList != null && stockIncomingMaterialsList.size()>0){
			stockIncomingMaterialsList.stream().forEach(s ->s.setInOrderCode(stockInOrderLevel2.getOrderCode()));
			stockIncomingMaterialsService.saveBatch(stockIncomingMaterialsList);
		}
		boolean ok = this.updateById(stockInOrderLevel2);
		return ok;
	}

	@Override
	public boolean submitInOrderStatus(String status, String code, StockInOrderLevel2 stockInOrderLevel2) throws ParseException {
		String warehouseCode = stockInOrderLevel2.getWarehouseCode();
		List<StockLevel2Check> stockLevel2CheckList = iStockLevel2CheckService.list(new QueryWrapper<StockLevel2Check>().eq("del_flag", CommonConstant.DEL_FLAG_0)
				.eq("warehouse_code",warehouseCode).ne("status",CommonConstant.STOCK_LEVEL2_CHECK_STATUS_5));
		stockInOrderLevel2.setStatus(status);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		Date stockInTime = sdf.parse(sdf.format(new Date()));
		stockInOrderLevel2.setEntryTime(stockInTime);
		List<StockIncomingMaterials> stockIncomingMaterialsList = stockIncomingMaterialsService.list(new QueryWrapper<StockIncomingMaterials>().eq("in_order_code",code).eq("del_flag", CommonConstant.DEL_FLAG_0));
		if(stockIncomingMaterialsList != null && stockIncomingMaterialsList.size()>0){
			for(StockIncomingMaterials stockIncomingMaterials : stockIncomingMaterialsList){
				//物资编号
				String materialCode = stockIncomingMaterials.getMaterialCode();
				//数量
				Integer number = stockIncomingMaterials.getNumber();
				StockLevel2 stockLevel2 = stockLevel2Service.getOne(new QueryWrapper<StockLevel2>().eq("material_code",materialCode).eq("warehouse_code",warehouseCode).eq("del_flag", CommonConstant.DEL_FLAG_0));
				if(stockLevel2 != null){
					Integer num = stockLevel2.getNum();
					stockLevel2.setNum(num + number);
					stockLevel2.setStockInTime(stockInTime);
					stockLevel2Service.updateById(stockLevel2);
				}else{
					StockLevel2Info stockLevel2Info = iStockLevel2InfoService.getOne(new QueryWrapper<StockLevel2Info>().eq("warehouse_code",warehouseCode).eq("del_flag", CommonConstant.DEL_FLAG_0));
					String organizationId = stockLevel2Info.getOrganizationId();
					SysDepart sysDepart = iSysDepartService.getById(organizationId);
					MaterialBase materialBase = materialBaseService.getOne(new QueryWrapper<MaterialBase>().eq("code",materialCode));
					StockLevel2 stockLevel2new = new StockLevel2();
					stockLevel2new.setMaterialCode(materialCode);
					stockLevel2new.setBaseTypeCode(materialBase.getBaseTypeCodeCc());
					stockLevel2new.setWarehouseCode(warehouseCode);
					stockLevel2new.setNum(number);
					stockLevel2new.setOrgCode(sysDepart.getOrgCode());
					stockLevel2new.setMajorCode(materialBase.getMajorCode());
					stockLevel2new.setSystemCode(materialBase.getSystemCode());
					stockLevel2new.setStockInTime(stockInTime);
					stockLevel2Service.save(stockLevel2new);
				}
				if(stockLevel2CheckList != null && stockLevel2CheckList.size()>0){
					for(StockLevel2Check stockLevel2Check : stockLevel2CheckList){
						String stockCheckCode = stockLevel2Check.getStockCheckCode();
						StockLevel2CheckDetail stockLevel2CheckDetail = iStockLevel2CheckDetailService.getOne(new QueryWrapper<StockLevel2CheckDetail>()
										.eq("material_code",materialCode).eq("warehouse_code",warehouseCode).eq("del_flag", CommonConstant.DEL_FLAG_0)
										.eq("stock_check_code",stockCheckCode));
						if(stockLevel2CheckDetail != null){
							Integer num = stockLevel2CheckDetail.getActualNum()==null?0:stockLevel2CheckDetail.getActualNum();
							stockLevel2CheckDetail.setBookNumber(num + number);
							iStockLevel2CheckDetailService.updateById(stockLevel2CheckDetail);
						}else{
							MaterialBase materialBase = materialBaseService.getOne(new QueryWrapper<MaterialBase>().eq("code",materialCode));
							Double price = materialBase.getPrice()==null?0.00:Double.valueOf(materialBase.getPrice());
							StockLevel2CheckDetail stockLevel2CheckDetailknew = new StockLevel2CheckDetail();
							stockLevel2CheckDetailknew.setStockCheckCode(stockCheckCode);
							stockLevel2CheckDetailknew.setMaterialCode(materialCode);
							stockLevel2CheckDetailknew.setWarehouseCode(warehouseCode);
							stockLevel2CheckDetailknew.setBookNumber(number);
							stockLevel2CheckDetailknew.setBookValue((price * number)+"");
							iStockLevel2CheckDetailService.save(stockLevel2CheckDetailknew);
						}
					}
				}

			}
		}
		boolean ok = this.updateById(stockInOrderLevel2);
		return ok;
	}

	@Override
	public void eqExport(String ids, HttpServletRequest request, HttpServletResponse response) {
		String[] split = ids.split(",");
		List<String> strings = Arrays.asList(split);
		// 过滤选中数据
		List<StockInOrderLevel2> list = this.list(new QueryWrapper<StockInOrderLevel2>().in("id", strings).orderByDesc("create_time"));
		//设置相应头
		response.setContentType("Application/excel");
		try {
			response.addHeader("Content-Disposition", "attachment;filename="
					+ new String("物资提报数据导出".getBytes("GBK"), "ISO8859_1")
					+ ".xls");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		//读取excel模板
		XlsExport excel = new XlsExport();
		HSSFWorkbook hbook = excel.getWorkbook();
		HSSFCellStyle hstyle = hbook.createCellStyle();
		hstyle.setAlignment(HorizontalAlignment.CENTER);
		hstyle.setVerticalAlignment(VerticalAlignment.CENTER);
		//设置边框样式
		int rowIndex = 0;
		if(list != null && list.size()>0){
			for(StockInOrderLevel2 stockInOrderLevel2 : list){
				String code = stockInOrderLevel2.getOrderCode();
				QueryWrapper<StockIncomingMaterials> queryWrapper = new QueryWrapper<StockIncomingMaterials>();
				queryWrapper.eq("in_order_code",code).eq("del_flag", CommonConstant.DEL_FLAG_0);
				List<StockIncomingMaterials> materials = stockIncomingMaterialsService.list(queryWrapper);
				stockInOrderLevel2.setStockIncomingMaterialsList(materials);
				String userid = stockInOrderLevel2.getUserId()==null?"":stockInOrderLevel2.getUserId();
				String username = sysBaseApi.translateDictFromTable("sys_user", "realname", "id", userid);
				String warehouseCode = stockInOrderLevel2.getWarehouseCode()==null?"":stockInOrderLevel2.getWarehouseCode();
				String warehouseCodename = sysBaseApi.translateDictFromTable("stock_level2_info","warehouse_name","warehouse_code",warehouseCode);
				String statuscode = stockInOrderLevel2.getStatus()==null?"":stockInOrderLevel2.getStatus().toString();
				String statusname = sysBaseApi.translateDict("stock_in_order_level2_status",statuscode);
				excel.createRow(rowIndex++);
				excel.setCell(0, "基本信息");
				int num = 10;
				for(int i = 1; i<num; i++){
					excel.setCell(i, "");
				}
				excel.createRow(rowIndex++);
				excel.setCell(0, "");
				excel.setCell(1, "入库单号");
				excel.setCell(2, code==null?"":code);
				excel.setCell(3, "");
				excel.setCell(4, "入库时间");
				Date entryTime = stockInOrderLevel2.getEntryTime();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
				excel.setCell(5, sdf.format(entryTime));
				excel.setCell(6, "");
				excel.setCell(7, "入库人");
				excel.setCell(8, username==null?"":username);
				excel.setCell(9, "");
				excel.createRow(rowIndex++);
				excel.setCell(0, "");
				excel.setCell(1, "入库仓库");
				excel.setCell(2, warehouseCodename==null?"":warehouseCodename);
				excel.setCell(3, "");
				excel.setCell(4, "入库单状态");
				excel.setCell(5, statusname==null?"":statusname);
				excel.setCell(6, "");
				excel.setCell(7, "备注");
				excel.setCell(8, stockInOrderLevel2.getNote()==null?"":stockInOrderLevel2.getNote());
				excel.setCell(9, "");
				excel.createRow(rowIndex++);
				excel.setCell(0, "提报物资清单");
				for(int i = 1; i<num; i++){
					excel.setCell(i, "");
				}
				excel.createRow(rowIndex++);
				excel.setCell(0, "");
				excel.setCell(1, "所属专业");
				excel.setCell(2, "所属子系统");
				excel.setCell(3, "物资分类");
				excel.setCell(4, "物资编号");
				excel.setCell(5, "物资名称");
				excel.setCell(6, "物资类型");
				excel.setCell(7, "存放仓库");
				excel.setCell(8, "入库数量");
				excel.setCell(9, "单位");
				if(materials != null && materials.size()>0){
					for(StockIncomingMaterials stockIncomingMaterials : materials){
						String wzcode = stockIncomingMaterials.getMaterialCode()==null?"":stockIncomingMaterials.getMaterialCode();
						MaterialBase materialBase = materialBaseService.getOne(new QueryWrapper<MaterialBase>().eq("code",wzcode));
						materialBase = materialBaseService.translate(materialBase);
						CsMajor csMajor = csMajorService.getOne(new QueryWrapper<CsMajor>().eq("major_code",materialBase.getMajorCode()).eq("del_flag", CommonConstant.DEL_FLAG_0));
						String zyname = csMajor==null?"":csMajor.getMajorName();
						CsSubsystem csSubsystem = csSubsystemService.getOne(new QueryWrapper<CsSubsystem>().eq("system_code",materialBase.getSystemCode()).eq("del_flag", CommonConstant.DEL_FLAG_0));
						String zxyname = csSubsystem==null?"":csSubsystem.getSystemName();
						String wztype = materialBase.getType()==null?"":materialBase.getType().toString();
						String wztypename = sysBaseApi.translateDict("material_type",wztype);
						String unitcode = materialBase.getUnit()==null?"":materialBase.getUnit();
						String unitname = sysBaseApi.translateDict("materian_unit",unitcode);
						excel.createRow(rowIndex++);
						excel.setCell(0, "");
						excel.setCell(1, zyname==null?"":zyname);
						excel.setCell(2, zxyname==null?"":zxyname);
						excel.setCell(3, materialBase.getBaseTypeCodeCcName()==null?"":materialBase.getBaseTypeCodeCcName());
						excel.setCell(4, materialBase.getCode());
						excel.setCell(5, materialBase.getName());
						excel.setCell(6, wztypename==null?"":wztypename);
						excel.setCell(7, warehouseCodename==null?"":warehouseCodename);
						excel.setCell(8, stockIncomingMaterials.getNumber()==null?"":stockIncomingMaterials.getNumber().toString());
						excel.setCell(9, unitname==null?"":unitname);
					}
				}
				excel.createRow(rowIndex++);
			}
		}
		excel.exportXls(response);
	}

	@Override
	public void exportXls(HttpServletRequest request, HttpServletResponse response, StockInOrderLevel2ExportDTO stockInOrderLevel2ExportDTO) {
		// 封装数据
		List<StockInOrderLevel2ExportDTO> pageList = this.getStockIncomingMaterialsList(stockInOrderLevel2ExportDTO);
		List<StockInOrderLevel2ExportDTO> exportList = null;
		// 过滤选中数据
		String selections = request.getParameter("selections");
		if (oConvertUtils.isNotEmpty(selections)) {
			List<String> selectionList = Arrays.asList(selections.split(","));
			exportList = pageList.stream().filter(item -> selectionList.contains(item.getId())).collect(Collectors.toList());
		} else {
			exportList = pageList;
		}
		LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		String title = "二级库入库导出数据";
		cn.afterturn.easypoi.excel.entity.ExportParams exportParams = new ExportParams(title + "报表", "导出人:" + sysUser.getRealname(), ExcelType.XSSF);
		//调用ExcelExportUtil.exportExcel方法生成workbook
		Workbook wb = cn.afterturn.easypoi.excel.ExcelExportUtil.exportExcel(exportParams, StockInOrderLevel2ExportDTO.class, exportList);
		String fileName = "二级库入库导出数据";
		try {
			response.setHeader("Content-Disposition",
					"attachment;filename=" + new String(fileName.getBytes("UTF-8"), "iso8859-1"));
			//xlsx格式设置
			response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
			BufferedOutputStream bufferedOutPut = new BufferedOutputStream(response.getOutputStream());
			wb.write(bufferedOutPut);
			bufferedOutPut.flush();
			bufferedOutPut.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取excel表格数据
	 *
	 * @param stockInOrderLevel2ExportDTO
	 * @return
	 */
	private List<StockInOrderLevel2ExportDTO> getStockIncomingMaterialsList(StockInOrderLevel2ExportDTO stockInOrderLevel2ExportDTO) {
		List<StockInOrderLevel2ExportDTO> StockInOrderLevel2List = stockInOrderLevel2Mapper.getList(stockInOrderLevel2ExportDTO);
		for (StockInOrderLevel2ExportDTO dto : StockInOrderLevel2List) {
			//启动日期转换
			String entryTime = dto.getEntryTime();
			entryTime = cn.hutool.core.date.DateUtil.format(DateUtil.parse(entryTime), "yyyy-MM-dd");
			dto.setEntryTime(entryTime);
			//入库单状态
			List<DictModel> stockStatus = sysBaseApi.getDictItems("stock_in_order_level2_status");
			stockStatus= stockStatus.stream().filter(f -> (String.valueOf(dto.getStatus())).equals(f.getValue())).collect(Collectors.toList());
			String status = stockStatus.stream().map(DictModel::getText).collect(Collectors.joining());
			dto.setStatus(status);
			//入库仓库
			QueryWrapper<StockLevel2Info> infoQueryWrapper = new QueryWrapper<>();
			infoQueryWrapper.eq("del_flag", CommonConstant.DEL_FLAG_0);
			infoQueryWrapper.eq("status", CommonConstant.STOCK_LEVEL2_STATUS_1);
			infoQueryWrapper.eq("warehouse_code",dto.getWarehouseCode());
			infoQueryWrapper.orderByDesc("create_time");
			StockLevel2Info one = iStockLevel2InfoService.getOne(infoQueryWrapper);
			dto.setWarehouseName(one.getWarehouseName());
			//入库人
			LoginUser userById = iSysBaseAPI.getUserById(dto.getUserId());
			if(ObjectUtil.isNotEmpty(userById)){
				dto.setRealName(userById.getRealname());
			}

			if (CollUtil.isEmpty(StockInOrderLevel2List)) {
				return StockInOrderLevel2List;
			}
			// 物资清单
			if (ObjectUtil.isEmpty(dto)) {
				continue;
			}
			List<StockIncomingMaterialsExportDTO> stockIncomingMaterialsExportDTOList = stockInOrderLevel2Mapper.selectByStockInOrderLevel2Id(dto.getOrderCode());
			for (StockIncomingMaterialsExportDTO stockIncomingMaterialsExportDTO : stockIncomingMaterialsExportDTOList) {
				if (StrUtil.isNotBlank(stockIncomingMaterialsExportDTO.getMaterialCode())){
					String materialCode = stockIncomingMaterialsExportDTO.getMaterialCode();

					MaterialBase materialBase = materialBaseService.getOne(new QueryWrapper<MaterialBase>().eq("code", materialCode).eq("del_flag",0));
					if(ObjectUtil.isNotEmpty(materialBase)){
						//物资类型
						List<DictModel> materialType = sysBaseApi.getDictItems("material_type");
						materialType= materialType.stream().filter(f -> (String.valueOf(materialBase.getType())).equals(f.getValue())).collect(Collectors.toList());
						String type = materialType.stream().map(DictModel::getText).collect(Collectors.joining());
						stockIncomingMaterialsExportDTO.setType(type);
						//单位
						List<DictModel> materialUnit = sysBaseApi.getDictItems("materian_unit");
						materialUnit= materialUnit.stream().filter(f -> (String.valueOf(materialBase.getUnit())).equals(f.getValue())).collect(Collectors.toList());
						String unit = materialUnit.stream().map(DictModel::getText).collect(Collectors.joining());
						stockIncomingMaterialsExportDTO.setUnit(unit);
						//专业
						CsMajor csMajor = csMajorService.getOne(new QueryWrapper<CsMajor>().eq("major_code",materialBase.getMajorCode()).eq("del_flag", CommonConstant.DEL_FLAG_0));
						String majorName = csMajor==null?"":csMajor.getMajorName();
						stockIncomingMaterialsExportDTO.setMajorCode(materialBase.getMajorCode());
						stockIncomingMaterialsExportDTO.setMajorName(majorName);
						//子系统
						CsSubsystem csSubsystem = csSubsystemService.getOne(new QueryWrapper<CsSubsystem>().eq("system_code",materialBase.getSystemCode()).eq("del_flag", CommonConstant.DEL_FLAG_0));
						String subSystemName = csSubsystem==null?"":csSubsystem.getSystemName();
						stockIncomingMaterialsExportDTO.setSystemCode(materialBase.getSystemCode());
						stockIncomingMaterialsExportDTO.setSystemName(subSystemName);
						//物资分类
						StockIncomingMaterials stockIncomingMaterials = new StockIncomingMaterials();
						BeanUtils.copyProperties(materialBase,stockIncomingMaterials);
						String baseTypeCodeCcName = stockIncomingMaterialsService.getCcName(stockIncomingMaterials);
						stockIncomingMaterialsExportDTO.setBaseTypeCode(materialBase.getBaseTypeCode());
						stockIncomingMaterialsExportDTO.setBaseTypeCodeCc(materialBase.getBaseTypeCodeCc());
						stockIncomingMaterialsExportDTO.setBaseTypeCodeCcName(baseTypeCodeCcName);
						//物资名称
						stockIncomingMaterialsExportDTO.setMaterialName(materialBase.getName());
					}

				}
			}
			if (CollUtil.isEmpty(stockIncomingMaterialsExportDTOList)) {
				continue;
			}
			dto.setStockIncomingMaterialsDTOList(stockIncomingMaterialsExportDTOList);
		}
		return StockInOrderLevel2List;
	}

	@Override
	public void exportTemplateXls(HttpServletResponse response) throws IOException {
		//获取输入流，原始模板位置
		org.springframework.core.io.Resource resource = new ClassPathResource("/templates/stockInOrderLevel2.xlsx");
		InputStream resourceAsStream = resource.getInputStream();

		//2.获取临时文件
		File fileTemp= new File("/templates/stockInOrderLevel2.xlsx");
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

		QueryWrapper<StockLevel2Info> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("del_flag", CommonConstant.DEL_FLAG_0);
		queryWrapper.eq("status", CommonConstant.STOCK_LEVEL2_STATUS_1);
		queryWrapper.orderByDesc("create_time");
		List<StockLevel2Info> stockLevel2Infos = iStockLevel2InfoService.list(queryWrapper);
		selectList(workbook, "入库仓库", 0, 0, stockLevel2Infos);
		String fileName = "二级库入库导入模板.xlsx";

		try {
			response.setHeader("Content-Disposition",
					"attachment;filename=" + new String(fileName.getBytes("UTF-8"), "iso8859-1"));
			response.setHeader("Content-Disposition", "attachment;filename="+"二级库入库导入模板.xlsx");
			BufferedOutputStream bufferedOutPut = new BufferedOutputStream(response.getOutputStream());
			workbook.write(bufferedOutPut);
			bufferedOutPut.flush();
			bufferedOutPut.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) throws IOException {
		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
		Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
		List<String> errorMessage = new ArrayList<>();
		int successLines = 0;
		String tipMessage = null;
		String url = null;
		boolean errorMark = false;
		int errorLines = 0;
		for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
			// 获取上传文件对象
			MultipartFile file = entity.getValue();
			String type = FilenameUtils.getExtension(file.getOriginalFilename());
			if (!StrUtil.equalsAny(type, true, "xls", "xlsx")) {
				tipMessage = "导入失败，文件类型错误！";
				return imporReturnRes(errorLines, successLines, tipMessage, false, null);
			}
			ImportParams params = new ImportParams();
			params.setTitleRows(2);
			params.setHeadRows(2);
			params.setNeedSave(true);

			try {
				List<StockInOrderLevel2> stockInOrderLevel2List = new ArrayList<>();
				List<StockIncomingMaterials> stockIncomingMaterialsList = new ArrayList<>();
				List<StockInOrderLevel2DTO> stockInOrderLevel2DTOList = ExcelImportUtil.importExcel(file.getInputStream(), StockInOrderLevel2DTO.class, params);

				Iterator<StockInOrderLevel2DTO> iterator = stockInOrderLevel2DTOList.iterator();
				while (iterator.hasNext()) {
					StockInOrderLevel2DTO model = iterator.next();
					boolean b = XlsUtil.checkObjAllFieldsIsNull(model);
					if (b) {
						iterator.remove();
					}
				}
				if (CollectionUtil.isEmpty(stockInOrderLevel2DTOList)) {
					tipMessage = "导入失败，该文件为空。";
					return imporReturnRes(errorLines, successLines, tipMessage, false, null);
				}
				//数据校验
				for (StockInOrderLevel2DTO stockInOrderLevel2DTO : stockInOrderLevel2DTOList) {
					if (ObjectUtil.isNotEmpty(stockInOrderLevel2DTO)) {
						StockInOrderLevel2 stockInOrderLevel2 = new StockInOrderLevel2();
						List<StockIncomingMaterials> stockIncomingMaterials = new ArrayList<>();

						StringBuilder stringBuilder = new StringBuilder();

						//校验二级库信息
						examine(stockInOrderLevel2DTO, stockInOrderLevel2, stringBuilder);
						//校验物资信息
						errorMark = this.StockIncomingMaterials(stockInOrderLevel2DTO,stockIncomingMaterials,errorMark);
						if (stringBuilder.length() > 0 || errorMark) {
							// 截取字符
							if (stringBuilder.length() > 0){
								stringBuilder = stringBuilder.deleteCharAt(stringBuilder.length() - 1);
								stockInOrderLevel2DTO.setStockInOrderLevelMistake(stringBuilder.toString());
							}
							errorLines++;
						}else {
							stockInOrderLevel2List.add(stockInOrderLevel2);
							stockIncomingMaterialsList.addAll(stockIncomingMaterials);
						}
					}
				}
				if (errorLines > 0) {
					//错误报告下载
					return getErrorExcel(errorLines, stockInOrderLevel2DTOList, errorMessage, successLines, type, url);
				}else {
					successLines = stockInOrderLevel2DTOList.size();
					for (StockInOrderLevel2 stockInOrderLevel2 : stockInOrderLevel2List) {
						 StockInOrderLevel2 inOrderCode = this.getInOrderCode();
						 String orderCode = inOrderCode.getOrderCode();
						 stockInOrderLevel2.setOrderCode(orderCode);
						 stockInOrderLevel2.setDelFlag(0);
						 stockInOrderLevel2.setEntryTime(new Date());
						 stockInOrderLevel2.setStatus("1");
						 stockInOrderLevel2.setOrgCode(((LoginUser) SecurityUtils.getSubject().getPrincipal()).getOrgCode());
						this.save(stockInOrderLevel2);

						for (StockIncomingMaterials stockIncomingMaterials : stockIncomingMaterialsList) {
							 stockIncomingMaterials.setInOrderCode(stockInOrderLevel2.getOrderCode());
							 stockIncomingMaterials.setDelFlag(0);
							 stockIncomingMaterialsService.save(stockIncomingMaterials);
						}
					}
					return imporReturnRes(errorLines, successLines, tipMessage, true, null);
				}
			}catch (Exception e) {
				String msg = e.getMessage();
				log.error(msg, e);
				if (msg != null && msg.contains("Duplicate entry")) {
					return Result.error("文件导入失败:有重复数据！");
				} else {
					return Result.error("文件导入失败:" + e.getMessage());
				}
			}finally {
				try {
					file.getInputStream().close();
				} catch (IOException e) {
					log.error(e.getMessage(), e);
				}
			}
		}
		return imporReturnRes(errorLines, successLines, tipMessage, true, null);
	}

	private Result<?> getErrorExcel(int errorLines, List<StockInOrderLevel2DTO> list, List<String> errorMessage, int successLines, String type, String url) throws IOException {
		//创建导入失败错误报告,进行模板导出
		org.springframework.core.io.Resource resource = new ClassPathResource("/templates/stockInOrderLevelError.xlsx");
		InputStream resourceAsStream = resource.getInputStream();
		//2.获取临时文件
		File fileTemp = new File("/templates/stockInOrderLevelError.xlsx");
		try {
			//将读取到的类容存储到临时文件中，后面就可以用这个临时文件访问了
			FileUtils.copyInputStreamToFile(resourceAsStream, fileTemp);
		} catch (Exception e) {
			log.error(e.getMessage());
		}

		String path = fileTemp.getAbsolutePath();
		TemplateExportParams exportParams = new TemplateExportParams(path);
		Map<String, Object> errorMap = new HashMap<String, Object>(16);
		List<Map<String, String>> listMap = new ArrayList<>();
		for (int i = 0; i < list.size(); i++) {
			StockInOrderLevel2DTO stockInOrderLevel2DTO = list.get(i);
			List<StockIncomingMaterialsDTO> stockIncomingMaterialsDTOList = stockInOrderLevel2DTO.getStockIncomingMaterialsDTOList();
			for (int j = 0; j < stockIncomingMaterialsDTOList.size(); j++){
				StockIncomingMaterialsDTO stockIncomingMaterialsDTO = stockIncomingMaterialsDTOList.get(j);
				//物资错误报告获取信息
				Map<String, String> map = new HashMap<>(16);
				map.put("warehouseName", stockInOrderLevel2DTO.getWarehouseName());
				map.put("realName", stockInOrderLevel2DTO.getRealName());
				map.put("workNo", stockInOrderLevel2DTO.getWorkNo());
				map.put("note", stockInOrderLevel2DTO.getNote());
				map.put("stockInOrderLevelMistake", stockInOrderLevel2DTO.getStockInOrderLevelMistake());
				map.put("materialCode", stockIncomingMaterialsDTO.getMaterialCode());
				map.put("materialName", stockIncomingMaterialsDTO.getMaterialName());
				map.put("num", Convert.toStr(stockIncomingMaterialsDTO.getNum()));
				map.put("materialMistake",stockIncomingMaterialsDTO.getMaterialMistake());
				listMap.add(map);
			}
		}
		errorMap.put("maplist", listMap);
		Map<Integer, Map<String, Object>> sheetsMap = new HashMap<>(16);
		sheetsMap.put(0, errorMap);
		Workbook workbook = ExcelExportUtil.exportExcel(sheetsMap, exportParams);
		int size = 3;
		for (StockInOrderLevel2DTO stockInOrderLevel2DTO : list) {
			//合并单元格
			PoiMergeCellUtil.addMergedRegion(workbook.getSheetAt(0), size, size + stockInOrderLevel2DTO.getStockIncomingMaterialsDTOList().size() - 1, 0, 0);

			PoiMergeCellUtil.addMergedRegion(workbook.getSheetAt(0), size, size + stockInOrderLevel2DTO.getStockIncomingMaterialsDTOList().size() - 1, 1, 1);

			PoiMergeCellUtil.addMergedRegion(workbook.getSheetAt(0), size, size + stockInOrderLevel2DTO.getStockIncomingMaterialsDTOList().size() - 1, 2, 2);

			PoiMergeCellUtil.addMergedRegion(workbook.getSheetAt(0), size, size + stockInOrderLevel2DTO.getStockIncomingMaterialsDTOList().size() - 1, 3, 3);

			PoiMergeCellUtil.addMergedRegion(workbook.getSheetAt(0), size, size + stockInOrderLevel2DTO.getStockIncomingMaterialsDTOList().size() - 1, 7, 7);
		}

		try {
			String fileName = "二级库入库错误信息清单"+"_" + System.currentTimeMillis()+"."+type;
			FileOutputStream out = new FileOutputStream(errorExcelUpload+ File.separator+fileName);
			url = File.separator+"errorExcelFiles"+ File.separator+fileName;
			workbook.write(out);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		String tipMessage = "导入失败，文件类型不对。";
		return imporReturnRes(errorLines, successLines, tipMessage, true, url);
	}


	private void examine(StockInOrderLevel2DTO stockInOrderLevel2DTO,
						 StockInOrderLevel2 stockInOrderLevel2,
						 StringBuilder stringBuilder)
	{
            if (StrUtil.isBlank(stockInOrderLevel2DTO.getWarehouseName())){
				stringBuilder.append("入库仓库必填，");
			}else {
				QueryWrapper<StockLevel2Info> infoQueryWrapper = new QueryWrapper<>();
				infoQueryWrapper.eq("del_flag", CommonConstant.DEL_FLAG_0);
				infoQueryWrapper.eq("status", CommonConstant.STOCK_LEVEL2_STATUS_1);
				infoQueryWrapper.eq("warehouse_name",stockInOrderLevel2DTO.getWarehouseName());
				infoQueryWrapper.orderByDesc("create_time");
				StockLevel2Info one = iStockLevel2InfoService.getOne(infoQueryWrapper);
				if (ObjectUtil.isNotEmpty(one)){
					stockInOrderLevel2.setWarehouseCode(one.getWarehouseCode());
				}else {
					stringBuilder.append("系统中不存在该入库仓库，");
				}
			}
            if (StrUtil.isNotBlank(stockInOrderLevel2DTO.getRealName()) && StrUtil.isBlank(stockInOrderLevel2DTO.getWorkNo())){
				List<LoginUser> userByRealName = iSysBaseAPI.getUserByRealName(stockInOrderLevel2DTO.getRealName(), null);
				if (CollectionUtil.isNotEmpty(userByRealName) && userByRealName.size()==1){
					List<String> collect = userByRealName.stream().map(LoginUser::getId).collect(Collectors.toList());
					String join = CollectionUtil.join(collect, ",");
					stockInOrderLevel2.setUserId(join);
				}if (CollectionUtil.isNotEmpty(userByRealName) && userByRealName.size()>1){
					stringBuilder.append("入库人存在同名，请填写工号，");
				}
				if (CollectionUtil.isEmpty(userByRealName)){
					stringBuilder.append("系统中不存在该入库人，");
				}
			}
		if (StrUtil.isNotBlank(stockInOrderLevel2DTO.getRealName()) && StrUtil.isNotBlank(stockInOrderLevel2DTO.getWorkNo())){
			List<LoginUser> userByRealName = iSysBaseAPI.getUserByRealName(stockInOrderLevel2DTO.getRealName(), stockInOrderLevel2DTO.getWorkNo());
			if (CollectionUtil.isNotEmpty(userByRealName)){
				List<String> collect = userByRealName.stream().map(LoginUser::getId).collect(Collectors.toList());
				String join = CollectionUtil.join(collect, ",");
				stockInOrderLevel2.setUserId(join);
			}
			if (CollectionUtil.isEmpty(userByRealName)) {
				stringBuilder.append("系统中不存在该入库人，");
			}
		}

		 if (StrUtil.isBlank(stockInOrderLevel2DTO.getRealName()) && StrUtil.isBlank(stockInOrderLevel2DTO.getWorkNo())){
			LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
			stockInOrderLevel2.setUserId(sysUser.getId());
		   }

		 if (StrUtil.isBlank(stockInOrderLevel2DTO.getRealName()) && StrUtil.isNotBlank(stockInOrderLevel2DTO.getWorkNo())){
			LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
			stockInOrderLevel2.setUserId(sysUser.getId());
		 }

		 if (StrUtil.isNotBlank(stockInOrderLevel2DTO.getNote())){
		 	stockInOrderLevel2.setNote(stockInOrderLevel2DTO.getNote());
		 }
	}


	private boolean StockIncomingMaterials(StockInOrderLevel2DTO stockInOrderLevel2DTO,
										List<StockIncomingMaterials> stockIncomingMaterials,
										Boolean errorMark)
	{
		List<StockIncomingMaterialsDTO> stockIncomingMaterialsDTOList = stockInOrderLevel2DTO.getStockIncomingMaterialsDTOList();
		errorMark = false;
		if (CollectionUtil.isNotEmpty(stockIncomingMaterialsDTOList)){
			for (StockIncomingMaterialsDTO stockIncomingMaterialsDTO : stockIncomingMaterialsDTOList) {
				StringBuilder stringBuilder1 = new StringBuilder();
				StockIncomingMaterials stockIncomingMaterials1 = new StockIncomingMaterials();
				if (StrUtil.isBlank(stockIncomingMaterialsDTO.getMaterialCode())){
					stringBuilder1.append("物资编码必填，");
				}
				if (StrUtil.isBlank(stockIncomingMaterialsDTO.getMaterialName())){
					stringBuilder1.append("物资名称必填，");
				}
				if (StrUtil.isNotBlank(stockIncomingMaterialsDTO.getMaterialCode())){
					LambdaQueryWrapper<MaterialBase> lambdaQueryWrapper = new LambdaQueryWrapper<>();
					lambdaQueryWrapper.eq(MaterialBase::getCode,stockIncomingMaterialsDTO.getMaterialCode());
					lambdaQueryWrapper.eq(MaterialBase::getDelFlag,CommonConstant.DEL_FLAG_0);
					MaterialBase one = iMaterialBaseService.getOne(lambdaQueryWrapper);
					if (ObjectUtil.isNotEmpty(one)){
						stockIncomingMaterials1.setMaterialCode(one.getCode());
					}else {
						stringBuilder1.append("系统中不存在该物资编码，");
					}
				}
				if (StrUtil.isNotBlank(stockIncomingMaterialsDTO.getMaterialName())){
					LambdaQueryWrapper<MaterialBase> lambdaQueryWrapper = new LambdaQueryWrapper<>();
					lambdaQueryWrapper.eq(MaterialBase::getName,stockIncomingMaterialsDTO.getMaterialName());
					lambdaQueryWrapper.eq(MaterialBase::getDelFlag,CommonConstant.DEL_FLAG_0);
					MaterialBase one = iMaterialBaseService.getOne(lambdaQueryWrapper);
					if(ObjectUtil.isNull(one)) {
						stringBuilder1.append("系统中不存在该物资名称，");
					}
				}
				if (StrUtil.isNotBlank(stockIncomingMaterialsDTO.getMaterialCode()) && StrUtil.isNotBlank(stockIncomingMaterialsDTO.getMaterialName())){
					LambdaQueryWrapper<MaterialBase> lambdaQueryWrapper = new LambdaQueryWrapper<>();
					lambdaQueryWrapper.eq(MaterialBase::getCode,stockIncomingMaterialsDTO.getMaterialCode());
					lambdaQueryWrapper.eq(MaterialBase::getName,stockIncomingMaterialsDTO.getMaterialName());
					lambdaQueryWrapper.eq(MaterialBase::getDelFlag,CommonConstant.DEL_FLAG_0);
					MaterialBase one = iMaterialBaseService.getOne(lambdaQueryWrapper);
					if (ObjectUtil.isNotEmpty(one)){
						stockIncomingMaterials1.setMaterialCode(one.getCode());
					}else {
						stringBuilder1.append("物资编码和物资名称不匹配，");
					}
				}
				if (stockIncomingMaterialsDTO.getNum()==null){
					stringBuilder1.append("入库数量必填，");
				}else {
					stockIncomingMaterials1.setNumber(stockIncomingMaterialsDTO.getNum());
				}
				if (stringBuilder1.length() > 0) {
					stringBuilder1 = stringBuilder1.deleteCharAt(stringBuilder1.length() - 1);
					stockIncomingMaterialsDTO.setMaterialMistake(stringBuilder1.toString());
					errorMark = true;
				}
				stockIncomingMaterials.add(stockIncomingMaterials1);
			}
		}
		return errorMark;
	}

	//下拉框
	private void selectList(Workbook workbook,String name,int firstCol, int lastCol,List<StockLevel2Info> modelList){
		Sheet sheet = workbook.getSheetAt(0);
		if (CollectionUtil.isNotEmpty(modelList)) {
			//将新建的sheet页隐藏掉, 下拉值太多，需要创建隐藏页面
			int sheetTotal = workbook.getNumberOfSheets();
			String hiddenSheetName = name + "_hiddenSheet";
			List<String> collect = modelList.stream().map(StockLevel2Info::getWarehouseName).collect(Collectors.toList());
			Sheet hiddenSheet = workbook.getSheet(hiddenSheetName);
			if (hiddenSheet == null) {
				hiddenSheet = workbook.createSheet(hiddenSheetName);
				//写入下拉数据到新的sheet页中
				for (int i = 0; i < collect.size(); i++) {
					Row hiddenRow = hiddenSheet.createRow(i);
					Cell hiddenCell = hiddenRow.createCell(0);
					hiddenCell.setCellValue(collect.get(i));
				}
				workbook.setSheetHidden(sheetTotal, true);
			}

			// 下拉数据
			CellRangeAddressList cellRangeAddressList = new CellRangeAddressList(3, 65535, firstCol, lastCol);
			//  生成下拉框内容名称
			String strFormula = hiddenSheetName + "!$A$1:$A$65535";
			// 根据隐藏页面创建下拉列表
			XSSFDataValidationConstraint constraint = new XSSFDataValidationConstraint(DataValidationConstraint.ValidationType.LIST, strFormula);
			XSSFDataValidationHelper dvHelper = new XSSFDataValidationHelper((XSSFSheet) hiddenSheet);
			DataValidation validation = dvHelper.createValidation(constraint, cellRangeAddressList);
			//  对sheet页生效
			sheet.addValidationData(validation);
		}

	}
	public static Result<?> imporReturnRes(int errorLines, int successLines, String tipMessage, boolean isType, String failReportUrl) throws IOException {
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
			res.setMessage(tipMessage);
			res.setCode(200);
			return res;
		}

	}
	@Override
	public IPage<StockInOrderLevel2> pageList(Page<StockInOrderLevel2> page, StockInOrderLevel2 stockInOrderLevel2) {
		List<StockInOrderLevel2> baseList = baseMapper.pageList(page, stockInOrderLevel2);
		page.setRecords(baseList);
		return page;
	}
}
