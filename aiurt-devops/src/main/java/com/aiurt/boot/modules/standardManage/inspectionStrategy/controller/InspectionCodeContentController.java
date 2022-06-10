package com.aiurt.boot.modules.standardManage.inspectionStrategy.controller;
import java.time.LocalDate;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.aiurt.copsms.common.api.vo.Result;
import com.aiurt.copsms.common.aspect.annotation.AutoLog;
import com.aiurt.copsms.common.constant.InspectionContant;
import com.aiurt.copsms.common.system.query.QueryGenerator;
import com.aiurt.copsms.common.util.oConvertUtils;
import com.aiurt.copsms.modules.standardManage.inspectionSpecification.entity.InspectionCode;
import com.aiurt.copsms.modules.standardManage.inspectionSpecification.service.IInspectionCodeService;
import com.aiurt.copsms.modules.standardManage.inspectionStrategy.entity.InspectionCodeContent;
import com.aiurt.copsms.modules.standardManage.inspectionStrategy.service.IinspectionCodeContentService;
import com.aiurt.copsms.modules.standardManage.inspectionStrategy.vo.InspectionCodeContentExcelVO;
import com.aiurt.copsms.modules.standardManage.safetyPrecautions.service.IsafetyPrecautionsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.entity.enmus.ExcelType;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Description: 检修策略管理
 * @Author: qian
 * @Date: 2021-09-15
 * @Version: V1.0
 */
@Slf4j
@Api(tags = "检修策略管理")
@RestController
@RequestMapping("/inspectionStrategy/inspectionCodeContent")
public class InspectionCodeContentController {
    @Autowired
    private IinspectionCodeContentService inspectionCodeContentService;

    @Autowired
    private IInspectionCodeService inspectionCodeService;

    @Autowired
    private IsafetyPrecautionsService isafetyPrecautionsService;

//    @Value("${support.downFilePath.inspectionCodeTemplatePath}")
//    private String excelPath;

    /**
     * 分页列表查询
     *
     * @param inspectionCodeContent
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "检修策略管理-分页列表查询")
    @ApiOperation(value = "检修策略管理-分页列表查询", notes = "检修策略管理-分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<InspectionCodeContent>> queryPageList(InspectionCodeContent inspectionCodeContent,
                                                              @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                              @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                              HttpServletRequest req) {
        Result<IPage<InspectionCodeContent>> result = new Result<IPage<InspectionCodeContent>>();
        QueryWrapper<InspectionCodeContent> queryWrapper = QueryGenerator.initQueryWrapper(inspectionCodeContent, req.getParameterMap());
        //排序
        queryWrapper.orderByAsc("sort_no");
        Page<InspectionCodeContent> page = new Page<InspectionCodeContent>(pageNo, pageSize);
        IPage<InspectionCodeContent> pageList = inspectionCodeContentService.page(page, queryWrapper);
        result.setSuccess(true);
        result.setResult(pageList);
        return result;
    }

    /**
     * 添加
     *
     * @param inspectionCodeContent
     * @return
     */
    @AutoLog(value = "检修策略管理-添加")
    @ApiOperation(value = "检修策略管理-添加", notes = "检修策略管理-添加")
    @PostMapping(value = "/add")
    public Result<InspectionCodeContent> add(@RequestBody @Validated InspectionCodeContent inspectionCodeContent) {
        Result<InspectionCodeContent> result = new Result<InspectionCodeContent>();
        try {
            if (inspectionCodeContent.getType().equals(InspectionContant.WEEK)){
                inspectionCodeContent.setTactics(InspectionContant.WEEK);
            }
            final Integer sortNo = inspectionCodeContent.getSortNo();
            final Integer inspectionCodeId = inspectionCodeContent.getInspectionCodeId();
            final LambdaQueryWrapper<InspectionCodeContent> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(InspectionCodeContent::getInspectionCodeId, inspectionCodeId)
                    .eq(InspectionCodeContent::getType,inspectionCodeContent.getType())
                    .eq(InspectionCodeContent::getDelFlag, 0);
            final List<InspectionCodeContent> codeContentList = inspectionCodeContentService.list(wrapper);
            final List<Integer> sortList = codeContentList.stream().map(InspectionCodeContent::getSortNo).collect(Collectors.toList());
            if (sortList.contains(sortNo)){
                return result.error500("显示顺序重复");
            }
            inspectionCodeContentService.save(inspectionCodeContent);
            result.success("添加成功！");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("操作失败");
        }
        return result;
    }

