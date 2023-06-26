package com.aiurt.modules.system.controller;

import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.system.base.controller.BaseController;
import com.aiurt.modules.system.entity.SysUser;
import com.aiurt.modules.system.entity.SysUserAptitudes;
import com.aiurt.modules.system.service.ISysUserAptitudesService;
import com.aiurt.modules.system.service.ISysUserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @Description: sys_user_aptitudes
 * @Author: aiurt
 * @Date:   2023-06-07
 * @Version: V1.0
 */
@Api(tags="用户资质")
@RestController
@RequestMapping("/system/sysUserAptitudes")
@Slf4j
public class SysUserAptitudesController extends BaseController<SysUserAptitudes, ISysUserAptitudesService> {
	@Autowired
	private ISysUserAptitudesService sysUserAptitudesService;

	 @Autowired
	 private ISysUserService sysUserService;

    /**
     * @return
     */
    @ApiOperation(value = "构造数据", notes = "构造数据")
    @GetMapping(value = "/random")
    public Result<?> data(HttpServletRequest request, HttpServletResponse response) {
        // todo 后期有相应的页面维护数据了删除此方法
        List<SysUser> list = sysUserService.lambdaQuery()
                .eq(SysUser::getDelFlag, CommonConstant.DEL_FLAG_0)
                .list();
        List<SysUserAptitudes> aptitudes = new ArrayList<>();
        SysUserAptitudes userAptitudes = null;
        Random random = new Random();
        for (SysUser user : list) {
            userAptitudes = new SysUserAptitudes();
            userAptitudes.setUserId(user.getId());
            int i = random.nextInt(3) + 1;
            userAptitudes.setLevel(String.valueOf(i));
            userAptitudes.setName("专业技术人员职业资格证");
            userAptitudes.setNumber(1);
            aptitudes.add(userAptitudes);
        }
        sysUserAptitudesService.remove(new QueryWrapper<>());
        sysUserAptitudesService.saveBatch(aptitudes);
        return Result.ok("操作成功！");
    }
//	/**
//	 * 分页列表查询
//	 *
//	 * @param sysUserAptitudes
//	 * @param pageNo
//	 * @param pageSize
//	 * @param req
//	 * @return
//	 */
//	//@AutoLog(value = "sys_user_aptitudes-分页列表查询")
//	@ApiOperation(value="sys_user_aptitudes-分页列表查询", notes="sys_user_aptitudes-分页列表查询")
//	@GetMapping(value = "/list")
//	public Result<IPage<SysUserAptitudes>> queryPageList(SysUserAptitudes sysUserAptitudes,
//								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
//								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
//								   HttpServletRequest req) {
//		QueryWrapper<SysUserAptitudes> queryWrapper = QueryGenerator.initQueryWrapper(sysUserAptitudes, req.getParameterMap());
//		Page<SysUserAptitudes> page = new Page<SysUserAptitudes>(pageNo, pageSize);
//		IPage<SysUserAptitudes> pageList = sysUserAptitudesService.page(page, queryWrapper);
//		return Result.OK(pageList);
//	}
//
//	/**
//	 *   添加
//	 *
//	 * @param sysUserAptitudes
//	 * @return
//	 */
//	@AutoLog(value = "sys_user_aptitudes-添加")
//	@ApiOperation(value="sys_user_aptitudes-添加", notes="sys_user_aptitudes-添加")
//	@PostMapping(value = "/add")
//	public Result<String> add(@RequestBody SysUserAptitudes sysUserAptitudes) {
//		sysUserAptitudesService.save(sysUserAptitudes);
//		return Result.OK("添加成功！");
//	}
//
//	/**
//	 *  编辑
//	 *
//	 * @param sysUserAptitudes
//	 * @return
//	 */
//	@AutoLog(value = "sys_user_aptitudes-编辑")
//	@ApiOperation(value="sys_user_aptitudes-编辑", notes="sys_user_aptitudes-编辑")
//	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
//	public Result<String> edit(@RequestBody SysUserAptitudes sysUserAptitudes) {
//		sysUserAptitudesService.updateById(sysUserAptitudes);
//		return Result.OK("编辑成功!");
//	}
//
//	/**
//	 *   通过id删除
//	 *
//	 * @param id
//	 * @return
//	 */
//	@AutoLog(value = "sys_user_aptitudes-通过id删除")
//	@ApiOperation(value="sys_user_aptitudes-通过id删除", notes="sys_user_aptitudes-通过id删除")
//	@DeleteMapping(value = "/delete")
//	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
//		sysUserAptitudesService.removeById(id);
//		return Result.OK("删除成功!");
//	}
//
//	/**
//	 *  批量删除
//	 *
//	 * @param ids
//	 * @return
//	 */
//	@AutoLog(value = "sys_user_aptitudes-批量删除")
//	@ApiOperation(value="sys_user_aptitudes-批量删除", notes="sys_user_aptitudes-批量删除")
//	@DeleteMapping(value = "/deleteBatch")
//	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
//		this.sysUserAptitudesService.removeByIds(Arrays.asList(ids.split(",")));
//		return Result.OK("批量删除成功!");
//	}
//
//	/**
//	 * 通过id查询
//	 *
//	 * @param id
//	 * @return
//	 */
//	//@AutoLog(value = "sys_user_aptitudes-通过id查询")
//	@ApiOperation(value="sys_user_aptitudes-通过id查询", notes="sys_user_aptitudes-通过id查询")
//	@GetMapping(value = "/queryById")
//	public Result<SysUserAptitudes> queryById(@RequestParam(name="id",required=true) String id) {
//		SysUserAptitudes sysUserAptitudes = sysUserAptitudesService.getById(id);
//		if(sysUserAptitudes==null) {
//			return Result.error("未找到对应数据");
//		}
//		return Result.OK(sysUserAptitudes);
//	}
//
//    /**
//    * 导出excel
//    *
//    * @param request
//    * @param sysUserAptitudes
//    */
//    @RequestMapping(value = "/exportXls")
//    public ModelAndView exportXls(HttpServletRequest request, SysUserAptitudes sysUserAptitudes) {
//        return super.exportXls(request, sysUserAptitudes, SysUserAptitudes.class, "sys_user_aptitudes");
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
//        return super.importExcel(request, response, SysUserAptitudes.class);
//    }

}
