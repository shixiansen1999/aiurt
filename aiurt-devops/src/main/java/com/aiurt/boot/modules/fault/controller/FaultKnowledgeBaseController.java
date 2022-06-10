package com.aiurt.boot.modules.fault.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.swsc.copsms.common.api.vo.Result;
import com.swsc.copsms.common.aspect.annotation.AutoLog;
import com.swsc.copsms.common.result.FaultAnalysisReportResult;
import com.swsc.copsms.common.result.FaultCodesResult;
import com.swsc.copsms.common.result.FaultKnowledgeBaseResult;
import com.swsc.copsms.common.system.query.QueryGenerator;
import com.swsc.copsms.common.util.oConvertUtils;
import com.swsc.copsms.modules.fault.dto.FaultKnowledgeBaseDTO;
import com.swsc.copsms.modules.fault.entity.FaultKnowledgeBase;
import com.swsc.copsms.modules.fault.param.FaultKnowledgeBaseParam;
import com.swsc.copsms.modules.fault.service.IFaultKnowledgeBaseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
* @Description: 故障知识库
* @Author: swsc
* @Date:   2021-09-14
* @Version: V1.0
*/
@Slf4j
@Api(tags="故障知识库")
@RestController
@RequestMapping("/fault/faultKnowledgeBase")
public class FaultKnowledgeBaseController {
   @Autowired
   private IFaultKnowledgeBaseService faultKnowledgeBaseService;

   /**
     * 分页列表查询
    * @param faultKnowledgeBase
    * @param pageNo
    * @param pageSize
    * @param req
    * @return
    */
   @AutoLog(value = "故障知识库-分页列表查询")
   @ApiOperation(value="故障知识库-分页列表查询", notes="故障知识库-分页列表查询")
   @GetMapping(value = "/list")
   public Result<IPage<FaultKnowledgeBaseResult>> queryPageList(FaultKnowledgeBaseResult faultKnowledgeBase,
                                                                @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                                                @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
                                                                @Valid FaultKnowledgeBaseParam param,
                                                                HttpServletRequest req) {
       Result<IPage<FaultKnowledgeBaseResult>> result = new Result<IPage<FaultKnowledgeBaseResult>>();
       QueryWrapper<FaultKnowledgeBaseResult> queryWrapper = QueryGenerator.initQueryWrapper(faultKnowledgeBase, req.getParameterMap());
       Page<FaultKnowledgeBaseResult> page = new Page<FaultKnowledgeBaseResult>(pageNo, pageSize);
       IPage<FaultKnowledgeBaseResult> pageList = faultKnowledgeBaseService.pageList(page, queryWrapper,param);
       result.setSuccess(true);
       result.setResult(pageList);
       return result;
   }

   /**
     *   添加 故障知识库
    * @param dto
    * @return
    */
   @AutoLog(value = "故障知识库-添加")
   @ApiOperation(value="故障知识库-添加", notes="故障知识库-添加")
   @PostMapping(value = "/add")
   public Result<FaultKnowledgeBase> add(@RequestBody FaultKnowledgeBaseDTO dto, HttpServletRequest req) {
       Result<FaultKnowledgeBase> result = new Result<FaultKnowledgeBase>();
       try {
           faultKnowledgeBaseService.add(dto,req);
           result.success("添加成功！");
       } catch (Exception e) {
           log.error(e.getMessage(),e);
           result.error500(e.getMessage());
       }
       return result;
   }

   /**
     *  编辑
    * @param faultKnowledgeBase
    * @return
    */
   @AutoLog(value = "故障知识库-编辑")
   @ApiOperation(value="故障知识库-编辑", notes="故障知识库-编辑")
   @PutMapping(value = "/edit")
   public Result<FaultKnowledgeBase> edit(@RequestBody FaultKnowledgeBase faultKnowledgeBase) {
       Result<FaultKnowledgeBase> result = new Result<FaultKnowledgeBase>();
       FaultKnowledgeBase faultKnowledgeBaseEntity = faultKnowledgeBaseService.getById(faultKnowledgeBase.getId());
       if(faultKnowledgeBaseEntity==null) {
           result.error500("未找到对应实体");
       }else {
           boolean ok = faultKnowledgeBaseService.updateById(faultKnowledgeBase);
           //TODO 返回false说明什么？
           if(ok) {
               result.success("修改成功!");
           }
       }

       return result;
   }

   /**
     *   通过id假删除
    * @param id
    * @return
    */
   @AutoLog(value = "故障知识库-通过id删除")
   @ApiOperation(value="故障知识库-通过id删除", notes="故障知识库-通过id删除")
   @DeleteMapping(value = "/delete")
   public Result<?> delete(@RequestParam(name="id",required=true) Integer id) {
       try {
           faultKnowledgeBaseService.deleteById(id);
       } catch (Exception e) {
           log.error("删除失败",e.getMessage());
           return Result.error("删除失败!");
       }
       return Result.ok("删除成功!");
   }

