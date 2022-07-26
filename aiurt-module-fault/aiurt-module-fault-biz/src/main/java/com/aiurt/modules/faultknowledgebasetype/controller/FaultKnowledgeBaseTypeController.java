package com.aiurt.modules.faultknowledgebasetype.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.aiurt.common.aspect.annotation.PermissionData;
import com.aiurt.modules.faultknowledgebase.entity.FaultKnowledgeBase;
import com.aiurt.modules.faultknowledgebase.mapper.FaultKnowledgeBaseMapper;
import com.aiurt.modules.faultknowledgebase.service.IFaultKnowledgeBaseService;
import com.aiurt.modules.faultknowledgebasetype.dto.MajorDTO;
import com.aiurt.modules.faulttype.entity.FaultType;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.commons.collections.CollectionUtils;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import com.aiurt.common.util.oConvertUtils;
import com.aiurt.modules.faultknowledgebasetype.entity.FaultKnowledgeBaseType;
import com.aiurt.modules.faultknowledgebasetype.service.IFaultKnowledgeBaseTypeService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.jeecg.common.system.vo.LoginUser;
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
 * @Description: 故障知识分类
 * @Author: aiurt
 * @Date:   2022-06-24
 * @Version: V1.0
 */
@Api(tags="故障知识分类")
@RestController
@RequestMapping("/faultknowledgebasetype/faultKnowledgeBaseType")
@Slf4j
public class FaultKnowledgeBaseTypeController extends BaseController<FaultKnowledgeBaseType, IFaultKnowledgeBaseTypeService> {
	@Autowired
	private IFaultKnowledgeBaseTypeService faultKnowledgeBaseTypeService;
	 @Autowired
	 private FaultKnowledgeBaseMapper faultKnowledgeBaseMapper;

