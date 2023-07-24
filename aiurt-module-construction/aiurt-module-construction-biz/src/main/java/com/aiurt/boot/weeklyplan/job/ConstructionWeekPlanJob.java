package com.aiurt.boot.weeklyplan.job;

import com.aiurt.boot.constant.SysParamCodeConstant;
import com.aiurt.boot.weeklyplan.dto.ConstructionWeekPlanDTO;
import com.aiurt.boot.weeklyplan.entity.ConstructionWeekPlanCommand;
import com.aiurt.boot.weeklyplan.mapper.ConstructionWeekPlanCommandMapper;
import com.aiurt.boot.weeklyplan.service.IConstructionWeekPlanCommandService;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.api.ISysParamAPI;
import org.jeecg.common.system.vo.DictModel;
import org.jeecg.common.system.vo.SysParamModel;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ConstructionWeekPlanJob implements Job {

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private ConstructionWeekPlanCommandMapper commandMapper;
    @Autowired
    private IConstructionWeekPlanCommandService commandService;
    @Autowired
    private ISysBaseAPI sysBaseApi;
    @Autowired
    private ISysParamAPI sysParamApi;
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        log.info("******正在导入施工计划数据...******");
        try {
            generateWeekPlan();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        log.info("******施工计划数据导入完成！*******");
    }

    void generateWeekPlan() throws ParseException {

        // 定义请求URL和请求参数
        SysParamModel sysParamModel = sysParamApi.selectByCode(SysParamCodeConstant.CONSTRUCTION_WEEK_PLAN_COMMAND);
        String url = sysParamModel.getValue();
//        String url = "http://10.100.100.11:30300/cims/pool/pool/noGetwayGetPlan";
        Map params = new HashMap<String, Object>();
        SysParamModel start = sysParamApi.selectByCode(SysParamCodeConstant.LAST_MONTH);
        SysParamModel end = sysParamApi.selectByCode(SysParamCodeConstant.NEW_MONTH);
        LocalDate currentDate = LocalDate.now();
        LocalDate startDate = currentDate.minusMonths(Integer.valueOf(start.getValue()));
        LocalDate endDate = currentDate.plusMonths(Integer.valueOf(end.getValue()));
        params.put("taskDateStart", String.valueOf(startDate));
        params.put("taskDateEnd", String.valueOf(endDate));
        SysParamModel planIstate = sysParamApi.selectByCode(SysParamCodeConstant.PLAN_ISTATE);
        params.put("planIstate", planIstate.getValue());
        SysParamModel department = sysParamApi.selectByCode(SysParamCodeConstant.DEPARTMENT_NAME);
        params.put("departmentName", department.getValue());
        JSONObject json = (JSONObject) JSONObject.toJSON(params);
        JSONObject resultList = restTemplate.postForObject(url, json, JSONObject.class);
        JSONArray result = resultList.getJSONArray("data");
        ArrayList<ConstructionWeekPlanCommand> list = new ArrayList<>();
        //数据预加载
        List<DictModel> constructionCategory = sysBaseApi.queryEnableDictItemsByCode("construction_category");
        Map<String, String> category = constructionCategory.stream().collect(Collectors.toMap(DictModel::getText, DictModel::getValue, (t1, t2) -> t1));

        List<DictModel> constructionPlanTypec = sysBaseApi.queryEnableDictItemsByCode("construction_plan_typec");
        Map<String, String> typec = constructionPlanTypec.stream().collect(Collectors.toMap(DictModel::getText, DictModel::getValue, (t1, t2) -> t1));

        List<DictModel> constructionNature = sysBaseApi.queryEnableDictItemsByCode("construction_nature");
        Map<String, String> nature = constructionNature.stream().collect(Collectors.toMap(DictModel::getText, DictModel::getValue, (t1, t2) -> t1));

        List<DictModel> departs = commandMapper.selectOrgName();
        Map<String, String> depart = departs.stream().collect(Collectors.toMap(DictModel::getText, DictModel::getValue, (t1, t2) -> t1));
        List<DictModel> stations = commandMapper.selectStationName();
        Map<String, String> station = stations.stream().collect(Collectors.toMap(DictModel::getText, DictModel::getValue, (t1, t2) -> t1));

        // 遍历结果,存入数据库中
        for (int i = 0; i < result.size(); i++) {
            JSONObject plan = result.getJSONObject(i);
            ConstructionWeekPlanDTO dto = plan.toJavaObject(ConstructionWeekPlanDTO.class);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            ConstructionWeekPlanCommand command = new ConstructionWeekPlanCommand();
            command.setId(dto.getIndocno());
            command.setWeekday(Integer.parseInt(dto.getWeekday()));
            command.setTaskDate(sdf.parse(dto.getTaskDate()));
            command.setTaskStaffNum(Integer.parseInt(dto.getTaskStaffNum()));
            String[] split = dto.getTaskTime().split(",");
            command.setTaskStartTime(sdf.parse(split[0]));
            command.setTaskEndTime(sdf.parse(split[1]));
            command.setProtectiveMeasure(dto.getProtectiveMeasure());
            if (dto.getType()!=null){
                String type = category.get(dto.getType());
                if (type!=null){
                    command.setType(Integer.valueOf(type));
                }
            }
            if (dto.getDepartmentName()!=null){
                String[] split1 = dto.getDepartmentName().split("-");
                String orgName=null;
                if (split1.length >= 2 && split1[1]!=null){
                    orgName=split1[1];
                }else {
                    orgName=split1[0];
                }
                command.setOrgCode(depart.get(orgName));
            }
            command.setTaskRange(dto.getTaskRange());
            command.setTaskContent(dto.getTaskContent());
//            chargeStaffName
            command.setLargeAppliances(dto.getLargeAppliances());
//            lineStaffName
//            dispatchStaffName
            command.setRemark(dto.getRemark());
            if (dto.getAssistStationName()!=null){
//                command.setAssistStationCode(assistStationCode);
                command.setCoordinationDepartmentCode(dto.getAssistStationName());
            }
            command.setPlanChange(Integer.parseInt(dto.getPlanChange()));
            if (dto.getNature()!=null){
                String model = nature.get(dto.getNature());
                if (model !=null){
                    command.setNature(Integer.valueOf(model));
                }
            }
            command.setPowerSupplyRequirementId(dto.getIpowerRequirement());
            command.setPowerSupplyRequirementContent(dto.getPowerSupplyRequirement());
            if (dto.getFirstStationName()!=null){
                command.setFirstStationCode(station.get(dto.getFirstStationName()));
            }
            if (dto.getSecondStationName()!=null){
                command.setSecondStationCode(station.get(dto.getSecondStationName()));
            }
            if (dto.getSubstationName()!=null){
                command.setSubstationCode(station.get(dto.getSubstationName()));
            }
//            applyStaffName
            command.setFormStatus(Integer.parseInt(dto.getFormStatus()));
            command.setApplyFormStatus(Integer.parseInt(dto.getApplyFormStatus()));
            command.setLineStatus(Integer.parseInt(dto.getLineFormStatus()));
            command.setDispatchStatus(Integer.parseInt(dto.getDispatchFormStatus()));
            if (dto.getPlantype()!=null){
                String type1 = typec.get(dto.getPlantype());
                if (type1 !=null){
                    command.setPlanType(Integer.valueOf(type1));
                }
            }
            command.setCode(dto.getPlanno());
            Date date = new Date();
            command.setUpdateTime(date);
            list.add(command);
        }
        if (result.size()>0 && result!=null){
            commandService.saveOrUpdateBatch(list);
        }
    }
}