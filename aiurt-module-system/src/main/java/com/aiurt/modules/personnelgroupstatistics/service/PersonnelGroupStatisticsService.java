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
     * 班组统计
     *
     * @param departIds 部门id集合字符串
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param page
     * @return GroupModel
     */
    Page<GroupModel> queryGroupPageList(List<String> departIds, String startTime, String endTime, Page<GroupModel> page);

    /**
     * 人员统计
     * @param departIds 部门id集合字符串
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param page
     * @return Page<PersonnelModel>
     */
    Page<PersonnelModel> queryUserPageList(List<String> departIds, String startTime, String endTime, Page<PersonnelModel> page);

    /**
     * 班组详情
     *
     * @param departId 部门id
     * @return TeamPortraitModel
     */
    TeamPortraitModel queryGroupById(String departId);

    /**
     * 人员详情
     *
     * @param userId 人员id
     * @return TeamUserModel
     */
    TeamUserModel queryUserById(String userId);

    /**
     * 统计报表人员报表-班组列表导出
     *
     * @param exportField 自定义导出字段集合合并字符串
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param request
     * @return ModelAndView
     */
    ModelAndView reportGroupExport(HttpServletRequest request, String startTime, String endTime,String exportField);

    /**
     * 统计报表人员报表-人员列表导出
     *
     * @param exportField 自定义导出字段集合合并字符串
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param request
     * @return ModelAndView
     */
    ModelAndView reportUserExport(HttpServletRequest request,String startTime, String endTime,String exportField);


}
