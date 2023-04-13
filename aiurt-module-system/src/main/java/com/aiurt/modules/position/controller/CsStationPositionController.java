package com.aiurt.modules.position.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.modules.device.entity.Device;
import com.aiurt.modules.device.service.IDeviceService;
import com.aiurt.modules.position.entity.CsLine;
import com.aiurt.modules.position.entity.CsStation;
import com.aiurt.modules.position.entity.CsStationPosition;
import com.aiurt.modules.position.service.ICsLineService;
import com.aiurt.modules.position.service.ICsStationPositionService;
import com.aiurt.modules.position.service.ICsStationService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

 /**
 * @Description: cs_station_position
 * @Author: jeecg-boot
 * @Date:   2022-06-21
 * @Version: V1.0
 */
@Api(tags="系统管理-基础数据-位置管理-三级")
@RestController
@RequestMapping("/position")
@Slf4j
public class CsStationPositionController  {
	public static final String LEVEL_1 = "1";
	 public static final String LEVEL_2 = "2";
	 public static final String LEVEL_3 = "3";
	@Autowired
	private ICsStationPositionService csStationPositionService;
	@Autowired
	private ICsStationService csStationService;
	@Autowired
	private ICsLineService csLineService;
	@Autowired
	private ISysBaseAPI sysBaseApi;
	@Autowired
	private IDeviceService deviceService;
	 /**
	  * 位置管理树
	  *
	  * @return
	  */
	 @AutoLog(value = "查询",operateType = 1,operateTypeAlias = "查询位置管理树",permissionUrl = "/position/list")
	 @ApiOperation(value="位置管理树", notes="位置管理树")
	 @GetMapping(value = "/treeList")
	 public Result<?> queryTreeList(@RequestParam(name="name",required = false) String name) {
	 	 //查询所有一级
		 List<CsLine> lineList = csLineService.list(new LambdaQueryWrapper<CsLine>().eq(CsLine::getDelFlag, CommonConstant.DEL_FLAG_0).orderByAsc(CsLine::getSort).orderByDesc(CsLine::getUpdateTime));
		 //查询所有二级
		 List<CsStation> stationList = csStationService.list(new LambdaQueryWrapper<CsStation>().eq(CsStation::getDelFlag, CommonConstant.DEL_FLAG_0).orderByAsc(CsStation::getSort).orderByDesc(CsStation::getUpdateTime));
		 //查询所有三级
		 List<CsStationPosition> positionList = csStationPositionService.list(new LambdaQueryWrapper<CsStationPosition>().eq(CsStationPosition::getDelFlag, CommonConstant.DEL_FLAG_0).orderByAsc(CsStationPosition::getSort).orderByDesc(CsStationPosition::getUpdateTime));
		 List<CsStationPosition> newList = new ArrayList<>();
		 //循环一级
		 lineList.forEach(line -> {
		 	String codeCc1 = line.getLineCode();
			 CsStationPosition onePosition = setEntity(line.getId(),1,line.getSort(),line.getLineCode(),line.getLineName(),null,null,codeCc1,line.getLineType(),"",line.getLongitude(),line.getLatitude());
			 List<CsStation> twoStationList = stationList.stream().filter(station-> station.getLineCode().equals(line.getLineCode())).collect(Collectors.toList());
			 List<CsStationPosition> twoList = new ArrayList<>();
			 //循环二级
			 twoStationList.forEach(two->{
				 String codeCc2 = line.getLineCode()+"/"+two.getStationCode();
				 CsStationPosition twoPosition = setEntity(two.getId(),2,two.getSort(),two.getStationCode(),two.getStationName(),line.getLineCode(),line.getLineName(),codeCc2,two.getStationType(),"",two.getLongitude(),two.getLatitude());
				 List<CsStationPosition> threeStationList = positionList.stream().filter(position-> position.getStaionCode().equals(two.getStationCode())).collect(Collectors.toList());
				 List<CsStationPosition> threeList = new ArrayList<>();
				 //循环三级
				 threeStationList.forEach(three->{
					 String codeCc3 = line.getLineCode()+"/"+two.getStationCode()+"/"+three.getPositionCode();
					 CsStationPosition threePosition = setEntity(three.getId(),3,three.getSort(),three.getPositionCode(),three.getPositionName(),two.getStationCode(),two.getStationName(),codeCc3,three.getPositionType(),three.getLength(),three.getLongitude(),three.getLatitude());
					 threeList.add(threePosition);
				 });
				 twoPosition.setChildren(threeList);
				 twoList.add(twoPosition);
			 });
			 //二级放入一级的子节点里
			 onePosition.setChildren(twoList);
			 newList.add(onePosition);
		 });
		 //做树形搜索处理
		 if (StrUtil.isNotBlank(name) && CollUtil.isNotEmpty(newList)){
			 this.assetTreeList(newList,name);
		 }
		 return Result.OK(newList);
	 }

