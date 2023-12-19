package com.aiurt.boot.task.controller;

import cn.hutool.core.util.ObjectUtil;
import com.aiurt.boot.constant.PatrolConstant;
import com.aiurt.boot.constant.SysParamCodeConstant;
import com.aiurt.boot.task.dto.PatrolCheckDTO;
import com.aiurt.boot.task.entity.PatrolCheckResult;
import com.aiurt.boot.task.service.IPatrolCheckResultService;
import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.constant.enums.ModuleType;
import com.aiurt.common.system.base.controller.BaseController;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.api.ISysParamAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.system.vo.SysParamModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Description: patrol_check_result
 * @Author: aiurt
 * @Date:   2022-06-21
 * @Version: V1.0
 */
@Api(tags="巡检检查项(检查结果)")
@RestController
@RequestMapping("/patrolCheckResult")
@Slf4j
public class PatrolCheckResultController extends BaseController<PatrolCheckResult, IPatrolCheckResultService> {
	@Autowired
	private IPatrolCheckResultService patrolCheckResultService;
	@Autowired
	private ISysBaseAPI sysBaseApi;
	@Autowired
	private ISysParamAPI iSysParamAPI;

	/**
	 * 分页列表查询
	 *
	 * @param patrolCheckResult
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	/*//@AutoLog(value = "patrol_check_result-分页列表查询")
	@ApiOperation(value="patrol_check_result-分页列表查询", notes="patrol_check_result-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<PatrolCheckResult>> queryPageList(PatrolCheckResult patrolCheckResult,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<PatrolCheckResult> queryWrapper = QueryGenerator.initQueryWrapper(patrolCheckResult, req.getParameterMap());
		Page<PatrolCheckResult> page = new Page<PatrolCheckResult>(pageNo, pageSize);
		IPage<PatrolCheckResult> pageList = patrolCheckResultService.page(page, queryWrapper);
		return Result.OK(pageList);
	}*/

