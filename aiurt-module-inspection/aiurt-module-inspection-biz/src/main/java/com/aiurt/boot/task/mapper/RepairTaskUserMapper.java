package com.aiurt.boot.task.mapper;

import org.jeecg.common.system.vo.PortraitTaskModel;
import com.aiurt.boot.task.dto.RepairTaskUserNameDTO;
import com.aiurt.boot.task.entity.RepairTaskUser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description: repair_task_user
 * @Author: aiurt
 * @Date:   2022-06-22
 * @Version: V1.0
 */
public interface RepairTaskUserMapper extends BaseMapper<RepairTaskUser> {
    /**
     * 获取检修任务ID和用户名称列表
     *
     * @param repairTaskIds 检修任务id列表
     * @return 检修任务ID和用户名称列表的DTO对象
     */
    List<RepairTaskUserNameDTO> selectTaskIdWithUserNames(@Param("repairTaskIds") List<String> repairTaskIds);

    /**
     * 批量插入
     * @param repairTaskUserList
     * @return
     */
    int batchInsert(List<RepairTaskUser> repairTaskUserList);

    /**
     * 获取近五年的检修任务数据
     *
     * @param userId
     * @param fiveYearsAgo
     * @param thisYear
     * @param completed
     * @return
     */
    List<PortraitTaskModel> getInspectionNumber(@Param("userId") String userId,
                                                @Param("fiveYearsAgo") int fiveYearsAgo,
                                                @Param("thisYear") int thisYear,
                                                @Param("completed") Integer completed);

    /**
     * 通过检修单code获取检修人username，用逗号拼接
     * @param repairTaskCode
     * @return
     */
    String selectUserNameByRepairTaskCode(String repairTaskCode);
}
