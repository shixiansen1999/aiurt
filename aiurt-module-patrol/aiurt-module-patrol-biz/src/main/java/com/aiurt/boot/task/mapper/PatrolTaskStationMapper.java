package com.aiurt.boot.task.mapper;

import com.aiurt.boot.standard.dto.LineDTO;
import com.aiurt.boot.standard.dto.StationDTO;
import com.aiurt.boot.standard.dto.StationPositionDTO;
import com.aiurt.boot.statistics.dto.IndexStationDTO;
import com.aiurt.boot.task.dto.PatrolTaskStationDTO;
import com.aiurt.boot.task.entity.PatrolTaskStation;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description: patrol_task_station
 * @Author: aiurt
 * @Date: 2022-06-27
 * @Version: V1.0
 */
public interface PatrolTaskStationMapper extends BaseMapper<PatrolTaskStation> {

    List<PatrolTaskStationDTO> selectStationByTaskCode(@Param("taskCode") String taskCode);

    List<LineDTO> getLineList();

    List<StationDTO> getStationList();

    List<StationPositionDTO> getStationPositionList();

    String getLineStaionCode(String sc);

    /**
     * 首页巡视异常任务的站点信息
     *
     * @param taskCode
     * @return
     */
    List<IndexStationDTO> getStationInfo(@Param("taskCode") String taskCode);
}
