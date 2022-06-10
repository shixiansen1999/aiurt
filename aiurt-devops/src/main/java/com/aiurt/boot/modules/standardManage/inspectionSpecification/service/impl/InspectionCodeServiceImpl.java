package com.aiurt.boot.modules.standardManage.inspectionSpecification.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.aiurt.boot.common.constant.CommonConstant;
import com.aiurt.boot.modules.repairManage.service.impl.RepairPoolServiceImpl;
import com.aiurt.boot.modules.standardManage.inspectionSpecification.entity.InspectionCode;
import com.aiurt.boot.modules.standardManage.inspectionSpecification.mapper.InspectionCodeMapper;
import com.aiurt.boot.modules.standardManage.inspectionSpecification.service.IInspectionCodeService;
import com.aiurt.boot.modules.standardManage.inspectionStrategy.entity.InspectionCodeContent;
import com.aiurt.boot.modules.standardManage.inspectionStrategy.mapper.InspectionCodeContentMapper;
import com.aiurt.boot.modules.standardManage.safetyPrecautions.mapper.SafetyPrecautionsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @Description: 检修标准管理
 * @Author: swsc
 * @Date: 2021-09-14
 * @Version: V1.0
 */
@Service
public class InspectionCodeServiceImpl extends ServiceImpl<InspectionCodeMapper, InspectionCode> implements IInspectionCodeService {

    @Autowired
    private RepairPoolServiceImpl repairPoolServiceImpl;

    @Resource
    private SafetyPrecautionsMapper safetyPrecautionsMapper;

    @Resource
    private InspectionCodeContentMapper inspectionCodeContentMapper;

    @Override
    public Result copy(String ids) {
        String[] split = ids.split(",");
        if (split.length == 1) {
            //查询记录
            InspectionCode select = this.baseMapper.selectById(ids);
            if (select == null) {
                return Result.error("请选择一条正确记录");
            }
            InspectionCode inspectionCode = select;
            this.copyRecordById(inspectionCode);
            return Result.ok();
        }
        for (String id : split) {
            //查询记录
            InspectionCode select = this.baseMapper.selectById(id);
            if (select == null) {
                return Result.error("请选择一条正确记录");
            }
            InspectionCode inspectionCode = select;
            this.copyRecordById(inspectionCode);
        }
        return Result.ok();
    }

    @Override
    public Result addAnnualPlan(String id) throws Exception {
        InspectionCode inspectionCode = this.getEffectInspectionCode(id);
        if (inspectionCode == null) {
            return Result.error("非法数据");
        }
        //查询是否有未配置的策略
        List<InspectionCodeContent> inspectionCodeContentList = inspectionCodeContentMapper.selectListById(id);
        if (inspectionCodeContentList.size() == 0) {
            return Result.error("请先完成所有检修策略的设置");
        }
        for (InspectionCodeContent inspectionCodeContent : inspectionCodeContentList) {
            if (inspectionCodeContent.getTactics() == null) {
                return Result.error("检修规范:".concat(inspectionCode.getTitle()).concat(",有未设置的策略"));
            }
        }
        Result result = repairPoolServiceImpl.generateTask(inspectionCode);
        if (!result.getCode().equals(CommonConstant.SC_OK_200)) {
            return result;
        }
        inspectionCode.setGenerateStatus(1);
        this.baseMapper.updateById(inspectionCode);
        return Result.ok();
    }

    @Override
    public Result addAnnualNewPlan(String id) throws Exception {
        InspectionCode inspectionCode = this.getEffectInspectionCode(id);
        if (inspectionCode == null) {
            return Result.error("非法数据");
        }
        return repairPoolServiceImpl.generateReNewTask(inspectionCode);
    }

    /**
     * 获取已生效的记录
     *
     * @param id
     * @return
     */
    private InspectionCode getEffectInspectionCode(String id) {
        QueryWrapper<InspectionCode> wrapper = new QueryWrapper<>();
        wrapper.eq("id", id).eq("del_flag", 0).eq("status", 1);
        return this.baseMapper.selectOne(wrapper);
    }

    /**
     * 复制检修规范
     */
    private void copyRecordById(InspectionCode inspectionCode) {
        //复制记录 修改标题和创建时间
        inspectionCode.setId(null);
        inspectionCode.setTitle(inspectionCode.getTitle().concat("-").concat("copy"));
        inspectionCode.setStatus(0);
        inspectionCode.setGenerateStatus(0);
        inspectionCode.setCreateTime(new Date());
        inspectionCode.setUpdateTime(new Date());
        this.baseMapper.insert(inspectionCode);
    }
}
