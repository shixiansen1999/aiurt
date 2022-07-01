package com.aiurt.boot.strategy.mapper;

import com.aiurt.boot.plan.entity.RepairPoolOrgRel;
import com.aiurt.boot.strategy.entity.InspectionStrOrgRel;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * @Description: inspection_str_org_rel
 * @Author: aiurt
 * @Date:   2022-06-22
 * @Version: V1.0
 */
public interface InspectionStrOrgRelMapper extends BaseMapper<InspectionStrOrgRel> {

    /**
     * 批量插入组织机构
     * @param repairPoolOrgRels
     */
    void insertBatch(List<RepairPoolOrgRel> repairPoolOrgRels);
}
