package com.aiurt.boot.team.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.boot.team.constant.TeamConstant;
import com.aiurt.boot.team.dto.EmergencyTrainingRecordDTO;
import com.aiurt.boot.team.entity.EmergencyTrainingRecord;
import com.aiurt.boot.team.mapper.EmergencyTrainingRecordMapper;
import com.aiurt.boot.team.service.IEmergencyTrainingRecordService;
import com.aiurt.boot.team.vo.EmergencyTrainingRecordVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.system.vo.SysDepartModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description: emergency_training_record
 * @Author: aiurt
 * @Date:   2022-11-29
 * @Version: V1.0
 */
@Service
public class EmergencyTrainingRecordServiceImpl extends ServiceImpl<EmergencyTrainingRecordMapper, EmergencyTrainingRecord> implements IEmergencyTrainingRecordService {

    @Autowired
    private ISysBaseAPI iSysBaseAPI;
    @Autowired
    private EmergencyTrainingRecordMapper emergencyTrainingRecordMapper;

    @Override
    public IPage<EmergencyTrainingRecordVO> queryPageList(EmergencyTrainingRecordDTO emergencyTrainingRecordDTO, Integer pageNo, Integer pageSize) {
        // 系统管理员不做权限过滤
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        String roleCodes = user.getRoleCodes();
        List<SysDepartModel> models = new ArrayList<>();
        List<String> orgCodes = new ArrayList<>();
        if (StrUtil.isNotBlank(roleCodes)) {
            if (!roleCodes.contains(TeamConstant.ADMIN)) {
                //获取用户的所属部门及所属部门子部门
                models = iSysBaseAPI.getUserDepartCodes();
                if (CollUtil.isEmpty(models)) {
                    return new Page<>();
                }
                orgCodes = models.stream().map(SysDepartModel::getOrgCode).collect(Collectors.toList());
                emergencyTrainingRecordDTO.setOrgCodeList(orgCodes);
            }
        }else {
            return new Page<>();
        }
        Page<EmergencyTrainingRecordVO> page = new Page<>(pageNo, pageSize);
        List<EmergencyTrainingRecordVO> result = emergencyTrainingRecordMapper.queryPageList(page, emergencyTrainingRecordDTO);
        page.setRecords(result);
        return page;
    }

    @Override
    public Result<EmergencyTrainingRecordVO> queryById(String id) {
        EmergencyTrainingRecordVO emergencyTrainingRecordVO = emergencyTrainingRecordMapper.queryById(id);
        emergencyTrainingRecordMapper.getTrainingCrews(id);
        return null;
    }
}