	/**
	 * 分页列表查询
	 *
	 * @param faultKnowledgeBaseType
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "故障知识分类-分页列表查询")
	@ApiOperation(value="故障知识分类-分页列表查询", notes="故障知识分类-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<FaultKnowledgeBaseType>> queryPageList(FaultKnowledgeBaseType faultKnowledgeBaseType,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<FaultKnowledgeBaseType> queryWrapper = QueryGenerator.initQueryWrapper(faultKnowledgeBaseType, req.getParameterMap());
		Page<FaultKnowledgeBaseType> page = new Page<FaultKnowledgeBaseType>(pageNo, pageSize);
		IPage<FaultKnowledgeBaseType> pageList = faultKnowledgeBaseTypeService.page(page, queryWrapper);
		return Result.OK(pageList);
	}

	/**
	 *   添加
	 *
	 * @param faultKnowledgeBaseType
	 * @return
	 */
	@AutoLog(value = "故障知识分类-添加")
	@ApiOperation(value="故障知识分类-添加", notes="故障知识分类-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody FaultKnowledgeBaseType faultKnowledgeBaseType) {
		faultKnowledgeBaseTypeService.add(faultKnowledgeBaseType);
		return Result.OK("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param faultKnowledgeBaseType
	 * @return
	 */
	@AutoLog(value = "故障知识分类-编辑")
	@ApiOperation(value="故障知识分类-编辑", notes="故障知识分类-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody FaultKnowledgeBaseType faultKnowledgeBaseType) {
		FaultKnowledgeBaseType byId = faultKnowledgeBaseTypeService.getById(faultKnowledgeBaseType.getId());
		LambdaQueryWrapper<FaultKnowledgeBase> queryWrapper = new LambdaQueryWrapper<>();
		List<FaultKnowledgeBase> faultKnowledgeBases = faultKnowledgeBaseMapper.selectList(queryWrapper
				.eq(FaultKnowledgeBase::getKnowledgeBaseTypeCode, byId.getCode()).eq(FaultKnowledgeBase::getDelFlag,0));
		if (CollectionUtils.isEmpty(faultKnowledgeBases)) {
			return Result.OK("该分类已经被使用，不可编辑!");
		}
		faultKnowledgeBaseTypeService.updateById(faultKnowledgeBaseType);
		return Result.OK("编辑成功!");
	}

	 /**
	  *  查询是否被使用（未启用该接口）
	  *
	  * @param id
	  * @return
	  */
	 @AutoLog(value = "故障知识分类-查询是否被使用")
	 @ApiOperation(value="故障知识分类-查询是否被使用", notes="故障知识分类-查询是否被使用")
	 @RequestMapping(value = "/used", method = {RequestMethod.PUT,RequestMethod.POST})
	 public Boolean used(@RequestParam(name="id",required=true) String id) {
		 FaultKnowledgeBaseType byId = faultKnowledgeBaseTypeService.getById(id);
		 LambdaQueryWrapper<FaultKnowledgeBase> queryWrapper = new LambdaQueryWrapper<>();
		 List<FaultKnowledgeBase> faultKnowledgeBases = faultKnowledgeBaseMapper.selectList(queryWrapper
				 .eq(FaultKnowledgeBase::getKnowledgeBaseTypeCode, byId.getCode()).eq(FaultKnowledgeBase::getDelFlag,0));
		 if (CollectionUtils.isEmpty(faultKnowledgeBases)) {
			 return true;
		 }
		 return false;
	 }

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "故障知识分类-通过id删除")
	@ApiOperation(value="故障知识分类-通过id删除", notes="故障知识分类-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		FaultKnowledgeBaseType byId = faultKnowledgeBaseTypeService.getById(id);
		LambdaQueryWrapper<FaultKnowledgeBase> queryWrapper = new LambdaQueryWrapper<>();
		List<FaultKnowledgeBase> faultKnowledgeBases = faultKnowledgeBaseMapper.selectList(queryWrapper
				.eq(FaultKnowledgeBase::getKnowledgeBaseTypeCode,  byId.getCode()).eq(FaultKnowledgeBase::getDelFlag,0));
		if (CollectionUtils.isEmpty(faultKnowledgeBases)) {
			return Result.OK("该分类已经被使用，不可删除!");
		}
		faultKnowledgeBaseTypeService.removeById(id);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "故障知识分类-批量删除")
	@ApiOperation(value="故障知识分类-批量删除", notes="故障知识分类-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.faultKnowledgeBaseTypeService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "故障知识分类-通过id查询")
	@ApiOperation(value="故障知识分类-通过id查询", notes="故障知识分类-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<FaultKnowledgeBaseType> queryById(@RequestParam(name="id",required=true) String id) {
		FaultKnowledgeBaseType faultKnowledgeBaseType = faultKnowledgeBaseTypeService.getById(id);
		if(faultKnowledgeBaseType==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(faultKnowledgeBaseType);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param faultKnowledgeBaseType
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, FaultKnowledgeBaseType faultKnowledgeBaseType) {
        return super.exportXls(request, faultKnowledgeBaseType, FaultKnowledgeBaseType.class, "故障知识分类");
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
        return super.importExcel(request, response, FaultKnowledgeBaseType.class);
    }

	 /**
	  * 知识库类别树
	  *
	  * @return
	  */
	 @ApiOperation(value = "故障知识分类-知识库类别树", notes = "知识库类别树")
	 @GetMapping(value = "/faultKnowledgeBaseTypeTreeList")
	 @ApiResponses({
			 @ApiResponse(code = 200, message = "OK", response = MajorDTO.class)
	 })
	 @PermissionData(pageComponent = "fault/FaultKnowledgeBaseListChange")
	 public Result<List<MajorDTO>> faultKnowledgeBaseTypeTreeList() {
		 List<MajorDTO> list = faultKnowledgeBaseTypeService.faultKnowledgeBaseTypeTreeList();
		 return Result.OK(list);
	 }
}
