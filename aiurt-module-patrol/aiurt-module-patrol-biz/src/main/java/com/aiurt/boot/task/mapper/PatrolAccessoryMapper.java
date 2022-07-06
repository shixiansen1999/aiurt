package com.aiurt.boot.task.mapper;

import com.aiurt.boot.task.dto.PatrolAccessoryDTO;
import com.aiurt.boot.task.entity.PatrolAccessory;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description: patrol_accessory
 * @Author: aiurt
 * @Date:   2022-06-21
 * @Version: V1.0
 */
public interface PatrolAccessoryMapper extends BaseMapper<PatrolAccessory> {


    /**
     * app-获取这个单号下一个巡检项的所以附件
     * @param taskDeviceId
     * @param checkResultId
     * @return
     */
    List<PatrolAccessoryDTO> getAllAccessory(@Param("taskDeviceId") String taskDeviceId, @Param("checkResultId")String checkResultId);

    /**
     * app-获取这个单号的所有巡检项 的附件
     * @param id
     * @return
     */
    List<PatrolAccessoryDTO> getCheckAllAccessory(String id);
}
