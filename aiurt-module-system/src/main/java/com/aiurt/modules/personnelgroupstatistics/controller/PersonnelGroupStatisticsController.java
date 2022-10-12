package com.aiurt.modules.personnelgroupstatistics.controller;

import cn.hutool.core.text.StrSpliter;
import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.aspect.annotation.PermissionData;
import com.aiurt.modules.personnelgroupstatistics.model.GroupModel;
import com.aiurt.modules.personnelgroupstatistics.model.PersonnelModel;
import com.aiurt.modules.personnelgroupstatistics.model.TeamPortraitModel;
import com.aiurt.modules.personnelgroupstatistics.model.TeamUserModel;
import com.aiurt.modules.personnelgroupstatistics.service.PersonnelGroupStatisticsService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.system.vo.SysDepartModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @Description: 统计报表人员班组
 * @Author: lkj
 * @Date:   2022-10-09
 */
@Api(tags="统计报表人员班组")
@RestController
@RequestMapping("/personnelgroupstatistics/personnelGroupStatistics")
@Slf4j
public class PersonnelGroupStatisticsController {

    @Autowired
    private PersonnelGroupStatisticsService personnelGroupStatisticsService;

    @Autowired
    private ISysBaseAPI iSysBaseAPI;

    /**
     * 班组统计
     *
     * @param departIds 部门id集合字符串
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return GroupModel
     */
    @AutoLog(value = "班组统计-查询", operateType =  1, operateTypeAlias = "查询", permissionUrl = "")
    @ApiOperation(value="班组统计", notes="班组统计")
    @GetMapping(value = "/groupList")
    @PermissionData(pageComponent = "")
    public Result<Page<GroupModel>> queryGroupPageList(@RequestParam(name="departIds",required = false)  String departIds,
                                                       @RequestParam(name="startTime") String startTime,
                                                       @RequestParam(name="endTime")  String endTime,
                                                       @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                       @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        Page<GroupModel> page = new Page<>(pageNo, pageSize);
        List<String> list = StrSpliter.splitTrim(departIds, ",", true);
        Page<GroupModel> groupModelPage = personnelGroupStatisticsService.queryGroupPageList(list, startTime, endTime, page);
        return Result.OK(groupModelPage);
    }

    /**
     * 班组详情
     *
     * @param departId 部门id
     * @return TeamPortraitModel
     */
    @AutoLog(value = "班组详情-通过id查询", operateType =  1, operateTypeAlias = "查询-通过id查询", permissionUrl = "")
    @ApiOperation(value="班组详情-通过id查询", notes="班组详情-通过id查询")
    @GetMapping(value = "/queryGroupById")
    public Result<TeamPortraitModel> queryGroupById(@RequestParam(name="departId",required=true)  String departId) {
        TeamPortraitModel teamPortraitModel = personnelGroupStatisticsService.queryGroupById(departId);
        return Result.OK(teamPortraitModel);
    }

    /**
     * 人员统计
     * @param departIds 部门id集合字符串
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return Page<PersonnelModel>
     */
    @AutoLog(value = "人员统计-查询", operateType =  1, operateTypeAlias = "查询", permissionUrl = "")
    @ApiOperation(value="人员统计", notes="人员统计")
    @GetMapping(value = "/userList")
    @PermissionData(pageComponent = "")
    public Result<Page<PersonnelModel>> queryUserPageList(@RequestParam(name="departIds",required = false)  String departIds,
                                                          @RequestParam(name="startTime") String startTime,
                                                          @RequestParam(name="endTime")  String endTime,
                                                          @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                          @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        Page<PersonnelModel> page = new Page<>(pageNo, pageSize);
        List<String> list = StrSpliter.splitTrim(departIds, ",", true);
        Page<PersonnelModel> personnelModelPage = personnelGroupStatisticsService.queryUserPageList(list, startTime, endTime, page);
        return Result.OK(personnelModelPage);
    }

    /**
     * 人员详情
     *
     * @param userId 人员id
     * @return TeamUserModel
     */
    @AutoLog(value = "人员详情-通过id查询", operateType =  1, operateTypeAlias = "查询-通过id查询", permissionUrl = "")
    @ApiOperation(value="人员详情-通过id查询", notes="人员详情-通过id查询")
    @GetMapping(value = "/queryUserById")
    public Result<TeamUserModel> queryUserById(@RequestParam(name="userId",required=true)  String userId) {
        TeamUserModel teamUserModel = personnelGroupStatisticsService.queryUserById(userId);
        return Result.OK(teamUserModel);
    }

    /**
     * 统计报表人员报表-班组列表导出
     *
     * @param exportField 自定义导出字段集合合并字符串
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return ModelAndView
     */
    @AutoLog(value = "统计报表人员报表-班组列表导出", operateType = 6, operateTypeAlias = "导出")
    @ApiOperation(value = "统计报表人员报表-班组列表导出", notes = "统计报表人员报表-班组列表导出")
    @GetMapping(value = "/reportGroupExport")
    public ModelAndView reportGroupExport(HttpServletRequest request,
                                     @RequestParam(name = "exportField",required = false) String exportField,
                                     @RequestParam(name = "startTime",required = false) String startTime,
                                     @RequestParam(name = "endTime",required = false) String endTime) {
        return personnelGroupStatisticsService.reportGroupExport(request,startTime,endTime,exportField);
    }

    /**
     * 统计报表人员报表-人员列表导出
     *
     * @param exportField 自定义导出字段集合合并字符串
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return ModelAndView
     */
    @AutoLog(value = "统计报表人员报表-人员列表导出", operateType = 6, operateTypeAlias = "导出")
    @ApiOperation(value = "统计报表人员报表-人员列表导出", notes = "统计报表人员报表-人员列表导出")
    @GetMapping(value = "/reportUserExport")
    public ModelAndView reportUserExport(HttpServletRequest request,
                                         @RequestParam(name = "exportField",required = false) String exportField,
                                         @RequestParam(name = "startTime",required = false) String startTime,
                                         @RequestParam(name = "endTime",required = false) String endTime) {
        return personnelGroupStatisticsService.reportUserExport(request,startTime,endTime,exportField);
    }

    /**
     * 班组下拉框
     * @return
     */
    @AutoLog(value = "统计报表人员报表-班组下拉框", operateType = 1, operateTypeAlias = "查询")
    @ApiOperation(value = "统计报表人员报表-班组下拉框", notes = "统计报表人员报表-班组下拉框")
    @GetMapping(value = "/selectDepart")
    public List<SysDepartModel> selectDepart() {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        return iSysBaseAPI.getUserSysDepart(sysUser.getId());
    }
}
