package com.aiurt.boot.modules.fault.controller;

import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.common.result.FaultDeviceChangSpareResult;
import com.aiurt.common.util.oConvertUtils;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.aiurt.boot.modules.fault.entity.DeviceChangeSparePart;
import com.aiurt.boot.modules.fault.entity.FaultRepairRecord;
import com.aiurt.boot.modules.fault.param.FaultDeviceParam;
import com.aiurt.boot.modules.fault.service.IFaultChangeSparePartService;
import com.aiurt.boot.modules.fault.service.IFaultRepairRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
* @Description: 故障更换备件表
* @Author: swsc
* @Date:   2021-09-14
* @Version: V1.0
*/
@Slf4j
@Api(tags="故障更换备件表")
@RestController
@RequestMapping("/fault/faultChangeSparePart")
public class DeviceChangeSparePartController {
   @Resource
   private IFaultChangeSparePartService faultChangeSparePartService;

   @Resource
   private IFaultRepairRecordService faultRepairRecordService;

   /**
     * 分页列表查询
    * @param faultChangeSparePart
    * @param pageNo
    * @param pageSize
    * @param req
    * @return
    */
   @AutoLog(value = "故障更换备件表-分页列表查询")
   @ApiOperation(value="故障更换备件表-分页列表查询", notes="故障更换备件表-分页列表查询")
   @GetMapping(value = "/list")
   public Result<IPage<DeviceChangeSparePart>> queryPageList(DeviceChangeSparePart faultChangeSparePart,
                                                             @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                                             @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
                                                             HttpServletRequest req) {
       Result<IPage<DeviceChangeSparePart>> result = new Result<IPage<DeviceChangeSparePart>>();
       QueryWrapper<DeviceChangeSparePart> queryWrapper = QueryGenerator.initQueryWrapper(faultChangeSparePart, req.getParameterMap());
       Page<DeviceChangeSparePart> page = new Page<DeviceChangeSparePart>(pageNo, pageSize);
       IPage<DeviceChangeSparePart> pageList = faultChangeSparePartService.page(page, queryWrapper);
       result.setSuccess(true);
       result.setResult(pageList);
       return result;
   }

   /**
     *   添加
    * @param faultChangeSparePart
    * @return
    */
   @AutoLog(value = "故障更换备件表-添加")
   @ApiOperation(value="故障更换备件表-添加", notes="故障更换备件表-添加")
   @PostMapping(value = "/add")
   public Result<DeviceChangeSparePart> add(@RequestBody DeviceChangeSparePart faultChangeSparePart) {
       Result<DeviceChangeSparePart> result = new Result<DeviceChangeSparePart>();
       try {
           faultChangeSparePartService.save(faultChangeSparePart);
           result.success("添加成功！");
       } catch (Exception e) {
           log.error(e.getMessage(),e);
           result.error500("操作失败");
       }
       return result;
   }

