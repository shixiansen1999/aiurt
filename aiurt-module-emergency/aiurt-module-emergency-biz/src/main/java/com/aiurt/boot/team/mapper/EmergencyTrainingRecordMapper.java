package com.aiurt.boot.team.mapper;

import com.aiurt.boot.team.dto.EmergencyTrainingRecordDTO;
import com.aiurt.boot.team.entity.EmergencyTrainingRecord;
import com.aiurt.boot.team.vo.EmergencyCrewVO;
import com.aiurt.boot.team.vo.EmergencyTrainingRecordVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description: emergency_training_record
 * @Author: aiurt
 * @Date:   2022-11-29
 * @Version: V1.0
 */
public interface EmergencyTrainingRecordMapper extends BaseMapper<EmergencyTrainingRecord> {

    /**
     * 应急队伍训练记录列表查询
     * @param page
     * @param emergencyTrainingRecordDTO
     * @return
     */
    List<EmergencyTrainingRecordVO> queryPageList(Page<EmergencyTrainingRecordVO> page,@Param("emergencyTrainingRecordDTO")EmergencyTrainingRecordDTO emergencyTrainingRecordDTO);

    /**
     * 根据id查询
     * @param id
     * @return
     */
    EmergencyTrainingRecordVO queryById(String id);
    /**
     * 根据记录id查询培训人员
     * @param id
     * @return
     */
    List<EmergencyCrewVO> getTrainingCrews(String id);
}