	 private void assetTreeList(List<CsStationPosition> deviceTypeTree, String name){
		 Iterator<CsStationPosition> iterator = deviceTypeTree.iterator();
		 while (iterator.hasNext()) {
			 CsStationPosition next = iterator.next();
			 if (StrUtil.containsAnyIgnoreCase(next.getPositionName(), name)) {
				 //名称匹配则赋值颜色
				 next.setColor("#FF5B05");
			 }
			 List<CsStationPosition> children = next.getChildren();
			 if (CollUtil.isNotEmpty(children)) {
				 assetTreeList(children,name);
			 }
			 //如果没有子级，并且当前不匹配，则去除
			 if (CollUtil.isEmpty(next.getChildren()) && StrUtil.isEmpty(next.getColor())) {
				 iterator.remove();
			 }
		 }
	 }

	 /**
	  * 位置管理树-转换实体
	  * @param id
	  * @param level
	  * @param sort
	  * @param positionCode
	  * @param positionName
	  * @return
	  */
	 public CsStationPosition setEntity(String id, Integer level, Integer sort, String positionCode, String positionName, String pCode, String pName, String codeCc, Integer positionType, String length, BigDecimal longitude,BigDecimal latitude){
		 CsStationPosition position = new CsStationPosition();
		 position.setId(id);
		 position.setLevel(level);
		 position.setSort(sort);
		 position.setPositionCode(positionCode);
		 position.setPositionName(positionName);
		 position.setLongitude(longitude);
		 position.setLatitude(latitude);
		 position.setPCode(pCode);
		 position.setPUrl(pName);
		 position.setCodeCc(codeCc);
		 position.setPositionType(positionType);
		 position.setLength(length);
		 position.setTitle(positionName);
		 position.setValue(positionCode);
         return position;
	 }
	/**
	 * 分页列表查询
	 *
	 * @param csStationPosition
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "查询",operateType = 1,operateTypeAlias = "位置管理分页列表查询",permissionUrl = "/position/list")
	@ApiOperation(value="位置管理分页列表查询", notes="位置管理分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(CsStationPosition csStationPosition,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		Page<CsStationPosition> page = new Page<CsStationPosition>(pageNo, pageSize);

		List<CsStationPosition> list = csStationPositionService.readAll(page,csStationPosition);
		list.forEach(position -> {
			position.setPositionTypes(position.getPositionType()+"");
		});
		page.setRecords(list);
		return Result.OK(page);
	}
	/**
	 * 添加
	 *
	 * @param csStationPosition
	 * @return
	 */
	@AutoLog(value = "添加",operateType = 2,operateTypeAlias = "添加三级位置",permissionUrl = "/position/list")
	@ApiOperation(value="位置管理添加", notes="位置管理添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody CsStationPosition csStationPosition) {
		return csStationPositionService.add(csStationPosition);
	}

