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

    List<BdStaffInfoReturnTypeDTO> queryStaffByTeamId(String teamId);

    List<BdStaffInfoReturnTypeDTO> queryStaffByRoleType(String roleType, String deptId);

    List<BdStaffInfoReturnTypeDTO> queryStaffByRoleName(@Param("roleName") String roleName,@Param("deptId") String deptId);

    List<BdStationReturnTypeDTO> queryStations(Integer teamId);

    List<BdLineDTO> queryLines(List<Integer> idList);

    String queryStaffNameById(String staffId);

    String queryStationNameById(String stationId);

    List<BdUserInfoDTO> queryUserInfo(String userId);

    List<BdStationReturnTypeDTO> queryAllStations(@Param("positionList")List<String> positionList,@Param("pid")String pid);

    Integer checkChargeStaffIfConflict(String chargeStaffId, Date taskDate);

    Integer checkFormIfEdited(Integer changeCorrelation);

    List<BdOperatePlanDeclarationFormReturnTypeDTO> queryPages(
            @Param("queryPagesParams") QueryPagesParams queryPagesParams,
            @Param("busId") String busId);

    String queryStationNamesById(@Param("parseID")String[] parseId);

    BdOperatePlanDeclarationFormReturnTypeDTO queryFormInfoById(Integer id);

    String queryStaffNamesByIds(String[] parseId);

    String queryStaffIdByName(String realName);

    /**
     * 修改发送的信息
     * @param idList
     */
    void updateBusAnnouncement(@Param("idList")List<String> idList);

    /**
     * 查询发送的消息
     * @param busId
     * @param busType
     * @param username
     * @return
     */
    List<String> queryBusAnnouncement(@Param("busId")String busId, @Param("busType")String busType, @Param("username")String username);

    /**
     * 查询所有总线路负责人
     * @return
     */
    List<String> queryUsernameByLineallpeople();

    /**
     * 查询班组下 线路负责人角色
     * @return
     */
    List<BdStaffInfoReturnTypeDTO> queryLineStaff();

    /**
     * 根据作业时间查询已同意的周计划
     * @param taskDate
     * @return
     */
    List<BdOperatePlanDeclarationReturnDTO> queryListByDate(String taskDate);

    /**
     * 查询周、月生产计划
     * @param dto
     * @return
     */
    List<ProductPlanDTO> readAll(ProductPlanDTO dto);
}
