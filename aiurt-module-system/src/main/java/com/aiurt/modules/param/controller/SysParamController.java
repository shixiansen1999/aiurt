package com.aiurt.modules.param.controller;

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
import org.jeecg.common.system.vo.SelectTreeModel;
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
            return Result.OK(pageList);
        }
	}

	 /**
	  * 【vue3专用】加载节点的子数据
	  *
	  * @param pid
	  * @return
	  */
	 @RequestMapping(value = "/loadTreeChildren", method = RequestMethod.GET)
	 public Result<List<SelectTreeModel>> loadTreeChildren(@RequestParam(name = "pid") String pid) {
		 Result<List<SelectTreeModel>> result = new Result<>();
		 try {
			 List<SelectTreeModel> ls = sysParamService.queryListByPid(pid);
			 result.setResult(ls);
			 result.setSuccess(true);
		 } catch (Exception e) {
			 e.printStackTrace();
			 result.setMessage(e.getMessage());
			 result.setSuccess(false);
		 }
		 return result;
	 }

	 /**
	  * 【vue3专用】加载一级节点/如果是同步 则所有数据
	  *
	  * @param async
	  * @param pcode
	  * @return
	  */
	 @RequestMapping(value = "/loadTreeRoot", method = RequestMethod.GET)
	 public Result<List<SelectTreeModel>> loadTreeRoot(@RequestParam(name = "async") Boolean async, @RequestParam(name = "pcode") String pcode) {
		 Result<List<SelectTreeModel>> result = new Result<>();
		 try {
			 List<SelectTreeModel> ls = sysParamService.queryListByCode(pcode);
			 if (!async) {
				 loadAllChildren(ls);
			 }
			 result.setResult(ls);
			 result.setSuccess(true);
		 } catch (Exception e) {
			 e.printStackTrace();
			 result.setMessage(e.getMessage());
			 result.setSuccess(false);
		 }
		 return result;
	 }

	 /**
	  * 【vue3专用】递归求子节点 同步加载用到
	  *
	  * @param ls
	  */
	 private void loadAllChildren(List<SelectTreeModel> ls) {
		 for (SelectTreeModel tsm : ls) {
			 List<SelectTreeModel> temp = sysParamService.queryListByPid(tsm.getKey());
			 if (temp != null && temp.size() > 0) {
				 tsm.setChildren(temp);
				 loadAllChildren(temp);
			 }
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
		this.sysParamService.removeByIds(Arrays.asList(ids.split(",")));
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