   /**
     *  编辑
    * @param faultChangeSparePart
    * @return
    */
   @AutoLog(value = "故障更换备件表-编辑")
   @ApiOperation(value="故障更换备件表-编辑", notes="故障更换备件表-编辑")
   @PutMapping(value = "/edit")
   public Result<DeviceChangeSparePart> edit(@RequestBody DeviceChangeSparePart faultChangeSparePart) {
       Result<DeviceChangeSparePart> result = new Result<DeviceChangeSparePart>();
       DeviceChangeSparePart faultChangeSparePartEntity = faultChangeSparePartService.getById(faultChangeSparePart.getId());
       if(faultChangeSparePartEntity==null) {
           result.onnull("未找到对应实体");
       }else {
           boolean ok = faultChangeSparePartService.updateById(faultChangeSparePart);
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
   @AutoLog(value = "故障更换备件表-通过id删除")
   @ApiOperation(value="故障更换备件表-通过id删除", notes="故障更换备件表-通过id删除")
   @DeleteMapping(value = "/delete")
   public Result<?> delete(@RequestParam(name="id",required=true) String id) {
       try {
           faultChangeSparePartService.removeById(id);
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
   @AutoLog(value = "故障更换备件表-批量删除")
   @ApiOperation(value="故障更换备件表-批量删除", notes="故障更换备件表-批量删除")
   @DeleteMapping(value = "/deleteBatch")
   public Result<DeviceChangeSparePart> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
       Result<DeviceChangeSparePart> result = new Result<DeviceChangeSparePart>();
       if(ids==null || "".equals(ids.trim())) {
           result.error500("参数不识别！");
       }else {
           this.faultChangeSparePartService.removeByIds(Arrays.asList(ids.split(",")));
           result.success("删除成功!");
       }
       return result;
   }

   /**
     * 通过id查询
    * @param id
    * @return
    */
   @AutoLog(value = "故障更换备件表-通过id查询")
   @ApiOperation(value="故障更换备件表-通过id查询", notes="故障更换备件表-通过id查询")
   @GetMapping(value = "/queryById")
   public Result<DeviceChangeSparePart> queryById(@RequestParam(name="id",required=true) String id) {
       Result<DeviceChangeSparePart> result = new Result<DeviceChangeSparePart>();
       DeviceChangeSparePart faultChangeSparePart = faultChangeSparePartService.getById(id);
       if(faultChangeSparePart==null) {
           result.onnull("未找到对应实体");
       }else {
           result.setResult(faultChangeSparePart);
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
     QueryWrapper<DeviceChangeSparePart> queryWrapper = null;
     try {
         String paramsStr = request.getParameter("paramsStr");
         if (oConvertUtils.isNotEmpty(paramsStr)) {
             String deString = URLDecoder.decode(paramsStr, "UTF-8");
             DeviceChangeSparePart faultChangeSparePart = JSON.parseObject(deString, DeviceChangeSparePart.class);
             queryWrapper = QueryGenerator.initQueryWrapper(faultChangeSparePart, request.getParameterMap());
         }
     } catch (UnsupportedEncodingException e) {
         e.printStackTrace();
     }

     //Step.2 AutoPoi 导出Excel
     ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
     List<DeviceChangeSparePart> pageList = faultChangeSparePartService.list(queryWrapper);
     //导出文件名称
     mv.addObject(NormalExcelConstants.FILE_NAME, "故障更换备件表列表");
     mv.addObject(NormalExcelConstants.CLASS, DeviceChangeSparePart.class);
     mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("故障更换备件表列表数据", "导出人:Jeecg", "导出信息"));
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
             List<DeviceChangeSparePart> listFaultChangeSpareParts = ExcelImportUtil.importExcel(file.getInputStream(), DeviceChangeSparePart.class, params);
             faultChangeSparePartService.saveBatch(listFaultChangeSpareParts);
             return Result.ok("文件导入成功！数据行数:" + listFaultChangeSpareParts.size());
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
  * 根据设备编号查询更换备件记录
  * @param code
  * @param pageNo
  * @param pageSize
  * @param param
  * @return
  */
 @AutoLog(value = "根据设备编号查询更换备件记录")
 @ApiOperation(value = "根据设备编号查询更换备件记录", notes = "根据设备编号查询故障备件记录")
 @GetMapping("/getFaultDeviceChangeSpare")
public Result<IPage<FaultDeviceChangSpareResult>> getFaultDeviceChangeSpare(@RequestParam(name = "code", required = true) String code,
                                                                            @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                                            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                                            FaultDeviceParam param) {
     Page<FaultDeviceChangSpareResult> page = new Page<>(pageNo, pageSize);
     IPage<FaultDeviceChangSpareResult> faultDeviceChangeSpare = faultChangeSparePartService.getFaultDeviceChangeSpare(page,code,param);
     return Result.ok(faultDeviceChangeSpare);
}

    /**
     * 根据维修记录id查询详情
     * @param id
     * @return
     */
    @AutoLog(value = "根据维修记录id查询详情")
    @ApiOperation(value = "根据维修记录id查询详情", notes = "根据维修记录id查询详情")
    @GetMapping("/getReportById")
    public Result<String> getFaultReportById(@RequestParam(name = "id", required = false) String id,
                                             @RequestParam(name = "type", required = false) String type) {
        FaultRepairRecord faultRepairRecord = faultRepairRecordService.getOne(new QueryWrapper<FaultRepairRecord>().eq("id", id), false);
        if (StringUtils.isBlank(faultRepairRecord.getMaintenanceMeasures())) {
            throw new AiurtBootException("该记录没有详情");
        } else {
            String faultPhenomenon = faultRepairRecord.getMaintenanceMeasures();
            return Result.ok(faultPhenomenon);
        }
    }
}