	 /**
	  * app巡检-检查项-检查结果-保存
	  * @param checkResult
	  * @param id
	  * @param req
	  * @return
	  */
	 @AutoLog(value = "app巡检-检查项-检查结果备注-保存", operateType = 3, operateTypeAlias = "修改", module = ModuleType.PATROL,permissionUrl = "/Inspection/pool")
	 @ApiOperation(value = "app巡检-检查项-检查结果备注-保存", notes = "app巡检-检查项-检查结果备注-保存")
	 @GetMapping(value = "/patrolTaskCheckResult")
	 public Result<?> patrolTaskCheckResult(@RequestParam(name ="id")String id,
											@RequestParam(name="checkResult") Integer checkResult,
											@RequestParam(name="remark") String remark,
											HttpServletRequest req) {
		 LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		 LambdaUpdateWrapper<PatrolCheckResult> updateWrapper= new LambdaUpdateWrapper<>();
		 //前端会传null字符串进来
		 String a ="null";
		 if(!a.equals(checkResult)&&ObjectUtil.isNotEmpty(checkResult))
		 {
			 updateWrapper.set(PatrolCheckResult::getCheckResult,checkResult).set(PatrolCheckResult::getUserId,sysUser.getId()).eq(PatrolCheckResult::getId,id);
		 }
		 if(ObjectUtil.isNotNull(remark))
		 {
			 updateWrapper.set(PatrolCheckResult::getRemark,remark).set(PatrolCheckResult::getUserId,sysUser.getId()).eq(PatrolCheckResult::getId,id);
		 }
		 patrolCheckResultService.update(updateWrapper);
		 return Result.OK("保存成功");
	 }
	 /**
	  * app巡检-检查项-检查值-保存
	  * @param patrolCheckDTO
	  * @param req
	  * @return
	  */
	 @AutoLog(value = "app巡检-检查项-检查值-保存", operateType = 3, operateTypeAlias = "修改", module = ModuleType.PATROL,permissionUrl = "/Inspection/pool")
	 @ApiOperation(value = "app巡检-检查项-检查值-保存", notes = "app巡检-检查项-检查值-保存")
	 @PostMapping(value = "/patrolTaskAccessory")
	 @Transactional(rollbackFor = Exception.class)
	 public Result<?> patrolTaskAccessory(@RequestBody PatrolCheckDTO patrolCheckDTO,
	 									  HttpServletRequest req) {
		 LambdaUpdateWrapper<PatrolCheckResult> updateWrapper = new LambdaUpdateWrapper<>();
		  PatrolCheckResult patrolCheckResult = patrolCheckResultService.getById(patrolCheckDTO.getId());
		 LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		 if(ObjectUtil.isNotEmpty(patrolCheckDTO.getInputType()))
		 {
			 if(PatrolConstant.DEVICE_INP_TYPE.equals(patrolCheckDTO.getInputType()))
		 	{
				updateWrapper.set(PatrolCheckResult::getOptionValue,patrolCheckDTO.getOptionValue()).set(PatrolCheckResult::getUserId,sysUser.getId()).eq(PatrolCheckResult::getId,patrolCheckDTO.getId());
			 }
			 if(PatrolConstant.DEVICE_OUT.equals(patrolCheckDTO.getInputType()))
			 {
			 	if(ObjectUtil.isNotEmpty(patrolCheckResult.getRegular()))
				{
					Pattern pattern = Pattern.compile(patrolCheckDTO.getRegular());
					Matcher matcher = pattern.matcher(patrolCheckDTO.getWriteValue());
					if(matcher.find())
					{
						updateWrapper.set(PatrolCheckResult::getWriteValue,patrolCheckDTO.getWriteValue()).set(PatrolCheckResult::getUserId,sysUser.getId()).eq(PatrolCheckResult::getId,patrolCheckDTO.getId());
					} else {
						String regex = sysBaseApi.translateDict("regex", patrolCheckDTO.getRegular());
						return Result.error(regex);
					}
				}
			 	else
				{
					updateWrapper.set(PatrolCheckResult::getWriteValue, patrolCheckDTO.getWriteValue()).set(PatrolCheckResult::getUserId, sysUser.getId()).eq(PatrolCheckResult::getId, patrolCheckDTO.getId());
				}
			 }
			 if (PatrolConstant.DATE_TYPE_SPECIALCHAR.equals(patrolCheckDTO.getInputType())) {
				 updateWrapper.set(PatrolCheckResult::getSpecialCharacters, patrolCheckDTO.getSpecialCharacters()).set(PatrolCheckResult::getUserId, sysUser.getId()).eq(PatrolCheckResult::getId, patrolCheckDTO.getId());

				 //通信要根据温湿度自动判断检查结果是否异常
				 String specialCharacters = patrolCheckDTO.getSpecialCharacters();
				 int firstSlashIndex = specialCharacters.indexOf('/');
				 int secondSlashIndex = specialCharacters.indexOf('/', firstSlashIndex + 1);

				 SysParamModel paramModel = iSysParamAPI.selectByCode(SysParamCodeConstant.PATROL_AUTO_TEMP_HUMIDITY_JUDGE);
				 boolean isTemperature = SysParamCodeConstant.PATROL_TEMP_STATUS_TEST.equals(patrolCheckResult.getResultDictCode());
				 boolean isHumidity = SysParamCodeConstant.PATROL_HUMIDITY_STATUS_TEST.equals(patrolCheckResult.getResultDictCode());

				 if (CommonConstant.SYSTEM_CONFIG_BOOLEAN_YES.equals(paramModel.getValue()) && (isTemperature || isHumidity)) {

					 if (firstSlashIndex != -1 && secondSlashIndex != -1) {
						 String result = specialCharacters.substring(firstSlashIndex + 1, secondSlashIndex);
						 double max = 0.0;
						 double min = 0.0;
						 //温度判断
						 if (isTemperature) {
							 SysParamModel maximumTemperature = iSysParamAPI.selectByCode(SysParamCodeConstant.MAXIMUM_TEMPERATURE);
							 max = Double.parseDouble(maximumTemperature.getValue());
							 SysParamModel minimumTemperature = iSysParamAPI.selectByCode(SysParamCodeConstant.MINIMUM_TEMPERATURE);
							 min = Double.parseDouble(minimumTemperature.getValue());

						 }
						 //湿度判断
						 if (isHumidity) {
							 SysParamModel maximumHumidity = iSysParamAPI.selectByCode(SysParamCodeConstant.MAXIMUM_HUMIDITY);
							 max = Double.parseDouble(maximumHumidity.getValue());
							 SysParamModel minimumHumidity = iSysParamAPI.selectByCode(SysParamCodeConstant.MINIMUM_HUMIDITY);
							 min = Double.parseDouble(minimumHumidity.getValue());
						 }

						 if (Double.parseDouble(result) > max || Double.parseDouble(result) < min) {
							 updateWrapper.set(PatrolCheckResult::getCheckResult, PatrolConstant.RESULT_EXCEPTION).set(PatrolCheckResult::getUserId, sysUser.getId()).eq(PatrolCheckResult::getId, patrolCheckDTO.getId());
						 } else {
							 updateWrapper.set(PatrolCheckResult::getCheckResult, PatrolConstant.RESULT_NORMAL).set(PatrolCheckResult::getUserId, sysUser.getId()).eq(PatrolCheckResult::getId, patrolCheckDTO.getId());
						 }
					 }

				 }
			 }
		 }
		 patrolCheckResultService.update(updateWrapper);
		 return Result.OK("检查值保存成功");
	 }

