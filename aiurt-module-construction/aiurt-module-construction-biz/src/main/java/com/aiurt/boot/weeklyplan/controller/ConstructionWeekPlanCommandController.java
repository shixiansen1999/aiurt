package com.aiurt.boot.weeklyplan.controller;

import com.aiurt.boot.constant.SysParamCodeConstant;
import com.aiurt.boot.weeklyplan.dto.ConstructionWeekPlanCommandDTO;
import com.aiurt.boot.weeklyplan.entity.ConstructionWeekPlanCommand;
import com.aiurt.boot.weeklyplan.mapper.ConstructionWeekPlanCommandMapper;
import com.aiurt.boot.weeklyplan.service.IConstructionWeekPlanCommandService;
import com.aiurt.boot.weeklyplan.vo.ConstructionUserVO;
import com.aiurt.boot.weeklyplan.vo.ConstructionWeekPlanCommandVO;
import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.system.base.controller.BaseController;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.api.ISysParamAPI;
import org.jeecg.common.system.vo.DictModel;
import org.jeecg.common.system.vo.SysParamModel;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Description: construction_week_plan_command
 * @Author: aiurt
 * @Date: 2022-11-22
 * @Version: V1.0
 */
@Api(tags = "施工周计划")
@RestController
@RequestMapping("/weeklyplan/constructionWeekPlanCommand")
@Slf4j
public class ConstructionWeekPlanCommandController extends BaseController<ConstructionWeekPlanCommand, IConstructionWeekPlanCommandService> {
    @Autowired
    private IConstructionWeekPlanCommandService constructionWeekPlanCommandService;
    @Autowired
    private ConstructionWeekPlanCommandMapper commandMapper;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private ISysBaseAPI sysBaseApi;
    @Autowired
    private ISysParamAPI sysParamApi;

