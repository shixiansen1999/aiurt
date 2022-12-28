package com.aiurt.modules.maplocation.service;


import com.aiurt.modules.maplocation.dto.*;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;


import java.util.List;

/**
 * @Description: bd_info_list
 * @Author: jeecg-boot
 * @Date:   2021-04-19
 * @Version: V1.0
 */
public interface IBdMapListService extends IService<CurrentTeamPosition> {

    /**
     * 查询人员的位置信息
     * @param teamId
     * @param userInfoList
     * @param stationId
     * @param stateId
     * @return
     */
    List<CurrentTeamPosition> queryPositionById(String teamId,String userInfoList, String stationId, String stateId);
    /**
     * 根据机构查询人员
     * @param teamId
     * @return
     */
    List<UserInfo> getUserByTeamIdList(String teamId);
    /**
     * 根据人员id查询人员信息
     * @param id
     * @return
     */
    List<UserInfo> getUserById(String id);
    /**
     * 根据人员id查询附近设备
     * @param id
     * @param stationId
     * @param pageList
     * @return
     */
    Page<EquipmentHistoryDTO> getEquipmentByUserId(String id, String stationId, Page<EquipmentHistoryDTO> pageList);

    /**
     * 根据机构获取机构下的人员状态
     * @param teamId
     * @param userId
     * @param stateId
     * @return
     */
    List<AssignUserDTO> getUserStateByTeamId(String teamId,String userId,Integer stateId);
    /**
     * 发送消息给对应的用户
     *
     * @param username 用户账号
     * @param msg
     * @return
     */
    void sendSysAnnouncement(String username, String msg);

    /**
     * 站点下拉框
     * @return
     */
    List<LineDTO> getStation();
}