	/**
	 * 编辑
	 *
	 * @param csStationPosition
	 * @return
	 */
	@AutoLog(value = "编辑",operateType = 3,operateTypeAlias = "编辑三级位置",permissionUrl = "/position/list")
	@ApiOperation(value="位置管理编辑", notes="位置管理编辑")
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody CsStationPosition csStationPosition) {
		return csStationPositionService.update(csStationPosition);
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "删除",operateType = 4,operateTypeAlias = "删除三级位置",permissionUrl = "/position/list")
	@ApiOperation(value="位置管理通过id删除", notes="位置管理通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		CsStationPosition csStationPosition = csStationPositionService.getById(id);
		//判断设备主数据是否使用
		LambdaQueryWrapper<Device> deviceWrapper =  new LambdaQueryWrapper<Device>();
		deviceWrapper.eq(Device::getPositionCode,csStationPosition.getPositionCode());
		deviceWrapper.eq(Device::getDelFlag, CommonConstant.DEL_FLAG_0);
		List<Device> deviceList = deviceService.list(deviceWrapper);
		if(!deviceList.isEmpty()){
			return Result.error("该位置信息被设备主数据使用中，无法删除");
		}
		csStationPosition.setDelFlag(CommonConstant.DEL_FLAG_1);
		csStationPositionService.updateById(csStationPosition);
		return Result.OK("删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "查询",operateType = 1,operateTypeAlias = "通过id查询三级位置",permissionUrl = "/position/list")
	@ApiOperation(value="位置管理通过id查询", notes="位置管理通过id查询")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		CsStationPosition csStationPosition = csStationPositionService.getById(id);
		if(csStationPosition==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(csStationPosition);
	}
	 /**
	  * 查询最大排序数
	  *
	  * @return
	  */
	 @AutoLog(value = "查询",operateType = 1,operateTypeAlias = "查询最大排序数",permissionUrl = "/position/list")
	 @ApiOperation(value="查询最大排序数", notes="查询最大排序数")
	 @GetMapping(value = "/getSort")
	 public Result<?> getSort(String level,String code) {
	 	Integer sort = 1;
	 	 if(null!=level && (LEVEL_1).equals(level)){
			 LambdaQueryWrapper<CsLine> wrapper = new LambdaQueryWrapper<>();
			 wrapper.orderByDesc(CsLine::getSort);
			 List<CsLine> list = csLineService.list(wrapper.eq(CsLine::getDelFlag, CommonConstant.DEL_FLAG_0));
			 if(CollUtil.isNotEmpty(list)){
				 CsLine csLine = list.get(0);
				 if (Objects.nonNull(csLine)) {
					 sort = Objects.isNull(csLine.getSort())?list.size()+1:csLine.getSort()+1;
				 }else{
				 	sort = list.size()+1;
				 }

			 }
		 }else if(null!=level && (LEVEL_2).equals(level)){
			 LambdaQueryWrapper<CsStation> wrapper = new LambdaQueryWrapper<>();
			 wrapper.orderByDesc(CsStation::getSort);
			 wrapper.eq(CsStation::getDelFlag, CommonConstant.DEL_FLAG_0);
			 wrapper.eq(CsStation::getLineCode,code);
			 List<CsStation> list = csStationService.list(wrapper);
			 if(CollUtil.isNotEmpty(list)){
				 CsStation csLine = list.get(0);
				 if (Objects.nonNull(csLine)) {
					 sort = Objects.isNull(csLine.getSort())?list.size()+1:csLine.getSort()+1;
				 }else{
					 sort = list.size()+1;
				 }
			 }
		 }else if(null!=level && (LEVEL_3).equals(level)){
			 LambdaQueryWrapper<CsStationPosition> wrapper = new LambdaQueryWrapper<>();
			 wrapper.orderByDesc(CsStationPosition::getSort);
			 wrapper.eq(CsStationPosition::getStaionCode,code);
			 wrapper.eq(CsStationPosition::getDelFlag, CommonConstant.DEL_FLAG_0);
			 List<CsStationPosition> list = csStationPositionService.list(wrapper);
			 if(CollUtil.isNotEmpty(list)){
				 CsStationPosition csLine = list.get(0);
				 if (Objects.nonNull(csLine)) {
					 sort = Objects.isNull(csLine.getSort())?list.size()+1:csLine.getSort()+1;
				 }else{
					 sort = list.size()+1;
				 }
			 }
		 }
		 return Result.OK(sort);
	 }

	 /**
	  * 下载导入模板
	  * @param response
	  * @param request
	  * @throws IOException
	  */
	 @ApiOperation(value = "下载位置导入模板", notes = "下载位置导入模板")
	 @RequestMapping(value = "/downloadExcel", method = RequestMethod.GET)
	 public void downloadExcel(HttpServletResponse response, HttpServletRequest request) throws IOException {
		 ClassPathResource classPathResource = new ClassPathResource("templates/csStationPosition.xlsx");
		 InputStream bis = classPathResource.getInputStream();
		 BufferedOutputStream out = new BufferedOutputStream(response.getOutputStream());
		 int len = 0;
		 while ((len = bis.read()) != -1) {
			 out.write(len);
			 out.flush();
		 }
		 out.close();
	 }
	 /**
	  * 通过excel导入数据
	  *
	  * @param request
	  * @param response
	  * @return
	  */
	 @ApiOperation(value = "通过excel导入数据", notes = "通过excel导入数据")
	 @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
	 public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
		 MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
		 Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
		 for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
			 // 获取上传文件对象
			 MultipartFile file = entity.getValue();
			 ImportParams params = new ImportParams();
			 params.setTitleRows(2);
			 params.setHeadRows(1);
			 params.setNeedSave(true);
			 try {
				 return csStationPositionService.importExcelMaterial(file, params);
			 } catch (Exception e) {
				 log.error(e.getMessage(), e);
				 return Result.error("文件导入失败:" + e.getMessage());
			 } finally {
				 try {
					 file.getInputStream().close();
				 } catch (IOException e) {
					 log.error(e.getMessage(), e);
				 }
			 }
		 }
		 return Result.error("文件导入失败！");
	 }

 }