    /**
     * 施工周计划列表查询
     *
     * @param constructionWeekPlanCommandDTO
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "施工周计划列表查询")
    @ApiOperation(value = "施工周计划列表查询", notes = "施工周计划列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<ConstructionWeekPlanCommandVO>> queryPageList(ConstructionWeekPlanCommandDTO constructionWeekPlanCommandDTO,
                                                                      @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                                      @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                                      HttpServletRequest req) {
        Page<ConstructionWeekPlanCommandVO> page = new Page<>(pageNo, pageSize);
        IPage<ConstructionWeekPlanCommandVO> pageList = constructionWeekPlanCommandService.queryPageList(page, constructionWeekPlanCommandDTO);
        return Result.OK(pageList);
    }

    @AutoLog(value = "定时任务暂时")
    @ApiOperation(value = "定时任务暂时", notes = "定时任务暂时")
    @GetMapping(value = "/quartz")
    public void queryPageList() throws ParseException {
        generateWeekPlan();
    }

    void generateWeekPlan() throws ParseException {

        commandMapper.delete(null);
        // 定义请求URL和请求参数
        // 定义请求URL和请求参数
        SysParamModel sysParamModel = sysParamApi.selectByCode(SysParamCodeConstant.CONSTRUCTION_WEEK_PLAN_COMMAND);
        String url = sysParamModel.getValue();
//        String url = "http://10.100.100.11:30300/cims/pool/pool/noGetwayGetPlan";
//        JSONObject params = new JSONObject();
        Map params = new HashMap<String, Object>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        calendar.add(Calendar.YEAR, -1);
        int lastYear = calendar.get(Calendar.YEAR);
        String startDate = lastYear + "-" + month + "-" + day;
        String endDate = year + "-" + month + "-" + day;
        params.put("taskDateStart", startDate);
        params.put("taskDateEnd", endDate);
        params.put("planIstate", 2);
        params.put("departmentName", SysParamCodeConstant.DEPARTMENT_NAME);
        JSONObject json = (JSONObject) JSONObject.toJSON(params);
        JSONObject resultList = restTemplate.postForObject(url, json, JSONObject.class);
        JSONArray result = resultList.getJSONArray("data");
        ArrayList<ConstructionWeekPlanCommand> list = new ArrayList<>();
        // 遍历结果,存入数据库中
        for (int i = 0; i < result.size(); i++) {
            JSONObject plan = result.getJSONObject(i);

            // 获取结果字段,存入test表
            String weekday = plan.getString("weekday");
            String taskDate = plan.getString("taskDate");
            String taskStaffNum = plan.getString("taskStaffNum");
            String taskTime = plan.getString("taskTime");
            String protectiveMeasure = plan.getString("protectiveMeasure");
            String type = plan.getString("type");
            String departmentName = plan.getString("departmentName");
            String taskRange = plan.getString("taskRange");
            String taskContent = plan.getString("taskContent");
            String chargeStaffName = plan.getString("chargeStaffName");
            String largeAppliances = plan.getString("largeAppliances");
            String lineStaffName = plan.getString("lineStaffName");
            String dispatchStaffName = plan.getString("dispatchStaffName");
            String remark = plan.getString("remark");
            String assistStationName = plan.getString("assistStationName");
            String planChange = plan.getString("planChange");
            String nature = plan.getString("nature");
            String powerSupplyRequirement = plan.getString("powerSupplyRequirement");
            String firstStationName = plan.getString("firstStationName");
            String secondStationName = plan.getString("secondStationName");
            String substationName = plan.getString("substationName");
            String applyStaffName = plan.getString("applyStaffName");
            String formStatus = plan.getString("formStatus");
            String applyFormStatus = plan.getString("applyFormStatus");
            String lineFormStatus = plan.getString("lineFormStatus");
            String dispatchFormStatus = plan.getString("dispatchFormStatus");
            String plantype = plan.getString("plantype");
            String planno = plan.getString("planno");

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            ConstructionWeekPlanCommand command = new ConstructionWeekPlanCommand();
            command.setWeekday(Integer.parseInt(weekday));
            command.setTaskDate(sdf.parse(taskDate));
            command.setTaskStaffNum(Integer.parseInt(taskStaffNum));
            String[] split = taskTime.split(",");
            command.setTaskStartTime(sdf.parse(split[0]));
            command.setTaskEndTime(sdf.parse(split[1]));
            command.setProtectiveMeasure(protectiveMeasure);
            if (type!=null){
                DictModel model = sysBaseApi.queryEnableDictItemsByCode("construction_category")
                        .stream().filter(l -> l.getValue().equals(type))
                        .findFirst().orElse(null);
                if (model !=null){
                    command.setType(Integer.valueOf(model.getText()));
                }
            }
            if (departmentName!=null){
                String[] split1 = departmentName.split("-");
                String orgName=null;
                if (split1.length >= 2 && split1[1]!=null){
                    orgName=split1[1];
                }else {
                    orgName=split1[0];
                }
                String orgCode = commandMapper.selectOrgName(orgName);
                command.setOrgCode(orgCode);
            }
            command.setTaskRange(taskRange);
            command.setTaskContent(taskContent);
//            chargeStaffName
            command.setLargeAppliances(largeAppliances);
//            lineStaffName
//            dispatchStaffName
            command.setRemark(remark);
            if (assistStationName!=null){
                String assistStationCode = commandMapper.selectStationName(assistStationName);
                command.setAssistStationCode(assistStationCode);
            }
            command.setPlanChange(Integer.parseInt(planChange));
            if (nature!=null){
                DictModel model = sysBaseApi.queryEnableDictItemsByCode("construction_nature")
                        .stream().filter(l -> l.getValue().equals(nature))
                        .findFirst().orElse(null);
                if (model !=null){
                    command.setNature(Integer.valueOf(model.getText()));
                }
            }
            command.setPowerSupplyRequirementContent(powerSupplyRequirement);
            if (firstStationName!=null){
                String stationCode = commandMapper.selectStationName(firstStationName);
                command.setFirstStationCode(stationCode);
            }
            if (secondStationName!=null){
                String stationCode = commandMapper.selectStationName(secondStationName);
                command.setSecondStationCode(stationCode);
            }
            if (substationName!=null){
                String stationCode = commandMapper.selectStationName(substationName);
                command.setSubstationCode(stationCode);
            }
//            applyStaffName
            command.setFormStatus(Integer.parseInt(formStatus));
            command.setApplyFormStatus(Integer.parseInt(applyFormStatus));
            command.setLineStatus(Integer.parseInt(lineFormStatus));
            command.setDispatchStatus(Integer.parseInt(dispatchFormStatus));
            if (plantype!=null){
                DictModel model = sysBaseApi.queryEnableDictItemsByCode("construction_plan_type1")
                        .stream().filter(l -> l.getValue().equals(plantype))
                        .findFirst().orElse(null);
                if (model !=null){
                    command.setPlanType(Integer.valueOf(model.getText()));
                }
            }
            command.setCode(planno);
            list.add(command);
        }
        constructionWeekPlanCommandService.saveBatch(list);

    }

    /**
     * 施工周计划申报
     *
     * @param constructionWeekPlanCommand
     * @return
     */
    @AutoLog(value = "施工周计划申报")
    @ApiOperation(value = "施工周计划申报", notes = "施工周计划申报")
    @PostMapping(value = "/declaration")
    public Result<String> declaration(@RequestBody ConstructionWeekPlanCommand constructionWeekPlanCommand) {
        String id = constructionWeekPlanCommandService.declaration(constructionWeekPlanCommand);
        return Result.OK("添加成功！", id);
    }

