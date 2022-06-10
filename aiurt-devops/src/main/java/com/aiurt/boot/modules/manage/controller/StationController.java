package com.aiurt.boot.modules.manage.controller;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.aiurt.boot.common.api.vo.Result;
import com.aiurt.boot.common.aspect.annotation.AutoLog;
import com.aiurt.boot.common.system.query.QueryGenerator;
import com.aiurt.boot.common.util.oConvertUtils;
import com.aiurt.boot.modules.manage.entity.Line;
import com.aiurt.boot.modules.manage.entity.Station;
import com.aiurt.boot.modules.manage.entity.StationPosition;
import com.aiurt.boot.modules.manage.model.StationModel;
import com.aiurt.boot.modules.manage.model.StationWarning;
import com.aiurt.boot.modules.manage.service.ILineService;
import com.aiurt.boot.modules.manage.service.IStationPositionService;
import com.aiurt.boot.modules.manage.service.IStationService;
import com.aiurt.boot.modules.system.entity.SysDepart;
import com.aiurt.boot.modules.system.entity.SysUser;
import com.aiurt.boot.modules.system.service.ISysDepartService;
import com.aiurt.boot.modules.system.service.ISysUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
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
 * @Description: cs_station
 * @Author: swsc
 * @Date: 2021-09-15
 * @Version: V1.0
 */
@Slf4j
@Api(tags = "站点信息")
@RestController
@RequestMapping("/manage/station")
public class StationController {
    @Autowired
    private IStationService stationService;
    @Autowired
    private ISysDepartService departService;
    @Autowired
    private ISysUserService userService;
    @Autowired
    private IStationPositionService positionService;
    @Autowired
    private ILineService lineService;

