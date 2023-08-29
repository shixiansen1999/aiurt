package com.aiurt.boot.rehearsal.service;

import com.aiurt.boot.rehearsal.dto.EmergencyRecordDTO;
import com.aiurt.boot.rehearsal.dto.EmergencyRehearsalRegisterDTO;
import com.aiurt.boot.rehearsal.entity.EmergencyImplementationRecord;
import com.aiurt.boot.rehearsal.vo.EmergencyImplementationRecordVO;
import com.aiurt.boot.rehearsal.vo.EmergencyRecordReadOneVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.system.vo.SysDeptUserModel;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * @Description: emergency_implementation_record
 * @Author: aiurt
 * @Date: 2022-11-29
 * @Version: V1.0
 */
public interface IEmergencyImplementationRecordService extends IService<EmergencyImplementationRecord> {
    /**
     * 应急实施记录-演练登记
     *
     * @param emergencyRehearsalRegisterDTO
     */
    String rehearsalRegister(EmergencyRehearsalRegisterDTO emergencyRehearsalRegisterDTO);

    /**
     * 应急实施记录-分页列表查询
     *
     * @param page
     * @param emergencyRecordDTO
     * @return
     */
    IPage<EmergencyImplementationRecordVO> queryPageList(Page<EmergencyImplementationRecordVO> page, EmergencyRecordDTO emergencyRecordDTO);

    /**
     * 应急实施记录-提交(将记录更新为已提交)
     *
     * @param id
     * @param status
     * @return
     */
    boolean submit(String id, Integer status);

    /**
     * 应急实施记录-通过id删除
     *
     * @param id
     * @return
     */
    void delete(String id);

    /**
     * 应急实施记录-编辑
     *
     * @param emergencyRehearsalRegisterDTO
     */
    void edit(EmergencyRehearsalRegisterDTO emergencyRehearsalRegisterDTO);

    /**
     * 应急实施记录-通过id查询
     *
     * @param id
     * @return
     */
    EmergencyRecordReadOneVO queryById(String id);

    /**
     * 应急模块-责任部门和用户联动信息
     */
    List<SysDeptUserModel> getDeptUserGanged();

    /**
     * 应急模块-责任人信息
     */
    List<LoginUser> getDutyUser();

    /**
     * 导出闭环台账
     * @param emergencyRecordDTO
     * @param pageNo
     * @param pageSize
     * @param request
     * @param response
     */
    void exportLedger(EmergencyRecordDTO emergencyRecordDTO, Integer pageNo, Integer pageSize, HttpServletRequest request, HttpServletResponse response);

    /**
     * 批量打印
     * @param ids
     * @return
     */
    List<EmergencyRecordReadOneVO> printEmergency(String ids);
}
