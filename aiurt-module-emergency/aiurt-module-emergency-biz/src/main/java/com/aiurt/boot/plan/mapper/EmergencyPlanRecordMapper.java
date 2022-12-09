package com.aiurt.boot.plan.mapper;

import com.aiurt.boot.plan.dto.EmergencyPlanRecordDTO;
import com.aiurt.boot.plan.dto.EmergencyPlanRecordQueryDTO;
import com.aiurt.boot.plan.entity.EmergencyPlanRecord;
import com.aiurt.boot.plan.vo.EmergencyPlanRecordVO;
import com.aiurt.boot.rehearsal.dto.EmergencyRecordDTO;
import com.aiurt.boot.rehearsal.vo.EmergencyImplementationRecordVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description: emergency_plan_record
 * @Author: aiurt
 * @Date:   2022-11-29
 * @Version: V1.0
 */
public interface EmergencyPlanRecordMapper extends BaseMapper<EmergencyPlanRecord> {
    /**
     * 应急预案启动记录分页列表
     * @param page
     * @param emergencyPlanRecordQueryDTO
     * @param orgCodes
     * @return
     */
    IPage<EmergencyPlanRecordVO> queryPageList(@Param("page") Page<EmergencyPlanRecordVO> page
            , @Param("condition") EmergencyPlanRecordQueryDTO emergencyPlanRecordQueryDTO, @Param("orgCodes") List<String> orgCodes);

}
