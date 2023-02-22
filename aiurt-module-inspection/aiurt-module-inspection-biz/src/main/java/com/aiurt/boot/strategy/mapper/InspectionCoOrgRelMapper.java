package com.aiurt.boot.strategy.mapper;

import com.aiurt.boot.manager.dto.OrgVO;
import com.aiurt.boot.strategy.entity.InspectionCoOrgRel;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description: inspection_str_org_rel
 * @Author: aiurt
 * @Date:   2022-06-22
 * @Version: V1.0
 */
public interface InspectionCoOrgRelMapper extends BaseMapper<InspectionCoOrgRel> {
    /**
     * 获取部门信息
     * @param orgRelList
     * @return
     */
    List<OrgVO> getOrgList(@Param("orgRelList") List<InspectionCoOrgRel> orgRelList);
}
