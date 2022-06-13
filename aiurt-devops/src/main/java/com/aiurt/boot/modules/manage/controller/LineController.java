package com.aiurt.boot.modules.manage.controller;

import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.util.oConvertUtils;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.aiurt.boot.modules.manage.entity.Line;
import com.aiurt.boot.modules.manage.model.LineModel;
import com.aiurt.boot.modules.manage.service.ILineService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @Description: cs_line
 * @Author: swsc
 * @Date: 2021-09-15
 * @Version: V1.0
 */
@Slf4j
@Api(tags = "地铁线路表")
@RestController
@RequestMapping("/manage/line")
public class LineController {
    @Autowired
    private ILineService lineService;

    /**
     * 分页列表查询
     *
     * @param line
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "地铁线路表-分页列表查询")
    @ApiOperation(value = "地铁线路表-分页列表查询", notes = "地铁线路表-分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<Line>> queryPageList(Line line,
                                             @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                             @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                             HttpServletRequest req) {
        Result<IPage<Line>> result = new Result<IPage<Line>>();
        QueryWrapper<Line> queryWrapper = QueryGenerator.initQueryWrapper(line, req.getParameterMap());
        Page<Line> page = new Page<Line>(pageNo, pageSize);
        IPage<Line> pageList = lineService.page(page, queryWrapper);
        result.setSuccess(true);
        result.setResult(pageList);
        return result;
    }

    /**
     * 添加
     *
     * @param line
     * @return
     */
    @AutoLog(value = "地铁线路表-添加")
    @ApiOperation(value = "地铁线路表-添加", notes = "地铁线路表-添加")
    @PostMapping(value = "/add")
    public Result<Line> add(@RequestBody Line line) {
        Result<Line> result = new Result<Line>();
        try {
            lineService.save(line);
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
     * @param line
     * @return
     */
    @AutoLog(value = "地铁线路表-编辑")
    @ApiOperation(value = "地铁线路表-编辑", notes = "地铁线路表-编辑")
    @PutMapping(value = "/edit")
    public Result<Line> edit(@RequestBody Line line) {
        Result<Line> result = new Result<Line>();
        Line lineEntity = lineService.getById(line.getId());
        if (lineEntity == null) {
            result.onnull("未找到对应实体");
        } else {
            boolean ok = lineService.updateById(line);

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
    @AutoLog(value = "地铁线路表-通过id删除")
    @ApiOperation(value = "地铁线路表-通过id删除", notes = "地铁线路表-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        try {
            lineService.removeById(id);
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
    @AutoLog(value = "地铁线路表-批量删除")
    @ApiOperation(value = "地铁线路表-批量删除", notes = "地铁线路表-批量删除")
    @DeleteMapping(value = "/deleteBatch")
    public Result<Line> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        Result<Line> result = new Result<Line>();
        if (ids == null || "".equals(ids.trim())) {
            result.error500("参数不识别！");
        } else {
            this.lineService.removeByIds(Arrays.asList(ids.split(",")));
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
    @AutoLog(value = "地铁线路表-通过id查询")
    @ApiOperation(value = "地铁线路表-通过id查询", notes = "地铁线路表-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<Line> queryById(@RequestParam(name = "id", required = true) String id) {
        Result<Line> result = new Result<Line>();
        Line line = lineService.getById(id);
        if (line == null) {
            result.onnull("未找到对应实体");
        } else {
            result.setResult(line);
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
        QueryWrapper<Line> queryWrapper = null;
        try {
            String paramsStr = request.getParameter("paramsStr");
            if (oConvertUtils.isNotEmpty(paramsStr)) {
                String deString = URLDecoder.decode(paramsStr, "UTF-8");
                Line line = JSON.parseObject(deString, Line.class);
                queryWrapper = QueryGenerator.initQueryWrapper(line, request.getParameterMap());
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        //Step.2 AutoPoi 导出Excel
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        List<Line> pageList = lineService.list(queryWrapper);
        //导出文件名称
        mv.addObject(NormalExcelConstants.FILE_NAME, "cs_line列表");
        mv.addObject(NormalExcelConstants.CLASS, Line.class);
        mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("cs_line列表数据", "导出人:Jeecg", "导出信息"));
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
                List<Line> listLines = ExcelImportUtil.importExcel(file.getInputStream(), Line.class, params);
                lineService.saveBatch(listLines);
                return Result.ok("文件导入成功！数据行数:" + listLines.size());
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

    @GetMapping("queryTreeList")
    public Result<List<LineModel>> queryTreeList() {
        Result<List<LineModel>> result = new Result<List<LineModel>>();
        List<Line> lineList = lineService.list(new LambdaQueryWrapper<Line>().eq(Line::getDelFlag, 0));
        List<LineModel> list = new ArrayList<>();
        if (lineList != null && lineList.size() > 0) {
            lineList.forEach(line -> {
                list.add(new LineModel(line));
            });
            result.setResult(list);
        }
        return result;
    }
    @ApiModelProperty(value = "查询线路接口")
    @GetMapping("lineSelect")
    public Result<List<Line>> lineSelect() {
        Result<List<Line>> result = new Result<List<Line>>();
        List<Line> lineList = lineService.list(new LambdaQueryWrapper<Line>().eq(Line::getDelFlag, 0));
        result.setResult(lineList);
        return result;
    }

}
