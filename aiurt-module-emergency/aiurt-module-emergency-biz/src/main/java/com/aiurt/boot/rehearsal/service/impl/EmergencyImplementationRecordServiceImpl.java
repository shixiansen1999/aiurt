package com.aiurt.boot.rehearsal.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.aiurt.boot.rehearsal.dto.EmergencyRecordDTO;
import com.aiurt.boot.rehearsal.dto.EmergencyRehearsalRegisterDTO;
import com.aiurt.boot.rehearsal.entity.EmergencyImplementationRecord;
import com.aiurt.boot.rehearsal.entity.EmergencyRecordDept;
import com.aiurt.boot.rehearsal.entity.EmergencyRecordQuestion;
import com.aiurt.boot.rehearsal.entity.EmergencyRecordStep;
import com.aiurt.boot.rehearsal.mapper.EmergencyImplementationRecordMapper;
import com.aiurt.boot.rehearsal.service.IEmergencyImplementationRecordService;
import com.aiurt.boot.rehearsal.service.IEmergencyRecordDeptService;
import com.aiurt.boot.rehearsal.service.IEmergencyRecordQuestionService;
import com.aiurt.boot.rehearsal.service.IEmergencyRecordStepService;
import com.aiurt.boot.rehearsal.vo.EmergencyImplementationRecordVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description: emergency_implementation_record
 * @Author: aiurt
 * @Date: 2022-11-29
 * @Version: V1.0
 */
@Service
public class EmergencyImplementationRecordServiceImpl extends ServiceImpl<EmergencyImplementationRecordMapper, EmergencyImplementationRecord> implements IEmergencyImplementationRecordService {

    @Autowired
    private ISysBaseAPI iSysBaseApi;
    @Autowired
    private IEmergencyRecordDeptService emergencyRecordDeptService;
    @Autowired
    private IEmergencyRecordStepService emergencyRecordStepService;
    @Autowired
    private IEmergencyRecordQuestionService emergencyRecordQuestionService;
    @Autowired
    private EmergencyImplementationRecordMapper emergencyImplementationRecordMapper;

    @Override
    public String rehearsalRegister(EmergencyRehearsalRegisterDTO emergencyRehearsalRegisterDTO) {
        EmergencyImplementationRecord implementationRecord = new EmergencyImplementationRecord();
        BeanUtils.copyProperties(emergencyRehearsalRegisterDTO, implementationRecord);
        this.save(implementationRecord);

        String id = emergencyRehearsalRegisterDTO.getId();
        // 参与部门
        List<EmergencyRecordDept> depts = new ArrayList<>();
        List<String> orgCodes = emergencyRehearsalRegisterDTO.getOrgCodes();
        if (CollectionUtil.isNotEmpty(orgCodes)) {
            orgCodes.forEach(orgCode -> depts.add(new EmergencyRecordDept(null, id, orgCode)));
            emergencyRecordDeptService.saveBatch(depts);
        }
        // 演练步骤
        List<EmergencyRecordStep> steps = emergencyRehearsalRegisterDTO.getSteps();
        if (CollectionUtil.isNotEmpty(steps)) {
            steps.forEach(l -> l.setRecordId(id));
            emergencyRecordStepService.saveBatch(steps);
        }
        // 登记问题
        List<EmergencyRecordQuestion> questions = emergencyRehearsalRegisterDTO.getQuestions();
        if (CollectionUtil.isNotEmpty(questions)) {
            questions.forEach(l -> l.setRecordId(id));
            emergencyRecordQuestionService.saveBatch(questions);
        }
        return id;

    }

    @Override
    public IPage<EmergencyImplementationRecordVO> queryPageList(Page<EmergencyImplementationRecordVO> page, EmergencyRecordDTO emergencyRecordDTO) {
        IPage<EmergencyImplementationRecordVO> pageList = emergencyImplementationRecordMapper.queryPageList(page, emergencyRecordDTO);
        return pageList;
    }
}
