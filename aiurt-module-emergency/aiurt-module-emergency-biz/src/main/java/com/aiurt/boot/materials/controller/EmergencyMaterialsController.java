package com.aiurt.boot.materials.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.boot.materials.dto.*;
import com.aiurt.boot.materials.entity.EmergencyMaterials;
import com.aiurt.boot.materials.entity.EmergencyMaterialsCategory;
import com.aiurt.boot.materials.entity.EmergencyMaterialsInvoices;
import com.aiurt.boot.materials.entity.EmergencyMaterialsInvoicesItem;
import com.aiurt.boot.materials.service.IEmergencyMaterialsCategoryService;
import com.aiurt.boot.materials.service.IEmergencyMaterialsInvoicesItemService;
import com.aiurt.boot.materials.service.IEmergencyMaterialsInvoicesService;
import com.aiurt.boot.materials.service.IEmergencyMaterialsService;
import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.system.base.controller.BaseController;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.system.vo.SysDepartModel;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description: emergency_materials
 * @Author: aiurt
 * @Date:   2022-11-29
 * @Version: V1.0
 */
@Api(tags="物资信息")
@RestController
@RequestMapping("/emergency/emergencyMaterials")
@Slf4j
public class EmergencyMaterialsController extends BaseController<EmergencyMaterials, IEmergencyMaterialsService> {
	@Autowired
	private IEmergencyMaterialsService emergencyMaterialsService;

	 @Autowired
	 private IEmergencyMaterialsCategoryService emergencyMaterialsCategoryService;

	 @Autowired
	 private IEmergencyMaterialsInvoicesItemService iEmergencyMaterialsInvoicesItemService;

	 @Autowired
	 private IEmergencyMaterialsInvoicesService iEmergencyMaterialsInvoicesService;

	 @Autowired
	 private ISysBaseAPI iSysBaseAPI;

	/**
	 * 分页列表查询
	 *
	 * @param emergencyMaterials
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "emergency_materials-分页列表查询")
	@ApiOperation(value="物资信息-分页列表查询", notes="物资信息-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<EmergencyMaterials>> queryPageList(EmergencyMaterials emergencyMaterials,
														   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
														   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
														   HttpServletRequest req) {
		QueryWrapper<EmergencyMaterials> queryWrapper = QueryGenerator.initQueryWrapper(emergencyMaterials, req.getParameterMap());
		Page<EmergencyMaterials> page = new Page<EmergencyMaterials>(pageNo, pageSize);
		IPage<EmergencyMaterials> pageList = emergencyMaterialsService.page(page, queryWrapper);
		return Result.OK(pageList);
	}


	 /**
	  * 应急物资台账列表查询
	  * @param condition
	  * @param pageNo
	  * @param pageSize
	  * @return
	  */
	 @AutoLog(value = "物资信息-应急物资台账列表查询或检查巡检应急物资查询", operateType =  1, operateTypeAlias = "应急物资台账列表查询或检查巡检应急物资查询")
	 @ApiOperation(value = "物资信息-应急物资台账列表查询或检查巡检应急物资查询", notes = "物资信息-应急物资台账列表查询或检查巡检应急物资查询")
	 @GetMapping(value = "/getMaterialAccountList")
	 @ApiResponses({
			 @ApiResponse(code = 200, message = "OK", response = MaterialAccountDTO.class)
	 })
	 public Result<Page<MaterialAccountDTO>> repairTaskPageList(MaterialAccountDTO condition,
																@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
																@RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize
	 ) {
		 Page<MaterialAccountDTO> pageList = new Page<>(pageNo, pageSize);
		 Page<MaterialAccountDTO> repairTaskPage = emergencyMaterialsService.getMaterialAccountList(pageList, condition);
		 return Result.OK(repairTaskPage);
	 }


	 @AutoLog(value = "物资信息-应急物资巡检-巡检标准下拉列表")
	 @ApiOperation(value="物资信息-应急物资巡检-巡检标准下拉列表", notes="物资信息-应急物资巡检-巡检标准下拉列表")
	 @GetMapping(value = "/getMaterialPatrol")
	 public Result<?> getMaterialPatrol(){
		 MaterialPatrolDTO materialPatrol = emergencyMaterialsService.getMaterialPatrol();
		 return Result.OK(materialPatrol);
	 }


