package com.aiurt.modules.maplocation.mapper;

import java.util.List;

import com.aiurt.modules.maplocation.dto.AlarmRecordDTO;
import com.aiurt.modules.maplocation.entity.BdAlarmRecord;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: bd_alarm_record
 * @Author: jeecg-boot
 * @Date:   2021-05-07
 * @Version: V1.0
 */
public interface BdAlarmRecordMapper extends BaseMapper<BdAlarmRecord> {

    /**
     * 分页列表查询报警记录
     * @param pageList
     * @param bdAlarmRecord
     * @return
     */
    List<AlarmRecordDTO> listAlarmRecord(@Param("pageList") Page<AlarmRecordDTO> pageList, @Param("bdAlarmRecord") BdAlarmRecord bdAlarmRecord);
}
