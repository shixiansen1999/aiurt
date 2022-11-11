package com.aiurt.modules.maplocation.service.impl;

import com.aiurt.modules.maplocation.dto.AlarmRecordDTO;
import com.aiurt.modules.maplocation.entity.BdAlarmRecord;
import com.aiurt.modules.maplocation.mapper.BdAlarmRecordMapper;
import com.aiurt.modules.maplocation.service.IBdAlarmRecordService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @Description: bd_alarm_record
 * @Author: jeecg-boot
 * @Date:   2021-05-07
 * @Version: V1.0
 */
@Service
public class BdAlarmRecordServiceImpl extends ServiceImpl<BdAlarmRecordMapper, BdAlarmRecord> implements IBdAlarmRecordService {

    // 分页列表查询报警记录
    @Override
    public Page<AlarmRecordDTO> listAlarmRecord(BdAlarmRecord bdAlarmRecord, Page<AlarmRecordDTO> pageList) {
        List<AlarmRecordDTO> recordDTOList = baseMapper.listAlarmRecord(pageList,bdAlarmRecord);
        return pageList.setRecords(recordDTOList);
    }

    @Override
    public ModelAndView exportXls(HttpServletRequest request, BdAlarmRecord bdAlarmRecord, Class<BdAlarmRecord> bdAlarmRecordClass, String bd_alarm_record) {
        return null;
    }


}