	@AutoLog(value = "物资信息-应急物资台账-巡检标准下拉列表")
	@ApiOperation(value="物资信息-应急物资台账-巡检标准下拉列表", notes="物资信息-应急物资台账-巡检标准下拉列表")
	@GetMapping(value = "/getStandingBook")
	public Result<?> getStandingBook(@RequestParam(name = "materialsCode",required=true) String materialsCode,
									 @RequestParam(name = "categoryCode",required=true) String categoryCode,
			                         @RequestParam(name = "lineCode",required=false) String  lineCode,
									 @RequestParam(name = "stationCode",required=false) String  stationCode,
									 @RequestParam(name = "positionCode",required=false) String  positionCode){
		MaterialPatrolDTO materialPatrol = emergencyMaterialsService.getStandingBook(materialsCode,categoryCode,lineCode,stationCode,positionCode);
		return Result.OK(materialPatrol);
	}

	 @AutoLog(value = "物资信息-应急物资台账检查-巡检人下拉列表")
	 @ApiOperation(value="物资信息-应急物资台账检查-巡检人下拉列表", notes="物资信息-应急物资台账检查-巡检人下拉列表")
	 @GetMapping(value = "/getPatrolPeople")
	 public Result<?> getPatrolPeople(){
		 PatrolPeopleDTO patrolPeopleDTO = new PatrolPeopleDTO();
		 LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		 //当前登录人的id
		 patrolPeopleDTO.setPatrolId(sysUser.getId());
		 //当前登录人的名称
		 patrolPeopleDTO.setPatrolName(sysUser.getRealname());

		 //部门列表
		 List<LoginUser> userPersonnel = iSysBaseAPI.getUserPersonnel(sysUser.getOrgId());
		 if (CollUtil.isNotEmpty(userPersonnel)){
			 patrolPeopleDTO.setLoginUserList(userPersonnel);
		 }
		 return Result.OK(patrolPeopleDTO);
	 }


	 @AutoLog(value = "物资信息-巡检记录信息查询")
	 @ApiOperation(value="物资信息-巡检记录信息查询", notes="物资信息-巡检记录信息查询")
	 @GetMapping(value = "/getPatrolRecord")
	 public Result<?> getPatrolRecord(@RequestParam(name = "materialsCode",required=true) String materialsCode,
									  @RequestParam(name = "standardCode",required=false) String  standardCode,
									  @RequestParam(name = "startTime",required=false) String  startTime,
	                                  @RequestParam(name = "endTime",required=false) String  endTime,
									  @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
									  @RequestParam(name = "lineCode",required=false) String  lineCode,
									  @RequestParam(name = "stationCode",required=false) String  stationCode,
									  @RequestParam(name = "positionCode",required=false) String  positionCode,
									  @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize){
		 Page<EmergencyMaterialsInvoicesItem> pageList = new Page<>(pageNo, pageSize);
		 Page<EmergencyMaterialsInvoicesItem> patrolRecord = iEmergencyMaterialsInvoicesItemService.getPatrolRecord(pageList, materialsCode, startTime, endTime,standardCode,lineCode,stationCode,positionCode);
		 return  Result.OK(patrolRecord);
	 }

