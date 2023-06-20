package com.aiurt.modules.system.controller;

import com.aiurt.common.system.base.controller.BaseController;
import com.aiurt.modules.system.entity.SysUserPerf;
import com.aiurt.modules.system.service.ISysUserPerfService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description: sys_user_perf
 * @Author: aiurt
 * @Date: 2023-06-07
 * @Version: V1.0
 */
@Api(tags = "sys_user_perf")
@RestController
@RequestMapping("/system/sysUserPerf")
@Slf4j
public class SysUserPerfController extends BaseController<SysUserPerf, ISysUserPerfService> {
    @Autowired
    private ISysUserPerfService sysUserPerfService;

//	/**
//	 * 分页列表查询
//	 *
//	 * @param sysUserPerf
//	 * @param pageNo
//	 * @param pageSize
//	 * @param req
//	 * @return
//	 */
//	//@AutoLog(value = "sys_user_perf-分页列表查询")
//	@ApiOperation(value="sys_user_perf-分页列表查询", notes="sys_user_perf-分页列表查询")
//	@GetMapping(value = "/list")
//	public Result<IPage<SysUserPerf>> queryPageList(SysUserPerf sysUserPerf,
//								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
//								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
//								   HttpServletRequest req) {
//		QueryWrapper<SysUserPerf> queryWrapper = QueryGenerator.initQueryWrapper(sysUserPerf, req.getParameterMap());
//		Page<SysUserPerf> page = new Page<SysUserPerf>(pageNo, pageSize);
//		IPage<SysUserPerf> pageList = sysUserPerfService.page(page, queryWrapper);
//		return Result.OK(pageList);
//	}
//
//	/**
//	 *   添加
//	 *
//	 * @param sysUserPerf
//	 * @return
//	 */
//	@AutoLog(value = "sys_user_perf-添加")
//	@ApiOperation(value="sys_user_perf-添加", notes="sys_user_perf-添加")
//	@PostMapping(value = "/add")
//	public Result<String> add(@RequestBody SysUserPerf sysUserPerf) {
//		sysUserPerfService.save(sysUserPerf);
//		return Result.OK("添加成功！");
//	}
//
//	/**
//	 *  编辑
//	 *
//	 * @param sysUserPerf
//	 * @return
//	 */
//	@AutoLog(value = "sys_user_perf-编辑")
//	@ApiOperation(value="sys_user_perf-编辑", notes="sys_user_perf-编辑")
//	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
//	public Result<String> edit(@RequestBody SysUserPerf sysUserPerf) {
//		sysUserPerfService.updateById(sysUserPerf);
//		return Result.OK("编辑成功!");
//	}
//
//	/**
//	 *   通过id删除
//	 *
//	 * @param id
//	 * @return
//	 */
//	@AutoLog(value = "sys_user_perf-通过id删除")
//	@ApiOperation(value="sys_user_perf-通过id删除", notes="sys_user_perf-通过id删除")
//	@DeleteMapping(value = "/delete")
//	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
//		sysUserPerfService.removeById(id);
//		return Result.OK("删除成功!");
//	}
//
//	/**
//	 *  批量删除
//	 *
//	 * @param ids
//	 * @return
//	 */
//	@AutoLog(value = "sys_user_perf-批量删除")
//	@ApiOperation(value="sys_user_perf-批量删除", notes="sys_user_perf-批量删除")
//	@DeleteMapping(value = "/deleteBatch")
//	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
//		this.sysUserPerfService.removeByIds(Arrays.asList(ids.split(",")));
//		return Result.OK("批量删除成功!");
//	}
//
//	/**
//	 * 通过id查询
//	 *
//	 * @param id
//	 * @return
//	 */
//	//@AutoLog(value = "sys_user_perf-通过id查询")
//	@ApiOperation(value="sys_user_perf-通过id查询", notes="sys_user_perf-通过id查询")
//	@GetMapping(value = "/queryById")
//	public Result<SysUserPerf> queryById(@RequestParam(name="id",required=true) String id) {
//		SysUserPerf sysUserPerf = sysUserPerfService.getById(id);
//		if(sysUserPerf==null) {
//			return Result.error("未找到对应数据");
//		}
//		return Result.OK(sysUserPerf);
//	}
//
//    /**
//    * 导出excel
//    *
//    * @param request
//    * @param sysUserPerf
//    */
//    @RequestMapping(value = "/exportXls")
//    public ModelAndView exportXls(HttpServletRequest request, SysUserPerf sysUserPerf) {
//        return super.exportXls(request, sysUserPerf, SysUserPerf.class, "sys_user_perf");
//    }
//
//    /**
//      * 通过excel导入数据
//    *
//    * @param request
//    * @param response
//    * @return
//    */
//    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
//    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
//        return super.importExcel(request, response, SysUserPerf.class);
//    }

}
