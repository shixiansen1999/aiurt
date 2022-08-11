package com.aiurt.modules.workarea.mapper;

import com.aiurt.modules.workarea.entity.WorkAreaOrg;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * @Description: work_area_org
 * @Author: aiurt
 * @Date:   2022-08-11
 * @Version: V1.0
 */
public interface WorkAreaOrgMapper extends BaseMapper<WorkAreaOrg> {

    /**
     * 根据工区编码，获取组织机构名称
     * @param code
     * @return
     */
    List<String> getOrgName(String code);
}