	 @AutoLog(value = "物资信息-应急物资检查记录列表查询")
	 @ApiOperation(value="物资信息-应急物资检查记录列表查询", notes="物资信息-应急物资检查记录列表查询")
	 @GetMapping(value = "/getInspectionRecord")
	 public Result<?> getInspectionRecord(EmergencyMaterialsInvoicesItem condition,
										  @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
										  @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize){
		 Page<EmergencyMaterialsInvoicesItem> pageList = new Page<>(pageNo, pageSize);
		 Page<EmergencyMaterialsInvoicesItem> inspectionRecord = emergencyMaterialsService.getInspectionRecord(pageList, condition);
		 return  Result.OK(inspectionRecord);
	 }
	 @AutoLog(value = "物资信息-应急物资检查记录列表-excel导出")
	 @ApiOperation(value="物资信息-应急物资检查记录列表-excel导出", notes="物资信息-应急物资检查记录列表-excel导出")
	 @GetMapping(value = "/getInspectionRecordExportExcel")
	 public void getInspectionRecordExportExcel(EmergencyMaterialsInvoicesDTO condition, HttpServletRequest request, HttpServletResponse response) throws IOException {
		  emergencyMaterialsService.getInspectionRecordExportExcel(condition,request,response);

	 }
	 @AutoLog(value = "物资信息-应急物资检查记录列表-压缩导出")
	 @ApiOperation(value="物资信息-应急物资检查记录列表-压缩导出", notes="物资信息-应急物资检查记录列表-压缩导出")
	 @GetMapping(value = "/getInspectionRecordExportZip")
	 public void getInspectionRecordExportZip(EmergencyMaterialsInvoicesDTO condition, HttpServletRequest request, HttpServletResponse response) throws IOException {
		 emergencyMaterialsService.getInspectionRecordExportZip(condition,request,response);

	 }
	 @AutoLog(value = "物资信息-应急物资检查记录查看")
	 @ApiOperation(value="物资信息-应急物资检查记录查看", notes="物资信息-应急物资检查记录查看")
	 @GetMapping(value = "/getMaterialInspection")
	 public Result<?> getMaterialInspection(@RequestParam(name = "invoicesId",required=true) String invoicesId,
											@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
											@RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize){
		 Page<EmergencyMaterialsInvoicesItem> pageList = new Page<>(pageNo, pageSize);
		 Page<EmergencyMaterialsInvoicesItem> materialInspection = emergencyMaterialsService.getMaterialInspection(pageList,invoicesId);
		 return  Result.OK(materialInspection);
	 }

	@AutoLog(value = "物资信息-应急物资检查记录查看")
	@ApiOperation(value="物资信息-应急物资检查记录查看", notes="物资信息-应急物资检查记录查看")
	@GetMapping(value = "/getMaterialInspectionById")
	public Result<?> getMaterialInspectionById(EmergencyMaterialsInvoicesReqDTO emergencyMaterialsInvoicesReqDTO){
		DynamicTableEntity materialInspectionById = emergencyMaterialsService.getMaterialInspectionById(emergencyMaterialsInvoicesReqDTO);
		return  Result.OK(materialInspectionById);
	}
	/**
	 *   添加
	 *
	 * @param emergencyMaterials
	 * @return
	 */
	@AutoLog(value = "物资信息-添加")
	@ApiOperation(value="物资信息-添加", notes="物资信息-添加")
	@PostMapping(value = "/add")
	@Transactional(rollbackFor = Exception.class)
	public Result<String> add(@RequestBody EmergencyMaterials emergencyMaterials) {
		emergencyMaterialsService.save(emergencyMaterials);
		return Result.OK("添加成功！");
	}


