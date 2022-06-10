package com.aiurt.boot.modules.fault.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.swsc.copsms.common.api.vo.Result;
import com.swsc.copsms.common.aspect.annotation.AutoLog;
import com.swsc.copsms.common.enums.FaultStatusEnum;
import com.swsc.copsms.common.result.FaultRepairRecordResult;
import com.swsc.copsms.common.result.FaultResult;
import com.swsc.copsms.common.system.query.QueryGenerator;
import com.swsc.copsms.common.util.oConvertUtils;
import com.swsc.copsms.modules.fault.dto.FaultDTO;
import com.swsc.copsms.modules.fault.entity.Fault;
import com.swsc.copsms.modules.fault.param.FaultParam;
import com.swsc.copsms.modules.fault.service.IFaultService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.auth.In;
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
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
* @Description: 故障表
* @Author: swsc
* @Date:   2021-09-14
* @Version: V1.0
*/
@Slf4j
@Api(tags="故障表")
@RestController
@RequestMapping("/fault/fault")
public class FaultController {
   @Autowired
   private IFaultService faultService;

   /**
     * 分页列表查询   故障列表
    * @pa
    * @param req
    * @return
    */
   @AutoLog(value = "故障表-分页列表查询")
   @ApiOperation(value="故障表-分页列表查询", notes="故障表-分页列表查询")
   @GetMapping(value = "/list")
   public Result<IPage<FaultResult>> queryPageList(FaultResult fault,
                                             @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                             @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
                                             @Valid FaultParam param,
                                             HttpServletRequest req) {
       Result<IPage<FaultResult>> result = new Result<IPage<FaultResult>>();
       QueryWrapper<FaultResult> queryWrapper = QueryGenerator.initQueryWrapper(fault, req.getParameterMap());
       Page<FaultResult> page = new Page<>(pageNo, pageSize);
       IPage<FaultResult> pageList = faultService.pageList(page, queryWrapper,param);
       List<FaultResult> records = pageList.getRecords();
       for (FaultResult record : records) {
            record.setStatusDesc(FaultStatusEnum.findMessage(record.getStatus()));
       }

       result.setSuccess(true);
       result.setResult(pageList);
       return result;
   }

   /**
     *   添加  故障登记
    * @param fault
    * @return
    */
   @AutoLog(value = "故障表-添加")
   @ApiOperation(value="故障表-添加", notes="故障表-添加")
   @PostMapping(value = "/add")
   public Result<Fault> add(@RequestBody FaultDTO fault, HttpServletRequest req) {
       Result<Fault> result = new Result<Fault>();
       try {
           faultService.add(fault,req);
           result.success("添加成功！");
       } catch (Exception e) {
           log.error(e.getMessage(),e);
           result.error500(e.getMessage());
       }
       return result;
   }

   /**
     *  编辑
    * @param fault
    * @return
    */
   @AutoLog(value = "故障表-编辑")
   @ApiOperation(value="故障表-编辑", notes="故障表-编辑")
   @PutMapping(value = "/edit")
   public Result<Fault> edit(@RequestBody Fault fault) {
       Result<Fault> result = new Result<Fault>();
       Fault faultEntity = faultService.getById(fault.getId());
       if(faultEntity==null) {
           result.error500("未找到对应实体");
       }else {
           boolean ok = faultService.updateById(fault);
           //TODO 返回false说明什么？
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
   @AutoLog(value = "故障表-通过id删除")
   @ApiOperation(value="故障表-通过id删除", notes="故障表-通过id删除")
   @DeleteMapping(value = "/delete")
   public Result<?> delete(@RequestParam(name="id",required=true) String id) {
       try {
           faultService.removeById(id);
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
   @AutoLog(value = "故障表-批量删除")
   @ApiOperation(value="故障表-批量删除", notes="故障表-批量删除")
   @DeleteMapping(value = "/deleteBatch")
   public Result<Fault> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
       Result<Fault> result = new Result<Fault>();
       if(ids==null || "".equals(ids.trim())) {
           result.error500("参数不识别！");
       }else {
           this.faultService.removeByIds(Arrays.asList(ids.split(",")));
           result.success("删除成功!");
       }
       return result;
   }

   /**
     * 通过id查询
    * @param id
    * @return
    */
   @AutoLog(value = "故障表-通过id查询")
   @ApiOperation(value="故障表-通过id查询", notes="故障表-通过id查询")
   @GetMapping(value = "/queryById")
   public Result<Fault> queryById(@RequestParam(name="id",required=true) String id) {
       Result<Fault> result = new Result<Fault>();
       Fault fault = faultService.getById(id);
       if(fault==null) {
           result.error500("未找到对应实体");
       }else {
           result.setResult(fault);
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
 @GetMapping(value = "/exportXls")
 public ModelAndView exportXls(HttpServletRequest request, HttpServletResponse response) {
     // Step.1 组装查询条件
     QueryWrapper<Fault> queryWrapper = null;
     try {
         String paramsStr = request.getParameter("paramsStr");
         if (oConvertUtils.isNotEmpty(paramsStr)) {
             String deString = URLDecoder.decode(paramsStr, "UTF-8");
             Fault fault = JSON.parseObject(deString, Fault.class);
             queryWrapper = QueryGenerator.initQueryWrapper(fault, request.getParameterMap());
         }
     } catch (UnsupportedEncodingException e) {
         e.printStackTrace();
     }

     //Step.2 AutoPoi 导出Excel
     ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
     List<Fault> pageList = faultService.list(queryWrapper);
     //导出文件名称
     mv.addObject(NormalExcelConstants.FILE_NAME, "故障表列表");
     mv.addObject(NormalExcelConstants.CLASS, Fault.class);
     mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("故障表列表数据", "导出人:Jeecg", "导出信息"));
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
             List<Fault> listFaults = ExcelImportUtil.importExcel(file.getInputStream(), Fault.class, params);
             faultService.saveBatch(listFaults);
             return Result.ok("文件导入成功！数据行数:" + listFaults.size());
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
     * 挂起  挂起后不可以指派
     * @param id
     * @param remark
     * @return
     */
    @GetMapping("/hang")
    public Result hang(@RequestParam(name = "id",required = true) Integer id,@RequestParam(name = "remark",required = true) String remark) {
       faultService.hang(id,remark);
       return Result.ok();
    }

    /**
     * 取消挂起  取消挂起后可以指派
     * @param id
     * @return
     */
    @GetMapping("cancelHang")
    public Result cancelHang(@RequestParam(name = "id",required = true) Integer id) {
    faultService.cancelHang(id);
    return Result.ok();
    }
}
