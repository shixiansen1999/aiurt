package com.aiurt.boot.weeklyplan.job;

import com.aiurt.boot.weeklyplan.entity.ConstructionWeekPlanCommand;
import com.aiurt.boot.weeklyplan.mapper.ConstructionWeekPlanCommandMapper;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.DictModel;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component

public class constructionWeekPlanJob implements Job {

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private ConstructionWeekPlanCommandMapper commandMapper;
    @Autowired
    private ISysBaseAPI sysBaseApi;
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


    public void generateWeekPlan() throws ParseException {

        commandMapper.delete(null);
        // 定义请求URL和请求参数
        String url = "http://10.100.100.11:30300/cims/pool/pool/noGetwayGetPlan";
//        JSONObject params = new JSONObject();
        Map params = new HashMap<String, Object>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
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
        params.put("departmentName", "通号中心");
        JSONObject json = (JSONObject) JSONObject.toJSON(params);
        JSONObject resultList = restTemplate.postForObject(url, json, JSONObject.class);
        JSONArray result = resultList.getJSONArray("data");
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

            ConstructionWeekPlanCommand command = new ConstructionWeekPlanCommand();
            command.setWeekday(Integer.parseInt(weekday));
//            SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
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
            //            departmentName
            if (departmentName!=null){
                String orgCode = commandMapper.selectOrgName(departmentName);
                command.setOrgCode(orgCode);
            }
            command.setTaskRange(taskRange);
            command.setTaskContent(taskContent);
//            chargeStaffName
            command.setLargeAppliances(largeAppliances);
//            lineStaffName
//            dispatchStaffName
            command.setRemark(remark);
//            assistStationName
            if (assistStationName!=null){
                String orgCode = commandMapper.selectStationName(assistStationName);
                command.setOrgCode(orgCode);
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
//            firstStationName
            if (firstStationName!=null){
                String stationCode = commandMapper.selectStationName(firstStationName);
                command.setFirstStationCode(stationCode);
            }
//            secondStationName
            if (secondStationName!=null){
                String stationCode = commandMapper.selectStationName(secondStationName);
                command.setSecondStationCode(stationCode);
            }
//            substationName
            if (substationName!=null){
                String stationCode = commandMapper.selectStationName(substationName);
                command.setSubstationCode(stationCode);
            }
//            applyStaffName
            command.setFormStatus(Integer.parseInt(formStatus));
//            applyFormStatus
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
            commandMapper.insert(command);
        }

    }
}
