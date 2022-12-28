package com.aiurt.boot.team.service;

import com.aiurt.boot.team.dto.EmergencyTrainingProgramDTO;
import com.aiurt.boot.team.dto.EmergencyTrainingRecordDTO;
import com.aiurt.boot.team.entity.EmergencyTrainingProgram;
import com.aiurt.boot.team.entity.EmergencyTrainingRecord;
import com.aiurt.boot.team.vo.EmergencyTrainingRecordVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.common.api.vo.Result;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Description: emergency_training_record
 * @Author: aiurt
 * @Date:   2022-11-29
 * @Version: V1.0
 */
public interface IEmergencyTrainingRecordService extends IService<EmergencyTrainingRecord> {

    /**
     * 应急队伍训练记录列表查询
     * @param emergencyTrainingRecordDTO
     * @param pageNo
     * @param pageSize
     * @return
     */
    IPage<EmergencyTrainingRecordVO> queryPageList(EmergencyTrainingRecordDTO emergencyTrainingRecordDTO, Integer pageNo, Integer pageSize);
    /**
     * 根据id查询
     * @param id
     * @return
     */
    Result<EmergencyTrainingRecordVO> queryById(String id);
    /**
     * 添加
     * @param emergencyTrainingRecord
     * @return
     */
    Result<String> add(EmergencyTrainingRecord emergencyTrainingRecord);
    /**
     * 编辑
     * @param emergencyTrainingRecord
     * @return
     */
    Result<String> edit(EmergencyTrainingRecord emergencyTrainingRecord);
    /**
     * 提交
     * @param emergencyTrainingRecord
     * @return
     */
    void submit(EmergencyTrainingRecord emergencyTrainingRecord);
    /**
     * 删除
     * @param id
     * @return
     */
    void delete(String id);
    /**
     * 根据应急队伍选择训练计划
     * @param emergencyTrainingProgramDTO
     * @param pageNo
     * @param pageSize
     * @return
     */
    IPage<EmergencyTrainingProgram>  getTrainingProgram(EmergencyTrainingProgramDTO emergencyTrainingProgramDTO,Integer pageNo, Integer pageSize);

    /**
     * 导入
     * @param request
     * @param response
     * @return
     */
    Result<?> importExcel(HttpServletRequest request, HttpServletResponse response);
    /**
     * 导出
     * @param response
     * @param id
     * @return
     */
    void exportXls(HttpServletResponse response, String id);
}
