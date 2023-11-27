package com.aiurt.boot.task.service;

import com.aiurt.boot.manager.dto.EquipmentOverhaulDTO;
import com.aiurt.boot.manager.dto.ExamineDTO;
import com.aiurt.boot.manager.dto.MajorDTO;
import com.aiurt.boot.manager.dto.OrgDTO;
import com.aiurt.boot.task.dto.*;
import com.aiurt.boot.task.entity.RepairTask;
import com.aiurt.boot.task.entity.RepairTaskDeviceRel;
import com.aiurt.boot.task.entity.RepairTaskEnclosure;
import com.aiurt.common.api.dto.message.MessageDTO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.common.api.vo.Result;

import java.util.List;

/**
 * @Description: repair_task
 * @Author: aiurt
 * @Date: 2022-06-22
 * @Version: V1.0
 */
public interface IRepairTaskService extends IService<RepairTask> {

    /**
     * 检修任务列表查询
     *
     * @param pageList  分页查询条件
     * @param condition 查询条件
     * @return 分页的检修任务列表
     */
    Page<RepairTask> selectables(Page<RepairTask> pageList, RepairTask condition);

    /**
     * 检修任务清单查询
     *
     * @param pageList
     * @param condition
     * @return
     */
    Page<RepairTaskDTO> selectTasklet(Page<RepairTaskDTO> pageList, RepairTaskDTO condition);

    /**
     * 检修清单列表
     * @param taskId
     * @return
     */
    List<RepairTaskDTO> selectTaskList( String taskId,String stationCode);

    /**
     * 站点下拉列表
     * @param taskId
     * @return
     */
    List<RepairTaskStationDTO> repairTaskStationList(String taskId);

    /**
     * 检修任务详情
     * @param taskId
     * @param stationCode
     * @param deviceId
     * @return
     */
    CheckListDTO selectRepairTaskInfo( String taskId,String stationCode,String deviceId);

    /**
     * 设备台账-检修履历
     *
     * @param pageList
     * @param condition
     * @return
     */
    Page<RepairTaskDTO> repairSelectTaskletForDevice(Page<RepairTaskDTO> pageList, RepairTaskDTO condition);


    /**
     * 查询专业，专业子系统的信息
     *
     * @param id
     * @return
     */
    List<MajorDTO> selectMajorCodeList(String id);


    /**
     * 查询专业，专业子系统的信息
     *
     * @param id
     * @param majorCode
     * @param subsystemCode
     * @return
     */
    EquipmentOverhaulDTO selectEquipmentOverhaulList(String id, String majorCode, String subsystemCode);

    /**
     * 查询检修单信息
     *
     * @param id
     * @param code
     * @return
     */
    CheckListDTO selectCheckList(String id, String code);

    /**
     * 审核
     *
     * @param examineDTO
     */
    void toExamine(ExamineDTO examineDTO);

    /**
     * 待执行-执行
     *
     * @param examineDTO
     */
    void toBeImplement(ExamineDTO examineDTO);

    /**
     * 执行中-执行
     *
     * @param examineDTO
     */
    void inExecution(ExamineDTO examineDTO);

    /**
     * 验收
     *
     * @param examineDTO
     */
    void acceptance(ExamineDTO examineDTO);

    /**
     * 查询附件息
     *
     * @param resultId
     * @return
     */
    List<RepairTaskEnclosure> selectEnclosure(String resultId);

    /**
     * 待确认退回任务
     *
     * @param examineDTO
     */
    void confirmedDelete(ExamineDTO examineDTO);

    /**
     * 领取检修任务
     *
     * @param id
     * @return
     */
    void receiveTask(String id);
    /**
     * 领取检修任务
     *
     * @param examineDTO
     * @return
     */
    String receiveTask(ExamineDTO examineDTO);



    /**
     * 填写检修工单
     *
     * @param monadDTO
     */
    void writeMonad(WriteMonadDTO monadDTO);

    /**
     * 填写检修单上的同行人
     *
     * @param code   检修单code
     * @param peerId 同行人ids
     */
    void writePeerPeople(String code, String peerId);

    void writeSampling(String code, String samplingId);

    /**
     * 填写检修单上的检修位置
     *
     * @param id               检修单id
     * @param specificLocation 检修位置
     * @return
     */
    void writeLocation(String id, String specificLocation);

    /**
     * 提交检修工单
     *
     * @param id 检修单id
     * @return
     */
    void submitMonad(String id, String samplingSignUrl);

    /**
     * 检修单同行人下拉
     * @param id
     */
    List<OrgDTO> queryPeerList(String id);

    /**
     * 确认检修任务
     * @param examineDTO
     */
    void confirmTask(ExamineDTO examineDTO);

    /**
     * 扫码设备查询检修单
     * @param taskId 检修任务id
     * @param deviceCode 设备编码
     * @return
     */
    List<RepairTaskDeviceRel> scanCodeDevice(String taskId, String deviceCode);


    /**
     * 检修消息发送
     *
     * @param messageDTO
     * @param usernames
     * @param username
     * @param repairTaskMessageDTO
     */
    void sendMessage(MessageDTO messageDTO, String usernames, String username, RepairTaskMessageDTO repairTaskMessageDTO);


    /**
     * 统计运维系统数据
     * @return
     */
    List<SystemInformationDTO> getSystemInformation();

    /**
     * 归档
     * @param data 归档数据
     * @return 返回结果
     */
    Result<?> archiveRepair(List<RepairTaksArchDTO> data);

    /**
     * 检修任务表-打印检修详情
     * @param ids
     * @param overhaulCode
     * @return
     */
    List<PrintRepairTaskDTO> printRepairTaskById(String ids,String overhaulCode);

    /**
     * 根据检修任务id获取需要签名的人员列表
     * @param taskId 检修任务id
     * @return 返回SignUserDTO列表
     */
    List<SignUserDTO> appGetSignUserList(String taskId);

    /**
     * 打印
     *
     * @param ids
     * @param code
     * @param deviceId
     * @return
     */
    String printTask(String ids, String code, String deviceId);
}
