package com.aiurt.modules.faultlevel.controller;

import java.util.Arrays;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.aiurt.common.system.base.controller.BaseController;
import com.aiurt.modules.fault.entity.Fault;
import com.aiurt.modules.fault.service.IFaultService;
import com.aiurt.modules.faultlevel.service.IFaultLevelService;
import com.aiurt.modules.faulttype.entity.FaultType;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import com.aiurt.modules.faultlevel.entity.FaultLevel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import com.aiurt.common.aspect.annotation.AutoLog;

 /**
 * @Description: 故障等级
 * @Author: aiurt
 * @Date:   2022-06-24
 * @Version: V1.0
 */
@Api(tags="故障管理-故障基础数据-故障等级")
@RestController
@RequestMapping("/faultlevel/faultLevel")
@Slf4j
public class FaultLevelController extends BaseController<FaultLevel, IFaultLevelService> {
	@Autowired
	private IFaultLevelService faultLevelService;
	 @Autowired
	 private IFaultService faultService;

	/**
	 * 分页列表查询
	 *
	 * @param faultLevel
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "故障等级-分页列表查询")
	@ApiOperation(value="故障等级-分页列表查询", notes="故障等级-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<FaultLevel>> queryPageList(FaultLevel faultLevel,
												   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
												   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
												   HttpServletRequest req) {
		QueryWrapper<FaultLevel> queryWrapper = QueryGenerator.initQueryWrapper(faultLevel, req.getParameterMap());
		Page<FaultLevel> page = new Page<FaultLevel>(pageNo, pageSize);
		IPage<FaultLevel> pageList = faultLevelService.page(page, queryWrapper.lambda().eq(FaultLevel::getDelFlag,0));
		return Result.OK(pageList);
	}

	/**
	 *   添加
	 *
	 * @param faultLevel
	 * @return
	 */
	@AutoLog(value = "故障等级-添加")
	@ApiOperation(value="故障等级-添加", notes="故障等级-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody FaultLevel faultLevel) {
		return faultLevelService.add(faultLevel);
	}

	/**
	 *  编辑
	 *
	 * @param faultLevel
	 * @return
	 */
	@AutoLog(value = "故障等级-编辑")
	@ApiOperation(value="故障等级-编辑", notes="故障等级-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<?> edit(@RequestBody FaultLevel faultLevel) {
		return faultLevelService.update(faultLevel);
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "故障等级-通过id删除")
	@ApiOperation(value="故障等级-通过id删除", notes="故障等级-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		FaultLevel faultLevel = faultLevelService.getById(id);
		//判断故障上报是否使用 todo
		/*LambdaQueryWrapper<Fault> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.eq(Fault::getFaultTypeCode, faultLevel.getCode());
		List<Fault> list = faultService.list(queryWrapper);
		if (!list.isEmpty()) {
			return Result.error("故障分类编码已被故障报修单使用，不能删除！");
		}*/
		faultLevel.setDelFlag(1);
		faultLevelService.updateById(faultLevel);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	/*@AutoLog(value = "故障等级-批量删除")
	@ApiOperation(value="故障等级-批量删除", notes="故障等级-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.faultLevelService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}*/

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "故障等级-通过id查询")
	@ApiOperation(value="故障等级-通过id查询", notes="故障等级-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<FaultLevel> queryById(@RequestParam(name="id",required=true) String id) {
		FaultLevel faultLevel = faultLevelService.getById(id);
		if(faultLevel==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(faultLevel);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param faultLevel
    */
    /*@RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, FaultLevel faultLevel) {
        return super.exportXls(request, faultLevel, FaultLevel.class, "故障等级");
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
        return super.importExcel(request, response, FaultLevel.class);
    }
*/
}