   /**
     *  批量删除
    * @param ids
    * @return
    */
   @AutoLog(value = "故障知识库-批量删除")
   @ApiOperation(value="故障知识库-批量删除", notes="故障知识库-批量删除")
   @DeleteMapping(value = "/deleteBatch")
   public Result<FaultKnowledgeBase> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
       Result<FaultKnowledgeBase> result = new Result<FaultKnowledgeBase>();
       if(ids==null || "".equals(ids.trim())) {
           result.error500("参数不识别！");
       }else {
           this.faultKnowledgeBaseService.removeByIds(Arrays.asList(ids.split(",")));
           result.success("删除成功!");
       }
       return result;
   }

   /**
     * 通过id查询
    * @param id
    * @return
    */
   @AutoLog(value = "故障知识库-通过id查询")
   @ApiOperation(value="故障知识库-通过id查询", notes="故障知识库-通过id查询")
   @GetMapping(value = "/queryById")
   public Result<FaultKnowledgeBase> queryById(@RequestParam(name="id",required=true) String id) {
       Result<FaultKnowledgeBase> result = new Result<FaultKnowledgeBase>();
       FaultKnowledgeBase faultKnowledgeBase = faultKnowledgeBaseService.getById(id);
       if(faultKnowledgeBase==null) {
           result.error500("未找到对应实体");
       }else {
           result.setResult(faultKnowledgeBase);
           result.setSuccess(true);
       }
       return result;
   }

 /**
     * 导出excel
  *
  * @param request
  * @param response
  */
 @RequestMapping(value = "/exportXls")
 public ModelAndView exportXls(HttpServletRequest request, HttpServletResponse response) {
     // Step.1 组装查询条件
     QueryWrapper<FaultKnowledgeBase> queryWrapper = null;
     try {
         String paramsStr = request.getParameter("paramsStr");
         if (oConvertUtils.isNotEmpty(paramsStr)) {
             String deString = URLDecoder.decode(paramsStr, "UTF-8");
             FaultKnowledgeBase faultKnowledgeBase = JSON.parseObject(deString, FaultKnowledgeBase.class);
             queryWrapper = QueryGenerator.initQueryWrapper(faultKnowledgeBase, request.getParameterMap());
         }
     } catch (UnsupportedEncodingException e) {
         e.printStackTrace();
     }

     //Step.2 AutoPoi 导出Excel
     ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
     List<FaultKnowledgeBase> pageList = faultKnowledgeBaseService.list(queryWrapper);
     //导出文件名称
     mv.addObject(NormalExcelConstants.FILE_NAME, "故障知识库列表");
     mv.addObject(NormalExcelConstants.CLASS, FaultKnowledgeBase.class);
     mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("故障知识库列表数据", "导出人:Jeecg", "导出信息"));
     mv.addObject(NormalExcelConstants.DATA_LIST, pageList);
     return mv;
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
     MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
     Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
     for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
         MultipartFile file = entity.getValue();// 获取上传文件对象
         ImportParams params = new ImportParams();
         params.setTitleRows(2);
         params.setHeadRows(1);
         params.setNeedSave(true);
         try {
             List<FaultKnowledgeBase> listFaultKnowledgeBases = ExcelImportUtil.importExcel(file.getInputStream(), FaultKnowledgeBase.class, params);
             faultKnowledgeBaseService.saveBatch(listFaultKnowledgeBases);
             return Result.ok("文件导入成功！数据行数:" + listFaultKnowledgeBases.size());
         } catch (Exception e) {
             log.error(e.getMessage(),e);
             return Result.error("文件导入失败:"+e.getMessage());
         } finally {
             try {
                 file.getInputStream().close();
             } catch (IOException e) {
                 e.printStackTrace();
             }
         }
     }
     return Result.ok("文件导入失败！");
 }

    /**
     * 查询关联故障
     * @param id
     * @return
     */
 @GetMapping(value = "/getAssociateFault")
 public Result<?> getAssociateFault(@RequestParam(name="id",required=true) Integer id) {
     Result<?> associateFault = faultKnowledgeBaseService.getAssociateFault(id);
     return associateFault;
 }

    /**
     * 编辑管关联故障  fault/faultKnowledgeBase/associateFaultEdit
     * @param id
     * @param faultCodes
     * @return
     */
 @GetMapping(value = "/associateFaultEdit")
 public Result associateFaultEdit(@RequestParam(name="id",required=true) Integer id,
                                @RequestParam(name="faultCodes",required=true) String faultCodes) {
     try {
         faultKnowledgeBaseService.associateFaultEdit(id,faultCodes);
     }catch (Exception e) {
         log.error(e.getMessage(),e);
         Result.error("修改失败");
     }
     return Result.ok("修改成功");
 }
}
