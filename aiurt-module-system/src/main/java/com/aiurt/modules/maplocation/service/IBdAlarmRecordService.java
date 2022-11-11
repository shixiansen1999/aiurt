package com.aiurt.modules.maplocation.service;

import com.aiurt.modules.maplocation.dto.AlarmRecordDTO;
import com.aiurt.modules.maplocation.entity.BdAlarmRecord;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

/**
 * @Description: bd_alarm_record
 * @Author: jeecg-boot
 * @Date:   2021-05-07
 * @Version: V1.0
 */
public interface IBdAlarmRecordService extends IService<BdAlarmRecord> {
    // 分页列表查询
    Page<AlarmRecordDTO> listAlarmRecord(BdAlarmRecord bdAlarmRecord, Page<AlarmRecordDTO> pageList);

    /**
     * 导出
     * @param request
     * @param bdAlarmRecord
     * @param bdAlarmRecordClass
     * @param bd_alarm_record
     * @return
     */
    ModelAndView exportXls(HttpServletRequest request, BdAlarmRecord bdAlarmRecord, Class<BdAlarmRecord> bdAlarmRecordClass, String bd_alarm_record);
}
