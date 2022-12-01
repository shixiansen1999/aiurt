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
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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
import org.springframework.transaction.annotation.Transactional;
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
    @Transactional(rollbackFor = Exception.class)
    public String rehearsalRegister(EmergencyRehearsalRegisterDTO emergencyRehearsalRegisterDTO) {
        EmergencyImplementationRecord implementationRecord = new EmergencyImplementationRecord();
        BeanUtils.copyProperties(emergencyRehearsalRegisterDTO, implementationRecord);
        implementationRecord.setStatus(EmergencyConstant.RECORD_STATUS_1);
        this.save(implementationRecord);

        String id = implementationRecord.getId();
        // 添加记录的部门、问题、步骤等关联信息
        addAssociatedInfo(id, emergencyRehearsalRegisterDTO);
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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(String id) {
        EmergencyImplementationRecord record = this.getById(id);
        Assert.notNull(record, "未找到对应数据！");
        if (EmergencyConstant.RECORD_STATUS_2.equals(record.getStatus())) {
            throw new AiurtBootException("记录已提交，不允许删除！");
        }
        this.removeById(id);
        // 根据实施记录的ID删除记录的部门、问题、步骤等关联信息
        deleteAssociatedInfo(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void edit(EmergencyRehearsalRegisterDTO emergencyRehearsalRegisterDTO) {
        String id = emergencyRehearsalRegisterDTO.getId();
        Assert.notNull(emergencyRehearsalRegisterDTO.getId(), "记录ID为空！");
        EmergencyImplementationRecord record = this.getById(id);
        Assert.notNull(record, "未找到对应数据！");
        if (EmergencyConstant.RECORD_STATUS_2.equals(record.getStatus())) {
            throw new AiurtBootException("记录已经提交，不允许更改！");
        }
        BeanUtils.copyProperties(emergencyRehearsalRegisterDTO, record);
        record.setStatus(EmergencyConstant.RECORD_STATUS_1);
        this.updateById(record);

        // 根据实施记录的ID删除记录的部门、问题、步骤等关联信息
        deleteAssociatedInfo(id);
        // 添加记录的部门、问题、步骤等关联信息
        addAssociatedInfo(id, emergencyRehearsalRegisterDTO);
    }

    /**
     * 添加记录的部门、问题、步骤等关联信息
     *
     * @param id
     * @param emergencyRehearsalRegisterDTO
     */
    private void addAssociatedInfo(String id, EmergencyRehearsalRegisterDTO emergencyRehearsalRegisterDTO) {
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
    }

    /**
     * 根据实施记录的ID删除记录的部门、问题、步骤等关联信息
     *
     * @param recordId
     */
    private void deleteAssociatedInfo(String recordId) {
        // 删除关联的部门
        emergencyRecordDeptService.remove(new LambdaQueryWrapper<EmergencyRecordDept>().eq(EmergencyRecordDept::getRecordId, recordId));
        // 删除关联的问题
        emergencyRecordQuestionService.remove(new LambdaQueryWrapper<EmergencyRecordQuestion>().eq(EmergencyRecordQuestion::getRecordId, recordId));
        // 删除关联的步骤
        emergencyRecordStepService.remove(new LambdaQueryWrapper<EmergencyRecordStep>().eq(EmergencyRecordStep::getRecordId, recordId));

    }
}
