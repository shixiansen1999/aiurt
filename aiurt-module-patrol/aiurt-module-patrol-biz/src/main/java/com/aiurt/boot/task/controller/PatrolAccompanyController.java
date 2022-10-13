package com.aiurt.boot.task.controller;

import cn.hutool.core.collection.CollUtil;
import com.aiurt.boot.task.dto.PatrolAccompanyDTO;
import com.aiurt.boot.task.dto.PatrolAccompanySaveDTO;
import com.aiurt.boot.task.entity.PatrolAccompany;
import com.aiurt.boot.task.entity.PatrolTaskDevice;
import com.aiurt.boot.task.service.IPatrolAccompanyService;
import com.aiurt.boot.task.service.IPatrolTaskDeviceService;
import com.aiurt.boot.task.service.IPatrolTaskService;
import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.constant.enums.ModuleType;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.common.system.base.controller.BaseController;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Description: patrol_accompany
 * @Author: aiurt
 * @Date:   2022-06-28
 * @Version: V1.0
 */
@Api(tags="巡检同行人")
@RestController
@RequestMapping("/patrolAccompany")
@Slf4j
public class PatrolAccompanyController extends BaseController<PatrolAccompany, IPatrolAccompanyService> {
	@Autowired
	private IPatrolAccompanyService patrolAccompanyService;
	@Autowired
	private IPatrolTaskDeviceService patrolTaskDeviceService;
	@Autowired
	private IPatrolTaskService patrolTaskService;

	/**
	 * 分页列表查询
	 *
	 * @param patrolAccompany
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "patrol_accompany-分页列表查询")
	/*@ApiOperation(value="patrol_accompany-分页列表查询", notes="patrol_accompany-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<PatrolAccompany>> queryPageList(PatrolAccompany patrolAccompany,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<PatrolAccompany> queryWrapper = QueryGenerator.initQueryWrapper(patrolAccompany, req.getParameterMap());
		Page<PatrolAccompany> page = new Page<PatrolAccompany>(pageNo, pageSize);
		IPage<PatrolAccompany> pageList = patrolAccompanyService.page(page, queryWrapper);
		return Result.OK(pageList);
	}*/

	 /**
	  *app填写巡检工单-添加同行人||巡检位置
	  * @param patrolAccompanyList
	  * @return
	  */
	 @AutoLog(value = "app填写巡检工单-添加同行人||巡检位置", operateType = 3, operateTypeAlias = "修改", module = ModuleType.PATROL,permissionUrl = "/Inspection/pool")
	 @ApiOperation(value="app填写巡检工单-添加同行人||巡检位置", notes="app填写巡检工单-添加同行人||巡检位置")
	 @PostMapping(value = "/addPatrolAccompany")
	 public Result<String> addPatrolAccompany(@RequestBody PatrolAccompanySaveDTO patrolAccompanyList) {
		 if(patrolAccompanyList.getPatrolNumber()==null)
		 {
			 throw new AiurtBootException("小主，该巡检任务不在您的范围之内哦");
		 }
		 //获取该任务的指派人
		 LambdaUpdateWrapper<PatrolTaskDevice> updateWrapper= new LambdaUpdateWrapper<>();
		 updateWrapper.set(PatrolTaskDevice::getCustomPosition,patrolAccompanyList.getPosition()).eq(PatrolTaskDevice::getPatrolNumber,patrolAccompanyList.getPatrolNumber());
		 patrolTaskDeviceService.update(updateWrapper);
		 LambdaQueryWrapper<PatrolAccompany> queryWrapper = new LambdaQueryWrapper<>();
		 queryWrapper.eq(PatrolAccompany::getTaskDeviceCode,patrolAccompanyList.getPatrolNumber());
		 List<PatrolAccompany> list = patrolAccompanyService.list(queryWrapper);
		 if(CollUtil.isNotEmpty(list))
		 {
			 patrolAccompanyService.removeBatchByIds(list);
		 }
		 for(PatrolAccompanyDTO accompany:patrolAccompanyList.getAccompanyDTOList())
		{
			PatrolAccompany patrolAccompany = new PatrolAccompany();
			BeanUtils.copyProperties(accompany,patrolAccompany);
			patrolAccompany.setDelFlag(0);
			patrolAccompany.setTaskDeviceCode(patrolAccompanyList.getPatrolNumber());
			patrolAccompanyService.save(patrolAccompany);
		}

		 return Result.OK("添加成功！");
	 }

	/**
	 *   添加
	 *
	 * @param patrolAccompany
	 * @return
	 */
	/*@AutoLog(value = "patrol_accompany-添加")
	@ApiOperation(value="patrol_accompany-添加", notes="patrol_accompany-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody PatrolAccompany patrolAccompany) {
		patrolAccompanyService.save(patrolAccompany);
		return Result.OK("添加成功！");
	}*/

	/**
	 *  编辑
	 *
	 * @param patrolAccompany
	 * @return
	 */
	/*@AutoLog(value = "patrol_accompany-编辑")
	@ApiOperation(value="patrol_accompany-编辑", notes="patrol_accompany-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody PatrolAccompany patrolAccompany) {
		patrolAccompanyService.updateById(patrolAccompany);
		return Result.OK("编辑成功!");
	}*/

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	/*@AutoLog(value = "patrol_accompany-通过id删除")
	@ApiOperation(value="patrol_accompany-通过id删除", notes="patrol_accompany-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		patrolAccompanyService.removeById(id);
		return Result.OK("删除成功!");
	}*/

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	/*@AutoLog(value = "patrol_accompany-批量删除")
	@ApiOperation(value="patrol_accompany-批量删除", notes="patrol_accompany-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.patrolAccompanyService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}*/

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	/*//@AutoLog(value = "patrol_accompany-通过id查询")
	@ApiOperation(value="patrol_accompany-通过id查询", notes="patrol_accompany-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<PatrolAccompany> queryById(@RequestParam(name="id",required=true) String id) {
		PatrolAccompany patrolAccompany = patrolAccompanyService.getById(id);
		if(patrolAccompany==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(patrolAccompany);
	}*/

    /**
    * 导出excel
    *
    * @param request
    * @param patrolAccompany
    */
   /* @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, PatrolAccompany patrolAccompany) {
        return super.exportXls(request, patrolAccompany, PatrolAccompany.class, "patrol_accompany");
    }*/

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    /*@RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, PatrolAccompany.class);
    }
*/
}
