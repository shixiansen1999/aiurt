package com.aiurt.boot.plan.mapper;

import com.aiurt.boot.index.dto.MapDTO;
import com.aiurt.boot.plan.entity.RepairPoolOrgRel;
import com.aiurt.common.aspect.annotation.EnableDataPerm;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.Collection;
import java.util.List;

/**
 * @Description: repair_pool_org_rel
 * @Author: aiurt
 * @Date:   2022-06-22
 * @Version: V1.0
 */
@EnableDataPerm
public interface RepairPoolOrgRelMapper extends BaseMapper<RepairPoolOrgRel> {
    /**
     * 根据code查询检修任务对应的组织机构编码
     *
     * @param planCodes
     * @return
     */
    List<MapDTO> selectOrgByCode(List<String> planCodes);
}
