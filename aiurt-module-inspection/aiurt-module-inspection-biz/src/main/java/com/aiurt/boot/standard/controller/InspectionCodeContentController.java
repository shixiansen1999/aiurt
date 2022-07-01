package com.aiurt.boot.standard.controller;


import com.aiurt.boot.standard.entity.InspectionCodeContent;
import com.aiurt.boot.standard.mapper.InspectionCodeContentMapper;
import com.aiurt.boot.standard.service.IInspectionCodeContentService;
import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.system.base.controller.BaseController;
import com.aiurt.common.util.oConvertUtils;
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
 * @Description: inspection_code_content
 * @Author: aiurt
 * @Date:   2022-06-21
 * @Version: V1.0
 */
@Api(tags="inspection_code_content")
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
	 @AutoLog(value = "检修检查项表-树型分页列表查询")
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
//	 /**
//	  * 【vue3专用】加载节点的子数据
//	  *
//	  * @param pid
//	  * @return
//	  */
//	 @RequestMapping(value = "/loadTreeChildren", method = RequestMethod.GET)
//	 public Result<List<SelectTreeModel>> loadTreeChildren(@RequestParam(name = "pid") String pid) {
//		 Result<List<SelectTreeModel>> result = new Result<>();
//		 try {
//			 List<SelectTreeModel> ls = inspectionCodeContentService.queryListByPid(pid);
//			 result.setResult(ls);
//			 result.setSuccess(true);
//		 } catch (Exception e) {
//			 e.printStackTrace();
//			 result.setMessage(e.getMessage());
//			 result.setSuccess(false);
//		 }
//		 return result;
//	 }
//
//	 /**
//	  * 【vue3专用】加载一级节点/如果是同步 则所有数据
//	  *
//	  * @param async
//	  * @param pcode
//	  * @return
//	  */
//	 @RequestMapping(value = "/loadTreeRoot", method = RequestMethod.GET)
//	 public Result<List<SelectTreeModel>> loadTreeRoot(@RequestParam(name = "async") Boolean async, @RequestParam(name = "pcode") String pcode) {
//		 Result<List<SelectTreeModel>> result = new Result<>();
//		 try {
//			 List<SelectTreeModel> ls = inspectionCodeContentService.queryListByCode(pcode);
//			 if (!async) {
//				 loadAllChildren(ls);
//			 }
//			 result.setResult(ls);
//			 result.setSuccess(true);
//		 } catch (Exception e) {
//			 e.printStackTrace();
//			 result.setMessage(e.getMessage());
//			 result.setSuccess(false);
//		 }
//		 return result;
//	 }
//
//	 /**
//	  * 【vue3专用】递归求子节点 同步加载用到
//	  *
//	  * @param ls
//	  */
//	 private void loadAllChildren(List<SelectTreeModel> ls) {
//		 for (SelectTreeModel tsm : ls) {
//			 List<SelectTreeModel> temp = inspectionCodeContentService.queryListByPid(tsm.getKey());
//			 if (temp != null && temp.size() > 0) {
//				 tsm.setChildren(temp);
//				 loadAllChildren(temp);
//			 }
//		 }
//	 }
//
//	 /**
//      * 获取子数据
//      * @param inspectionCodeContent
//      * @param req
//      * @return
//      */
//	@AutoLog(value = "inspection_code_content-获取子数据")
//	@ApiOperation(value="inspection_code_content-获取子数据", notes="inspection_code_content-获取子数据")
//	@GetMapping(value = "/childList")
//	public Result<IPage<InspectionCodeContent>> queryPageList(InspectionCodeContent inspectionCodeContent,HttpServletRequest req) {
//		QueryWrapper<InspectionCodeContent> queryWrapper = QueryGenerator.initQueryWrapper(inspectionCodeContent, req.getParameterMap());
//		List<InspectionCodeContent> list = inspectionCodeContentService.list(queryWrapper);
//		IPage<InspectionCodeContent> pageList = new Page<>(1, 10, list.size());
//        pageList.setRecords(list);
//		return Result.OK(pageList);
//	}
//
//    /**
//      * 批量查询子节点
//      * @param parentIds 父ID（多个采用半角逗号分割）
//      * @return 返回 IPage
//      * @param parentIds
//      * @return
//      */
//	@AutoLog(value = "inspection_code_content-批量获取子数据")
//    @ApiOperation(value="inspection_code_content-批量获取子数据", notes="inspection_code_content-批量获取子数据")
//    @GetMapping("/getChildListBatch")
//    public Result getChildListBatch(@RequestParam("parentIds") String parentIds) {
//        try {
//            QueryWrapper<InspectionCodeContent> queryWrapper = new QueryWrapper<>();
//            List<String> parentIdList = Arrays.asList(parentIds.split(","));
//            queryWrapper.in("pid", parentIdList);
//            List<InspectionCodeContent> list = inspectionCodeContentService.list(queryWrapper);
//            IPage<InspectionCodeContent> pageList = new Page<>(1, 10, list.size());
//            pageList.setRecords(list);
//            return Result.OK(pageList);
//        } catch (Exception e) {
//            log.error(e.getMessage(), e);
//            return Result.error("批量查询子节点失败：" + e.getMessage());
//        }
//    }

	/**
	 *   添加
	 *
	 * @param inspectionCodeContent
	 * @return
	 */
	@AutoLog(value = "inspection_code_content-添加")
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
	@AutoLog(value = "inspection_code_content-编辑")
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
	@AutoLog(value = "inspection_code_content-通过id删除")
	@ApiOperation(value="inspection_code_content-通过id删除", notes="inspection_code_content-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		InspectionCodeContent inspectionCodeContent = new InspectionCodeContent();
		inspectionCodeContent.setId(id); inspectionCodeContent.setDelFlag(1);
		InspectionCodeContent ins = inspectionCodeContentService.getById(id);
		if ("0".equals(ins.getPid())){
			inspectionCodeContentMapper.updatPId(id);
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
	@AutoLog(value = "inspection_code_content-批量删除")
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
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "inspection_code_content-通过id查询")
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
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, InspectionCodeContent inspectionCodeContent) {
		return super.exportXls(request, inspectionCodeContent, InspectionCodeContent.class, "inspection_code_content");
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
		return super.importExcel(request, response, InspectionCodeContent.class);
    }

}
