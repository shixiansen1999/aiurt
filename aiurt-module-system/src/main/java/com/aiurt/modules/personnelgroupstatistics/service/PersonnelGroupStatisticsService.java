package com.aiurt.modules.personnelgroupstatistics.service;

import com.aiurt.modules.personnelgroupstatistics.model.GroupModel;
import com.aiurt.modules.personnelgroupstatistics.model.PersonnelModel;
import com.aiurt.modules.personnelgroupstatistics.model.TeamPortraitModel;
import com.aiurt.modules.personnelgroupstatistics.model.TeamUserModel;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
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
    Page<GroupModel> queryGroupPageList(List<String> departIds, String startTime, String endTime, Page<GroupModel> page);

    /**
     * 人员统计-查询
     * @param departIds
     * @param startTime
     * @param endTime
     * @return List<PersonnelGroupModel>
     */
    Page<PersonnelModel> queryUserPageList(List<String> departIds, String startTime, String endTime, Page<PersonnelModel> page);

    TeamPortraitModel queryGroupById(String departId);

    TeamUserModel queryUserById(String userId);

    ModelAndView reportGroupExport(HttpServletRequest request, String startTime, String endTime,String exportField);

    ModelAndView reportUserExport(HttpServletRequest request,String startTime, String endTime,String exportField);


}
