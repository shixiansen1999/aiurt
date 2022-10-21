package com.aiurt.modules.train.task.listener;

import cn.hutool.core.util.StrUtil;
import com.aiurt.modules.train.task.entity.BdTrainPlan;
import com.aiurt.modules.train.task.entity.BdTrainPlanSub;
import com.aiurt.modules.train.task.entity.DemoData;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * @author admin
 */
@Slf4j
@Data
public class NoModelDataListener extends AnalysisEventListener<DemoData> {
    /**
     * 每隔5条存储数据库，实际使用中可以100条，然后清理list ，方便内存回收
     */

    private Integer year;

    private JSONObject jsonObject;

    private BdTrainPlan bdTrainPlan = new BdTrainPlan();

    private List<BdTrainPlanSub> list = new ArrayList<>();

    private int[] index = {1};

    private String classify = "";


    @Override
    public void invoke(DemoData data, AnalysisContext context) {
        log.info("解析到一条数据:{}", JSON.toJSONString(data));
        if (index[0] == 1) {
            index[0] += 1;
            return;
        }
        if (index[0] == 2) {
            index[0] += 1;
            this.jsonObject = JSON.parseObject(JSON.toJSONString(data));
            return;
        }
        index[0] += 1;
        JSONObject json = JSON.parseObject(JSON.toJSONString(data));
        BdTrainPlanSub bdTrainPlanSub = new BdTrainPlanSub();
        Set<String> keySet = json.keySet();
        String row0 = json.getString("row0");
        if (StrUtil.isNotBlank(row0) && !StrUtil.contains(row0, "编制部门") && !StrUtil.contains(row0, "注：1")) {
            bdTrainPlan.setDeptName(row0);
        }

        if (!StrUtil.contains(row0, "编制部门") && !StrUtil.contains(row0, "注：1")) {
            bdTrainPlanSub.setCourseHours(json.getInteger("row15"));
            bdTrainPlanSub.setCourseName(json.getString("row2"));

            String row16 = json.getString("row16");
            if (StrUtil.isNotBlank(row16)) {
                if (StrUtil.contains(row16, "安全类")) {
                    classify = "安全类";
                }
                if (StrUtil.contains(row16, "制度类")) {
                    classify = "制度类";
                }
                if (StrUtil.contains(row16, "技能类")) {
                    classify = "技能类";
                }
            }

            bdTrainPlanSub.setClassifyName(classify);

            keySet.stream().forEach(key -> {
                if (jsonObject.containsKey(key)) {
                    bdTrainPlanSub.setPlanTime(jsonObject.getString(key));
                }
            });
            list.add(bdTrainPlanSub);
        } else if (!StrUtil.contains(row0, "编制部门") && StrUtil.contains(row0, "注：1")) {
            String sRemark = row0.replaceAll(" ", "").replaceAll("\n", "");
            bdTrainPlan.setRemarks(sRemark);
        } else {
            //设置编制部门
            String trim = row0.replaceAll(" ", "");
            String sPrepareDept = trim.substring(5, trim.indexOf("制表人"));
            if (StrUtil.isNotBlank(sPrepareDept)) {
                bdTrainPlan.setPrepareDept(sPrepareDept);
            }
            //设置编制人
            String sprepareUserName = trim.substring(trim.indexOf("制表人") + 4, trim.indexOf("经理签字"));
            bdTrainPlan.setPrepareUserName(sprepareUserName);
        }


    }

    @Override
    public void invokeHeadMap(Map headMap, AnalysisContext context) {
        log.info("解析到的表头数据: {}", headMap);
        Collection values = headMap.values();
        values.stream().forEach(v -> {
            if (v instanceof String) {
                String iPlanYear = ((String) v).substring(0, 4);
                String sPlanName = ((String) v).substring(6, ((String) v).length() - 3);
                bdTrainPlan.setPlanYear(Integer.valueOf(iPlanYear));
                bdTrainPlan.setPlanName(sPlanName);
            }
        });

    }


    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        log.info("所有数据解析完成！");
    }

}
