package com.aiurt.modules.online.page.controller;

import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.system.base.controller.BaseController;
import com.aiurt.modules.online.page.entity.ActCustomPageModule;
import com.aiurt.modules.online.page.service.IActCustomPageModuleService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.vo.SelectTreeModel;
import org.jeecg.common.util.oConvertUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

 /**
 * @Description: act_custom_page_module
 * @Author: jeecg-boot
 * @Date:   2023-08-18
 * @Version: V1.0
 */
@Api(tags="静态表单所属模块")
@RestController
@RequestMapping("/pagemodule/actCustomPageModule")
@Slf4j
public class ActCustomPageModuleController extends BaseController<ActCustomPageModule, IActCustomPageModuleService> {
	@Autowired
	private IActCustomPageModuleService actCustomPageModuleService;

	 /**
	  * 加载所有数据
	  *
	  * @param async
	  * @param pcode
	  * @return
	  */
	 @ApiOperation(value = "加载全部节点的数据", notes = "加载全部节点的数据")
	 @RequestMapping(value = "/loadTreeRoot", method = RequestMethod.GET)
	 public Result<List<SelectTreeModel>> loadTreeRoot(@RequestParam(name = "async") Boolean async, @RequestParam(name = "pcode") String pcode) {
		 Result<List<SelectTreeModel>> result = new Result<>();
		 try {
			 List<SelectTreeModel> ls = actCustomPageModuleService.queryListByCode(null);
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
			 List<SelectTreeModel> temp = actCustomPageModuleService.queryListByPid(tsm.getKey());
			 if (temp != null && temp.size() > 0) {
				 tsm.setChildren(temp);
				 loadAllChildren(temp);
			 }
		 }
	 }

	 /**
	  * 获取子数据
	  *
	  * @param actCustomPageModule
	  * @param req
	  * @return
	  */
	@ApiOperation(value = "act_custom_page_module-获取子数据", notes = "act_custom_page_module-获取子数据")
	@GetMapping(value = "/childList")
	public Result<IPage<ActCustomPageModule>> queryPageList(ActCustomPageModule actCustomPageModule,HttpServletRequest req) {
		QueryWrapper<ActCustomPageModule> queryWrapper = QueryGenerator.initQueryWrapper(actCustomPageModule, req.getParameterMap());
		List<ActCustomPageModule> list = actCustomPageModuleService.list(queryWrapper);
		IPage<ActCustomPageModule> pageList = new Page<>(1, 10, list.size());
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
	//@AutoLog(value = "act_custom_page_module-批量获取子数据")
    @ApiOperation(value="act_custom_page_module-批量获取子数据", notes="act_custom_page_module-批量获取子数据")
    @GetMapping("/getChildListBatch")
    public Result getChildListBatch(@RequestParam("parentIds") String parentIds) {
        try {
            QueryWrapper<ActCustomPageModule> queryWrapper = new QueryWrapper<>();
            List<String> parentIdList = Arrays.asList(parentIds.split(","));
            queryWrapper.in("pid", parentIdList);
            List<ActCustomPageModule> list = actCustomPageModuleService.list(queryWrapper);
            IPage<ActCustomPageModule> pageList = new Page<>(1, 10, list.size());
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
	 * @param actCustomPageModule
	 * @return
	 */
	@AutoLog(value = "act_custom_page_module-添加")
	@ApiOperation(value="act_custom_page_module-添加", notes="act_custom_page_module-添加")
    @RequiresPermissions("pagemodule:act_custom_page_module:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody ActCustomPageModule actCustomPageModule) {
		actCustomPageModuleService.addActCustomPageModule(actCustomPageModule);
		return Result.OK("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param actCustomPageModule
	 * @return
	 */
	@AutoLog(value = "act_custom_page_module-编辑")
	@ApiOperation(value="act_custom_page_module-编辑", notes="act_custom_page_module-编辑")
    @RequiresPermissions("pagemodule:act_custom_page_module:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody ActCustomPageModule actCustomPageModule) {
		actCustomPageModuleService.updateActCustomPageModule(actCustomPageModule);
		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "act_custom_page_module-通过id删除")
	@ApiOperation(value="act_custom_page_module-通过id删除", notes="act_custom_page_module-通过id删除")
    @RequiresPermissions("pagemodule:act_custom_page_module:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		actCustomPageModuleService.deleteActCustomPageModule(id);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "act_custom_page_module-批量删除")
	@ApiOperation(value="act_custom_page_module-批量删除", notes="act_custom_page_module-批量删除")
    @RequiresPermissions("pagemodule:act_custom_page_module:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.actCustomPageModuleService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功！");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "act_custom_page_module-通过id查询")
	@ApiOperation(value="act_custom_page_module-通过id查询", notes="act_custom_page_module-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<ActCustomPageModule> queryById(@RequestParam(name="id",required=true) String id) {
		ActCustomPageModule actCustomPageModule = actCustomPageModuleService.getById(id);
		if(actCustomPageModule==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(actCustomPageModule);
	}

}