    /**
     * 编辑
     *
     * @param inspectionCodeContent
     * @return
     */
    @AutoLog(value = "检修策略管理-编辑")
    @ApiOperation(value = "检修策略管理-编辑", notes = "检修策略管理-编辑")
    @PutMapping(value = "/edit")
    public Result<InspectionCodeContent> edit(@RequestBody @Validated InspectionCodeContent inspectionCodeContent) {
        Result<InspectionCodeContent> result = new Result<InspectionCodeContent>();
        InspectionCodeContent inspectionCodeContentEntity = inspectionCodeContentService.getById(inspectionCodeContent.getId());
        if (inspectionCodeContentEntity == null) {
            result.onnull("未找到对应实体");
        } else {
            final Integer sortNo = inspectionCodeContent.getSortNo();
            final Integer inspectionCodeId = inspectionCodeContent.getInspectionCodeId();
            final LambdaQueryWrapper<InspectionCodeContent> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(InspectionCodeContent::getInspectionCodeId, inspectionCodeId)
                    .eq(InspectionCodeContent::getType,inspectionCodeContent.getType())
                    .eq(InspectionCodeContent::getDelFlag, 0);
            final List<InspectionCodeContent> codeContentList = inspectionCodeContentService.list(wrapper);
            final List<Integer> sortList = codeContentList.stream().map(InspectionCodeContent::getSortNo).collect(Collectors.toList());
            if (sortList.contains(sortNo) && inspectionCodeContent.getSortNo() != inspectionCodeContentEntity.getSortNo()){
                return result.error500("显示顺序重复");
            }
            boolean ok = inspectionCodeContentService.updateById(inspectionCodeContent);

            if (ok) {
                result.success("修改成功!");
            }
        }

        return result;
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "检修策略管理-通过id删除")
    @ApiOperation(value = "检修策略管理-通过id删除", notes = "检修策略管理-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        try {
            inspectionCodeContentService.removeById(id);
        } catch (Exception e) {
            log.error("删除失败", e.getMessage());
            return Result.error("删除失败!");
        }
        return Result.ok("删除成功!");
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "检修策略管理-批量删除")
    @ApiOperation(value = "检修策略管理-批量删除", notes = "检修策略管理-批量删除")
    @DeleteMapping(value = "/deleteBatch")
    public Result<InspectionCodeContent> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        Result<InspectionCodeContent> result = new Result<InspectionCodeContent>();
        if (ids == null || "".equals(ids.trim())) {
            result.error500("参数不识别！");
        } else {
            this.inspectionCodeContentService.removeByIds(Arrays.asList(ids.split(",")));
            result.success("删除成功!");
        }
        return result;
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @AutoLog(value = "检修策略管理-通过id查询")
    @ApiOperation(value = "检修策略管理-通过id查询", notes = "检修策略管理-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<InspectionCodeContent> queryById(@RequestParam(name = "id", required = true) String id) {
        Result<InspectionCodeContent> result = new Result<InspectionCodeContent>();
        InspectionCodeContent inspectionCodeContent = inspectionCodeContentService.getById(id);
        if (inspectionCodeContent == null) {
            result.onnull("未找到对应实体");
        } else {
            result.setResult(inspectionCodeContent);
            result.setSuccess(true);
        }
        return result;
    }

    /**
     * 通过ids设置策略
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "标准管理-设置策略-通过ids设置")
    @ApiOperation(value = "标准管理-设置策略-通过ids设置", notes = "标准管理-设置策略-通过ids设置")
    @PostMapping(value = "/setStrategyByIds")
    public Result setStrategyByIds(@RequestParam(name = "ids", required = true) String ids,
                                   @RequestParam(name = "tactics", required = true) Integer tactics) {
        return inspectionCodeContentService.setStrategyByIds(ids, tactics);
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
        QueryWrapper<InspectionCodeContent> queryWrapper = null;
        try {
            String paramsStr = request.getParameter("paramsStr");
            if (oConvertUtils.isNotEmpty(paramsStr)) {
                String deString = URLDecoder.decode(paramsStr, "UTF-8");
                InspectionCodeContent inspectionCodeContent = JSON.parseObject(deString, InspectionCodeContent.class);
                queryWrapper = QueryGenerator.initQueryWrapper(inspectionCodeContent, request.getParameterMap());
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        //Step.2 AutoPoi 导出Excel
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        List<InspectionCodeContent> pageList = inspectionCodeContentService.list(queryWrapper);
        //导出文件名称
        mv.addObject(NormalExcelConstants.FILE_NAME, "检修策略管理列表");
        mv.addObject(NormalExcelConstants.CLASS, InspectionCodeContent.class);
        mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("检修策略管理列表数据", "导出时间:"+ LocalDate.now(), ExcelType.XSSF));
        mv.addObject(NormalExcelConstants.DATA_LIST, pageList);
        return mv;
    }

    /**
     * 通过excel导入数据
     *
     * @param inspectionCodeId
     * @param request
     * @param response
     * @return
     */
    @ApiOperation(value = "检修规范导入", notes = "检修规范导入")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result importExcel(@RequestParam(name = "inspectionCodeId", required = true) Integer inspectionCodeId,HttpServletRequest request, HttpServletResponse response) {
        InspectionCode inspectionCode = inspectionCodeService.getById(inspectionCodeId);
        if (inspectionCode == null){
            return Result.error("非法参数ID");
        }
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
            MultipartFile file = entity.getValue();
            ImportParams params = new ImportParams();
            params.setTitleRows(0);
            params.setHeadRows(1);
            params.setNeedSave(true);
            try {
                List<InspectionCodeContentExcelVO> list = ExcelImportUtil.importExcel(file.getInputStream(), InspectionCodeContentExcelVO.class, params);
                if (list.size() == 0){
                    return Result.error("转换异常");
                }
                for (int i = 0; i < list.size(); i++) {
                    final Integer sortNo = list.get(i).getSortNort();
                    final LambdaQueryWrapper<InspectionCodeContent> wrapper = new LambdaQueryWrapper<>();
                    wrapper.eq(InspectionCodeContent::getInspectionCodeId, inspectionCodeId)
                            .eq(InspectionCodeContent::getType,list.get(i).getType())
                            .eq(InspectionCodeContent::getDelFlag, 0);
                    final List<InspectionCodeContent> codeContentList = inspectionCodeContentService.list(wrapper);
                    final List<Integer> sortList = codeContentList.stream().map(InspectionCodeContent::getSortNo).collect(Collectors.toList());
                    if (sortList.contains(sortNo)){
                        return Result.error(500,"显示顺序重复");
                    }
                    InspectionCodeContent content = new InspectionCodeContent();
                    content.setType(list.get(i).getType());
                    if (list.get(i).getType() ==1){
                        content.setTactics(1);
                    }
                    content.setIsReceipt(list.get(i).getIsReceipt());
                    content.setInspectionCodeId(inspectionCodeId);
                    content.setContent(list.get(i).getContent());
                    content.setSortNo(list.get(i).getSortNort());
                    content.setRemarks(list.get(i).getRemarks());
                    inspectionCodeContentService.save(content);
                }
                return Result.ok("文件导入成功！数据行数:" + list.size());
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                return Result.error("文件导入失败:" + e.getMessage());
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
     * 下载模板
     *
     * @param response
     * @param request
     * @throws IOException
     */
    @AutoLog(value = "下载模板")
    @ApiOperation(value = "下载模板", notes = "下载模板")
    @RequestMapping(value = "/downloadExcel", method = RequestMethod.GET)
    public void downloadExcel(HttpServletResponse response, HttpServletRequest request) throws IOException {
        //获取输入流，原始模板位置
//        String filePath = excelPath;
        ClassPathResource classPathResource =  new ClassPathResource("classpath:template/safetyPrecautionsTemplate.xlsx");
        InputStream bis = classPathResource.getInputStream();
        BufferedOutputStream out = new BufferedOutputStream(response.getOutputStream());
        int len = 0;
        while ((len = bis.read()) != -1) {
            out.write(len);
            out.flush();
        }
        out.close();
    }
}