    /**
     * 施工周计划-编辑
     *
     * @param constructionWeekPlanCommand
     * @return
     */
    @AutoLog(value = "施工周计划-编辑")
    @ApiOperation(value = "施工周计划-编辑", notes = "施工周计划-编辑")
    @RequestMapping(value = "/edit", method = {RequestMethod.POST})
    public Result<String> edit(@RequestBody ConstructionWeekPlanCommand constructionWeekPlanCommand) {
        constructionWeekPlanCommandService.edit(constructionWeekPlanCommand);
        return Result.OK("编辑成功!");
    }

    /**
     * 施工周计划-取消计划
     *
     * @param id
     * @return
     */
    @AutoLog(value = "施工周计划-取消计划")
    @ApiOperation(value = "施工周计划-取消计划", notes = "施工周计划-取消计划")
    @RequestMapping(value = "/cancel", method = {RequestMethod.POST})
    public Result<String> cancel(@ApiParam(name = "id", value = "记录主键ID") @RequestParam("id") String id,
                                 @ApiParam(name = "reason", value = "取消原因") @RequestParam("reason") String reason) {
        constructionWeekPlanCommandService.cancel(id, reason);
        return Result.OK("计划已成功取消!");
    }

    /**
     * 施工周计划-计划提审
     *
     * @param id
     * @return
     */
    @AutoLog(value = "施工周计划-计划提审")
    @ApiOperation(value = "施工周计划-计划提审", notes = "施工周计划-计划提审")
    @RequestMapping(value = "/submit", method = {RequestMethod.POST})
    public Result<String> submit(@ApiParam(name = "id", value = "记录主键ID") @RequestParam("id") String id) {
        constructionWeekPlanCommandService.submit(id);
        return Result.OK("计划提审成功!");
    }

    /**
     * 施工周计划-计划审核
     *
     * @param id
     * @return
     */
    @AutoLog(value = "施工周计划-计划审核")
    @ApiOperation(value = "施工周计划-计划审核", notes = "施工周计划-计划审核")
    @RequestMapping(value = "/audit", method = {RequestMethod.POST})
    public Result<String> audit(@ApiParam(name = "id", value = "记录主键ID")
                                @RequestParam("id") String id,
                                @ApiParam(name = "status", value = "审批状态：0未审批、1同意、2驳回")
                                @RequestParam("status") Integer status) {
        constructionWeekPlanCommandService.audit(id);
        return Result.OK("计划审核成功!");
    }

