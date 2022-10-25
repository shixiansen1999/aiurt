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

    /**
     * 站点信息
     * @param taskCode
     * @return
     */
    List<PatrolTaskStationDTO> selectStationByTaskCode(@Param("taskCode") String taskCode);

    /**
     * 获取线路列表
     * @return
     */
    List<LineDTO> getLineList();

    /**
     * 获取站点列表
     * @return
     */
    List<StationDTO> getStationList();

    /**
     * 获取位置列表
     * @return
     */
    List<StationPositionDTO> getStationPositionList();

    /**
     * 获取线路code
     * @param sc
     * @return
     */
    String getLineStaionCode(String sc);

    /**
     * 首页巡视异常任务的站点信息
     *
     * @param taskCode
     * @return
     */
    List<IndexStationDTO> getStationInfo(@Param("taskCode") String taskCode);
}
