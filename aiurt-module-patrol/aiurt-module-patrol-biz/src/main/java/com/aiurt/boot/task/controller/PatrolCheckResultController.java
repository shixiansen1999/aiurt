package com.aiurt.boot.task.controller;

import cn.hutool.core.util.ObjectUtil;
import com.aiurt.boot.constant.PatrolConstant;
import com.aiurt.boot.task.dto.PatrolCheckDTO;
import com.aiurt.boot.task.entity.PatrolCheckResult;
import com.aiurt.boot.task.service.IPatrolCheckResultService;
import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.constant.enums.ModuleType;
import com.aiurt.common.system.base.controller.BaseController;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.vo.LoginUser;
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
	 @PostMapping(value = "/patrolTaskCheckResult")
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
			if(!checkResult.equals(PatrolConstant.RESULT_NORMAL)&&!checkResult.equals(PatrolConstant.RESULT_EXCEPTION))
			{

				updateWrapper.set(PatrolCheckResult::getCheckResult,null).set(PatrolCheckResult::getUserId,sysUser.getId()).eq(PatrolCheckResult::getId,id);
			}
			else
			{
				updateWrapper.set(PatrolCheckResult::getCheckResult,checkResult).set(PatrolCheckResult::getUserId,sysUser.getId()).eq(PatrolCheckResult::getId,id);

			}
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
					}
					else
					{
						return Result.error("填写有误，请重新填写");
					}
				}
			 	else
				{
					updateWrapper.set(PatrolCheckResult::getWriteValue,patrolCheckDTO.getWriteValue()).set(PatrolCheckResult::getUserId,sysUser.getId()).eq(PatrolCheckResult::getId,patrolCheckDTO.getId());

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
