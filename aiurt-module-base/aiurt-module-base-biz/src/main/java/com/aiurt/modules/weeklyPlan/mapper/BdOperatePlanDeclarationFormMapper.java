package com.aiurt.modules.weeklyplan.mapper;

import com.aiurt.modules.weeklyplan.dto.*;
import com.aiurt.modules.weeklyplan.entity.BdOperatePlanDeclarationForm;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;


import java.util.Date;
import java.util.List;

/**
 * @Description: 周计划表
 * @Author: Lai W.
 * @Date:   2021-05-10
 * @Version: V1.0
 */
public interface BdOperatePlanDeclarationFormMapper extends BaseMapper<BdOperatePlanDeclarationForm> {

    List<BdConstructionTypeDTO> queryConstructionTypeList();

    List<BdStaffInfoReturnTypeDTO> queryStaffByTeamId(String teamID);

    List<BdStaffInfoReturnTypeDTO> queryStaffByRoleType(String roleType, String deptID);

    List<BdStaffInfoReturnTypeDTO> queryStaffByRoleName(@Param("roleName") String roleName,@Param("deptID") String deptID);

    List<BdStationReturnTypeDTO> queryStations(Integer teamID);

    List<BdLineDTO> queryLines(List<Integer> idList);

    String queryStaffNameById(String staffID);

    String queryStationNameById(String stationID);

    List<BdUserInfoDTO> queryUserInfo(String userID);

    List<BdStationReturnTypeDTO> queryAllStations(@Param("positionList")List<String> positionList,@Param("pid")String pid);

    Integer checkChargeStaffIfConflict(String chargeStaffId, Date taskDate);

    Integer checkFormIfEdited(Integer changeCorrelation);

    List<BdOperatePlanDeclarationFormReturnTypeDTO> queryPages(
            @Param("queryPagesParams") QueryPagesParams queryPagesParams,
            @Param("busId") String busId);

    String queryStationNamesById(@Param("parseID")String[] parseID);

    BdOperatePlanDeclarationFormReturnTypeDTO queryFormInfoById(Integer id);

    String queryStaffNamesByIds(String[] parseID);

    String queryStaffIdByName(String realName);

    //修改发送的信息
    void updateBusAnnouncement(@Param("idList")List<String> idList);

    //查询发送的消息
    List<String> queryBusAnnouncement(@Param("busId")String busId, @Param("busType")String busType, @Param("username")String username);

    //查询所有总线路负责人
    List<String> queryUsernameByLineallpeople();

    //查询班组下 线路负责人角色
    List<BdStaffInfoReturnTypeDTO> queryLineStaff();

    //根据作业时间查询已同意的周计划
    List<BdOperatePlanDeclarationReturnDTO> queryListByDate(String taskDate);

    //查询周、月生产计划
    List<ProductPlanDTO> readAll(ProductPlanDTO dto);
}
