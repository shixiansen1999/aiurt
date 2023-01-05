package com.aiurt.modules.param.controller;

import cn.hutool.core.collection.CollUtil;
import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.system.base.controller.BaseController;
import com.aiurt.modules.param.entity.SysParam;
import com.aiurt.modules.param.service.ISysParamService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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
@Api(tags="实施配置")
@RestController
@RequestMapping("/sysParam/sysParam")
@Slf4j
public class SysParamController extends BaseController<SysParam, ISysParamService>{
	@Autowired
	private ISysParamService sysParamService;



	 /**
      * 查询列表
      * @param sysParam
      * @return
      */
	@ApiOperation(value="实施配置-查询列表", notes="实施配置-查询列表")
	@GetMapping(value = "/rootList")
	public Result<IPage<SysParam>> queryPageList(@RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
												 @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
												 SysParam sysParam) {
		Result<IPage<SysParam>> result = sysParamService.queryPageList(sysParam, pageNo, pageSize);
		return result;
	}

    /**
      * 批量查询子节点
      * @param parentIds 父ID（多个采用半角逗号分割）
      * @return 返回 IPage
      * @param parentIds
      * @return
      */
    @ApiOperation(value="实施配置-批量获取子数据", notes="实施配置-批量获取子数据")
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
	@AutoLog(value = "实施配置-添加")
	@ApiOperation(value="实施配置-添加", notes="实施配置-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody SysParam sysParam) {
		return  sysParamService.addSysParam(sysParam);
	}

	/**
	 *  编辑
	 *
	 * @param sysParam
	 * @return
	 */
	@AutoLog(value = "实施配置-编辑")
	@ApiOperation(value="实施配置-编辑", notes="实施配置-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody SysParam sysParam) {
		return sysParamService.updateSysParam(sysParam);

	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "实施配置-通过id删除")
	@ApiOperation(value="实施配置-通过id删除", notes="实施配置-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		return  sysParamService.deleteSysParam(id);
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "实施配置-批量删除")
	@ApiOperation(value="实施配置-批量删除", notes="实施配置-批量删除")
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
	@ApiOperation(value="实施配置-通过id查询", notes="实施配置-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<SysParam> queryById(@RequestParam(name="id",required=true) String id) {
		SysParam sysParam = sysParamService.getById(id);
		if(sysParam==null) {
			return Result.error("未找到对应数据");
		}
		sysParamService.getCategoryName(sysParam);
		return Result.OK(sysParam);
	}

	 @ApiOperation(value="实施配置—查询所有配置项", notes="实施配置—查询所有配置项")
	 @GetMapping(value = "/configItemList")
	 public Result<List<SysParam>> configItemList() {
		 String configItem = "configItem";
		 LambdaQueryWrapper<SysParam> queryWrapper = new LambdaQueryWrapper<>();
		 queryWrapper.eq(SysParam::getDelFlag, 0);
		 queryWrapper.eq(SysParam::getCategory, configItem);
		 List<SysParam> sysParams = sysParamService.getBaseMapper().selectList(queryWrapper);
		 if (CollUtil.isNotEmpty(sysParams)) {
			 for (SysParam record : sysParams) {
				 sysParamService.getCategoryName(record);
			 }
		 }
		 return Result.OK(sysParams);
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
