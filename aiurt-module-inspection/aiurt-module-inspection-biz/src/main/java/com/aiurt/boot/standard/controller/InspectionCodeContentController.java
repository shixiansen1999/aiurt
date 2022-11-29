package com.aiurt.boot.standard.controller;


import com.aiurt.boot.constant.InspectionConstant;
import com.aiurt.boot.standard.entity.InspectionCodeContent;
import com.aiurt.boot.standard.mapper.InspectionCodeContentMapper;
import com.aiurt.boot.standard.service.IInspectionCodeContentService;
import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.constant.enums.ModuleType;
import com.aiurt.common.system.base.controller.BaseController;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;

 /**
 * @Description: inspection_code_content
 * @Author: aiurt
 * @Date:   2022-06-21
 * @Version: V1.0
 */
@Api(tags="检修项")
@RestController
@RequestMapping("/standard/inspectionCodeContent")
@Slf4j
public class InspectionCodeContentController extends BaseController<InspectionCodeContent, IInspectionCodeContentService>{
	@Autowired
	private IInspectionCodeContentService inspectionCodeContentService;
	 @Autowired
	 private InspectionCodeContentMapper inspectionCodeContentMapper;

//	/**
//	 * 分页列表查询
//	 *
//	 * @param inspectionCodeContent
//	 * @param pageNo
//	 * @param pageSize
//	 * @param req
//	 * @return
//	 */
//	@AutoLog(value = "inspection_code_content-分页列表查询")
//	@ApiOperation(value="inspection_code_content-分页列表查询", notes="inspection_code_content-分页列表查询")
//	@GetMapping(value = "/rootList")
//	public Result<IPage<InspectionCodeContent>> queryPageList(InspectionCodeContent inspectionCodeContent,
//								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
//								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
//								   HttpServletRequest req) {
//		String hasQuery = req.getParameter("hasQuery");
//        if(hasQuery != null && "true".equals(hasQuery)){
//            QueryWrapper<InspectionCodeContent> queryWrapper =  QueryGenerator.initQueryWrapper(inspectionCodeContent, req.getParameterMap());
//            List<InspectionCodeContent> list = inspectionCodeContentService.queryTreeListNoPage(queryWrapper);
//            IPage<InspectionCodeContent> pageList = new Page<>(1, 10, list.size());
//            pageList.setRecords(list);
//            return Result.OK(pageList);
//        }else{
//            String parentId = inspectionCodeContent.getPid();
//            if (oConvertUtils.isEmpty(parentId)) {
//                parentId = "0";
//            }
//            inspectionCodeContent.setPid(null);
//            QueryWrapper<InspectionCodeContent> queryWrapper = QueryGenerator.initQueryWrapper(inspectionCodeContent, req.getParameterMap());
//            // 使用 eq 防止模糊查询
//            queryWrapper.eq("pid", parentId);
//            Page<InspectionCodeContent> page = new Page<InspectionCodeContent>(pageNo, pageSize);
//            IPage<InspectionCodeContent> pageList = inspectionCodeContentService.page(page, queryWrapper);
//            return Result.OK(pageList);
//        }
//	}
	 /**
	  * 分页列表查询
	  *
	  * @param inspectionCodeContent
	  * @param pageNo
	  * @param pageSize
	  * @param req
	  * @return
	  */
	 @AutoLog(value = "检修检查项表-树型分页列表查询", operateType =  1, operateTypeAlias = "树型分页列表查询", module = ModuleType.INSPECTION)
	 @ApiOperation(value="检修检查项表-树型分页列表查询", notes="检修检查项表-树型分页列表查询")
	 @GetMapping(value = "/list")
	 public Result<IPage<InspectionCodeContent>> queryList(   InspectionCodeContent inspectionCodeContent,
															   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
															   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
															   HttpServletRequest req) {
		 Page<InspectionCodeContent> page = new Page<InspectionCodeContent>(pageNo, pageSize);
		 IPage<InspectionCodeContent> pageList = inspectionCodeContentService.pageList(page, inspectionCodeContent);
		 return Result.OK(pageList);
	 }
	/**
	 *   添加
	 *
	 * @param inspectionCodeContent
	 * @return
	 */
	@AutoLog(value = "inspection_code_content-添加", operateType =  2, operateTypeAlias = "添加", module = ModuleType.INSPECTION)
	@ApiOperation(value="inspection_code_content-添加", notes="inspection_code_content-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody InspectionCodeContent inspectionCodeContent) {
		inspectionCodeContent.setDelFlag(0);
		inspectionCodeContentMapper.insert(inspectionCodeContent);
		return Result.OK("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param inspectionCodeContent
	 * @return
	 */
	@AutoLog(value = "inspection_code_content-编辑", operateType =  3, operateTypeAlias = "编辑", module = ModuleType.INSPECTION)
	@ApiOperation(value="inspection_code_content-编辑", notes="inspection_code_content-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody InspectionCodeContent inspectionCodeContent) {
		inspectionCodeContentService.updateInspectionCodeContent(inspectionCodeContent);
		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "inspection_code_content-通过id删除", operateType =  4, operateTypeAlias = "通过id删除", module = ModuleType.INSPECTION)
	@ApiOperation(value="inspection_code_content-通过id删除", notes="inspection_code_content-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		InspectionCodeContent inspectionCodeContent = new InspectionCodeContent();
		inspectionCodeContent.setId(id); inspectionCodeContent.setDelFlag(1);
		InspectionCodeContent ins = inspectionCodeContentService.getById(id);
		if (InspectionConstant.TREE_ROOT_0.equals(ins.getPid())){
			inspectionCodeContentMapper.updatePid(id);
		}
		inspectionCodeContentService.updateById(inspectionCodeContent);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "inspection_code_content-批量删除", operateType =  4, operateTypeAlias = "批量删除", module = ModuleType.INSPECTION)
	@ApiOperation(value="inspection_code_content-批量删除", notes="inspection_code_content-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		List<String> id = Arrays.asList(ids.split(","));
		for (String id1 :id){
			this.delete(id1);
		}
		return Result.OK("批量删除成功！");
	}
	 /**
	  * 校验添加内容排序
	  * @return
	  */
	 @AutoLog(value = "校验添加code唯一",operateType =  1, operateTypeAlias = "查询code唯一", module = ModuleType.PATROL)
	 @ApiOperation(value = "校验添加code唯一", notes = "校验添加code唯一")
	 @GetMapping(value = "/checkCode")
	 public void checkCode(
			 @RequestParam(name="code") String code,
			 @RequestParam(name = "inspectionCodeId")String inspectionCodeId,
			 @RequestParam(name = "id",required = false)String id) {
		 inspectionCodeContentService.checkCode(code,inspectionCodeId,id);

	 }
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "inspection_code_content-通过id查询", operateType =  1, operateTypeAlias = "通过id查询", module = ModuleType.INSPECTION)
	@ApiOperation(value="inspection_code_content-通过id查询", notes="inspection_code_content-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<InspectionCodeContent> queryById(@RequestParam(name="id",required=true) String id) {
		InspectionCodeContent inspectionCodeContent = inspectionCodeContentService.getById(id);
		if(inspectionCodeContent==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(inspectionCodeContent);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param inspectionCodeContent
    */
	/**
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, InspectionCodeContent inspectionCodeContent) {
		return super.exportXls(request, inspectionCodeContent, InspectionCodeContent.class, "inspection_code_content");
    }
	*/

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
	/**
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
		return super.importExcel(request, response, InspectionCodeContent.class);
    }
	 */

	 /**
	  * 通过检修标准id查看检修项
	  *
	  * @param id  检修标准id
	  * @return
	  */
	 @AutoLog(value = "通过检修标准id查看检修项", operateType =  1, operateTypeAlias = "通过检修标准id查看检修项", module = ModuleType.INSPECTION)
	 @ApiOperation(value = "通过检修标准id查看检修项", notes = "通过检修标准id查看检修项")
	 @GetMapping(value = "/selectCodeContentList")
	 public Result<List<InspectionCodeContent>> selectCodeContentList(@RequestParam @ApiParam(name = "id", required = true, value = "检修标准id") String id) {
		 List<InspectionCodeContent> selectCodeContentList = inspectionCodeContentService.selectCodeContentList(id);
		 return Result.OK(selectCodeContentList);
	 }


	 @AutoLog(value = "配置检修项-导出excel",  operateType =  4, operateTypeAlias = "导出excel", module = ModuleType.INSPECTION)
	 @ApiOperation(value="配置检修项-导出excel", notes="配置检修项-导出excel")
	 @RequestMapping(value = "/exportXls",method = RequestMethod.GET)
	 public ModelAndView exportXls(HttpServletRequest request,HttpServletResponse response, InspectionCodeContent inspectionCodeContent) {
		 return inspectionCodeContentService.exportXls(request,response,inspectionCodeContent);
	 }


	 @AutoLog(value = "配置检修项模板下载", operateType =  4, operateTypeAlias = "导出excel", module = ModuleType.INSPECTION)
	 @ApiOperation(value="配置检修项导入模板下载", notes="配置检修项导入模板下载")
	 @RequestMapping(value = "/exportTemplateXls",method = RequestMethod.GET)
	 public ModelAndView exportTemplateXl() {
//        String remark = "检修标准导入模板\n" +
//                "填写须知：\n" +
//                "1.请勿增加、删除、或修改表格中的字段顺序、字段名称；\n" +
//                "2.请严格按照数据规范填写，并填写完所有必填项，红底白字列为必填项；\n" +
//                "字段说明：\n" +
//                "1.厂商名称：必填字段；\n" +
//                "2.厂商等级：必填字段，且与系统下拉项保持一致；\n" +
//                "3.联系电话：选填字段，11位数的手机号码；\n" +
//                "4.企业资质文件：支持PNG、JP图片格式；pdf请在系统中直接上传；";
		 return super.exportTemplateXls("", InspectionCodeContent.class,"检配置检修项导入模板","");
	 }

	 @AutoLog(value = "配置检查项-通过excel导入数据", operateType =  6, operateTypeAlias = "通过excel导入数据", module = ModuleType.INSPECTION)
	 @ApiOperation(value="配置检查项-通过excel导入数据", notes="配置检查项-通过excel导入数据")
	 @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
	 public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) throws Exception{
		 return inspectionCodeContentService.importExcel(request,response);
	 }

}
