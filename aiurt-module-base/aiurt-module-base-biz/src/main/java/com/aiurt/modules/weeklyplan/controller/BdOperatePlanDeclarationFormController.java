package com.aiurt.modules.weeklyplan.controller;

import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.system.base.controller.BaseController;
import com.aiurt.modules.weeklyplan.dto.*;
import com.aiurt.modules.weeklyplan.entity.BdOperatePlanDeclarationForm;
import com.aiurt.modules.weeklyplan.entity.BdOperatePlanStateChange;
import com.aiurt.modules.weeklyplan.entity.BdSite;
import com.aiurt.modules.weeklyplan.entity.BdStation;
import com.aiurt.modules.weeklyplan.service.IBdOperatePlanDeclarationFormService;
import com.aiurt.modules.weeklyplan.service.IBdSiteService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Set;

/**
 * @Description: 周计划表 Controller
 * @Author: Lai W.
 * @Version: V1.0
 */
@Api(tags="周计划表")
@RestController
@RequestMapping("/weeklyplan/bdOperatePlanDeclarationForm")
@Slf4j
public class BdOperatePlanDeclarationFormController extends BaseController<BdOperatePlanDeclarationForm, IBdOperatePlanDeclarationFormService> {
	@Autowired
	private IBdOperatePlanDeclarationFormService bdOperatePlanDeclarationFormService;
	@Autowired
	private ISysBaseAPI sysBaseApi;
	@Autowired
	private IBdSiteService bdSiteService;

	 /**
	  * 获取施工类型
	  *
	  * @return A list of BdConstructionType DTO.
	  */
	@AutoLog(value = "周计划表-获取施工类型",operateType = 1,operateTypeAlias = "查询",permissionUrl = "/production/plan")
	@ApiOperation(value = "周计划表-获取施工类型", notes = "周计划表-获取施工类型")
	@GetMapping(value = "/getConstructionType")
	@Cacheable(value = {"weeklyConstructionTypes"})
	public Result<List<BdConstructionTypeDTO>> getConstructionType() {
		Result<List<BdConstructionTypeDTO>> result = new Result<>();
		List<BdConstructionTypeDTO> resultBuffer = bdOperatePlanDeclarationFormService.getConstructionType();
		return Result.OK("获取施工类型成功", resultBuffer);
	}

	 /**
	  * 通过teamId获取组内和管辖组内成员-施工负责人
	  * @param teamId Team ID.
	  * @return A list of BdStaffInfoReturnTypeDTO.
	  */
	@AutoLog(value = "周计划表-获取组内和管辖组内用户",operateType = 1,operateTypeAlias = "查询",permissionUrl = "/production/plan")
	@ApiOperation(value = "周计划表-获取组内和管辖组内用户", notes = "周计划表-获取组内和管辖组内用户-施工负责人")
	@GetMapping(value = "/getMemberByTeamId")
	@Cacheable(value = {"weeklyStaffsByTeam"})
	public Result<List<BdStaffInfoReturnTypeDTO>> getMemberByTeamId(@RequestParam(name = "teamId") String teamId) {
		List<BdStaffInfoReturnTypeDTO> resultBuffer = bdOperatePlanDeclarationFormService.getMemberByTeamId(teamId);
		return Result.OK("获取组内和管辖组内用户成功", resultBuffer);
	}

	 /**
	  * 通过roleType和deptID获取用户
	  * @param roleType the Role Type
	  * @param deptId the Department ID
	  * @return A list of BdStaffInfoReturnTypeDTO.
	  */
	@AutoLog(value = "周计划表-通过roleType和deptID获取用户",operateType = 1,operateTypeAlias = "查询",permissionUrl = "/production/plan")
	@ApiOperation(value = "周计划表-通过roleType和deptID获取用户")
	@GetMapping(value = "/getStaffsByRoleType")
	public Result<List<BdStaffInfoReturnTypeDTO>> getStaffsByRoleType(@RequestParam(name = "roleType") String roleType,
										 @RequestParam(name = "deptId") String deptId) {
		Result<List<BdStaffInfoReturnTypeDTO>> result = new Result<>();
		List<BdStaffInfoReturnTypeDTO> resultBuffer =
				bdOperatePlanDeclarationFormService.getStaffsByRoleType(roleType, deptId);
		return Result.OK("通过角色类型和专业获取用户成功", resultBuffer);
	}

