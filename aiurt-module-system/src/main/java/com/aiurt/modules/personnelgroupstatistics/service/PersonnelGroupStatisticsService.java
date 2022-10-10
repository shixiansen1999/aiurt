package com.aiurt.modules.personnelgroupstatistics.service;

import com.aiurt.modules.personnelgroupstatistics.model.PersonnelGroupModel;
import com.aiurt.modules.personnelgroupstatistics.model.TeamPortraitModel;
import com.aiurt.modules.personnelgroupstatistics.model.TeamUserModel;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;

/**
 * @author lkj
 */
public interface PersonnelGroupStatisticsService {
    /**
     * 班组统计-查询
     * @param departIds
     * @param startTime
     * @param endTime
     * @return List<PersonnelGroupModel>
     */
    List<PersonnelGroupModel> queryGroupPageList(List<String> departIds, String startTime, String endTime, Page<PersonnelGroupModel> page);

    /**
     * 人员统计-查询
     * @param departIds
     * @param startTime
     * @param endTime
     * @return List<PersonnelGroupModel>
     */
    List<PersonnelGroupModel> queryUserPageList(List<String> departIds, String startTime, String endTime, Page<PersonnelGroupModel> page);

    TeamPortraitModel queryGroupById(String departId);

    TeamUserModel queryUserById(String userId);


}