    /**
     * 施工周计划-根据ID查询计划信息
     *
     * @param id
     * @return
     */
    @AutoLog(value = "施工周计划-根据ID查询计划信息")
    @ApiOperation(value = "施工周计划-根据ID查询计划信息", notes = "施工周计划-根据ID查询计划信息")
    @GetMapping(value = "/queryById")
    public Result<ConstructionWeekPlanCommand> queryById(@RequestParam(name = "id", required = true) String id) {
        ConstructionWeekPlanCommand constructionWeekPlanCommand = constructionWeekPlanCommandService.queryById(id);
        return Result.OK(constructionWeekPlanCommand);
    }

    @ApiOperation(value = "周计划审核", notes = "周计划审核")
    @GetMapping(value = "/queryWorkToDo")
    public Result<IPage<ConstructionWeekPlanCommandVO>> queryWorkToDo(ConstructionWeekPlanCommandDTO constructionWeekPlanCommandDTO,
                                                                      @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                                      @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        Page<ConstructionWeekPlanCommandVO> page = new Page<>(pageNo, pageSize);
        IPage<ConstructionWeekPlanCommandVO> pageList = constructionWeekPlanCommandService.queryWorkToDo(page, constructionWeekPlanCommandDTO);
        // TODO: 2023/3/18 通用出无数据
        return Result.OK(pageList);
    }

    /**
     * 施工周计划-根据ID删除计划
     */
    @AutoLog(value = "施工周计划-根据ID删除计划",operateType = 4,permissionUrl = "/prodManage/week/weekchange")
    @ApiOperation(value = "施工周计划-根据ID删除计划", notes = "施工周计划-根据ID删除计划")
    @PostMapping(value = "/delete")
    public Result<String> delete(@RequestParam @ApiParam(name = "id", value = "计划主键ID") String id) {
        constructionWeekPlanCommandService.delete(id);
        return Result.OK("删除成功！");
    }

    /**
     * 施工周计划-获取岗位为施工负责人的用户
     *
     * @return
     */
    @ApiOperation(value = "施工周计划-获取岗位为施工负责人的用户", notes = "施工周计划-获取岗位为施工负责人的用户")
    @RequestMapping(value = "/getConstructionUser", method = RequestMethod.GET)
    public Result<?> getConstructionUser() {
        List<ConstructionUserVO> loginUsers = constructionWeekPlanCommandService.getConstructionUser();
        return Result.OK(loginUsers);
    }

    @ApiOperation(value = "下载施工周计划导入模板", notes = "下载施工周计划导入模板")
    @RequestMapping(value = "/downloadExcel", method = RequestMethod.GET)
    public void downloadExcel(HttpServletResponse response, HttpServletRequest request) throws IOException {
        ClassPathResource classPathResource = new ClassPathResource("templates/constructionWeekPlanCommand.xlsx");
        InputStream bis = classPathResource.getInputStream();
        BufferedOutputStream out = new BufferedOutputStream(response.getOutputStream());
        int len = 0;
        while ((len = bis.read()) != -1) {
            out.write(len);
            out.flush();
        }
        out.close();
    }

    /**
     * 通过excel导入数据
     *
     * @param request
     * @param response
     * @return
     */
    @ApiOperation(value = "通过excel导入数据", notes = "通过excel导入数据")
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
                return constructionWeekPlanCommandService.importExcelMaterial(file, params);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                return Result.error("文件导入失败:" + e.getMessage());
            } finally {
                try {
                    file.getInputStream().close();
                } catch (IOException e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
        return Result.error("文件导入失败！");
    }

    /**
     * 施工周计划-导出周计划
     *
     * @return
     */
    @ApiOperation(value = "施工周计划-导出周计划", notes = "施工周计划-导出周计划")
    @RequestMapping(value = "/exportXls", method = {RequestMethod.GET, RequestMethod.POST})
    public void exportXls(HttpServletRequest request, HttpServletResponse response,
                          @ApiParam(name = "lineCode", value = "线路编码") String lineCode,
                          @ApiParam(name = "startDate", value = "所属周起始时间，格式yyyy-MM-dd")
                          @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
                          @ApiParam(name = "endDate", value = "所属周截止时间，格式yyyy-MM-dd")
                          @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {
        constructionWeekPlanCommandService.exportXls(request, response, lineCode, startDate, endDate);
    }
}
