package com.aiurt.boot.weeklyplan.mapper;

import com.aiurt.boot.weeklyplan.dto.*;
import com.aiurt.boot.weeklyplan.entity.BdOperatePlanDeclarationForm;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
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
    /**
     * 查找施工类型
     * @return
     */
    List<BdConstructionTypeDTO> queryConstructionTypeList();

    /**
     * 根据组织id查找人员
     * @param teamId
     * @return
     */
    List<BdStaffInfoReturnTypeDTO> queryStaffByTeamId(String teamId);

    /**
     * 根据权限类型查找人员
     * @param roleType
     * @param deptId
     * @return
     */
    List<BdStaffInfoReturnTypeDTO> queryStaffByRoleType(String roleType, String deptId);

    /**
     * 根据权限名称查找人员
     * @param roleName
     * @param deptId
     * @return
     */
    List<BdStaffInfoReturnTypeDTO> queryStaffByRoleName(@Param("roleName") String roleName,@Param("deptId") String deptId);

    /**
     * 查找站点
     * @param teamId
     * @return
     */
    List<BdStationReturnTypeDTO> queryStations(Integer teamId);

    /**
     * 查找线路
     * @param idList
     * @return
     */
    List<BdLineDTO> queryLines(List<Integer> idList);

    /**
     * 查找人员名称
     * @param staffId
     * @return
     */
    String queryStaffNameById(String staffId);

    /**
     * 查找站点名称
     * @param stationId
     * @return
     */
    String queryStationNameById(String stationId);

    /**
     * 查找用户信息
     * @param userId
     * @return
     */
    List<BdUserInfoDTO> queryUserInfo(String userId);

    /**
     * 查找所有的站点
     * @param positionList
     * @param pid
     * @return
     */
    List<BdStationReturnTypeDTO> queryAllStations(@Param("positionList")List<String> positionList,@Param("pid")String pid);

    /**
     * 查询施工负责人
     * @param chargeStaffId
     * @param taskDate
     * @return
     */
    Integer checkChargeStaffIfConflict(String chargeStaffId, Date taskDate);

    /**
     * 查询变更关联的周计划令
     * @param changeCorrelation
     * @return
     */
    Integer checkFormIfEdited(Integer changeCorrelation);

    /**
     * 分页查询
     * @param queryPagesParams
     * @param busId
     * @return
     */
    List<BdOperatePlanDeclarationFormReturnTypeDTO> queryPages(
            @Param("queryPagesParams") QueryPagesParams queryPagesParams,
            @Param("busId") String busId);

    /**
     * 查询站点名称
     * @param parseId
     * @return
     */
    String queryStationNamesById(@Param("parseId")String[] parseId);

    /**
     * 查找施工信息
     * @param id
     * @return
     */
    BdOperatePlanDeclarationFormReturnTypeDTO queryFormInfoById(Integer id);

    /**
     * 查询人员名字
     * @param parseId
     * @return
     */
    String queryStaffNamesByIds(String[] parseId);

    /**
     * 根据名字查询人员ID
     * @param realName
     * @return
     */
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
