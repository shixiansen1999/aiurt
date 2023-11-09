package com.aiurt.boot.task.mapper;

import com.aiurt.boot.task.dto.PatrolAbnormalDeviceDTO;
import com.aiurt.boot.task.dto.PatrolCheckResultDTO;
import com.aiurt.boot.task.entity.PatrolCheckResult;
import com.aiurt.boot.task.param.PatrolTaskDeviceParam;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description: patrol_check_result
 * @Author: aiurt
 * @Date: 2022-06-21
 * @Version: V1.0
 */
public interface PatrolCheckResultMapper extends BaseMapper<PatrolCheckResult> {
    /**
     * 根据任务设备表id查找巡检结果
     * @param taskDeviceId
     * @return
     */
    List<PatrolCheckResultDTO> getListByTaskDeviceId(@Param("taskDeviceId") String taskDeviceId);

    /**
     * 批量添加巡检结果
     * @param resultList
     * @return
     */
    int addResultList(@Param("list") List<PatrolCheckResult> resultList);

    /**
     *app-获取巡检设备检查结果
     * @param taskDeviceId
     * @param taskDeviceId
     * @return
     */
    List<PatrolCheckResultDTO> getCheckResult(String taskDeviceId);
    /**
     * 根据工单id查询异常设备
     * @param taskDeviceId 工单id
     * @return
     */
    List<PatrolAbnormalDeviceDTO> queryAbnormalDevices(@Param("taskDeviceId") String taskDeviceId);
    /**
     * 根据任务设备表id查找巡检结果(只找是巡检项目)
     * @param taskDeviceId
     * @return
     */
    List<PatrolCheckResultDTO> getCheckByTaskDeviceId(@Param("taskDeviceId") String taskDeviceId);
    /**
     * 根据任务设备表id查找巡检结果父级
     * @param collect
     * @return
     */
    List<PatrolCheckResultDTO> getCheckByTaskDeviceIdAndParent(@Param("collect") List<String> collect);

    /**
     * 根据任务设备表id查找巡检结果父级子级
     * @param collect
     * @param parentId
     * @return
     */
    List<PatrolCheckResultDTO> getQualityStandard(@Param("collect") List<String> collect, @Param("parentId") String parentId);

    /**
     * 根据任务设备表id查找巡检结果
     * @param collect
     * @return
     */
    List<PatrolCheckResultDTO> getCheckResultAllByTaskId(@Param("collect") List<String> collect);


    /**
     * 获取正常项数量，异常项数量
     * @param patrolNumbers 巡检单号集合
     * @return
     */
    List<PatrolTaskDeviceParam> getNum(@Param("patrolNumbers")  List<String> patrolNumbers);

}
