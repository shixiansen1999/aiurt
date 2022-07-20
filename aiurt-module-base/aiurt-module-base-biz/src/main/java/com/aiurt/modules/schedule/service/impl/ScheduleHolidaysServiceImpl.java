package com.aiurt.modules.schedule.service.impl;

import cn.hutool.core.util.ObjectUtil;

import com.aiurt.modules.schedule.mapper.ScheduleHolidaysMapper;
import com.aiurt.modules.schedule.service.IScheduleHolidaysService;
import com.aiurt.modules.schedule.entity.ScheduleHolidays;
import com.aiurt.common.util.DateUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Description: schedule_holidays
 * @Author: swsc
 * @Date:   2021-09-23
 * @Version: V1.0
 */
@Service
@Slf4j
public class ScheduleHolidaysServiceImpl extends ServiceImpl<ScheduleHolidaysMapper, ScheduleHolidays> implements IScheduleHolidaysService {

    @Override
    public List<ScheduleHolidays> getListByMonth(String date) {
        return this.baseMapper.getListByMonth(date);
    }

    @Override
    public void importHolidayExcel(List<Map<Integer, String>> data, HttpServletRequest request) {
        log.info("{}条数据，开始存储数据库！", data.size() - 1);
        List<String> headerList = new ArrayList<>();
        for (int j = 0; j < data.size(); j++) {
            if(ObjectUtil.isEmpty(data.get(j).get(0)) && ObjectUtil.isEmpty(data.get(j).get(2)) && ObjectUtil.isEmpty(data.get(j).get(3))){
                Map<Integer, String> nullDate = data.get(j);
            }else{
                Map<Integer, String> integerStringMap = data.get(j);
                //处理表头
                if (j == 0) {
                    List<String> beforeHeard = new ArrayList<>();
                    beforeHeard.addAll(data.get(0).values());
                    headerList = getNotBlankHead(beforeHeard);
                    continue;
                }
                if (StringUtils.isBlank(integerStringMap.get(0)) && StringUtils.isBlank(integerStringMap.get(1)) && StringUtils.isBlank(integerStringMap.get(2)) && StringUtils.isBlank(integerStringMap.get(3)) && StringUtils.isBlank(integerStringMap.get(4)) && StringUtils.isBlank(integerStringMap.get(5))) {
                    continue;
                }

                saveHoliday(integerStringMap, j ,request);

                log.info("存储数据库成功！");
            }
        }
    }
    private List<String> getNotBlankHead(List<String> beforeHeard) {
        List<String> headerList = new ArrayList<>();
        for (String header : beforeHeard) {
            if (StringUtils.isNotBlank(header)) {
                headerList.add(header);
            }
        }
        return headerList;
    }
    //导入学生信息
    private void saveHoliday(Map<Integer, String> parMap, int rowNum , HttpServletRequest request) {
        try {
            ScheduleHolidays scheduleHoliday=new ScheduleHolidays();
            if (StringUtils.isEmpty(parMap.get(0))){
                throw new RuntimeException("第" + (rowNum+1) + "行节假日名称为空，请检查。");
            }else {
                scheduleHoliday.setName(parMap.get(0));
            }
            if (StringUtils.isEmpty(parMap.get(1))){
                throw new RuntimeException("第" + (rowNum+1) + "行日期为空，请检查。");
            }else {
                scheduleHoliday.setDate(DateUtils.str2Date(parMap.get(1),new SimpleDateFormat("yyyy-MM-dd")));
            }
            this.baseMapper.insert(scheduleHoliday);
        }catch (Exception e){
        }
    }
}
