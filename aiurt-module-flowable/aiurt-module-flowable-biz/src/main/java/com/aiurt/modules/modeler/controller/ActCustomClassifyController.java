package com.aiurt.modules.modeler.controller;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.system.base.controller.BaseController;
import com.aiurt.common.util.oConvertUtils;
import com.aiurt.modules.common.entity.SelectTable;
import com.aiurt.modules.modeler.entity.ActCustomClassify;
import com.aiurt.modules.modeler.service.IActCustomClassifyService;
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

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description: 流程分类
 * @Author: aiurt
 * @Date:   2022-07-21
 * @Version: V1.0
 */
@Api(tags="流程分类")
@RestController
@RequestMapping("/modeler/actCustomClassify")
@Slf4j
public class ActCustomClassifyController extends BaseController<ActCustomClassify, IActCustomClassifyService>{
	@Autowired
	private IActCustomClassifyService actCustomClassifyService;

	/**
	 * 分页列表查询
	 *
	 * @param actCustomClassify
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@ApiOperation(value="流程分类-分页列表查询", notes="流程分类-分页列表查询")
	@GetMapping(value = "/rootList")
	public Result<IPage<ActCustomClassify>> queryPageList(ActCustomClassify actCustomClassify,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		String hasQuery = req.getParameter("hasQuery");
        if(hasQuery != null && "true".equals(hasQuery)){
            QueryWrapper<ActCustomClassify> queryWrapper =  QueryGenerator.initQueryWrapper(actCustomClassify, req.getParameterMap());
            List<ActCustomClassify> list = actCustomClassifyService.queryTreeListNoPage(queryWrapper);
            IPage<ActCustomClassify> pageList = new Page<>(1, 10, list.size());
            pageList.setRecords(list);
            return Result.OK(pageList);
        }else{
            String parentId = actCustomClassify.getPid();
            if (oConvertUtils.isEmpty(parentId)) {
                parentId = "0";
            }
            actCustomClassify.setPid(null);
            QueryWrapper<ActCustomClassify> queryWrapper = QueryGenerator.initQueryWrapper(actCustomClassify, req.getParameterMap());
            // 使用 eq 防止模糊查询
            queryWrapper.eq("pid", parentId);
            Page<ActCustomClassify> page = new Page<ActCustomClassify>(pageNo, pageSize);
            IPage<ActCustomClassify> pageList = actCustomClassifyService.page(page, queryWrapper);
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
			 List<SelectTreeModel> ls = actCustomClassifyService.queryListByPid(pid);
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
			 List<SelectTreeModel> ls = actCustomClassifyService.queryListByCode(pcode);
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
			 List<SelectTreeModel> temp = actCustomClassifyService.queryListByPid(tsm.getKey());
			 if (temp != null && temp.size() > 0) {
				 tsm.setChildren(temp);
				 loadAllChildren(temp);
			 }
		 }
	 }

	 /**
      * 获取子数据
      * @param actCustomClassify
      * @param req
      * @return
      */
	@ApiOperation(value="流程分类-获取子数据", notes="流程分类-获取子数据")
	@GetMapping(value = "/childList")
	public Result<IPage<ActCustomClassify>> queryPageList(ActCustomClassify actCustomClassify,HttpServletRequest req) {
		QueryWrapper<ActCustomClassify> queryWrapper = QueryGenerator.initQueryWrapper(actCustomClassify, req.getParameterMap());
		List<ActCustomClassify> list = actCustomClassifyService.list(queryWrapper);
		IPage<ActCustomClassify> pageList = new Page<>(1, 10, list.size());
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
    @ApiOperation(value="流程分类-批量获取子数据", notes="流程分类-批量获取子数据")
    @GetMapping("/getChildListBatch")
    public Result getChildListBatch(@RequestParam("parentIds") String parentIds) {
        try {
            QueryWrapper<ActCustomClassify> queryWrapper = new QueryWrapper<>();
            List<String> parentIdList = Arrays.asList(parentIds.split(","));
            queryWrapper.in("pid", parentIdList);
            List<ActCustomClassify> list = actCustomClassifyService.list(queryWrapper);
            IPage<ActCustomClassify> pageList = new Page<>(1, 10, list.size());
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
	 * @param actCustomClassify
	 * @return
	 */
	@AutoLog(value = "流程分类-添加")
	@ApiOperation(value="流程分类-添加", notes="流程分类-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody ActCustomClassify actCustomClassify) {
		actCustomClassifyService.addActCustomClassify(actCustomClassify);
		return Result.OK("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param actCustomClassify
	 * @return
	 */
	@AutoLog(value = "流程分类-编辑")
	@ApiOperation(value="流程分类-编辑", notes="流程分类-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody ActCustomClassify actCustomClassify) {
		actCustomClassifyService.updateActCustomClassify(actCustomClassify);
		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "流程分类-通过id删除")
	@ApiOperation(value="流程分类-通过id删除", notes="流程分类-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		actCustomClassifyService.deleteActCustomClassify(id);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "流程分类-批量删除")
	@ApiOperation(value="流程分类-批量删除", notes="流程分类-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.actCustomClassifyService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功！");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@ApiOperation(value="流程分类-通过id查询", notes="流程分类-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<ActCustomClassify> queryById(@RequestParam(name="id",required=true) String id) {
		ActCustomClassify actCustomClassify = actCustomClassifyService.getById(id);
		if(actCustomClassify==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(actCustomClassify);
	}

	@GetMapping("/queryClassifyTree")
	@ApiOperation("查询流程分类树")
	public Result<List<SelectTable>> queryClassifyTree() {
		List<ActCustomClassify> departList = actCustomClassifyService.getBaseMapper().selectList(null);
		List<SelectTable> treeList = departList.stream().map(entity -> {
			SelectTable table = new SelectTable();
			table.setValue(entity.getScode());
			table.setLabel(entity.getSname());
			table.setKey(entity.getId());
			table.setParentValue(StrUtil.isBlank(entity.getPid()) ? "-9999" : entity.getPid());
			return table;
		}).collect(Collectors.toList());

		Map<String, SelectTable> root = new LinkedHashMap<>();
		for (SelectTable item : treeList) {
			SelectTable parent = root.get(item.getParentValue());
			if (Objects.isNull(parent)) {
				parent = new SelectTable();
				root.put(item.getParentValue(), parent);
			}
			SelectTable table = root.get(item.getKey());
			if (Objects.nonNull(table)) {
				item.setChildren(table.getChildren());
			}
			root.put(item.getValue(), item);
			parent.addChildren(item);
		}
		List<SelectTable> resultList = new ArrayList<>();
		List<SelectTable> collect = root.values().stream().filter(entity -> StrUtil.isBlank(entity.getParentValue())).collect(Collectors.toList());
		for (SelectTable entity : collect) {
			resultList.addAll(CollectionUtil.isEmpty(entity.getChildren()) ? Collections.emptyList() : entity.getChildren());
		}
		return Result.OK(resultList);
	}


}
