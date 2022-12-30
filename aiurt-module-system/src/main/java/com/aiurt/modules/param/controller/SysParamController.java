package com.aiurt.modules.param.controller;

import cn.hutool.core.collection.CollUtil;
import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.system.base.controller.BaseController;
import com.aiurt.common.util.oConvertUtils;
import com.aiurt.modules.param.entity.SysParam;
import com.aiurt.modules.param.service.ISysParamService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;

 /**
 * @Description: sys_param
 * @Author: aiurt
 * @Date:   2022-12-30
 * @Version: V1.0
 */
@Api(tags="sys_param")
@RestController
@RequestMapping("/sysParam/sysParam")
@Slf4j
public class SysParamController extends BaseController<SysParam, ISysParamService>{
	@Autowired
	private ISysParamService sysParamService;

	/**
	 * 分页列表查询
	 *
	 * @param sysParam
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "sys_param-分页列表查询")
	@ApiOperation(value="sys_param-分页列表查询", notes="sys_param-分页列表查询")
	@GetMapping(value = "/rootList")
	public Result<IPage<SysParam>> queryPageList(SysParam sysParam,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		String hasQuery = req.getParameter("hasQuery");
        if(hasQuery != null && "true".equals(hasQuery)){
            QueryWrapper<SysParam> queryWrapper =  QueryGenerator.initQueryWrapper(sysParam, req.getParameterMap());
            List<SysParam> list = sysParamService.queryTreeListNoPage(queryWrapper);
            IPage<SysParam> pageList = new Page<>(1, 10, list.size());
            pageList.setRecords(list);
            return Result.OK(pageList);
        }else{
            String parentId = sysParam.getPid();
            if (oConvertUtils.isEmpty(parentId)) {
                parentId = "0";
            }
            sysParam.setPid(null);
            QueryWrapper<SysParam> queryWrapper = QueryGenerator.initQueryWrapper(sysParam, req.getParameterMap());
            // 使用 eq 防止模糊查询
            queryWrapper.eq("pid", parentId);
            Page<SysParam> page = new Page<SysParam>(pageNo, pageSize);
            IPage<SysParam> pageList = sysParamService.page(page, queryWrapper);
			List<SysParam> records = pageList.getRecords();
			if (CollUtil.isNotEmpty(records)) {
				for (SysParam record : records) {
					sysParamService.getCategoryName(record);
				}
			}
			return Result.OK(pageList);
        }
	}


	 /**
      * 获取子数据
      * @param sysParam
      * @param req
      * @return
      */
	//@AutoLog(value = "sys_param-获取子数据")
	@ApiOperation(value="sys_param-获取子数据", notes="sys_param-获取子数据")
	@GetMapping(value = "/childList")
	public Result<IPage<SysParam>> queryPageList(SysParam sysParam,HttpServletRequest req) {
		QueryWrapper<SysParam> queryWrapper = QueryGenerator.initQueryWrapper(sysParam, req.getParameterMap());
		List<SysParam> list = sysParamService.list(queryWrapper);
		IPage<SysParam> pageList = new Page<>(1, 10, list.size());
        pageList.setRecords(list);
		return Result.OK(pageList);
	}

    /**
      * 批量查询子节点
      * @param parentIds 父ID（多个采用半角逗号分割）
      * @return 返回 IPage
      * @param parentIds
      * @return
      */
	//@AutoLog(value = "sys_param-批量获取子数据")
    @ApiOperation(value="sys_param-批量获取子数据", notes="sys_param-批量获取子数据")
    @GetMapping("/getChildListBatch")
    public Result getChildListBatch(@RequestParam("parentIds") String parentIds) {
        try {
            QueryWrapper<SysParam> queryWrapper = new QueryWrapper<>();
            List<String> parentIdList = Arrays.asList(parentIds.split(","));
            queryWrapper.in("pid", parentIdList);
            List<SysParam> list = sysParamService.list(queryWrapper);
            IPage<SysParam> pageList = new Page<>(1, 10, list.size());
            pageList.setRecords(list);
            return Result.OK(pageList);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Result.error("批量查询子节点失败：" + e.getMessage());
        }
    }

	/**
	 *   添加
	 *
	 * @param sysParam
	 * @return
	 */
	@AutoLog(value = "sys_param-添加")
	@ApiOperation(value="sys_param-添加", notes="sys_param-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody SysParam sysParam) {
		sysParamService.addSysParam(sysParam);
		return Result.OK("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param sysParam
	 * @return
	 */
	@AutoLog(value = "sys_param-编辑")
	@ApiOperation(value="sys_param-编辑", notes="sys_param-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody SysParam sysParam) {
		sysParamService.updateSysParam(sysParam);
		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "sys_param-通过id删除")
	@ApiOperation(value="sys_param-通过id删除", notes="sys_param-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		sysParamService.deleteSysParam(id);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "sys_param-批量删除")
	@ApiOperation(value="sys_param-批量删除", notes="sys_param-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		List<String> list = Arrays.asList(ids.split(","));
		for (String id : list) {
			sysParamService.deleteSysParam(id);
		}
		return Result.OK("批量删除成功！");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "sys_param-通过id查询")
	@ApiOperation(value="sys_param-通过id查询", notes="sys_param-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<SysParam> queryById(@RequestParam(name="id",required=true) String id) {
		SysParam sysParam = sysParamService.getById(id);
		if(sysParam==null) {
			return Result.error("未找到对应数据");
		}
		sysParamService.getCategoryName(sysParam);
		return Result.OK(sysParam);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param sysParam
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, SysParam sysParam) {
		return super.exportXls(request, sysParam, SysParam.class, "sys_param");
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
		return super.importExcel(request, response, SysParam.class);
    }

}
