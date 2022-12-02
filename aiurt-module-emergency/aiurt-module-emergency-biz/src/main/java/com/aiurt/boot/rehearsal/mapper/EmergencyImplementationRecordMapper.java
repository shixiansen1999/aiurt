package com.aiurt.boot.rehearsal.mapper;

import com.aiurt.boot.rehearsal.dto.EmergencyRecordDTO;
import com.aiurt.boot.rehearsal.entity.EmergencyImplementationRecord;
import com.aiurt.boot.rehearsal.vo.EmergencyImplementationRecordVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description: emergency_implementation_record
 * @Author: aiurt
 * @Date: 2022-11-29
 * @Version: V1.0
 */
public interface EmergencyImplementationRecordMapper extends BaseMapper<EmergencyImplementationRecord> {
    /**
     * 应急实施记录-分页列表查询
     *
     * @param page
     * @param emergencyRecordDTO
     * @return
     */
    IPage<EmergencyImplementationRecordVO> queryPageList(@Param("page") Page<EmergencyImplementationRecordVO> page
            , @Param("condition") EmergencyRecordDTO emergencyRecordDTO, @Param("orgCodes") List<String> orgCodes);

}