	/**
	 * 通过roleName和deptID获取用户
	 *
	 * @param deptId the Department ID
	 * @return A list of BdStaffInfoReturnTypeDTO.
	 */
	@AutoLog(value = "周计划表-通过角色和deptID获取用户",operateType = 1,operateTypeAlias = "查询",permissionUrl = "/production/plan")
	@ApiOperation(value = "周计划表-通过角色和deptID获取用户")
	@GetMapping(value = "/getStaffsByRoleName")
	public Result<List<BdStaffInfoReturnTypeDTO>> getStaffsByRole(@RequestParam(name = "roleName") String roleName,
																	  @RequestParam(name = "deptId") String deptId) {
		Result<List<BdStaffInfoReturnTypeDTO>> result = new Result<>();
		List<BdStaffInfoReturnTypeDTO> resultBuffer =
				bdOperatePlanDeclarationFormService.getStaffsByRoleName(roleName, deptId);
		return Result.OK("通过角色类型和专业获取用户成功", resultBuffer);
	}


	 /**
	  * 获取请点车站和销点车站.
	  * 目前是获取所有车站.
	  * @return A list of Station DTO.
	  */
	@AutoLog(value = "周计划表-获取车站列表",operateType = 1,operateTypeAlias = "查询",permissionUrl = "/production/plan")
	@ApiOperation(value = "周计划表-获取车站列表", notes = "周计划表-获取车站列表-请点销点车站")
	@GetMapping(value = "/getStations")
	public Result<List<BdStation>> getStationList() {
		List<BdStation> resultBuffer = bdOperatePlanDeclarationFormService.getStations();
		return Result.OK("获取车站列表成功", resultBuffer);
	}
	 /**
	  * 获取所有地铁线路.
	  * @return A list of lines.
	  */
	@AutoLog(value = "周计划表-获取地铁线路",operateType = 1,operateTypeAlias = "查询",permissionUrl = "/production/plan")
	@ApiOperation(value = "周计划表-获取地铁线路", notes = "周计划表-获取地铁线路")
	@GetMapping(value = "/getLines")
	public Result<List<BdLineDTO>> getLines() {
		List<BdLineDTO> resultBuffer = bdOperatePlanDeclarationFormService.getLines();
		return Result.OK("获取线路成功", resultBuffer);
	}

	 /**
	  * 获取登录人员信息。
	  * @return BdUserInfoDTO.
	  */
	@AutoLog(value = "周计划表-获取登录人员信息",operateType = 1,operateTypeAlias = "查询",permissionUrl = "/production/plan")
	@ApiOperation(value = "周计划表-获取登录人员信息", notes = "周计划表-获取登录人员信息")
	@GetMapping(value = "/getUserInfo")
	public Result<?> getUserInfo() {
		//获取当前用户
		LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		List<BdUserInfoDTO> resultBuffer = bdOperatePlanDeclarationFormService.getUserInfo(sysUser.getId());
		if(null != resultBuffer && resultBuffer.size() >0){
			return  Result.OK("获取登录用户信息成功", resultBuffer.get(0));
		}
		return  Result.error("未查询到用户信息");
	}

	/**
	 * 分页查询周计划表.
	 * @param queryPagesParams 查询参数DTO.
	 * @param pageNo 页码.
	 * @param pageSize 页面大小.
	 * @return Current Page.
	 */
	@AutoLog(value = "周计划表-分页列表查询",operateType = 1,operateTypeAlias = "查询",permissionUrl = "/production/plan")
	@ApiOperation(value="周计划表-分页列表查询", notes="周计划表-分页列表查询")
	@GetMapping(value = "/queryPages")
	public Result<?> queryPages(QueryPagesParams queryPagesParams,
								@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
								@RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
		queryPagesParams.setIsChange(0);
		Page<BdOperatePlanDeclarationFormReturnTypeDTO> result =
				bdOperatePlanDeclarationFormService.queryPages(queryPagesParams, pageNo, pageSize, null);
		return Result.OK("分页查询(周计划)成功", result);
	}