	 @AutoLog(value = "物资信息-应急物资巡检登记-提交")
	 @ApiOperation(value="物资信息-应急物资巡检登记-提交", notes="物资信息-应急物资巡检登记-提交")
	 @PostMapping(value = "/emergencyMaterialsSubmit")
	 @Transactional(rollbackFor = Exception.class)
	 public Result<?> emergencyMaterialsSubmit(@RequestBody EmergencyMaterialsDTO emergencyMaterialsDTO){
		 EmergencyMaterialsInvoices emergencyMaterialsInvoices = new EmergencyMaterialsInvoices();
		 //应急物资巡检单号
		 emergencyMaterialsInvoices.setMaterialsPatrolCode(emergencyMaterialsDTO.getMaterialsPatrolCode());
		 //巡检标准
		 if (StrUtil.isNotBlank(emergencyMaterialsDTO.getStandardCode())){
			 emergencyMaterialsInvoices.setStandardCode(emergencyMaterialsDTO.getStandardCode());
		 }
		 //巡检名称
		 if (StrUtil.isNotBlank(emergencyMaterialsDTO.getStandardName())){
			 emergencyMaterialsInvoices.setStandardName(emergencyMaterialsDTO.getStandardName());
		 }
		 //巡检日期
		 emergencyMaterialsInvoices.setPatrolDate(emergencyMaterialsDTO.getPatrolDate());
          //巡检线路
		 if(StrUtil.isNotBlank(emergencyMaterialsDTO.getLineCode())){
			 emergencyMaterialsInvoices.setLineCode(emergencyMaterialsDTO.getLineCode());
		 } //巡检站点
		 if(StrUtil.isNotBlank(emergencyMaterialsDTO.getStationCode())){
			 emergencyMaterialsInvoices.setStationCode(emergencyMaterialsDTO.getStationCode());
		 } //巡检位置
		 if(StrUtil.isNotBlank(emergencyMaterialsDTO.getPositionCode())){
			 emergencyMaterialsInvoices.setPositionCode(emergencyMaterialsDTO.getPositionCode());
		 }//巡检结果
		 if(emergencyMaterialsDTO.getInspectionResults()!=null){
			 emergencyMaterialsInvoices.setInspectionResults(emergencyMaterialsDTO.getInspectionResults());
		 }

         //巡检人
		 emergencyMaterialsInvoices.setUserId(emergencyMaterialsDTO.getUserId());

		 if (StrUtil.isNotBlank(emergencyMaterialsDTO.getUserId())){
		 	//巡视班组Code
			 LoginUser userById = iSysBaseAPI.getUserById(emergencyMaterialsDTO.getUserId());
			 emergencyMaterialsInvoices.setDepartmentCode(userById.getOrgCode());
		 }
		 //插入物资巡检单
		 iEmergencyMaterialsInvoicesService.save(emergencyMaterialsInvoices);

		 //应急物资巡检单ID
		 List<EmergencyMaterialsInvoicesItem> emergencyMaterialsInvoicesItemList = emergencyMaterialsDTO.getEmergencyMaterialsInvoicesItemList();

		 if (CollUtil.isNotEmpty(emergencyMaterialsInvoicesItemList)){
		 emergencyMaterialsInvoicesItemList.forEach(e->{
		 	e.setInvoicesId(emergencyMaterialsInvoices.getId());
		 });

			 List<EmergencyMaterialsInvoicesItem> collect = emergencyMaterialsInvoicesItemList.stream().filter(e -> "0".equals(e.getPid())).collect(Collectors.toList());
			 if (CollUtil.isNotEmpty(collect)){
			 	    collect.forEach(e->{
						List<EmergencyMaterialsInvoicesItem> collect1 = emergencyMaterialsInvoicesItemList.stream().filter(q -> q.getPid().equals(e.getId()) && q.getCategoryCode().equals(e.getCategoryCode()) && q.getMaterialsCode().equals(e.getMaterialsCode())).collect(Collectors.toList());
                        e.setId(null);
						iEmergencyMaterialsInvoicesItemService.save(e);
						String id = e.getId();
						if (CollUtil.isNotEmpty(collect1)){
							collect1.forEach(q->{
								q.setPid(id);
								q.setId(null);
								iEmergencyMaterialsInvoicesItemService.save(q);
							});
						}
					});
			   }

		 }

		 return Result.OK("提交成功！");
	 }

