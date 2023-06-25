package com.aiurt.boot.weeklyplan.job;

import com.aiurt.boot.constant.SysParamCodeConstant;
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
import org.springframework.web.client.RestTemplate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Component

public class constructionWeekPlanJob implements Job {

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
        // 定义请求URL和请求参数
        SysParamModel sysParamModel = sysParamApi.selectByCode(SysParamCodeConstant.CONSTRUCTION_WEEK_PLAN_COMMAND);
        String url = sysParamModel.getValue();
//        String url = "http://10.100.100.11:30300/cims/pool/pool/noGetwayGetPlan";
        Map params = new HashMap<String, Object>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.MONTH, -1);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int lastMonth = calendar.get(Calendar.MONTH);
        String startDate = year + "-" + lastMonth + "-" + day;
        String endDate = year + "-" + month + "-" + day;
        params.put("taskDateStart", startDate);
        params.put("taskDateEnd", endDate);
        SysParamModel planIstate = sysParamApi.selectByCode(SysParamCodeConstant.PLAN_ISTATE);
        params.put("planIstate", planIstate.getValue());
        SysParamModel department = sysParamApi.selectByCode(SysParamCodeConstant.DEPARTMENT_NAME);
        params.put("departmentName", department.getValue());
        JSONObject json = (JSONObject) JSONObject.toJSON(params);
        JSONObject resultList = restTemplate.postForObject(url, json, JSONObject.class);
        JSONArray result = resultList.getJSONArray("data");
        ArrayList<ConstructionWeekPlanCommand> list = new ArrayList<>();
        // 遍历结果,存入数据库中
        for (int i = 0; i < result.size(); i++) {
            JSONObject plan = result.getJSONObject(i);

            // 获取结果字段,存入test表
            String indocno = plan.getString("indocno");
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
            String ipowerRequirement = plan.getString("ipowerRequirement");
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
            command.setId(indocno);
            command.setWeekday(Integer.parseInt(weekday));
            command.setTaskDate(sdf.parse(taskDate));
            command.setTaskStaffNum(Integer.parseInt(taskStaffNum));
            String[] split = taskTime.split(",");
            command.setTaskStartTime(sdf.parse(split[0]));
            command.setTaskEndTime(sdf.parse(split[1]));
            command.setProtectiveMeasure(protectiveMeasure);
            if (type!=null){
                DictModel model = sysBaseApi.queryEnableDictItemsByCode("construction_category")
                        .stream().filter(l -> l.getText().equals(type))
                        .findFirst().orElse(null);
                if (model !=null){
                    command.setType(Integer.valueOf(model.getValue()));
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
//                command.setAssistStationCode(assistStationCode);
                command.setCoordinationDepartmentCode(assistStationCode);
            }
            command.setPlanChange(Integer.parseInt(planChange));
            if (nature!=null){
                DictModel model = sysBaseApi.queryEnableDictItemsByCode("construction_nature")
                        .stream().filter(l -> l.getText().equals(nature))
                        .findFirst().orElse(null);
                if (model !=null){
                    command.setNature(Integer.valueOf(model.getValue()));
                }
            }
            command.setPowerSupplyRequirementId(ipowerRequirement);
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
                DictModel model = sysBaseApi.queryEnableDictItemsByCode("construction_plan_typec")
                        .stream().filter(l -> l.getText().equals(plantype))
                        .findFirst().orElse(null);
                if (model !=null){
                    command.setPlanType(Integer.valueOf(model.getValue()));
                }
            }
            command.setCode(planno);
            Date date = new Date();
            command.setUpdateTime(date);
            list.add(command);
        }
        if (result.size()>0 && result!=null){
//            commandMapper.delete(null);
            commandService.saveOrUpdateBatch(list);
        }
    }
}
