package com.aiurt.boot.modules.repairManage.mapper;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import cn.hutool.core.date.DateTime;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.aiurt.boot.modules.statistical.vo.StatisticsRepairVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.aiurt.boot.modules.statistical.vo.RepairTaskVo;
import com.aiurt.boot.modules.statistical.vo.UserAndAmountVO;
import org.apache.ibatis.annotations.Param;
import com.aiurt.boot.modules.repairManage.entity.RepairTask;
import org.apache.ibatis.annotations.Select;

/**
 * @Description: 检修单列表
 * @Author: swsc
 * @Date: 2021-09-16
 * @Version: V1.0
 */
public interface RepairTaskMapper extends BaseMapper<RepairTask> {

    @Select("select count(*) from repair_task where create_time >= #{startTime} and create_time <= #{endTime}")
    Integer getRepairTaskAmount(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    List<UserAndAmountVO> getStatistical(@Param("startTime") String startTime, @Param("endTime") String endTime,
                                         @Param("userName") String userName, @Param("userNameList") List<String> userNameList);

    /**
     * 查询各班组检修统计数据
     * @param startTime
     * @param endTime
     * @param lineCode
     * @return
     */
    List<StatisticsRepairVO> getRepairCountGroupByOrg(@Param("startTime") Date startTime, @Param("endTime") Date endTime, @Param("lineCode") String lineCode);

    IPage<RepairTaskVo> getRepairTaskVos(IPage<RepairTaskVo> page, @Param("lineCode") String lineCode, @Param("startTime") Date startTime, @Param("endTime") Date endTime);

    List<RepairTaskVo> getRepairTaskVos2(@Param("lineCode") String lineCode, @Param("startTime") Date startTime, @Param("endTime") Date endTime);

    List<RepairTaskVo> getRepairTaskVosByTime(@Param("lineCode") String lineCode, @Param("startTime") Date startTime, @Param("endTime") Date endTime);

    Integer getCompleteRepairNum(@Param("lineCode") String lineCode, @Param("startTime") Date startTime, @Param("endTime") Date endTime);

    @Select("select count(*) from repair_task where submit_time between #{startTime} and #{endTime}")
    Integer getRepairTaskNum(@Param("startTime") Date startTime, @Param("endTime") Date endTime);

    List<RepairTaskVo> getCompletedRepair(String lineCode, DateTime startTime, DateTime endTime);

    List<RepairTaskVo> getUncompletedRepair(String lineCode, DateTime startTime, DateTime endTime);
}
