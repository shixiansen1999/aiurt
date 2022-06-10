package com.aiurt.boot.modules.fault.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.aiurt.boot.common.api.vo.Result;
import com.aiurt.boot.common.aspect.annotation.AutoLog;
import com.aiurt.boot.common.result.OperationProcessResult;
import com.aiurt.boot.common.system.query.QueryGenerator;
import com.aiurt.boot.common.util.oConvertUtils;
import com.aiurt.boot.modules.fault.entity.OperationProcess;
import com.aiurt.boot.modules.fault.service.IOperationProcessService;
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
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
* @Description: 运转流程
* @Author: qian
* @Date:   2021-09-27
* @Version: V1.0
*/
@Slf4j
@Api(tags="运转流程")
@RestController
@RequestMapping("/fault/operationProcess")
public class OperationProcessController {
   @Autowired
   private IOperationProcessService operationProcessService;

   /**
     * 分页列表查询
    * @param operationProcess
    * @param pageNo
    * @param pageSize
    * @param req
    * @return
    */
   @AutoLog(value = "运转流程-分页列表查询")
   @ApiOperation(value="运转流程-分页列表查询", notes="运转流程-分页列表查询")
   @GetMapping(value = "/list")
   public Result<IPage<OperationProcess>> queryPageList(OperationProcess operationProcess,
                                     @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                     @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
                                     HttpServletRequest req) {
       Result<IPage<OperationProcess>> result = new Result<IPage<OperationProcess>>();
       QueryWrapper<OperationProcess> queryWrapper = QueryGenerator.initQueryWrapper(operationProcess, req.getParameterMap());
       Page<OperationProcess> page = new Page<OperationProcess>(pageNo, pageSize);
       IPage<OperationProcess> pageList = operationProcessService.page(page, queryWrapper);
       result.setSuccess(true);
       result.setResult(pageList);
       return result;
   }

   /**
     *   添加
    * @param operationProcess
    * @return
    */
   @AutoLog(value = "运转流程-添加")
   @ApiOperation(value="运转流程-添加", notes="运转流程-添加")
   @PostMapping(value = "/add")
   public Result<OperationProcess> add(@RequestBody OperationProcess operationProcess) {
       Result<OperationProcess> result = new Result<OperationProcess>();
       try {
           operationProcessService.save(operationProcess);
           result.success("添加成功！");
       } catch (Exception e) {
           log.error(e.getMessage(),e);
           result.error500("操作失败");
       }
       return result;
   }

   /**
     *  编辑
    * @param operationProcess
    * @return
    */
   @AutoLog(value = "运转流程-编辑")
   @ApiOperation(value="运转流程-编辑", notes="运转流程-编辑")
   @PutMapping(value = "/edit")
   public Result<OperationProcess> edit(@RequestBody OperationProcess operationProcess) {
       Result<OperationProcess> result = new Result<OperationProcess>();
       OperationProcess operationProcessEntity = operationProcessService.getById(operationProcess.getId());
       if(operationProcessEntity==null) {
           result.onnull("未找到对应实体");
       }else {
           boolean ok = operationProcessService.updateById(operationProcess);

           if(ok) {
               result.success("修改成功!");
           }
       }

       return result;
   }

   /**
     *   通过id删除
    * @param id
    * @return
    */
   @AutoLog(value = "运转流程-通过id删除")
   @ApiOperation(value="运转流程-通过id删除", notes="运转流程-通过id删除")
   @DeleteMapping(value = "/delete")
   public Result<?> delete(@RequestParam(name="id",required=true) String id) {
       try {
           operationProcessService.removeById(id);
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
   @AutoLog(value = "运转流程-批量删除")
   @ApiOperation(value="运转流程-批量删除", notes="运转流程-批量删除")
   @DeleteMapping(value = "/deleteBatch")
   public Result<OperationProcess> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
       Result<OperationProcess> result = new Result<OperationProcess>();
       if(ids==null || "".equals(ids.trim())) {
           result.error500("参数不识别！");
       }else {
           this.operationProcessService.removeByIds(Arrays.asList(ids.split(",")));
           result.success("删除成功!");
       }
       return result;
   }

   /**
     * 通过id查询
    * @param id
    * @return
    */
   @AutoLog(value = "运转流程-通过id查询")
   @ApiOperation(value="运转流程-通过id查询", notes="运转流程-通过id查询")
   @GetMapping(value = "/queryById")
   public Result<OperationProcess> queryById(@RequestParam(name="id",required=true) String id) {
       Result<OperationProcess> result = new Result<OperationProcess>();
       OperationProcess operationProcess = operationProcessService.getById(id);
       if(operationProcess==null) {
           result.onnull("未找到对应实体");
       }else {
           result.setResult(operationProcess);
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
     QueryWrapper<OperationProcess> queryWrapper = null;
     try {
         String paramsStr = request.getParameter("paramsStr");
         if (oConvertUtils.isNotEmpty(paramsStr)) {
             String deString = URLDecoder.decode(paramsStr, "UTF-8");
             OperationProcess operationProcess = JSON.parseObject(deString, OperationProcess.class);
             queryWrapper = QueryGenerator.initQueryWrapper(operationProcess, request.getParameterMap());
         }
     } catch (UnsupportedEncodingException e) {
         e.printStackTrace();
     }

     //Step.2 AutoPoi 导出Excel
     ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
     List<OperationProcess> pageList = operationProcessService.list(queryWrapper);
     //导出文件名称
     mv.addObject(NormalExcelConstants.FILE_NAME, "运转流程列表");
     mv.addObject(NormalExcelConstants.CLASS, OperationProcess.class);
     mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("运转流程列表数据", "导出人:Jeecg", "导出信息"));
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
         // 获取上传文件对象
         MultipartFile file = entity.getValue();
         ImportParams params = new ImportParams();
         params.setTitleRows(2);
         params.setHeadRows(1);
         params.setNeedSave(true);
         try {
             List<OperationProcess> listOperationProcesss = ExcelImportUtil.importExcel(file.getInputStream(), OperationProcess.class, params);
             operationProcessService.saveBatch(listOperationProcesss);
             return Result.ok("文件导入成功！数据行数:" + listOperationProcesss.size());
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
     * 根据故障编号查询运转流程
     * @param code
     * @return
     */
 @AutoLog(value = "根据故障编号查询运转流程")
 @ApiOperation(value="根据故障编号查询运转流程", notes="根据故障编号查询运转流程")
 @GetMapping(value = "/getOperationProcess")
 public Result<List<OperationProcessResult>> getOperationProcess(@RequestParam(name="code",required=true) String code) {
     Result<List<OperationProcessResult>> result = new Result<List<OperationProcessResult>>();
     List<OperationProcessResult> process = operationProcessService.getOperationProcess(code);
     result.setResult(process);
     return result;
 }

}
