package com.aiurt.boot.modules.fault.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.aiurt.common.aspect.annotation.AutoLog;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.aiurt.boot.common.exception.SwscException;
import com.aiurt.boot.common.result.FaultKnowledgeBaseResult;
import com.aiurt.boot.common.system.api.ISysBaseAPI;
import com.aiurt.boot.common.system.query.QueryGenerator;
import com.aiurt.boot.common.util.TokenUtils;
import com.aiurt.boot.common.util.oConvertUtils;
import com.aiurt.boot.modules.fault.dto.FaultKnowledgeBaseDTO;
import com.aiurt.boot.modules.fault.dto.FaultKnowledgeBaseInputDTO;
import com.aiurt.boot.modules.fault.entity.FaultKnowledgeBase;
import com.aiurt.boot.modules.fault.entity.FaultKnowledgeBaseType;
import com.aiurt.boot.modules.fault.param.FaultKnowledgeBaseParam;
import com.aiurt.boot.modules.fault.service.IFaultKnowledgeBaseService;
import com.aiurt.boot.modules.fault.service.IFaultKnowledgeBaseTypeService;
import com.aiurt.boot.modules.manage.entity.CommonFault;
import com.aiurt.boot.modules.manage.entity.Subsystem;
import com.aiurt.boot.modules.manage.mapper.SubsystemMapper;
import com.aiurt.boot.modules.manage.service.ICommonFaultService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
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

   @Resource
   private SubsystemMapper subsystemMapper;

    @Resource
    private ISysBaseAPI iSysBaseAPI;

    @Resource
    private IFaultKnowledgeBaseTypeService knowledgeBaseTypeService;

    @Autowired
    private ICommonFaultService commonFaultService;

    /**
     * 分页列表查询
     * @param pageNo
     * @param pageSize
     * @param param
     * @return
     */
   @AutoLog(value = "故障知识库-分页列表查询")
   @ApiOperation(value="故障知识库-分页列表查询", notes="故障知识库-分页列表查询")
   @GetMapping(value = "/list")
   public Result<IPage<FaultKnowledgeBaseResult>> queryPageList(@RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                                                @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
                                                                FaultKnowledgeBaseParam param) {
       Result<IPage<FaultKnowledgeBaseResult>> result = new Result<IPage<FaultKnowledgeBaseResult>>();
       Page<FaultKnowledgeBaseResult> page = new Page<FaultKnowledgeBaseResult>(pageNo, pageSize);
       IPage<FaultKnowledgeBaseResult> pageList = faultKnowledgeBaseService.pageList(page,param);
       result.setSuccess(true);
       result.setResult(pageList);
       return result;
   }

    @AutoLog(value = "故障知识库-查询详情")
    @ApiOperation(value="故障知识库-查询详情", notes="故障知识库-查询详情")
    @GetMapping(value = "/queryDetail")
    public Result<FaultKnowledgeBaseResult> queryDetail(@RequestParam(name = "id") Long id) {
        Result<FaultKnowledgeBaseResult> result = new Result<FaultKnowledgeBaseResult>();
        FaultKnowledgeBaseResult detail = faultKnowledgeBaseService.queryDetail(id);
        result.setSuccess(true);
        result.setResult(detail);
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
   public Result<FaultKnowledgeBase> add(@Valid @RequestBody FaultKnowledgeBaseDTO dto, HttpServletRequest req) {
       Result<FaultKnowledgeBase> result = new Result<FaultKnowledgeBase>();
       try {
           Long knowledgeId = faultKnowledgeBaseService.add(dto, req);
           /**
            * 关联常见数据与故障知识库
            */
           CommonFault commonFault = new CommonFault();
           commonFault.setFault(dto.getFaultPhenomenon());
           Subsystem subsystem = subsystemMapper.selectByCode(dto.getSystemCode());
           commonFault.setSubId(Long.valueOf(subsystem.getId()));
           commonFault.setKnowledgeId(knowledgeId);
           commonFaultService.save(commonFault);
           result.success("添加成功！");
       } catch (Exception e) {
           log.error(e.getMessage(),e);
           result.error500("添加失败："+e.getMessage());
       }
       return result;
   }

   /**
     *  编辑
    * @param dto
    * @return
    */
    @AutoLog(value = "故障知识库-编辑")
    @ApiOperation(value="故障知识库-编辑", notes="故障知识库-编辑")
    @PostMapping(value = "/edit")
    public Result<?> edit(@RequestBody FaultKnowledgeBaseDTO dto, HttpServletRequest req) {
        try {
            faultKnowledgeBaseService.updateByKnowledgeId(dto,req);
        } catch (Exception e) {
            log.error("修改成功",e.getMessage());
            return Result.error("修改失败!");
        }
        return Result.ok("修改成功!");
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
           faultKnowledgeBaseService.removeById(id);
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
           result.onnull("未找到对应实体");
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
 @ApiOperation(value="通过excel导入数据", notes="")
 @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
 public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
     MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
     Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
     for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
         // 获取上传文件对象
         MultipartFile file = entity.getValue();
         ImportParams params = new ImportParams();
         params.setTitleRows(0);
         params.setHeadRows(1);
         params.setNeedSave(true);
         try {
             List<FaultKnowledgeBaseInputDTO> list = ExcelImportUtil.importExcel(file.getInputStream(),FaultKnowledgeBaseInputDTO.class, params);
             if(CollUtil.isEmpty(list)){
                 return Result.error("Excel转换异常");
             }
             String userId = TokenUtils.getUserId(request, iSysBaseAPI);
             FaultKnowledgeBase base = new FaultKnowledgeBase();
             for (FaultKnowledgeBaseInputDTO dto : list) {
                 if (StringUtils.isBlank(dto.getFaultPhenomenon())) {
                     throw new SwscException("故障现象不能为空");
                 }
                 if (StringUtils.isBlank(dto.getFaultReason())) {
                     throw new SwscException("故障原因不能为空");
                 }
                 if (StringUtils.isBlank(dto.getSolution())) {
                     throw new SwscException("故障措施不能为空");
                 }
                 if (StringUtils.isBlank(dto.getFaultCodes())) {
                     throw new SwscException("关联故障不能为空");
                 }
                 if (StringUtils.isNotBlank(dto.getSystemName()) && StringUtils.isNotBlank(dto.getFaultKnowledgeType())) {
                     dto.setSystemCode(subsystemMapper.selectByName(dto.getSystemName()).getSystemCode());
                     FaultKnowledgeBaseType one = knowledgeBaseTypeService.getOne(new QueryWrapper<FaultKnowledgeBaseType>().eq(FaultKnowledgeBaseType.SYSTEM_CODE, dto.getSystemCode()).eq(FaultKnowledgeBaseType.NAME, dto.getFaultKnowledgeType()), false);
                     if (ObjectUtil.isNotEmpty(one)) {
                         int i = (one.getId()).intValue();
                         base.setTypeId(i);
                     }else {
                         FaultKnowledgeBaseType baseType = new FaultKnowledgeBaseType();
                         baseType.setDelFlag(0);
                         baseType.setName(dto.getFaultKnowledgeType());
                         baseType.setCreateBy(userId);
                         baseType.setSystemCode(dto.getSystemCode());
                         knowledgeBaseTypeService.save(baseType);
                         int i = (baseType.getId()).intValue();
                         base.setTypeId(i);
                     }
                 }
                 dto.setDelFlag(0);
                 dto.setCreateBy(userId);
                 BeanUtils.copyProperties(dto,base);
                 faultKnowledgeBaseService.save(base);
             }
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
     return Result.ok("文件导入成功！");
 }

    /**
     * 查询关联故障
     * @param id
     * @return
     */
 @AutoLog(value = "查询关联故障")
 @ApiOperation(value="查询关联故障", notes="查询关联故障")
 @GetMapping(value = "/getAssociateFault")
 public Result<?> getAssociateFault(@RequestParam(name="id",required=true) Long id) {
     Result<?> associateFault = faultKnowledgeBaseService.getAssociateFault(id);
     return associateFault;
 }

    /**
     * 编辑关联故障
     * @param id
     * @param faultCodes
     * @return
     */
 @AutoLog(value = "编辑关联故障")
 @ApiOperation(value="编辑关联故障", notes="编辑关联故障")
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

    /**
     * 下载故障知识库导入模板
     *
     * @param response
     * @param request
     * @throws IOException
     */
    @AutoLog(value = "故障知识库导入模板")
    @ApiOperation(value = "下载故障知识库导入模板", notes = "下载故障知识库导入模板")
    @RequestMapping(value = "/downloadExcel", method = RequestMethod.GET)
    public void downloadExcel(HttpServletResponse response, HttpServletRequest request) throws IOException {
        ClassPathResource classPathResource =  new ClassPathResource("template/FaultKnowledgeBase.xlsx");
        InputStream bis = classPathResource.getInputStream();
        BufferedOutputStream out = new BufferedOutputStream(response.getOutputStream());
        int len = 0;
        while ((len = bis.read()) != -1) {
            out.write(len);
            out.flush();
        }
        out.close();
    }

    @GetMapping("/getById")
    public Result<FaultKnowledgeBaseResult>getById(@RequestParam(value = "id" , required = false)String id){
        Result<FaultKnowledgeBaseResult> result = new Result<>();
        if (StringUtils.isBlank(id)){
            result.setMessage("当前故障没有解决方案");
            return result.ok(new FaultKnowledgeBaseResult());
        }
        FaultKnowledgeBaseResult knowledgeBaseResult =faultKnowledgeBaseService.getResultById(id);
        return result.ok(knowledgeBaseResult);
    }
}
