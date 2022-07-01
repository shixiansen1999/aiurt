package com.aiurt.boot.standard.controller;

import cn.hutool.core.lang.tree.Tree;
import com.aiurt.boot.standard.entity.PatrolStandardItems;
import com.aiurt.boot.standard.mapper.PatrolStandardItemsMapper;
import com.aiurt.boot.standard.service.IPatrolStandardItemsService;
import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.system.base.controller.BaseController;
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
 * @Description: patrol_standard_items
 * @Author: aiurt
 * @Date:   2022-06-21
 * @Version: V1.0
 */
@Api(tags="巡检标准项目")
@RestController
@RequestMapping("/patrolStandardItems")
@Slf4j
public class PatrolStandardItemsController extends BaseController<PatrolStandardItems, IPatrolStandardItemsService> {
	@Autowired
	private IPatrolStandardItemsService patrolStandardItemsService;
	@Autowired
	private PatrolStandardItemsMapper patrolStandardItemsMapper;
	/**
	 * 分页列表查询
	 *
	 * @param patrolStandardItems
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "patrol_standard_items-分页列表查询")
	@ApiOperation(value="patrol_standard_items-分页列表查询", notes="patrol_standard_items-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<PatrolStandardItems>> queryPageList(PatrolStandardItems patrolStandardItems,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<PatrolStandardItems> queryWrapper = QueryGenerator.initQueryWrapper(patrolStandardItems, req.getParameterMap());
		Page<PatrolStandardItems> page = new Page<PatrolStandardItems>(pageNo, pageSize);
		IPage<PatrolStandardItems> pageList = patrolStandardItemsService.page(page, queryWrapper);
		return Result.OK(pageList);
	}

	 /**
	  * 查询配置巡检项树
	  *
	  * @return
	  */
	 @AutoLog(value = "巡检标准项目表-查询配置巡检项树")
	 @ApiOperation(value = "巡检标准项目表-查询配置巡检项树", notes = "巡检标准项目表-查询配置巡检项树")
	 @GetMapping(value = "/rootList")
	 public Result<List<PatrolStandardItems>> queryPageList( @RequestParam(name="standardId") String id) {
         return Result.OK( patrolStandardItemsService.queryPageList(id));
	 }
	/**
	 *   添加
	 *
	 * @param patrolStandardItems
	 * @return
	 */
	@AutoLog(value = "巡检标准项目表-添加")
	@ApiOperation(value="巡检标准项目表-添加", notes="巡检标准项目表-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody PatrolStandardItems patrolStandardItems) {
		patrolStandardItemsMapper.insert(patrolStandardItems);
		return Result.OK("添加成功！");
	}

	 /**
	  * 校验添加内容排序
	  * @return
	  */
	 @AutoLog(value = "校验添加内容排序")
	 @ApiOperation(value = "校验添加内容排序", notes = "校验添加内容排序")
	 @GetMapping(value = "/check")
	 public Result<?> check(@RequestParam(name="order") Integer order,
							@RequestParam(name="parentId") String parentId,
							@RequestParam(name = "standardId")String standardId) {
	 	Boolean  b = patrolStandardItemsService.check(order,parentId,standardId);
	 	if (b){
			return Result.OK("成功");
		}
		 return Result.error("重复排序,请重新输入");
	 }
	 /**
	  * 巡检标准项目表-app查询巡检工单检查项
	  * @return
	  */
	 @AutoLog(value = "巡检标准项目表-app查询巡检工单检查项")
	 @ApiOperation(value = "巡检标准项目表-app查询巡检工单检查项", notes = "巡检标准项目表-app查询巡检工单检查项")
	 @GetMapping(value = "/taskPoolList")
	 public Result<?> taskPoolList(String id) {
	 	List<Tree<String>> patrolStandardItems = patrolStandardItemsService.getTaskPoolList(id);
		 return Result.OK(patrolStandardItems);
	 }
	/**
	 *  编辑
	 *
	 * @param patrolStandardItems
	 * @return
	 */
	@AutoLog(value = "巡检标准项目表-编辑")
	@ApiOperation(value="巡检标准项目表-编辑", notes="巡检标准项目表-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody PatrolStandardItems patrolStandardItems) {
		patrolStandardItemsService.updateById(patrolStandardItems);
		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "巡检标准项目表-通过id删除")
	@ApiOperation(value="巡检标准项目表-通过id删除", notes="巡检标准项目表-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		PatrolStandardItems patrolStandardItems = new PatrolStandardItems();
		patrolStandardItems.setId(id); patrolStandardItems.setDelFlag(1);
		PatrolStandardItems patrolStandardItem = patrolStandardItemsService.getById(id);
		if ("0".equals(patrolStandardItem.getParentId())){
			patrolStandardItemsMapper.updatPId(id);
		}
		patrolStandardItemsService.updateById(patrolStandardItems);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "巡检标准项目表-批量删除")
	@ApiOperation(value="巡检标准项目表-批量删除", notes="巡检标准项目表-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		List<String> id = Arrays.asList(ids.split(","));
		for (String id1 :id){
			this.delete(id1);
		}
		return Result.OK("批量删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "巡检标准项目表-通过id查询")
	@ApiOperation(value="巡检标准项目表-通过id查询", notes="巡检标准项目表-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<PatrolStandardItems> queryById(@RequestParam(name="id",required=true) String id) {
		PatrolStandardItems patrolStandardItems = patrolStandardItemsService.getById(id);
		if(patrolStandardItems==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(patrolStandardItems);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param patrolStandardItems
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, PatrolStandardItems patrolStandardItems) {
        return super.exportXls(request, patrolStandardItems, PatrolStandardItems.class, "patrol_standard_items");
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
        return super.importExcel(request, response, PatrolStandardItems.class);
    }

}