    /**
     * 分页列表查询
     *
     * @param station
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "站点信息-分页列表查询")
    @ApiOperation(value = "站点信息-分页列表查询", notes = "站点信息-分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<Station>> queryPageList(Station station,
                                                @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                HttpServletRequest req) {
        Result<IPage<Station>> result = new Result<IPage<Station>>();
        QueryWrapper<Station> queryWrapper = QueryGenerator.initQueryWrapper(station, req.getParameterMap());
        Page<Station> page = new Page<Station>(pageNo, pageSize);
        queryWrapper.orderByAsc("sort");
        IPage<Station> pageList = stationService.page(page, queryWrapper);
        pageList.getRecords().forEach(temp -> {
            SysDepart depart = departService.getById(temp.getTeamId());
            if (depart != null) {
                temp.setTeamName(depart.getDepartName());
            }
        });
        result.setSuccess(true);
        result.setResult(pageList);
        return result;
    }

    @AutoLog("获取站点信息")
    @ApiOperation(value = "获取站点信息", notes = "获取站点信息")
    @GetMapping(value = "/getStations")
    public Result<List<Station>> getStations() {
        Result<List<Station>> result = new Result<List<Station>>();
        List<Station> stations = stationService.getStationsInOrdered();
        if (ObjectUtil.isNotEmpty(stations)) {
            result.setSuccess(true);
            result.setResult(stations);
            return result;
        }
        return Result.error("未获取到站点信息");
    }

    /**
     * 添加
     *
     * @param station
     * @return
     */
    @AutoLog(value = "站点信息-添加")
    @ApiOperation(value = "站点信息-添加", notes = "站点信息-添加")
    @PostMapping(value = "/add")
    public Result<Station> add(@RequestBody Station station) {
        Result<Station> result = new Result<Station>();
        try {
            stationService.save(station);
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
     * @param station
     * @return
     */
    @AutoLog(value = "站点信息-编辑")
    @ApiOperation(value = "站点信息-编辑", notes = "站点信息-编辑")
    @PutMapping(value = "/edit")
    public Result<Station> edit(@RequestBody Station station) {
        Result<Station> result = new Result<Station>();
        Station stationEntity = stationService.getById(station.getId());
        if (stationEntity == null) {
            result.onnull("未找到对应实体");
        } else {
            boolean ok = stationService.updateById(station);

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
    @AutoLog(value = "站点信息-通过id删除")
    @ApiOperation(value = "站点信息-通过id删除", notes = "站点信息-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        try {
            stationService.removeById(id);
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
    @AutoLog(value = "站点信息-批量删除")
    @ApiOperation(value = "站点信息-批量删除", notes = "站点信息-批量删除")
    @DeleteMapping(value = "/deleteBatch")
    public Result<Station> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        Result<Station> result = new Result<Station>();
        if (ids == null || "".equals(ids.trim())) {
            result.error500("参数不识别！");
        } else {
            this.stationService.removeByIds(Arrays.asList(ids.split(",")));
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
    @AutoLog(value = "站点信息-通过id查询")
    @ApiOperation(value = "站点信息-通过id查询", notes = "站点信息-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<Station> queryById(@RequestParam(name = "id", required = true) String id) {
        Result<Station> result = new Result<Station>();
        Station station = stationService.getById(id);
        if (station == null) {
            result.onnull("未找到对应实体");
        } else {
            result.setResult(station);
            result.setSuccess(true);
        }
        return result;
    }


    /**
     * 通过code查询
     *
     * @param code
     * @return
     */
    @AutoLog(value = "cs_station-通过code查询")
    @ApiOperation(value = "cs_station-通过code查询", notes = "cs_station-通过code查询")
    @GetMapping(value = "/queryByCode")
    public Result<Station> queryByCode(@RequestParam(name = "code", required = true) String code) {
        Result<Station> result = new Result<Station>();
        Station station = stationService.getOne(new LambdaQueryWrapper<Station>().eq(Station::getDelFlag, 0)
                .eq(Station::getStationCode,code));
        if (station == null) {
            result.onnull("未找到对应实体");
        } else {
            result.setResult(station);
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
        QueryWrapper<Station> queryWrapper = null;
        try {
            String paramsStr = request.getParameter("paramsStr");
            if (oConvertUtils.isNotEmpty(paramsStr)) {
                String deString = URLDecoder.decode(paramsStr, "UTF-8");
                Station station = JSON.parseObject(deString, Station.class);
                queryWrapper = QueryGenerator.initQueryWrapper(station, request.getParameterMap());
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        //Step.2 AutoPoi 导出Excel
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        List<Station> pageList = stationService.list(queryWrapper);
        //导出文件名称
        mv.addObject(NormalExcelConstants.FILE_NAME, "cs_station列表");
        mv.addObject(NormalExcelConstants.CLASS, Station.class);
        mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("cs_station列表数据", "导出人:Jeecg", "导出信息"));
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
                List<Station> listStations = ExcelImportUtil.importExcel(file.getInputStream(), Station.class, params);
                stationService.saveBatch(listStations);
                return Result.ok("文件导入成功！数据行数:" + listStations.size());
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
    public Result<List<StationModel>> queryTreeList() {
        Result<List<StationModel>> result = new Result<List<StationModel>>();
        List<Station> stationList = stationService.list(new LambdaQueryWrapper<Station>().eq(Station::getDelFlag, 0));
        List<StationModel> list = new ArrayList<>();
        if (stationList != null && stationList.size() > 0) {
            stationList.forEach(station -> {
                list.add(new StationModel(station));
            });
            result.setResult(list);
        }
        return result;
    }
    @ApiOperation(value = "查询线路与站点树", notes = "查询线路与站点树")
    @GetMapping("queryStationTree")
    public Result<List<StationModel>> queryStationTree() {

        Result<List<StationModel>> result = new Result<List<StationModel>>();
        List<Line> lineList = lineService.list(new LambdaQueryWrapper<Line>().eq(Line::getDelFlag, 0));
        List<Station> stationList = stationService.list(new LambdaQueryWrapper<Station>().eq(Station::getDelFlag, 0));
        List<StationModel> treeList = new ArrayList<>();
        if (lineList != null && lineList.size() > 0) {
            lineList.forEach(line -> {
                treeList.add(new StationModel(line, false));
            });

            for(StationModel stationModel : treeList){
                List<StationModel> childrenList = new ArrayList<>();
                stationList.forEach(station -> {
                    if(stationModel.getId().equals(station.getLineId().toString())){
                        childrenList.add(new StationModel(station));
                    }
                });
                stationModel.setChildren(childrenList);
            }
        }
        result.setResult(treeList);
        return result;
    }

    @ApiOperation(value = "根据线路id查询站点信息", notes = "根据线路id查询站点信息")
    @RequestMapping(value = "/queryStationListByLineId", method = RequestMethod.GET)
    public Result<List<Station>> queryStationListByLineId(@RequestParam(name = "lineId", required = true) String lineId) {
        Result<List<Station>> result = new Result<List<Station>>();
        List<Station> stationList = stationService.list(new QueryWrapper<Station>().eq("line_id", lineId).eq("del_flag", 0));
        if (stationList.size() > 0 && stationList != null) {
            result.setResult(stationList);
            result.setSuccess(true);
        } else {
            result.onnull("未找到对应实体");
        }
        return result;
    }

    @ApiOperation(value = " 根据人员id或者班组id获取所在的线路和车站信息 ", notes = " 根据人员id或者班组id，获取所在的线路和车站信息 ")
    @RequestMapping(value = "/queryStationListByTidOrUid", method = RequestMethod.GET)
    public Result<List<Station>> queryStationListByTidOrUid(@RequestParam(name = "tid", required = false) String tid, @RequestParam(name = "uid", required = false) String uid) {
        Result<List<Station>> result = new Result<List<Station>>();
        List<Station> stationList = new ArrayList<>();
        if (StringUtils.isNotEmpty(tid)) {
            stationList = stationService.list(new QueryWrapper<Station>().eq("team_id", tid).eq("del_flag", 0));
            stationList.forEach(station -> {
                SysDepart depart = departService.getOne(new QueryWrapper<SysDepart>().eq("id", station.getTeamId()).eq("del_flag", 0));
                station.setTeamName(depart != null ? depart.getDepartName() : "");
                Line line = lineService.getById(station.getLineId());
                station.setLineCode(line != null ? line.getLineCode() : "");
            });
        } else {
            SysUser user = userService.getById(uid);
            if (user != null) {
                stationList = stationService.list(new QueryWrapper<Station>().eq("team_id", user.getOrgId()).eq("del_flag", 0));
                stationList.forEach(station -> {
                    SysDepart depart = departService.getOne(new QueryWrapper<SysDepart>().eq("id", station.getTeamId()).eq("del_flag", 0));
                    station.setTeamName(depart != null ? depart.getDepartName() : "");
                    Line line = lineService.getById(station.getLineId());
                    station.setLineCode(line != null ? line.getLineCode() : "");
                });
            }
        }
        if (stationList.size() > 0 && stationList != null) {
            result.setResult(stationList);
        } else {
            result.setResult(new ArrayList<>());
        }
        result.setSuccess(true);
        return result;
    }

    @ApiOperation(value = "根据站点id查询站点位置信息", notes = "根据站点id查询站点位置信息")
    @RequestMapping(value = "/queryStationPositionList", method = RequestMethod.GET)
    public Result<List<StationPosition>> queryStationPositionList(@RequestParam(name = "stationId", required = true) String stationId) {
        Result<List<StationPosition>> result = new Result<List<StationPosition>>();
        List<StationPosition> stationPositionList = new ArrayList<>();
        stationPositionList = positionService.list(new QueryWrapper<StationPosition>().eq("station_id", stationId).eq("del_flag", 0));
        result.setResult(stationPositionList);
        result.setSuccess(true);
        return result;
    }

    @ApiOperation(value = "根据站点名称修改预警状态和开站状态", notes = "根据站点名称修改预警状态和开站状态")
    @PutMapping(value = "/editWarningStatus")
    public Result<Station> editWarningStatus(@RequestBody StationWarning stationWarning) {
        Result<Station> result = new Result<Station>();
        Station stationEntity = stationService.getOne(new LambdaQueryWrapper<Station>().eq(Station::getStationName, stationWarning.getStationName()));

        if (stationEntity == null) {
            result.onnull("未找到对应实体");
        } else {
            stationEntity.setWarningStatus(stationWarning.getStatus());
            stationEntity.setOpenStatus(stationWarning.getOpenStatus());
            boolean ok = stationService.updateById(stationEntity);

            if (ok) {
                result.success("修改成功!");
            }
        }

        return result;
    }


}