	/**
	 *   添加
	 *
	 * @param patrolCheckResult
	 * @return
	 */
	/*@AutoLog(value = "patrol_check_result-添加")
	@ApiOperation(value="patrol_check_result-添加", notes="patrol_check_result-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody PatrolCheckResult patrolCheckResult) {
		patrolCheckResultService.save(patrolCheckResult);
		return Result.OK("添加成功！");
	}*/

	/**
	 *  编辑
	 *
	 * @param patrolCheckResult
	 * @return
	 */
	/*@AutoLog(value = "patrol_check_result-编辑")
	@ApiOperation(value="patrol_check_result-编辑", notes="patrol_check_result-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody PatrolCheckResult patrolCheckResult) {
		patrolCheckResultService.updateById(patrolCheckResult);
		return Result.OK("编辑成功!");
	}
*/
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	/*@AutoLog(value = "patrol_check_result-通过id删除")
	@ApiOperation(value="patrol_check_result-通过id删除", notes="patrol_check_result-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		patrolCheckResultService.removeById(id);
		return Result.OK("删除成功!");
	}
*/
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	/*@AutoLog(value = "patrol_check_result-批量删除")
	@ApiOperation(value="patrol_check_result-批量删除", notes="patrol_check_result-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.patrolCheckResultService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}*/

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	/*//@AutoLog(value = "patrol_check_result-通过id查询")
	@ApiOperation(value="patrol_check_result-通过id查询", notes="patrol_check_result-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<PatrolCheckResult> queryById(@RequestParam(name="id",required=true) String id) {
		PatrolCheckResult patrolCheckResult = patrolCheckResultService.getById(id);
		if(patrolCheckResult==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(patrolCheckResult);
	}*/

    /**
    * 导出excel
    *
    * @param request
    * @param patrolCheckResult
    */
  /*  @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, PatrolCheckResult patrolCheckResult) {
        return super.exportXls(request, patrolCheckResult, PatrolCheckResult.class, "patrol_check_result");
    }*/

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
   /* @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, PatrolCheckResult.class);
    }*/

}
