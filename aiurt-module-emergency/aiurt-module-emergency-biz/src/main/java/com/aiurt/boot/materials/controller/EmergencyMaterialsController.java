package com.aiurt.boot.materials.controller;

import java.util.Arrays;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.boot.materials.dto.EmergencyMaterialsDTO;
import com.aiurt.boot.materials.dto.MaterialAccountDTO;
import com.aiurt.boot.materials.dto.MaterialPatrolDTO;
import com.aiurt.boot.materials.entity.EmergencyMaterialsCategory;
import com.aiurt.boot.materials.entity.EmergencyMaterialsInvoices;
import com.aiurt.boot.materials.entity.EmergencyMaterialsInvoicesItem;
import com.aiurt.boot.materials.service.IEmergencyMaterialsCategoryService;
import com.aiurt.boot.materials.service.IEmergencyMaterialsInvoicesItemService;
import com.aiurt.boot.materials.service.IEmergencyMaterialsInvoicesService;
import com.aiurt.common.constant.enums.ModuleType;
import com.aiurt.common.system.base.controller.BaseController;
import com.aiurt.boot.materials.entity.EmergencyMaterials;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import liquibase.pro.packaged.E;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.query.QueryGenerator;
import com.aiurt.boot.materials.service.IEmergencyMaterialsService;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.system.vo.SysDepartModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import com.aiurt.common.aspect.annotation.AutoLog;

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
	 @AutoLog(value = "物资信息-应急物资台账列表查询", operateType =  1, operateTypeAlias = "应急物资台账列表查询", module = ModuleType.INSPECTION)
	 @ApiOperation(value = "物资信息-应急物资台账列表查询", notes = "物资信息-应急物资台账列表查询")
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


	 @AutoLog(value = "物资信息-应急物资台账检查-巡检标准下拉")
	 @ApiOperation(value="物资信息-应急物资台账检查-巡检标准下拉", notes="物资信息-应急物资台账检查-巡检标准下拉")
	 @GetMapping(value = "/getMaterialPatrol")
	 public Result<?> getMaterialPatrol(){
		 MaterialPatrolDTO materialPatrol = emergencyMaterialsService.getMaterialPatrol();
		 return Result.OK(materialPatrol);
	 }


	 @AutoLog(value = "物资信息-巡检记录信息查询")
	 @ApiOperation(value="物资信息-巡检记录信息查询", notes="物资信息-巡检记录信息查询")
	 @GetMapping(value = "/getPatrolRecord")
	 public Result<?> getPatrolRecord(@RequestParam(name = "materialsCode",required=true) String materialsCode,
									  @RequestParam(name = "startTime",required=false) String  startTime,
	                                  @RequestParam(name = "endTime",required=false) String  endTime){
		 List<EmergencyMaterialsInvoicesItem> patrolRecord = iEmergencyMaterialsInvoicesItemService.getPatrolRecord(materialsCode, startTime, endTime);
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

	/**
	 *   添加
	 *
	 * @param emergencyMaterials
	 * @return
	 */
	@AutoLog(value = "物资信息-添加")
	@ApiOperation(value="物资信息-添加", notes="物资信息-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody EmergencyMaterials emergencyMaterials) {
		emergencyMaterialsService.save(emergencyMaterials);
		return Result.OK("添加成功！");
	}


	 @AutoLog(value = "物资信息-应急物资巡检登记-提交")
	 @ApiOperation(value="物资信息-应急物资巡检登记-提交", notes="物资信息-应急物资巡检登记-提交")
	 @PostMapping(value = "/emergencyMaterialsSubmit")
	 public Result<?> emergencyMaterialsSubmit(@RequestBody EmergencyMaterialsDTO emergencyMaterialsDTO){
		 EmergencyMaterialsInvoices emergencyMaterialsInvoices = new EmergencyMaterialsInvoices();
		 //应急物资巡检单号
		 emergencyMaterialsInvoices.setMaterialsPatrolCode(emergencyMaterialsDTO.getMaterialsPatrolCode());
		 //巡检标准
		 if (StrUtil.isNotBlank(emergencyMaterialsDTO.getStandardCode())){
			 emergencyMaterialsInvoices.setStandardCode(emergencyMaterialsDTO.getStandardCode());
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
		 emergencyMaterialsInvoicesItemList.forEach(e->{
		 	e.setInvoicesId(emergencyMaterialsInvoices.getId());
		 });

		 //插入物资巡检检修项
		 iEmergencyMaterialsInvoicesItemService.saveBatch(emergencyMaterialsInvoicesItemList);
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
		if (StrUtil.isNotBlank(emergencyMaterials.getCategoryCode())){
			//根据分类编码查询分类名称
			LambdaQueryWrapper<EmergencyMaterialsCategory> queryWrapper = new LambdaQueryWrapper<>();
			queryWrapper.eq(EmergencyMaterialsCategory::getCategoryCode,emergencyMaterials.getCategoryCode());
			EmergencyMaterialsCategory one = emergencyMaterialsCategoryService.getOne(queryWrapper, true);
			emergencyMaterials.setCategoryName(one.getCategoryName());
		}
		if(emergencyMaterials==null) {
			return Result.error("未找到对应数据");
		}
		if (StrUtil.isNotBlank(emergencyMaterials.getUserId())){
			//根据负责人id查询负责人名称
			LoginUser userById = iSysBaseAPI.getUserById(emergencyMaterials.getUserId());
			emergencyMaterials.setUserName(userById.getRealname());
		}if (StrUtil.isNotBlank(emergencyMaterials.getPrimaryOrg())){
			//根据部门编码查询部门名称
			SysDepartModel departByOrgCode = iSysBaseAPI.getDepartByOrgCode(emergencyMaterials.getPrimaryOrg());
			emergencyMaterials.setPrimaryName(departByOrgCode.getDepartName());
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
		 List<EmergencyMaterials> list = emergencyMaterialsService.list(queryWrapper);
		 if (CollUtil.isNotEmpty(list)){
			 return Result.OK("同一位置的编码不能重复！");
		 }
		 return Result.OK("校验成功，请继续！");
	}

    /**
    * 导出excel
    *
    * @param request
    * @param emergencyMaterials
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, EmergencyMaterials emergencyMaterials) {
        return super.exportXls(request, emergencyMaterials, EmergencyMaterials.class, "emergency_materials");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, EmergencyMaterials.class);
    }

}
