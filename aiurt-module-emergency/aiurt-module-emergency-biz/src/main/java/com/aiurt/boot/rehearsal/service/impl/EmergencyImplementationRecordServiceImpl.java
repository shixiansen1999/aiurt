package com.aiurt.boot.rehearsal.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.aiurt.boot.rehearsal.constant.EmergencyConstant;
import com.aiurt.boot.rehearsal.dto.EmergencyDeptDTO;
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
import com.aiurt.common.exception.AiurtBootException;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.system.vo.SysDepartModel;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
        implementationRecord.setStatus(EmergencyConstant.RECORD_STATUS_1);
        this.save(implementationRecord);

        String id = implementationRecord.getId();
        // 参与部门
        List<EmergencyRecordDept> depts = new ArrayList<>();
        List<String> orgCodes = emergencyRehearsalRegisterDTO.getOrgCodes();
        if (CollectionUtil.isNotEmpty(orgCodes)) {
            orgCodes.forEach(orgCode -> {
                EmergencyRecordDept dept = new EmergencyRecordDept();
                dept.setRecordId(id);
                dept.setOrgCode(orgCode);
                depts.add(dept);
            });
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
        Map<String, String> orgMap = iSysBaseApi.getAllSysDepart().stream()
                .collect(Collectors.toMap(k -> k.getOrgCode(), v -> v.getDepartName(), (a, b) -> a));
        pageList.getRecords().stream().forEach(l -> {
            List<EmergencyRecordDept> recordDepts = emergencyRecordDeptService.lambdaQuery()
                    .eq(EmergencyRecordDept::getRecordId, l.getId()).list();
            if (CollectionUtil.isNotEmpty(recordDepts)) {
                List<EmergencyDeptDTO> depts = new ArrayList<>();
                recordDepts.forEach(d -> depts.add(new EmergencyDeptDTO(d.getOrgCode(), orgMap.get(d.getOrgCode()))));
                l.setDeptList(depts);
            }
        });
        return pageList;
    }

    @Override
    public boolean submit(String id, Integer status) {
        EmergencyImplementationRecord record = this.getById(id);
        Assert.notNull(record, "未找到对应数据！");
        if (EmergencyConstant.RECORD_STATUS_2.equals(record.getStatus())) {
            throw new AiurtBootException("记录已提交，无需重复提交！");
        } else if (EmergencyConstant.RECORD_STATUS_1.equals(record.getStatus())) {
            if (!EmergencyConstant.RECORD_STATUS_2.equals(status)) {
                throw new AiurtBootException("当前记录已经是待提交状态，不允许再更改为待提交状态！");
            }
            record.setStatus(status);
            boolean update = this.updateById(record);
            return update;
        } else {
            throw new AiurtBootException("数据存在异常！");
        }
    }
}
