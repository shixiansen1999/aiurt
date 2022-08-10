package com.aiurt.modules.worklog.mapper;

import com.aiurt.modules.worklog.entity.WorkLogRemind;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author cgkj0
 * @version 1.0
 * @date 2022/7/20
 * @desc
 */
public interface WorkLogRemindMapper extends BaseMapper<WorkLogRemind> {
    /**
     * 根据当前时间和组织，查询当天部门上班的人员
     * @param dateNow
     * @param orgId
     * @return
     */
    List<String> getOrgUserTodayWork(@Param("dateNow") String dateNow,@Param("orgId") String orgId);
}