	/**
	 * 根据作业时间查询已同意的周计划
	 * @param
	 * @return Current Page.
	 */
	@AutoLog(value = "周计划表-根据作业时间查询已同意的周计划",operateType = 1,operateTypeAlias = "查询",permissionUrl = "/production/plan")
	@ApiOperation(value="周计划表-根据作业时间查询已同意的周计划", notes="根据作业时间查询已同意的周计划-分页列表查询")
	@GetMapping(value = "/queryListByDate")
	public Result<?> queryListByDate(String taskDate) {
		List<BdOperatePlanDeclarationReturnDTO> list = bdOperatePlanDeclarationFormService.queryListByDate(taskDate);
		return Result.OK("查询(周计划)成功", list);
	}

	/**
	 * 分页查询补充计划.
	 * @param queryPagesParams 查询参数.
	 * @param pageNo 页码.
	 * @param pageSize 页面大小.
	 * @return Current page.
	 */
	@AutoLog(value = "补充计划-分页列表查询",operateType = 1,operateTypeAlias = "查询",permissionUrl = "/production/plan")
	@ApiOperation(value = "补充计划-分页列表查询", notes = "补充计划-分页列表查询")
	@GetMapping(value = "/queryChangeablePages")
	public Result<?> queryChangeablePages(QueryPagesParams queryPagesParams,
										  @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
										  @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
		queryPagesParams.setIsChange(1);
		Page<BdOperatePlanDeclarationFormReturnTypeDTO> result =
				bdOperatePlanDeclarationFormService.queryPages(queryPagesParams, pageNo, pageSize, null);
		return Result.OK("分页查询(补充计划)成功", result);
	}

	/**
	 *  分页查询审批有关计划.
	 * @param queryPagesParams 查询参数.
	 * @param pageNo 页码.
	 * @param pageSize 页面大小.
	 * @return Current Page.
	 */
	@AutoLog(value = "计划审批-分页列表查询",operateType = 1,operateTypeAlias = "查询",permissionUrl = "/production/plan")
	@ApiOperation(value = "计划审批-分页列表查询", notes = "计划审批-分页列表查询")
	@GetMapping(value = "/queryUpdatablePages")
	public Result<?> queryUpdatablePages(QueryPagesParams queryPagesParams,
										  @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
										  @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
										  @RequestParam(name = "busId", required = false) String busId) {
		queryPagesParams.setIsChange(2);
		Page<BdOperatePlanDeclarationFormReturnTypeDTO> result =
				bdOperatePlanDeclarationFormService.queryPages(queryPagesParams, pageNo, pageSize, busId);
		return Result.OK("分页查询(计划审批)成功", result);
	}

	/**
	 * 添加
	 *
	 * @param bdOperatePlanDeclarationForm 周计划表.
	 * @return Result.
	 */
	@AutoLog(value = "周计划表-添加",operateType = 2,operateTypeAlias = "添加",permissionUrl = "/production/plan")
	@ApiOperation(value="周计划表-添加", notes="周计划表-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody BdOperatePlanDeclarationForm bdOperatePlanDeclarationForm) {
		BdOperatePlanDeclarationForm declarationForm;
		//将RequestBody转换成DeclarationFormEntity
		try {
			//只有 工班长、工作负责人、驻班工程师 有权限添加生产计划
			LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
			Set<String> roleSet = sysBaseApi.getUserRoleSet(sysUser.getUsername());
			long count = roleSet.stream().filter(s -> ("foreman").equals(s) || ("on_duty_engineer").equals(s) || ("conscientious").equals(s)).count();
			if(count == 0){
				return Result.error("您没有权限添加生产计划");
			}else{
				bdOperatePlanDeclarationForm.setApplyStaffId(sysUser.getId());
				declarationForm = bdOperatePlanDeclarationFormService.convertRequestBody(bdOperatePlanDeclarationForm);
				return Result.OK("添加成功", declarationForm.getId());
			}
		/*} catch (IllegalStateException e) {
			return Result.error("施工负责人已被占用");*/
		} catch (RuntimeException e) {
			return Result.error("该计划已经变更过，不能进行再次变更");
		}
	}