	 /**
      *  编辑
      *
      * @param emergencyMaterials
      * @return
      */
	@AutoLog(value = "物资信息-编辑")
	@ApiOperation(value="物资信息-编辑", notes="物资信息-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	@Transactional(rollbackFor = Exception.class)
	public Result<String> edit(@RequestBody EmergencyMaterials emergencyMaterials) {
		emergencyMaterialsService.updateById(emergencyMaterials);
		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "物资信息-通过id删除")
	@ApiOperation(value="物资信息-通过id删除", notes="物资信息-通过id删除")
	@DeleteMapping(value = "/delete")
	@Transactional(rollbackFor = Exception.class)
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		EmergencyMaterials emergencyMaterials = new EmergencyMaterials();
		if (StrUtil.isNotBlank(id)){
			emergencyMaterials.setId(id);
			emergencyMaterials.setDelFlag(1);
		}else {
			return Result.OK("删除失败，id为空或不存在!");
		}
		emergencyMaterialsService.updateById(emergencyMaterials);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "物资信息-批量删除")
	@ApiOperation(value="物资信息-批量删除", notes="物资信息-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	@Transactional(rollbackFor = Exception.class)
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.emergencyMaterialsService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "物资信息-应急物资台账通过id查询")
	@ApiOperation(value="物资信息-应急物资台账通过id查询", notes="物资信息-应急物资台账通过id查询")
	@GetMapping(value = "/queryById")
	public Result<EmergencyMaterials> queryById(@RequestParam(name="id",required=true) String id) {
		EmergencyMaterials emergencyMaterials = emergencyMaterialsService.getById(id);
		if(emergencyMaterials==null) {
			return Result.error("未找到对应数据");
		}
		if (StrUtil.isNotBlank(emergencyMaterials.getCategoryCode())){
			//根据分类编码查询分类名称
			LambdaQueryWrapper<EmergencyMaterialsCategory> queryWrapper = new LambdaQueryWrapper<>();
			queryWrapper.eq(EmergencyMaterialsCategory::getCategoryCode,emergencyMaterials.getCategoryCode()).eq(EmergencyMaterialsCategory::getDelFlag,0);
			EmergencyMaterialsCategory one = emergencyMaterialsCategoryService.getOne(queryWrapper, true);
			emergencyMaterials.setCategoryName(one.getCategoryName());
		}
		if (StrUtil.isNotBlank(emergencyMaterials.getUserId())){
			//根据负责人id查询负责人名称
			LoginUser userById = iSysBaseAPI.getUserById(emergencyMaterials.getUserId());
			emergencyMaterials.setUserName(userById.getRealname());
		}if (StrUtil.isNotBlank(emergencyMaterials.getPrimaryOrg())){
			//根据部门编码查询部门名称
			SysDepartModel departByOrgCode = iSysBaseAPI.getDepartByOrgCode(emergencyMaterials.getPrimaryOrg());
			if(ObjectUtil.isNotEmpty(departByOrgCode)){
				emergencyMaterials.setPrimaryName(departByOrgCode.getDepartName());
			}
		}if (StrUtil.isNotBlank(emergencyMaterials.getLineCode())){
			//根据线路编码查询线路名称
			String position = iSysBaseAPI.getPosition(emergencyMaterials.getLineCode());
			emergencyMaterials.setLineName(position);
		}if(StrUtil.isNotBlank(emergencyMaterials.getStationCode())){
			//根据站点编码查询站点名称
			String position = iSysBaseAPI.getPosition(emergencyMaterials.getStationCode());
			emergencyMaterials.setStationName(position);
		}if(StrUtil.isNotBlank(emergencyMaterials.getPositionCode())){
			//根据位置编码查询位置名称
			String position = iSysBaseAPI.getPosition(emergencyMaterials.getPositionCode());
			emergencyMaterials.setPositionName(position);
		}
		return Result.OK(emergencyMaterials);
	}

	 @AutoLog(value = "物资信息-应急物资位置查询")
	 @ApiOperation(value="物资信息-应急物资位置查询", notes="物资信息-应急物资位置查询")
	 @GetMapping(value = "/getMaterialsCode")
	public Result<?> getMaterialsCode(@RequestParam(name="stationCode",required=false) String stationCode,
									  @RequestParam(name="positionCode",required=false) String positionCode,
									  @RequestParam(name="materialsCode",required=false) String materialsCode){
		 LambdaQueryWrapper<EmergencyMaterials> queryWrapper = new LambdaQueryWrapper<>();
		 if (StrUtil.isNotBlank(stationCode)){
			 queryWrapper.eq(EmergencyMaterials::getStationCode,stationCode);
		 }if (StrUtil.isNotBlank(positionCode)){
			 queryWrapper.eq(EmergencyMaterials::getPositionCode,positionCode);
		 }if (StrUtil.isNotBlank(materialsCode)){
			 queryWrapper.eq(EmergencyMaterials::getMaterialsCode,materialsCode);
		 }
		 queryWrapper.eq(EmergencyMaterials::getDelFlag,0);
		 List<EmergencyMaterials> list = emergencyMaterialsService.list(queryWrapper);
		 if (CollUtil.isNotEmpty(list)){
			 return Result.OK("编码不能重复！");
		 }
		 return Result.OK("校验成功，请继续！");
	}

    /**
    * 导出excel
    *
    * @param request
    * @param condition
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, MaterialAccountDTO condition) {
		ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());

	   return emergencyMaterialsService.getMaterialPatrolList(condition);

    }
	 /**
	  * 应急物资台账导入模板下载
	  *
	  */
	 @AutoLog(value = "应急物资台账导入模板下载", operateType =  6, operateTypeAlias = "应急物资台账导入模板下载", permissionUrl = "")
	 @ApiOperation(value="应急物资台账导入模板下载", notes="应急物资台账导入模板下载")
	 @RequestMapping(value = "/downloadTemple",method = RequestMethod.GET)
	 public void downloadTemple(HttpServletResponse response, HttpServletRequest request) throws IOException {
		 emergencyMaterialsService.getImportTemplate(response,request);
	 }
    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response)throws IOException {
        return emergencyMaterialsService.importExcel(request, response);
    }

}
