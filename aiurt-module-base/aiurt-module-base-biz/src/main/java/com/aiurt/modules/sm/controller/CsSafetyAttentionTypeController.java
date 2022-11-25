package com.aiurt.modules.sm.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.aiurt.common.api.vo.TreeNode;
import com.aiurt.common.aspect.annotation.PermissionData;
import com.aiurt.modules.sm.dto.SafetyAttentionTypeTreeDTO;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import com.aiurt.common.util.oConvertUtils;
import org.jeecg.common.system.vo.SelectTreeModel;
import com.aiurt.modules.sm.entity.CsSafetyAttentionType;
import com.aiurt.modules.sm.service.ICsSafetyAttentionTypeService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import com.aiurt.common.system.base.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import com.aiurt.common.aspect.annotation.AutoLog;

 /**
 * @Description: 安全事项类型表
 * @Author: aiurt
 * @Date:   2022-11-17
 * @Version: V1.0
 */
@Api(tags="安全事项类型表")
@RestController
@RequestMapping("/sm/csSafetyAttentionType")
@Slf4j
public class CsSafetyAttentionTypeController extends BaseController<CsSafetyAttentionType, ICsSafetyAttentionTypeService>{
	@Autowired
	private ICsSafetyAttentionTypeService csSafetyAttentionTypeService;

	 /**
	  * 查询数据 查出所有安全事项类型,并以树结构数据格式响应给前端
	  *
	  * @return
	  */
	 @ApiOperation(value="安全事项管理-查询所有安全事项类型", notes="安全事项管理-查询所有安全事项类型")
	 @RequestMapping(value = "/queryTreeList", method = RequestMethod.GET)
	 public Result<List<TreeNode>> queryTreeList() {
		 Result<List<TreeNode>> result = new Result<>();
		 try {
			 List<TreeNode> selectTreeModels = csSafetyAttentionTypeService.queryTreeList();
			 result.setResult(selectTreeModels);
			 result.setSuccess(true);
		 } catch (Exception e) {
			 log.error(e.getMessage(),e);
		 }
		 return result;
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
			 List<SelectTreeModel> ls = csSafetyAttentionTypeService.queryListByPid(pid);
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
			 List<SelectTreeModel> ls = csSafetyAttentionTypeService.queryListByCode(pcode);
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
			 List<SelectTreeModel> temp = csSafetyAttentionTypeService.queryListByPid(tsm.getKey());
			 if (temp != null && temp.size() > 0) {
				 tsm.setChildren(temp);
				 loadAllChildren(temp);
			 }
		 }
	 }

	 /**
      * 获取子数据
      * @param csSafetyAttentionType
      * @param req
      * @return
      */
	//@AutoLog(value = "安全事项类型表-获取子数据")
	@ApiOperation(value="安全事项类型表-获取子数据", notes="安全事项类型表-获取子数据")
	@GetMapping(value = "/childList")
	public Result<IPage<CsSafetyAttentionType>> queryPageList(CsSafetyAttentionType csSafetyAttentionType,HttpServletRequest req) {
		QueryWrapper<CsSafetyAttentionType> queryWrapper = QueryGenerator.initQueryWrapper(csSafetyAttentionType, req.getParameterMap());
		List<CsSafetyAttentionType> list = csSafetyAttentionTypeService.list(queryWrapper);
		IPage<CsSafetyAttentionType> pageList = new Page<>(1, 10, list.size());
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
	//@AutoLog(value = "安全事项类型表-批量获取子数据")
    @ApiOperation(value="安全事项类型表-批量获取子数据", notes="安全事项类型表-批量获取子数据")
    @GetMapping("/getChildListBatch")
    public Result getChildListBatch(@RequestParam("parentIds") String parentIds) {
        try {
            QueryWrapper<CsSafetyAttentionType> queryWrapper = new QueryWrapper<>();
            List<String> parentIdList = Arrays.asList(parentIds.split(","));
            queryWrapper.in("pid", parentIdList);
            List<CsSafetyAttentionType> list = csSafetyAttentionTypeService.list(queryWrapper);
            IPage<CsSafetyAttentionType> pageList = new Page<>(1, 10, list.size());
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
	 * @param csSafetyAttentionType
	 * @return
	 */
	@AutoLog(value = "安全事项类型表-添加")
	@ApiOperation(value="安全事项类型表-添加", notes="安全事项类型表-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody CsSafetyAttentionType csSafetyAttentionType) {
		csSafetyAttentionTypeService.addCsSafetyAttentionType(csSafetyAttentionType);
		return Result.OK("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param csSafetyAttentionType
	 * @return
	 */
	@AutoLog(value = "安全事项类型表-编辑")
	@ApiOperation(value="安全事项类型表-编辑", notes="安全事项类型表-编辑")
	@PostMapping(value = "/edit")
	public Result<String> edit(@RequestBody CsSafetyAttentionType csSafetyAttentionType) {
		csSafetyAttentionTypeService.updateCsSafetyAttentionType(csSafetyAttentionType);
		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "安全事项类型表-通过id删除")
	@ApiOperation(value="安全事项类型表-通过id删除", notes="安全事项类型表-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		csSafetyAttentionTypeService.deleteCsSafetyAttentionType(id);
		return Result.OK("删除成功!");
	}


	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "安全事项类型表-通过id查询")
	@ApiOperation(value="安全事项类型表-通过id查询", notes="安全事项类型表-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<CsSafetyAttentionType> queryById(@RequestParam(name="id",required=true) String id) {
		CsSafetyAttentionType csSafetyAttentionType = csSafetyAttentionTypeService.getById(id);
		if(csSafetyAttentionType==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(csSafetyAttentionType);
	}
	 /**
	  * 根据专业查询所有的节点
	  *
	  * @return
	  */
	 @ApiOperation(value="安全事项管理-根据专业查询所有的节点", notes="安全事项管理-根据专业查询所有的节点")
	 @RequestMapping(value = "/queryTreeByMajorCode", method = RequestMethod.GET)
	 public Result<TreeNode> queryTreeByMajorCode(@RequestParam(name="majorCode",required=true) String majorCode) {
		 Result<TreeNode> result = new Result<>();
		 try {
			 TreeNode selectTreeModels = csSafetyAttentionTypeService.queryTreeByMajorCode(majorCode);
			 result.setResult(selectTreeModels);
			 result.setSuccess(true);
		 } catch (Exception e) {
			 log.error(e.getMessage(),e);
		 }
		 return result;
	 }

}