	/**
	 *  编辑
	 *
	 * @param bdOperatePlanDeclarationForm 周计划表.
	 * @return Result.
	 */
	@AutoLog(value = "周计划表-编辑",operateType = 3,operateTypeAlias = "编辑",permissionUrl = "/production/plan")
	@ApiOperation(value="周计划表-编辑", notes="周计划表-编辑")
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody BdOperatePlanDeclarationForm bdOperatePlanDeclarationForm) {
		bdOperatePlanDeclarationFormService.edit(bdOperatePlanDeclarationForm);
		return Result.OK("编辑成功!");
	}

	/**
	 * 获取所有可能状态.
	 * @param isAdditional 是否为补充计划.
	 * @return result.
	 */
	@AutoLog(value = "周计划表-获取所有状态",operateType = 1,operateTypeAlias = "查询",permissionUrl = "/production/plan")
	@ApiOperation(value = "周计划表-获取所有状态", notes = "周计划表-获取所有状态")
	@GetMapping(value = "/getStatus")
	public Result<?> getStatus(@RequestParam(name = "isAddidional") Integer isAdditional) {
		List<FormStatusTup> result = bdOperatePlanDeclarationFormService.getStatus(isAdditional);
		return Result.OK("获取状态成功", result);
	}

	@AutoLog(value = "周计划表-确认流程结束",operateType = 3,operateTypeAlias = "编辑",permissionUrl = "/production/plan")
	@ApiOperation(value = "周计划表-确认流程结束", notes = "周计划表-确认流程结束")
	@GetMapping(value = "/setApplyFormStatus")
	public Result<?> setApplyFormStatus(@RequestParam(name = "id") Integer id,
										@RequestParam(name = "applyFormStatus") Integer applyFormStatus) {
		return bdOperatePlanDeclarationFormService.setApplyFormStatus(id, applyFormStatus);
	}


	/**
	 * 通过id查询
	 *
	 * @param id id.
	 * @return Result.
	 */
	@AutoLog(value = "周计划表-通过id查询",operateType = 1,operateTypeAlias = "查询",permissionUrl = "/production/plan")
	@ApiOperation(value="周计划表-通过id查询", notes="周计划表-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		BdOperatePlanDeclarationFormReturnTypeDTO result =
				bdOperatePlanDeclarationFormService.getFormInfoById(Integer.parseInt(id));
		if (result == null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(result);
	}

	/**
	 * 通过id删除
	 *
	 * @param id id.
	 * @return Result.
	 */
	@AutoLog(value = "周计划表-通过id删除",operateType = 4,operateTypeAlias = "删除",permissionUrl = "/production/plan")
	@ApiOperation(value="周计划表-通过id删除", notes="周计划表-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		bdOperatePlanDeclarationFormService.removeById(id);
		return Result.OK("删除成功!");
	}

    /**
    * 导出excel
	 * @param queryPagesParams 查询参数.
	 */

	@AutoLog(value = "周计划表-导出excel",operateType = 6,operateTypeAlias = "导出",permissionUrl = "/production/plan")
	@ApiOperation(value="周计划表-导出excel", notes="周计划表-导出excel")
    @RequestMapping(value = "/exportXls", method = RequestMethod.GET)
    public void exportXls(QueryPagesParams queryPagesParams, HttpServletResponse response) {
		if (queryPagesParams.getIsChange() == 0) {
			List<BdOperatePlanDeclarationFormReturnTypeDTO> record = bdOperatePlanDeclarationFormService.getListByQuery(queryPagesParams);
			bdOperatePlanDeclarationFormService.exportExcel(record, response, queryPagesParams);
		} else {
			List<BdOperatePlanDeclarationFormReturnTypeDTO> record =
					bdOperatePlanDeclarationFormService.queryPages(queryPagesParams, 1, 100000, null).getRecords();
			bdOperatePlanDeclarationFormService.exportExcelChangeable(record, response, queryPagesParams);
		}
	}

	/**
	 *  通过excel导入数据
	 *
	 * @param request
	 * @return
	 */
	@AutoLog(value = "周计划表-通过excel导入数据",operateType = 5,operateTypeAlias = "导入",permissionUrl = "/production/plan")
	@ApiOperation(value="周计划表-通过excel导入数据", notes="周计划表-通过excel导入数据")
	@RequestMapping(value = "/importExcel", method = RequestMethod.POST)
	public Result<?> importExcel(@RequestParam(value = "excel", required = false) MultipartFile excel,
								 HttpServletRequest request) {
		return Result.OK("success", bdOperatePlanDeclarationFormService.importExcel(excel));
	}

	/**
	 * 计划审批接口.
	 * @param voice 录音路径.
	 * @param picture 图片路径.
	 * @param bdOperatePlanStateChange 审批参数.
	 * @return Result.
	 */
	@AutoLog(value = "周计划表-审批计划",operateType = 3,operateTypeAlias = "编辑",permissionUrl = "/production/plan")
	@ApiOperation(value = "周计划表-审批计划", notes = "周计划表-审批计划")
	@RequestMapping(value = "/updateOperateForm", method = RequestMethod.GET)
	public Result<?> updateOperationForm(@RequestParam(name = "voice", required = false) String voice,
										 @RequestParam(name = "picture", required = false) String picture,
										 BdOperatePlanStateChange bdOperatePlanStateChange) {
		return bdOperatePlanDeclarationFormService.updateOperateForm(voice, picture, bdOperatePlanStateChange);
	}




	/**
	 * 周/月计划表-查询线路负责人
	 * @return
	 */
	@AutoLog(value = "周计划表-查询线路负责人",operateType = 1,operateTypeAlias = "查询",permissionUrl = "/production/plan")
	@ApiOperation(value = "周计划表-查询线路负责人", notes = "周计划表-查询线路负责人")
	@RequestMapping(value = "/queryLineStaff", method = RequestMethod.GET)
	public Result<?> queryLineStaff() {
		List<BdStaffInfoReturnTypeDTO> list = bdOperatePlanDeclarationFormService.queryLineStaff();
		return Result.OK(list);
	}

	/**
	 * 周计划表-重新申请
	 * @return
	 */
	@AutoLog(value = "周计划表-重新申请",operateType = 3,operateTypeAlias = "编辑",permissionUrl = "/production/plan")
	@ApiOperation(value = "周计划表-重新申请", notes = "周计划表-重新申请")
	@RequestMapping(value = "/reapply", method = RequestMethod.GET)
	public Result<?> reapply(@RequestParam(name = "id", required = false) Integer id) {
		bdOperatePlanDeclarationFormService.reapply(id);
		return Result.OK();
	}

	/**
	 * 周计划表-编辑
	 * @param bdOperatePlanDeclarationForm
	 * @return
	 */
	@AutoLog(value = "周计划表-编辑",operateType = 3,operateTypeAlias = "编辑",permissionUrl = "/production/plan")
	@ApiOperation(value="周计划表-编辑", notes="周计划表-编辑")
	@GetMapping(value = "/updateById")
	public Result<?> updateById(BdOperatePlanDeclarationForm bdOperatePlanDeclarationForm) {
		bdOperatePlanDeclarationFormService.edit(bdOperatePlanDeclarationForm);
		return Result.OK("编辑成功!");
	}

	/**
	 * 周计划表-是否有权限审批
	 * @return
	 */
	@AutoLog(value = "周计划表-是否有权限审批",operateType = 1,operateTypeAlias = "查询",permissionUrl = "/production/plan")
	@ApiOperation(value="周计划表-是否有权限审批", notes="周计划表-是否有权限审批")
	@GetMapping(value = "/isApprove")
	public Result<?> isApprove(@RequestParam(name = "id", required = false) Integer id) {
		IsApproveDTO isApproveDTO = bdOperatePlanDeclarationFormService.isApprove(id);
		return Result.OK(isApproveDTO);
	}

	/**
	 * 获取当前用户管辖班组下工区
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	@AutoLog(value = "获取当前用户管辖班组下工区")
	@ApiOperation(value = "获取当前用户管辖班组下工区", notes = "获取当前用户管辖班组下工区")
	@GetMapping(value = "/querySiteByTeam")
	public Result<?> querySiteByTeam(@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
									 @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
		IPage<BdSite> bdSiteIpage = bdSiteService.querySiteByTeam(new Page<BdSite>(pageNo, pageSize));
		return Result.OK(bdSiteIpage);
	}
}
