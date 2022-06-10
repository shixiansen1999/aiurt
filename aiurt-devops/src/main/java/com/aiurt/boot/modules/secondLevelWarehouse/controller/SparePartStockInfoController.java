package com.aiurt.boot.modules.secondLevelWarehouse.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.swsc.copsms.common.api.vo.Result;
import com.swsc.copsms.common.aspect.annotation.AutoLog;
import com.swsc.copsms.common.system.query.QueryGenerator;
import com.swsc.copsms.common.util.oConvertUtils;
import com.swsc.copsms.modules.secondLevelWarehouse.entity.SparePartStockInfo;
import com.swsc.copsms.modules.secondLevelWarehouse.mapper.SparePartStockInfoMapper;
import com.swsc.copsms.modules.secondLevelWarehouse.service.ISparePartStockInfoService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * @Description: 备件仓库信息
 * @Author: qian
 * @Date: 2021-09-22
 * @Version: V1.0
 */
@Slf4j
@Api(tags = "备件仓库信息")
@RestController
@RequestMapping("/secondLevelWarehouse/sparePartStockInfo")
public class SparePartStockInfoController {
    @Resource
    private SparePartStockInfoMapper sparePartStockInfoMapper;

    @AutoLog(value = "备件仓库信息-列表查询")
    @ApiOperation(value = "备件仓库信息-列表查询", notes = "备件仓库信息-列表查询")
    @GetMapping(value = "/list")
    public Result<List<SparePartStockInfo>> queryList() {
        Result<List<SparePartStockInfo>> result = new Result<List<SparePartStockInfo>>();
        List<SparePartStockInfo> sparePartStockInfos = sparePartStockInfoMapper.selectList(null);
        result.setSuccess(true);
        result.setResult(sparePartStockInfos);
        return result;
    }


}
