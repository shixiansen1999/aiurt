package com.aiurt.boot.standard.mapper;

import com.aiurt.boot.standard.dto.OrgVO;
import com.aiurt.boot.standard.entity.PatrolStandardOrg;
import com.aiurt.common.aspect.annotation.EnableDataPerm;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description: inspection_str_org_rel
 * @Author: aiurt
 * @Date:   2022-06-22
 * @Version: V1.0
 */
@EnableDataPerm(excluseMethodName = {"getOrgList"})
public interface PatrolStandardOrgMapper extends BaseMapper<PatrolStandardOrg> {
    /**
     * 获取部门信息
     * @param orgRelList
     * @return
     */
    List<OrgVO> getOrgList(@Param("orgRelList") List<PatrolStandardOrg> orgRelList);

    /**
     * 通过用户部门权限获取标准编号
     *
     * @return
     */
    List<String> getStandardCodeByUserOrg();
}
